package com.elend.p2p.workflow.service.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.ManagementService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricProcessInstanceQuery;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.cmd.AbstractCustomSqlExecution;
import org.activiti.engine.impl.cmd.CustomSqlExecution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.elend.p2p.PageInfo;
import com.elend.p2p.Result;
import com.elend.p2p.constant.ResultCode;
import com.elend.p2p.gson.JSONUtils;
import com.elend.p2p.util.CommonUtil;
import com.elend.p2p.workflow.InstanceExecuter;
import com.elend.p2p.workflow.ProcessDefinitionData;
import com.elend.p2p.workflow.TaskExecuter;
import com.elend.p2p.workflow.WorkflowFactory;
import com.elend.p2p.workflow.WorkflowUserInfoData;
import com.elend.p2p.workflow.exception.DefNotFoundException;
import com.elend.p2p.workflow.exception.InstanceExecuteNotFoundException;
import com.elend.p2p.workflow.exception.InstanceNotFoundException;
import com.elend.p2p.workflow.exception.TaskExecuteNotFoundException;
import com.elend.p2p.workflow.exception.TaskNotFoundException;
import com.elend.p2p.workflow.mapper.CommentMapper;
import com.elend.p2p.workflow.service.WorkflowService;
import com.elend.p2p.workflow.vo.CommentSearchVO;
import com.elend.p2p.workflow.vo.CommentVO;
import com.elend.p2p.workflow.vo.CreateInstanceResponse;
import com.elend.p2p.workflow.vo.HistoricProcessInstanceVO;
import com.elend.p2p.workflow.vo.InstanceSearchVO;
import com.elend.p2p.workflow.vo.InstanceTaskDetailVO;
import com.elend.p2p.workflow.vo.ProcessDefinitionVO;
import com.elend.p2p.workflow.vo.TaskDetailVO;
import com.elend.p2p.workflow.vo.TaskSearchVO;
import com.elend.p2p.workflow.vo.TaskVO;
import com.google.gson.reflect.TypeToken;

/**
 * 流程处理类
 * 
 * @author liyongquan 2013-11-27
 */
@Service
public class WorkflowServiceImpl implements WorkflowService {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    public static final String PROCESS_ABSINFO_KEY = "abstract_info";

    @Autowired
    private WorkflowFactory workflowFactory;

    @Autowired
    private TaskService taskService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private IdentityService identityService;

    @Autowired
    private WorkflowUserInfoData userInfoData;
    @Autowired
    private ProcessDefinitionData definitionData;
    
    @Autowired
    private ManagementService managementService;
    
    /**
     * 批注操作说明固定的key
     */
    public static final String OPERATION_INSTRUCTION_KEY = "operation_instruction";

    @Override
    public void claim(String userId, String taskId) {
        taskService.claim(taskId, userId);
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRED)
    public void create(String userId, Map<String, String> paramMap,
            String processDefinitionKey,String appId) {
        //repositoryService.changeDeploymentTenantId(deploymentId, newTenantId);
        identityService.setAuthenticatedUserId(userId);//增加统一的activiti登录验证
        InstanceExecuter handler = workflowFactory.getInstanceExecuter(processDefinitionKey);
        if (handler == null) {
            throw new InstanceExecuteNotFoundException();
        }
        CreateInstanceResponse response = handler.create(paramMap);

        String businessKey = response.getBusinessKey();
        Map<String, Object> variables = response.getVariables();
        if(variables==null)variables=new HashMap<String, Object>();
        // 更新流程实例摘要信息
        String absInfo = handler.getAbstractInfo(businessKey);
        if (absInfo != null) {
            variables.put(PROCESS_ABSINFO_KEY, absInfo);
        }
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKeyAndTenantId(processDefinitionKey,
                                                                                   businessKey,
                                                                                   variables,appId);
        
        //添加操作说明（提单固定为创建流程）
        paramMap.put(OPERATION_INSTRUCTION_KEY, "创建流程");

        //comment记录参数的json
        String comment = JSONUtils.toJson(paramMap, false);
        
        //记录提单的comment
        taskService.addComment(null, processInstance.getProcessInstanceId(), comment);
        
        // 发送提单邮件
        workflowFactory.getInstanceExecuter(processDefinitionKey).sendCreateMsg(paramMap,
                                                                                businessKey,
                                                                                processInstance.getProcessInstanceId());
        logger.info("create instance");
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRED)
    public void complete(String userId, Map<String, String> paramMap,
            String taskId) {
        logger.info("task complete action start.userId:{},params:{}",userId,JSONUtils.toJson(paramMap,false));
        Task task = taskService.createTaskQuery().taskId(taskId).taskCandidateUser(userId).singleResult();
        if(task==null){
            logger.error("任务{}不存在或者用户{}没有权限操作.",taskId,userId);
            throw new TaskNotFoundException(String.format("任务%s不存在或者用户%s没有权限操作",taskId,userId));
        }
        identityService.setAuthenticatedUserId(userId);//增加统一的activiti登录验证
        InstanceTaskData instanceTaskData=new InstanceTaskData();
        TaskExecuter handler=getTaskExecuterByTaskId(taskId, instanceTaskData);
        if (handler == null) {
            throw new TaskExecuteNotFoundException("找不到对应的执行类，请联系开发人员");
        }
        HistoricProcessInstance instance=instanceTaskData.getInstance();
        Map<String, Object> variables = handler.process(instance.getBusinessKey(),
                                                        instance.getId(),
                                                        paramMap);
        // 发送邮件
        handler.sendMsg(paramMap, instance.getBusinessKey(),
                         instance.getId());
        // 更新流程实例摘要信息
        String absInfo = handler.getAbstractInfo(instance.getBusinessKey());
        if (absInfo != null) {
            variables.put(PROCESS_ABSINFO_KEY, absInfo);
        }
        
        //添加操作说明
        String oi = handler.getOperationInstruction(paramMap);
        if(StringUtils.isNotBlank(oi)) {
            paramMap.put(OPERATION_INSTRUCTION_KEY, oi);
        }
        
        String comment = JSONUtils.toJson(paramMap, false);
        
        taskService.addComment(taskId, task.getProcessInstanceId(), comment);
        
        /***
         * 审批意见
         */
    
        taskService.setVariablesLocal(taskId, variables);
     
        taskService.complete(taskId, variables);
     
    }

    @Override
    public String createPage(String processDefinitionKey) {
        InstanceExecuter instanceExecuter = workflowFactory.getInstanceExecuter(processDefinitionKey);
        if (instanceExecuter == null) {
            throw new InstanceExecuteNotFoundException();
        }
        return instanceExecuter.getCreatePage();
    }

    @Override
    public String completePage(String taskId,String userId) {
        TaskExecuter taskExecuter=this.getTaskExecuter(taskId, userId);
        if (taskExecuter == null) {
            throw new TaskExecuteNotFoundException();
        }
        return taskExecuter.getCompletePage();
    }
    
    @Override
    public String mCompletePage(String taskId,String userId) throws TaskExecuteNotFoundException,DefNotFoundException{
        TaskExecuter taskExecuter=this.getTaskExecuter(taskId, userId);
        if (taskExecuter == null) {
            throw new TaskExecuteNotFoundException();
        }
        return taskExecuter.getMCompletePage();
    }
    
    /**
     * 获取对应的任务处理逻辑
     * @param taskId
     * 任务ID
     * @param userId
     * 操作用户
     * @return
     * 任务处理逻辑
     */
    private TaskExecuter getTaskExecuter(String taskId,String userId)throws TaskExecuteNotFoundException,DefNotFoundException{
        Task task = taskService.createTaskQuery().taskId(taskId).taskCandidateUser(userId).singleResult();
        if(task==null){
            logger.error("任务ID:{}不存在或者用户{}没有权限操作.",taskId,userId);
            throw new TaskNotFoundException(String.format("任务ID:%s不存在或者用户%s没有权限操作",taskId,userId));
        }
        ProcessDefinitionVO definition=definitionData.get(task.getProcessDefinitionId());
        if (definition == null) {
            throw new DefNotFoundException();
        }
        // 调用具体的处理类
        TaskExecuter taskExecuter = workflowFactory.getTaskExecuter(definition.getKey(),
                                                                    task.getTaskDefinitionKey());
        if (taskExecuter == null) {
            throw new TaskExecuteNotFoundException();
        }
        return taskExecuter;
    }

    @Override
    public String getDetailPage(String processInstanceId) {
        HistoricProcessInstance instance = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        if (instance == null) {
            throw new InstanceNotFoundException();
        }
        ProcessDefinitionVO definition=definitionData.get(instance.getProcessDefinitionId());
        if (definition == null) {
            throw new DefNotFoundException();
        }
        InstanceExecuter handler = workflowFactory.getInstanceExecuter(definition.getKey());
        if (handler == null) {
            throw new InstanceExecuteNotFoundException();
        }
        String view = handler.getDetailPage();
        return view;
    }

    @Override
    public Result<String> endProcessByProcessInstanceId(String userName,String processInstanceId) {
        HistoricProcessInstance instance = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        if(instance==null){
            return new Result<String>(ResultCode.FAILURE, processInstanceId, "找不到对应的实例，终止失败");
        }
        logger.info("用户强制终止流程,userName:{},processInstanceId:{},businessKey:{},processDefinitionId:{}",userName,processInstanceId,instance.getBusinessKey(),instance.getProcessDefinitionId());
        runtimeService.deleteProcessInstance(processInstanceId, "");
        return new Result<String>(ResultCode.SUCCESS);
    }

    /**
     * 任务通用查询
     * 
     * @param svo
     * @param appId
     * 系统ID
     * @return
     * 任务列表
     */
    private PageInfo<TaskDetailVO> taskQuery(TaskSearchVO svo,String appId) {
        PageInfo<TaskDetailVO> paginInfo = new PageInfo<TaskDetailVO>();
        TaskQuery query = taskService.createTaskQuery().includeProcessVariables();
        if (!StringUtils.isBlank(svo.getCandidateUser())) {
            query.taskCandidateUser(svo.getCandidateUser());
        }
        if (!StringUtils.isBlank(svo.getAssignee())) {
            query.taskAssignee(svo.getAssignee());
        }
        if (svo.getStartTime() != null) {
            query.taskCreatedAfter(svo.getStartTime());
        }
        if (svo.getEndTime() != null) {
            query.taskCreatedBefore(svo.getEndTime());
        }
        if (StringUtils.isNotBlank(svo.getProcessDefinitionKey())) {  //"withdrawApply"
            query.processDefinitionKey(svo.getProcessDefinitionKey());
        }
        //摘要信息搜索，模糊搜索
        if(StringUtils.isNotBlank(svo.getAbstractInfo())){
            query.processVariableValueLike(PROCESS_ABSINFO_KEY, "%"+svo.getAbstractInfo()+"%");
        }
        //增加APP ID的搜索条件
        if(StringUtils.isNotBlank(appId)){
        	query.taskTenantId(appId);
        }
        //query.taskCategory("http://www.activiti.org/processdef");
        List<Task> tasks = query.orderByTaskCreateTime().desc().listPage(svo.getStart(),
                                                                         svo.getSize());
        // 这里将Task转成TaskVO再返回前端，不然解析成JSON串会报错
        List<TaskDetailVO> list = new ArrayList<TaskDetailVO>();
        for (Task task : tasks) {
            TaskDetailVO vo = taskDetailMapping(task);
            list.add(vo);
        }
        paginInfo.setList(list);
        if (list != null && list.size() > 0) {
            long totalNum = query.count();
            long totalPage = totalNum % svo.getSize() == 0 ? totalNum
                    / svo.getSize() : totalNum / svo.getSize() + 1;
            paginInfo.setCount((int) totalNum);
            paginInfo.setPage(svo.getPage());
            paginInfo.setPageCount((int) totalPage);
        } else {
            paginInfo.setCount(0);
            paginInfo.setPage(svo.getPage());
            paginInfo.setPageCount(0);
        }
        return paginInfo;
    }

    /**
     * 任务信息映射
     * 
     * @param task
     * @param userMap
     * @return
     */
    private TaskDetailVO taskDetailMapping(Task task) {
        // 任务信息
        TaskVO vo = taskMapping(task);
        // instance数据
        HistoricProcessInstance instance = historyService.createHistoricProcessInstanceQuery().includeProcessVariables().processInstanceId(task.getProcessInstanceId()).singleResult();
        HistoricProcessInstanceVO hvo = instanceMapping(instance);
        // 流程定义信息
        //ProcessDefinition definition = repositoryService.createProcessDefinitionQuery().processDefinitionId(task.getProcessDefinitionId()).singleResult();
        ProcessDefinitionVO definition=definitionData.get(task.getProcessDefinitionId());
        //ProcessDefinitionVO pvo = definitionMapping(definition);
        TaskDetailVO detail = new TaskDetailVO();
        detail.setTask(vo);
        detail.setHistoricProcessInstance(hvo);
        detail.setProcessDefinition(definition);
        detail.setAbstractInfo((String) instance.getProcessVariables().get(PROCESS_ABSINFO_KEY));
        return detail;
    }

    /**
     * 我的申请实例信息映射
     * 
     * @param vo
     * @param userMap
     * @return
     */
    private TaskDetailVO myApplyMapping(HistoricProcessInstance vo) {
        // instance数据
        HistoricProcessInstanceVO hvo = instanceMapping(vo);
        // 流程定义信息
        //ProcessDefinition definition = repositoryService.createProcessDefinitionQuery().processDefinitionId(vo.getProcessDefinitionId()).singleResult();
        ProcessDefinitionVO definition=definitionData.get(vo.getProcessDefinitionId());
        //ProcessDefinitionVO pvo = definitionMapping(definition);
        // 任务信息
        TaskVO task = new TaskVO();
        if (vo.getEndTime() == null) {// 未完成的流程加载流程的步骤数据
            // Task
            // t=taskService.createTaskQuery().processInstanceId(vo.getId()).singleResult();
            // 如果一个instance有多个活动的task(比如会签任务)，这里先临时取第一个task显示
            List<Task> ts = taskService.createTaskQuery().processInstanceId(vo.getId()).list();
            Set<String> candidates = new HashSet<String>();
            if (ts != null && ts.size() > 0) {
                Task t = ts.get(0);
                task.setId(t.getId());
                task.setName(t.getName());
                // 查询任务候选人
                List<IdentityLink> links = taskService.getIdentityLinksForTask(t.getId());
                if (links != null) {
                    for (IdentityLink link : links) {
                        if (link.getUserId() != null)
                            candidates.add(userInfoData.getUsername(link.getUserId()));
                    }
                }
            }
            task.setCandidates(CommonUtil.join(candidates, ","));
        }
        TaskDetailVO detail = new TaskDetailVO();
        detail.setTask(task);
        detail.setHistoricProcessInstance(hvo);
        detail.setProcessDefinition(definition);
        detail.setAbstractInfo((String) vo.getProcessVariables().get(PROCESS_ABSINFO_KEY));
        return detail;
    }

    /**
     * 实例查询公共方法
     * 
     * @param svo
     * @param appId
     * 系统ID
     * @return
     * 流程实例
     */
    private PageInfo<TaskDetailVO> instanceQuery(InstanceSearchVO svo,String appId) {
        HistoricProcessInstanceQuery query = historyService.createHistoricProcessInstanceQuery().includeProcessVariables();
        if (!StringUtils.isBlank(svo.getCreateUserId())) {
            query.startedBy(svo.getCreateUserId());
        }
        if (svo.getFinish() != InstanceSearchVO.UNDEFINE) {
            if (svo.getFinish() == InstanceSearchVO.FINISH) {
                query.finished();
            } else {
                query.unfinished();
            }
        }
        if (svo.getCreateStart() != null) {
            query.startedAfter(svo.getCreateStart());
        }
        if (svo.getCreateEnd() != null) {
            query.startedBefore(svo.getCreateEnd());
        }
        if (svo.getFinishStart() != null) {
            query.finishedAfter(svo.getFinishStart());
        }
        if (svo.getFinishEnd() != null) {
            query.finishedBefore(svo.getFinishEnd());
        }
        //系统ID
        if(StringUtils.isNotBlank(appId)){
        	query.processInstanceTenantId(appId);
        }
        //摘要信息模糊搜索
        if(StringUtils.isNotBlank(svo.getAbstractInfo())){
            query.variableValueLike(PROCESS_ABSINFO_KEY, "%"+svo.getAbstractInfo()+"%");
        }
        List<HistoricProcessInstance> instances = query.orderByProcessInstanceStartTime().desc().listPage(svo.getStart(),
                                                                                                          svo.getSize());
        List<TaskDetailVO> list = new ArrayList<TaskDetailVO>();
        for (HistoricProcessInstance instance : instances) {
            TaskDetailVO vo = myApplyMapping(instance);
            list.add(vo);
        }
        PageInfo<TaskDetailVO> pageInfo = new PageInfo<TaskDetailVO>();
        // Map<String, Object> paginInfo = new HashMap<String, Object>();
        pageInfo.setList(list);
        if (list != null && list.size() > 0) {
            long totalNum = query.count();
            long totalPage = totalNum % svo.getSize() == 0 ? totalNum
                    / svo.getSize() : totalNum / svo.getSize() + 1;
            pageInfo.setCount((int) totalNum);
            pageInfo.setPage(svo.getPage());
            pageInfo.setPageCount((int) totalPage);
        } else {
            pageInfo.setCount(0);
            pageInfo.setPage(svo.getPage());
            pageInfo.setPageCount(0);
        }
        return pageInfo;
    }

    @Override
    public InstanceTaskDetailVO getInstanceDetail(String processInstanceId) {
        HistoricProcessInstance instance = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        if (instance == null) {
            throw new InstanceNotFoundException();
        }
        //ProcessDefinition definition = repositoryService.createProcessDefinitionQuery().processDefinitionId(instance.getProcessDefinitionId()).singleResult();
        ProcessDefinitionVO definition=definitionData.get(instance.getProcessDefinitionId());
        if (definition == null) {
            throw new DefNotFoundException();
        }
        InstanceExecuter handler = workflowFactory.getInstanceExecuter(definition.getKey());
        if (handler == null) {
            throw new InstanceExecuteNotFoundException();
        }
        List<Task> tasks = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
        /**
         * 统一将流程的数据返回
         */
        InstanceTaskDetailVO detail = new InstanceTaskDetailVO();
        List<TaskVO> ts = new ArrayList<TaskVO>();
        for (Task task : tasks) {
            ts.add(taskMapping(task));
        }
        detail.setTasks(ts);
        detail.setProcessDefinition(definition);
        detail.setHistoricProcessInstance(instanceMapping(instance));
        Object businessData = handler.getDetail(instance.getBusinessKey(),"");
        detail.setBusinessData(businessData);
        return detail;
    }

    private TaskVO taskMapping(Task task) {
        TaskVO vo = new TaskVO();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        vo.setAssignee(task.getAssignee()==null?"":task.getAssignee());
        vo.setCreateTime(df.format(task.getCreateTime()));
        vo.setDescription(task.getDescription());
        vo.setDueDate(task.getDueDate());
        vo.setId(task.getId());
        vo.setName(task.getName());
        vo.setOwner(task.getOwner());
        vo.setProcessDefinitionId(task.getProcessDefinitionId());
        vo.setProcessInstanceId(task.getProcessInstanceId());
        vo.setTaskDefinitionKey(task.getTaskDefinitionKey());
        return vo;
    }

    private HistoricProcessInstanceVO instanceMapping(
            HistoricProcessInstance instance) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        HistoricProcessInstanceVO hvo = new HistoricProcessInstanceVO();
        hvo.setStartTime(df.format(instance.getStartTime()));
        hvo.setStartUserId(instance.getStartUserId());
        hvo.setStartUserNick(userInfoData.getUsername(instance.getStartUserId()));
        hvo.setBusinessKey(instance.getBusinessKey());
        hvo.setProcessDefinitionId(instance.getProcessDefinitionId());
        hvo.setEndTime(instance.getEndTime() == null ? null
            : df.format(instance.getEndTime()));
        hvo.setProcessInstanceId(instance.getId());
        Map<String, Object> variables = instance.getProcessVariables();
        hvo.setVariables(variables);
        if(variables!=null&&variables.containsKey(PROCESS_ABSINFO_KEY)){
            hvo.setAbstractInfo(variables.get(PROCESS_ABSINFO_KEY).toString());
        }
        return hvo;
    }

    /**
     * 通过taskId获取InstanceExecuter
     * 
     * @param taskId
     * @return
     */
    private InstanceExecuter getInstanceExecuterByTaskId(String userId,String taskId,InstanceTaskData instanceTaskData) {
        /**
         * 权限控制
         */
        Task task = taskService.createTaskQuery().taskId(taskId).taskCandidateUser(userId).singleResult();
        if(task==null){
            logger.error("任务ID:{}不存在或者用户{}没有权限操作.",taskId,userId);
            throw new TaskNotFoundException(String.format("任务ID:%s不存在或者用户%s没有权限操作",taskId,userId));
        }
        //ProcessDefinition definition = repositoryService.createProcessDefinitionQuery().processDefinitionId(task.getProcessDefinitionId()).singleResult();
        ProcessDefinitionVO definition=definitionData.get(task.getProcessDefinitionId());
        if (definition == null) {
            throw new DefNotFoundException();
        }
        HistoricProcessInstance instance = historyService.createHistoricProcessInstanceQuery().includeProcessVariables().processInstanceId(task.getProcessInstanceId()).singleResult();
        if (instance == null) {
            throw new InstanceNotFoundException();
        }
        instanceTaskData.setTask(task);
        instanceTaskData.setDefinition(definition);
        instanceTaskData.setInstance(instance);
        InstanceExecuter instanceExecuter = workflowFactory.getInstanceExecuter(definition.getKey());
        return instanceExecuter;
    }
    
    /**
     * 通过taskId获取TaskExecuter
     * 
     * @param taskId
     * @return
     */
    private TaskExecuter getTaskExecuterByTaskId(String taskId,InstanceTaskData instanceTaskData) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            throw new TaskNotFoundException();
        }
        //ProcessDefinition definition = repositoryService.createProcessDefinitionQuery().processDefinitionId(task.getProcessDefinitionId()).singleResult();
        ProcessDefinitionVO definition=definitionData.get(task.getProcessDefinitionId());
        if (definition == null) {
            throw new DefNotFoundException();
        }
        HistoricProcessInstance instance = historyService.createHistoricProcessInstanceQuery().processInstanceId(task.getProcessInstanceId()).singleResult();
        if (instance == null) {
            throw new InstanceNotFoundException();
        }
        instanceTaskData.setTask(task);
        instanceTaskData.setDefinition(definition);
        instanceTaskData.setInstance(instance);
        TaskExecuter taskExecuter= workflowFactory.getTaskExecuter(definition.getKey(), task.getTaskDefinitionKey());
        return taskExecuter;
    }

    @Override
    public TaskDetailVO getTaskDetail(String taskId,String userId) {
        /**
         * 1.获取流程数据
         */
        InstanceTaskData instanceTaskData=new InstanceTaskData();
        InstanceExecuter instanceExecuter = getInstanceExecuterByTaskId(userId,taskId,instanceTaskData);
        if (instanceExecuter == null) {
            throw new InstanceExecuteNotFoundException();
        }
        /**
         * 2.统一将流程的数据返回
         */
        TaskDetailVO detail = new TaskDetailVO();
        detail.setTask(taskMapping(instanceTaskData.getTask()));
        detail.setProcessDefinition(instanceTaskData.getDefinition());
        detail.setHistoricProcessInstance(instanceMapping(instanceTaskData.getInstance()));
        Object businessData = instanceExecuter.getDetail(instanceTaskData.getInstance().getBusinessKey(),taskId);
        detail.setBusinessData(businessData);
        return detail;
    }

    @Override
    public PageInfo<TaskDetailVO> todoTaskList(TaskSearchVO svo, String userId,String appId) {
        svo.setAssignee(userId);
        return taskQuery(svo,appId);
    }

    @Override
    public PageInfo<TaskDetailVO> claimTaskList(TaskSearchVO svo,
            String userId,String appId) {
        svo.setCandidateUser(userId);
        return taskQuery(svo,appId);
    }

    @Override
    public PageInfo<TaskDetailVO> getAllMyApply(InstanceSearchVO svo,
            String userId,String appId) {
        svo.setCreateUserId(userId);
        return instanceQuery(svo,appId);
    }

    @Override
    public PageInfo<TaskDetailVO> getRunningApply(InstanceSearchVO svo,String appId) {
        svo.setFinish(InstanceSearchVO.UNFINISH);
        return instanceQuery(svo,appId);
    }

    @Override
    public PageInfo<TaskDetailVO> getFinishApply(InstanceSearchVO svo,String appId) {
        svo.setFinish(InstanceSearchVO.FINISH);
        return instanceQuery(svo,appId);
    }
    
    public class InstanceTaskData{
        private Task task;
        private ProcessDefinitionVO definition;
        private HistoricProcessInstance instance;
        public Task getTask() {
            return task;
        }
        public void setTask(Task task) {
            this.task = task;
        }
        public ProcessDefinitionVO getDefinition() {
            return definition;
        }
        public void setDefinition(ProcessDefinitionVO definition) {
            this.definition = definition;
        }
        public HistoricProcessInstance getInstance() {
            return instance;
        }
        public void setInstance(HistoricProcessInstance instance) {
            this.instance = instance;
        }
    }

    @Override
    public Result<PageInfo<CommentVO>> commentList(final CommentSearchVO svo) {
        
        CustomSqlExecution<CommentMapper, List<CommentVO>> listExecution = new AbstractCustomSqlExecution<CommentMapper, List<CommentVO>>(CommentMapper.class) {
            public List<CommentVO> execute(
                    CommentMapper customMapper) {
                return customMapper.list(svo);
            }
        };
        CustomSqlExecution<CommentMapper, Integer> countExecution = new AbstractCustomSqlExecution<CommentMapper, Integer>(CommentMapper.class) {
            public Integer execute(
                    CommentMapper customMapper) {
                return customMapper.count(svo);
            }
        };

        
        List<CommentVO> list = managementService.executeCustomSql(listExecution);
        
        for(CommentVO vo : list) {
            //查询流程信息
            HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(vo.getProcessInstanceId()).singleResult();
            if(historicProcessInstance != null) {
                ProcessDefinitionVO definition = definitionData.get(historicProcessInstance.getProcessDefinitionId());
                if(definition != null) {
                    vo.setProcessInstanceName(definition.getName());
                }
            }
            if(StringUtils.isNotBlank(vo.getTaskId())) {
                HistoricTaskInstance task = historyService.createHistoricTaskInstanceQuery().taskId(vo.getTaskId()).singleResult();
                if(task != null) {
                    vo.setTaskName(task.getName());
                }
            }
            
            //截取操作说明(json存在fullMessage中)
            try {
                Map<String, String> map = JSONUtils.fromJson(vo.getFullMessage(), new TypeToken<Map<String, String>>() {});
                String operationInstruction = map.get(OPERATION_INSTRUCTION_KEY);
                vo.setOperationInstruction(operationInstruction);
            } catch (Exception e) {
                logger.info("解析json异常", e);
            }
            
        }
        
        int totalNum = managementService.executeCustomSql(countExecution);
        
        PageInfo<CommentVO> paginInfo = new PageInfo<CommentVO>();
        paginInfo.setList(list);

        int totalPage = totalNum % svo.getSize() == 0 ? totalNum
                / svo.getSize() : totalNum / svo.getSize() + 1;
        paginInfo.setCount(totalNum);
        paginInfo.setPage(svo.getPage());
        paginInfo.setPageCount(totalPage);
        
        return new Result<PageInfo<CommentVO>>(ResultCode.SUCCESS, paginInfo);
    }
}
