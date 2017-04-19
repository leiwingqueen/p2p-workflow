package com.elend.p2p.workflow.vo;

import java.util.Date;

import com.elend.p2p.util.vo.BaseSearchVO;

public class InstanceSearchVO extends BaseSearchVO implements
        java.io.Serializable {
    /** 未定义 */
    public static final int UNDEFINE = 0;

    /** 已完成 */
    public static final int FINISH = 1;

    /** 未完成 */
    public static final int UNFINISH = 2;

    /***/
    private static final long serialVersionUID = -6096566735589750271L;

    /** 提单人 */
    private String createUserId;

    /** 开始时间(提单时间) */
    private Date createStart;

    /** 结束时间(提单时间) */
    private Date createEnd;

    /** 开始时间(结单时间) */
    private Date finishStart;

    /** 结束时间(结单时间) */
    private Date finishEnd;

    /** 开始时间(提单时间) */
    private String createStartStr;

    /** 结束时间(提单时间) */
    private String createEndStr;

    /** 开始时间(结单时间) */
    private String finishStartStr;

    /** 结束时间(结单时间) */
    private String finishEndStr;
    
    /**
     * 是否已完成 0--所有,1:已完成,2--未完成
     */
    private int finish;
    /**
     * 摘要信息(模糊查询)
     */
    private String abstractInfo;

    
    public String getCreateStartStr() {
        return createStartStr;
    }

    public void setCreateStartStr(String createStartStr) {
        this.createStartStr = createStartStr;
    }

    public String getCreateEndStr() {
        return createEndStr;
    }

    public void setCreateEndStr(String createEndStr) {
        this.createEndStr = createEndStr;
    }

    public String getFinishStartStr() {
        return finishStartStr;
    }

    public void setFinishStartStr(String finishStartStr) {
        this.finishStartStr = finishStartStr;
    }

    public String getFinishEndStr() {
        return finishEndStr;
    }

    public void setFinishEndStr(String finishEndStr) {
        this.finishEndStr = finishEndStr;
    }

    public String getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(String createUserId) {
        this.createUserId = createUserId;
    }

    public Date getCreateStart() {
        return createStart;
    }

    public void setCreateStart(Date createStart) {
        this.createStart = createStart;
    }

    public Date getCreateEnd() {
        return createEnd;
    }

    public void setCreateEnd(Date createEnd) {
        this.createEnd = createEnd;
    }

    public Date getFinishStart() {
        return finishStart;
    }

    public void setFinishStart(Date finishStart) {
        this.finishStart = finishStart;
    }

    public Date getFinishEnd() {
        return finishEnd;
    }

    public void setFinishEnd(Date finishEnd) {
        this.finishEnd = finishEnd;
    }

    public int getFinish() {
        return finish;
    }

    public void setFinish(int finish) {
        this.finish = finish;
    }

    public String getAbstractInfo() {
        return abstractInfo;
    }

    public void setAbstractInfo(String abstractInfo) {
        this.abstractInfo = abstractInfo;
    }
}
