package com.nmghr.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.common.Result;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;

@RestController
@RequestMapping("/laws")
public class SyhLawsController {

	private final String LAWS_ALIAS = "SYHLAWS";

	@Autowired
	@Qualifier("baseService")
	private IBaseService baseService;

	@GetMapping("/list")
	@ResponseBody
	public Object list(@RequestParam Map<String, Object> requestParam, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String fllb = String.valueOf(requestParam.get("syhFllb"));
		String lawCategory = String.valueOf(requestParam.get("lawCategory"));
		if (requestParam.get("syhFllb") != null && !"".equals(String.valueOf(requestParam.get("syhFllb")))) {
			requestParam.put("syhFllb", fllb.split(","));
		} else {
			requestParam.remove("syhFllb");
		}
		if (requestParam.get("lawCategory") != null && !"".equals(String.valueOf(requestParam.get("lawCategory")))) {
			requestParam.put("lawCategory", lawCategory.split(","));
		} else {
			requestParam.remove("lawCategory");
		}
		int currentPage = 1;
		int pageSize = 10;
		if (requestParam.get("pageNum") != null) {
			currentPage = Integer.parseInt(String.valueOf(requestParam.get("pageNum")));
		}
		if (requestParam.get("pageSize") != null) {
			pageSize = Integer.parseInt(String.valueOf(requestParam.get("pageSize")));
		}
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, LAWS_ALIAS);
		Object obj = baseService.page(requestParam, currentPage, pageSize);
		return Result.ok(obj);
	}
}
