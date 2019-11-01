package com.nmghr.service;

import java.util.ArrayList;
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
		List<Map<String, Object>> cList = null;
		if (!"".equals(provinceCode)) {
			cList = userExtMapper.getCity();
		}
		if (!"".equals(cityCode)) {
			cList = userExtMapper.getCityDepart(cityCode);
		}
		if (!"".equals(departCode)) {
			cList = userExtMapper.getDepart(departCode);
		}
		for (Map<String, Object> city : cList) {
			Object dCode = city.get("departCode");
			// List<Map<String, Object>> depart = userExtMapper.getDepart(departCode);
			// city.put("children", depart);
			if (!"".equals(provinceCode)) {
				Map<String, Object> type = userExtMapper.getDepartType(dCode);
				city.putAll(type);
			} else {
				Map<String, Object> type = userExtMapper.getMyDepartType(dCode);
				city.putAll(type);
			}
		}
		return cList;
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
