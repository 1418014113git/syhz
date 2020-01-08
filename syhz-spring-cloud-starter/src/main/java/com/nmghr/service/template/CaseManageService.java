package com.nmghr.service.template;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author yj
 * @version 1.0
 * @date 2019/11/7 19:15
 **/
@Service("caseManageService")
public class CaseManageService {

  private final static String AJ_QUERY = "AJJBXXSYH";

  @Autowired
  @Qualifier("baseService")
  private IBaseService baseService;

  public Object page(Map<String, Object> requestBody) throws Exception {
    int currentPage = Integer.parseInt(String.valueOf(requestBody.get("pageNum")));
    int pageSize = Integer.parseInt(String.valueOf(requestBody.get("pageSize")));
    if(!StringUtils.isEmpty(requestBody.get("ajzt"))){
      String[] ajzts = String.valueOf(requestBody.get("ajzt")).split(",");
      if(ajzts.length>0){
        requestBody.put("ajzt",Arrays.asList(ajzts));
      }
    } else {
      requestBody.remove("ajzt");
    }
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, AJ_QUERY);
    return baseService.page(requestBody, currentPage, pageSize);
  }

}
