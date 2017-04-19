package com.elend.p2p.workflow;

/**
 * 流程处理类
 * 整个业务流程的处理包含一个instanceExecuter和多个taskExecuter
 * @author liyongquan
 *
 */
public interface WorkflowExecuter {
    /**
     * 获取流程的执行类
     * @return
     */
    InstanceExecuter getInstanceExecuter();
    /**
     * 根据taskDefinitionKey获取任务的执行类
     * @param taskDefinitionKey
     * @return
     */
    TaskExecuter getTaskExecuter(String taskDefinitionKey);
}
