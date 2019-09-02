/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.nmghr.hander.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractQueryHandler;
import com.nmghr.util.ListPageUtil;

/**
 * <功能描述/>
 *
 * @author wujin
 * @date 2018年10月10日 下午4:13:47
 * @version 1.0
 */
@Service("itemSearchQueryHandler")
public class AdvancedQueryHandler extends AbstractQueryHandler {

  public AdvancedQueryHandler(IBaseService baseService) {
    super(baseService);
  }

  @Autowired
  DepService depService;

  @SuppressWarnings("unchecked")
  @Override
  public Object list(Map<String, Object> requestBody) throws Exception {
    int pageNum = 1;
    if(requestBody.get("pageNum")!=null) {
      pageNum = Integer.parseInt(requestBody.get("pageNum").toString());
    }
    int pageSize = 10;
    if(requestBody.get("pageSize")!=null) {
      pageSize = Integer.parseInt(requestBody.get("pageSize").toString());
    }
    Object seniorForm = requestBody.get("seniorForm");
    Map<String, Object> request = (Map<String, Object>) JSON.parse(seniorForm.toString());
    Object condition = request.get("condition");
    List<Map<String, Object>> configlist = config(request);
    Map<String, Object> result = new HashMap<String, Object>();
    if (condition.equals("aj")) {
      List<Map<String, Object>> ajconfiglist = new ArrayList<>();
      for (int i = 0; i < configlist.size(); i++) {
        Map<String, Object> map = configlist.get(i);
        if (map.get("business_type").equals("1")) {
          ajconfiglist.add(map);
        }
      }
      List<Map<String, Object>> list = new ArrayList<>();
      if (ajconfiglist.size() == 0) {
        list = ajcx(request);
      } else {
        list = ajpzcx(requestBody, configlist);
      }
      if (null != list && list.size() != 0) {
        ListPageUtil<Map<String, Object>> listPageUtil =
            new ListPageUtil<Map<String, Object>>(list, pageNum, pageSize);
        List<Map<String, Object>> pagedList = listPageUtil.getPagedList();
        result.put("AJ", pagedList);
        result.put("AJCOUNT", list.size());
      } else {
        result.put("AJ", null);
        result.put("AJCOUNT", 0);
      }
    } else if (condition.equals("xyr")) {
      List<Map<String, Object>> xyrconfiglist = new ArrayList<>();
      for (int i = 0; i < configlist.size(); i++) {
        Map<String, Object> map = configlist.get(i);
        if (map.get("business_type").equals("2")) {
          xyrconfiglist.add(map);
        }
      }
      List<Map<String, Object>> xyrlist = new ArrayList<>();
      if (xyrconfiglist.size() == 0) {
        xyrlist = xyrcx(request);
      } else {
        xyrlist = xyrpzcx(requestBody, configlist);
      }
      if (xyrlist.size() != 0) {
        ListPageUtil<Map<String, Object>> listPageUtil =
            new ListPageUtil<Map<String, Object>>(xyrlist, pageNum, pageSize);
        List<Map<String, Object>> pagedList = listPageUtil.getPagedList();
        result.put("XYR", pagedList);
        result.put("XYRCOUNT", xyrlist.size());
      } else {
        result.put("XYR", null);
        result.put("XYRCOUNT", 0);
      }
    } else if (condition.equals("qbxs")) {
      List<Map<String, Object>> qbxsconfiglist = new ArrayList<>();
      for (int i = 0; i < configlist.size(); i++) {
        Map<String, Object> map = configlist.get(i);
        if (map.get("business_type").equals("4")) {
          qbxsconfiglist.add(map);
        }
      }
      List<Map<String, Object>> qbxslist = new ArrayList<>();
      if (qbxsconfiglist.size() == 0) {
        qbxslist = qbxscx(request);
      } else {
        qbxslist = qbxspzcx(requestBody, configlist);
      }
      if (qbxslist.size() != 0) {
        ListPageUtil<Map<String, Object>> listPageUtil =
            new ListPageUtil<Map<String, Object>>(qbxslist, pageNum, pageSize);
        List<Map<String, Object>> pagedList = listPageUtil.getPagedList();
        result.put("QBXS", pagedList);
        result.put("QBXSCOUNT", qbxslist.size());
      } else {
        result.put("QBXS", null);
        result.put("QBXSCOUNT", 0);
      }

    } else if (condition.equals("ajgldw")) {
      List<Map<String, Object>> ajgldwconfiglist = new ArrayList<>();
      for (int i = 0; i < configlist.size(); i++) {
        Map<String, Object> map = configlist.get(i);
        if (map.get("business_type").equals("3")) {
          ajgldwconfiglist.add(map);
        }
      }
      List<Map<String, Object>> ajgldwlist = new ArrayList<>();
      if (ajgldwconfiglist.size() == 0) {
        ajgldwlist = ajgldwcx(request);
      } else {
        ajgldwlist = ajgldwpzcx(requestBody, configlist);
      }
      if (ajgldwlist.size() != 0) {
        ListPageUtil<Map<String, Object>> listPageUtil =
            new ListPageUtil<Map<String, Object>>(ajgldwlist, pageNum, pageSize);
        List<Map<String, Object>> pagedList = listPageUtil.getPagedList();
        result.put("AJGLDW", pagedList);
        result.put("AJGLDWCOUNT", ajgldwlist.size());
      } else {
        result.put("AJGLDW", null);
        result.put("AJGLDWCOUNT", 0);
      }
    }
    result.put("pageNum", pageNum);
    result.put("pageSize", pageSize);
    return result;
  }

  @SuppressWarnings("unchecked")
  public List<Map<String, Object>> ajcx(Map<String, Object> requestBody) throws Exception {
    if (requestBody.get("RLDW")!=null || requestBody.get("depCode")!=null ) {
      String depCode = "";
      if(requestBody.get("RLDW")!=null) {
        depCode = String.valueOf(requestBody.get("RLDW"));
      } else {
        depCode = String.valueOf(requestBody.get("depCode"));
      }
      Map<String, Object> reqmap = new HashMap<>();
      reqmap.put("deptCode", depCode);
      List<Map<String, Object>> deplist=(List<Map<String, Object>>) depService.list(reqmap);
      if (deplist != null && deplist.size() > 0 && deplist.get(0) !=null) {
        requestBody.put("ids", deplist.get(0).get("ids"));
      }
    }
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "CASEADVANCEDNEW");
    return (List<Map<String, Object>>) baseService.list(requestBody);
  }
  // @SuppressWarnings("unchecked")
  // public List<Map<String, Object>> ajcx(Map<String, Object> requestBody) throws Exception {
  // Object requestSign=requestBody.get("requestSign");
  // String depCode=requestBody.get("depCode")+"";
  // Object rldw = requestBody.get("RLDW");
  // List<Map<String, Object>> ajlist = new ArrayList<>();
  // List<Map<String, Object>> reslist = new ArrayList<>();
  // if (!StringUtils.isEmpty(rldw)) {
  // Map<String, Object> reqmap=new HashMap<>();
  // reqmap.put("RLDWCODE", rldw);
  // Map<String, Object> depidmap = (Map<String, Object>) depService.get(reqmap);
  // requestBody.put("ids", depidmap.get("ids"));
  // if(!StringUtils.isEmpty(depidmap)) {
  // if(requestSign.equals("city")) {
  // String cityCode=depCode.substring(0,6);
  // Map<String, Object> map=new HashMap<>();
  // map.put("cityCode", cityCode);
  // Object depids=depService.list(map);
  // List<Map<String, Object>> deplist=(List<Map<String, Object>>) depids;
  // String ids=deplist.get(0).get("ids")+"";
  // depidmap.put("ztids", ids);
  // }
  // if(requestSign.equals("area")) {
  // Map<String, Object> map=new HashMap<>();
  // map.put("areaCode", depCode);
  // Object depids=depService.list(map);
  // List<Map<String, Object>> deplist=(List<Map<String, Object>>) depids;
  // String ids=deplist.get(0).get("ids")+"";
  // depidmap.put("ztids", ids);
  // }
  // LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "DEPCASEQUERY");// 认领单位反查案件
  // Object ajobj = baseService.list(depidmap);
  // ajlist = (List<Map<String, Object>>) ajobj;
  // }
  // }
  // if(requestSign.equals("city")) {
  // String cityCode=depCode.substring(0,6);
  // Map<String, Object> map=new HashMap<>();
  // map.put("cityCode", cityCode);
  // Object depids=depService.list(map);
  // List<Map<String, Object>> deplist=(List<Map<String, Object>>) depids;
  // String ids=deplist.get(0).get("ids")+"";
  // requestBody.put("ztids", ids);
  // }
  // if(requestSign.equals("area")) {
  // Map<String, Object> map=new HashMap<>();
  // map.put("areaCode", depCode);
  // Object depids=depService.list(map);
  // List<Map<String, Object>> deplist=(List<Map<String, Object>>) depids;
  // String ids=deplist.get(0).get("ids")+"";
  // requestBody.put("ztids", ids);
  // }
  // LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "CASEADVANCED");// 案件默认高级查询
  // Object obj = baseService.list(requestBody);
  // List<Map<String, Object>> list = (List<Map<String, Object>>) obj;
  // if (!StringUtils.isEmpty(rldw)) {
  // for (int i = 0; i < ajlist.size(); i++) {
  // Map<String, Object> map = ajlist.get(i);
  // Object id = map.get("ID");
  // boolean bol = false;
  // for (int j = 0; j < list.size(); j++) {
  // Map<String, Object> newmap = list.get(j);
  // Object newid = newmap.get("ID");
  // if (id.equals(newid)) {
  // bol = true;
  // }
  // }
  // if (bol) {
  // reslist.add(map);
  // }
  // }
  // } else {
  // reslist = list;
  // }
  // for (int i = 0; i < reslist.size(); i++) {
  // Map<String, Object> map = reslist.get(i);
  // Object id = map.get("OID");
  // Object param = requestBody.get("param");
  // map.put("param", param);
  // if (!StringUtils.isEmpty(id)) {
  // map.put("id", id);
  // Map<String, Object> depmap = (Map<String, Object>) depService.get(map);
  // map.put("RLBM", depmap.get("depname") + "");
  // } else {
  // map.put("RLBM", "");
  // }
  // }
  // return reslist;
  // }

  @SuppressWarnings("unchecked")
  public List<Map<String, Object>> ajpzcx(Map<String, Object> requestBody,
      List<Map<String, Object>> configlist) throws Exception {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < configlist.size(); i++) {
      Map<String, Object> configmap = configlist.get(i);
      Object businessType = configmap.get("business_type");
      Object isShow = configmap.get("is_show");
      Object columnName = configmap.get("column_name");
      if (businessType.equals("1") && isShow.equals(true)) {
        if (sb.length() > 0) {
          sb.append(",");
        }
        sb.append("s." + columnName);
      }
    }
    if (sb.length() == 0) {
      return null;
    } else {
      requestBody.put("query", sb);
      Object seniorForm = requestBody.get("seniorForm");
      Map<String, Object> request = (Map<String, Object>) JSON.parse(seniorForm.toString());
      requestBody.putAll(request);
      if (requestBody.get("RLDW") != null || requestBody.get("depCode") !=null) {
        String depCode = "";
        if(requestBody.get("RLDW")!=null) {
          depCode = String.valueOf(requestBody.get("RLDW"));
        } else {
          depCode = String.valueOf(requestBody.get("depCode"));
        }
        Map<String, Object> reqmap = new HashMap<>();
        reqmap.put("deptCode", depCode);
        List<Map<String, Object>> deplist=(List<Map<String, Object>>) depService.list(reqmap);
        if (deplist != null && deplist.size() > 0 && deplist.get(0) != null) {
          requestBody.put("ids", deplist.get(0).get("ids"));
        }
      }
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJCONFIGADVANCEDNEW");// 案件配置高级查询
      Object obj = baseService.list(requestBody);
      List<Map<String, Object>> list = (List<Map<String, Object>>) obj;
      return list;
    }
  }
  
//  @SuppressWarnings("unchecked")
//  public List<Map<String, Object>> ajpzcx(Map<String, Object> requestBody,
//      List<Map<String, Object>> configlist) throws Exception {
//    StringBuilder sb = new StringBuilder();
//    for (int i = 0; i < configlist.size(); i++) {
//      Map<String, Object> configmap = configlist.get(i);
//      Object businessType = configmap.get("business_type");
//      Object isShow = configmap.get("is_show");
//      Object columnName = configmap.get("column_name");
//      if (businessType.equals("1") && isShow.equals(true)) {
//        if (sb.length() > 0) {
//          sb.append(",");
//        }
//        sb.append("s." + columnName);
//      }
//    }
//    if (sb.length() == 0) {
//      return null;
//    } else {
//      requestBody.put("query", sb);
//      Object seniorForm = requestBody.get("seniorForm");
//      Map<String, Object> request = (Map<String, Object>) JSON.parse(seniorForm.toString());
//      Object requestSign = request.get("requestSign");
//      String depCode = request.get("depCode") + "";
//      if (requestSign.equals("city")) {
//        String cityCode = depCode.substring(0, 6);
//        Map<String, Object> map = new HashMap<>();
//        map.put("cityCode", cityCode);
//        Object depids = depService.list(map);
//        List<Map<String, Object>> deplist = (List<Map<String, Object>>) depids;
//        String ids = deplist.get(0).get("ids") + "";
//        requestBody.put("ztids", ids);
//      }
//      if (requestSign.equals("area")) {
//        Map<String, Object> map = new HashMap<>();
//        map.put("areaCode", depCode);
//        Object depids = depService.list(map);
//        List<Map<String, Object>> deplist = (List<Map<String, Object>>) depids;
//        String ids = deplist.get(0).get("ids") + "";
//        requestBody.put("ztids", ids);
//      }
//      requestBody.putAll(request);
//      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJCONFIGADVANCED");// 案件配置高级查询
//      Object obj = baseService.list(requestBody);
//      List<Map<String, Object>> list = (List<Map<String, Object>>) obj;
//      return list;
//    }
//  }

  @SuppressWarnings("unchecked")
  public List<Map<String, Object>> xyrcx(Map<String, Object> requestBody) throws Exception {
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "XYRADVANCED");// 嫌疑人默认高级查询
    Object xyrobj = baseService.list(requestBody);
    List<Map<String, Object>> xyrlist = (List<Map<String, Object>>) xyrobj;
    return xyrlist;
  }

  @SuppressWarnings("unchecked")
  public List<Map<String, Object>> xyrpzcx(Map<String, Object> requestBody,
      List<Map<String, Object>> configlist) throws Exception {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < configlist.size(); i++) {
      Map<String, Object> configmap = configlist.get(i);
      Object businessType = configmap.get("business_type");
      Object isShow = configmap.get("is_show");
      Object columnName = configmap.get("column_name");
      if (businessType.equals("2") && isShow.equals(true)) {
        if (sb.length() > 0) {
          sb.append(",");
        }
        sb.append(columnName);
      }
    }
    if (sb.length() == 0) {
      return null;
    } else {
      requestBody.put("columnQuery", sb);
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "XYRCONFIGADVANCED");// 嫌疑人配置高级查询
      Object xyrobj = baseService.list(requestBody);
      List<Map<String, Object>> xyrlist = (List<Map<String, Object>>) xyrobj;
      return xyrlist;
    }
  }

  @SuppressWarnings("unchecked")
  public List<Map<String, Object>> qbxscx(Map<String, Object> requestBody) throws Exception {
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "QBXSADVANCED");// 情报线索默认高级查询
    Object qbxsobj = baseService.list(requestBody);
    List<Map<String, Object>> qbxslist = (List<Map<String, Object>>) qbxsobj;
    return qbxslist;
  }

  @SuppressWarnings("unchecked")
  public List<Map<String, Object>> qbxspzcx(Map<String, Object> requestBody,
      List<Map<String, Object>> configlist) throws Exception {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < configlist.size(); i++) {
      Map<String, Object> configmap = configlist.get(i);
      Object businessType = configmap.get("business_type");
      Object isShow = configmap.get("is_show");
      Object columnName = configmap.get("column_name");
      if (businessType.equals("4") && isShow.equals(true)) {
        if (sb.length() > 0) {
          sb.append(",");
        }
        sb.append(columnName);
      }
    }
    if (sb.length() == 0) {
      return null;
    } else {
      requestBody.put("query", sb);
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "QBXSCONFIGADVANCED");// 情报线索配置高级查询
      Object qbxsobj = baseService.list(requestBody);
      List<Map<String, Object>> qbxslist = (List<Map<String, Object>>) qbxsobj;
      return qbxslist;
    }
  }

  @SuppressWarnings("unchecked")
  public List<Map<String, Object>> ajgldwcx(Map<String, Object> requestBody) throws Exception {
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJGLDWADVANCED");// 案件关联单位默认查询
    Object ajgldwobj = baseService.list(requestBody);
    List<Map<String, Object>> ajgldwlist = (List<Map<String, Object>>) ajgldwobj;
    return ajgldwlist;
  }

  @SuppressWarnings("unchecked")
  public List<Map<String, Object>> ajgldwpzcx(Map<String, Object> requestBody,
      List<Map<String, Object>> configlist) throws Exception {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < configlist.size(); i++) {
      Map<String, Object> configmap = configlist.get(i);
      Object businessType = configmap.get("business_type");
      Object isShow = configmap.get("is_show");
      Object columnName = configmap.get("column_name");
      if (businessType.equals("3") && isShow.equals(true)) {
        if (sb.length() > 0) {
          sb.append(",");
        }
        sb.append(columnName);
      }
    }
    if (sb.length() == 0) {
      return null;
    } else {
      requestBody.put("query", sb);
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJGLDWCONFIGADVANCED");// 案件关联单位配置高级查询
      Object ajgldwobj = baseService.list(requestBody);
      List<Map<String, Object>> ajgldwlist = (List<Map<String, Object>>) ajgldwobj;
      return ajgldwlist;
    }
  }

  @SuppressWarnings("unchecked")
  public List<Map<String, Object>> config(Map<String, Object> requestBody) throws Exception {
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "CONFIGQUERY");// 用户配置查询
    Object configobj = baseService.list(requestBody);
    List<Map<String, Object>> configlist = (List<Map<String, Object>>) configobj;
    return configlist;
  }

//  @SuppressWarnings({"unused", "unchecked"})
//  private List<Map<String, Object>> getLastList(Map<String, Object> requestBody,
//      List<Map<String, Object>> list) throws Exception {
//    Object requestSign = requestBody.get("requestSign");
//    String depCode = requestBody.get("depCode") + "";
//    String ztids = "101,102,201,401,402";
//    if (requestSign.equals("province")) {
//      return list;
//    } else if (requestSign.equals("city")) {
//      String cityCode = depCode.substring(0, 6);
//      Map<String, Object> map = new HashMap<>();
//      map.put("cityCode", cityCode);
//      Object depids = depService.list(map);
//      List<Map<String, Object>> deplist = (List<Map<String, Object>>) depids;
//      String ids = deplist.get(0).get("ids") + "";
//      Iterator<Map<String, Object>> it = list.iterator();
//      while (it.hasNext()) {
//        Map<String, Object> ajmap = it.next();
//        String depid = ajmap.get("OID") + "";
//        String ajzt = ajmap.get("AJZT") + "";
//        if (ids.contains(depid) && (!ztids.contains(ajzt))) {
//          list.remove(ajmap);
//        }
//      }
//      return list;
//    } else if (requestSign.equals("area")) {
//      Map<String, Object> map = new HashMap<>();
//      map.put("areaCode", depCode);
//      Object depids = depService.list(map);
//      List<Map<String, Object>> deplist = (List<Map<String, Object>>) depids;
//      String ids = deplist.get(0).get("ids") + "";
//      Iterator<Map<String, Object>> it = list.iterator();
//      while (it.hasNext()) {
//        Map<String, Object> ajmap = it.next();
//        String depid = ajmap.get("OID") + "";
//        String ajzt = ajmap.get("AJZT") + "";
//        if (ids.contains(depid) && (!ztids.contains(ajzt))) {
//          list.remove(ajmap);
//        }
//      }
//      return list;
//    } else {
//      return null;
//    }
//  }
}
