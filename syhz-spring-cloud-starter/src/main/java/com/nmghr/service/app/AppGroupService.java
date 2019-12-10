package com.nmghr.service.app;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.common.exception.GlobalErrorException;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.util.ValidationUtils;
import com.nmghr.entity.operation.OperationResult;
import com.nmghr.entity.query.QueryResult;
import com.nmghr.util.SyhzUtil;
import com.nmghr.util.app.AppVerifyUtils;
import com.nmghr.util.app.Result;
import com.nmghr.vo.OperationRequestVo;
import com.nmghr.vo.QueryRequestVo;

/**
 * APP常用组
 * 
 * @author heijiantao
 * @date 2019年12月4日
 * @version 1.0
 */
@Service
public class AppGroupService {
	// 删除常用组
	public Object delGroup(OperationRequestVo operationRequestVo, Map<String, Object> requestBody,
			Map<String, Object> requestParam, IBaseService baseService) throws Exception {

		validateParam(requestParam, 2);
		// 判断是否被使用
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "BASEGROUPCHECKINUSE");
		Map<String, Object> check = new HashMap<>();
		String groupId = String.valueOf(requestParam.get("groupId"));
		String groupJson = '"' + "group" + '"' + ":" + groupId;
		check.put("groupJson", groupJson);
		List<Map<String, Object>> jsons = (List<Map<String, Object>>) baseService.list(check);
		if (jsons != null && jsons.size() > 0) {
			return Result.fail(operationRequestVo.getJsonrpc(), operationRequestVo.getId(), "2", "该常用组正在使用，不可删除");
		}
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "BASEGROUP");
		baseService.update(String.valueOf(requestParam.get("groupId")), requestParam);
		// 物理删除明细表
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "BASEGROUPDETAIL");
		Map<String, Object> param = new HashMap<>();
		param.put("groupId", String.valueOf(requestParam.get("groupId")));
		baseService.remove(param);

		OperationResult result = AppVerifyUtils.setOperatorReult(operationRequestVo);

		return Result.ok(operationRequestVo.getJsonrpc(), operationRequestVo.getId(), result);

	}

	// 添加常用组
	public Object saveGroup(OperationRequestVo operationRequestVo, Map<String, Object> requestBody,
			Map<String, Object> requestParam, IBaseService baseService) throws Exception {

		validate(requestParam);

		// 组名去重
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "BASEGROUPNAMECHECK");
		Map<String, Object> num = (Map<String, Object>) baseService.get(requestParam);
		if (num != null) {
			if (Integer.valueOf(String.valueOf(num.get("num"))) > 0)
				throw new GlobalErrorException("998001", "组名重复，请确认后重新输入！");
		}

		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "BASEGROUP");
		Integer id = (Integer) baseService.save(requestParam);

		String dept = SyhzUtil.setDate(requestParam.get("deptIds")).replace("[", "").replace("]", "");
		List<String> deptIds = Arrays.asList(dept.split(","));
		if (deptIds != null && deptIds.size() > 0) {
			for (String deptId : deptIds) {
				Map<String, Object> param = new HashMap<>();
				param.put("groupId", id);
				param.put("itemId", deptId);
				param.put("itemType", 2);
				// 存储明细表即组员信息
				LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "BASEGROUPDETAIL");
				baseService.save(param);
			}
		}
		OperationResult result = AppVerifyUtils.setOperatorReult(operationRequestVo);

		return Result.ok(operationRequestVo.getJsonrpc(), operationRequestVo.getId(), result);

	}

	// 修改常用组
	public Object updateGroup(OperationRequestVo operationRequestVo, Map<String, Object> requestBody,
			Map<String, Object> requestParam, IBaseService baseService) throws Exception {

		validate(requestParam);
		String id = SyhzUtil.setDate(requestParam.get("id"));
		// 组名去重
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "BASEGROUPNAMECHECK");
		Map<String, Object> nameMap = new HashMap<>();
		nameMap.put("groupId", id);
		nameMap.put("groupName", String.valueOf(requestParam.get("groupName")));
		Map<String, Object> num = (Map<String, Object>) baseService.get(nameMap);

		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "BASEGROUP");
		baseService.update(id, requestParam);
		// 更新组成员
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "BASEGROUPDETAIL");
		Map<String, Object> param = new HashMap<>();
		param.put("groupId", id);
		baseService.remove(param);

		String dept = SyhzUtil.setDate(requestParam.get("deptIds")).replace("[", "").replace("]", "");
		List<String> deptIds = Arrays.asList(dept.split(","));
		requestParam.put("deptIds", deptIds);

		if (deptIds != null && deptIds.size() > 0) {
			for (String deptId : deptIds) {
				Map<String, Object> itemParam = new HashMap<>();
				itemParam.put("groupId", id);
				itemParam.put("itemId", deptId);
				itemParam.put("itemType", 2);
				// 存储明细表即组员信息
				LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "BASEGROUPDETAIL");
				baseService.save(itemParam);
			}
		}

		OperationResult result = AppVerifyUtils.setOperatorReult(operationRequestVo);
		return Result.ok(operationRequestVo.getJsonrpc(), operationRequestVo.getId(), result);

	}

	// 查询常用组
	public Object getGroup(QueryRequestVo queryRequestVo, Map<String, Object> requestBody,
			Map<String, Object> requestParam, IBaseService baseService) throws Exception {

		// 查询
		validateParam(requestParam, 1);
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "BASEGROUPBYUSERANDDEPT");
		List<Map<String, Object>> groupList = (List<Map<String, Object>>) baseService.list(requestParam);
		if (groupList != null && groupList.size() > 0) {
			for (Map<String, Object> group : groupList) {
				Map<String, Object> idMap = new HashMap<>();
				idMap.put("groupId", Integer.valueOf(String.valueOf(group.get("groupId"))));
				LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "BASEGROUPDETAIL");
				List<Map<String, Object>> items = (List<Map<String, Object>>) baseService.list(idMap);
				// group.put("items", items);
				List<Integer> deptIds = new ArrayList<>();
				if (items != null && items.size() > 0) {
					for (Map<String, Object> item : items) {
						deptIds.add(Integer.valueOf(String.valueOf(item.get("itemId"))));
					}
				}
				group.put("detail", deptIds);

			}

		}
		QueryResult result = AppVerifyUtils.setQueryResult(queryRequestVo, groupList);
		return Result.ok(queryRequestVo.getJsonrpc(), queryRequestVo.getId(), result);

	}

	// 常用组详情
	public Object getGroupDetail(QueryRequestVo queryRequestVo, Map<String, Object> requestBody,
			Map<String, Object> requestParam, IBaseService baseService) throws Exception {
		validateParam(requestParam, 2);
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "BASEGROUPDETAIL");
		List<Map<String, Object>> items = (List<Map<String, Object>>) baseService.list(requestParam);
		QueryResult result = AppVerifyUtils.setQueryResult(queryRequestVo, items);
		return Result.ok(queryRequestVo.getJsonrpc(), queryRequestVo.getId(), result);
	}

	// 名字校验重复
	public Object checkRepeat(QueryRequestVo queryRequestVo, Map<String, Object> requestBody,
			Map<String, Object> requestParam, IBaseService baseService) throws Exception {
		validateParam(requestParam, 3);

		// 组名查重
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "BASEGROUPNAMECHECK");
		Map<String, Object> num = (Map<String, Object>) baseService.get(requestParam);

		List<Map<String, Object>> messageList = new ArrayList<Map<String, Object>>();
		if (num != null && !"0".equals(String.valueOf(num.get("num")))) {
			messageList.add(num);
		}
		QueryResult result = AppVerifyUtils.setQueryResult(queryRequestVo, messageList);
		return Result.ok(queryRequestVo.getJsonrpc(), queryRequestVo.getId(), result);

	}

	private void validateParam(Map<String, Object> requestBody, int type) {
		if (type == 1) {
			// 组列表和分页
			ValidationUtils.notNull(requestBody.get("creatorId"), "创建人Id不能为空!");
			ValidationUtils.notNull(requestBody.get("deptCode"), "创建人部门编号不能为空!");
		}
		if (type == 2) {
			// 详情
			ValidationUtils.notNull(requestBody.get("groupId"), "组Id不能为空!");
		}
		if (type == 3) {
			// 组名查重
			ValidationUtils.notNull(requestBody.get("groupName"), "组名不能为空!");
		}
	}

	private void validate(Map<String, Object> requestBody) {
		ValidationUtils.notNull(String.valueOf(requestBody.get("groupId")), "组Id不能为空");
		ValidationUtils.notNull(String.valueOf(requestBody.get("groupName")), "组名不能为空");
		ValidationUtils.notNull(String.valueOf(requestBody.get("deptIds")), "组成员不能为空");
	}

}
