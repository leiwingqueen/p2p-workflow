package com.elend.p2p.workflow.facade;

import java.util.List;
import java.util.Map;

import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.elend.p2p.PageInfo;
import com.elend.p2p.Result;
import com.elend.p2p.workflow.exception.DefNotFoundException;
import com.elend.p2p.workflow.exception.TaskExecuteNotFoundException;
import com.elend.p2p.workflow.exception.TaskNotFoundException;
import com.elend.p2p.workflow.service.ProcessCoreService;
import com.elend.p2p.workflow.service.WorkflowService;
import com.elend.p2p.workflow.service.impl.Audit;
import com.elend.p2p.workflow.vo.CommentSearchVO;
import com.elend.p2p.workflow.vo.CommentVO;
import com.elend.p2p.workflow.vo.InstanceSearchVO;
import com.elend.p2p.workflow.vo.InstanceTaskDetailVO;
import com.elend.p2p.workflow.vo.TaskDetailVO;
import com.elend.p2p.workflow.vo.TaskSearchVO;

/**
 * 流程相关门面类
 * @author liyongquan
 *
 */
@Component
public class WorkflowFacade {
    @Autowired
    private WorkflowService workflowService;
    
    @Autowired
    private ProcessCoreService processCoreService;
    
    /************************************流程操作类接口***********************************************/
    /**
     * 签收任务
     * 
     * @param taskId
     *            --任务ID
     * @return
     */
    public void claim(String userId,String taskId){
        workflowService.claim(userId, taskId);
    }

    /**
     * 提单
     * 
     * @param paramMap
     * @param processDefinitionId
     * @param appId
     * 系统ID
     * @return
     */
    public void create(String userId,Map<String, String> paramMap, String processDefinitionId,String appId){
        workflowService.create(userId, paramMap, processDefinitionId,appId);
    }

    /**
     * 任务处理逻辑
     * 
     * @param paramMap
     * @param taskId
     * @return
     */
    public void complete(String userId,Map<String, String> paramMap, String taskId){
        workflowService.complete(userId, paramMap, taskId);
    }
    /**
     * 强制结束流程
     * 
     * @param taskId
     * @return
     */
    public Result<String> endProcessByProcessInstanceId(String userName,String processInstanceId){
        return workflowService.endProcessByProcessInstanceId(userName,processInstanceId);
    }

    /************************************页面展示类接口***********************************************/
    /**
     * 提单页面
     * 
     * @return--提单页面的url
     */
    public String createPage(String processDefinitionId){
        return workflowService.createPage(processDefinitionId);
    }
    /**
     * 任务处理页面
     * @param taskId
     * 任务ID
     * @param userId
     * 用户ID
     * @return
     * 处理页面
     */
    public String completePage(String taskId,String userId)throws TaskNotFoundException{
        return workflowService.completePage(taskId,userId);
    }
    
    /**
     * 任务处理页面(财务m版)
     * @param taskId
     * 任务ID
     * @param userId
     * 用户ID
     * @return
     * 对应的处理页地址
     */
    public String mCompletePage(String taskId,String userId) throws TaskExecuteNotFoundException,DefNotFoundException{
        return workflowService.mCompletePage(taskId,userId);
    }
    
    /**
     * 查询流程信息页面
     * 
     * @param taskId
     * @return
     */
    public String getDetailPage(String processInstanceId){
        return workflowService.getDetailPage(processInstanceId);
    }

    /**
     * 获取流程实例业务数据
     * 
     * @param processInstanceId
     * @return
     */
    public InstanceTaskDetailVO getInstanceDetail(String processInstanceId){
        return workflowService.getInstanceDetail(processInstanceId);
    }

    /**
     * 获取任务详细信息(包含业务数据)
     * 
     * @param taskId
     * 任务ID
     * @param
     * 用户ID
     * @return
     * 任务详细信息
     */
    public TaskDetailVO getTaskDetail(String taskId,String userId){
        return workflowService.getTaskDetail(taskId,userId);
    }
    
    /************************************通用查询类接口***********************************************/

    /**
     * 待办列表
     * @param svo
     * 查询条件
     * @param userId
     * 用户ID
     * @param appId
     * 系统ID
     * @return
     * 任务列表
     */
    public PageInfo<TaskDetailVO> todoTaskList(TaskSearchVO svo,String userId,String appId){
        return workflowService.todoTaskList(svo, userId,appId);
    }

    /**
     * 待签收列表
     * @param svo
     * 查询条件
     * @param userId
     * 用户ID
     * @param appId
     * 系统ID
     * @return
     * 任务列表
     */
    public PageInfo<TaskDetailVO> claimTaskList(TaskSearchVO svo,String userId,String appId){
        return workflowService.claimTaskList(svo, userId,appId);
    }

    /**
     * 获取该用户的所有的申请
     * 
     * @param svo
     * @param appId
     * 系统ID
     * @return
     */
    public PageInfo<TaskDetailVO> getAllMyApply(InstanceSearchVO svo,String userId,String appId){
        return workflowService.getAllMyApply(svo, userId,appId);
    }

    /**
     * 获取所有进行的流程
     * 
     * @param vo
     * @param searchOptions
     * @param appId
     * 系统ID
     * @return
     */
    public PageInfo<TaskDetailVO> getRunningApply(InstanceSearchVO svo,String appId){
        return workflowService.getRunningApply(svo,appId);
    }

    /**
     * 获取所有完成的流程
     * 
     * @param svo
     * @param appId
     * 系统ID
     * @return
     */
    public PageInfo<TaskDetailVO> getFinishApply(InstanceSearchVO svo,String appId){
        return workflowService.getFinishApply(svo,appId);
    }
    
    
    /**
     * 获取所有完成的流程
     * 
     * @param svo
     * @return
     * @throws Exception 
     */
    public List<ActivityImpl> findBackAvtivity(String taskId) throws Exception{
        return processCoreService.findBackAvtivity(taskId);
    }
    
    
    public void backProcess(String userId, String taskId) throws Exception{
         processCoreService.backProcess(taskId, null, userId);
    }
    
    public void  queryHistoricProcessInstance() throws Exception{
    	processCoreService.queryHistoricProcessInstance();
    }
    
    
    public List<Audit>  getHistoryAudit(String taskId) throws Exception{
    	return processCoreService.getHistoryAudit(taskId);
    }

    /**
     * 查询批注
     * @param svo
     * @return
     */
    public Result<PageInfo<CommentVO>> commentList(CommentSearchVO svo) {
        return workflowService.commentList(svo);
    }
}
