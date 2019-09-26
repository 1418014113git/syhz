package com.nmghr.controller;

import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
import com.nmghr.handler.vo.standardinfo;
import com.nmghr.util.SyhzUtil;

@RestController
@RequestMapping("standardInfo")
public class StandardInfoController {
	@Autowired
	private EsService StandardInfoService;

	@PostMapping("/query")
	@ResponseBody
	public Object query(@RequestBody Map<String, Object> map) {
		try {
			String searchType = "";
		    String articleType = SyhzUtil.setDate(map.get("articleType"));
		    String search = SyhzUtil.setDate(map.get("search"));
		    searchType = "search" + toInt(articleType) + toInt(search);
			Map<String, Object> esMap = StandardInfoService.query("standardinfo", map, searchType);// es查询documentId
			IQueryHandler queryHandler = SpringUtils.getBean("standardinfoQueryHandler", IQueryHandler.class);
			Map<String, Object> responseMap = (Map<String, Object>) queryHandler.list(esMap);
			return responseMap;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return Result.fail();
		}
	}

	@PostMapping("/save")
	@ResponseBody
	public Object save(@RequestBody Map<String, Object> map) {
		UUID(map);// 生成documentId
		standardinfo standardinfo = JSON.parseObject(JSON.toJSONString(map), standardinfo.class);
		standardinfo.setEnable(0);// 启用
		standardinfo.setDelFlag(0);// 正常
		standardinfo.setAuditStatus(0);// 待审核
		String documentId = StandardInfoService.insert(map, "standardinfo", standardinfo);// 保存到es
		try {
			map.put("documentId", documentId);
			ISaveHandler saveHandler = SpringUtils.getBean("standardinfoSaveHandler", ISaveHandler.class);
			Object object = saveHandler.save(map);// 保存到数据库
			return Result.ok(object);
		} catch (Exception e) {
			e.printStackTrace();
			StandardInfoService.delete("standardinfo", documentId);
			return Result.fail();
		}
	}

	@PostMapping("/update")
	@ResponseBody
	public Object update(@RequestBody Map<String, Object> map) {
		String documnetId = SyhzUtil.setDate(map.get("documentId"));
		try {
			standardinfo standardinfo = JSON.parseObject(JSON.toJSONString(map), standardinfo.class);
			StandardInfoService.update(map, documnetId, "standardinfo", standardinfo);
			IUpdateHandler IUpdateHandler = SpringUtils.getBean("standardInfoUpdateHandler", IUpdateHandler.class);
			return IUpdateHandler.update(documnetId, map);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return Result.fail();
		}
	}

	@PostMapping("/delete")
	@ResponseBody
	public Object delete(@RequestBody Map<String, Object> map) {
		String documnetId = SyhzUtil.setDate(map.get("documentId"));
		StandardInfoService.delete("standardinfo", documnetId);
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

}
