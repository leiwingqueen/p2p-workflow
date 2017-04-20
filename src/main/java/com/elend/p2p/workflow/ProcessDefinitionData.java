package com.elend.p2p.workflow;

import java.util.Date;
import java.util.List;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.ProcessDefinition;
import org.apache.commons.collections.map.LRUMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
    /**
     * 缓存最大存放大小
     */
    private static final int CACHE_MAX_SIZE=128;
    private LRUMap defintionMap;
    //private Map<String,ProcessDefinitionVO> defintionMap;
    
    public void setRepositoryService(RepositoryService repositoryService) {
        this.repositoryService = repositoryService;
    }

    public ProcessDefinitionData(){
        defintionMap=new LRUMap(CACHE_MAX_SIZE);
        //defintionMap=new HashMap<String, ProcessDefinitionVO>();
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
        /**
         * 1.读取缓存
         */
        ProcessDefinitionVO def=defintionMap.containsKey(id)?(ProcessDefinitionVO)defintionMap.get(id):null;
        if(def!=null)return def;
        /**
         * 2.缓存查询失败，实时查询
         */
        ProcessDefinition queryResult=repositoryService.createProcessDefinitionQuery().processDefinitionId(id).singleResult();
        if(queryResult==null){
            return null;
        }
        ProcessDefinitionVO definition=new ProcessDefinitionVO();
        definition.setId(queryResult.getId());
        definition.setKey(queryResult.getKey());
        definition.setName(queryResult.getName());
        defintionMap.put(queryResult.getId(), definition);
        return definition;
    }

    public void run() {
        load();
    }
}
