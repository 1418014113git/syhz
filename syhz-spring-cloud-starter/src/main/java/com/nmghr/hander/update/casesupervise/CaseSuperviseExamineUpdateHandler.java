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
import com.nmghr.hander.update.examine.ExamineUpdateHandler;
import com.nmghr.util.LogQueueThread;

@Service("casesuperviseexamineUpdateHandler")
public class CaseSuperviseExamineUpdateHandler extends AbstractUpdateHandler {
	@Autowired
	private ExamineUpdateHandler examineUpdateHandler;

	public CaseSuperviseExamineUpdateHandler(IBaseService baseService) {
		super(baseService);
		// TODO Auto-generated constructor stub
	}

	@Override
	@Transactional
	public Object update(String id, Map<String, Object> body) throws Exception {

		String status = String.valueOf(body.get("status"));
		if ("3".equals(status)) {
			updateCaseSupervise(id, 3); // 审核通过
			saveLog(id, body, "案件督办-审核通过");
		}
		if ("4".equals(status)) {
			saveLog(id, body, "案件督办-审核不通过");
			updateCaseSupervise(id, 4); // 审核不通过
		}
		// 处理审核信息
		return updateExamine(id, body);
	}

	private void updateCaseSupervise(String id, int status) throws Exception {
		Map<String, Object> noticeMap = new HashMap<>();
		noticeMap.put("status", String.valueOf(status));
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "CASESUPERVISERECORDSTATUS");
		baseService.update(id, noticeMap);
	}

	private Object updateExamine(String id, Map<String, Object> body) throws Exception {
		Map<String, Object> noticeMap = new HashMap<>();
		noticeMap.put("title", "案件督办" + body.get("caseName"));
		noticeMap.put("status", body.get("status"));
		noticeMap.put("wdId", body.get("workId"));
		noticeMap.put("userId", body.get("userId"));
		noticeMap.put("userName", body.get("userName"));
		noticeMap.put("creatorId", body.get("applyUserId"));
		noticeMap.put("creatorName", body.get("applyUserName"));
		noticeMap.put("acceptDeptId", body.get("acceptDeptId"));
		noticeMap.put("acceptDeptName", body.get("acceptDeptName"));
		noticeMap.put("curDeptCode", body.get("departCode"));
		noticeMap.put("curDeptName", body.get("departName"));
		noticeMap.put("content", body.get("content"));
		return examineUpdateHandler.update(String.valueOf(body.get("flowId")), noticeMap);
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
