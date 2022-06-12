package com.nmghr.handler.vo;

import org.frameworkset.elasticsearch.entity.ESBaseData;

// 规则制度
public class Standardinfo extends ESBaseData {

	private String documentId;// 主键
	private String title;// 文章标题
	private int articleType;// 文章类型（3环境、1食品、2药品、4综合）
	private int category;// 文章分类（法律、行政法规、地方性行政法规、部门规章、司法解释、其他规范性文件）
	private String content;// 文章内容
	private int enable;// 是否启用 0:启用;1:禁用
	private int delFlag;// 删除标记 0:正常;1:删除
	private int auditStatus;// 审核状态
	private String publishTime;// 审核时间
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

	public int getCategory() {
		return category;
	}

	public void setCategory(int category) {
		this.category = category;
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

	public String getPublishTime() {
		return publishTime;
	}

	public void setPublishTime(String publishTime) {
		this.publishTime = publishTime;
	}



}
