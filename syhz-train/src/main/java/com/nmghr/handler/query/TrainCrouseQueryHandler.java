package com.nmghr.handler.query;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractQueryHandler;
import com.nmghr.basic.core.util.ValidationUtils;
import com.nmghr.util.SyhzUtil;

/**
 * 在线课堂列表查询
 * 
 * @author heijiantao
 * @date 2019年9月19日
 * @version 1.0
 */
@Service("traincourselistQueryHandler")
public class TrainCrouseQueryHandler extends AbstractQueryHandler {
	private static final String EN_TYPE_0 = "0";
	private static final String EN_TYPE_1 = "1";
	private static final String EN_TYPE_2 = "2";
	private static String ALIAS_TRAINCOURSE = "TRAINCOURSE";// 课程查询
	private static String ALIAS_TRAINCOURSE_BYTITLE = "TRAINCOURSEBYTITLE";// 课程查询

	public TrainCrouseQueryHandler(IBaseService baseService) {
		super(baseService);
	}

	public Object list(Map<String, Object> requestBody) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		requestBody.put("limit", SyhzUtil.setDateInt(requestBody.get("pageSize")));// 显示数量
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS_TRAINCOURSE);

		requestBody.put("enType", EN_TYPE_0);// 文档
		crouseList(requestBody, map, "enType0");

		requestBody.put("enType", EN_TYPE_1);// 视频
		crouseList(requestBody, map, "enType1");

		requestBody.put("enType", EN_TYPE_2);// 音频
		crouseList(requestBody, map, "enType2");
		return map;
	}

	public void crouseList(Map<String, Object> requestBody, Map<String, Object> map, String saveMapKey)
			throws Exception {
		List<Map<String, Object>> mapList = (List<Map<String, Object>>) baseService.list(requestBody);
		map.put(saveMapKey, mapList);
	}

	// 课程详情查询
	public Object get(String id) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", id);
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS_TRAINCOURSE);
		Map<String, Object> course = (Map<String, Object>) baseService.get(id);
		if (course != null) {// 分集处理
			int count = SyhzUtil.setDateInt(course.get("count"));
			if (count > 1) {
				LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS_TRAINCOURSE_BYTITLE);// 课程查询
				List<Map<String, Object>> courseList = (List<Map<String, Object>>) baseService.list(course);
				course.put("courseList", courseList);
			}
		}
		return course;

	}

	public Object page(Map<String, Object> requestBody, int currentPage, int pageSize) throws Exception {
		validation(requestBody);
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS_TRAINCOURSE);// 课程查询
		return baseService.page(requestBody, currentPage, pageSize);

	}

	private void validation(Map<String, Object> requestBody) {
		Object enType = requestBody.get("enType");
		ValidationUtils.notNull(enType, "搜索类型不能为空");
	}
}
