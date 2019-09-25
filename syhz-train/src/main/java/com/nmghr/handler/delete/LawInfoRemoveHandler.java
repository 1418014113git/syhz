package com.nmghr.handler.delete;

import org.springframework.stereotype.Service;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractRemoveHandler;

@Service("lawInfoRemoveHandler")
public class LawInfoRemoveHandler extends AbstractRemoveHandler {
	private static String ALIAS_LAWINFO = "TRAINLAWINFO";// 法律法规

	public LawInfoRemoveHandler(IBaseService baseService) {
		super(baseService);
		// TODO Auto-generated constructor stub
	}

	public void remove(String id) throws Exception {
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS_LAWINFO);
		baseService.remove(id);
	}

}
