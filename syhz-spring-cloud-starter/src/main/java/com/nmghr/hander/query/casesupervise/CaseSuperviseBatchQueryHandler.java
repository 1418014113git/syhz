package com.nmghr.hander.query.casesupervise;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractQueryHandler;

@Service("casesupervisebatchQueryHandler")
public class CaseSuperviseBatchQueryHandler extends AbstractQueryHandler {

	public CaseSuperviseBatchQueryHandler(IBaseService baseService) {
		super(baseService);
		// TODO Auto-generated constructor stub
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object get(String id) throws Exception {
		// 查询工作单 传入部门判断是否正确
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "CASESUPERVISEBATCH");
		Map<String, Object> flow = (Map<String, Object>) baseService.get(id);
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "CASESUPERVISERECORDDETAIL");
		flow.put("batchId", id);
		List<Map<String, Object>> caseList = (List<Map<String, Object>>) baseService.list(flow);
		flow.put("caseList", caseList);
		return flow;
	}
}
