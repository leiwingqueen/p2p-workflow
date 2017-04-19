package com.elend.p2p.workflow.vo;

import java.io.Serializable;

import com.elend.p2p.constant.ResultCode;

public class AddTaskResultVO implements Serializable {

    private static final long serialVersionUID = -1586118647101027089L;

    /**
     * 返回代码
     */
    private int code = ResultCode.FAILURE;

    /**
     * 信息
     */
    private String message;

    /**
     * 对象
     */
    private Long object;

    /**
     * 是否成功
     */
    private boolean success = false;

    public AddTaskResultVO() {

    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
        this.success = (code == ResultCode.SUCCESS);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getObject() {
        return object;
    }

    public void setObject(Long object) {
        this.object = object;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
