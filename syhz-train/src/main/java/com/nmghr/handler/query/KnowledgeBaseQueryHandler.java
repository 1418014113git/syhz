package com.nmghr.handler.query;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractQueryHandler;

@Service("knowledgeBaseQueryHandler")
public class KnowledgeBaseQueryHandler extends AbstractQueryHandler {

	public KnowledgeBaseQueryHandler(IBaseService baseService) {
		super(baseService);
		// TODO Auto-generated constructor stub
	}

	private static String ALIAS_TRAINWAIT = "TRAINWAIT";// 待审核

	public Object page(Map<String, Object> requestBody, int currentPage, int pageSize) throws Exception {
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS_TRAINWAIT);
		return baseService.page(requestBody, currentPage, pageSize);
	}

}
