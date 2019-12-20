package com.nmghr.hander.query.ajglqbxs;

import com.nmghr.basic.common.exception.GlobalErrorException;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractQueryHandler;
import com.nmghr.service.ajglqbxs.AjglQbxsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;

@SuppressWarnings("unchecked")
@Service("ajglQbxsQueryHandler")
public class AjglQbxsQueryHandler extends AbstractQueryHandler {
  public AjglQbxsQueryHandler(IBaseService baseService) {
    super(baseService);
  }

  @Autowired
  private AjglQbxsService ajglQbxsService;

  @Override
  public Object list(Map<String, Object> requestMap) throws Exception {
    if (requestMap.get("pageNum") == null || StringUtils.isEmpty(requestMap.get("pageNum"))) {
      requestMap.put("pageNum", 1);
    }
    if (requestMap.get("pageSize") == null || StringUtils.isEmpty(requestMap.get("pageSize"))) {
      requestMap.put("pageNum", 15);
    }
    if(StringUtils.isEmpty(requestMap.get("queryType"))){
      throw new GlobalErrorException("999967","queryType不能为空");
    }
    return ajglQbxsService.ajglQbxsList(requestMap);
  }
}
