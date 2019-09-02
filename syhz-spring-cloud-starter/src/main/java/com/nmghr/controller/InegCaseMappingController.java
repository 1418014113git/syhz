package com.nmghr.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.common.Result;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;

@RestController
@RequestMapping("/inegcasemapp")
public class InegCaseMappingController {
	private static final Logger log = LoggerFactory.getLogger(WorkFlowController.class);
	@Autowired
	@Qualifier("baseService")
	private IBaseService baseService;

	@PostMapping("/count")
	@ResponseBody
	public Object list(@RequestBody Map<String, Object> requestBody) throws Exception {
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "INVESTIGATIONCASETOTAL");
		Object obj = baseService.list(requestBody);
		return Result.ok(obj);
	}
 
	@PostMapping("/pagebycid")
	@ResponseBody
	public Object pageByCid(@RequestBody Map<String, Object> requestBody) throws Exception {
		Integer currentPage = (Integer) requestBody.get("pageNum");
		Integer pageSize = (Integer) requestBody.get("pageSize");
		if (currentPage == null) {
			currentPage = 1;
		}
		if (pageSize == null) {
			pageSize = 10;
		}
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "INVESTIGATIONCASE");
		Object obj = baseService.page(requestBody, currentPage, pageSize);
		return Result.ok(obj);
	}
}
