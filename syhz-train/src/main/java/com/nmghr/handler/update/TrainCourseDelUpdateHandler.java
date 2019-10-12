package com.nmghr.handler.update;

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

	private static String ALIAS_TRAINCOURSEDELETE = "TRAINCOURSEDELETE";
	private static String ALIAS_WORKORDERENABLE = "WORKORDERENABLE";

	@Override
	@Transactional
	public Object update(String id, Map<String, Object> requestBody) throws Exception {
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS_TRAINCOURSEDELETE);
		baseService.update(id, requestBody);
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS_WORKORDERENABLE);
		requestBody.put("belongSys", 2);
		requestBody.put("type", 1);
		return baseService.update(id, requestBody);
	}
}
