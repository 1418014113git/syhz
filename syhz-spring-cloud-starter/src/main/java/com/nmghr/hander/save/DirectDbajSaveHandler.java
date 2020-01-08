package com.nmghr.hander.save;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractSaveHandler;
import com.nmghr.common.WorkOrder;
import com.nmghr.hander.dto.ApproveParam;
import com.nmghr.hander.save.examine.ExamineSaveHandler;
import com.nmghr.util.LogQueueThread;
import com.nmghr.util.NoticeQueueThread;
import com.nmghr.util.SyhzUtil;

@Service("directdbajSaveHandler")
public class DirectDbajSaveHandler extends AbstractSaveHandler {
	@Autowired
	private ExamineSaveHandler examineSaveHandler;

	private final String DBAJ = "DBAJ";
	private final String BUSINESSSIGN = "BUSINESSSIGN"; // 签收表
	private final String BUSINESSTABLE = "aj_supervise"; // 签收表
	private final String CASESUPERVISERECORD = "CASESUPERVISERECORD"; // 签收表

	public DirectDbajSaveHandler(IBaseService baseService) {
		super(baseService);
	}

	@Override
	@Transactional
	public Object save(Map<String, Object> requestBody) throws Exception {
		// 签收表
		// 添加数据
		int superviseLevel = SyhzUtil.setDateInt(requestBody.get("superviseLevel"));
		int status = SyhzUtil.setDateInt(requestBody.get("status"));

		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, DBAJ);
		Object obj = baseService.save(requestBody);
		requestBody.put("superviseId", obj);
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, CASESUPERVISERECORD);
		Object recordId = baseService.save(requestBody);
		if (status == 1) {
			if (superviseLevel == 1) {
				saveLog(recordId, requestBody, "申请案件督办");
				saveLog(recordId, requestBody, "案件督办-审核成功");
				createApprove(requestBody, recordId, true);
				updateCaseSupervise(String.valueOf(recordId), 3);
			} else {
				saveLog(recordId, requestBody, "申请案件督办");
				createApprove(requestBody, recordId, false);
			}
		}
		// saveBusinessSign(String.valueOf(obj), requestBody);
		return obj;
	}

	private void updateCaseSupervise(String id, int status) throws Exception {
		Map<String, Object> noticeMap = new HashMap<>();
		noticeMap.put("status", String.valueOf(status));
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "CASESUPERVISERECORDSTATUS");
		baseService.update(id, noticeMap);
	}

	// 添加审核
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

	/**
	 * 增加业务签收
	 * 
	 * @param id
	 * @param requestBody
	 * @throws Exception
	 */
	private void saveBusinessSign(String id, Map<String, Object> requestBody) throws Exception {
		List<Object> ids = new ArrayList<Object>();
		String depts = String.valueOf(requestBody.get("superviseDeptId"));
		depts = depts.replaceAll("\\[", "").replaceAll("\\]", "");
		String[] deptIds = depts.split(",");
		for (int i = 0; i < deptIds.length; i++) {
			Map<String, Object> bSign = new HashMap<String, Object>();
			bSign.put("signUserId", "");
			bSign.put("signTime", null);
			bSign.put("businessTable", BUSINESSTABLE);
			bSign.put("businessProperty", "id");
			bSign.put("businessValue", id);
			bSign.put("noticeOrgId", deptIds[i]);
			bSign.put("noticeRole_id", "-1");
			bSign.put("noticeTime", new Date());
			bSign.put("noticeUserId", "");
			bSign.put("qsStatus", "1");
			bSign.put("parentId", "");
			bSign.put("noticeLx", null);
			bSign.put("updateTime", new Date());
			bSign.put("updateUserId", "");
			bSign.put("businessType", "4");
			bSign.put("deadlineTime", new Date());
			bSign.put("status", "1");
			bSign.put("revokeReason", "");
			LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, BUSINESSSIGN);
			baseService.save(bSign);
			ids.add(deptIds[i]);
		}
		Map<String, Object> paras = new HashMap<String, Object>();
		paras.put("ids", ids);
		paras.put("bizId", id);
		paras.put("type", "SUPERVISE");
		NoticeQueueThread.add(paras);
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
