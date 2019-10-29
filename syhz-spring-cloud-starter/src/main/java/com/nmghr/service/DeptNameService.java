package com.nmghr.service;

import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.rmdb.datasource.TargetDataSource;
import com.nmghr.mapper.UserExtMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service("deptNameService")
public class DeptNameService implements IBaseService {

  @Autowired
  private UserExtMapper userExtMapper;

  @Override
  @TargetDataSource(value="hrupms")
  public Object list(Map<String, Object> requestMap) throws Exception {
    if("deptName".equals(String.valueOf(requestMap.get("queryType")))){
      if(requestMap.get("ids")==null){
        return new ArrayList();
      }
      return userExtMapper.getDeptNameList((List<Object>) requestMap.get("ids"));
    }
    if("managerUserId".equals(String.valueOf(requestMap.get("queryType")))){
      return userExtMapper.getManagerUserId(requestMap.get("deptId"),requestMap.get("roleCodes"));
    }
    if("dictCode".equals(String.valueOf(requestMap.get("queryType")))){
      return userExtMapper.getDictCode(requestMap.get("mananercode"));
    }
    return new ArrayList();
  }

//  @TargetDataSource(value="hrupms")
//  public Object getManagerUserId(Map<String, Object> requestMap) {
//    return userExtMapper.getManagerUserId(requestMap.get("deptId"),requestMap.get("roleCodes"));
//  }

  @Override
  public Object get(String id) throws Exception {
    return null;
  }

  @Override
  public Object get(Map<String, Object> requestMap) throws Exception {
    return null;
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
