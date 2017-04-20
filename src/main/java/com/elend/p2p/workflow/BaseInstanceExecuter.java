package com.elend.p2p.workflow;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.elend.p2p.workflow.exception.InstanceNotFoundException;
import com.elend.p2p.workflow.exception.TaskNotFoundException;

/**
 * 所有instanceExecuter必须继承这个类
 * 
 * @author liyongquan
 */
@Component
public abstract class BaseInstanceExecuter implements InstanceExecuter {
    @Autowired
    private TaskService taskService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private HistoryService historyService;

    /**
     * 通过taskId获取InstanceId
     * 
     * @param taskId
     * @return
     * @throws ServiceException
     */
    public String getInstanceId(String taskId) {
        return getProcessInstance(taskId).getId();
    }

    /**
     * 通过taskId获取businessKey
     * 
     * @param taskId
     * @return
     */
    public String getBusinessKey(String taskId) {
        return getProcessInstance(taskId).getBusinessKey();
    }

    private ProcessInstance getProcessInstance(String taskId) {
        org.activiti.engine.task.Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            throw new TaskNotFoundException();
        }
        ProcessInstance instance = runtimeService.createProcessInstanceQuery().processInstanceId(task.getProcessInstanceId()).singleResult();
        if (instance == null) {
            throw new InstanceNotFoundException();
        }
        return instance;
    }

    /**
     * 获取流程业务数据(通过processInstanceId)
     * 
     * @param businessKey
     * @return
     */
    @Override
    public Object getDetailByInstanceId(String processInstanceId) {
        HistoricProcessInstance instance = historyService.createHistoricProcessInstanceQuery().includeProcessVariables().processInstanceId(processInstanceId).singleResult();
        if (instance == null)
            return null;
        return getDetail(instance.getBusinessKey(),"");
    }
}
