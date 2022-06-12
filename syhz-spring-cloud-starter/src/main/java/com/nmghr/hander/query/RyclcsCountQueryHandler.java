package com.nmghr.hander.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractQueryHandler;

@Service("ryclcscountQueryHandler")
public class RyclcsCountQueryHandler extends AbstractQueryHandler {

  public RyclcsCountQueryHandler(IBaseService baseService) {
    super(baseService);
  }

  @SuppressWarnings("unchecked")
  @Override
  public Object list(Map<String, Object> map) throws Exception {
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "RYCLCSCT");//人员处理措施统计
    List<Map<String, Object>> result = (List<Map<String, Object>>) baseService.list(map);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "ZHRYCT");
    Map<String, Object> zh = (Map<String, Object>) baseService.get(map);//抓获
    if(StringUtils.isEmpty(zh)) {
      zh=new HashMap<String, Object>();
      zh.put("count", 0);
    }
    zh.put("cslb", "1001");
    result.add(zh);
    return result;
  }

}
