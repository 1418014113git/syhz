package com.nmghr.hander.query;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractQueryHandler;

@Service("workexamineQueryHandler")
public class WorkExamineQueryHandle extends AbstractQueryHandler {

  public WorkExamineQueryHandle(IBaseService baseService) {
    super(baseService);
  }

  @SuppressWarnings("unchecked")
  @Override
  public Object get(String id) throws Exception {
    // 查询工作单 传入部门判断是否正确
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "WORKORDERFLOW");
    Map<String, Object> flow = (Map<String, Object>) baseService.get(id);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "WORKORDER");
    Map<String, Object> order =
        (Map<String, Object>) baseService.get(String.valueOf(flow.get("wd_id")));
    String table = (String) order.get("association_table");
    Map<String, Object> detail = new HashMap<String, Object>();
    if("AJFLWS".equals(table)) {
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJFLWSAJINFO");
      Object obj =  baseService.get(String.valueOf(order.get("association_value")));
      detail.put("order", order);
      detail.put("ajInfo", obj);
    } else {
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, table.toUpperCase());
      Object obj =  baseService.get(String.valueOf(order.get("association_value")));
      if (obj != null) {
        detail = (Map<String, Object>) obj;
      }
    }
    detail.put("flow", flow);
    detail.put("type", table);
    return detail;
  }

}
