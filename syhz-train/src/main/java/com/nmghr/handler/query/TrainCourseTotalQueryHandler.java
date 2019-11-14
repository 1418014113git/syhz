package com.nmghr.handler.query;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractQueryHandler;

@Service("traincoursetotalQueryHandler")

public class TrainCourseTotalQueryHandler extends AbstractQueryHandler {
	private static String ALIAS_TRAINCOURSEWAITADUIT = "TRAINCOURSEWAITADUITTOTAL";// 待审核

	public TrainCourseTotalQueryHandler(IBaseService baseService) {
		super(baseService);
		// TODO Auto-generated constructor stub
	}

	public Object list(Map<String, Object> requestBody) throws Exception {

			LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS_TRAINCOURSEWAITADUIT);
			return baseService.get(requestBody);

	}

}
