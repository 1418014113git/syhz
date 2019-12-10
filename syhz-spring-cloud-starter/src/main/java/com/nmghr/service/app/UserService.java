package com.nmghr.service.app;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.rmdb.datasource.TargetDataSource;
import com.nmghr.mapper.app.UserMapper;

@Service
public class UserService implements IBaseService {

	@Autowired
	private UserMapper UserMapper;

	@Override
	public Object save(Map<String, Object> requestMap) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@TargetDataSource(value = "hrupms")
	public Object update(String id, Map<String, Object> requestBody) throws Exception {
		// TODO Auto-generated method stub
		UserMapper.setUser(requestBody);
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
	@TargetDataSource(value = "hrupms")
	public Object get(String id) throws Exception {
		// TODO Auto-generated method stub
		return UserMapper.getUser(id);
	}

	@Override
	@TargetDataSource(value = "hrupms")
	public Object list(Map<String, Object> requestMap) throws Exception {
		// TODO Auto-generated method stub
		return UserMapper.getUserDept(requestMap);
	}

	@Override
	@TargetDataSource(value = "hrupms")
	public Object page(Map<String, Object> requestMap, int currentPage, int pageSize) throws Exception {
		// TODO Auto-generated method stub
		return UserMapper.getDeptUser(requestMap);
	}

	@Override

	public Object get(Map<String, Object> requestMap) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object findAll() throws Exception {
		// TODO Auto-generated method stub
		return null;
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
