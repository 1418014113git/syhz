package com.nmghr.controller;

import java.util.Map;
import java.util.UUID;

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
import com.nmghr.handler.vo.lawinfo;
import com.nmghr.util.SyhzUtil;

@RestController
@RequestMapping("lawInfo")
public class LawInfoController {
	@Autowired
	private EsService LawInfoService;

	@PostMapping("/query")
	@ResponseBody
	public Object query(@RequestBody Map<String, Object> map) {
		try {
			String searchType = "";
			String articleType = SyhzUtil.setDate(map.get("articleType"));
			if (articleType != "") {// 根据参数调用不同的DSL
				searchType = "searchPage";
			} else {
				searchType = "categorysearchPage";
			}
			Map<String, Object> esMap = LawInfoService.query("lawinfo", map, searchType);// es查询documentId
			IQueryHandler queryHandler = SpringUtils.getBean("lawinfoQueryHandler", IQueryHandler.class);
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
		lawinfo lawinfo = JSON.parseObject(JSON.toJSONString(map), lawinfo.class);
		lawinfo.setEnable(0);// 启用
		lawinfo.setDelFlag(0);// 正常
		lawinfo.setAuditStatus(0);// 待审核
		String documentId = LawInfoService.insert(map, "lawinfo", lawinfo);// 保存到es
		try {
			map.put("documentId", documentId);
			ISaveHandler saveHandler = SpringUtils.getBean("lawinfoSaveHandler", ISaveHandler.class);
			Object object = saveHandler.save(map);// 保存到数据库
			return Result.ok(object);
		} catch (Exception e) {
			e.printStackTrace();
			LawInfoService.delete("lawinfo", documentId);
			return Result.fail();
		}
	}

	@PostMapping("/update")
	@ResponseBody
	public Object update(@RequestBody Map<String, Object> map) {
		String documnetId = SyhzUtil.setDate(map.get("documentId"));
		try {
			lawinfo lawinfo = JSON.parseObject(JSON.toJSONString(map), lawinfo.class);
			LawInfoService.update(map, documnetId, "lawinfo", lawinfo);
			IUpdateHandler IUpdateHandler = SpringUtils.getBean("lawInfoUpdateHandler", IUpdateHandler.class);
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
		LawInfoService.delete("lawinfo", documnetId);
		SpringUtils.getBean("lawInfoRemoveHandler", IRemoveHandler.class);
		return Result.ok("000000");
	}

	private void UUID(Map<String, Object> map) {
		UUID uuid = UUID.randomUUID();
		String id = uuid.toString().replace("-", "");
		map.put("documentId", id);
	}

}
