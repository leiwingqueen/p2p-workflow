package com.elend.p2p.workflow;

import java.util.Map;

import com.elend.p2p.workflow.vo.CreateInstanceResponse;

public interface InstanceExecuter {
    /**
     * 提单页面
     * 
     * @return
     */
    String getCreatePage();

    /**
     * 流程详细信息页面(查看)
     * 
     * @return
     */
    String getDetailPage();

    /**
     * 获取流程业务数据 这里会统一将业务数据返回到workflowService重新组装
     * 
     * @param businessKey
     * @param taskId
     * 任务ID
     * @return
     */
    Object getDetail(String businessKey,String taskId);
    
    /**
     * 获取流程业务数据(通过processInstanceId)
     * 
     * @param businessKey
     * @return
     */
    Object getDetailByInstanceId(String processInstanceId);

    /**
     * 创建流程(创建成功的话必须返回bussniessKey和variables给上层)
     * 
     * @return businessKey--,variables--
     */
    CreateInstanceResponse create(Map<String, String> paramMap);

    /**
     * 获取流程定义的KEY
     * 
     * @return
     */
    String getProcessDefinitionKey();

    /**
     * 发送提单邮件/短信
     * 
     * @param paramMap
     * @param businessKey
     */
    void sendCreateMsg(Map<String, String> paramMap,
            String businessKey, String processInstanceId);

    /**
     * 更新流程实例的摘要信息
     * 
     * @param business
     * @return
     */
    String getAbstractInfo(String businessKey);
}
