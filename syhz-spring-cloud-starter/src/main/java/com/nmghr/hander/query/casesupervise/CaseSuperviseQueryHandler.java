package com.nmghr.hander.query.casesupervise;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractQueryHandler;
import com.nmghr.util.SyhzUtil;

@Service("casesuperviseQueryHandler")
public class CaseSuperviseQueryHandler extends AbstractQueryHandler {

	public CaseSuperviseQueryHandler(IBaseService baseService) {
		super(baseService);
		// TODO Auto-generated constructor stub
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object list(Map<String, Object> requestBody) throws Exception {
		String id = SyhzUtil.setDate(requestBody.get("id"));
		Map<String, Object> responseMap = new HashMap<String, Object>();
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> thMap = new HashMap<String, Object>();

		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "CASESUPERVISE");
		Map<String, Object> jbxx = (Map<String, Object>) baseService.get(id);
		jbxx.put("dbId", id);
		map.put("jbxx", jbxx);// 基本信息

		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "CASESUPERVISEWAITAUDIT");
		Map<String, Object> shxx = (Map<String, Object>) baseService.get(id);
		map.put("shxx", shxx);// 审核信息

		Map<String, Object> pjdf = new HashMap<String, Object>();
		pjdf.put("grade", jbxx.get("grade"));
		pjdf.put("gradeContent", jbxx.get("gradeContent"));
		map.put("pjdf", pjdf);// 评价打分

		Map<String, Object> cbxx = new HashMap<String, Object>();
		cbxx.put("urgedPersonId", jbxx.get("supervisePersonId"));
		cbxx.put("urgedPersonName", jbxx.get("supervisePerson"));
		cbxx.put("urgedDeptId", jbxx.get("applyDepartCode"));
		cbxx.put("urgedDeptCode", jbxx.get("applyDepartCode"));
		cbxx.put("urgedDeptName", jbxx.get("applyDepartName"));
		map.put("cbxx", cbxx);// 催办信息

		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "CASESUPERVISEREPORTINFO");
		Map<String, Object> jabg = (Map<String, Object>) baseService.get(id);
		map.put("jabg", jabg);// 结案报告

		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "CASESUPERVISELOG");
		List<Map<String, Object>> zxjz = (List<Map<String, Object>>) baseService.list(requestBody);
		map.put("zxjz", zxjz);// 最新进展
		thMap.put("zxjz", zxjz.size());// 最新进展

		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "CASESUPERVISERECORDTOTAL");
		Map<String, Object> shxxTotal = (Map<String, Object>) baseService.get(id);
		thMap.put("shxx", shxxTotal.get("total"));// 审核信息

		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "CASESUPERVISEREPORTTOTAL");
		Map<String, Object> jabgTotal = (Map<String, Object>) baseService.get(id);
		thMap.put("jabg", jabgTotal.get("total"));// 结案报告

		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "CASESUPERVISEURGENTTOTAL");
		Map<String, Object> cbxxTotal = (Map<String, Object>) baseService.get(id);
		thMap.put("cbxx", cbxxTotal.get("total"));// 催办信息

		// LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "CASESUPERVISEAUDIT");
		// List<Map<String, Object>> shxx = (List<Map<String, Object>>)
		// baseService.list(requestBody);
		// map.put("shxx", shxx);// 审核信息
		// thMap.put("shxx", shxx.size());
		// LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "CASESUPERVISEREPORT");
		// List<Map<String, Object>> jabg = (List<Map<String, Object>>)
		// baseService.list(requestBody);
		// map.put("jabg", jabg);// 结案报告
		// thMap.put("jabg", jabg.size());
		responseMap.put("data", map);
		responseMap.put("th", thMap);
		return responseMap;
	}
}
