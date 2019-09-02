package com.nmghr.hander.query;

import java.util.Calendar;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractQueryHandler;

@Service("heightcasetypeQueryHandler")
public class HeightCaseType extends AbstractQueryHandler {

  public HeightCaseType(IBaseService baseService) {
    super(baseService);
  }

  @Override
  public Object list(Map<String, Object> requestMap) throws Exception {
    Calendar now = Calendar.getInstance();
    int year = now.get(Calendar.YEAR);
    int month = now.get(Calendar.MONTH) + 1;
//    requestMap.put("nowMoth", year + "-" + String.format("%02d", month));
//    requestMap.put("beforeYearMoth", (year - 1) + "-" + String.format("%02d", month));
//    requestMap.put("beforeMoth", year + "-" + String.format("%02d", month - 1));
    requestMap.put("nowMoth", year);
    requestMap.put("beforeYearMoth", (year - 1));
    return baseService.list(requestMap);
  }

}
