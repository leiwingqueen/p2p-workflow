package com.elend.p2p.workflow;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 * 简单的任务执行类实现
 * @author liyongquan
 *
 */
public class SimpleWorkflowExecuter implements WorkflowExecuter{
    /**
     * 流程执行类
     */
    private InstanceExecuter instanceExecuter;
    /**
     * 任务执行类
     */
    private List<TaskExecuter> taskExecuterList;

    @Override
    public InstanceExecuter getInstanceExecuter() {
        return instanceExecuter;
    }

    @Override
    public TaskExecuter getTaskExecuter(String taskDefinitionKey) {
        if(StringUtils.isBlank(taskDefinitionKey))return null;
        for(TaskExecuter taskExecuter:taskExecuterList){
            if(taskDefinitionKey.equals(taskExecuter.getTaskDefinitionKey())){
                return taskExecuter;
            }
        }
        return null;
    }

    public List<TaskExecuter> getTaskExecuterList() {
        return taskExecuterList;
    }

    public void setTaskExecuterList(List<TaskExecuter> taskExecuterList) {
        this.taskExecuterList = taskExecuterList;
    }

    public void setInstanceExecuter(InstanceExecuter instanceExecuter) {
        this.instanceExecuter = instanceExecuter;
    }
}
