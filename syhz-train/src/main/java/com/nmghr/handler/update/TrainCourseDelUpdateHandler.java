package com.nmghr.handler.update;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractUpdateHandler;

@Service("traincoursedeleteUpdateHandler")
public class TrainCourseDelUpdateHandler extends AbstractUpdateHandler {

	public TrainCourseDelUpdateHandler(IBaseService baseService) {
		super(baseService);
		// TODO Auto-generated constructor stub
	}

	private static String ALIAS_TRAINCOUREENC = "TRAINCOUREENC";
	private static String ALIAS_TRAINCOURSEDELETE = "TRAINCOURSEDELETE";
	private static String ALIAS_TRAINWOKORDER = "TRAINWOKORDER";
	private static String ALIAS_TRAINWORKORDERFLOW = "TRAINWORKORDERFLOW";

	@Override
	@Transactional
	public Object update(String id, Map<String, Object> requestBody) throws Exception {
		requestBody.put("id", id);
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS_TRAINCOUREENC);
		List<Map<String, Object>> map = (List<Map<String, Object>>) baseService.list(requestBody);
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS_TRAINCOURSEDELETE);
		baseService.remove(id);
		requestBody.put("type", 5);
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS_TRAINWORKORDERFLOW);// 查附件
		baseService.remove(requestBody);// 工单流表
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS_TRAINWOKORDER);// 查附件
		baseService.remove(requestBody);// 工单流表
		return map;
	}
}
