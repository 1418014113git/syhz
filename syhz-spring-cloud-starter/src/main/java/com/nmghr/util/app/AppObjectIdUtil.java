package com.nmghr.util.app;

public class AppObjectIdUtil {
	private static final String PERSON_MESSAGE = "personMessage";// 获取字典
	private static final String USER_QUERY = "userMessageAppLogin";// 获取用户信息
	private static final String USER_UPDATE = "userMessage";// 编辑用户信息
	private static final String MESSAGE_PAGE = "sysMessagesPage";// 消息中心列表
	private static final String MESSAGE_DETAIL = "sysMessagesDetail";// 获取站内消息详情
	private static final String MESSAGE_DEL = "sysMessagesDel";// 删除消息
	private static final String MESSAGES = "sysMessages";// 提醒消息总计
	private static final String MESSAGES_STATUS = "sysMessagesStatus";// 修改已读状态

	private static final String NOTICE_HOME = "noticeHome";// 获取站内通知
	private static final String NOTICE_LIST = "noticeList";// 站内通知列表
	private static final String NOTICE_DETAIL = "noticeDetail";// 站内通知详情
	private static final String NOTICE_FLOW_LIST = "basemessageflowlsit";// 站内通知审核记录
	private static final String NOTICE_SIGN_LIST = "basemessagesign";// 站内通知签收记录
	private static final String NOTICE_SIGN_SAVE = "noticeSign";// 站内通知签收
	private static final String NOTICE_SAVE = "noticeSave";// 发送通知
	private static final String NOTICE_VAILD_NAME = "noticeValidName";// 名字校验

	private static final String DEPT_CHILDREN = "deptsbyparentdeptcode";// 子机构信息
	private static final String DEPT_USER = "departuser";// 机构下人员
	private static final String DEPARTTREE = "departtree";// 部门

	private static final String MESSAGE_QUERY = "messageQuery";// 站内消息列表
	private static final String MESSAGE_DELETE = "messageDelete";// 删除站内消息
	private static final String MESSAGE_SEND = "messageSend";// 发送通知

	private static final String GROUP_INFO = "groupInfo";// 查询常用组
	private static final String GROUP_VAILD_NAME = "groupVaildName";// 检测标题的唯一性
	private static final String GROUP_DEL = "groupdel";// 删除常用组
	private static final String GROUP_UPDATE = "groupUpdate";// 修改常用组
	private static final String GROUP_SAVE = "groupSave";// 添加常用组
	private static final String GROUP_Detail = "groupDetail";// 添加常用组

	private static final String CASEMANAGE_CASELIST = "CaseList";// 案件查询列表
	private static final String CASEMANAGE_CASEDETAIL = "CaseDetail";// 案件查详情
	private static final String CASEMANAGE_CASEAJZM = "CaseAjzm";// 案件罪名
	private static final String CASEMANAGE_CASEAJZM_CODE = "CaseAjzmCode";// 案件罪名code
	private static final String CASEMANAGE_CASEAJLB = "CaseAjlb";// 案件类别
	private static final String CASEMANAGE_CASETCPCODE = "CaseTcpCode";// 案件状态
	private static final String CASEMANAGE_CASEGROUP = "CaseGroup";// 首页代办

	private static final String CLUE_LIST = "clueList";// 线索列表查询
	private static final String CLUE_DETAIL = "clueDetail";// 线索详情查询
	private static final String CLUE_SAVE = "clueSave";// 线索采集

	public static String getClueList() {
		return CLUE_LIST;
	}

	public static String getClueDetail() {
		return CLUE_DETAIL;
	}

	public static String getClueSave() {
		return CLUE_SAVE;
	}

	public static String getCasemanageCasegroup() {
		return CASEMANAGE_CASEGROUP;
	}

	public static String getGroupDetail() {
		return GROUP_Detail;
	}

	public static String getGroupSave() {
		return GROUP_SAVE;
	}

	public static String getDeparttree() {
		return DEPARTTREE;
	}

	public static String getMessagesStatus() {
		return MESSAGES_STATUS;
	}

	public static String getCasemanageCaselist() {
		return CASEMANAGE_CASELIST;
	}

	public static String getCasemanageCasedetail() {
		return CASEMANAGE_CASEDETAIL;
	}

	public static String getCasemanageCaseajzm() {
		return CASEMANAGE_CASEAJZM;
	}

	public static String getCasemanageCaseajzmCode() {
		return CASEMANAGE_CASEAJZM_CODE;
	}

	public static String getCasemanageCaseajlb() {
		return CASEMANAGE_CASEAJLB;
	}

	public static String getCasemanageCasetcpcode() {
		return CASEMANAGE_CASETCPCODE;
	}

	public static String getPersonMessage() {
		return PERSON_MESSAGE;
	}

	public static String getUserQuery() {
		return USER_QUERY;
	}

	public static String getUserUpdate() {
		return USER_UPDATE;
	}

	public static String getMessagePage() {
		return MESSAGE_PAGE;
	}

	public static String getMessageDetail() {
		return MESSAGE_DETAIL;
	}

	public static String getMessageDel() {
		return MESSAGE_DEL;
	}

	public static String getMessages() {
		return MESSAGES;
	}

	public static String getNoticeHome() {
		return NOTICE_HOME;
	}

	public static String getNoticeList() {
		return NOTICE_LIST;
	}

	public static String getNoticeDetail() {
		return NOTICE_DETAIL;
	}

	public static String getNoticeFlowList() {
		return NOTICE_FLOW_LIST;
	}

	public static String getNoticeSignList() {
		return NOTICE_SIGN_LIST;
	}

	public static String getNoticeSignSave() {
		return NOTICE_SIGN_SAVE;
	}

	public static String getNoticeSave() {
		return NOTICE_SAVE;
	}

	public static String getNoticeVaildName() {
		return NOTICE_VAILD_NAME;
	}

	public static String getDeptChildren() {
		return DEPT_CHILDREN;
	}

	public static String getDeptUser() {
		return DEPT_USER;
	}

	public static String getMessageQuery() {
		return MESSAGE_QUERY;
	}

	public static String getMessageDelete() {
		return MESSAGE_DELETE;
	}

	public static String getMessageSend() {
		return MESSAGE_SEND;
	}

	public static String getGroupInfo() {
		return GROUP_INFO;
	}

	public static String getGroupVaildName() {
		return GROUP_VAILD_NAME;
	}

	public static String getGroupDel() {
		return GROUP_DEL;
	}

	public static String getGroupUpdate() {
		return GROUP_UPDATE;
	}

}
