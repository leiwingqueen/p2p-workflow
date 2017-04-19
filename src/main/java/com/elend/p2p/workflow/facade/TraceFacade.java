package com.elend.p2p.workflow.facade;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.elend.p2p.workflow.service.WorkflowTraceService;
/**
 * 流程跟踪相关
 * @author liyongquan
 *
 */
@Component
public class TraceFacade {
    @Autowired
    private WorkflowTraceService traceService;
    /**
     * 获取流程跟踪信息
     * 
     * @param processInstanceId
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> traceProcess(String processInstanceId)
            throws Exception{
        return traceService.traceProcess(processInstanceId);
    }

    /**
     * 获取流程资源文件(图片或xml定义)
     * 
     * @param type
     * @param processInstanceId
     * @return
     */
    public InputStream getInstanceResource(String type, String processInstanceId){
        return traceService.getInstanceResource(type, processInstanceId);
    }
}
