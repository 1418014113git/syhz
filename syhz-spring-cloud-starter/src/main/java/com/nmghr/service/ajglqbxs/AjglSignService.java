package com.nmghr.service.ajglqbxs;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.common.exception.GlobalErrorException;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.hander.save.cluster.DeptMapperSaveHandler;
import com.nmghr.hander.save.common.BatchSaveHandler;
import com.nmghr.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@SuppressWarnings("unchecked")
@Service("ajglSignService")
public class AjglSignService {

  @Autowired
  private IBaseService baseService;

  @Autowired
  private BatchSaveHandler batchSaveHandler;

  /**
   * 线索分配
   *
   * @param body
   * @return
   * @throws Exception
   */
  @Transactional
  public Object signing(Map<String, Object> body) throws Exception {
    if(body.get("signId")==null){
      throw new GlobalErrorException("999889","signId不能为空");
    }
    Map<String, Object> bean = new HashMap<>();
    bean.put("assistId", body.get("assistId"));
    bean.put("receiveUserId", body.get("userId"));
    bean.put("receiveUserName", body.get("userName"));
    bean.put("signStatus", 2);
    bean.put("receiveDate", DateUtil.dateFormart(new Date(),DateUtil.yyyyMMddHHmmss));
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJGLQBXSSIGN");
    baseService.update(String.valueOf(body.get("signId")), bean);

    Map<String, Object> p = new HashMap<>();
    p.put("assistId", body.get("assistId"));
    p.put("assistType", 2);
    p.put("deptCode", body.get("deptCode"));
    p.put("qbxSign", 1);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJGLQBXSSIGNLIST");
    List<Map<String, Object>> list = (List<Map<String, Object>>) baseService.list(p);
    if(list!=null && list.size()>0){
      for(Map<String, Object> map: list){
        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJCLUSTERFEEDBACK");
        List<Map<String, Object>> obj = (List<Map<String, Object>>) baseService.list(map);
        if(obj==null || obj.size()==0){
          LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJCLUSTERFEEDBACK");
          baseService.save(map);
        }
      }
    }
    return list.size();
  }



}
