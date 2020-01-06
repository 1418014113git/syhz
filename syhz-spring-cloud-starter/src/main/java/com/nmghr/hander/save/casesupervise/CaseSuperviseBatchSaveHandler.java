package com.nmghr.hander.save.casesupervise;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractSaveHandler;
import com.nmghr.basic.core.util.ValidationUtils;
import com.nmghr.util.SyhzUtil;

@Service("casesupervisebatchSaveHandler")
public class CaseSuperviseBatchSaveHandler extends AbstractSaveHandler {

	public CaseSuperviseBatchSaveHandler(IBaseService baseService) {
		super(baseService);
		// TODO Auto-generated constructor stub
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public Object save(Map<String, Object> requestBody) throws Exception {
		validate(requestBody);
		String caseIds = SyhzUtil.setDate(requestBody.get("caseIds"));
		String[] workId = caseIds.split(",");
		int status = SyhzUtil.setDateInt(requestBody.get("status"));
		requestBody.put("ajNumber", workId.length);
		Map<String, Object> map = new HashMap<String, Object>();
		String endDate = SyhzUtil.setDate(requestBody.get("endDate")) + " 23:59:59";
		requestBody.put("endDate", endDate);
		if (status == 1) {
			map.put("startDate", requestBody.get("startDate"));
			map.put("endDate", endDate);
			requestBody.put("publishPersonId", 1);
			requestBody.put("publishPersonName", 1);
			requestBody.put("publishDate", 1);
		}
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "CASESUPERVISEBATCH");// 添加批次
		Object id = baseService.save(requestBody);

		map.put("batchId", id);
		for (int i = 0; i < workId.length; i++) {
			map.put("caseOrder", i + 1);
			LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "CASESUPERVISERECORDBATCH");// 添加督办案件
			baseService.update(workId[i], map);
		}
		return requestBody;
	}

	private void validate(Map<String, Object> requestBody) {
		ValidationUtils.notNull(String.valueOf(requestBody.get("title")), "标题不能为空");
		ValidationUtils.notNull(String.valueOf(requestBody.get("referenceNumber")), "文号不能为空");
		ValidationUtils.notNull(String.valueOf(requestBody.get("startDate")), "开始日期不能为空");
		ValidationUtils.notNull(String.valueOf(requestBody.get("endDate")), "结束日期不能为空");
		ValidationUtils.notNull(String.valueOf(requestBody.get("superviseLevel")), "督办级别不能为空");
		ValidationUtils.notNull(String.valueOf(requestBody.get("remindDate")), "提醒时限不能为空");
		ValidationUtils.notNull(String.valueOf(requestBody.get("content")), "正文不能为空");
		ValidationUtils.notNull(String.valueOf(requestBody.get("caseIds")), "案件不能为空");
	}

}
