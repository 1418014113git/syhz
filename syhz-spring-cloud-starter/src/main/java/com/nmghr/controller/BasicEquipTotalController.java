package com.nmghr.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.service.DeptNameService;
import com.nmghr.util.SyhzUtil;

/**
 * 装备统计
 * 
 * @author heijiantao
 * @date 2019年10月30日
 * @version 1.0
 */
@RestController
@RequestMapping("/basicequip")
public class BasicEquipTotalController {
	@Autowired
	@Qualifier("baseService")
	private IBaseService baseService;
	@Autowired
	private DeptNameService DeptNameService;

	// 装备统计
	@PostMapping("/total")
	@ResponseBody
	public Object list(@RequestBody Map<String, Object> requestBody) throws Exception {
		String provinceCode = SyhzUtil.setDate(requestBody.get("provinceCode"));
		String cityCode = SyhzUtil.setDate(requestBody.get("cityCode"));
		String departCode = SyhzUtil.setDate(requestBody.get("departCode"));
		String type = SyhzUtil.setDate(requestBody.get("type"));
		List<Map<String, Object>> cityList = null;
		List<Map<String, Object>> responseList = new ArrayList<Map<String, Object>>();
		Map<String, Object> totalMap = new HashMap<String, Object>();
		cityList = (List<Map<String, Object>>) DeptNameService.get(requestBody);
		for (Map<String, Object> city : cityList) {
			String cCode = String.valueOf(city.get("areaCode"));
			if ("610403".equals(cCode)) {// 杨凌师范区特殊处理
				city.put("areaCodeSpe", cCode);
			} else {
				city.put("areaCode", cCode.substring(0, 4));
			}
			requestBody.putAll(city);
			LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "BASICEQUIPTOTAL");
			List<Map<String, Object>> totalList = (List<Map<String, Object>>) baseService.list(requestBody);
			requestBody.remove("areaCodeSpe");
			city.put("totalList", totalList);
			Map<String, Object> thMap2 = count(city, totalMap);// 数据处理
			responseList.add(thMap2);
		}

		if (!"1".equals(type)) {// 添加合计
			totalMap.put("areaName", "合计");
			totalMap.put("cityCode", "a");
			responseList.add(totalMap);
		}
		Map<String, Object> rsponsemap = new HashMap<String, Object>();
		rsponsemap.put("list", responseList);
		rsponsemap.put("th", get(requestBody));
		return rsponsemap;
	}

	// 数据结构处理
	public Object get(Map<String, Object> requestBody) throws Exception {
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "BASICEQUIPTH");
		List<Map<String, Object>> thList = (List<Map<String, Object>>) baseService.list(requestBody);
		for (Map<String, Object> th : thList) {
			String groupId = SyhzUtil.setDate(th.get("groupId"));
			List<Map<String, Object>> list = childrens(groupId);
			th.put("children", list);
		}
		Map<String, Object> total = new HashMap<String, Object>();
		total.put("label", "合计");
		total.put("groupId", "a");
		List<Map<String, Object>> list = childrens("a");
		total.put("children", list);
		thList.add(total);

		return thList;
	}

	// 表头结构处理
	private List<Map<String, Object>> childrens(String groupId) {
		List<Map<String, Object>> children = new ArrayList<Map<String, Object>>();
		Map<String, Object> thMap1 = new HashMap<String, Object>();
		thMap1.put("prop", "totalType0" + groupId);
		thMap1.put("label", "必配装备");
		thMap1.put("allocateType", "1");
		Map<String, Object> thMap2 = new HashMap<String, Object>();
		thMap2.put("prop", "totalType1" + groupId);
		thMap2.put("label", "选配装备");
		thMap2.put("allocateType", "2");
		Map<String, Object> thMap3 = new HashMap<String, Object>();
		thMap3.put("prop", "totalType2" + groupId);
		thMap3.put("label", "有待更新");
		thMap3.put("allocateType", "");
		children.add(thMap1);
		children.add(thMap2);
		children.add(thMap3);
		for (Map<String, Object> c : children) {
			c.put("groupId", groupId);
		}
		return children;
	}

	// 数据处理

	private Map<String, Object> count(Map<String, Object> city, Map<String, Object> totalMap) {

		int p1 = SyhzUtil.setDateInt(city.get("p1"));
		int p2 = SyhzUtil.setDateInt(city.get("p2"));
		int p3 = SyhzUtil.setDateInt(city.get("p3"));


		int value1 = 0;
		int value2 = 0;
		int totalType1total = 0;
		int totalType2total = 0;
		String areaName = SyhzUtil.setDate(city.get("areaName"));
		String cityCode = SyhzUtil.setDate(city.get("cityCode"));
		String departType = SyhzUtil.setDate(city.get("departType"));

		String departCode = SyhzUtil.setDate(city.get("departCode"));
		Map<String, Object> map = new HashMap<String, Object>();
		List<Map<String, Object>> totalList = (List<Map<String, Object>>) city.get("totalList");
		for (Map<String, Object> total : totalList) {
			toMap(total, totalMap, map, p1, p2, p3, value1, value2, totalType1total, totalType2total);
		}
		map.put("areaName", areaName);
		map.put("departCode", departCode);
		map.put("cityCode", cityCode);
		map.put("departType", departType);
		map.put("value1a", value1);
		map.put("value2a", value2);
		map.put("totalType0a", value1 + "/" + value2);
		map.put("totalType1a", totalType1total);
		map.put("totalType2a", totalType2total);
		totalMap.put("value1a", value1 + SyhzUtil.setDateInt(totalMap.get("value1a")));
		totalMap.put("value2a", value2 + SyhzUtil.setDateInt(totalMap.get("value2a")));
		totalMap.put("totalType0a",
				SyhzUtil.setDateInt(totalMap.get("value1a")) + "/" + SyhzUtil.setDateInt(totalMap.get("value2a")));
		totalMap.put("totalType1a", totalType1total + SyhzUtil.setDateInt(totalMap.get("totalType1totala")));
		totalMap.put("totalType2a", totalType2total + SyhzUtil.setDateInt(totalMap.get("totalType2totala")));
		return map;
	}

	private void toMap(Map<String, Object> total, Map<String, Object> totalMap, Map<String, Object> map, int p1, int p2,
			int p3, int value1, int value2, int totalType1total, int totalType2total) {
		int e1 = SyhzUtil.setDateInt(total.get("equip1"));
		int e2 = SyhzUtil.setDateInt(total.get("equip2"));
		int e3 = SyhzUtil.setDateInt(total.get("equip3"));
		int groupId = SyhzUtil.setDateInt(total.get("groupId"));
		String groupName = SyhzUtil.setDate(total.get("group_name"));
		int p1v1 = SyhzUtil.setDateInt(total.get("p1v1"));
		int p1v2 = SyhzUtil.setDateInt(total.get("p1v2"));
		int p1v3 = SyhzUtil.setDateInt(total.get("p1v3"));
		int equipNumber = SyhzUtil.setDateInt(total.get("equipNumber"));
		int totalType1 = SyhzUtil.setDateInt(total.get("totalType1"));
		int totalType2 = SyhzUtil.setDateInt(total.get("totalType2"));
		System.out.println(e1 + "/" + e2 + "/" + e3);
		int v = p1 * p1v1 + p2 * p1v2 + p1v3 * p3 + e1 + e2 + e3;
		value1 = value1 + equipNumber;
		value2 = value2 + v;
		totalType1total = totalType1total + totalType1;
		totalType2total = totalType2total + totalType2;
		if (value1 < value2) {
			map.put("value3" + groupId, 0);
		} else {
			map.put("value3" + groupId, 1);
		}
		map.put("value1" + groupId, equipNumber);
		map.put("value2" + groupId, v);
		map.put("totalType0" + groupId, equipNumber + "/" + v);
		map.put("totalType1" + groupId, totalType1);
		map.put("totalType2" + groupId, totalType2);
		totalMap.put("value1" + groupId, equipNumber + SyhzUtil.setDateInt(totalMap.get("value1" + groupId)));
		totalMap.put("value2" + groupId, v + SyhzUtil.setDateInt(totalMap.get("value2" + groupId)));
		totalMap.put("totalType1" + groupId, totalType1 + SyhzUtil.setDateInt(totalMap.get("totalType1" + groupId)));
		totalMap.put("totalType2" + groupId, totalType2 + SyhzUtil.setDateInt(totalMap.get("totalType2" + groupId)));
		totalMap.put("totalType0" + groupId, SyhzUtil.setDateInt(totalMap.get("value1" + groupId)) + "/"
				+ SyhzUtil.setDateInt(totalMap.get("value2" + groupId)));
	}
}
