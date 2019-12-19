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

		List<Map<String, Object>> cityList = null;
		List<Map<String, Object>> responseList = new ArrayList<Map<String, Object>>();
		Map<String, Object> totalMap = new HashMap<String, Object>();
		cityList = (List<Map<String, Object>>) DeptNameService.get(requestBody);// 获取部门及行政区划
		Map<String, Object> rsponsemap = new HashMap<String, Object>();

		for (Map<String, Object> city : cityList) {
			String cCode = String.valueOf(city.get("areaCode"));
			requestBody.putAll(city);
			LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "BASICEQUIPTOTAL");// 查各部门询统计数据
			List<Map<String, Object>> totalList = (List<Map<String, Object>>) baseService.list(requestBody);
			Map<String, Object> thMap2 = count(totalList, city);// 数据处理
			responseList.add(thMap2);
			rsponsemap.put("tableHead", get(cityList));// 表头
		}
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "BASICEQUIPTH");// 查询所有可用装备项目
		List<Map<String, Object>> thList = (List<Map<String, Object>>) baseService.list(requestBody);
		rsponsemap.put("tableData", setMap(thList, responseList));// 表数据
		return rsponsemap;
	}

	// 数据结构处理
	public Object get(List<Map<String, Object>> cityList) throws Exception {
		List<Map<String, Object>> thList = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> city : cityList) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("value", city.get("cityCode"));
			map.put("label", city.get("cityName"));
			thList.add(map);
		}
		return thList;
	}

	// 数据处理

	private Map<String, Object> count(List<Map<String, Object>> totalList, Map<String, Object> totalMap) {

		int p1 = SyhzUtil.setDateInt(totalMap.get("p1"));// 总队级部门数
		int p2 = SyhzUtil.setDateInt(totalMap.get("p2"));// 支队级部门数
		int p3 = SyhzUtil.setDateInt(totalMap.get("p3"));// 大队级部门数
		int r1 = SyhzUtil.setDateInt(totalMap.get("r1"));// 总队级人数
		int r2 = SyhzUtil.setDateInt(totalMap.get("r2"));// 支队级人数
		int r3 = SyhzUtil.setDateInt(totalMap.get("r3"));// 大队级人数
		String cityCode = SyhzUtil.setDate(totalMap.get("cityCode"));
		String cityName = SyhzUtil.setDate(totalMap.get("cityName"));
		double dyp1 = 0;// 单位为车因配数
		double dyp2 = 0;// 单位为车因配数
		double dyp3 = 0;// 单位为车因配数

		for (Map<String, Object> total : totalList) {
			double yp1 = SyhzUtil.setDateInt(total.get("yp1"));// 总队应配1
			double yp2 = SyhzUtil.setDateInt(total.get("yp2"));// 支队应配1
			double yp3 = SyhzUtil.setDateInt(total.get("yp3"));// 大队应配1
			double yp12 = SyhzUtil.setDateInt(total.get("yp12"));// 总队应配2
			double yp22 = SyhzUtil.setDateInt(total.get("yp22"));// 支队应配2
			double yp32 = SyhzUtil.setDateInt(total.get("yp32"));// 大队应配2
			int sp1 = SyhzUtil.setDateInt(total.get("sp1"));// 无要求或按相关配备类型总队配备数量
			int sp2 = SyhzUtil.setDateInt(total.get("sp2"));// 无要求或按相关配备类型支队配备数量
			int sp3 = SyhzUtil.setDateInt(total.get("sp3"));// 无要求或按相关配备类型大队配备数量
			int e1 = SyhzUtil.setDateInt(total.get("e1"));// 实配数
			int x2 = SyhzUtil.setDateInt(total.get("p2"));// 需更新
			int zbjl = SyhzUtil.setDateInt(total.get("zbjl"));// 计量单位
			int groupId = SyhzUtil.setDateInt(total.get("groupId"));

			double ryp = 0;// 单位为人因配数
			double dyp = 0;// 单位为队因配数
			double typ = 0;// 单位为车因配数
			total.clear();
			if (zbjl >= 9) {// 单位为人
				ryp = Math.ceil(r1 * yp1 / yp12) + Math.ceil(r2 * yp2 / yp12) + Math.ceil(r3 * yp3 / yp32) + sp1 + sp2 + sp3;
				total.put("yp", ryp);
			} else if (zbjl < 8) {// 单位为队
				dyp = Math.ceil(p1 * yp1 / yp12) + Math.ceil(p2 * yp2 / yp22) + Math.ceil(p3 * yp3 / yp32) + sp1 + sp2 + sp3;
				dyp1 += Math.ceil(p1 * yp1 / yp12);
				dyp2 += Math.ceil(p2 * yp2 / yp22);
				dyp3 += Math.ceil(p3 * yp3 / yp32);
				total.put("yp", dyp);
			} else if (zbjl == 8) {// 单位为车
				typ = Math.ceil(dyp1 * yp1) + Math.ceil(dyp2 * yp2) + Math.ceil(dyp3 * yp3) + sp1 + sp2 + sp3;
				total.put("yp", typ);
			}
			total.put("sp", e1);
			total.put("x2", x2);
			totalMap.put("cityCode", cityCode);
			totalMap.put("cityName", cityName);
			totalMap.put(String.valueOf(groupId), total);
		}
		return totalMap;
	}

	// 处理返回格式
	private List<Map<String, Object>> setMap(List<Map<String, Object>> thList, List<Map<String, Object>> responseList) {
		List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> group : thList) {

			String groupId = SyhzUtil.setDate(group.get("groupId"));
			List<Map<String, Object>> mapList1 = new ArrayList<Map<String, Object>>();
			List<Map<String, Object>> mapList2 = new ArrayList<Map<String, Object>>();

			for (Map<String, Object> dept : responseList) {
				int dtype = SyhzUtil.setDateInt(dept.get("dtype"));
				String cityCode = SyhzUtil.setDate(dept.get("cityCode"));
				String cityName = SyhzUtil.setDate(dept.get("cityName"));
				Map<String, Object> map1 = new HashMap<String, Object>();
				Map<String, Object> map2 = new HashMap<String, Object>();
				if (dtype == 1) {
					map1.put("cityCode", cityCode);
					map1.put("cityName", cityName);
					map2.put("cityCode", cityCode);
					map2.put("cityName", cityName);
				} else {
					map1.put("deptCode", cityCode);
					map1.put("deptName", cityName);
					map2.put("deptCode", cityCode);
					map2.put("deptName", cityName);
				}
				Map<String, Object> totalMap = (Map<String, Object>) dept.get(groupId);
				map1.put("allocateId", groupId);
				map1.put("sp", totalMap.get("sp"));
				map1.put("yp", totalMap.get("yp"));
				map2.put("gx", totalMap.get("x2"));
				map2.put("allocateId", groupId);
				mapList1.add(map1);
				mapList2.add(map2);
			}
			Map<String, Object> map1 = new HashMap<String, Object>();
			map1.putAll(group);
			group.put("nameType", 1);
			group.put("name", "实配/应配");
			group.put("tableHead", mapList1);
			mapList.add(group);
			map1.put("nameType", 2);
			map1.put("name", "需更新");
			map1.put("tableHead", mapList2);
			mapList.add(map1);

		}
		return mapList;
	}

}
