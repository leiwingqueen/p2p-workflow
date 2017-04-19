package com.elend.p2p.workflow.vo;

import java.io.Serializable;
import java.util.List;

/**
 * 实例、任务详细信息(用于前端详细信息展示)
 * @author liyongquan
 * 2013-12-27
 */
public class InstanceTaskDetailVO implements Serializable{
	/***/
	private static final long serialVersionUID = -7021450189273190674L;
	/**任务信息*/
	private List<TaskVO> tasks;
	/**实例信息*/
	private HistoricProcessInstanceVO historicProcessInstance;
	/**流程定义信息*/
	private ProcessDefinitionVO processDefinition;
	/**业务数据*/
	private Object businessData;
	public List<TaskVO> getTasks() {
		return tasks;
	}
	public void setTasks(List<TaskVO> tasks){
		this.tasks=tasks;
	}
	public Object getBusinessData() {
		return businessData;
	}
	public void setBusinessData(Object businessData) {
		this.businessData = businessData;
	}
	public HistoricProcessInstanceVO getHistoricProcessInstance() {
		return historicProcessInstance;
	}
	public void setHistoricProcessInstance(
			HistoricProcessInstanceVO historicProcessInstance) {
		this.historicProcessInstance = historicProcessInstance;
	}
	public ProcessDefinitionVO getProcessDefinition() {
		return processDefinition;
	}
	public void setProcessDefinition(ProcessDefinitionVO processDefinition) {
		this.processDefinition = processDefinition;
	}
	
	
	
}
