package com.nmghr.hander.save.casesupervise;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractSaveHandler;
import com.nmghr.basic.core.util.ValidationUtils;
import com.nmghr.common.WorkOrder;
import com.nmghr.hander.dto.ApproveParam;
import com.nmghr.hander.save.examine.ExamineSaveHandler;
import com.nmghr.util.LogQueueThread;
import com.nmghr.util.SyhzUtil;

@Service("casesupervisereportSaveHandler")
public class CaseSuperviseReportSaveHandler extends AbstractSaveHandler {

	@Autowired
	private ExamineSaveHandler examineSaveHandler;

	public CaseSuperviseReportSaveHandler(IBaseService baseService) {
		super(baseService);
		// TODO Auto-generated constructor stub
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public Object save(Map<String, Object> requestBody) throws Exception {
		validate(requestBody);
		String createDeptCode = SyhzUtil.setDate(requestBody.get("createDeptCode"));
		String dbId = SyhzUtil.setDate(requestBody.get("dbId"));
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "DBAJREPORT");// 添加批次
		Object id = baseService.save(requestBody);
		if ("610000530000".equals(createDeptCode)) {
			saveLog(dbId, requestBody, "上报结案报告");
			saveLog(dbId, requestBody, "结案报告-审核成功");
			updateCaseSupervise(String.valueOf(id), 3);
			return createApprove(requestBody, id, true);
		} else {
			saveLog(dbId, requestBody, "上报结案报告");
			return createApprove(requestBody, id, false);
		}
	}

	private void updateCaseSupervise(String id, int status) throws Exception {
		Map<String, Object> noticeMap = new HashMap<>();
		noticeMap.put("ststus", String.valueOf(status));
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "CASESUPERVISEREPORTSTATUS");
		baseService.update(id, noticeMap);
	}

	// 添加审核
	private Object createApprove(Map<String, Object> params, Object clusterId, Boolean checkFlag) {
		ApproveParam approve = new ApproveParam();
		approve.setWdType(WorkOrder.supervisionReport.getType());
		approve.setWdStatus(checkFlag ? 3 : 1);
		approve.setUserId(params.get("createUser"));
		approve.setUserName(params.get("createUserName"));
		approve.setCurDeptId(params.get("createDept"));
		approve.setCurDeptName(params.get("createDeptName"));
		approve.setWdTable(WorkOrder.supervisionReport.getTable());
		approve.setWdValue(clusterId);
		approve.setAcceptDept(params.get("examineDeptId"));
		approve.setAcceptDeptName(params.get("examineDeptName"));
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
		params.put("userId", map.get("createUser"));
		params.put("userName", map.get("createUserName"));
		LogQueueThread.add(params);
	}

	private void validate(Map<String, Object> requestBody) {
		ValidationUtils.notNull(String.valueOf(requestBody.get("title")), "标题不能为空");
		ValidationUtils.notNull(String.valueOf(requestBody.get("content")), "结案报告不能为空");
		ValidationUtils.notNull(String.valueOf(requestBody.get("createUser")), "上传者不能为空");
	}

}
