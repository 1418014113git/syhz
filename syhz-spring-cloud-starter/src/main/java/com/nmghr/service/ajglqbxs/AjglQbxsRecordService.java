package com.nmghr.service.ajglqbxs;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.hander.save.common.BatchSaveHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
@Service("ajglQbxsRecordService")
public class AjglQbxsRecordService {

  @Autowired
  private IBaseService baseService;


  @Autowired
  private BatchSaveHandler batchSaveHandler;

  /**
   * 线索分发 创建部门分发线索
   *
   * @param body
   * @return
   * @throws Exception
   */
  @Transactional
  public Object batchSave(Map<String, Object> body) {
    List<Map<String, Object>> list = (List<Map<String, Object>>) body.get("list");
    if (list == null || list.size() == 0) {
      return -1;
    }
    Map<String, Object> params = new HashMap<>();
    params.put("list", list);
    params.put("alias", "AJGLQBXSRECORDBATCH");
    params.put("seqName", "AJGLQBXSRECORD");
    params.put("subSize", 20);
    batchSaveHandler.save(params);
    return list.size();
  }


  /**
   * 线索分发 创建部门分发线索
   *
   * @param body
   * @return
   * @throws Exception
   */
  @Transactional
  public Object allSave(Map<String, Object> body) {
    try {
      Map<String, Object> p = new HashMap<>();
      p.put("id", body.get("id"));
      p.put("assistType", Integer.parseInt(String.valueOf(body.get("assistType"))));
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJGLQBXSRECORDGETALLDEPT");
      List<Map<String, Object>> depts = (List<Map<String, Object>>) baseService.list(p);
      if(depts==null ||depts.size()==0){
        return 0;
      }
      for(Map<String, Object> map: depts){
        map.put("createCode", body.get("curDeptCode"));
        map.put("createName", body.get("curDeptName"));
        map.put("creatorId", body.get("userId"));
        map.put("creatorName", body.get("userName"));
        map.put("optCategory", 1);
      }
      Map<String, Object> params = new HashMap<>();
      params.put("list", depts);
      params.put("alias", "AJGLQBXSRECORDBATCH");
      params.put("seqName", "AJGLQBXSRECORD");
      params.put("subSize", 20);
      batchSaveHandler.save(params);
      return depts.size();
    } catch (Exception e) {
      e.printStackTrace();
    }
    //assistType,assistId,receiveName,receiveCode,,createName,createCode,,creatorName,creatorId,optCategory
    return -1;
  }


}
