package com.nmghr.service.app;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.page.Paging;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.entity.operation.OperationResult;
import com.nmghr.entity.operation.OperationResultData;
import com.nmghr.entity.query.Page;
import com.nmghr.entity.query.QueryResult;
import com.nmghr.util.app.AppVerifyUtils;
import com.nmghr.util.app.Result;
import com.nmghr.vo.OperationRequestVo;
import com.nmghr.vo.QueryRequestVo;

@Service
public class AppClueServise {

	public Object getClusList(QueryRequestVo queryRequestVo, Map<String, Object> requestBody,
			Map<String, Object> conditionMap, IBaseService baseService) throws Exception {
		String sign = "";
		String sourceId = AppVerifyUtils.getQuerySourceId(queryRequestVo);
		// 获取分页信息
		Page page = AppVerifyUtils.getQueryPage(queryRequestVo);
		int pageSize = page.getPageSize();
		int pageNo = page.getPageNo();

		// 查询
		if (ObjectUtils.isEmpty(conditionMap.get("dataStatus"))) {
			conditionMap.put("dataStatus", "1");
		}
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "APPQBXXXCLUE");
		Paging pageMap = (Paging) baseService.page(conditionMap, pageNo, pageSize);
		List<Map<String, Object>> messageList = (List<Map<String, Object>>) pageMap.getList();
		int total = (int) pageMap.getTotalCount();
		QueryResult result = AppVerifyUtils.setQueryPageResult(sign, pageNo, pageSize, total, sourceId, messageList);
		return Result.ok(queryRequestVo.getJsonrpc(), queryRequestVo.getId(), result);
	}

	public Object getClueDetail(QueryRequestVo queryRequestVo, Map<String, Object> requestBody,
			Map<String, Object> conditionMap, IBaseService baseService) throws Exception {
		String sign = "";
		String sourceId = AppVerifyUtils.getQuerySourceId(queryRequestVo);

		// 查询
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "QBXXCLUE");
		Map<String, Object> pageMap = (Map<String, Object>) baseService.get(conditionMap);
		List<Map<String, Object>> messageList = new ArrayList<Map<String, Object>>();
		if (pageMap != null) {
			messageList.add(pageMap);
		}
		QueryResult result = AppVerifyUtils.setQueryResult(sign, sourceId, messageList);
		return Result.ok(queryRequestVo.getJsonrpc(), queryRequestVo.getId(), result);

	}

	public Object ClueSave(OperationRequestVo operationRequestVo, Map<String, Object> requestBody,
			Map<String, Object> operationMap, IBaseService baseService) throws Exception {

		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "INTELL");
		baseService.save(operationMap);

		OperationResult result = new OperationResult();
		List<OperationResultData> operations = new ArrayList<OperationResultData>();
		OperationResultData operationResultData = new OperationResultData();
		operationResultData
				.setOperationId(operationRequestVo.getParams().getData().getOperations().get(0).getOperationId());
		operationResultData.setOperationCode("1");
		operations.add(operationResultData);
		result.setCode("1");
		result.setMsg("OK");
		result.setOperations(operations);
		return Result.ok(operationRequestVo.getJsonrpc(), operationRequestVo.getId(), result);

	}

}
