package com.elend.p2p.workflow.vo;
/**
 * 待办列表展示的任务信息
 * 
 * @author liyongquan
 * 2013-12-25
 */
public class TaskDetailVO {
	/**
	 * 任务信息
	 */
	private TaskVO task;
	/**
	 * 流程的历史信息
	 */
	private HistoricProcessInstanceVO historicProcessInstance;
	/**
	 * 流程定义信息
	 */
	private ProcessDefinitionVO processDefinition;
	/**
	 * 业务数据
	 */
	private Object businessData;
	/**
	 * 流程摘要信息(由用户定义的handler里面实现)
	 */
	private String abstractInfo; 
	
	public TaskVO getTask() {
		return task;
	}
	public void setTask(TaskVO task) {
		this.task = task;
	}
	public HistoricProcessInstanceVO getHistoricProcessInstance() {
		return historicProcessInstance;
	}
	public void setHistoricProcessInstance(
			HistoricProcessInstanceVO historicProcessInstance) {
		this.historicProcessInstance = historicProcessInstance;
	}
	public void setProcessDefinition(ProcessDefinitionVO processDefinition) {
		this.processDefinition = processDefinition;
	}
	public ProcessDefinitionVO getProcessDefinition() {
		return processDefinition;
	}
	public void setBusinessData(Object businessData) {
		this.businessData = businessData;
	}
	public Object getBusinessData() {
		return businessData;
	}
	public void setAbstractInfo(String abstractInfo) {
		this.abstractInfo = abstractInfo;
	}
	public String getAbstractInfo() {
		return abstractInfo;
	}
	
}
