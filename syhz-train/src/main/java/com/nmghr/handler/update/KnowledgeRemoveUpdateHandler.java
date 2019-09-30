package com.nmghr.handler.update;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractUpdateHandler;

@Service("KnowledgeRemoveUpdateHandler")
public class KnowledgeRemoveUpdateHandler extends AbstractUpdateHandler {
	private static String ALIAS_TRAINKNOWLEDGEBASE = "KNOWLEDGELOGREMOVE";

	public KnowledgeRemoveUpdateHandler(IBaseService baseService) {
		super(baseService);
		// TODO Auto-generated constructor stub
	}

	@Override
	@Transactional
	public Object update(String id, Map<String, Object> requestBody) throws Exception {
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS_TRAINKNOWLEDGEBASE);
		return baseService.update(id, requestBody);

	}
}
