package com.nmghr.handler.update;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractUpdateHandler;

@Service("lawInfoUpdateHandler")
public class LawInfoUpdateHandler extends AbstractUpdateHandler {

	private static String ALIAS_LAWINFO = "TRAINLAWINFO";// 法律法规

	public LawInfoUpdateHandler(IBaseService baseService) {
		super(baseService);
	}

	@Override
	@Transactional
	public Object update(String id, Map<String, Object> requestBody) throws Exception {
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS_LAWINFO);
		return baseService.update(id, requestBody);
	}
}
