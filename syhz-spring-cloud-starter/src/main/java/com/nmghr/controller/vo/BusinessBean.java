package com.nmghr.controller.vo;

import java.util.Date;

public class BusinessBean {
	private String actionType;
	private Integer businessId;
	private Integer businessType;
	private Date createTime;
	private Integer createUserId;
	private String createUserName;
	private Integer id;
	private Integer msgStatus;
	public String getActionType() {
		return actionType;
	}
	public void setActionType(String actionType) {
		this.actionType = actionType;
	}
	public Integer getBusinessId() {
		return businessId;
	}
	public void setBusinessId(Integer businessId) {
		this.businessId = businessId;
	}
	public Integer getBusinessType() {
		return businessType;
	}
	public void setBusinessType(Integer businessType) {
		this.businessType = businessType;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Integer getCreateUserId() {
		return createUserId;
	}
	public void setCreateUserId(Integer createUserId) {
		this.createUserId = createUserId;
	}
	public String getCreateUserName() {
		return createUserName;
	}
	public void setCreateUserName(String createUserName) {
		this.createUserName = createUserName;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getMsgStatus() {
		return msgStatus;
	}
	public void setMsgStatus(Integer msgStatus) {
		this.msgStatus = msgStatus;
	}
	
	
	
}
