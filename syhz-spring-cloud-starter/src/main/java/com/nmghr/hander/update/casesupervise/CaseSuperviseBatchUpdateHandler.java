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
import com.nmghr.util.SyhzUtil;

@Service("casesupervisebatchUpdateHandler")
public class CaseSuperviseBatchUpdateHandler extends AbstractUpdateHandler {

	public CaseSuperviseBatchUpdateHandler(IBaseService baseService) {
		super(baseService);
		// TODO Auto-generated constructor stub
	}

	@Override
	@Transactional
	public Object update(String id, Map<String, Object> requestBody) throws Exception {
		String caseIds = SyhzUtil.setDate(requestBody.get("caseIds"));
		String[] workId = caseIds.split(",");
		int status = SyhzUtil.setDateInt(requestBody.get("status"));
		requestBody.put("ajNumber", workId.length);
		Map<String, Object> map = new HashMap<String, Object>();
		String endDate = SyhzUtil.setDate(requestBody.get("endDate")) + " 23:59:59";
		requestBody.put("endDate", endDate);
		if (status == 1) {
			map.put("startDate", SyhzUtil.setDate(requestBody.get("startDate")));
			map.put("endDate", endDate);
			requestBody.put("publishPersonId", 1);
			requestBody.put("publishPersonName", 1);
			requestBody.put("publishDate", 1);
		}
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "CASESUPERVISEBATCH");// 修改批次
		baseService.update(id, requestBody);
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "CASESUPERVISEBATCHID");// 删除案件
		baseService.update(id, requestBody);
		map.put("batchId", id);
		for (int i = 0; i < workId.length; i++) {
			map.put("caseOrder", i + 1);
			LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "CASESUPERVISERECORDBATCH");// 添加督办案件
			baseService.update(workId[i], map);
		}
		return Result.ok("");
	}
}
