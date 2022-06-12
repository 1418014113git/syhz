package com.nmghr.hander.update;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractSaveHandler;
import com.nmghr.basic.core.service.handler.impl.AbstractUpdateHandler;

@Service("timelineUpdateHandler")
public class TimeLineUpdateHandler extends AbstractUpdateHandler {

  public TimeLineUpdateHandler(IBaseService baseService) {
    super(baseService);
  }

  @Override
  @Transactional
  public Object update(String id, Map<String, Object> requestBody) throws Exception {
    // 保存时间轴信息
    saveTimeline(requestBody);
    // 保存原业务信息
    String operatorName = String.valueOf(requestBody.get("TimeLineAlias"));
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, operatorName);
    return baseService.update(id, requestBody);
  }

  /** 保存时间轴数据 **/
  public void saveTimeline(Map<String, Object> requestBody) throws Exception {
    Map<String, Object> params = new HashMap<String, Object>();
    params.put("businessType", requestBody.get("businessType"));
    params.put("actionType", requestBody.get("actionType"));
    params.put("businessId", requestBody.get("businessId"));
    params.put("createUserId", requestBody.get("userId"));
    params.put("createUserName", requestBody.get("userName"));
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "BUSINESSLOG");
    baseService.save(params);
  }
}
