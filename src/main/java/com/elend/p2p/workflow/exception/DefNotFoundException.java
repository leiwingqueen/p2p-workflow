package com.elend.p2p.workflow.exception;

/**
 * 找不到流程定义
 * @author liyongquan
 *
 */
public class DefNotFoundException extends RuntimeException{
    /**
     * 
     */
    private static final long serialVersionUID = 2171425798292310971L;
    public DefNotFoundException(){}
    public DefNotFoundException(String msg){
        super(msg);
    }
}
