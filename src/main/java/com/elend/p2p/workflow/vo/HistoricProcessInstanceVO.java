package com.elend.p2p.workflow.vo;

import java.util.Map;

public class HistoricProcessInstanceVO {
    private String processInstanceId;

    private String processDefinitionId;

    /**
     * 结束时间，还未结束的实例为null
     */
    private String endTime;

    /**
     * 业务表key
     */
    private String businessKey;

    private String startTime;

    private String startUserId;

    private String startUserNick;

    private Map<String, Object> variables;
    /**
     * 流程摘要信息
     */
    private String abstractInfo="";
    
    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getStartUserId() {
        return startUserId;
    }

    public void setStartUserId(String startUserId) {
        this.startUserId = startUserId;
    }

    public void setStartUserNick(String startUserNick) {
        this.startUserNick = startUserNick;
    }

    public String getStartUserNick() {
        return startUserNick;
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

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getBusinessKey() {
        return businessKey;
    }

    public void setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
    }

    public void setVariables(Map<String, Object> variables) {
        this.variables = variables;
    }

    public Map<String, Object> getVariables() {
        return variables;
    }

    public String getAbstractInfo() {
        return abstractInfo;
    }

    public void setAbstractInfo(String abstractInfo) {
        this.abstractInfo = abstractInfo;
    }

}
