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

    Map<String, Object> backP = new HashMap<>();
    backP.put("fbId", body.get("fbId"));
    backP.put("assistId", body.get("assistId"));
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJCLUSTERFEEDBACK");
    Map<String, Object> feedBack = (Map<String, Object>) baseService.get(backP);
    if(feedBack==null){
      throw new GlobalErrorException("999889", "线索不存在");
    }
    if ("result".equals(body.get("type"))) {
      if (body.get("qbxsId") == null) {
        throw new GlobalErrorException("999889", "线索id不能为空");
      }
      String qbxsId = String.valueOf(body.get("qbxsId"));
      Map<String, Object> bean = new HashMap<>();
      bean.put("qbxsResult", body.get("qbxsResult"));
      bean.put("ids", qbxsId);
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJGLQBXSBASEBATCHUPDATE");
      baseService.update(qbxsId, bean);
    }

    if ("saveSyajs".equals(body.get("type"))) {
      Map<String, Object> param = new HashMap<>();
      if (feedBack.get("syajs") == null || StringUtils.isEmpty(feedBack.get("syajs"))) {
        JSONArray array = new JSONArray();
        array.add(body.get("syajs"));
        param.put("syajs", JSONArray.toJSONString(array));
      } else {
        JSONArray array = JSONArray.parseArray(String.valueOf(feedBack.get("syajs")));
        if(!array.contains(body.get("syajs"))){
          array.add(body.get("syajs"));
        }
        param.put("syajs", JSONArray.toJSONString(array));
      }
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJCLUSTERFEEDBACK");
      baseService.update(fbId, param);
    }
    if ("deleteSyajs".equals(body.get("type"))) {
      Map<String, Object> param = new HashMap<>();
      if (feedBack.get("syajs") != null && !StringUtils.isEmpty(feedBack.get("syajs"))) {
        JSONArray array = JSONArray.parseArray(String.valueOf(feedBack.get("syajs")));
        array.remove(String.valueOf(body.get("syajs")));
        param.put("syajs", JSONArray.toJSONString(array));
        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJCLUSTERFEEDBACK");
        baseService.update(fbId, param);
      }
    }
    //案件编号,捣毁窝点,涉案金额,批准逮捕
    if ("saveZbxss".equals(body.get("type"))) {
      Map<String, Object> param = new HashMap<>();
      if (feedBack.get("zbxss") == null || StringUtils.isEmpty(feedBack.get("zbxss"))) {
        String[] arr = String.valueOf(body.get("zbxss")).split(",");
        Map<String, Object> zbxss = new HashMap<>();
        zbxss.put(arr[0],body.get("zbxss"));
        param.put("zbxss", JSONObject.toJSONString(zbxss));
      } else {
        String[] arr = String.valueOf(body.get("zbxss")).split(",");
        JSONObject zbxss = JSONObject.parseObject(String.valueOf(feedBack.get("zbxss")));
        zbxss.put(arr[0],body.get("zbxss"));
        param.put("zbxss", JSONObject.toJSONString(zbxss));
      }
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJCLUSTERFEEDBACK");
      baseService.update(fbId, param);
    }
    if ("deleteZbxss".equals(body.get("type"))) {
      Map<String, Object> param = new HashMap<>();
      if (feedBack.get("zbxss") != null && !StringUtils.isEmpty(feedBack.get("zbxss"))) {
        String[] arr = String.valueOf(body.get("zbxss")).split(",");
        JSONObject zbxss = JSONObject.parseObject(String.valueOf(feedBack.get("zbxss")));
        zbxss.remove(arr[0]);
        param.put("zbxss", JSONObject.toJSONString(zbxss));
        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJCLUSTERFEEDBACK");
        baseService.update(fbId, param);
      }
    }
    return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
  }
}