package com.nmghr.es.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.nmghr.basic.core.service.handler.IQueryHandler;
import com.nmghr.basic.core.util.SpringUtils;
import com.nmghr.es.crud.courseCrud;

@RestController
@RequestMapping("escourse")
public class courseController {
	@Autowired
	private courseCrud courseCrud;

	@PostMapping("/query")
	@ResponseBody
	public Object query(@RequestBody Map<String, Object> map) throws Exception {

		Map<String, Object> response = courseCrud.query(map);
		IQueryHandler queryHandler = SpringUtils.getBean("traincourseQueryHandler", IQueryHandler.class);
		Object list = queryHandler.list(response);
		return list;
	}

	@PostMapping("/save")
	@ResponseBody
	public void save(@RequestBody Map<String, Object> map) {
		courseCrud.save(map);
	}
}
