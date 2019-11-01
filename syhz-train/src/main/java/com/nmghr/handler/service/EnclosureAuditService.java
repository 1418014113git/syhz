package com.nmghr.handler.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.ISaveHandler;
import com.nmghr.basic.core.util.SpringUtils;
import com.nmghr.basic.core.util.ValidationUtils;
import com.nmghr.handler.message.QueueConfig;
import com.nmghr.util.SyhzUtil;

/**
 * 审核公共类
 * 
 * @author heijiantao
 * @date 2019年9月26日
 * @version 1.0
 */
@Service
public class EnclosureAuditService {
	@Autowired
	TrainWorkorderExamineService trainWorkorderExamineService;
	@Autowired
	EsService esService;
	@Autowired
	private SendMessageService snedMessgeService;

	private static String ALIAS_ENCLOSURE = "TRAINKONWLEGEENCLOSURE";// 法律法规
	private static String ALIAS_TRAINRULECONFIG = "TRAINRULECONFIG";// 法律法规
	private static String ALIAS_PERSONKNOWLEDGE = "PERSONKNOWLEDGE";// 附件基本信息
	private static String ALIAS_KNOWLEDGEENCLOSURE = "KNOWLEDGEENCLOSURE";// 行业标准
	// private static String ALIAS_ACCPETDEPARTCODE = "ACCPETDEPARTCODE";// 消息发送单位
	private static String ALIAS_TRAINUSERNAME = "TRAINUSERNAME";// 消息发送人

	// 积分参数组装
	public Map<String, Object> rule(Map<String, Object> ruleMap, IBaseService baseService) throws Exception {
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS_PERSONKNOWLEDGE);
		String tableId = SyhzUtil.setDate(ruleMap.get("tableId"));
		Map<String, Object> person = (Map<String, Object>) baseService.get(ruleMap);
		ruleMap.put("ruleType", 2);
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS_TRAINRULECONFIG);
		List<Map<String, Object>> rule = (List<Map<String, Object>>) baseService.list(ruleMap);
		if (rule != null && rule.size() > 0) {
			ruleMap.put("branch", SyhzUtil.setDateInt(rule.get(0).get("oneNumber")));
			ruleMap.put("maxBranch", SyhzUtil.setDateInt(rule.get(0).get("maxNumber")));
			ruleMap.put("fractionType", 2);
			ruleMap.put("fractionNumber", 1);
			Date date = new Date();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			ruleMap.put("fractionTime", dateFormat.format(date));
			if (person != null) {
				ruleMap.putAll(person);
			}
		}
		return ruleMap;
	}

	// 附件保存
	public void enclouseSave(Map<String, Object> requestBody, Object id, IBaseService baseService, int belongMode)
			throws Exception {
		String enclosure = String.valueOf(requestBody.get("enclosure"));// 附件Josn传
		JSONArray array = JSONArray.parseArray(enclosure);
		for (int i = 0; i < array.size(); i++) {
			JSONObject json = array.getJSONObject(i);
			Map<String, Object> enclosureMap = requestBody;
			enclosureMap = titleSubstring(enclosureMap, json);// 匹配结尾_数字结尾，做分集处理
			enclosureMap = jsonToMap(enclosureMap, json);// 格式转换
			enclosureMap.put("tableId", id);
			enclosureMap.put("belongMode", belongMode);// 行业标准
			enclosureMap.put("belongType", enclosureMap.get("articleType"));
			LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS_ENCLOSURE);
			baseService.save(enclosureMap);
		}
	}

	// 审核
	public void subimtaduit(Object workId, Object courseId, int belongSys, int belongMode,
			Map<String, Object> requestBody, IBaseService baseService) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("tableId", courseId);
		map.put("tableIds", courseId);
		map.put("id", courseId);
		map.put("belongMode", belongMode);
		map.put("belongType", SyhzUtil.setDate(requestBody.get("type")));
		map.put("belongSys", belongSys);
		map.put("workId", workId);
		map.put("currentAuditType", 2);
		map.put("remark", "通过");
		map.put("documentId", SyhzUtil.setDate(requestBody.get("documentId")));
		map.put("creationId", SyhzUtil.setDate(requestBody.get("creationId")));
		map.put("creationName", SyhzUtil.setDate(requestBody.get("creationName")));
		map.put("deptCode", SyhzUtil.setDate(requestBody.get("belongDepCode")));
		map.put("deptName", SyhzUtil.setDate(requestBody.get("belongDepName")));
		map.put("deptAreaCode", SyhzUtil.setDate(requestBody.get("areaCode")));
		map.put("title", SyhzUtil.setDate(requestBody.get("title")));
		Map<String, String> headers = new HashMap<String, String>();
		trainWorkorderExamineService.examineWorkFlowData(baseService, headers, map);
		Map<String, Object> sendMap = activeMq(map, baseService, 0);
		int sendFlag = SyhzUtil.setDateInt(sendMap.get("sendFlag"));
		if (sendFlag == 0) {
			snedMessgeService.sendMessage(sendMap, QueueConfig.KNOWLEDGE);
			snedMessgeService.sendMessage(sendMap, QueueConfig.TIMELYMESSAGE);
		}
		map.put("fractionUserId", SyhzUtil.setDate(requestBody.get("creationId")));
		map.put("fractionUserName", SyhzUtil.setDate(requestBody.get("creationName")));
		map.put("fractionDeptCode", SyhzUtil.setDate(requestBody.get("belongDepCode")));
		map.put("fractionAreaCode", SyhzUtil.setDate(requestBody.get("areaCode")));
		map.put("fractionDeptName", SyhzUtil.setDate(requestBody.get("belongDepName")));
		rule1(map, baseService);
		audit(map);
	}

	public void subimtaduitFail(Object workId, Object courseId, int belongSys, int belongMode,
			Map<String, Object> requestBody, IBaseService baseService) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("tableId", courseId);
		map.put("tableIds", courseId);
		map.put("id", courseId);
		map.put("belongMode", belongMode);
		map.put("belongType", SyhzUtil.setDate(requestBody.get("type")));
		map.put("belongSys", belongSys);
		map.put("workId", workId);
		map.put("currentAuditType", 0);
		map.put("remark", "提交");
		map.put("documentId", SyhzUtil.setDate(requestBody.get("documentId")));
		map.put("creationId", SyhzUtil.setDate(requestBody.get("creationId")));
		map.put("creationName", SyhzUtil.setDate(requestBody.get("creationName")));
		map.put("deptCode", SyhzUtil.setDate(requestBody.get("belongDepCode")));
		map.put("deptName", SyhzUtil.setDate(requestBody.get("belongDepName")));
		map.put("deptAreaCode", SyhzUtil.setDate(requestBody.get("areaCode")));
		Map<String, String> headers = new HashMap<String, String>();
		trainWorkorderExamineService.examineWorkFlowData(baseService, headers, map);
	}

	// es审核
	public void audit(Map<String, Object> requestBody) {
		int belongSys = SyhzUtil.setDateInt(requestBody.get("belongSys"));
		int belongMode = SyhzUtil.setDateInt(requestBody.get("belongMode"));
		int currentAuditType = SyhzUtil.setDateInt(requestBody.get("currentAuditType"));
		int deptAreaCode = SyhzUtil.setDateInt(requestBody.get("deptAreaCode"));
		String documentId = SyhzUtil.setDate(requestBody.get("documentId"));
		if (belongSys == 1 && currentAuditType == 2 && deptAreaCode == 610000) {
			String[] documentIds = documentId.split(",");
			for (int i = 0; i < documentIds.length; i++) {
				if (belongMode == 1) {
					esService.auidt("lawinfo", documentIds[i]);// 改变es审核状态
				} else if (belongMode == 2) {
					esService.auidt("industryinfo", documentIds[i]);
				} else if (belongMode == 3) {
					esService.auidt("standardinfo", documentIds[i]);
				} else if (belongMode == 4) {
					esService.auidt("caseinfo", documentIds[i]);
				}
			}
		}
	}

	// 增加积分
	public void rule1(Map<String, Object> requestBody, IBaseService baseService) throws Exception {
		int belongSys = SyhzUtil.setDateInt(requestBody.get("belongSys"));
		int belongMode = SyhzUtil.setDateInt(requestBody.get("belongMode"));
		int currentAuditType = SyhzUtil.setDateInt(requestBody.get("currentAuditType"));
		int deptAreaCode = SyhzUtil.setDateInt(requestBody.get("deptAreaCode"));
		String id = SyhzUtil.setDate(requestBody.get("tableIds"));
		if (currentAuditType == 2 && deptAreaCode == 610000) {
			String[] documentIds = id.split(",");
			for (int i = 0; i < documentIds.length; i++) {
				Map<String, Object> ruleMap = rule(requestBody, baseService);
				ruleMap.put("tableId", documentIds[i]);
				ISaveHandler saveHandler = SpringUtils.getBean("trainFractionSaveHandler", ISaveHandler.class);
				Object object = saveHandler.save(ruleMap);// 保存到数据库s
			}
		}
	}

	// 审核参数组装
	public Map<String, Object> audit(Map<String, Object> map, int belongSys, int belongMode) {
		Map<String, Object> repsponseMap = new HashMap<String, Object>();
		Map<String, Object> departInfo = (Map<String, Object>) map.get("departInfo");// 附件Josn传
		Map<String, Object> departMap = depart(departInfo);
		repsponseMap.put("belongSys", belongSys);
		repsponseMap.put("belongMode", belongMode);
		repsponseMap.put("belongType", SyhzUtil.setDate(map.get("type")));
		repsponseMap.put("tableId", SyhzUtil.setDate(map.get("crouseId")));
		repsponseMap.put("creationId", SyhzUtil.setDate(map.get("creationId")));
		repsponseMap.put("creationName", SyhzUtil.setDate(map.get("creationName")));
		repsponseMap.put("myDept", departMap.get("myDept"));
		repsponseMap.put("cityDept", departMap.get("cityDept"));
		repsponseMap.put("provinceDept", departMap.get("provinceDept"));
		repsponseMap.put("draft", SyhzUtil.setDateInt(map.get("draft")));
		return repsponseMap;
	}

	// 提交审核部门信息处理
	public Map<String, Object> depart(Map<String, Object> departMap) {
		String provinceDeptCode = SyhzUtil.setDate(departMap.get("provinceDeptCode"));
		String provinceDeptName = SyhzUtil.setDate(departMap.get("provinceDeptName"));
		String provinceDeptAreaCode = SyhzUtil.setDate(departMap.get("provinceDeptAreaCode"));
		String cityDeptCode = SyhzUtil.setDate(departMap.get("cityDeptCode"));
		String cityDeptName = SyhzUtil.setDate(departMap.get("cityDeptName"));
		String cityDeptAreaCode = SyhzUtil.setDate(departMap.get("cityDeptAreaCode"));
		String myDept = SyhzUtil.setDate(departMap.get("myDept"));
		String myDeptName = SyhzUtil.setDate(departMap.get("myDeptName"));
		String myDeptAreaCode = SyhzUtil.setDate(departMap.get("myDeptAreaCode"));
		Pattern pattern = Pattern.compile("^\\d*[1-9]0{2}$");
		Matcher matcher = pattern.matcher(myDeptAreaCode);
		Pattern pattern1 = Pattern.compile("^\\d*[1-9]0{4}$");
		Matcher matcher1 = pattern1.matcher(myDeptAreaCode);
		boolean flag1 = matcher.find();
		boolean flag2 = matcher1.find();
		if (!flag1 && !flag2) {// 本机构为区级
			return departMap(provinceDeptCode, provinceDeptName, provinceDeptAreaCode, cityDeptCode, cityDeptName,
					cityDeptAreaCode, myDept, myDeptName, myDeptAreaCode);
		} else if (flag1 && !flag2) {// 本机构为市级
			return departMap(cityDeptCode, cityDeptName, cityDeptAreaCode, myDept, myDeptName, myDeptAreaCode, myDept,
					myDeptName, myDeptAreaCode);
		} else if (flag2) {// 本机构为省级
			return departMap(myDept, myDeptName, myDeptAreaCode, "", "", "", myDept, myDeptName, myDeptAreaCode);
		}
		return departMap;
	}

	public Map<String, Object> departMap(String provinceDeptCode, String provinceDeptName, String provinceDeptAreaCode,
			String cityDeptCode, String cityDeptName, String cityDeptAreaCode, String myDept, String myDeptName,
			String myDeptAreaCode) {
		Map<String, Object> myDeptMap = new HashMap<String, Object>();
		myDeptMap.put("deptCode", myDept);
		myDeptMap.put("deptName", myDeptName);
		myDeptMap.put("deptAreaCode", myDeptAreaCode);
		Map<String, Object> cityDeptMap = new HashMap<String, Object>();
		cityDeptMap.put("deptCode", cityDeptCode);
		cityDeptMap.put("deptName", cityDeptName);
		cityDeptMap.put("deptAreaCode", cityDeptAreaCode);
		Map<String, Object> provinceDeptMap = new HashMap<String, Object>();
		provinceDeptMap.put("deptCode", provinceDeptCode);
		provinceDeptMap.put("deptName", provinceDeptName);
		provinceDeptMap.put("deptAreaCode", provinceDeptAreaCode);
		Map<String, Object> responseMap = new HashMap<String, Object>();
		responseMap.put("myDept", myDeptMap);
		responseMap.put("cityDept", cityDeptMap);
		responseMap.put("provinceDept", provinceDeptMap);
		return responseMap;
	}

	// 消息发送
	public Map<String, Object> activeMq(Map<String, Object> map, IBaseService baseService, int i) throws Exception {

		Map<String, Object> responseMap = new HashMap<String, Object>();
		int belongSys = SyhzUtil.setDateInt(map.get("belongSys"));
		int belongMode = SyhzUtil.setDateInt(map.get("belongMode"));
		int currentAuditType = SyhzUtil.setDateInt(map.get("currentAuditType"));
		String deptCode = SyhzUtil.setDate(map.get("deptCode"));
		String deptName = SyhzUtil.setDate(map.get("deptName"));
		if (currentAuditType == 3 || "610000530000".equals(deptCode)) {// 审核不通过或者省级审核发送消息
			if (belongSys == 1) {
				responseMap.put("title", "知识库资料审核结果");
				if (belongMode == 1) {
					responseMap.put("bussionTypeInfo", 302);
					responseMap.put("bussionTable", "1");// 302 法律法规、303行业标准、304规则制度、305案例指引、306培训资料
				}
				if (belongMode == 2) {
					responseMap.put("bussionTypeInfo", 303);
					responseMap.put("bussionTable", "2");

				}
				if (belongMode == 3) {
					responseMap.put("bussionTypeInfo", 304);
					responseMap.put("bussionTable", "3");

				}
				if (belongMode == 4) {
					responseMap.put("bussionTypeInfo", 305);
					responseMap.put("bussionTable", "4");

				}
			}
			if (belongSys == 2) {
				responseMap.put("title", "培训资料审核结果");
				responseMap.put("bussionTypeInfo", 306);
				responseMap.put("bussionTable", "5");
			}
			responseMap.put("bussionId", map.get("tableId"));
			responseMap.put("id", map.get("tableId"));
			LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS_TRAINUSERNAME);
			Map<String, Object> user = (Map<String, Object>) baseService.get(responseMap);
			String title = SyhzUtil.setDate(user.get("title"));
			int userId = SyhzUtil.setDateInt(user.get("userId"));
			responseMap.put("bussionType", 3);
			String con = "";
			if (currentAuditType == 2) {
				con = "你上传的资料《" + title + "》 已通过最终审核，详细信息查看审核记录";
			}
			if (currentAuditType == 3) {
				con = "资料《" + title + "》" + deptName + "未审核通过，请修改后重新提交";
				responseMap.put("acceptId", userId);
			}
			responseMap.put("content", con);
			responseMap.put("status", 0);
			responseMap.put("creator", map.get("creationId"));
			responseMap.put("deptCode", deptCode);
			responseMap.put("deptName", deptName);
			if (i == 0) {
				responseMap.put("acceptId", SyhzUtil.setDateInt(map.get("creationId")));
			}
			if (i == 1) {
				responseMap.put("acceptId", userId);
			}
			responseMap.put("sendFlag", 0);// 发送
			responseMap.put("category", 1);// 弹出信息
		} else {
			responseMap.put("sendFlag", 1);// 不发送
		}
		return responseMap;
	}

	// 根据标题排序
	public Map<String, Object> titleSubstring(Map<String, Object> enclosureMap, JSONObject json) {
		String enName = String.valueOf(json.get("enName"));
		String enType = String.valueOf(json.get("enType"));
		Pattern pattern = Pattern.compile("_\\d+$");
		Matcher matcher = pattern.matcher(enName);
		if (matcher.find() && !"0".equals(enType)) {
			int j = enName.indexOf(matcher.group());
			String title = enName.substring(0, j);
			enclosureMap.put("title", title);
			enclosureMap.put("order", matcher.group().substring(1, matcher.group().length()));// 排序
		} else {
			enclosureMap.put("title", enName);
		}
		return enclosureMap;
	}

	// 附件json转map
	public Map<String, Object> jsonToMap(Map<String, Object> enclosureMap, JSONObject json) {
		enclosureMap.put("enCode", json.get("enCode"));
		enclosureMap.put("enType", json.get("enType"));
		enclosureMap.put("enPathOld", json.get("enPathOld"));
		enclosureMap.put("enClass", json.get("enClass"));
		enclosureMap.put("enName", json.get("enName"));
		enclosureMap.put("enPath", json.get("enPath"));
		enclosureMap.put("enable", 0);
		enclosureMap.put("downloadNumber", 0);
		enclosureMap.put("viewNumber", 0);
		enclosureMap.put("auditStatus", 1);
		return enclosureMap;
	}

	// 附件参数校验
	public void validationJson(JSONArray array) {
		for (int i = 0; i < array.size(); i++) {
			JSONObject json = array.getJSONObject(i);
			Object enCode = json.get("enCode");
			ValidationUtils.notNull(enCode, "附件编码不能为空");
			Object enType = json.get("enType");
			ValidationUtils.notNull(enType, "附件类型不能为空");
			Object enClass = json.get("enClass");
			ValidationUtils.notNull(enClass, "附件后缀不能为空");
			Object enName = json.get("enName");
			ValidationUtils.notNull(enName, "附件名不能为空");
			Object enPath = json.get("enPath");
			ValidationUtils.notNull(enPath, "附件地址不能为空");
			Object enPathOld = json.get("enPathOld");
			ValidationUtils.notNull(enPathOld, "附件旧地址不能为空");
		}
	}

}
