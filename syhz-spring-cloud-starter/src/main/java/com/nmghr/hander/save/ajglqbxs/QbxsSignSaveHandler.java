package com.nmghr.hander.save.ajglqbxs;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractSaveHandler;
import com.nmghr.hander.dto.BusinessSignParam;
import com.nmghr.hander.save.common.BatchSaveHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@SuppressWarnings("unchecked")
@Service("qbxsSignSaveHandler")
public class QbxsSignSaveHandler extends AbstractSaveHandler {
  private Logger log = LoggerFactory.getLogger(QbxsSignSaveHandler.class);

  public QbxsSignSaveHandler(IBaseService baseService) {
    super(baseService);
  }

  @Autowired
  private BatchSaveHandler batchSaveHandler;

  @Transactional
  @Override
  public Object save(Map<String, Object> body) throws Exception {
    List<Map<String, Object>> list = (List<Map<String, Object>>) body.get("list");
    if (list == null || list.size() == 0) {
      return -1;
    }
    List<Map<String, Object>> adds = new ArrayList<>();
    for (Map<String, Object> map : list) {
      Map<String, Object> p = new HashMap<>();
      p.put("assistId",map.get("assistId"));
      p.put("assistType",map.get("assistType"));
      p.put("deptCode",map.get("deptCode"));
      p.put("receiveDeptCode",map.get("receiveDeptCode"));
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJGLQBXSSIGNCHECK");
      Map<String, Object> rs = (Map<String, Object>) baseService.get(p);
      if(rs!=null){
        //查询总条数
        p = new HashMap<>();
        p.put("assistId",map.get("assistId"));
        p.put("signStatus", 1);
        p.put("clueNum",Integer.parseInt(String.valueOf(map.get("clueNum"))) + Integer.parseInt(String.valueOf(rs.get("clueNum"))));
        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJGLQBXSSIGN");
        baseService.update(String.valueOf(rs.get("id")), p);
      } else {
        adds.add(map);
      }
    }
    if(adds.size()>0){
      Map<String, Object> params = new HashMap<>();
      params.put("list", adds);
      params.put("alias", "AJGLQBXSSIGNBATCH");
      params.put("seqName", "AJGLQBXSSIGN");
      params.put("subSize", 20);
      batchSaveHandler.save(params);
    }
    return list.size();
  }
//  @Transactional
//  @Override
//  public Object save(Map<String, Object> body) throws Exception {
//    List<Map<String, Object>> list = (List<Map<String, Object>>) body.get("list");
//    if (list == null || list.size() == 0) {
//      return -1;
//    }
//    List<Map<String, Object>> adds = new ArrayList<>();
//    for (Map<String, Object> map : list) {
//      Map<String, Object> p = new HashMap<>();
//      p.put("assistId",map.get("assistId"));
//      p.put("deptCode",map.get("deptCode"));
//      p.put("receiveDeptCode",map.get("receiveDeptCode"));
//      p.put("signStatus",1);
//      p.put("toDay",new Date());
//      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJGLQBXSSIGNCHECKTODAY");
//      Map<String, Object> rs = (Map<String, Object>) baseService.get(p);
//      if(rs!=null){
//        //查询总条数
//        p = new HashMap<>();
//        p.put("assistId",map.get("assistId"));
//        p.put("clueNum",Integer.parseInt(String.valueOf(map.get("clueNum"))) + Integer.parseInt(String.valueOf(rs.get("clueNum"))));
//        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJGLQBXSSIGN");
//        baseService.update(String.valueOf(rs.get("id")), p);
//      } else {
//        adds.add(map);
//      }
//    }
//    if(adds.size()>0){
//      Map<String, Object> params = new HashMap<>();
//      params.put("list", adds);
//      params.put("alias", "AJGLQBXSSIGNBATCH");
//      params.put("seqName", "AJGLQBXSSIGN");
//      params.put("subSize", 20);
//      batchSaveHandler.save(params);
//    }
//    return list.size();
//  }

}