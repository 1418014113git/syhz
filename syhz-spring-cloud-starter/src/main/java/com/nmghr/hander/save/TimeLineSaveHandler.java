package com.nmghr.hander.save;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractSaveHandler;
import com.nmghr.util.LogQueueThread;

@Service("timelineSaveHandler")
public class TimeLineSaveHandler extends AbstractSaveHandler {

  public TimeLineSaveHandler(IBaseService baseService) {
    super(baseService);
  }

  @Override
  @Transactional
  public Object save(Map<String, Object> requestBody) throws Exception {
    String operatorName = String.valueOf(requestBody.get("TimeLineAlias"));
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, operatorName);
    Object id = baseService.save(requestBody);// 保存原业务
    if (requestBody.get("bizId") == null || "".equals(requestBody.get("bizId"))) {
      requestBody.put("bizId", id);
    }
    return saveTimeline(requestBody);
  }

  /**
   * 保存时间轴数据
   */
  private Object saveTimeline(Map<String, Object> requestBody) throws Exception {
    Map<String, Object> params = new HashMap<String, Object>();
    params.put("bizType", requestBody.get("bizType"));
    params.put("action", requestBody.get("action"));
    params.put("bizId", requestBody.get("bizId"));
    params.put("userId", requestBody.get("userId"));
    params.put("userName", requestBody.get("userName"));
    LogQueueThread.add(params);
    return "";
  }
}
