package com.elend.p2p.workflow.vo;

import com.elend.p2p.util.vo.BaseSearchVO;

public class RunningListSearchVO extends BaseSearchVO implements
        java.io.Serializable {

    /**
	 * 
	 */
    private static final long serialVersionUID = 5233699015321264415L;

    private String processDefinitionId;

    private String processDefinitionName;

    /**
     * 流程实例ID
     */
    private String processInstanceId;

    /**
     * 申请原因
     */
    private String reason;

    public String getProcessDefinitionName() {
        return processDefinitionName;
    }

    public void setProcessDefinitionName(String processDefinitionName) {
        this.processDefinitionName = processDefinitionName;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
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

}
