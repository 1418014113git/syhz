package com.nmghr.hander.query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.page.Paging;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractQueryHandler;
import com.nmghr.controller.vo.BusinessBean;
import com.nmghr.util.ListPageUtil;

/**
 * <功能描述/>
 *
 * @author weber
 * @date 2018年10月17日 下午4:00:57
 * @version 1.0
 */
@Service("messagesQueryHandler")
public class MessagesQueryHandler extends AbstractQueryHandler {

  public MessagesQueryHandler(IBaseService baseService) {
    super(baseService);
  }

  @Override
  public Object page(Map<String, Object> requestMap, int currentPage, int pageSize)
      throws Exception {
    Map<String, Object> result = new HashMap<String, Object>();
    if (requestMap.get("type") == null || "".equals(requestMap.get("type"))) {
      result.put("list", new ArrayList());
      result.put("totalCount", 0);
      return result;
    }
    int type = Integer.parseInt(String.valueOf(requestMap.get("type")));
    if (type == 0) {
      int total = 0;
      List<BusinessBean> list = new ArrayList<BusinessBean>();
      list.addAll(getBusinessData(requestMap, "AJJBXXETLRL", currentPage, pageSize, 1));
      list.addAll(getBusinessData(requestMap, "DBAJ", currentPage, pageSize, 3));
      list.addAll(getBusinessData(requestMap, "INVESTIGATION", currentPage, pageSize, 4));
      list.addAll(getBusinessData(requestMap, "CASEASSIST", currentPage, pageSize, 5));
      list.addAll(getBusinessData(requestMap, "SPECIALTASKSIGN", currentPage, pageSize, 6));
      list.addAll(getBusinessData(requestMap, "QBXSJBXX", currentPage, pageSize, 7));
      total = list.size();
      Collections.sort(list, new Comparator<BusinessBean>() {
        public int compare(BusinessBean p1, BusinessBean p2) {
          Date date1 = (Date) p1.getCreateTime();
          Date date2 = (Date) p2.getCreateTime();
          return date1.compareTo(date2) * -1;
        }
      });
      int fromIndex = (currentPage - 1) * pageSize;
      if (fromIndex >= list.size()) {
        return Collections.emptyList();// 空数组
      }
      if (fromIndex < 0) {
        return Collections.emptyList();// 空数组
      }
      int toIndex = currentPage * pageSize;
      if (toIndex >= list.size()) {
        toIndex = list.size();
      }
      List array = list.subList(fromIndex, toIndex);
      return new Paging(pageSize, currentPage, total, array);
    }
    String ALIAS = "";
    // 案件认领
    if (type == 1) {
      ALIAS = "AJJBXXETLRL";
    }
    // 案件督办
    if (type == 3) {
      ALIAS = "DBAJ";
    }
    // 全国协查
    if (type == 4) {
      ALIAS = "INVESTIGATION";
    }
    // 案件协查
    if (type == 5) {
      ALIAS = "CASEASSIST";
    }
    // 专项任务
    if (type == 6) {
      ALIAS = "SPECIALTASKSIGN";
    }
    // 情报线索
    if (type == 7) {
      ALIAS = "QBXSJBXX";
    }
    List<Object> ids = getIds(requestMap, ALIAS);
    return getPaging(currentPage, pageSize, ids, type);
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
  @SuppressWarnings("unchecked")
  private List<Object> getIds(Map<String, Object> requestMap, String alias) throws Exception {
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, alias);
    List<Object> ids = new ArrayList<Object>();
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
   * 获取业务信息数据
   * 
   * @param currentPage
   * @param pageSize
   * @param ids
   * @return
   * @throws Exception
   */
  private Object getPaging(int currentPage, int pageSize, List<Object> ids, int type)
      throws Exception {
    if (ids == null || ids.size() == 0) {
      return null;
    }
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "MESSAGES");
    Map<String, Object> params = new HashMap<String, Object>();
    params.put("ids", ids);
    params.put("bizType", type);
    return baseService.page(params, currentPage, pageSize);
  }

  private List<BusinessBean> getBusinessData(Map<String, Object> requestMap, String alias,
      int currentPage, int pageSize, int type) throws Exception {
//    List<Object> ids = getIds(requestMap, alias);
//    if (ids != null && ids.size() > 0) {
      Map<String, Object> params = new HashMap<String, Object>();
//      params.put("ids", ids);
      params.put("bizType", type);
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "MESSAGES");
      List<Map<String, Object>> result = (List<Map<String, Object>>) baseService.list(params);
      List<BusinessBean> array = new ArrayList<BusinessBean>();
      if (result != null && result.size() > 0) {
        for (int i = 0; i < result.size(); i++) {
          BusinessBean bb = new BusinessBean();
          Map<String, Object> map = result.get(i);
          if (map.get("action_type") != null) {
            bb.setActionType(String.valueOf(map.get("action_type")));
          }
          if (map.get("business_id") != null) {
            bb.setBusinessId(Integer.parseInt(String.valueOf(map.get("business_id"))));
          }
          if (map.get("business_type") != null) {
            bb.setBusinessType(Integer.parseInt(String.valueOf(map.get("business_type"))));
          }
          if (map.get("create_time") != null) {
            bb.setCreateTime((Date) map.get("create_time"));
          }
          if (map.get("create_user_id") != null) {
            bb.setCreateUserId(Integer.parseInt(String.valueOf(map.get("create_user_id"))));
          }
          if (map.get("create_user_name") != null) {
            bb.setCreateUserName(String.valueOf(map.get("create_user_name")));
          }
          if (map.get("id") != null) {
            bb.setId(Integer.parseInt(String.valueOf(map.get("id"))));
          }
          if (map.get("msg_status") != null) {
            bb.setMsgStatus(Integer.parseInt(String.valueOf(map.get("msg_status"))));
          }
          array.add(bb);
        }
        return array;
      }
//    }
    return new ArrayList();
  }
}
