package com.elend.p2p.workflow.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricDetail;
import org.activiti.engine.history.HistoricDetailQuery;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricProcessInstanceQuery;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.history.HistoricVariableInstanceQuery;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.process.ProcessDefinitionImpl;
import org.activiti.engine.impl.pvm.process.TransitionImpl;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.elend.p2p.gson.JSONUtils;
import com.elend.p2p.json.Json;
import com.elend.p2p.util.DateUtil;
import com.elend.p2p.workflow.exception.TaskNotFoundException;
import com.elend.p2p.workflow.service.ProcessCoreService;

/**
 * 工作流跟踪相关Service
 *
 * @author HenryYan
 */
@Service
public class ProcessCoreServiceImpl implements ProcessCoreService {// implements
        protected Logger logger = LoggerFactory.getLogger(getClass());														// ProcessCoreService
	@Autowired
	protected TaskService taskService;
	@Autowired
	protected RuntimeService runtimeService;
	@Autowired
	protected HistoryService historyService;
	@Autowired
	protected RepositoryService repositoryService;
	
	@Autowired
        protected ProcessEngine processEngine;
	
	@Autowired
	private IdentityService identityService;

	 /* 退回前一节点
	 * @see com.elend.p2p.workflow.service.ProcessCoreService#backProcess(java.lang.String, java.util.Map)
	 */
	public void backProcess(String taskId, Map<String, Object> variables, String userId)
			throws Exception {
	        Task task = taskService.createTaskQuery().taskId(taskId).taskCandidateUser(userId).singleResult();
	        if(task==null){
	            logger.error("任务{}不存在或者用户{}没有权限操作.",taskId,userId);
	            throw new TaskNotFoundException(String.format("任务%s不存在或者用户%s没有权限操作",taskId,userId));
	        }
	        identityService.setAuthenticatedUserId(userId);
		String activityId = findBackAvtivityStepId(taskId);
		backProcess(taskId, activityId, variables);
	}

	/**
	 * 驳回流程
	 * 
	 * @param taskId
	 *            当前任务ID
	 * @param activityId
	 *            驳回节点ID
	 * @param variables
	 *            流程存储参数
	 * @throws Exception
	 */
	public void backProcess(String taskId, String activityId,
			Map<String, Object> variables) throws Exception {
		if (StringUtils.isBlank(activityId)) {
			throw new Exception("驳回目标节点ID为空！");
		}
		// 查找所有并行任务节点，同时驳回
		List<Task> taskList = findTaskListByKey(
				findProcessInstanceByTaskId(taskId).getId(),
				findTaskById(taskId).getTaskDefinitionKey());
		for (Task task : taskList) {
		        if(variables == null) {
		            variables = new HashMap<>();
		        }
		        // 添加操作说明
	                variables.put(WorkflowServiceImpl.OPERATION_INSTRUCTION_KEY, "驳回流程");
	                String comment = JSONUtils.toJson(variables, false);
	                
	                //获取流程
	                String processInstanceId = taskService.createTaskQuery().taskId(taskId).singleResult().getProcessInstanceId();

	                //添加批注
	                taskService.addComment(taskId, processInstanceId, comment);
	                
			commitProcess(task.getId(), variables, activityId);
		}
	}

	/**
	 * 根据流程实例ID和任务key值查询所有同级任务集合
	 * 
	 * @param processInstanceId
	 * @param key
	 * @return
	 */
	private List<Task> findTaskListByKey(String processInstanceId, String key) {
		return taskService.createTaskQuery()
				.processInstanceId(processInstanceId).taskDefinitionKey(key)
				.list();
	}

	/**
	 * @param taskId
	 *            当前任务ID
	 * @param variables
	 *            流程变量
	 * @param activityId
	 *            流程转向执行任务节点ID<br>
	 *            此参数为空，默认为提交操作
	 * @throws Exception
	 */
	private void commitProcess(String taskId, Map<String, Object> variables,
			String activityId) throws Exception {
		if (variables == null) {
			variables = new HashMap<String, Object>();
		}
		
		// 跳转节点为空，默认提交操作
		if (StringUtils.isBlank(activityId)) {
		    
			taskService.complete(taskId, variables);
		} else {// 流程转向操作
			turnTransition(taskId, activityId, variables);
		}
	}

	/**
	 * 流程转向操作
	 * 
	 * @param taskId
	 *            当前任务ID
	 * @param activityId
	 *            目标节点任务ID
	 * @param variables
	 *            流程变量
	 * @throws Exception
	 */
	private void turnTransition(String taskId, String activityId,
			Map<String, Object> variables) throws Exception {
		// 当前节点
		ActivityImpl currActivity = findActivitiImpl(taskId, null);
		// 清空当前流向
		List<PvmTransition> oriPvmTransitionList = clearTransition(currActivity);

		// 创建新流向
		TransitionImpl newTransition = currActivity.createOutgoingTransition();
		// 目标节点
		ActivityImpl pointActivity = findActivitiImpl(taskId, activityId);
		// 设置新流向的目标节点
		newTransition.setDestination(pointActivity);

		// 执行转向任务
		taskService.complete(taskId, variables);
		// 删除目标节点新流入
		pointActivity.getIncomingTransitions().remove(newTransition);

		// 还原以前流向
		restoreTransition(currActivity, oriPvmTransitionList);
	}

	/**
	 * 还原指定活动节点流向
	 * 
	 * @param activityImpl
	 *            活动节点
	 * @param oriPvmTransitionList
	 *            原有节点流向集合
	 */
	private void restoreTransition(ActivityImpl activityImpl,
			List<PvmTransition> oriPvmTransitionList) {
		// 清空现有流向
		List<PvmTransition> pvmTransitionList = activityImpl
				.getOutgoingTransitions();
		pvmTransitionList.clear();
		// 还原以前流向
		for (PvmTransition pvmTransition : oriPvmTransitionList) {
			pvmTransitionList.add(pvmTransition);
		}
	}

	/**
	 * 清空指定活动节点流向
	 * 
	 * @param activityImpl
	 *            活动节点
	 * @return 节点流向集合
	 */
	private List<PvmTransition> clearTransition(ActivityImpl activityImpl) {
		// 存储当前节点所有流向临时变量
		List<PvmTransition> oriPvmTransitionList = new ArrayList<PvmTransition>();
		// 获取当前节点所有流向，存储到临时变量，然后清空
		List<PvmTransition> pvmTransitionList = activityImpl
				.getOutgoingTransitions();
		for (PvmTransition pvmTransition : pvmTransitionList) {
			oriPvmTransitionList.add(pvmTransition);
		}
		pvmTransitionList.clear();

		return oriPvmTransitionList;
	}

	/**
	 * 根据当前任务ID，查询可以驳回的任务节点
	 * 
	 * @param taskId
	 *            当前任务ID
	 */
	public List<ActivityImpl> findBackAvtivity(String taskId) throws Exception {
		List<ActivityImpl> rtnList = null;
	
		rtnList = iteratorBackActivity(taskId, findActivitiImpl(taskId, null),
				new ArrayList<ActivityImpl>(), new ArrayList<ActivityImpl>());
		
		
	
		return rtnList;
	}

	public String findBackAvtivityStepId(String taskId) throws Exception {		
		if (findBackAvtivity(taskId).size()>0) {
			return findBackAvtivity(taskId).get(0).getId();
		}		
		return "";
	}

	/**
	 * 反向排序list集合，便于驳回节点按顺序显示
	 * 
	 * @param list
	 * @return
	 */
	private List<ActivityImpl> reverList(List<ActivityImpl> list) {
		List<ActivityImpl> rtnList = new ArrayList<ActivityImpl>();
		// 由于迭代出现重复数据，排除重复
		for (int i = list.size(); i > 0; i--) {
			if (!rtnList.contains(list.get(i - 1)))
				rtnList.add(list.get(i - 1));
		}
		return rtnList;
	}

	/**
	 * 迭代循环流程树结构，查询当前节点可驳回的任务节点
	 * 
	 * @param taskId
	 *            当前任务ID
	 * @param currActivity
	 *            当前活动节点
	 * @param rtnList
	 *            存储回退节点集合
	 * @param tempList
	 *            临时存储节点集合（存储一次迭代过程中的同级userTask节点）
	 * @return 回退节点集合
	 */
	private List<ActivityImpl> iteratorBackActivity(String taskId,
			ActivityImpl currActivity, List<ActivityImpl> rtnList,
			List<ActivityImpl> tempList) throws Exception {
		// 查询流程定义，生成流程树结构
		ProcessInstance processInstance = findProcessInstanceByTaskId(taskId);

		// 当前节点的流入来源
		List<PvmTransition> incomingTransitions = currActivity
				.getIncomingTransitions();
		// 条件分支节点集合，userTask节点遍历完毕，迭代遍历此集合，查询条件分支对应的userTask节点
		List<ActivityImpl> exclusiveGateways = new ArrayList<ActivityImpl>();
		// 并行节点集合，userTask节点遍历完毕，迭代遍历此集合，查询并行节点对应的userTask节点
		List<ActivityImpl> parallelGateways = new ArrayList<ActivityImpl>();
		// 遍历当前节点所有流入路径
		for (PvmTransition pvmTransition : incomingTransitions) {
			TransitionImpl transitionImpl = (TransitionImpl) pvmTransition;
			ActivityImpl activityImpl = transitionImpl.getSource();
			String type = (String) activityImpl.getProperty("type");
			/**
			 * 并行节点配置要求：<br>
			 * 必须成对出现，且要求分别配置节点ID为:XXX_start(开始)，XXX_end(结束)
			 */
			if ("parallelGateway".equals(type)) {// 并行路线
				String gatewayId = activityImpl.getId();
				String gatewayType = gatewayId.substring(gatewayId
						.lastIndexOf("_") + 1);
				if ("START".equals(gatewayType.toUpperCase())) {// 并行起点，停止递归
					return rtnList;
				} else {// 并行终点，临时存储此节点，本次循环结束，迭代集合，查询对应的userTask节点
					parallelGateways.add(activityImpl);
				}
			} else if ("startEvent".equals(type)) {// 开始节点，停止递归
				return rtnList;
			} else if ("userTask".equals(type)) {// 用户任务
				tempList.add(activityImpl);
			} else if ("exclusiveGateway".equals(type)) {// 分支路线，临时存储此节点，本次循环结束，迭代集合，查询对应的userTask节点
				currActivity = transitionImpl.getSource();
				exclusiveGateways.add(currActivity);
			}
		}

		/**
		 * 迭代条件分支集合，查询对应的userTask节点
		 */
		for (ActivityImpl activityImpl : exclusiveGateways) {
			iteratorBackActivity(taskId, activityImpl, rtnList, tempList);
		}

		/**
		 * 迭代并行集合，查询对应的userTask节点
		 */
		for (ActivityImpl activityImpl : parallelGateways) {
			iteratorBackActivity(taskId, activityImpl, rtnList, tempList);
		}

		/**
		 * 根据同级userTask集合，过滤最近发生的节点
		 */
		currActivity = filterNewestActivity(processInstance, tempList);
		if (currActivity != null) {
			// 查询当前节点的流向是否为并行终点，并获取并行起点ID
			String id = findParallelGatewayId(currActivity);
			if (StringUtils.isBlank(id)) {// 并行起点ID为空，此节点流向不是并行终点，符合驳回条件，存储此节点
				rtnList.add(currActivity);
			} else {// 根据并行起点ID查询当前节点，然后迭代查询其对应的userTask任务节点
				currActivity = findActivitiImpl(taskId, id);
			}

			// 清空本次迭代临时集合
			tempList.clear();
			// 执行下次迭代
			iteratorBackActivity(taskId, currActivity, rtnList, tempList);
		}
		return rtnList;
	}

	/**
	 * 根据任务ID获取对应的流程实例
	 * 
	 * @param taskId
	 *            任务ID
	 * @return
	 * @throws Exception
	 */
	private ProcessInstance findProcessInstanceByTaskId(String taskId)
			throws Exception {
		// 找到流程实例
		ProcessInstance processInstance = runtimeService
				.createProcessInstanceQuery()
				.processInstanceId(findTaskById(taskId).getProcessInstanceId())
				.singleResult();
		if (processInstance == null) {
			throw new Exception("流程实例未找到!");
		}
		return processInstance;
	}

	/**
	 * 根据任务ID获得任务实例
	 * 
	 * @param taskId
	 *            任务ID
	 * @return
	 * @throws Exception
	 */
	private TaskEntity findTaskById(String taskId) throws Exception {
		TaskEntity task = (TaskEntity) taskService.createTaskQuery()
				.taskId(taskId).singleResult();
		if (task == null) {
			throw new Exception("任务实例未找到!");
		}
		return task;
	}

	/**
	 * 根据流入任务集合，查询最近一次的流入任务节点
	 * 
	 * @param processInstance
	 *            流程实例
	 * @param tempList
	 *            流入任务集合
	 * @return
	 */
	private ActivityImpl filterNewestActivity(ProcessInstance processInstance,
			List<ActivityImpl> tempList) {
		while (tempList.size() > 0) {
			ActivityImpl activity_1 = tempList.get(0);
			HistoricActivityInstance activityInstance_1 = findHistoricUserTask(
					processInstance, activity_1.getId());
			if (activityInstance_1 == null) {
				tempList.remove(activity_1);
				continue;
			}

			if (tempList.size() > 1) {
				ActivityImpl activity_2 = tempList.get(1);
				HistoricActivityInstance activityInstance_2 = findHistoricUserTask(
						processInstance, activity_2.getId());
				if (activityInstance_2 == null) {
					tempList.remove(activity_2);
					continue;
				}

				if (activityInstance_1.getEndTime().before(
						activityInstance_2.getEndTime())) {
					tempList.remove(activity_1);
				} else {
					tempList.remove(activity_2);
				}
			} else {
				break;
			}
		}
		if (tempList.size() > 0) {
			return tempList.get(0);
		}
		return null;
	}

	/**
	 * 查询指定任务节点的最新记录
	 * 
	 * @param processInstance
	 *            流程实例
	 * @param activityId
	 * @return
	 */
	private HistoricActivityInstance findHistoricUserTask(
			ProcessInstance processInstance, String activityId) {
		HistoricActivityInstance rtnVal = null;
		// 查询当前流程实例审批结束的历史节点
		List<HistoricActivityInstance> historicActivityInstances = historyService
				.createHistoricActivityInstanceQuery().activityType("userTask")
				.processInstanceId(processInstance.getId())
				.activityId(activityId).finished()
				.orderByHistoricActivityInstanceEndTime().desc().list();
		if (historicActivityInstances.size() > 0) {
			rtnVal = historicActivityInstances.get(0);
		}

		return rtnVal;
	}

	/**
	 * 根据当前节点，查询输出流向是否为并行终点，如果为并行终点，则拼装对应的并行起点ID
	 * 
	 * @param activityImpl
	 *            当前节点
	 * @return
	 */
	private String findParallelGatewayId(ActivityImpl activityImpl) {
		List<PvmTransition> incomingTransitions = activityImpl
				.getOutgoingTransitions();
		for (PvmTransition pvmTransition : incomingTransitions) {
			TransitionImpl transitionImpl = (TransitionImpl) pvmTransition;
			activityImpl = transitionImpl.getDestination();
			String type = (String) activityImpl.getProperty("type");
			if ("parallelGateway".equals(type)) {// 并行路线
				String gatewayId = activityImpl.getId();
				String gatewayType = gatewayId.substring(gatewayId
						.lastIndexOf("_") + 1);
				if ("END".equals(gatewayType.toUpperCase())) {
					return gatewayId.substring(0, gatewayId.lastIndexOf("_"))
							+ "_start";
				}
			}
		}
		return null;
	}

	/**
	 * 根据任务ID和节点ID获取活动节点 <br>
	 * 
	 * @param taskId
	 *            任务ID
	 * @param activityId
	 *            活动节点ID <br>
	 *            如果为null或""，则默认查询当前活动节点 <br>
	 *            如果为"end"，则查询结束节点 <br>
	 * 
	 * @return
	 * @throws Exception
	 */
	private ActivityImpl findActivitiImpl(String taskId, String activityId)
			throws Exception {
		// 取得流程定义
		ProcessDefinitionEntity processDefinition = findProcessDefinitionEntityByTaskId(taskId);

		// 获取当前活动节点ID
		if (StringUtils.isBlank(activityId)) {
			activityId = findTaskById(taskId).getTaskDefinitionKey();
		}

		// 根据流程定义，获取该流程实例的结束节点
		if (activityId.toUpperCase().equals("END")) {
			for (ActivityImpl activityImpl : processDefinition.getActivities()) {
				List<PvmTransition> pvmTransitionList = activityImpl
						.getOutgoingTransitions();
				if (pvmTransitionList.isEmpty()) {
					return activityImpl;
				}
			}
		}

		// 根据节点ID，获取对应的活动节点
		ActivityImpl activityImpl = ((ProcessDefinitionImpl) processDefinition)
				.findActivity(activityId);

		return activityImpl;
	}

	/**
	 * 根据任务ID获取流程定义
	 * 
	 * @param taskId
	 *            任务ID
	 * @return
	 * @throws Exception
	 */
	private ProcessDefinitionEntity findProcessDefinitionEntityByTaskId(
			String taskId) throws Exception {
		// 取得流程定义
		ProcessDefinitionEntity processDefinition = (ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService)
				.getDeployedProcessDefinition(findTaskById(taskId)
						.getProcessDefinitionId());

		if (processDefinition == null) {
			throw new Exception("流程定义未找到!");
		}

		return processDefinition;
	}

	// 历史流程实例查看（查找按照某个规则一共执行了多少次流程）

	public void queryHistoricProcessInstance() throws Exception {
		// 获取历史流程实例的查询对象
		HistoricProcessInstanceQuery historicProcessInstanceQuery = historyService
				.createHistoricProcessInstanceQuery();
		HistoricDetailQuery historicDetailQuery = historyService
				.createHistoricDetailQuery();// .taskId("36711");
		List<HistoricDetail> details = historicDetailQuery.list();
		// 设置查询参数
		historicProcessInstanceQuery
				.processDefinitionKey("smallNormalLoanApply");
		// 分页条件// .listPage(firstResult, maxResults) // 排序条件
		// .orderByProcessInstanceStartTime().desc(); // 执行查询
		List<HistoricProcessInstance> hpis = historicProcessInstanceQuery
				.finished().list();
		// 遍历查看结果
		for (HistoricProcessInstance hpi : hpis) {
			System.out.print("pid:" + hpi.getId() + ",");
			System.out.print("pdid:" + hpi.getProcessDefinitionId() + ",");
			System.out.print("startTime:" + hpi.getStartTime() + ",");
			System.out.print("endTime:" + hpi.getEndTime() + ",");
			System.out.print("duration:" + hpi.getDurationInMillis() + ",");
			System.out.println("vars:" + hpi.getProcessVariables());
		}

		String processInstanceId = findProcessInstanceByTaskId("40309")
				.getProcessInstanceId();
		List<HistoricActivityInstance> hiaciList = historyService
				.createHistoricActivityInstanceQuery()
				.processInstanceId(processInstanceId).finished()
				.orderByHistoricActivityInstanceEndTime().asc().list();
	
		HistoricVariableInstanceQuery historicVariableInstanceQuery = historyService
				.createHistoricVariableInstanceQuery().taskId("40811");
		

		for (HistoricActivityInstance historicActivityInstance : hiaciList) {

			System.out.println(historicActivityInstance.getActivityName());

			System.out.println(historicActivityInstance.getActivityType());

			System.out.println(historicActivityInstance.getActivityName());
			System.out.println(historicActivityInstance.getAssignee());
			System.out.println(historicActivityInstance.getTime());

			System.out.println(historicActivityInstance.getTaskId());

		}

		getHistoryAudit("40811");
	}

	/**
	 * 获得审核历史
	 * 
	 * @param taskId
	 * @throws Exception
	 */
	public List<Audit> getHistoryAudit(String taskId) throws Exception {

		String processInstanceId = findProcessInstanceByTaskId(taskId)
				.getProcessInstanceId();
		List<HistoricActivityInstance> hiaciList = historyService
				.createHistoricActivityInstanceQuery()
				.processInstanceId(processInstanceId).finished()
				.orderByHistoricActivityInstanceEndTime().asc().list();

		List<Audit> audits = new ArrayList<Audit>(hiaciList.size());

		for (HistoricActivityInstance historicActivityInstance : hiaciList) {

			String hprocessInstanceId = historicActivityInstance
					.getProcessInstanceId();

			if (hprocessInstanceId != null
					& StringUtils.isNotBlank(historicActivityInstance
							.getActivityName())) {
			        Audit audit = new Audit();
				audit.setAuditTime(DateUtil.timeToStr(
						historicActivityInstance.getTime(),
						DateUtil.DATE_FORMAT_PATTEN));
				audit.setActivityName(historicActivityInstance
						.getActivityName());				
				String activitId=historicActivityInstance.getActivityId();				
				 List<HistoricVariableInstance> hiac= processEngine.getHistoryService().createHistoricVariableInstanceQuery().processInstanceId(historicActivityInstance.getProcessInstanceId()).variableName(activitId).orderByVariableName().asc().list();				
				 if (hiac!=null&&hiac.size()>0) {
                                    HistoricVariableInstance historicVariableInstance=hiac.get(0);                                      
                                      String valMap=historicVariableInstance.getValue().toString();                                      
                                       Map<String, Object> values= Json.toMap(valMap);                                       
                                       String opinion= values.get("opinion").toString();
                                       String userName= values.get("userName")==null?"":values.get("userName").toString();
                                       String pass= values.get("pass").toString();
                                       audit.setOpinion(opinion);
                                       audit.setUserName(userName);
                                       audit.setPass(Boolean.parseBoolean(pass));                                  
                                 }			              
				 audits.add(audit);
			}

		}
		
		

		return audits;
	}
	
	
	
	
	/**
         * 获得审核历史
         * 
         * @param taskId
         * @throws Exception
         */
        public void getHistoryVal(String taskId) throws Exception {

                String processInstanceId = findProcessInstanceByTaskId(taskId)
                                .getProcessInstanceId();
                List<HistoricVariableInstance> hiaciList = processEngine.getHistoryService().createHistoricVariableInstanceQuery().processInstanceId(processInstanceId).orderByVariableName().asc().list();
                                
             if (hiaciList!=null&&hiaciList.size()>0) {
                for (HistoricVariableInstance historicVariableInstance : hiaciList) {
                    System.out.println("historicVariableInstance "+historicVariableInstance.getProcessInstanceId());
                    System.out.println("historicVariableInstance  name :"+historicVariableInstance.getVariableName());
                    
                    System.out.println("historicVariableInstance value :"+historicVariableInstance.getValue());
                    
                
                }
            }
             
             
             
             List<HistoricVariableInstance> hiac= processEngine.getHistoryService().createHistoricVariableInstanceQuery().variableName("bcreate").orderByVariableName().asc().list();
             
             if (hiac!=null&&hiac.size()>0) {
                for (HistoricVariableInstance historicVariableInstance : hiac) {
                    System.out.println("createHistoricVariableInstanceQuery().variableName ");
                    
                    System.out.println("historicVariableInstance "+historicVariableInstance.getProcessInstanceId());
                    System.out.println("historicVariableInstance  name :"+historicVariableInstance.getVariableName());
                    
                    System.out.println("historicVariableInstance value :"+historicVariableInstance.getValue());
                    
                
                }
            }
              
        }

}
