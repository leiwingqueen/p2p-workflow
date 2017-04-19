package com.elend.p2p.workflow.service;

import java.util.Map;

import com.elend.p2p.PageInfo;
import com.elend.p2p.Result;
import com.elend.p2p.workflow.exception.DefNotFoundException;
import com.elend.p2p.workflow.exception.TaskExecuteNotFoundException;
import com.elend.p2p.workflow.vo.CommentSearchVO;
import com.elend.p2p.workflow.vo.CommentVO;
import com.elend.p2p.workflow.vo.InstanceSearchVO;
import com.elend.p2p.workflow.vo.InstanceTaskDetailVO;
import com.elend.p2p.workflow.vo.TaskDetailVO;
import com.elend.p2p.workflow.vo.TaskSearchVO;

/**
 * @author liyongquan 2013-11-26
 */

public interface WorkflowService {
    /************************************流程操作类接口***********************************************/
    /**
     * 签收任务
     * 
     * @param taskId
     *            --任务ID
     * @return
     */
    void claim(String userId,String taskId);

    /**
     * 提单
     * 
     * @param paramMap
     * @param processDefinitionId
     * @return
     */
    void create(String userId,Map<String, String> paramMap, String processDefinitionId);

    /**
     * 任务处理逻辑
     * 
     * @param paramMap
     * @param taskId
     * @return
     */
    void complete(String userId,Map<String, String> paramMap, String taskId);
    /**
     * 强制结束流程
     * 
     * @param userName--用户名
     * @param processInstanceId--实例ID
     * @return
     */
    Result<String> endProcessByProcessInstanceId(String userName,String processInstanceId);

    /************************************页面展示类接口***********************************************/
    /**
     * 提单页面
     * 
     * @return--提单页面的url
     */
    String createPage(String processDefinitionId);
    /**
     * 任务处理页面
     * @param taskId
     * 任务ID
     * @param userId
     * 用户ID
     * @return
     */
    String completePage(String taskId,String userId);
    
    /**
     * 任务处理页面(财务m版)
     * @param taskId
     * 任务ID
     * @param userId
     * 用户ID
     * @return
     * 对应的处理页地址
     */
    String mCompletePage(String taskId,String userId) throws TaskExecuteNotFoundException,DefNotFoundException;
    
    /**
     * 查询流程信息页面
     * 
     * @param taskId
     * @return
     */
    String getDetailPage(String processInstanceId);

    /**
     * 获取流程实例业务数据
     * 
     * @param processInstanceId
     * @return
     */
    InstanceTaskDetailVO getInstanceDetail(String processInstanceId);

    /**
     * 获取任务详细信息(包含业务数据)
     * 
     * @param taskId
     * 任务ID
     * @param userId
     * 用户ID
     * @return
     * 任务详细信息
     */
    TaskDetailVO getTaskDetail(String taskId,String userId);
    
    /************************************通用查询类接口***********************************************/

    /**
     * 待办列表
     * 
     * @return
     */
    PageInfo<TaskDetailVO> todoTaskList(TaskSearchVO svo,String userId);

    /**
     * 待签收列表
     * 
     * @return
     */
    PageInfo<TaskDetailVO> claimTaskList(TaskSearchVO svo,String userId);

    /**
     * 获取该用户的所有的申请
     * 
     * @param svo
     * @return
     */
    PageInfo<TaskDetailVO> getAllMyApply(InstanceSearchVO svo,String userId);

    /**
     * 获取所有进行的流程
     * 
     * @param vo
     * @param searchOptions
     * @return
     */
    PageInfo<TaskDetailVO> getRunningApply(InstanceSearchVO svo);

    /**
     * 获取所有完成的流程
     * 
     * @param svo
     * @return
     */
    PageInfo<TaskDetailVO> getFinishApply(InstanceSearchVO svo);

    /**
     * 
     * @param svo
     * @return
     */
    Result<PageInfo<CommentVO>> commentList(CommentSearchVO svo);
}
