package com.elend.p2p.workflow.vo;

import java.util.Date;

import com.elend.p2p.util.vo.BaseSearchVO;

/**
 * 任务查找搜索条件
 * 
 * @author liyongquan 2013-12-21
 */
public class TaskSearchVO extends BaseSearchVO {
    /** 开始时间(任务创建时间) */
    private Date startTime;

    /** 结束时间(任务创建时间) */
    private Date endTime;

    /** 开始时间(任务创建时间) */
    private String startTimeStr;

    /** 结束时间(任务创建时间) */
    private String endTimeStr;
    
    
    /** 任务候选人 */
    private String candidateUser;

    /** 任务指派人 */
    private String assignee;
    
    private String processDefinitionKey;
    /**
     * 摘要信息(模糊查询)
     */
    private String abstractInfo;

    public String getProcessDefinitionKey() {
        return processDefinitionKey;
    }

    public void setProcessDefinitionKey(String processDefinitionKey) {
        this.processDefinitionKey = processDefinitionKey;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }
    
    public String getStartTimeStr() {
        return startTimeStr;
    }

    public void setStartTimeStr(String startTimeStr) {
        this.startTimeStr = startTimeStr;
    }

    public String getEndTimeStr() {
        return endTimeStr;
    }

    public void setEndTimeStr(String endTimeStr) {
        this.endTimeStr = endTimeStr;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getCandidateUser() {
        return candidateUser;
    }

    public void setCandidateUser(String candidateUser) {
        this.candidateUser = candidateUser;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public String getAbstractInfo() {
        return abstractInfo;
    }

    public void setAbstractInfo(String abstractInfo) {
        this.abstractInfo = abstractInfo;
    }
}
