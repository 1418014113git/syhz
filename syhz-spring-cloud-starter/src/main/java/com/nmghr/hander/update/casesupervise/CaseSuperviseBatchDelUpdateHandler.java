package com.nmghr.hander.update.casesupervise;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractUpdateHandler;

@Service("casesupervisebatchdelUpdateHandler")
public class CaseSuperviseBatchDelUpdateHandler extends AbstractUpdateHandler {

	public CaseSuperviseBatchDelUpdateHandler(IBaseService baseService) {
		super(baseService);
		// TODO Auto-generated constructor stub
	}

	@Override
	@Transactional
	public Object update(String id, Map<String, Object> requestBody) throws Exception {
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "CASESUPERVISEBATCHDEL");
		baseService.update(id, requestBody);
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "CASESUPERVISEBATCHID");// 删除案件
		return baseService.update(id, requestBody);
	}
}
