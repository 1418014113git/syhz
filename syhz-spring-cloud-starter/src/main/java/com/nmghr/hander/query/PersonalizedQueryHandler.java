package com.nmghr.hander.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractQueryHandler;
import com.nmghr.util.ListPageUtil;

@Service("personalizedQueryHandler")
public class PersonalizedQueryHandler extends AbstractQueryHandler {
  public PersonalizedQueryHandler(IBaseService baseService) {
    super(baseService);
  }

  @Autowired
  DepService depService;

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
    Object condition = requestBody.get("condition");
    Object param = requestBody.get("param");
    if (StringUtils.isEmpty(param)) {
      return null;
    }
    List<Map<String, Object>> configlist = config(requestBody);
    Map<String, Object> result = new HashMap<String, Object>();
    if (configlist.size() == 0) {
      // 如果无配置，则进行默认查询
      if (condition.equals("all")) {
        List<Map<String, Object>> reslist = new ArrayList<>();
        List<Map<String, Object>> list = ajcx(requestBody);
        List<Map<String, Object>> xyrlist = xyrcx(requestBody);
        List<Map<String, Object>> qbxslist = qbxscx(requestBody);
        List<Map<String, Object>> ajgldwlist = ajgldwcx(requestBody);
        List<Map<String, Object>> jcjlist = jcj(requestBody);
        if (!StringUtils.isEmpty(list)) {
          if (list.size() != 0) {
            reslist.addAll(list);
          }
        }
        if (!StringUtils.isEmpty(xyrlist)) {
          if (xyrlist.size() != 0) {
            reslist.addAll(xyrlist);
          }
        }
        if (!StringUtils.isEmpty(qbxslist)) {
          if (qbxslist.size() != 0) {
            reslist.addAll(qbxslist);
          }
        }
        if (!StringUtils.isEmpty(ajgldwlist)) {
          if (ajgldwlist.size() != 0) {
            reslist.addAll(ajgldwlist);
          }
        }
        if (!StringUtils.isEmpty(jcjlist)) {
          if (jcjlist.size() > 0) {
            reslist.addAll(jcjlist);
          }
        }
        if (reslist.size() != 0) {
          ListPageUtil<Map<String, Object>> listPageUtil =
              new ListPageUtil<Map<String, Object>>(reslist, pageNum, pageSize);
          List<Map<String, Object>> pagedList = listPageUtil.getPagedList();
          result.put("ALL", pagedList);
        } else {
          result.put("ALL", null);
        }
        if (!StringUtils.isEmpty(reslist)) {
          result.put("ALLCOUNT", reslist.size());
        } else {
          result.put("ALLCOUNT", 0);
        }
        if (!StringUtils.isEmpty(list)) {
          result.put("AJCOUNT", list.size());
        } else {
          result.put("AJCOUNT", 0);
        }
        if (!StringUtils.isEmpty(xyrlist)) {
          result.put("XYRCOUNT", xyrlist.size());
        } else {
          result.put("XYRCOUNT", 0);
        }
        if (!StringUtils.isEmpty(qbxslist)) {
          result.put("QBXSCOUNT", qbxslist.size());
        } else {
          result.put("QBXSCOUNT", 0);
        }
        if (!StringUtils.isEmpty(ajgldwlist)) {
          result.put("AJGLDWCOUNT", ajgldwlist.size());
        } else {
          result.put("AJGLDWCOUNT", 0);
        }
        if (!StringUtils.isEmpty(jcjlist)) {
          result.put("JCJCOUNT", jcjlist.size());
        } else {
          result.put("JCJCOUNT", 0);
        }
      } else if (condition.equals("aj")) {
        List<Map<String, Object>> list = ajcx(requestBody);
        if (list.size() != 0) {
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
        List<Map<String, Object>> xyrlist = xyrcx(requestBody);
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
        List<Map<String, Object>> qbxslist = qbxscx(requestBody);
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
        List<Map<String, Object>> ajgldwlist = ajgldwcx(requestBody);
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
      } else if (condition.equals("jcj")) {
        List<Map<String, Object>> jcjlist = jcj(requestBody);
        if (jcjlist.size() != 0) {
          ListPageUtil<Map<String, Object>> listPageUtil =
              new ListPageUtil<Map<String, Object>>(jcjlist, pageNum, pageSize);
          List<Map<String, Object>> pagedList = listPageUtil.getPagedList();
          result.put("JCJ", pagedList);
          result.put("JCJCOUNT", jcjlist.size());
        } else {
          result.put("JCJ", null);
          result.put("JCJCOUNT", 0);
        }
      }
    } else {
      // 有配置，则走配置查询
      if (condition.equals("all")) {
        List<Map<String, Object>> reslist = new ArrayList<>();
        List<Map<String, Object>> list = ajpzcx(requestBody, configlist);
        List<Map<String, Object>> xyrlist = xyrpzcx(requestBody, configlist);
        List<Map<String, Object>> qbxslist = qbxspzcx(requestBody, configlist);
        List<Map<String, Object>> ajgldwlist = ajgldwpzcx(requestBody, configlist);
        if (!StringUtils.isEmpty(list)) {
          if (list.size() != 0) {
            reslist.addAll(list);
          }
        }
        if (!StringUtils.isEmpty(xyrlist)) {
          if (xyrlist.size() != 0) {
            reslist.addAll(xyrlist);
          }
        }
        if (!StringUtils.isEmpty(qbxslist)) {
          if (qbxslist.size() != 0) {
            reslist.addAll(qbxslist);
          }
        }
        if (!StringUtils.isEmpty(ajgldwlist)) {
          if (ajgldwlist.size() != 0) {
            reslist.addAll(ajgldwlist);
          }
        }
        if (reslist.size() != 0) {
          ListPageUtil<Map<String, Object>> listPageUtil =
              new ListPageUtil<Map<String, Object>>(reslist, pageNum, pageSize);
          List<Map<String, Object>> pagedList = listPageUtil.getPagedList();
          result.put("ALL", pagedList);
        } else {
          result.put("ALL", null);
        }
        if (!StringUtils.isEmpty(reslist)) {
          result.put("ALLCOUNT", reslist.size());
        } else {
          result.put("ALLCOUNT", 0);
        }
        if (!StringUtils.isEmpty(list)) {
          result.put("AJCOUNT", list.size());
        } else {
          result.put("AJCOUNT", 0);
        }
        if (!StringUtils.isEmpty(xyrlist)) {
          result.put("XYRCOUNT", xyrlist.size());
        } else {
          result.put("XYRCOUNT", 0);
        }
        if (!StringUtils.isEmpty(qbxslist)) {
          result.put("QBXSCOUNT", qbxslist.size());
        } else {
          result.put("QBXSCOUNT", 0);
        }
        if (!StringUtils.isEmpty(ajgldwlist)) {
          result.put("AJGLDWCOUNT", ajgldwlist.size());
        } else {
          result.put("AJGLDWCOUNT", 0);
        }
      } else if (condition.equals("aj")) {
        List<Map<String, Object>> list = ajpzcx(requestBody, configlist);
        if (!StringUtils.isEmpty(list) && list.size() != 0) {
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
        List<Map<String, Object>> xyrlist = xyrpzcx(requestBody, configlist);
        if (!StringUtils.isEmpty(xyrlist) && xyrlist.size() != 0) {
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
        List<Map<String, Object>> qbxslist = qbxspzcx(requestBody, configlist);
        if (!StringUtils.isEmpty(qbxslist) && qbxslist.size() != 0) {
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
        List<Map<String, Object>> ajgldwlist = ajgldwpzcx(requestBody, configlist);
        if (!StringUtils.isEmpty(ajgldwlist) && ajgldwlist.size() != 0) {
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
    }
    result.put("pageNum", pageNum);
    result.put("pageSize", pageSize);
    return result;
  }

  /**
   * 新案件查询
   * @param requestBody
   * @return
   * @throws Exception
   */
  @SuppressWarnings("unchecked")
  public List<Map<String, Object>> ajcx(Map<String, Object> requestBody) throws Exception {
    if (requestBody.get("depCode")!=null ) {
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "CASEQUERYNEW");
      List<Map<String, Object>> list = (List<Map<String, Object>>) baseService.list(requestBody);
      for (int i = 0; i < list.size(); i++) {
        Map<String, Object> map = list.get(i);
        map.put("param", requestBody.get("param"));
        Object fllb=map.get("SYH_FLLB");
        if(StringUtils.isEmpty(fllb)) {
          map.put("SYH_FLLB", "");
        }
        map.put("condition", "aj");
      }
      return list;
    }
    return new ArrayList<>();
  }
//  @SuppressWarnings("unchecked")
//  public List<Map<String, Object>> ajcx(Map<String, Object> requestBody) throws Exception {
//    Object requestSign=requestBody.get("requestSign");
//    String depCode=requestBody.get("depCode")+"";
//    Map<String, Object> depidmap =
//        (Map<String, Object>) depService.get(requestBody.get("param").toString());
//    List<Map<String, Object>> ajlist = new ArrayList<>();
//    if (!StringUtils.isEmpty(depidmap)) {
//      if(requestSign.equals("city")) {
//        String cityCode=depCode.substring(0,6);
//        Map<String, Object> map=new HashMap<>();
//        map.put("cityCode", cityCode);
//        Object depids=depService.list(map);
//        List<Map<String, Object>> deplist=(List<Map<String, Object>>) depids;
//        String ids=deplist.get(0).get("ids")+"";
//        depidmap.put("ztids", ids);
//      }
//      if(requestSign.equals("area")) {
//        Map<String, Object> map=new HashMap<>();
//        map.put("areaCode", depCode);
//        Object depids=depService.list(map);
//        List<Map<String, Object>> deplist=(List<Map<String, Object>>) depids;
//        String ids=deplist.get(0).get("ids")+"";
//        depidmap.put("ztids", ids);
//      }
//      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "DEPCASEQUERY");// 认领单位反查案件
//      Object ajobj = baseService.list(depidmap);
//      ajlist = (List<Map<String, Object>>) ajobj;
//    }
//    if(requestSign.equals("city")) {
//      String cityCode=depCode.substring(0,6);
//      Map<String, Object> map=new HashMap<>();
//      map.put("cityCode", cityCode);
//      Object depids=depService.list(map);
//      List<Map<String, Object>> deplist=(List<Map<String, Object>>) depids;
//      String ids=deplist.get(0).get("ids")+"";
//      requestBody.put("ztids", ids);
//    }
//    if(requestSign.equals("area")) {
//      Map<String, Object> map=new HashMap<>();
//      map.put("areaCode", depCode);
//      Object depids=depService.list(map);
//      List<Map<String, Object>> deplist=(List<Map<String, Object>>) depids;
//      String ids=deplist.get(0).get("ids")+"";
//      requestBody.put("ztids", ids);
//    }
//    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "CASEQUERY");// 案件默认查询
//    Object obj = baseService.list(requestBody);
//    List<Map<String, Object>> list = (List<Map<String, Object>>) obj;
//    for (int i = 0; i < ajlist.size(); i++) {
//      Map<String, Object> map = ajlist.get(i);
//      Object id = map.get("ID");
//      boolean bol = true;
//      for (int j = 0; j < list.size(); j++) {
//        Map<String, Object> newmap = list.get(j);
//        Object newid = newmap.get("ID");
//        if (id.equals(newid)) {
//          bol = false;
//        }
//      }
//      if (bol) {
//        list.add(map);
//      }
//    }
//    for (int i = 0; i < list.size(); i++) {
//      Map<String, Object> map = list.get(i);
//      Object id = map.get("OID");
//      map.put("param", requestBody.get("param"));
//      if (!StringUtils.isEmpty(id)) {
//        map.put("id", id);
//        Map<String, Object> depmap = (Map<String, Object>) depService.get(map);
//        map.put("RLBM", depmap.get("depname") + "");
//      } else {
//        map.put("RLBM", "");
//      }
//      Object fllb=map.get("SYH_FLLB");
//      if(StringUtils.isEmpty(fllb)) {
//        map.put("SYH_FLLB", "");
//      }
//      map.put("condition", "aj");
//    }
//    return list;
//  }

  @SuppressWarnings("unchecked")
  public List<Map<String, Object>> ajpzcx(Map<String, Object> requestBody,
      List<Map<String, Object>> configlist) throws Exception {
    StringBuilder sb = new StringBuilder();
    StringBuilder str = new StringBuilder();
    for (int i = 0; i < configlist.size(); i++) {
      Map<String, Object> configmap = configlist.get(i);
      Object businessType = configmap.get("business_type");
      Object isContrast = configmap.get("is_contrast");
      Object isShow = configmap.get("is_show");
      Object columnName = configmap.get("column_name");
      if (businessType.equals("1") && isContrast.equals(true)) {
        requestBody.put(columnName.toString(), columnName);
        str.append("s."+columnName);
      }
      if (businessType.equals("1") && isShow.equals(true)) {
        if (sb.length() > 0) {
          sb.append(",");
        }
        sb.append("s."+columnName);
      }
    }
    if (sb.length() == 0 || str.length() == 0) {
      return new ArrayList<>();
    } else {
      requestBody.put("query", sb);
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJCONFIGQUERYNEW");// 案件配置高级查询
      Object obj = baseService.list(requestBody);
      List<Map<String, Object>> list = (List<Map<String, Object>>) obj;
      for (int i = 0; i < list.size(); i++) {
        Map<String, Object> map = list.get(i);
        map.put("condition", "aj");
      }
      return list;
    }
  }
  
//  @SuppressWarnings("unchecked")
//  public List<Map<String, Object>> ajpzcx(Map<String, Object> requestBody,
//      List<Map<String, Object>> configlist) throws Exception {
//    StringBuilder sb = new StringBuilder();
//    StringBuilder str = new StringBuilder();
//    for (int i = 0; i < configlist.size(); i++) {
//      Map<String, Object> configmap = configlist.get(i);
//      Object businessType = configmap.get("business_type");
//      Object isContrast = configmap.get("is_contrast");
//      Object isShow = configmap.get("is_show");
//      Object columnName = configmap.get("column_name");
//      if (businessType.equals("1") && isContrast.equals(true)) {
//        requestBody.put(columnName.toString(), columnName);
//        str.append("s."+columnName);
//      }
//      if (businessType.equals("1") && isShow.equals(true)) {
//        if (sb.length() > 0) {
//          sb.append(",");
//        }
//        sb.append("s."+columnName);
//      }
//    }
//    if (sb.length() == 0 || str.length() == 0) {
//      return null;
//    } else {
//      requestBody.put("query", sb);
//      Object requestSign=requestBody.get("requestSign");
//      String depCode=requestBody.get("depCode")+"";
//      if(requestSign.equals("city")) {
//        String cityCode=depCode.substring(0,6);
//        Map<String, Object> map=new HashMap<>();
//        map.put("cityCode", cityCode);
//        Object depids=depService.list(map);
//        List<Map<String, Object>> deplist=(List<Map<String, Object>>) depids;
//        String ids=deplist.get(0).get("ids")+"";
//        requestBody.put("ztids", ids);
//      }
//      if(requestSign.equals("area")) {
//        Map<String, Object> map=new HashMap<>();
//        map.put("areaCode", depCode);
//        Object depids=depService.list(map);
//        List<Map<String, Object>> deplist=(List<Map<String, Object>>) depids;
//        String ids=deplist.get(0).get("ids")+"";
//        requestBody.put("ztids", ids);
//      }
//      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJCONFIGQUERY");// 案件配置查询
//      Object obj = baseService.list(requestBody);
//      List<Map<String, Object>> list = (List<Map<String, Object>>) obj;
//      for (int i = 0; i < list.size(); i++) {
//        Map<String, Object> map = list.get(i);
//        map.put("condition", "aj");
//      }
//      return list;
//    }
//  }

  
  @SuppressWarnings("unchecked")
  public List<Map<String, Object>> xyrcx(Map<String, Object> requestBody) throws Exception {
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "XYRQUERY");// 嫌疑人默认查询
    Object xyrobj = baseService.list(requestBody);
    List<Map<String, Object>> xyrlist = (List<Map<String, Object>>) xyrobj;
    for (int i = 0; i < xyrlist.size(); i++) {
      Map<String, Object> map = xyrlist.get(i);
      map.put("condition", "xyr");
    }
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
      requestBody.put("query", sb);
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "XYRCONFIGQUERY");// 嫌疑人配置查询
      Object xyrobj = baseService.list(requestBody);
      List<Map<String, Object>> xyrlist = (List<Map<String, Object>>) xyrobj;
      for (int i = 0; i < xyrlist.size(); i++) {
        Map<String, Object> map = xyrlist.get(i);
        map.put("condition", "xyr");
      }
      return xyrlist;
    }
  }

  @SuppressWarnings("unchecked")
  public List<Map<String, Object>> qbxscx(Map<String, Object> requestBody) throws Exception {
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "QBXSQUERY");// 情报线索默认查询
    Object qbxsobj = baseService.list(requestBody);
    List<Map<String, Object>> qbxslist = (List<Map<String, Object>>) qbxsobj;
    for (int i = 0; i < qbxslist.size(); i++) {
      Map<String, Object> map = qbxslist.get(i);
      map.put("condition", "qbxs");
    }
    return qbxslist;
  }

  @SuppressWarnings("unchecked")
  public List<Map<String, Object>> qbxspzcx(Map<String, Object> requestBody,
      List<Map<String, Object>> configlist) throws Exception {
    StringBuilder sb = new StringBuilder();
    StringBuilder str = new StringBuilder();
    for (int i = 0; i < configlist.size(); i++) {
      Map<String, Object> configmap = configlist.get(i);
      Object businessType = configmap.get("business_type");
      Object isContrast = configmap.get("is_contrast");
      Object isShow = configmap.get("is_show");
      Object columnName = configmap.get("column_name");
      if (businessType.equals("4") && isContrast.equals(true)) {
        requestBody.put(columnName.toString(), columnName);
        str.append(columnName);
      }
      if (businessType.equals("4") && isShow.equals(true)) {
        if (sb.length() > 0) {
          sb.append(",");
        }
        sb.append(columnName);
      }
    }
    if (sb.length() == 0 || str.length() == 0) {
      return null;
    } else {
      requestBody.put("query", sb);
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "QBXSCONFIGQUERY");// 情报线索配置查询
      Object qbxsobj = baseService.list(requestBody);
      List<Map<String, Object>> qbxslist = (List<Map<String, Object>>) qbxsobj;
      for (int i = 0; i < qbxslist.size(); i++) {
        Map<String, Object> map = qbxslist.get(i);
        map.put("condition", "qbxs");
      }
      return qbxslist;
    }
  }

  /**
   * 案件关联单位
   * @param requestBody
   * @return
   * @throws Exception
   */
  @SuppressWarnings("unchecked")
  public List<Map<String, Object>> ajgldwcx(Map<String, Object> requestBody) throws Exception {
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJGLDWQUERY");// 案件关联单位默认查询
    Object ajgldwobj = baseService.list(requestBody);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJDWCCOMPANY");// 企业
    Object ajdwccobj = baseService.list(requestBody);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJDWPITCH");// 摊贩
    Object ajpitchobj = baseService.list(requestBody);
    List<Map<String, Object>> ajgldwlist = (List<Map<String, Object>>) ajgldwobj;
    List<Map<String, Object>> ajdwcclist = (List<Map<String, Object>>) ajdwccobj;
    List<Map<String, Object>> ajpitchlist = (List<Map<String, Object>>) ajpitchobj;
    if (ajgldwlist != null) {
      if(ajdwcclist != null && ajdwcclist.size()>0) {
        ajgldwlist.addAll(ajdwcclist);
      }
      if(ajpitchlist != null && ajdwcclist.size()>0) {
        ajgldwlist.addAll(ajpitchlist);
      }
      for (int i = 0; i < ajgldwlist.size(); i++) {
        Map<String, Object> map = ajgldwlist.get(i);
        map.put("condition", "ajgldw");
      }
    }
    return ajgldwlist;
  }
  /**
   * 接处警
   * @param requestBody
   * @return
   * @throws Exception
   */
  @SuppressWarnings("unchecked")
  public List<Map<String, Object>> jcj(Map<String, Object> requestBody) throws Exception {
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "QUERYRECEIVEPOLICE");// 案件关联单位默认查询
    Object obj = baseService.list(requestBody);
    List<Map<String, Object>> jcjlist = (List<Map<String, Object>>) obj;
    if (obj != null) {
      if(jcjlist != null && jcjlist.size()>0) {
        for (int i = 0; i < jcjlist.size(); i++) {
          Map<String, Object> map = jcjlist.get(i);
          map.put("condition", "jcj");
        }
      }
      
    }
    return jcjlist;
  }

  @SuppressWarnings("unchecked")
  public List<Map<String, Object>> ajgldwpzcx(Map<String, Object> requestBody,
      List<Map<String, Object>> configlist) throws Exception {
    StringBuilder sb = new StringBuilder();
    StringBuilder str = new StringBuilder();
    for (int i = 0; i < configlist.size(); i++) {
      Map<String, Object> configmap = configlist.get(i);
      Object businessType = configmap.get("business_type");
      Object isContrast = configmap.get("is_contrast");
      Object isShow = configmap.get("is_show");
      Object columnName = configmap.get("column_name");
      if (businessType.equals("3") && isContrast.equals(true)) {
        requestBody.put(columnName.toString(), columnName);
        str.append(columnName);
      }
      if (businessType.equals("3") && isShow.equals(true)) {
        if (sb.length() > 0) {
          sb.append(",");
        }
        sb.append(columnName);
      }
    }
    if (sb.length() == 0 || str.length() == 0) {
      return null;
    } else {
      requestBody.put("query", sb);
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJGLDWCONFIGQUERY");// 案件关联单位配置查询
      Object ajgldwobj = baseService.list(requestBody);
      List<Map<String, Object>> ajgldwlist = (List<Map<String, Object>>) ajgldwobj;
      for (int i = 0; i < ajgldwlist.size(); i++) {
        Map<String, Object> map = ajgldwlist.get(i);
        map.put("condition", "ajgldw");
      }
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
  
 
}
