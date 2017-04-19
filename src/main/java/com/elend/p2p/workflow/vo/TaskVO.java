package com.elend.p2p.workflow.vo;

import java.util.Date;

public class TaskVO {
    /**
     * taskId
     */
    private String id;

    /**
     * 任务名称
     */
    private String name;

    /** Free text description of the task. */
    private String description;

    /**
     * The {@link User.getId() userId} of the person that is responsible for
     * this task.
     */
    private String owner;

    private String assignee;

    /**
     * Reference to the process instance or null if it is not related to a
     * process instance.
     */
    private String processInstanceId;

    /**
     * Reference to the process definition or null if it is not related to a
     * process.
     */
    private String processDefinitionId;

    /** The date/time when this task was created */
    private String createTime;

    /**
     * The id of the activity in the process defining this task or null if
     * this is not related to a process
     */
    private String taskDefinitionKey;

    /** Due date of the task. */
    private Date dueDate;

    /** 任务的候选人 */
    private String candidates;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public String getProcessDefinitionId() {
        return processDefinitionId;
    }

    public void setProcessDefinitionId(String processDefinitionId) {
        this.processDefinitionId = processDefinitionId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getTaskDefinitionKey() {
        return taskDefinitionKey;
    }

    public void setTaskDefinitionKey(String taskDefinitionKey) {
        this.taskDefinitionKey = taskDefinitionKey;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public void setCandidates(String candidates) {
        this.candidates = candidates;
    }

    public String getCandidates() {
        return candidates;
    }
}
