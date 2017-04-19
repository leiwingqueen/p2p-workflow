package com.elend.p2p.workflow;

import java.util.Map;

/**
 * 任务执行
 * 
 * @author liyongquan 2013-11-25
 */
public abstract class TaskExecuter {
    /**
     * 获取任务定义的KEY
     * @return
     */
    public abstract String getTaskDefinitionKey();
    /**
     * 任务执行
     * 
     * @param vo
     *            --表单数据
     * @param businessKey
     *            --业务表key
     * @return
     */
    public abstract Map<String, Object> process(String businessKey,
            String processInstanceId, Map<String, String> paramMap);

    /**
     * 获取任务的处理页面 一般来说一个任务对应一个处理页面，提供这个参数防止一个处理逻辑多个处理页面的特殊情况，开发者可以 根据自己的需要进行扩展
     * 
     * @param taskId
     *            --任务ID
     * @return
     */
    public abstract String getCompletePage();
    
    /**
     * 获取m版处理页(财务m版使用)
     * 
     * @param taskId
     *            --任务ID
     * @return
     */
    public String getMCompletePage(){
        return "";
    }

    /**
     * 发送邮件
     * 
     * @param param
     * @param businessKey
     */
    public abstract void sendMsg(Map<String, String> param, String businessKey,
            String processInstanceId);

    /**
     * 更新流程数据的摘要信息
     * 
     * @return
     */
    public abstract String getAbstractInfo(String businessKey);
    
    /**
     * 获取操作说明（如通过，驳回）
     * @return
     */
    public abstract String getOperationInstruction(Map<String, String> paramMap);
}
