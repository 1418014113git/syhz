package com.nmghr.hander.update;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractUpdateHandler;
/**
 * 检验鉴定批量签收
 *
 * @author weber  
 * @date 2018年10月26日 下午2:47:05 
 * @version 1.0
 */
@Service("batchinspectsignUpdateHandler")
public class BatchInspectSignUpdateHandler extends AbstractUpdateHandler {

  private final String AUTHENTICATE_APPECT = "AUTHENTICATEAPPECT";

  public BatchInspectSignUpdateHandler(IBaseService baseService) {
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
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, AUTHENTICATE_APPECT);
      baseService.update(String.valueOf(data.get("id")), data);
    }
    return null;
  }

}
