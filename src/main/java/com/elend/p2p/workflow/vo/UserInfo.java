package com.elend.p2p.workflow.vo;

/**
 * 用户信息
 * @author liyongquan
 *
 */
public class UserInfo {
    /**
     * 用户ID
     */
    private String id;
    /**
     * 显示名称
     */
    private String username;
    
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
}
