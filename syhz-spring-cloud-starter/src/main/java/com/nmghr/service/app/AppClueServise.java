package com.nmghr.service.app;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.page.Paging;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.entity.operation.OperationResult;
import com.nmghr.entity.query.Page;
import com.nmghr.entity.query.QueryResult;
import com.nmghr.util.app.AppVerifyUtils;
import com.nmghr.util.app.Result;
import com.nmghr.vo.OperationRequestVo;
import com.nmghr.vo.QueryRequestVo;

/**
 * APP线索
 * 
 * @author heijiantao
 * @date 2019年12月4日
 * @version 1.0
 */
@Service
public class AppClueServise {
	// 线索列表
	public Object getClusList(QueryRequestVo queryRequestVo, Map<String, Object> requestBody,
			Map<String, Object> conditionMap, IBaseService baseService) throws Exception {

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
		QueryResult result = AppVerifyUtils.setQueryPageResult(queryRequestVo, pageNo, pageSize, total, messageList);
		return Result.ok(queryRequestVo.getJsonrpc(), queryRequestVo.getId(), result);
	}

	// 线索详情
	public Object getClueDetail(QueryRequestVo queryRequestVo, Map<String, Object> requestBody,
			Map<String, Object> conditionMap, IBaseService baseService) throws Exception {

		// 查询
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "QBXXCLUE");
		Map<String, Object> pageMap = (Map<String, Object>) baseService.get(conditionMap);
		List<Map<String, Object>> messageList = new ArrayList<Map<String, Object>>();
		if (pageMap != null) {
			messageList.add(pageMap);
		}
		QueryResult result = AppVerifyUtils.setQueryResult(queryRequestVo, messageList);
		return Result.ok(queryRequestVo.getJsonrpc(), queryRequestVo.getId(), result);

	}

	// 添加线索
	public Object ClueSave(OperationRequestVo operationRequestVo, Map<String, Object> requestBody,
			Map<String, Object> body, IBaseService baseService) throws Exception {

		Date submitTime = new Date();
		body.put("submitTime", submitTime);
		// 生成线索编号，单位所在行政区划代码+提交时间年月日时分毫秒
		body.put("clueNumber", body.get("deptAreaCode") + new SimpleDateFormat("yyyyMMddHHmmssSSS").format(submitTime));
		body.put("dataStatus", 1); // 数据状态
		body.put("shareStatus", 0); // 0原报，1分享
		body.put("submitPersonNumber", body.get("submitPersonNumber").toString()); // 警号
		body.put("collectionLocation", body.get("collectionLocation").toString()); // 采集地点行政区划
		body.put("collectionLocationLable", body.get("collectionLocationLable").toString()); // 采集地点行政区划名称
		body.put("clueSortId", body.get("clueSortId").toString()); // 线索分类字典表主键
		body.put("collectionTypeId", body.get("collectionTypeId").toString()); // 采集类型字典表主键
		body.put("clueTime", StringUtils.isEmpty(body.get("clueTime").toString()) ? null
				: new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(String.valueOf(body.get("clueTime")))); // 发生时间
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "QBXXCLUE");
		baseService.save(body);
		OperationResult result = AppVerifyUtils.setOperatorReult(operationRequestVo);
		return Result.ok(operationRequestVo.getJsonrpc(), operationRequestVo.getId(), result);
	}

}
