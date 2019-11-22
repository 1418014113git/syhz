package com.nmghr.controller.caseSupervise;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.service.DeptNameService;

/**
 * 案件督办破案情况统计
 * 
 * @author heijiantao
 * @date 2019年11月15日
 * @version 1.0
 */
@SuppressWarnings("unchecked")
@RestController
@RequestMapping("/casesupervise")
public class CaseSuperviseController {

	@Autowired
	@Qualifier("baseService")
	private IBaseService baseService;
	@Autowired
	private DeptNameService DeptNameService;

	// 案件督办破案情况统计
	@PostMapping("/total")
	@ResponseBody
	public Object list(@RequestBody Map<String, Object> requestBody) throws Exception {
		requestBody.put("flag", 1);
		List<Map<String, Object>> cityList = (List<Map<String, Object>>) DeptNameService.get(requestBody);
		for (Map<String, Object> city : cityList) {
			String cCode = String.valueOf(city.get("areaCode"));
			if ("610403".equals(cCode)) {// 杨凌师范区特殊处理
				city.put("areaCodeSpe", cCode);
			} else {
				city.put("areaCode", cCode.substring(0, 4));
			}
			requestBody.putAll(city);
			LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "CASESUPERVISETOTALTWO");
			Map<String, Object> totalList = (Map<String, Object>) baseService.get(requestBody);
			requestBody.remove("areaCodeSpe");
			city.put("totalList", totalList);
		}
		return cityList;
	}

}
