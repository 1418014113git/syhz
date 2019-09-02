package com.nmghr.controller;

import java.sql.Timestamp;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.common.Result;
import com.nmghr.basic.common.exception.GlobalErrorEnum;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.ISaveHandler;
import com.nmghr.basic.core.util.SpringUtils;

@RestController
@RequestMapping("/instruct")
public class InstructTimeController {
	private static final Logger log = LoggerFactory.getLogger(WorkFlowController.class);
	@Autowired
	@Qualifier("baseService")
	private IBaseService baseService;

	@PostMapping("/{type}")
	@ResponseBody
	public Object notice(@PathVariable String type, @RequestBody Map<String, Object> requestBody) throws Exception {
		if ("notice".equals(type)) {
			requestBody.put("noticeDate", new Timestamp(System.currentTimeMillis()));
		} else if ("receive".equals(type)) {
			requestBody.put("receiveDate", new Timestamp(System.currentTimeMillis()));
		} else if ("feedback".equals(type)) {
			requestBody.put("feedbackDate", new Timestamp(System.currentTimeMillis()));
		} else {
			return Result.fail(GlobalErrorEnum.DATA_NOT_VALID);
		}
		String id = String.valueOf(requestBody.get("id"));
		if (id == null || "".equals(id)) {
			return Result.fail(GlobalErrorEnum.DATA_NOT_VALID);
		}
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "INEGFEEDBACK");
		Object obj = baseService.update(id, requestBody);
		return Result.ok(obj);
	}
	@PutMapping("/directSave")
	@ResponseBody
	public Object directSave(@RequestBody Map<String, Object> requestBody) throws Exception {
		requestBody.put("noticeDate", new Timestamp(System.currentTimeMillis()));
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "INEGFEEDBACK");
		Object obj = baseService.save(requestBody);
		return Result.ok(obj);
	}
}
