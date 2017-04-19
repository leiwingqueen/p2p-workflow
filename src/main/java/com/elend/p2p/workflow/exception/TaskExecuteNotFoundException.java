package com.elend.p2p.workflow.exception;

/**
 * 找不到任务处理类
 * @author liyongquan
 *
 */
public class TaskExecuteNotFoundException extends RuntimeException{
    /**
     * 
     */
    private static final long serialVersionUID = 2171425798292310971L;
    public TaskExecuteNotFoundException(){}
    public TaskExecuteNotFoundException(String msg){
        super(msg);
    }
}
