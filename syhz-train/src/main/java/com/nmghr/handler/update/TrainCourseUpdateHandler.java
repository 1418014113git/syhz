package com.nmghr.handler.update;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractUpdateHandler;

@Service("traincourseupdateUpdateHandler")
public class TrainCourseUpdateHandler extends AbstractUpdateHandler {
	private static String ALIAS_TRAINCOURSE = "TRAINCOURSEUPDATE";// 法律法规

	public TrainCourseUpdateHandler(IBaseService baseService) {
		super(baseService);
	}

	@Override
	@Transactional
	public Object update(String id, Map<String, Object> requestBody) throws Exception {
		titleSubstring(requestBody);
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS_TRAINCOURSE);
		return baseService.update(id, requestBody);
	}

	private void titleSubstring(Map<String, Object> requestBody) {
		String enName = String.valueOf(requestBody.get("enName"));
		String enType = String.valueOf(requestBody.get("enType"));
		Pattern pattern = Pattern.compile("_\\d+$");
		Matcher matcher = pattern.matcher(enName);
		if (matcher.find() && !"0".equals(enType)) {
			int j = enName.indexOf(matcher.group());
			String title = enName.substring(0, j);
			requestBody.put("title", title);
			requestBody.put("order", matcher.group().substring(1, matcher.group().length()));// 排序
		} else {
			requestBody.put("title", enName);
		}
	}
}
