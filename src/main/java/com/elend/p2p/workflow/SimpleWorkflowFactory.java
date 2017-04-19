package com.elend.p2p.workflow;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 * 简单的流程配置中心
 * @author liyongquan
 *
 */
public class SimpleWorkflowFactory implements WorkflowFactory{
    private List<WorkflowExecuter> workflowExecuterList;
    @Override
    public TaskExecuter getTaskExecuter(String processDefinitionKey,
            String taskDefinitionKey) {
        if(StringUtils.isBlank(processDefinitionKey)||StringUtils.isBlank(taskDefinitionKey))
            return null;
        WorkflowExecuter executer=getWorkflowExecuter(processDefinitionKey);
        if(executer==null)return null;
        return executer.getTaskExecuter(taskDefinitionKey);
    }

    @Override
    public InstanceExecuter getInstanceExecuter(String processDefinitionKey) {
        WorkflowExecuter executer=getWorkflowExecuter(processDefinitionKey);
        if(executer==null)return null;
        return executer.getInstanceExecuter();
    }
    
    private WorkflowExecuter getWorkflowExecuter(String processDefinitionKey){
        if(StringUtils.isBlank(processDefinitionKey))
            return null;
        for(WorkflowExecuter executer:workflowExecuterList){
            if(processDefinitionKey.equals(executer.getInstanceExecuter().getProcessDefinitionKey())){
                return executer;
            }
        }
        return null;
    }

    public List<WorkflowExecuter> getWorkflowExecuterList() {
        return workflowExecuterList;
    }

    public void setWorkflowExecuterList(List<WorkflowExecuter> workflowExecuterList) {
        this.workflowExecuterList = workflowExecuterList;
    }
}
