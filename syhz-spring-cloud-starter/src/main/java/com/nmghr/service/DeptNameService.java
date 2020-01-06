package com.nmghr.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.rmdb.datasource.TargetDataSource;
import com.nmghr.mapper.UserExtMapper;
import com.nmghr.util.SyhzUtil;

@Service("deptNameService")
public class DeptNameService implements IBaseService {

	@Autowired
	private UserExtMapper userExtMapper;

	@Override
	@TargetDataSource(value = "hrupms")
	public Object list(Map<String, Object> requestMap) throws Exception {
		if ("deptName".equals(String.valueOf(requestMap.get("queryType")))) {
			if (requestMap.get("ids") == null) {
				return new ArrayList();
			}
			return userExtMapper.getDeptNameList((List<Object>) requestMap.get("ids"));
		}
		if ("managerUserId".equals(String.valueOf(requestMap.get("queryType")))) {
			return userExtMapper.getManagerUserId(requestMap.get("deptId"), requestMap.get("roleCodes"));
		}
		if ("dictCode".equals(String.valueOf(requestMap.get("queryType")))) {
			return userExtMapper.getDictCode(requestMap.get("mananercode"));
		}
		return new ArrayList();
	}

	// @TargetDataSource(value="hrupms")
	// public Object getManagerUserId(Map<String, Object> requestMap) {
	// return
	// userExtMapper.getManagerUserId(requestMap.get("deptId"),requestMap.get("roleCodes"));
	// }

	@Override
	public Object get(String id) throws Exception {
		return null;
	}

	@Override
	@TargetDataSource(value = "hrupms")
	public Object get(Map<String, Object> requestMap) throws Exception {
		String provinceCode = SyhzUtil.setDate(requestMap.get("provinceCode"));
		String cityCode = SyhzUtil.setDate(requestMap.get("cityCode"));
		String departCode = SyhzUtil.setDate(requestMap.get("departCode"));
		String reginCode = SyhzUtil.setDate(requestMap.get("reginCode"));
		int flag = SyhzUtil.setDateInt(requestMap.get("flag"));
		int i = 2;
		List<Map<String, Object>> cList = null;

		if (!"".equals(departCode)) {
			cList = userExtMapper.getDepart(departCode);
		} else if (!"".equals(reginCode)) {
			cList = userExtMapper.getReginDepart(reginCode);
		} else if (!"".equals(cityCode)) {
			cList = userExtMapper.getCityDepart(cityCode);
		} else if (!"".equals(provinceCode)) {
			cList = userExtMapper.getCity();
			i = 1;
		}

		if (flag != 1) {
			for (Map<String, Object> city : cList) {
				Object dCode = city.get("departCode");
				List<Map<String, Object>> type = userExtMapper.getMyDepartType(dCode, i);
				city.putAll(type.get(0));// 添加人数合计
				city.putAll(setMap(type.get(1)));// 添加部门数合计
			}
		}
		return cList;
	}

	private Map<String, Object> setMap(Map<String, Object> map) {
		Map<String, Object> newMap = new HashMap<String, Object>();
		newMap.put("p1", map.get("r1"));
		newMap.put("p2", map.get("r2"));
		newMap.put("p3", map.get("r3"));
		return newMap;

	}

	@Override
	public Object page(Map<String, Object> requestMap, int currentPage, int pageSize) throws Exception {
		return null;
	}

	@Override
	public Object findAll() throws Exception {
		return null;
	}

	@Override
	public Object update(String id, Map<String, Object> requestBody) throws Exception {
		return null;
	}

	@Override
	public Object update(Map<String, Object> requestParam, Map<String, Object> requestBody) throws Exception {
		return null;
	}

	@Override
	public Object getSequence(String seqName) throws Exception {
		return null;
	}

	@Override
	public void remove(Map<String, Object> requestMap) throws Exception {

	}

	@Override
	public void remove(String id) throws Exception {

	}

	@Override
	public Object save(Map<String, Object> requestMap) throws Exception {
		return null;
	}
}
