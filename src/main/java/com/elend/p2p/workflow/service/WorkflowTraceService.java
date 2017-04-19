package com.elend.p2p.workflow.service;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * 流程跟踪处理
 * 
 * @author liyongquan 2013-12-26
 */
public interface WorkflowTraceService {
    /**
     * 获取流程跟踪信息
     * 
     * @param processInstanceId
     * @return
     * @throws Exception
     */
    List<Map<String, Object>> traceProcess(String processInstanceId)
            throws Exception;

    /**
     * 获取流程资源文件(图片或xml定义)
     * 
     * @param type
     * @param processInstanceId
     * @return
     */
    InputStream getInstanceResource(String type, String processInstanceId);
}
