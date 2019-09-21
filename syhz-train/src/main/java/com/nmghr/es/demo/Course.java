package com.nmghr.es.demo;

import java.util.Date;

import com.frameworkset.orm.annotation.ESId;

public class Course {
	@ESId
	private String Id;// esId
	private int courseId;// 主键
	private String title;// 课程标题
	private String type;// 课程类别(环境、食品、药品、综合)
	private String describe;// 课程简
	private int enable;// 是否启用
	private int auditStatus;// 审核状态
	private Date auditTime;// 审核时间
	private String auditOpinion;// 审核意见
	private int auditUserId;// 审核人
	private String remark;// 文章简介
	private int viewNumber;// 文章浏览数
	private int belongOrgId;// 所属机构
	private int creationId;// 创建人
	private Date creationTime;// 创建时间
	private int lastId;// 修改人
	private Date latsTime;// 修改时间

	public int getCourseId() {
		return courseId;
	}

	public void setCourseId(int courseId) {
		this.courseId = courseId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDescribe() {
		return describe;
	}

	public void setDescribe(String describe) {
		this.describe = describe;
	}

	public int getEnable() {
		return enable;
	}

	public void setEnable(int enable) {
		this.enable = enable;
	}

	public int getAuditStatus() {
		return auditStatus;
	}

	public void setAuditStatus(int auditStatus) {
		this.auditStatus = auditStatus;
	}

	public Date getAuditTime() {
		return auditTime;
	}

	public void setAuditTime(Date auditTime) {
		this.auditTime = auditTime;
	}

	public String getAuditOpinion() {
		return auditOpinion;
	}

	public void setAuditOpinion(String auditOpinion) {
		this.auditOpinion = auditOpinion;
	}

	public int getAuditUserId() {
		return auditUserId;
	}

	public void setAuditUserId(int auditUserId) {
		this.auditUserId = auditUserId;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public int getViewNumber() {
		return viewNumber;
	}

	public void setViewNumber(int viewNumber) {
		this.viewNumber = viewNumber;
	}

	public int getBelongOrgId() {
		return belongOrgId;
	}

	public void setBelongOrgId(int belongOrgId) {
		this.belongOrgId = belongOrgId;
	}

	public int getCreationId() {
		return creationId;
	}

	public void setCreationId(int creationId) {
		this.creationId = creationId;
	}

	public Date getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(Date creationTime) {
		this.creationTime = creationTime;
	}

	public int getLastId() {
		return lastId;
	}

	public void setLastId(int lastId) {
		this.lastId = lastId;
	}

	public Date getLatsTime() {
		return latsTime;
	}

	public void setLatsTime(Date latsTime) {
		this.latsTime = latsTime;
	}

}
