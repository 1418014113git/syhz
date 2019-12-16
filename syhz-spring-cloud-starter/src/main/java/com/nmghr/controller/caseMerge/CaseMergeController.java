package com.nmghr.controller.caseMerge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.frameworkset.orm.annotation.Transaction;
import com.nmghr.basic.common.Constant;
import com.nmghr.basic.common.Result;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.util.ValidationUtils;
import com.nmghr.handler.message.QueueConfig;
import com.nmghr.handler.service.SendMessageService;
import com.nmghr.service.UserExtService;
import com.nmghr.util.SyhzUtil;

/**
 * 案件重复合并
 * 
 * @author heijiantao
 * @date 2019年12月11日
 * @version 1.0
 */
@RestController
@RequestMapping("/caseMerge")

public class CaseMergeController {

	@Autowired
	private IBaseService baseService;

	@Autowired
	private SendMessageService sendMessageService;

	@Autowired
	private UserExtService UserExtService;

	@GetMapping("/list")
	// 列表
	public Object list(@RequestParam Map<String, Object> requestParams) throws Exception {
		int pageNum = 1, pageSize = 15;
		if (requestParams.get("pageNum") != null && !StringUtils.isEmpty(requestParams.get("pageNum"))) {
			pageNum = Integer.parseInt(String.valueOf(requestParams.get("pageNum")));
		}
		if (requestParams.get("pageSize") != null && !StringUtils.isEmpty(requestParams.get("pageSize"))) {
			pageSize = Integer.parseInt(String.valueOf(requestParams.get("pageSize")));
		}
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "MERGE");
		return baseService.page(requestParams, pageNum, pageSize);
	}

	@GetMapping("/wait")
	// 代办列表
	public Object wait(@RequestParam Map<String, Object> requestParams) throws Exception {

		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "MERGEWAIT");
		return baseService.get(requestParams);
	}

	@GetMapping("/detail/{id}")
	// 详情
	public Object detail(@PathVariable String id) throws Exception {
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "MERGE");
		Map<String, Object> merge = (Map<String, Object>) baseService.get(id);
		String applyDeptName = SyhzUtil.setDate(merge.get("applyDeptName"));
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "MERGEDETAIL");
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", id);
		List<Map<String, Object>> mergeDetailList = (List<Map<String, Object>>) baseService.list(map);
		for (Map<String, Object> mergeDetail : mergeDetailList) {
			mergeDetail.put("noticeDeptName", applyDeptName);
		}
		merge.put("caseList", mergeDetailList);
		return merge;
	}

	@GetMapping("/logList")
	// 操作列表
	public Object logList(@RequestParam Map<String, Object> requestParams) throws Exception {
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJMERGELOG");
		return baseService.list(requestParams);
	}

	@GetMapping("/caseMergeList")
	// 案件档案详情合并列表
	public Object caseDetailMerge(@RequestParam Map<String, Object> requestParams) throws Exception {
		int pageNum = 1, pageSize = 15;
		if (requestParams.get("pageNum") != null && !StringUtils.isEmpty(requestParams.get("pageNum"))) {
			pageNum = Integer.parseInt(String.valueOf(requestParams.get("pageNum")));
		}
		if (requestParams.get("pageSize") != null && !StringUtils.isEmpty(requestParams.get("pageSize"))) {
			pageSize = Integer.parseInt(String.valueOf(requestParams.get("pageSize")));
		}

		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "CASEDETAILMERGE");
		return baseService.page(requestParams, pageNum, pageSize);
	}

	@PutMapping("/save")
	@ResponseBody
	@Transaction
	// 下发
	public Object save(@RequestBody Map<String, Object> requestParams) throws Exception {
		vaild(requestParams);
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJMERGE");
		Object mergeId = baseService.save(requestParams);// 保存合并记录
		saveDetail(mergeId, requestParams);
		int status = SyhzUtil.setDateInt(requestParams.get("status"));
		saveLog(mergeId.toString(), requestParams, status);
		sendMessage(requestParams, 2);// 发送督促消息
		return Result.ok("");
	}

	private void saveDetail(Object mergeId, Map<String, Object> requestParams) throws Exception {
		List<Map<String, Object>> caseList = (List<Map<String, Object>>) requestParams.get("caseList");
		for (Map<String, Object> caseInfo : caseList) {
			LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJMERGEDETAIL");
			caseInfo.put("mergeId", mergeId);
			baseService.save(caseInfo);// 保存合并明细
			String caseId = SyhzUtil.setDate(caseInfo.get("caseId"));
			int type = SyhzUtil.setDateInt(caseInfo.get("type"));
			if (type == 2) {
				LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJJBXXMERGESTATUS");
				baseService.update(caseId, requestParams);// 修改认领案件状态
			}
		}
	}

	@PostMapping("/update/{id}")
	@ResponseBody
	@Transaction
	// 编辑
	public Object update(@PathVariable String id, @RequestBody Map<String, Object> requestBody) throws Exception {
		vaild(requestBody);
		Map<String, Object> requestParam = new HashMap<String, Object>();
		requestParam.put("id", id);
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "MERGECASE");
		baseService.update(requestParam, requestBody);// 修改案件表显示状态
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "MERGEDETAIL");
		baseService.remove(requestParam);// 删除合并明细
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "MERGE");
		baseService.update(id, requestBody);// 修改主表
		saveDetail(id, requestBody);// 添加明细
		saveLog(id, requestBody, 4);
		return Result.ok("");
	}

	@PostMapping("/merge/{id}")
	@ResponseBody
	@Transaction
	// 合并
	public Object mergeCase(@PathVariable String id, @RequestBody Map<String, Object> requestBody) throws Exception {
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "MERGE");
		baseService.update(id, requestBody);// 修改主表
		saveLog(id, requestBody, SyhzUtil.setDateInt(requestBody.get("status")));// 添加操作记录
		sendMessage(requestBody, 1);// 发送消息
		return Result.ok("");
	}

	@PostMapping("/noMerge")
	@ResponseBody
	@Transaction
	// 合并
	public Object noMergeCase(@RequestBody Map<String, Object> requestBody) throws Exception {
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "CASEMERGEDETAIL");
		baseService.remove(requestBody);
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "MERGEDETAILCASE");
		baseService.update(requestBody, requestBody);
		setMap(requestBody);
		return Result.ok("");
	}

	private void setMap(Map<String, Object> requestBody) throws Exception {
		List<Map<String, Object>> caseList = new ArrayList<Map<String, Object>>();
		Map<String, Object> caseMap = new HashMap<String, Object>();
		caseMap.put("ajmc", requestBody.get("ajmc"));
		caseMap.put("ajbh", requestBody.get("ajbh"));
		caseMap.put("type", 1);
		caseMap.put("mergeId", requestBody.get("mergeId"));
		caseMap.put("caseId", requestBody.get("caseId"));
		caseList.add(caseMap);
		requestBody.put("caseList", caseList);
		requestBody.put("status", 3);
		saveLog(SyhzUtil.setDate(requestBody.get("mergeId")), requestBody, 5);// 添加操作记录
		sendMessage(requestBody, 3);// 发送消息
	}

	@PostMapping("/delete/{id}")
	@ResponseBody
	@Transaction
	// 删除
	public Object delete(@PathVariable String id) throws Exception {
		Map<String, Object> requestBody = new HashMap<String, Object>();
		requestBody.put("id", id);
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "MERGECASE");
		baseService.update(requestBody, requestBody);// 修改案件表显示状态
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "MERGE");
		baseService.remove(requestBody);// 删除合并主表
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "MERGEDETAIL");
		baseService.remove(requestBody);// 删除合并明细
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "MERGELOG");
		baseService.remove(requestBody);// 删除合并记录
		return Result.ok("");
	}

	private void saveLog(String id, Map<String, Object> body, int type) throws Exception {
		List<Map<String, Object>> caseList = (List<Map<String, Object>>) body.get("caseList");
		for (Map<String, Object> caseInfo : caseList) {
			int Mergetype = SyhzUtil.setDateInt(caseInfo.get("type"));
			if (Mergetype == 1) {
				body.put("ajmc", caseInfo.get("ajmc"));
				body.put("ajbh", caseInfo.get("ajbh"));
				body.put("mergeId", id);
				body.put("type", type);
				LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJMERGELOG");
				baseService.save(body);
				break;
			}
		}
	}

	private Object sendMessage(Map<String, Object> body, int type) throws Exception {
		String creationDeptCode = SyhzUtil.setDate(body.get("creationDeptCode"));
		String creationDeptName = SyhzUtil.setDate(body.get("creationDeptName"));

		String applyDeptCode = SyhzUtil.setDate(body.get("applyDeptCode"));
		String applyDeptName = SyhzUtil.setDate(body.get("applyDeptName"));
		int status = SyhzUtil.setDateInt(body.get("status"));
		StringBuilder title = new StringBuilder();
		StringBuilder content = new StringBuilder();
		if (type == 1) {// 发送合并消息
			title.append(applyDeptName).append("已完成重复案件的合并");
			if (status == 2) {
				content.append(applyDeptName).append("已完成了案件");
			}
			if (status == 3) {
				content.append(applyDeptName).append("对");
			}
		}
		if (type == 2) {// 发送督促合并消息
			title.append("重复案件合并工作提醒！");
			content.append(creationDeptName).append("给").append(applyDeptName).append("下发了案件");
		}
		if (type == 3) {// 发送取消合并消息
			title.append(applyDeptName).append("取消了重复案件的合并！");
			content.append(applyDeptName).append("取消了").append("案件");
		}

		List<Map<String, Object>> caseList = (List<Map<String, Object>>) body.get("caseList");
		for (int i = 0; i < caseList.size(); i++) {
			String ajmc = SyhzUtil.setDate(caseList.get(i).get("ajmc"));
			String ajbh = SyhzUtil.setDate(caseList.get(i).get("ajbh"));
			if (i == 0) {
				content.append(ajmc).append("(").append(ajbh).append(")");
			} else {
				content.append("、").append(ajmc).append("(").append(ajbh).append(")");
			}
		}
		if (type == 1) {// 发送合并消息

			if (status == 2) {
				content.append("的重复合并操作！");
			}
			if (status == 3) {
				content.append("的重复合并没有认可，请及时关注！");
			}
			Map<String, Object> sendMap = setMap(title, content, body.get("creationId"), body.get("creationName"),
					body.get("userId"), body.get("userName"), body.get("applyDeptCode"), body.get("applyDeptName"),
					body.get("id"));
			sendMessageService.sendMessage(sendMap, QueueConfig.SAVEMESSAGE);
			sendMessageService.sendMessage(sendMap, QueueConfig.TIMELYMESSAGE);
		}
		if (type == 2) {
			content.append("的重复合并指令，请督促尽快完成案件合并！");
		}
		if (type == 3) {
			content.append("的重复合并操作，请及时关注！");
		}
		String deptCode = getDeptCode(creationDeptCode, applyDeptCode);
		if (deptCode != null) {// 如果由总对给大队下发，给支队管理员发消息
			Map<String, Object> requestMap = new HashMap<String, Object>();
			requestMap.put("roleCode", "10001");
			requestMap.put("deptCode", deptCode);
			List<Map<String, Object>> mapList = (List<Map<String, Object>>) UserExtService.get(requestMap);
			if (mapList != null && mapList.size() > 0) {
				for (Map<String, Object> map : mapList) {
					body.put("creationId", map.get("creationId"));
					body.put("creationName", map.get("creationName"));
					Map<String, Object> sendMap = setMap(title, content, body.get("creationId"),
							body.get("creationName"), body.get("userId"), body.get("userName"),
							body.get("applyDeptCode"), body.get("applyDeptName"), body.get("id"));
					sendMessageService.sendMessage(sendMap, QueueConfig.SAVEMESSAGE);
					sendMessageService.sendMessage(sendMap, QueueConfig.TIMELYMESSAGE);
				}
			}
		}

		return baseService;
	}

	private String getDeptCode(String creationDeptCode, String applyDeptCode) {
		if ("610000530000".equals(creationDeptCode)) {
			if (!"00".equals(applyDeptCode.substring(4, 6))) {
				return applyDeptCode.substring(0, 4) + "00";
			}
		}
		return null;
	}

	private Map<String, Object> setMap(Object title, Object content, Object id, Object name, Object userId,
			Object userName, Object curDeptCode, Object curDeptName, Object tableId) {
		Map<String, Object> params = new HashMap<>();
		params.put("bussionType", 1);
		params.put("bussionTypeInfo", 105);
		params.put("bussionId", tableId);
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
		return params;
	}

	private void vaild(Map<String, Object> requestParams) {
		ValidationUtils.notNull(requestParams.get("caseList"), "请选择合并案件!");
		ValidationUtils.notNull(requestParams.get("applyDeptCode"), "请选择合并单位!");
		ValidationUtils.notNull(requestParams.get("userId"), "获取不到用户信息");
		ValidationUtils.notNull(requestParams.get("userName"), "获取不到用户信息");
	}

}
