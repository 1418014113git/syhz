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
import com.nmghr.handler.vo.Lawinfo;
import com.nmghr.util.SyhzUtil;

/**
 * 法律法规
 * 
 * @author heijiantao
 * @date 2019年9月30日
 * @version 1.0
 */
@RestController
@RequestMapping("lawInfo")
public class LawInfoController {
	@Autowired
	private EsService esService;

	@PostMapping("/query")
	@ResponseBody
	public Object query(@RequestBody Map<String, Object> map) throws Exception {
		try {
			String searchType = "";
			String articleType = SyhzUtil.setDate(map.get("articleType"));
			String search = SyhzUtil.setDate(map.get("search"));
			String category = SyhzUtil.setDate(map.get("category"));
			searchType = "search" + toInt(articleType) + toInt(category) + toInt(search);
			Map<String, Object> esMap = esService.query("lawinfo", map, searchType);// es查询documentId
			IQueryHandler queryHandler = SpringUtils.getBean("lawinfoQueryHandler", IQueryHandler.class);
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
		Lawinfo lawinfo = JSON.parseObject(JSON.toJSONString(map), Lawinfo.class);
		lawinfo.setEnable(0);// 启用
		lawinfo.setDelFlag(0);// 正常
		lawinfo.setAuditStatus(0);// 待审核
		String documentId = esService.insert(map, "lawinfo", lawinfo);// 保存到es
		if (!"false".equals(documentId)) {
			try {
				map.put("documentId", documentId);
				ISaveHandler saveHandler = SpringUtils.getBean("lawinfoSaveHandler", ISaveHandler.class);
				Object object = saveHandler.save(map);// 保存到数据库s
				if (1 == deptFlag(map)) {// 改变es审核状态
					esService.auidt("lawinfo", documentId);
				}
				return Result.ok(object);
			} catch (Exception e) {
				e.printStackTrace();
				esService.delete("lawinfo", documentId);// 删除本条数据
				return Result.fail();
			}
		} else {
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
			esService.update(map, documnetId, "lawinfo", IndustryInfo);
			IUpdateHandler IUpdateHandler = SpringUtils.getBean("lawInfoUpdateHandler", IUpdateHandler.class);
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
		esService.delete("lawinfo", documnetId);
		SpringUtils.getBean("lawInfoRemoveHandler", IRemoveHandler.class);
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
