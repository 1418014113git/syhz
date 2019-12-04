package com.nmghr.service.app;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.page.Paging;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.util.ValidationUtils;
import com.nmghr.entity.operation.OperationResult;
import com.nmghr.entity.query.Page;
import com.nmghr.entity.query.QueryResult;
import com.nmghr.handler.message.QueueConfig;
import com.nmghr.handler.service.SendMessageService;
import com.nmghr.util.SyhzUtil;
import com.nmghr.util.app.AppVerifyUtils;
import com.nmghr.util.app.Result;
import com.nmghr.vo.OperationRequestVo;
import com.nmghr.vo.QueryRequestVo;

/**
 * APP站内消息
 * 
 * @author heijiantao
 * @date 2019年12月4日
 * @version 1.0
 */
@Service
public class AppMessageService {
	private static final String ALIAS_SYS_MESSAGES_PAGE = "sysMessagesPage";// 获取站内消息
	private static final String ALIAS_SYS_MESSAGES_DEL = "sysmessagedel";// 删除提醒消息
	private static final String ALIAS_SYS_MESSAGES_DETAIL = "sysmessagesdetail";// 获取提醒消息详情
	private static final String ALIAS_SYS_MESSAGES = "sysMessages";// 提醒消息统计
	private static final String ALIAS_PERSONMESSAGE = "PERSONMESSAGE";// 字典
	private static final String ALIAS_SYS_MESSAGES_STAUS = "SYSMESSAGESSTATUS";// 修改已读状态

	@Autowired
	private SendMessageService sendMessageService;

	// 获取站内消息
	public Object getMessageList(QueryRequestVo queryRequestVo, Map<String, Object> requestBody,
			Map<String, Object> conditionMap, IBaseService baseService) throws Exception {

		// 获取分页信息
		Page page = AppVerifyUtils.getQueryPage(queryRequestVo);
		int pageSize = page.getPageSize();
		int pageNo = page.getPageNo();
		// 查询
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS_SYS_MESSAGES_PAGE);
		Paging pageMap = (Paging) baseService.page(conditionMap, pageNo, pageSize);
		List<Map<String, Object>> messageList = (List<Map<String, Object>>) pageMap.getList();
		int total = (int) pageMap.getTotalCount();
		QueryResult result = AppVerifyUtils.setQueryPageResult(queryRequestVo, pageNo, pageSize, total, messageList);
		return Result.ok(queryRequestVo.getJsonrpc(), queryRequestVo.getId(), result);
	}

	// 获取站内消息数量
	public Object getMessagesList(QueryRequestVo queryRequestVo, Map<String, Object> requestBody,
			Map<String, Object> conditionMap, IBaseService baseService) throws Exception {

		// 获取分页信息
		Page page = AppVerifyUtils.getQueryPage(queryRequestVo);
		int pageSize = page.getPageSize();
		int pageNo = page.getPageNo();
		// 查询
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS_SYS_MESSAGES);
		List<Map<String, Object>> list = (List<Map<String, Object>>) baseService.list(conditionMap);
		List<Map<String, Object>> list0 = new ArrayList<Map<String, Object>>();

		List<Map<String, Object>> list1 = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> bussionType1 = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> bussionType2 = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> bussionType3 = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> bussionType4 = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < list.size(); i++) {
			if ("0".equals(String.valueOf(list.get(i).get("status")))) {
				list0.add(list.get(i));
			} else if ("1".equals(String.valueOf(list.get(i).get("status")))) {
				list1.add(list.get(i));
			}
		}

		for (int i = 0; i < list.size(); i++) {
			if ("1".equals(String.valueOf(list.get(i).get("bussionType")))) {
				bussionType1.add(list.get(i));
			} else if ("2".equals(String.valueOf(list.get(i).get("bussionType")))) {
				bussionType2.add(list.get(i));
			} else if ("3".equals(String.valueOf(list.get(i).get("bussionType")))) {
				bussionType3.add(list.get(i));
			} else if ("4".equals(String.valueOf(list.get(i).get("bussionType")))) {
				bussionType4.add(list.get(i));
			}
		}

		Map<String, Object> map = new HashMap<String, Object>();
		//
		// // param.put("systemRank", new Paging(pageSize, pageNum, list0.size(),
		// list0));
		//
		// LocalThreadStorage.put(Constant.CONTROLLER_PAGE_TOTALCOUNT, list.size());
		map.put("list", list.size());
		map.put("list0", list0.size());
		map.put("list1", list1.size());
		map.put("bussionType1", bussionType1.size());
		map.put("bussionType2", bussionType2.size());
		map.put("bussionType3", bussionType3.size());
		map.put("bussionType4", bussionType4.size());
		List<Map<String, Object>> messageList = new ArrayList<Map<String, Object>>();
		messageList.add(map);

		QueryResult result = AppVerifyUtils.setQueryResult(queryRequestVo, messageList);
		return Result.ok(queryRequestVo.getJsonrpc(), queryRequestVo.getId(), result);

	}

	// 获取字典
	public Object getpersonMessage(QueryRequestVo queryRequestVo, Map<String, Object> requestBody,
			Map<String, Object> conditionMap, IBaseService baseService) throws Exception {

		// 查询
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS_PERSONMESSAGE);
		List<Map<String, Object>> messageList = (List<Map<String, Object>>) baseService.list(conditionMap);

		QueryResult result = AppVerifyUtils.setQueryResult(queryRequestVo, messageList);
		return Result.ok(queryRequestVo.getJsonrpc(), queryRequestVo.getId(), result);

	}

	// 消息删除
	public Object updateMessageInfo(OperationRequestVo operationRequestVo, Map<String, Object> requestBody,
			Map<String, Object> operationMap, IBaseService baseService) throws Exception {

		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS_SYS_MESSAGES_DEL);
		baseService.update(String.valueOf(operationMap.get("id")), operationMap);

		OperationResult result = AppVerifyUtils.setOperatorReult(operationRequestVo);

		return Result.ok(operationRequestVo.getJsonrpc(), operationRequestVo.getId(), result);

	}

	// 已读
	public Object updateMessageStaus(OperationRequestVo operationRequestVo, Map<String, Object> requestBody,
			Map<String, Object> operationMap, IBaseService baseService) throws Exception {
		String mesageId = SyhzUtil.setDate(operationMap.get("messagesId"));
		List<String> messagesId = Arrays.asList(mesageId.split(","));
		operationMap.put("messagesId", messagesId);
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS_SYS_MESSAGES_STAUS);
		baseService.update(String.valueOf(operationMap.get("id")), operationMap);

		OperationResult result = AppVerifyUtils.setOperatorReult(operationRequestVo);

		return Result.ok(operationRequestVo.getJsonrpc(), operationRequestVo.getId(), result);

	}

	// 获取站内消息详情
	public Object getMessageDetail(QueryRequestVo queryRequestVo, Map<String, Object> requestBody,
			Map<String, Object> conditionMap, IBaseService baseService) throws Exception {

		// 查询
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS_SYS_MESSAGES_DETAIL);
		Map<String, Object> pageMap = (Map<String, Object>) baseService.get(conditionMap);
		List<Map<String, Object>> messageList = new ArrayList<Map<String, Object>>();
		if (pageMap != null) {
			messageList.add(pageMap);
		}
		QueryResult result = AppVerifyUtils.setQueryResult(queryRequestVo, messageList);
		return Result.ok(queryRequestVo.getJsonrpc(), queryRequestVo.getId(), result);

	}

	// 获取站内消息详情
	public Object query(QueryRequestVo queryRequestVo, Map<String, Object> requestBody,
			Map<String, Object> conditionMap, IBaseService baseService) throws Exception {

		// 获取分页信息
		// 查询

		Page page = AppVerifyUtils.getQueryPage(queryRequestVo);
		int pageSize = page.getPageSize();
		int pageNo = page.getPageNo();
		// 查询
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "BASESYSMESSAGELIST");
		Paging pageMap = (Paging) baseService.page(conditionMap, pageNo, pageSize);
		List<Map<String, Object>> messageList = (List<Map<String, Object>>) pageMap.getList();
		int total = (int) pageMap.getTotalCount();
		QueryResult result = AppVerifyUtils.setQueryPageResult(queryRequestVo, pageNo, pageSize, total, messageList);
		return Result.ok(queryRequestVo.getJsonrpc(), queryRequestVo.getId(), result);
	}

	// 站内通知删除
	public Object delete(OperationRequestVo operationRequestVo, Map<String, Object> requestBody,
			Map<String, Object> operationMap, IBaseService baseService) throws Exception {
		String mesageId = SyhzUtil.setDate(operationMap.get("messagesId"));
		List<String> messagesId = Arrays.asList(mesageId.split(","));
		operationMap.put("messagesId", messagesId);
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "SYSMESSAGESDEL");
		baseService.update("", operationMap);

		OperationResult result = AppVerifyUtils.setOperatorReult(operationRequestVo);
		return Result.ok(operationRequestVo.getJsonrpc(), operationRequestVo.getId(), result);

	}

	// 站内通知发送
	public Object send(OperationRequestVo operationRequestVo, Map<String, Object> requestBody, Map<String, Object> body,
			IBaseService baseService) {
		ValidationUtils.notNull(body.get("userId"), "userId 不能为空!");
		ValidationUtils.notNull(body.get("userName"), "userName 不能为空!");
		ValidationUtils.notNull(body.get("curDeptId"), "curDeptId 不能为空!");
		ValidationUtils.notNull(body.get("curDeptName"), "curDeptName 不能为空!");
		ValidationUtils.notNull(body.get("curDeptCode"), "curDeptCode 不能为空!");
		ValidationUtils.notNull(body.get("title"), "title 不能为空!");
		ValidationUtils.notNull(body.get("content"), "content 不能为空!");
		String content = String.valueOf(body.get("content"));
		if (content.length() > 200) {
			return Result.fail("999667", "内容长度不能超过200字符");
		}
		ValidationUtils.notNull(body.get("recipient"), "recipient 不能为空!");

		JSONArray array = JSONArray.parseArray(String.valueOf(body.get("recipient")));
		List<Object> names = new ArrayList();
		for (int i = 0; i < array.size(); i++) {
			JSONObject json = array.getJSONObject(i);
			names.add(json.get("id") + "_" + json.get("name"));
			Map<String, Object> params = setMap(body.get("title"), body.get("content"), json.get("id"),
					json.get("name"), body.get("userId"), body.get("userName"), body.get("curDeptName"),
					body.get("curDeptCode"), null);
			sendMessageService.sendMessage(params, QueueConfig.SAVEMESSAGE);
			sendMessageService.sendMessage(params, QueueConfig.TIMELYMESSAGE);
		}
		Map<String, Object> params = setMap(body.get("title"), body.get("content"), "", names.get(0),
				body.get("userId"), body.get("userName"), body.get("curDeptCode"), body.get("curDeptName"),
				StringUtils.join(names.toArray(), ","));
		sendMessageService.sendMessage(params, QueueConfig.SAVEMESSAGE);
		OperationResult result = AppVerifyUtils.setOperatorReult(operationRequestVo);

		return Result.ok(operationRequestVo.getJsonrpc(), operationRequestVo.getId(), result);
	}

	private Map<String, Object> setMap(Object title, Object content, Object id, Object name, Object userId,
			Object userName, Object curDeptCode, Object curDeptName, Object remark) {
		Map<String, Object> params = new HashMap<>();
		params.put("bussionType", 4);
		params.put("bussionTypeInfo", 403);
		params.put("bussionId", -1);
		params.put("title", title);
		params.put("content", content);
		params.put("status", 0);
		params.put("acceptId", id);
		params.put("acceptName", name);
		params.put("creator", userId);
		params.put("creatorName", userName);
		params.put("deptCode", curDeptCode);
		params.put("deptName", curDeptName);
		params.put("category", 1);// 弹出信息
		if (remark != null) {
			params.put("remark", remark);// 弹出信息
		}
		return params;
	}

}
