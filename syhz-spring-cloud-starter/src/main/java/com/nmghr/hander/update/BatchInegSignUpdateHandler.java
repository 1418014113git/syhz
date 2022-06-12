package com.nmghr.hander.update;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractUpdateHandler;

@Service("batchinegsignUpdateHandler")
public class BatchInegSignUpdateHandler extends AbstractUpdateHandler {

  private final String INVESTIGATION_SIGN = "INVESTIGATIONSIGN";

  public BatchInegSignUpdateHandler(IBaseService baseService) {
    super(baseService);
  }

  @Override
  @Transactional
  public Object update(String id, Map<String, Object> requestBody) throws Exception {
    List<Object> list = (List<Object>) requestBody.get("params");
    if (list == null || list.size() < 1) {
      return null;
    }
    for (int i = 0; i < list.size(); i++) {
      Map<String, Object> data = (Map<String, Object>) list.get(i);
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, INVESTIGATION_SIGN);
      baseService.update(String.valueOf(data.get("id")), data);
    }
    return null;
  }
}
