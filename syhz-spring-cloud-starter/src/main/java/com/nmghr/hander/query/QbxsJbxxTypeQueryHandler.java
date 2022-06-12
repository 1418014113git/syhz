package com.nmghr.hander.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.page.Paging;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractQueryHandler;

@Service("qbxsJbxxTypeEleQueryHandler")
public class QbxsJbxxTypeQueryHandler extends AbstractQueryHandler {

  public QbxsJbxxTypeQueryHandler(IBaseService baseService) {
    super(baseService);
  }

  @Override
  public Object list(Map<String, Object> requestMap) throws Exception {
    // 根据type从要素表查询出要素列表
    Map<String, Object> param = new HashMap<String, Object>();
    param.put("type", requestMap.get("type"));
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "QBXSJBXXTYPE");
    List<Map<String, Object>> list = (List<Map<String, Object>>) super.list(param);
    StringBuffer buffer = new StringBuffer();
    for (Iterator iterator = list.iterator(); iterator.hasNext();) {
      Map<String, Object> map = (Map<String, Object>) iterator.next();
      buffer.append(map.get("name") + ",");
    }
    requestMap.put("ppl", buffer.toString());
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJCOMPANYPPL");
    List<Map<String, Object>> cList = (List<Map<String, Object>>) super.list(requestMap);

    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJPERSONCOLLECTPPL");
    List<Map<String, Object>> pList = (List<Map<String, Object>>) super.list(requestMap);


    // 封装结果
    Map<String, Object> result = new HashMap<String, Object>();
    result.put("cData", cList);
    result.put("pData", pList);
    return result;
  }

  @Override
  public Object page(Map<String, Object> requestMap, int currentPage, int pageSize)
      throws Exception {
    // 根据type从要素表查询出要素列表
    Map<String, Object> param = new HashMap<String, Object>();
    param.put("type", requestMap.get("type"));
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "QBXSJBXXTYPE");
    List<Map<String, Object>> list = (List<Map<String, Object>>) super.list(param);
    StringBuffer buffer = new StringBuffer();
    for (Iterator iterator = list.iterator(); iterator.hasNext();) {
      Map<String, Object> map = (Map<String, Object>) iterator.next();
      buffer.append(map.get("name") + ",");
    }
    requestMap.put("ppl", buffer.toString());
    LocalThreadStorage.put(Constant.CONTROLLER_PAGE, true);
    LocalThreadStorage.put(Constant.CONTROLLER_PAGE_CURPAGE, currentPage);
    LocalThreadStorage.put(Constant.CONTROLLER_PAGE_PAGESIZE, pageSize);

    Object cData = null;
    Object pData = null;
    String qbxsId = (String) requestMap.get("qbxsId");
    if ("0".equals(requestMap.get("cpType"))) {
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJCOMPANYPPL");
      cData = super.page(requestMap, currentPage, pageSize);

      if (cData instanceof Paging) {
        long total = LocalThreadStorage.getLong(Constant.CONTROLLER_PAGE_TOTALCOUNT);
        ((Paging<?>) cData).setTotalCount(total);
      }
      // 保存线索-企业信息
      updateAbxsCompany(cData, qbxsId, requestMap, "QBXSJBXXCOMPANYCIDS", "QBXSJBXXCOMPANY",
          "companyId", "company_id");
    } else if ("1".equals(requestMap.get("cpType"))) {
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJPERSONCOLLECTPPL");
      pData = super.page(requestMap, currentPage, pageSize);

      if (pData instanceof Paging) {
        long total = LocalThreadStorage.getLong(Constant.CONTROLLER_PAGE_TOTALCOUNT);
        ((Paging<?>) pData).setTotalCount(total);
      }
      // 保存线索-人员信息
      updateAbxsCompany(pData, qbxsId, requestMap, "QBXSJBXXPERSONPIDS", "QBXSJBXXPERSON",
          "personId", "person_id");
    }



    // 封装结果
    Map<String, Object> result = new HashMap<String, Object>();
    result.put("cData", cData);
    result.put("pData", pData);

    return result;
  }

  /**
   * 
   * @param data
   * @param qbxsId
   * @param requestMap
   * @param queryTable //"QBXSJBXXCOMPANYCIDS"
   * @param saveTable //QBXSJBXXCOMPANY
   * @param idName //companyId
   * @param columnId //company_id
   * @throws Exception
   */
  private void updateAbxsCompany(Object data, String qbxsId, Map<String, Object> requestMap,
      String queryTable, String saveTable, String idName, String columnId) throws Exception {
    // 根据abxsId查找
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, queryTable);
    List<?> idList = (List<?>) super.list(requestMap);
    List<?> ids = getIdsFromComList(idList, columnId);

    Paging<?> d = (Paging<?>) data;

    List<Map<String, Object>> list = (List<Map<String, Object>>) d.getList();

    for (int i = 0; i < list.size(); i++) {
      if (isNotContain(list.get(i).get("id"), ids)) {
        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, saveTable);
        requestMap.put(idName, list.get(i).get("id"));
        baseService.save(requestMap);
      }
    }
  }

  private boolean isNotContain(Object id, List idList) {
    return !idList.contains(id);
  }

  private List getIdsFromComList(List<?> idList, String columnId) {
    List list = new ArrayList();
    for (Iterator iterator = idList.iterator(); iterator.hasNext();) {
      Map<String, Object> object = (Map<String, Object>) iterator.next();
      list.add(object.get(columnId));
    }
    return list;
  }
}
