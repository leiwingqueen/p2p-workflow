package com.elend.p2p.workflow.vo;

public class TaskListSearchVO extends RunningListSearchVO implements
        java.io.Serializable {

    /**
	 * 
	 */
    private static final long serialVersionUID = 7474284289607395272L;

    /**
     * 申请人
     */
    private String user_id;

    /**
     * 提单人
     */
    private String apply_id;

    /**
     * 申请时间
     */
    private String time;

    /**
     * 任务ID
     */
    private String taskId;

    /**
     * 操作步骤
     */
    private String name;

    /**
     * 提单人(中文名)
     */
    private String create_nick;

    /**
     * 申请人(中文名)
     */
    private String apply_nick;

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getApply_id() {
        return apply_id;
    }

    public void setApply_id(String apply_id) {
        this.apply_id = apply_id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreate_nick() {
        return create_nick;
    }

    public void setCreate_nick(String create_nick) {
        this.create_nick = create_nick;
    }

    public String getApply_nick() {
        return apply_nick;
    }

    public void setApply_nick(String apply_nick) {
        this.apply_nick = apply_nick;
    }
}
