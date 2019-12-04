package com.nmghr.service.app;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.nmghr.basic.common.Constant;
import com.nmghr.basic.common.exception.GlobalErrorException;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.page.Paging;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.ISaveHandler;
import com.nmghr.basic.core.util.SpringUtils;
import com.nmghr.basic.core.util.ValidationUtils;
import com.nmghr.entity.operation.OperationResult;
import com.nmghr.entity.query.Page;
import com.nmghr.entity.query.QueryResult;
import com.nmghr.handler.service.SendMessageService;
import com.nmghr.service.DeptNameService;
import com.nmghr.util.DateUtil;
import com.nmghr.util.SyhzUtil;
import com.nmghr.util.app.AppVerifyUtils;
import com.nmghr.util.app.Result;
import com.nmghr.vo.OperationRequestVo;
import com.nmghr.vo.QueryRequestVo;

/**
 * APP站内通知
 * 
 * @author heijiantao
 * @date 2019年12月4日
 * @version 1.0
 */
@Service
public class NoticeServise {

	@Autowired
	private SendMessageService sendMessageService;

	@Autowired
	@Qualifier("deptNameService")
	private DeptNameService deptNameService;

	// 获取站内通知
	public Object home(QueryRequestVo queryRequestVo, Map<String, Object> requestBody, Map<String, Object> conditionMap,
			IBaseService baseService) throws Exception {
		ValidationUtils.notNull(conditionMap.get("checkFlag"), "checkFlag不能为空!");
		ValidationUtils.notNull(conditionMap.get("userId"), "userId 不能为空!");
		ValidationUtils.notNull(conditionMap.get("curDeptId"), "curDeptId 不能为空!");
		Page page = AppVerifyUtils.getQueryPage(queryRequestVo);
		int pageSize = page.getPageSize();
		int pageNo = page.getPageNo();
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "BASEMESSAGELIST");
		Object obj = baseService.page(conditionMap, pageNo, pageSize);
		if (obj == null) {
			return Result.ok(new HashMap<>());
		}
		Paging pages = (Paging) obj;
		Map<String, Object> result = new HashMap<>();
		result.put("total", pages.getTotalCount());
		result.put("allData", pages.getList());
		if ("true".equals(String.valueOf(conditionMap.get("checkFlag")))) {
			conditionMap.put("signStatus", 1);
			LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "BASEMESSAGELIST");
			Object unObj = baseService.page(conditionMap, pageNo, pageSize);
			if (obj != null) {
				Paging unList = (Paging) unObj;
				result.put("unSignNum", unList.getTotalCount());
				result.put("unSignData", unList.getList());
			}
		}
		List<Map<String, Object>> messageList = new ArrayList<Map<String, Object>>();
		messageList.add(result);
		int total = (int) pages.getTotalCount();

		QueryResult results = AppVerifyUtils.setQueryPageResult(queryRequestVo, pageNo, pageSize, total, messageList);
		return Result.ok(queryRequestVo.getJsonrpc(), queryRequestVo.getId(), results);
	}

	// 获取站内列表
	public Object list(QueryRequestVo queryRequestVo, Map<String, Object> requestBody, Map<String, Object> conditionMap,
			IBaseService baseService) throws Exception {
		// 获取分页信息
		Page page = AppVerifyUtils.getQueryPage(queryRequestVo);
		int pageSize = page.getPageSize();
		int pageNo = page.getPageNo();
		// 查询
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "BASEMESSAGELIST");
		Paging pageMap = (Paging) baseService.page(conditionMap, pageNo, pageSize);
		List<Map<String, Object>> messageList = (List<Map<String, Object>>) pageMap.getList();

		int total = (int) pageMap.getTotalCount();
		QueryResult result = AppVerifyUtils.setQueryPageResult(queryRequestVo, pageNo, pageSize, total, messageList);
		return Result.ok(queryRequestVo.getJsonrpc(), queryRequestVo.getId(), result);
	}

	// 站内通知详情
	public Object detail(QueryRequestVo queryRequestVo, Map<String, Object> requestBody,
			Map<String, Object> conditionMap, IBaseService baseService) throws Exception {
		String id = SyhzUtil.setDate(conditionMap.get("id"));
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "BASEMESSAGE");
		Map<String, Object> result = (Map<String, Object>) baseService.get(id);
		if (result == null) {
			return Result.fail("999667", "通知不存在！");
		}
		// 查询是否有上一次信息
		if (result.get("parentId") != null && !"".equals(String.valueOf(result.get("parentId")).trim())) {
			getParentInfo(result, baseService);
		}
		if ("0".equals(String.valueOf(result.get("messageStatus")))) {
			// 为0 表示草稿没有签收信息
			return Result.ok(result);
		}
		// 查询签收信息
		Map<String, Object> params = new HashMap<>();
		params.put("messageId", id);
		params.put("receiverDeptId", conditionMap.get("curDeptId"));
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "BASEMESSAGESIGN");
		List<Map<String, Object>> signs = (List<Map<String, Object>>) baseService.list(params);
		if (signs != null && signs.size() > 0) {
			result.put("signInfo", tojson(signs.get(0)));
		}
		List<Map<String, Object>> messageList = new ArrayList<Map<String, Object>>();
		messageList.add(result);
		QueryResult results = AppVerifyUtils.setQueryResult(queryRequestVo, messageList);
		return Result.ok(queryRequestVo.getJsonrpc(), queryRequestVo.getId(), results);

	}

	// 名字校验重复
	public Object validName(QueryRequestVo queryRequestVo, Map<String, Object> requestBody, Map<String, Object> body,
			IBaseService baseService) throws Exception {
		ValidationUtils.notNull(body.get("userId"), "userId 不能为空!");
		ValidationUtils.notNull(body.get("title"), "title 不能为空!");
		Map<String, Object> params = new HashMap<>();
		params.put("userId", body.get("userId"));
		params.put("title", body.get("title"));
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "BASEMESSAGENAMECHECK");
		List<Map<String, Object>> messageList = (List<Map<String, Object>>) baseService.list(params);
		QueryResult result = AppVerifyUtils.setQueryResult(queryRequestVo, messageList);
		return Result.ok(queryRequestVo.getJsonrpc(), queryRequestVo.getId(), result);
	}

	// 站内通知审核记录
	public Object basemessageflowlsit(QueryRequestVo queryRequestVo, Map<String, Object> requestBody,
			Map<String, Object> conditionMap, IBaseService baseService) throws Exception {

		AppVerifyUtils.getQueryCondition(conditionMap, queryRequestVo);
		// 查询
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "basemessageflowlsit");
		List<Map<String, Object>> messageList = (List<Map<String, Object>>) baseService.list(conditionMap);
		QueryResult result = AppVerifyUtils.setQueryResult(queryRequestVo, messageList);
		return Result.ok(queryRequestVo.getJsonrpc(), queryRequestVo.getId(), result);

	}

	// 站内通知签收记录
	public Object basemessagesign(QueryRequestVo queryRequestVo, Map<String, Object> requestBody,
			Map<String, Object> conditionMap, IBaseService baseService) throws Exception {

		AppVerifyUtils.getQueryCondition(conditionMap, queryRequestVo);
		// 查询
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "basemessagesign");
		List<Map<String, Object>> messageList = (List<Map<String, Object>>) baseService.list(conditionMap);
		QueryResult result = AppVerifyUtils.setQueryResult(queryRequestVo, messageList);
		return Result.ok(queryRequestVo.getJsonrpc(), queryRequestVo.getId(), result);

	}

	// 站内通知签收
	public Object sign(OperationRequestVo operationRequestVo, Map<String, Object> requestBody, Map<String, Object> body,
			IBaseService baseService) throws Exception {
		ValidationUtils.notNull(body.get("id"), "id不能为空!");
		ValidationUtils.notNull(body.get("userId"), "userId不能为空!");
		ValidationUtils.notNull(body.get("userName"), "userName不能为空!");
		Map<String, Object> params = new HashMap<>();
		params.put("receiverId", body.get("userId"));
		params.put("receiverName", body.get("userName"));
		params.put("receiveTime", DateUtil.dateFormart(new Date(), DateUtil.yyyyMMddHHmmss));
		params.put("signStatus", body.get("signStatus"));
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "BASEMESSAGESIGN");
		baseService.update(String.valueOf(body.get("id")), params);

		OperationResult result = AppVerifyUtils.setOperatorReult(operationRequestVo);
		return Result.ok(operationRequestVo.getJsonrpc(), operationRequestVo.getId(), result);

	}

	// 站内通知添加
	public Object save(OperationRequestVo operationRequestVo, Map<String, Object> requestBody, Map<String, Object> body)
			throws Exception {

		validParams(body);

		if ("true".equals(String.valueOf(body.get("checkFlag")))
				&& "1".equals(String.valueOf(body.get("messageStatus")))) {
			body.put("depts", getDepts(String.valueOf(body.get("recipient"))));
		}
		ISaveHandler saveHandler = SpringUtils.getBean("noticeSubmitSaveHandler", ISaveHandler.class);
		Object obj = saveHandler.save(body);
		OperationResult result = AppVerifyUtils.setOperatorReult(operationRequestVo);
		return Result.ok(operationRequestVo.getJsonrpc(), operationRequestVo.getId(), result);

	}

	private List<Map<String, Object>> getDepts(String recipient) throws Exception {
		JSONArray groups = JSONArray.parseArray(recipient);
		Map<String, Object> deptIds = new HashMap<>();
		for (int i = 0; i < groups.size(); i++) {
			JSONObject obj = groups.getJSONObject(i);
			JSONArray array = obj.getJSONArray("list");
			for (int j = 0; j < array.size(); j++) {
				String id = array.getString(j);
				deptIds.put(id, id);
			}
		}
		return getDeptName(new ArrayList(deptIds.values()));
	}

	private List<Map<String, Object>> getDeptName(List<Object> ids) throws Exception {
		if (ids == null || ids.size() == 0) {
			throw new GlobalErrorException("999667", "接收人不能为空");
		}
		Map<String, Object> params = new HashMap<>();
		params.put("ids", ids);
		params.put("queryType", "deptName");
		Object obj = deptNameService.list(params);
		if (obj == null) {
			throw new GlobalErrorException("999667", "部门信息异常");
		}
		return (List<Map<String, Object>>) obj;
	}

	private void validId(Object id) {
		ValidationUtils.notNull(id, "id不能为空!");
		ValidationUtils.regexp(id, "^\\d+$", "非法输入");
	}

	private void validParams(Map<String, Object> body) {
		ValidationUtils.notNull(body.get("title"), "标题不能为空!");
		ValidationUtils.notNull(body.get("content"), "内容不能为空!");
		ValidationUtils.notNull(body.get("userId"), "userId不能为空!");
		ValidationUtils.notNull(body.get("userName"), "userName不能为空!");
		ValidationUtils.notNull(body.get("curDeptId"), "curDeptId不能为空!");
		ValidationUtils.notNull(body.get("curDeptName"), "curDeptName不能为空!");
		ValidationUtils.notNull(body.get("curDeptCode"), "curDeptCode不能为空!");
		ValidationUtils.notNull(body.get("messageStatus"), "messageStatus不能为空!");
		ValidationUtils.notNull(body.get("recipient"), "接收人不能为空!");
		JSONArray groups = JSONArray.parseArray(String.valueOf(body.get("recipient")));
		if (groups == null || groups.size() == 0) {
			throw new GlobalErrorException("999667", "接收人不能为空");
		}
	}

	private void getParentInfo(Map<String, Object> result, IBaseService baseService) throws Exception {
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "BASEMESSAGE");
		Map<String, Object> parent = (Map<String, Object>) baseService.get(String.valueOf(result.get("parentId")));
		Object deptName = result.get("creatorDeptName");
		Object userName = result.get("creatorName");
		Object time = result.get("createTime");
		result.put("forwardDeptName", deptName);
		result.put("forwardUserName", userName);
		result.put("forwardTime", time);
		result.put("creatorDeptName", parent.get("creatorDeptName"));
		result.put("creatorName", parent.get("creatorName"));
		result.put("createTime", parent.get("createTime"));
	}

	private String tojson(Map<String, Object> list) {
		if (list != null && list.size() > 0) {
			return JSON.toJSON(list).toString();

		} else {
			return "";
		}
	}
}
