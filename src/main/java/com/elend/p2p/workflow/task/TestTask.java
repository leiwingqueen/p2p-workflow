package com.elend.p2p.workflow.task;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import com.elend.p2p.workflow.WorkflowUserInfoData;

/**
 * 增加测试用例
 * @author lxy
 *
 */
@Component
public class TestTask {
	
	protected static Log logger = LogFactory.getLog(WorkflowUserInfoData.class);
	
	//@Scheduled(fixedRate=2000)  // 每隔2秒执行一次
    public void test(){  
        System.out.println("test"+(new Date()));
        //logger.debug("============test========");
        
    }  
}
