package com.nmghr.service.ajglqbxs;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.common.exception.GlobalErrorException;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.hander.save.cluster.DeptMapperSaveHandler;
import com.nmghr.hander.save.common.BatchSaveHandler;
import com.nmghr.handler.message.QueueConfig;
import com.nmghr.handler.service.SendMessageService;
import com.nmghr.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;

@SuppressWarnings("unchecked")
@Service("ajglSignService")
public class AjglSignService {

  @Autowired
  private IBaseService baseService;

  @Autowired
  private BatchSaveHandler batchSaveHandler;

  @Autowired
  private SendMessageService sendMessageService;

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
    p.put("assistType", body.get("assistType"));
    p.put("deptCode", body.get("deptCode"));
    p.put("qbxSign", 1);

    String assistType = String.valueOf(body.get("assistType"));
    String alias = "";
    if ("".equals(assistType) || "2".equals(assistType)) {
        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJGLQBXSSIGNLIST");
      alias = "AJCLUSTERFEEDBACK";
    }else if ("1".equals(assistType)) {
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJASSISTGLQBXSSIGNLIST");
      alias = "AJASSISTFEEDBACK";
    } else {
      throw new GlobalErrorException("999889","assistType异常");
    }
    List<Map<String, Object>> list = (List<Map<String, Object>>) baseService.list(p);

    if(list!=null && list.size()>0){
      for(Map<String, Object> map: list){
        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, alias);
        List<Map<String, Object>> obj = (List<Map<String, Object>>) baseService.list(map);
        if(obj==null || obj.size()==0){
          LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, alias);
          baseService.save(map);
        }
      }
      saveRecords(body, list, assistType);
    }
    return list.size();
  }


  private void saveRecords(Map<String, Object> body, List<Map<String, Object>> list, Object assistType) {
    List<Map<String, Object>> datas = new ArrayList<>();
//      qbxsId , assistType , assistId , receiveCode , receiveName ,
    for (Map<String, Object> map : list) {
      map.put("assistType", assistType);
      map.put("createName", body.get("deptName"));
      map.put("createCode", body.get("deptCode"));
      map.put("creatorId", body.get("userId"));
      map.put("creatorName", body.get("userName"));
      map.put("optCategory", 2);
      datas.add(map);
    }
    Map<String, Object> param = new HashMap<>();
    param.put("type", "batch");
    param.put("list", datas);
    sendMessageService.sendMessage(param, QueueConfig.AJGLQBXSRECORD);
  }


}
