package com.elend.p2p.workflow.service;

import java.util.List;
import java.util.Map;

import org.activiti.engine.impl.pvm.process.ActivityImpl;

import com.elend.p2p.workflow.service.impl.Audit;

public interface ProcessCoreService {
	  List<ActivityImpl> findBackAvtivity(String taskId) throws Exception;
	  void backProcess(String taskId,  
	            Map<String, Object> variables, String userId) throws Exception;
	  
	 void queryHistoricProcessInstance() throws Exception;
	 List<Audit> getHistoryAudit(String taskId) throws Exception; 
}
