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

@Service("casesupervisegradeUpdateHandler")
public class CaseSuperviseGradeUpdateHandler extends AbstractUpdateHandler {

	public CaseSuperviseGradeUpdateHandler(IBaseService baseService) {
		super(baseService);
		// TODO Auto-generated constructor stub
	}

	@Override
	@Transactional
	public Object update(String id, Map<String, Object> requestBody) throws Exception {

		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "CASESUPERVISEGRADE");
		baseService.update(id, requestBody);
		saveLog(id, requestBody, "评价打分");
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
