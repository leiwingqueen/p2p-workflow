package com.elend.p2p.workflow.exception;

/**
 * 找不实例的处理类
 * @author liyongquan
 *
 */
public class InstanceExecuteNotFoundException extends RuntimeException{
    /**
     * 
     */
    private static final long serialVersionUID = 2171425798292310971L;
    public InstanceExecuteNotFoundException(){}
    public InstanceExecuteNotFoundException(String msg){
        super(msg);
    }
}
