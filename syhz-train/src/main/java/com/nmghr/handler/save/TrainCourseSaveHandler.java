package com.nmghr.handler.save;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.nmghr.basic.common.Constant;
import com.nmghr.basic.common.Result;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractSaveHandler;
import com.nmghr.basic.core.util.ValidationUtils;
import com.nmghr.handler.service.EnclosureAuditService;
import com.nmghr.handler.service.TrainWorkorderExamineService;
import com.nmghr.handler.service.TrainWorkorderService;
import com.nmghr.util.SyhzUtil;

/**
 * 培训资料保存
 * 
 * @author heijiantao
 * @date 2019年9月19日
 * @version 1.0
 */
@Service("traincourseSaveHandler")
public class TrainCourseSaveHandler extends AbstractSaveHandler {
	@Autowired
	private TrainWorkorderService TrainWorkorderService;
	@Autowired
	private EnclosureAuditService EnclosureAuditService;
	@Autowired
	TrainWorkorderExamineService trainWorkorderExamineService;
	private static String ALIAS_TRAINCOURSE = "TRAINCOURSE";// 课程查询

	public TrainCourseSaveHandler(IBaseService baseService) {
		super(baseService);
	}

	@Transactional
	public Object save(Map<String, Object> requestBody) throws Exception {
		validation(requestBody);
		String enclosure = String.valueOf(requestBody.get("enclosure"));// 附件Josn传
		int adminFlag = SyhzUtil.setDateInt(requestBody.get("adminFlag"));
		JSONArray array = JSONArray.parseArray(enclosure);
		EnclosureAuditService.validationJson(array);
		for (int i = 0; i < array.size(); i++) {
			JSONObject json = array.getJSONObject(i);
			Map<String, Object> enclosureMap = requestBody;
			enclosureMap = EnclosureAuditService.titleSubstring(enclosureMap, json);// 匹配结尾_数字结尾，做分集处理
			enclosureMap = EnclosureAuditService.jsonToMap(enclosureMap, json);// 格式转换
			LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS_TRAINCOURSE);
			Object crouseId = baseService.save(enclosureMap);
			requestBody.put("crouseId", crouseId);
			Map<String, String> header = new HashMap<String, String>();
			Map<String, Object> auditMap = EnclosureAuditService.audit(requestBody, 2, 1);
			Object workId = TrainWorkorderService.createWorkflowData(baseService, header, auditMap);// 添加审批记录
			if (adminFlag == 0) {// 管理员默认审核通过
				EnclosureAuditService.subimtaduit(workId, crouseId, 2, 1, requestBody, baseService);
			}
		}

		return Result.ok("保存成功");
	}

	private void validation(Map<String, Object> requestBody) {
		Object type = requestBody.get("type");
		ValidationUtils.notNull(type, "课程类型不能为空");
		Object enclosure = requestBody.get("enclosure");
		ValidationUtils.notNull(enclosure, "附件不能为空");
		Object belongOrgId = requestBody.get("belongDepCode");
		ValidationUtils.notNull(belongOrgId, "所属机构Code不能为空");
		Object belongDepName = requestBody.get("belongDepName");
		ValidationUtils.notNull(belongDepName, "所属机构名称不能为空");
		Object areaCode = requestBody.get("areaCode");
		ValidationUtils.notNull(areaCode, "所属区域不能为空");
		Object creationId = requestBody.get("creationId");
		ValidationUtils.notNull(creationId, "上传人不能为空");
		Object creationName = requestBody.get("creationName");
		ValidationUtils.notNull(creationName, "上传人姓名不能为空");
	}

}
