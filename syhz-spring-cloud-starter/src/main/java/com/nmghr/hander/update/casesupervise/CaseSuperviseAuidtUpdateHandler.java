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
import com.nmghr.util.LogQueueThread;
import com.nmghr.util.SyhzUtil;

@Service("CaseSuperviseAuidtUpdateHandler")
public class CaseSuperviseAuidtUpdateHandler extends AbstractUpdateHandler {
	@Autowired
	private ExamineSaveHandler examineSaveHandler;

	public CaseSuperviseAuidtUpdateHandler(IBaseService baseService) {
		super(baseService);
		// TODO Auto-generated constructor stub
	}

	@Override
	@Transactional
	public Object update(String id, Map<String, Object> requestBody) throws Exception {
		int superviseLevel = SyhzUtil.setDateInt(requestBody.get("superviseLevel"));
		int wdStatus = SyhzUtil.setDateInt(requestBody.get("wdStatus"));

		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "CASESUPERVISERECORD");
		Object recordId = "";
		if (superviseLevel == 1) {
			requestBody.put("status", 3);
			recordId = baseService.save(requestBody);
			saveLog(recordId, requestBody, "申请案件督办");
			saveLog(recordId, requestBody, "案件督办-审核成功");
			return createApprove(requestBody, recordId, true);
		} else {
			if (wdStatus == 0) {
				recordId = baseService.save(requestBody);
				saveLog(recordId, requestBody, "申请案件督办");
			}
			return createApprove(requestBody, recordId, false);
		}
	}

	private Object createApprove(Map<String, Object> params, Object clusterId, Boolean checkFlag) {
		ApproveParam approve = new ApproveParam();
		approve.setWdType(WorkOrder.caseSupervision.getType());
		approve.setWdStatus(checkFlag ? 3 : 1);
		approve.setUserId(params.get("applyPersonId"));
		approve.setUserName(params.get("applyPersonName"));
		approve.setCurDeptId(params.get("applyDeptId"));
		approve.setCurDeptName(params.get("applyDeptName"));
		approve.setWdTable(WorkOrder.caseSupervision.getTable());
		approve.setWdValue(clusterId);
		approve.setAcceptDept(params.get("superviseExamDeptId"));
		approve.setAcceptDeptName(params.get("superviseExamDept"));
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
		params.put("userId", map.get("applyPersonId"));
		params.put("userName", map.get("applyPersonName"));
		LogQueueThread.add(params);
	}
}