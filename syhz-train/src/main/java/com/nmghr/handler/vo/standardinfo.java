package com.nmghr.handler.vo;

import java.util.Date;

import org.frameworkset.elasticsearch.entity.ESBaseData;

// 规则制度
public class standardinfo extends ESBaseData {

	private String documentId;// 主键

	private String title;// 文章标题
	private int articleType;// 文章类型（3环境、1食品、2药品、4综合）
	private String publishOrg; // 颁发机构
	private String publishCode; // 颁发文件号
	private String content;// 文章内容
	private int enable;// 是否启用 0:启用;1:禁用
	private int delFlag;// 删除标记 0:正常;1:删除
	private int auditStatus;// 审核状态
	private int viewNumber;// 文章浏览数
	private Date auditTime;// 审核时间
	private String remark;// 备注
	private Date creationTime;// 创建时间
	private Date latsTime;// 修改时间


	public String getDocumentId() {
		return documentId;
	}

	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public int getArticleType() {
		return articleType;
	}
	public void setArticleType(int articleType) {
		this.articleType = articleType;
	}
	public String getPublishOrg() {
		return publishOrg;
	}
	public void setPublishOrg(String publishOrg) {
		this.publishOrg = publishOrg;
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
	public int getDelFlag() {
		return delFlag;
	}
	public void setDelFlag(int delFlag) {
		this.delFlag = delFlag;
	}
	public int getAuditStatus() {
		return auditStatus;
	}
	public void setAuditStatus(int auditStatus) {
		this.auditStatus = auditStatus;
	}
	public int getViewNumber() {
		return viewNumber;
	}
	public void setViewNumber(int viewNumber) {
		this.viewNumber = viewNumber;
	}
	public Date getAuditTime() {
		return auditTime;
	}
	public void setAuditTime(Date auditTime) {
		this.auditTime = auditTime;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public Date getCreationTime() {
		return creationTime;
	}
	public void setCreationTime(Date creationTime) {
		this.creationTime = creationTime;
	}
	public Date getLatsTime() {
		return latsTime;
	}
	public void setLatsTime(Date latsTime) {
		this.latsTime = latsTime;
	}

}
