package com.elend.p2p.workflow.service.impl;

public class Audit {
private String userName;

private String opinion;

private boolean isPass;

private String passStr;

private String auditTime;

private String activityName;




public String getPassStr() {
	if (isPass) {
		return "通过";
	}
	
	return "不通过";
}

public void setPassStr(String passStr) {
	this.passStr = passStr;
}

public String getActivityName() {
	return activityName;
}

public void setActivityName(String activityName) {
	this.activityName = activityName;
}

public String getUserName() {
	return userName;
}

public void setUserName(String userName) {
	this.userName = userName;
}

public String getOpinion() {
	return opinion;
}

public void setOpinion(String opinion) {
	this.opinion = opinion;
}



public boolean isPass() {
	return isPass;
}

public void setPass(boolean isPass) {
	this.isPass = isPass;
}

public String getAuditTime() {
	return auditTime;
}

public void setAuditTime(String auditTime) {
	this.auditTime = auditTime;
}


}
