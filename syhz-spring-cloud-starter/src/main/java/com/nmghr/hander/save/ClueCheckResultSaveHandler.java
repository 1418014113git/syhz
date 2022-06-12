package com.nmghr.hander.save;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractSaveHandler;

@Service("cluecheckinfoSaveHandler")
public class ClueCheckResultSaveHandler extends AbstractSaveHandler {

  public ClueCheckResultSaveHandler(IBaseService baseService) {
    super(baseService);
  }

  @Override
  @Transactional
  public Object save(Map<String, Object> requestBody) throws Exception {
    List<String> list = (List<String>) requestBody.get("clueCheckIds");
    for (int i = 0; i < list.size(); i++) {
      Map<String, Object> params = (Map<String, Object>) requestBody;
      params.put("clueCheckId", list.get(i));
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "CLUECHECKINFO");
      baseService.save(params);
    }
    return list.size();
  }

}
