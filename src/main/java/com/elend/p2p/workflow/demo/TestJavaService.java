package com.elend.p2p.workflow.demo;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

/**
 * activiti调用java内部类测试
 * @author liyongquan 2015年12月22日
 *
 */
public class TestJavaService implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) throws Exception {
        System.out.println("this is my java service");
        //参数获取
        String var = (String) execution.getVariable("input");
        System.out.println("get input:"+var);
    }
}

