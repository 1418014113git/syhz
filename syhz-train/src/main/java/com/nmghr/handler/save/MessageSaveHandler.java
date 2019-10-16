package com.nmghr.handler.save;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractSaveHandler;

@Service("messagesSaveHandler")
public class MessageSaveHandler extends AbstractSaveHandler {

	private static final String ALIAS_TRAINCOURSE = "SYSMESSAGES";

	public MessageSaveHandler(IBaseService baseService) {
		super(baseService);
		// TODO Auto-generated constructor stub
	}

	public Object save(Map<String, Object> requestBody) throws Exception {
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS_TRAINCOURSE);
		return baseService.save(requestBody);
	}

}
