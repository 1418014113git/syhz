package com.nmghr.handler.update;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.common.Result;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractUpdateHandler;

@Service("trainruleconfigUpdateHandler")
public class TrainRuleConfigUpdateHandler extends AbstractUpdateHandler {

	private static String ALIAS_TRAINRULECONFIG = "TRAINRULECONFIG";

	public TrainRuleConfigUpdateHandler(IBaseService baseService) {
		super(baseService);
		// TODO Auto-generated constructor stub
	}

	@Override
	@Transactional
	public Object update(String id, Map<String, Object> requestBody) throws Exception {
		List<Map<String, Object>> ruleList = (List<Map<String, Object>>) requestBody.get("ruleList");
		for (Map<String, Object> map : ruleList) {
			LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS_TRAINRULECONFIG);
			baseService.update(id, map);
		}
		return Result.ok("");
	}
}
