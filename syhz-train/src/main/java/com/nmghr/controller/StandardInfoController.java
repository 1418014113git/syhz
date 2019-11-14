package com.nmghr.controller;

import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.nmghr.basic.common.Result;
import com.nmghr.basic.core.service.handler.IQueryHandler;
import com.nmghr.basic.core.service.handler.IRemoveHandler;
import com.nmghr.basic.core.service.handler.ISaveHandler;
import com.nmghr.basic.core.service.handler.IUpdateHandler;
import com.nmghr.basic.core.util.SpringUtils;
import com.nmghr.handler.service.EsService;
import com.nmghr.handler.vo.IndustryInfo;
import com.nmghr.handler.vo.Standardinfo;
import com.nmghr.util.SyhzUtil;

/**
 * 规范制度
 * 
 * @author heijiantao
 * @date 2019年9月30日
 * @version 1.0
 */
@RestController
@RequestMapping("standardInfo")
public class StandardInfoController {
	@Autowired
	private EsService esService;

	@PostMapping("/query")
	@ResponseBody
	public Object query(@RequestBody Map<String, Object> map) {
		try {
			String searchType = "";
			String articleType = SyhzUtil.setDate(map.get("articleType"));
			String search = SyhzUtil.setDate(map.get("search"));
			searchType = "search" + toInt(articleType) + toInt(search);
			Map<String, Object> esMap = esService.query("standardinfo", map, searchType);// es查询documentId
			IQueryHandler queryHandler = SpringUtils.getBean("standardinfoQueryHandler", IQueryHandler.class);
			Map<String, Object> responseMap = (Map<String, Object>) queryHandler.list(esMap);
			return responseMap;
		} catch (Exception e) {
			return Result.fail("000000", "暂无数据");
		}
	}

	@PostMapping("/save")
	@ResponseBody
	public Object save(@RequestBody Map<String, Object> map) {
		UUID(map);// 生成documentId
		Standardinfo standardinfo = JSON.parseObject(JSON.toJSONString(map), Standardinfo.class);
		standardinfo.setEnable(0);// 启用
		standardinfo.setDelFlag(0);// 正常
		standardinfo.setAuditStatus(0);// 待审核
		String documentId = esService.insert(map, "standardinfo", standardinfo);// 保存到es
		try {
			map.put("documentId", documentId);
			ISaveHandler saveHandler = SpringUtils.getBean("standardinfoSaveHandler", ISaveHandler.class);
			Object object = saveHandler.save(map);// 保存到数据库
			if (1 == deptFlag(map)) {// 改变es审核状态
				esService.auidt("standardinfo", documentId);
			}
			return Result.ok(object);
		} catch (Exception e) {
			e.printStackTrace();
			esService.delete("standardinfo", documentId);
			return Result.fail();
		}
	}

	@PostMapping("/update")
	@ResponseBody
	public Object update(@RequestBody Map<String, Object> map) {
		String id = SyhzUtil.setDate(map.get("id"));
		String documnetId = SyhzUtil.setDate(map.get("documentId"));
		try {
			IndustryInfo IndustryInfo = JSON.parseObject(JSON.toJSONString(map), IndustryInfo.class);
			esService.update(map, documnetId, "standardinfo", IndustryInfo);
			IUpdateHandler IUpdateHandler = SpringUtils.getBean("standardInfoUpdateHandler", IUpdateHandler.class);
			return IUpdateHandler.update(id, map);
		} catch (Exception e) {
			e.printStackTrace();
			return Result.fail();
		}
	}

	@PostMapping("/delete")
	@ResponseBody
	public Object delete(@RequestBody Map<String, Object> map) {
		String documnetId = SyhzUtil.setDate(map.get("documentId"));
		esService.delete("standardinfo", documnetId);
		SpringUtils.getBean("standardInfoRemoveHandler", IRemoveHandler.class);
		return Result.ok("000000");
	}

	private void UUID(Map<String, Object> map) {
		UUID uuid = UUID.randomUUID();
		String id = uuid.toString().replace("-", "");
		map.put("documentId", id);
	}

	private String toInt(String str) {
		if ("".equals(str)) {
			return "0";
		} else {
			return "1";
		}
	}

	private int deptFlag(Map<String, Object> map) {
		int draft = SyhzUtil.setDateInt(map.get("draft"));// 是否为草稿
		int adminFlag = SyhzUtil.setDateInt(map.get("adminFlag"));// 是否为管理员
		if (adminFlag == 0 && draft == 1) {
			String myDeptAreaCode = SyhzUtil.setDate(map.get("myDeptAreaCode"));
			Pattern pattern1 = Pattern.compile("^\\d*[1-9]0{4}$");
			Matcher matcher1 = pattern1.matcher(myDeptAreaCode);
			if (matcher1.find()) {// 省级管理员
				return 1;
			}
		}
		return 0;
	}
}
