package com.elend.p2p.workflow.vo;

import com.elend.p2p.util.vo.BaseSearchVO;

public class MyApplySearchVO extends BaseSearchVO implements
        java.io.Serializable {
    /***/
    private static final long serialVersionUID = -5866394642658367623L;

    /**
     * 开始时间(提单)
     */
    private String start_time;

    /**
     * 结束时间(提单)
     */
    private String end_time;

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

}
