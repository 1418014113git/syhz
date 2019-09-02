package com.nmghr.hander.save;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.page.Paging;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractQueryHandler;
import com.nmghr.basic.core.service.handler.impl.AbstractSaveHandler;

@Service("specialtaskcaseSaveHandler")
public class SpecialTasksCaseSaveHandler extends AbstractSaveHandler {

  public SpecialTasksCaseSaveHandler(IBaseService baseService) {
    super(baseService);
  }

  @Override
  @Transactional
  public Object save(Map<String, Object> requestBody) throws Exception {
    List caseIds = (List) requestBody.get("caseIds");
    if (caseIds != null && caseIds.size() > 0) {
      for (Iterator iterator = caseIds.iterator(); iterator.hasNext();) {
        Object object = (Object) iterator.next();
        requestBody.put("caseId", object);
        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "SPECIALTASKCASE");//统一防止LocalThread被修改
        baseService.save(requestBody);
      }
    }
    if (requestBody.get("bizId") != null && !"".equals(requestBody.get("bizId"))) {
      saveTimeline(requestBody);
    }
    return "OK";
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
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "BUSINESSLOG");
    return baseService.save(params);
  }

}
