package com.nmghr.hander.update.casesupervise;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractUpdateHandler;
import com.nmghr.common.WorkOrder;
import com.nmghr.hander.dto.ApproveParam;
import com.nmghr.hander.save.examine.ExamineSaveHandler;
import com.nmghr.hander.update.examine.ExamineUpdateHandler;
import com.nmghr.util.LogQueueThread;
import com.nmghr.util.SyhzUtil;

@Service("CaseSuperviseReportExamineUpdateHandler")
public class CaseSuperviseReportExamineUpdateHandler extends AbstractUpdateHandler {
	public CaseSuperviseReportExamineUpdateHandler(IBaseService baseService) {
		super(baseService);
		// TODO Auto-generated constructor stub
	}

	@Autowired
	private ExamineUpdateHandler examineUpdateHandler;
	@Autowired
	private ExamineSaveHandler examineSaveHandler;

	@Override
	@Transactional
	public Object update(String id, Map<String, Object> body) throws Exception {
		String dbId = String.valueOf(body.get("dbId"));

		String status = String.valueOf(body.get("status"));
		if ("3".equals(status)) {
			saveLog(dbId, body, "结案报告-审核通过");
			updateCaseSupervise(id, 3); // 审核通过
		}
		if ("4".equals(status)) {
			saveLog(dbId, body, "结案报告-审核不通过");
			updateCaseSupervise(id, 4); // 审核不通过
		}
		if ("5".equals(status)) {
			body.put("status", 3);
			saveLog(dbId, body, "结案报告-审核通过");
			updateCaseSupervise(id, 3); // 审核通过
			createReport(body, id); // 审核通过并向上申请
		}
		// 处理审核信息
		return updateExamine(id, body);
	}

	// 修改结案报告状态
	private void updateCaseSupervise(String id, int status) throws Exception {
		Map<String, Object> noticeMap = new HashMap<>();
		noticeMap.put("ststus", String.valueOf(status));
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "CASESUPERVISEREPORTSTATUS");
		baseService.update(id, noticeMap);
	}

	// 修改工单状态
	private Object updateExamine(String id, Map<String, Object> body) throws Exception {
		Map<String, Object> noticeMap = new HashMap<>();
		noticeMap.put("title", "结案报告" + body.get("title"));
		noticeMap.put("status", body.get("status"));
		noticeMap.put("wdId", body.get("workId"));
		noticeMap.put("userId", body.get("userId"));
		noticeMap.put("userName", body.get("userName"));
		noticeMap.put("creatorId", body.get("applyUserId"));
		noticeMap.put("creatorName", body.get("applyUserName"));
		noticeMap.put("acceptDeptId", body.get("acceptDeptId"));
		noticeMap.put("acceptDeptName", body.get("auditDepartName"));
		noticeMap.put("curDeptCode", body.get("departCode"));
		noticeMap.put("curDeptName", body.get("departName"));
		noticeMap.put("content", body.get("content"));
		return examineUpdateHandler.update(String.valueOf(body.get("flowId")), noticeMap);
	}

	// 添加结案报告
	private Object createReport(Map<String, Object> params, String id) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("departCode", params.get("departCode"));
		map.put("recordId", params.get("dbId"));
		map.put("id", id);
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "CASESUPERVISEREPORT");
		Map<String, Object> reportMap = (Map<String, Object>) baseService.get(map);//
		String createDeptCode = SyhzUtil.setDate(params.get("createDeptCode"));
		String dbId = SyhzUtil.setDate(reportMap.get("dbId"));
		reportMap.put("content", params.get("content"));
		reportMap.put("createUser", params.get("userId"));
		reportMap.put("createUserName", params.get("userName"));
		reportMap.put("createDept", params.get("auditDepartId"));
		reportMap.put("createDeptName", params.get("auditDepartName"));
		reportMap.put("createDeptCode", params.get("departCode"));
		reportMap.put("examineDeptId", params.get("UpDepartId"));
		reportMap.put("examineDeptName", params.get("UpDepartName"));
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "DBAJREPORT");

		if ("610000530000".equals(createDeptCode)) {
			saveLog(dbId, params, "上报结案报告");
			saveLog(dbId, params, "结案报告-审核成功");
			reportMap.put("status", 3);
			Object reportId = baseService.save(reportMap);
			return createApprove(params, reportId, true);
		} else {
			saveLog(dbId, params, "上报结案报告");
			reportMap.put("status", 1);
			Object reportId = baseService.save(reportMap);
			return createApprove(params, reportId, false);
		}
	}

	// 添加审核
	private Object createApprove(Map<String, Object> params, Object clusterId, Boolean checkFlag) {
		ApproveParam approve = new ApproveParam();
		approve.setWdType(WorkOrder.supervisionReport.getType());
		approve.setWdStatus(checkFlag ? 3 : 1);
		approve.setUserId(params.get("userId"));
		approve.setUserName(params.get("userName"));
		approve.setCurDeptId(params.get("auditDepartId"));
		approve.setCurDeptName(params.get("auditDepartName"));
		approve.setWdTable(WorkOrder.supervisionReport.getTable());
		approve.setWdValue(clusterId);
		approve.setAcceptDept(params.get("UpDepartId"));
		approve.setAcceptDeptName(params.get("UpDepartName"));
		approve.setAcceptedUser("");
		approve.setWfStatus(checkFlag ? 3 : 1);
		return examineSaveHandler.createApprove(approve, checkFlag);
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
