package com.nmghr.hander.save.cluster;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractSaveHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
@Service("deptMapperSaveHandler")
public class DeptMapperSaveHandler extends AbstractSaveHandler {

  public DeptMapperSaveHandler(IBaseService baseService) {
    super(baseService);
  }

  @Override
  @Transactional
  public Object save(Map<String, Object> body) throws Exception {
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJCLUSTERASSISTDEPT");
    List<Map<String, Object>> rs = (List<Map<String, Object>>) baseService.list(body);
    if (rs == null || rs.size() == 0) {
      LocalThreadStorage.put(Constant.CONTROLLER_AUTO_INCREMENT, true);
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJCLUSTERASSISTDEPT");
      return baseService.save(body);
    }
    Map<String, Object> map = rs.get(0);
    return map.get("id");
  }
}
