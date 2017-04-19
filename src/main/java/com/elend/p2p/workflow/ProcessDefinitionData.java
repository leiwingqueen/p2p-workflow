package com.elend.p2p.workflow;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.ProcessDefinition;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.elend.p2p.workflow.vo.ProcessDefinitionVO;

/**
 * 用户数据（加载到内存）
 * @author liyongquan
 *
 */
//@Component("processDefinitionData")
public class ProcessDefinitionData{
    protected static Log logger = LogFactory.getLog(ProcessDefinitionData.class);
    //@Autowired
    private RepositoryService repositoryService;
    
    private Map<String,ProcessDefinitionVO> defintionMap;
    
    public void setRepositoryService(RepositoryService repositoryService) {
        this.repositoryService = repositoryService;
    }

    public ProcessDefinitionData(){
        defintionMap=new HashMap<String, ProcessDefinitionVO>();
    }
    
    /**
     * 数据从activiti加载到内存
     */
    public void load(){
        logger.info("start load defintion data.");
        long startTime=new Date().getTime();
        List<ProcessDefinition> definitions = repositoryService.createProcessDefinitionQuery().list();
        for(ProcessDefinition def:definitions){
            ProcessDefinitionVO definition=new ProcessDefinitionVO();
            definition.setId(def.getId());
            definition.setKey(def.getKey());
            definition.setName(def.getName());
            defintionMap.put(def.getId(), definition);
        }
        long endTime=new Date().getTime();
        logger.info("load user defintion end .use time is:"+(endTime-startTime)+"ms");
    }
    
    /**
     * 根据ID获取流程定义
     * @param id
     * @return
     */
    public ProcessDefinitionVO get(String id){
        ProcessDefinitionVO def=defintionMap.get(id);
        return def;
    }

    public void run() {
        load();
    }
}
