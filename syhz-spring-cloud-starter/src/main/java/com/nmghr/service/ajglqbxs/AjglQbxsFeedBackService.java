package com.nmghr.service.ajglqbxs;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.common.exception.GlobalErrorException;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.handler.message.QueueConfig;
import com.nmghr.handler.service.SendMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.*;

@SuppressWarnings("unchecked")
@Service("ajglQbxsFeedBackService")
public class AjglQbxsFeedBackService {

  @Autowired
  private IBaseService baseService;
  @Autowired
  private SendMessageService sendMessageService;

  /**
   * 线索反馈
   *
   * @param body
   * @return
   * @throws Exception
   */
  @Transactional
  public Object feedBack(Map<String, Object> body) throws Exception {
    if (StringUtils.isEmpty(body.get("fbId"))) {
      throw new GlobalErrorException("999889", "fbId不能为空");
    }
    if (StringUtils.isEmpty(body.get("qbxsId"))) {
      throw new GlobalErrorException("999889", "qbxsId不能为空");
    }
    String fbId = String.valueOf(body.get("fbId"));
    String qbxsId = String.valueOf(body.get("qbxsId"));

    //修改线索的反馈状态
    Map<String, Object> bean = new HashMap<>();
    bean.put("qbxsResult", body.get("qbxsResult"));
    bean.put("ids", Arrays.asList(qbxsId.split(",")));
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJGLQBXSBASEBATCHUPDATE");
    baseService.update("", bean);

    Map<String, Object> param = new HashMap<>();
    param.put("handleResult", body.get("handleResult"));
    //立案存案件
    if(!StringUtils.isEmpty(body.get("handleResult")) && "1".equals(String.valueOf(body.get("handleResult")))){
      param.put("zbxss", body.get("zbxss"));
    } else {
      param.put("zbxss", "");
    }
    if(!StringUtils.isEmpty(body.get("backResult"))){
      param.put("backResult", body.get("backResult"));
    }
    if(!StringUtils.isEmpty(body.get("backFiles"))){
      param.put("backFiles", body.get("backFiles"));
    }

    Map<String, Object> reqParam = new HashMap<>();
    reqParam.put("fdIds",Arrays.asList(fbId.split(",")));
    String assistType = String.valueOf(body.get("assistType"));
    String feedBack_alias = "1".equals(assistType)?"AJASSISTFEEDBACK":"AJCLUSTERFEEDBACK";// 1案件协查,2集群战役
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, feedBack_alias);
    baseService.update(reqParam,param);

    saveRecords(body,Arrays.asList(qbxsId.split(",")));

    return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
  }

  private void saveRecords(Map<String, Object> body, List<Object> qbxsIds) {
    List<Map<String, Object>> datas = new ArrayList<>();
    //下发线索增加流转记录
    for (Object qbxsId : qbxsIds) {
      Map<String, Object> map = new HashMap<>();
      map.put("qbxsId", qbxsId);
      map.put("assistType", body.get("assistType"));
      map.put("assistId", body.get("assistId"));
//      map.put("receiveCode", body.get("acceptDeptCode"));
//      map.put("receiveName", body.get("acceptDeptName"));
      map.put("createName", body.get("curDeptName"));
      map.put("createCode", body.get("curDeptCode"));
      map.put("creatorId", body.get("userId"));
      map.put("creatorName", body.get("userName"));
      map.put("optCategory", 6);
      datas.add(map);
    }
    Map<String, Object> param = new HashMap<>();
    param.put("type", "batch");
    param.put("list", datas);
    sendMessageService.sendMessage(param, QueueConfig.AJGLQBXSRECORD);
  }

}