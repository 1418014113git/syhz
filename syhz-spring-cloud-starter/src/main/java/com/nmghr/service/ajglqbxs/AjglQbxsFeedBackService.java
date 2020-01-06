package com.nmghr.service.ajglqbxs;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.nmghr.basic.common.Constant;
import com.nmghr.basic.common.exception.GlobalErrorException;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.hander.save.common.BatchSaveHandler;
import com.nmghr.util.DateUtil;
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

  /**
   * 线索分配
   *
   * @param body
   * @return
   * @throws Exception
   */
  @Transactional
  public Object feedBack(Map<String, Object> body) throws Exception {
    if (body.get("fbId") == null) {
      throw new GlobalErrorException("999889", "fbId不能为空");
    }
    String fbId = String.valueOf(body.get("fbId"));
    String assistType = String.valueOf(body.get("assistType"));
    String feedBack_alias = "1".equals(assistType)?"AJASSISTFEEDBACK":"AJCLUSTERFEEDBACK";// 1案件协查,2集群战役
    Map<String, Object> backP = new HashMap<>();
    backP.put("fbId", body.get("fbId"));
    backP.put("assistId", body.get("assistId"));
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, feedBack_alias);
    Map<String, Object> feedBack = (Map<String, Object>) baseService.get(backP);
    if(feedBack==null){
      throw new GlobalErrorException("999889", "线索不存在");
    }
    if (StringUtils.isEmpty(feedBack.get("ajglQbxsId"))) {
      throw new GlobalErrorException("999889", "该线索异常不能为空");
    }
    String qbxsId = String.valueOf(body.get("qbxsId"));
    Map<String, Object> bean = new HashMap<>();
    bean.put("qbxsResult", body.get("qbxsResult"));
    bean.put("ids", feedBack.get("ajglQbxsId"));
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJGLQBXSBASEBATCHUPDATE");
    baseService.update(qbxsId, bean);

    Map<String, Object> param = new HashMap<>();
    param.put("handleResult", body.get("handleResult"));
    //立案村案件
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
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, feedBack_alias);
    baseService.update(fbId, param);
    return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
  }
}