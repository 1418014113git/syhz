package com.nmghr.handler.query;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractQueryHandler;

@Service("sysMessagesPageQueryHandler")
public class SysMessagesPageQueryHandler extends AbstractQueryHandler{

	private static String ALIAS_SYSMESSAGESPAGE = "SYSMESSAGESPAGE";// 消息中心
	
	public SysMessagesPageQueryHandler(IBaseService baseService) {
		super(baseService);
	}
	
	public Object page(Map<String, Object> requestBody, int currentPage, int pageSize) throws Exception {
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS_SYSMESSAGESPAGE);
		return baseService.page(requestBody, currentPage, pageSize);
	}

}
