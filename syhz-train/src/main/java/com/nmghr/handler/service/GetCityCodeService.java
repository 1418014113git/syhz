package com.nmghr.handler.service;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.rmdb.datasource.TargetDataSource;
import com.nmghr.handler.mapper.GetCityMapper;

@Service("getcitycodeService")
public class GetCityCodeService implements IBaseService {

	@Autowired
	@Resource
	GetCityMapper GetCityMapper;

	@Override
	public Object save(Map<String, Object> requestMap) throws Exception {
		return null;
	}

	@Override
	public Object update(String id, Map<String, Object> requestBody) throws Exception {
		return null;
	}

	@Override
	public void remove(Map<String, Object> requestMap) throws Exception {

	}

	@Override
	public void remove(String id) throws Exception {

	}

	@Override
	@TargetDataSource(value = "hrupms")
	public Object get(String id) throws Exception {
		List<Map<String, Object>> getCityMap = (List<Map<String, Object>>) GetCityMapper.getCityCode();

		return getCityMap;
	}

	@Override
	@TargetDataSource(value = "hrupms")

	public Object list(Map<String, Object> requestMap) throws Exception {
		String belongDepCode = String.valueOf(requestMap.get("departCode"));
		List<Map<String, Object>> getAreaMap = GetCityMapper.getAreaCode(belongDepCode);

		return getAreaMap;

	}

	@Override
	public Object page(Map<String, Object> requestMap, int currentPage, int pageSize) throws Exception {

		return null;
	}

	@Override
	@TargetDataSource(value = "hrupms")

	public Object get(Map<String, Object> requestMap) throws Exception {
		List<Map<String, Object>> getCityMap = (List<Map<String, Object>>) GetCityMapper.getCityCode();
		for (Map<String, Object> CityMap : getCityMap) {
			String belongDepCode = String.valueOf(CityMap.get("departCode"));
			List<Map<String, Object>> getAreaMap = GetCityMapper.getAreaCode(belongDepCode);
			CityMap.put("depart", getAreaMap);
		}

		return getCityMap;
	}

	@Override
	public Object findAll() throws Exception {
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

}
