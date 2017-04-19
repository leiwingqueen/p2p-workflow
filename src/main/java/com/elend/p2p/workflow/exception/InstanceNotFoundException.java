package com.elend.p2p.workflow.exception;

/**
 * 找不到实例
 * @author liyongquan
 *
 */
public class InstanceNotFoundException extends RuntimeException{
    /**
     * 
     */
    private static final long serialVersionUID = 2171425798292310971L;
    public InstanceNotFoundException(){}
    public InstanceNotFoundException(String msg){
        super(msg);
    }
}
