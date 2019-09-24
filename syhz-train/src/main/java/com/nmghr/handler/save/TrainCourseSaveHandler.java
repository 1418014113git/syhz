package com.nmghr.handler.save;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

/**
 * 培训资料保存
 * 
 * @author heijiantao
 * @date 2019年9月19日
 * @version 1.0
 */
@Service("traincourseSaveHandler")
public class TrainCourseSaveHandler extends AbstractSaveHandler {

	public TrainCourseSaveHandler(IBaseService baseService) {
		super(baseService);
	}

	private static String ALIAS_TRAINCOURSE = "TRAINCOURSE";// 课程查询


	@Transactional
	public Object save(Map<String, Object> requestBody) throws Exception {
		validation(requestBody);
		String enclosure = String.valueOf(requestBody.get("enclosure"));// 附件Josn传
		JSONArray array = JSONArray.parseArray(enclosure);
		validationJson(array);
		for (int i = 0; i < array.size(); i++) {
			JSONObject json = array.getJSONObject(i);
			Map<String, Object> enclosureMap = requestBody;
			titleSubstring(enclosureMap, json);// 匹配结尾_数字结尾，做分集处理
			jsonToMap(enclosureMap, json);// 格式转换
			LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS_TRAINCOURSE);
			baseService.save(enclosureMap);
		}
		return Result.ok("保存成功");
	}


	private void titleSubstring(Map<String, Object> enclosureMap, JSONObject json) {
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
	}

	private void jsonToMap(Map<String, Object> enclosureMap, JSONObject json) {
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
	}

	private void validationJson(JSONArray array) {
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
