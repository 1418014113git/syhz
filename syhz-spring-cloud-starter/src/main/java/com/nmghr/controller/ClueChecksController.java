package com.nmghr.controller;

import java.util.Map;

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
@RequestMapping("/cluecheckinfo")
public class ClueChecksController {
	@Autowired
	@Qualifier("baseService")
	private IBaseService baseService;

	@PostMapping("/list")
	@ResponseBody
	public Object list(@RequestBody Map<String, Object> requestBody) throws Exception {
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "CLUECHECKINFO");
		Object obj = baseService.list(requestBody);
		return Result.ok(obj);
	}
}
