package com.elend.p2p.workflow.vo;

import java.util.Date;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;


/**
 * 批注信息封装
 * @author mgt
 *
 */
public class CommentVO {
    /**
     * 批注ID
     * */
    private String commentId;
    
    /** 
     * 用户ID
     * */ 
    private String userId;

    /** 创建时间 */ 
    private Date time;

    /** 任务ID */ 
    private String taskId;
    
    /**
     * 任务名称
     */
    private String taskName;

    /** 流程实例ID */ 
    private String processInstanceId;
    
    /**
     * 流程名称
     */
    private String processInstanceName;
    
    /** 批注信息 */ 
    private byte[] fullMessage;
    
    /**
     * 批注信息摘要
     */
    private String message;
    
    /**
     * 操作说明
     */
    private String operationInstruction;

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public String getFullMessage() {
        return new String(fullMessage);
    }

    public void setFullMessage(byte[] fullMessage) {
        this.fullMessage = fullMessage;
    }
    
    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getProcessInstanceName() {
        return processInstanceName;
    }

    public void setProcessInstanceName(String processInstanceName) {
        this.processInstanceName = processInstanceName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getOperationInstruction() {
        return operationInstruction;
    }

    public void setOperationInstruction(String operationInstruction) {
        this.operationInstruction = operationInstruction;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
