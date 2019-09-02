package com.nmghr.hander.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractQueryHandler;

/**
 * <功能描述/>
 *
 * @author weber
 * @date 2018年10月17日 下午4:00:57
 * @version 1.0
 */
@Service("messagescountQueryHandler")
public class MessagesCountQueryHandler extends AbstractQueryHandler {

  public MessagesCountQueryHandler(IBaseService baseService) {
    super(baseService);
  }

  @Override
  public Object list(Map<String, Object> requestMap) throws Exception {
    int total = 0;
    total += getBusinessData(requestMap, "AJJBXXETLRL", 1);
    total += getBusinessData(requestMap, "DBAJ", 3);
    total += getBusinessData(requestMap, "INVESTIGATION", 4);
    total += getBusinessData(requestMap, "CASEASSIST", 5);
    total += getBusinessData(requestMap, "SPECIALTASKSIGN", 6);
    total += getBusinessData(requestMap, "QBXSJBXX", 7);
    return total;
  }

  /**
   * 获取业务ID
   * 
   * @param requestMap
   * @param currentPage
   * @param pageSize
   * @param alias
   * @return
   * @throws Exception
   */
  private List<Object> getIds(Map<String, Object> requestMap, String alias) throws Exception {
    List<Object> ids = new ArrayList();
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, alias);
    List<Map<String, Object>> result = (List<Map<String, Object>>) baseService.list(requestMap);
    if (result != null && result.size() > 0) {
      for (int i = 0; i < result.size(); i++) {
        Map<String, Object> bean = result.get(i);
        ids.add(bean.get("id"));
      }
    }
    return ids;
  }

  /**
   * 获取业务数量
   * 
   * @param requestMap
   * @param alias
   * @param currentPage
   * @param pageSize
   * @param type
   * @return
   * @throws Exception
   */
  private int getBusinessData(Map<String, Object> requestMap, String alias, int type)
      throws Exception {
//    List<Object> ids = getIds(requestMap, alias);
//    if (ids != null && ids.size() > 0) {}
    
      Map<String, Object> params = new HashMap<String, Object>();
      params.put("bizType", type);
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "MESSAGESCOUNT");
      List<Map<String, Object>> result = (List<Map<String, Object>>) baseService.list(params);
      if (result == null || result.size() == 0) {
        return 0;
      }
      if (result.get(0) == null) {
        return 0;
      }
      Map<String, Object> map = result.get(0);
      if (map.get("num") != null && !"".equals(map.get("num"))) {
        return Integer.valueOf(String.valueOf(map.get("num")));
      }
    return 0;
  }
}
