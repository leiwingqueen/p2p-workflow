package com.elend.p2p.workflow;

/**
 * 任务工厂 负责构建流程和任务的处理类，统一的流程处理逻辑的配置中心
 * 
 * @author liyongquan 2013-11-26
 */
public interface WorkflowFactory {
    /**
     * 获取任务处理类
     * 
     * @param processDefinitionKey
     *            --流程定义ID
     * @param taskDefinitionKey
     *            --任务定义key
     * @return
     */
    TaskExecuter getTaskExecuter(String processDefinitionKey, String taskDefinitionKey);

    /**
     * 获取流程处理类
     * 
     * @param processDefinitionKey
     *            流程定义ID
     * @return
     */
    InstanceExecuter getInstanceExecuter(String processDefinitionKey);
}
