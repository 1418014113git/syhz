package com.nmghr.service.app;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.rmdb.datasource.TargetDataSource;
import com.nmghr.mapper.app.DeptMapper;

@Service
public class DeptServise implements IBaseService {
	@Autowired
	private DeptMapper DeptMapper;

	@Override
	public Object save(Map<String, Object> requestMap) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object update(String id, Map<String, Object> requestBody) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void remove(Map<String, Object> requestMap) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void remove(String id) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public Object get(String id) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@TargetDataSource(value = "hrupms")
	public Object list(Map<String, Object> requestMap) throws Exception {
		// TODO Auto-generated method stub
		return DeptMapper.getDept(requestMap);
	}

	@Override
	public Object page(Map<String, Object> requestMap, int currentPage, int pageSize) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object get(Map<String, Object> requestMap) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@TargetDataSource(value = "hrupms")
	public Object findAll() throws Exception {

		return DeptMapper.getCityTree();
	}

	@Override
	public Object update(Map<String, Object> requestParam, Map<String, Object> requestBody) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getSequence(String seqName) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
