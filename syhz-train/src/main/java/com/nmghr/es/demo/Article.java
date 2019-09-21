package com.nmghr.es.demo;

import java.util.Date;

import org.frameworkset.elasticsearch.entity.ESBaseData;

public class Article extends ESBaseData {
	private int trainId;// 主键
	private int modelType;// 所属模块类型(0知识库、1网上培训)
	private int modelCategory;// 所属模块分类(如知识库：法律法规、行业标准、规范制度、案件指引)
	private String title;// 文章标题
	private String type;// 文章类型
	private int category;// 文章分类
	private String publishOrgName;// 颁发机构名称
	private Date publishTime;// 颁发时间
	private Date effectiveTime;// 施行时间
	private String publishCode;// 颁发文件号
	private String content;// 文章内容
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

	public int getTrainId() {
		return trainId;
	}

	public void setTrainId(int trainId) {
		this.trainId = trainId;
	}

	public int getModelType() {
		return modelType;
	}

	public void setModelType(int modelType) {
		this.modelType = modelType;
	}

	public int getModelCategory() {
		return modelCategory;
	}

	public void setModelCategory(int modelCategory) {
		this.modelCategory = modelCategory;
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

	public int getCategory() {
		return category;
	}

	public void setCategory(int category) {
		this.category = category;
	}

	public String getPublishOrgName() {
		return publishOrgName;
	}

	public void setPublishOrgName(String publishOrgName) {
		this.publishOrgName = publishOrgName;
	}

	public Date getPublishTime() {
		return publishTime;
	}

	public void setPublishTime(Date publishTime) {
		this.publishTime = publishTime;
	}

	public Date getEffectiveTime() {
		return effectiveTime;
	}

	public void setEffectiveTime(Date effectiveTime) {
		this.effectiveTime = effectiveTime;
	}

	public String getPublishCode() {
		return publishCode;
	}

	public void setPublishCode(String publishCode) {
		this.publishCode = publishCode;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
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
