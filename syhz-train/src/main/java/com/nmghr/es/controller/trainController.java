package com.nmghr.es.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.nmghr.es.crud.trainCrud;

@RestController
@RequestMapping("es")
public class trainController {
	@Autowired
	private trainCrud trainCrud;

	@GetMapping("/trainequery")
	public void created() {
		// trainCrud.insert();
	}

	@GetMapping("/trainsave")
	@ResponseBody
	public void save(Map<String, Object> map) {
		trainCrud.insert(map);
	}
}
