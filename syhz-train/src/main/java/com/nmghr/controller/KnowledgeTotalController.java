package com.nmghr.controller;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.common.Result;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.handler.service.GetCityCodeService;
import com.nmghr.util.SyhzUtil;

@RestController
@RequestMapping("knowledge")
public class KnowledgeTotalController {

	@Autowired
	@Qualifier("baseService")
	private IBaseService baseService;

	@Autowired
	GetCityCodeService GetCityCodeService;
	private static String ALIAS_KNOWLEDGETOTAL = "KNOWLEDGETOTAL";// 发布情况
	private static String ALIAS_KNOWLEDGEUSETOTAL = "KNOWLEDGEUSETOTAL";// 使用情况
	private static String ALIAS_TRAINCOURSELOGSTATISTICS = "TRAINCOURSELOGSTATISTICS"; // 培训资料使用情况

	// 知识库发布情况统计
	@PostMapping("/query")
	@ResponseBody
	public Object query(@RequestBody Map<String, Object> map) throws Exception {
		String departCode = SyhzUtil.setDate(map.get("departCode"));
		if ("".equals(departCode)) {
			// 市级统计
			List<Map<String, Object>> cityCodeList = (List<Map<String, Object>>) GetCityCodeService.get("1");
			for (Map<String, Object> city : cityCodeList) {
				String cityCode = String.valueOf(city.get("areaCode"));
				city.put("areaCode", cityCode.substring(0, 4));
				LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS_KNOWLEDGETOTAL);
				map.putAll(city);
				Map<String, Object> cityTotal = (Map<String, Object>) baseService.get(map);
				Sum(cityTotal);
				city.putAll(cityTotal);
			}
			return cityCodeList;
		} else {
			// 部门
			List<Map<String, Object>> areaCodeList = (List<Map<String, Object>>) GetCityCodeService.list(map);
			if (areaCodeList != null && areaCodeList.size() > 0) {
				for (Map<String, Object> areaCode : areaCodeList) {
					areaCode.remove("areaCode");
					LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS_KNOWLEDGETOTAL);
					map.putAll(areaCode);
					Map<String, Object> areaTotal = (Map<String, Object>) baseService.get(map);
					Sum(areaTotal);
					areaCode.putAll(areaTotal);
				}
			}
			return areaCodeList;
		}
	}

	@PostMapping("/queryAll")
	@ResponseBody
	public Object queryAll(@RequestBody Map<String, Object> map) throws Exception {
		List<Map<String, Object>> cityCodeList = (List<Map<String, Object>>) GetCityCodeService.get(map);
		for (Map<String, Object> city : cityCodeList) {
			String cityCode = String.valueOf(city.get("areaCode"));
			city.put("areaCode", cityCode.substring(0, 4));
			LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS_KNOWLEDGETOTAL);
			map.putAll(city);
			Map<String, Object> cityTotal = (Map<String, Object>) baseService.get(map);
			// Sum(cityTotal);
			List<Map<String, Object>> areaCodeList = (List<Map<String, Object>>) city.get("depart");
			if (areaCodeList != null && areaCodeList.size() > 0) {
				for (Map<String, Object> areaCode : areaCodeList) {
					areaCode.remove("areaCode");
					LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS_KNOWLEDGETOTAL);
					map.putAll(areaCode);
					Map<String, Object> areaTotal = (Map<String, Object>) baseService.get(map);
					// Sum(areaTotal);
					areaCode.putAll(areaTotal);
				}
				city.put("depart", areaCodeList);
			}
			city.putAll(cityTotal);
		}
		return cityCodeList;
	}

	// 知识库使用情况统计
	@PostMapping("/queryUse")
	@ResponseBody
	public Object queryUse(@RequestBody Map<String, Object> map) throws Exception {
		String departCode = SyhzUtil.setDate(map.get("departCode"));
		if ("".equals(departCode)) {
			List<Map<String, Object>> cityCodeList = (List<Map<String, Object>>) GetCityCodeService.get("1");
			for (Map<String, Object> city : cityCodeList) {
				String cityCode = String.valueOf(city.get("areaCode"));
				city.put("areaCode", cityCode.substring(0, 4));
				LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS_KNOWLEDGEUSETOTAL);
				map.putAll(city);
				Map<String, Object> cityTotal = (Map<String, Object>) baseService.get(map);
				city.putAll(cityTotal);
			}
			return cityCodeList;
		} else {
			List<Map<String, Object>> areaCodeList = (List<Map<String, Object>>) GetCityCodeService.list(map);
			if (areaCodeList != null && areaCodeList.size() > 0) {
				for (Map<String, Object> areaCode : areaCodeList) {
					areaCode.remove("areaCode");
					LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS_KNOWLEDGEUSETOTAL);
					map.putAll(areaCode);
					Map<String, Object> areaTotal = (Map<String, Object>) baseService.get(map);
					areaCode.putAll(areaTotal);
				}
			}
			return areaCodeList;
		}
	}

	// 培训资料使用情况统计
	@PostMapping("/queryTrainCrouse")
	@ResponseBody
	public Object queryTrainCrouse(@RequestBody Map<String, Object> map) throws Exception {
		String departCode = SyhzUtil.setDate(map.get("departCode"));
		if ("".equals(departCode)) {
			List<Map<String, Object>> cityCodeList = (List<Map<String, Object>>) GetCityCodeService.get("1");
			for (Map<String, Object> city : cityCodeList) {
				String cityCode = String.valueOf(city.get("areaCode"));
				city.put("areaCode", cityCode.substring(0, 4));
				LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS_TRAINCOURSELOGSTATISTICS);
				map.putAll(city);
				Map<String, Object> cityTotal = (Map<String, Object>) baseService.get(map);
				city.putAll(cityTotal);
			}
			return cityCodeList;
		} else {
			List<Map<String, Object>> areaCodeList = (List<Map<String, Object>>) GetCityCodeService.list(map);
			if (areaCodeList != null && areaCodeList.size() > 0) {
				for (Map<String, Object> areaCode : areaCodeList) {
					areaCode.remove("areaCode");
					LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS_TRAINCOURSELOGSTATISTICS);
					map.putAll(areaCode);
					Map<String, Object> areaTotal = (Map<String, Object>) baseService.get(map);
					areaCode.putAll(areaTotal);
				}
			}
			return areaCodeList;
		}
	}

	// 获取系统时间
	@GetMapping("/queryTime")
	@ResponseBody
	public Object queryTime() {
		return Result.ok(new Date().getTime());
	}

	// 行总数
	private void Sum(Map<String, Object> map) {
		int total1 = SyhzUtil.setDateInt(map.get("total1"));
		int total2 = SyhzUtil.setDateInt(map.get("total2"));
		int total3 = SyhzUtil.setDateInt(map.get("total3"));
		int total4 = SyhzUtil.setDateInt(map.get("total4"));
		int total0 = total1 + total2 + total3 + total4;
		map.put("total0", total0);
	}

	// 列总数
	// private List<Map<String, Object>> total(List<Map<String, Object>> mapList) {
	// Map<String, Object> totalMap = new HashMap<String, Object>();
	// int i0 = 0;
	// int i1 = 0;
	// int i2 = 0;
	// int i3 = 0;
	// int i4 = 0;
	// for (Map<String, Object> map : mapList) {
	// int total1 = SyhzUtil.setDateInt(map.get("total1"));
	// int total2 = SyhzUtil.setDateInt(map.get("total2"));
	// int total3 = SyhzUtil.setDateInt(map.get("total3"));
	// int total4 = SyhzUtil.setDateInt(map.get("total4"));
	// int total0 = SyhzUtil.setDateInt(map.get("total0"));
	// i0 += total0;
	// i1 += total1;
	// i2 += total2;
	// i3 += total3;
	// i4 += total4;
	// }
	// totalMap.put("total0", i0);
	// totalMap.put("total1", i1);
	// totalMap.put("total2", i2);
	// totalMap.put("total3", i3);
	// totalMap.put("total4", i4);
	// totalMap.put("departCode", "000000");
	// mapList.add(totalMap);
	// return null;
	// }
}
