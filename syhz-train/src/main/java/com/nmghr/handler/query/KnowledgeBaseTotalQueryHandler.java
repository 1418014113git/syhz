package com.nmghr.handler.query;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractQueryHandler;

@Service("KnowledgeBaseTotalQueryHandler")
public class KnowledgeBaseTotalQueryHandler extends AbstractQueryHandler {

	private static String ALIAS_KONWLEDGETOTAL = "KONWLEDGETOTAL";

	public KnowledgeBaseTotalQueryHandler(IBaseService baseService) {
		super(baseService);
		// TODO Auto-generated constructor stub
	}

	public Object list(Map<String, Object> requestBody) throws Exception {
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS_KONWLEDGETOTAL);
		Map<String, Object> reponseMap = new HashMap<String, Object>();
		for (int i = 1; i < 5; i++) {
			requestBody.put("type", i);
			Map<String, Object> map = (Map<String, Object>) baseService.get(requestBody);
			reponseMap.put("totalCount" + i, map.get("count"));
		}
		return reponseMap;
	}
}
