package com.elend.p2p.workflow.vo;

import java.util.Map;

/**
 * 所有流程业务数据都继承这个类
 * 
 * @author liyongquan
 */
public class ContentVO {
    private Map<String, Object> task;

    private Map<String, Object> variables;

    /**
     * 流程类型
     */
    private String taskDefinitionKey;

    /**
     * 任务ID
     */
    private String taskId;

    public void setTask(Map<String, Object> task) {
        this.task = task;
    }

    public Map<String, Object> getTask() {
        return task;
    }

    public void setVariables(Map<String, Object> variables) {
        this.variables = variables;
    }

    public Map<String, Object> getVariables() {
        return variables;
    }

    public String getTaskDefinitionKey() {
        return taskDefinitionKey;
    }

    public void setTaskDefinitionKey(String taskDefinitionKey) {
        this.taskDefinitionKey = taskDefinitionKey;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

}
