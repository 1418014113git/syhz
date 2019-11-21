package com.nmghr.hander.update.casesupervise;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.common.Result;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractUpdateHandler;
import com.nmghr.util.LogQueueThread;
import com.nmghr.util.SyhzUtil;

@Service("dbcbacceptUpdateHandler")
public class CaseSuperviseUrgentUpdateHandler extends AbstractUpdateHandler {

	public CaseSuperviseUrgentUpdateHandler(IBaseService baseService) {
		super(baseService);
		// TODO Auto-generated constructor stub
	}

	@Override
	@Transactional
	public Object update(String id, Map<String, Object> requestBody) throws Exception {
		String type = SyhzUtil.setDate(requestBody.get("type"));
		String dbId = SyhzUtil.setDate(requestBody.get("dbId"));
		String cbId = SyhzUtil.setDate(requestBody.get("cbId"));
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "DBCBACCEPT");
		baseService.update(id, requestBody);
		if ("1".equals(type)) {
			LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "CASESUPERVISEURGENTSIGN");
			baseService.update(cbId, requestBody);
			saveLog(dbId, requestBody, "督办催办-已签收");
		}
		if ("2".equals(type)) {
			LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "dbcbaj");
			baseService.update(cbId, requestBody);
			saveLog(dbId, requestBody, "督办催办-已反馈");
		}

		return Result.ok("");
	}

	// 添加时间轴
	private void saveLog(Object id, Map<String, Object> map, String action) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("bizType", 3);
		params.put("action", action);
		params.put("bizId", id);
		params.put("userId", map.get("userId"));
		params.put("userName", map.get("userName"));
		LogQueueThread.add(params);
	}
}
