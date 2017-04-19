package com.elend.p2p.workflow.exception;

/**
 * 找不到任务
 * @author liyongquan
 *
 */
public class TaskNotFoundException extends RuntimeException{
    /**
     * 
     */
    private static final long serialVersionUID = 2171425798292310971L;
    public TaskNotFoundException(){}
    public TaskNotFoundException(String msg){
        super(msg);
    }
}
