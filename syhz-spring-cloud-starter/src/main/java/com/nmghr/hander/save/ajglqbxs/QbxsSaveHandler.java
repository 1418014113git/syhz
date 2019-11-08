package com.nmghr.hander.save.ajglqbxs;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractSaveHandler;
import com.nmghr.common.BusinessSign;
import com.nmghr.hander.dto.BusinessSignParam;
import com.nmghr.hander.save.cluster.DeptMapperSaveHandler;
import com.nmghr.hander.save.common.BatchSaveHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@SuppressWarnings("unchecked")
@Service("qbxsSaveHandler")
public class QbxsSaveHandler extends AbstractSaveHandler {

  private Logger log = LoggerFactory.getLogger(QbxsSaveHandler.class);

  public QbxsSaveHandler(IBaseService baseService) {
    super(baseService);
  }

  @Autowired
  private QbxsSignSaveHandler qbxsSignSaveHandler;

  @Autowired
  private BatchSaveHandler batchSaveHandler;
  @Autowired
  private DeptMapperSaveHandler deptMapperSaveHandler;

  /**
   * 批量导入数据增加方法
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public Object save(Map<String, Object> body) throws Exception {
    //添加标题信息
    //添加明细数据
    List<Map<String, Object>> list = (List<Map<String, Object>>) body.get("list");
    if (list == null || list.size() == 0) {
      return null;
    }
    Object type = body.get("type"); // 1案件协查  2集群战役
    Object category = body.get("category");
    Object creator = body.get("userId");
    Object creatorName = body.get("userName");
    Object curDeptCode = body.get("curDeptCode");
    Object curDeptName = body.get("curDeptName");
    Object assistId = body.get("assistId");
    Map<String, Object> bean = list.get(0);
    List<Map<String, Object>> paramTitles = new ArrayList<>(bean.keySet().size());
    int tIdx = 0;
    for (String str : bean.keySet()) {
      Map<String, Object> p = new HashMap<>();
      p.put("qbxsType", type);
      p.put("qbxsIndex", tIdx);
      p.put("titleItem", str);
      p.put("creator", creator);
      p.put("deptCode", curDeptCode);
      p.put("deptName", curDeptName);
      p.put("assistId", assistId);
      paramTitles.add(p);
      tIdx++;
    }
    Map<String, Object> zhidui = new HashMap<>();
    getDeptInfos(zhidui);
    List<Map<String, Object>> paramValues = new ArrayList<>(list.size());
    Map<String, Object> assistDepts = new HashMap<>();

    int size = list.size();
    int distribute = 0;
    for (int i = 0; i < size; i++) {
      Map<String, Object> values = list.get(i);
      int vIdx = 0;
      String xsId = assistId + "_" + i;
      String addr = String.valueOf(values.get("地址"));
      List<Map<String, Object>> depts = (List<Map<String, Object>>) zhidui.get(getCityName(addr));
      Boolean ff = false;
      if (depts != null && depts.size() == 1) {
        Map<String, Object> dept = depts.get(0);
        //组装分发数据   线索分发 增加关联单位，增加待办
        if ("2".equals(String.valueOf(type))) {
          dept.put("clusterId", assistId);
        }
        if ("1".equals(String.valueOf(type))) {
          dept.put("assistId", assistId);
        }
        if (assistDepts.containsKey(String.valueOf(dept.get("deptCode")))) {
          Map<String, Object> valData = (Map<String, Object>) assistDepts.get(String.valueOf(dept.get("deptCode")));
          valData.put("clueCount", Integer.parseInt(String.valueOf(valData.get("clueCount"))) + 1);
          assistDepts.put(String.valueOf(dept.get("deptCode")), valData);
        } else {
          Map<String, Object> valData = new HashMap<>();
          valData.putAll(dept);
          valData.put("clueCount", 1);
          assistDepts.put(String.valueOf(dept.get("deptCode")), valData);
        }
        ff = true;
        distribute++;
      }
      for (Object str : values.values()) {
        Map<String, Object> p = new HashMap<>();
        p.put("assistId", assistId);
        p.put("ajglQbxsIdx", xsId);
        p.put("rowIndex", vIdx);
        p.put("columnIndex", i);
        p.put("value", str);
        p.put("qbxsCategory", category);
        p.put("creator", creator);
        p.put("status", ff ? 1 : 0);
        paramValues.add(p);
        vIdx++;
      }
    }

    Map<String, Object> params = new HashMap<>();
    params.put("list", paramTitles);
    params.put("alias", "AJGLQBXSBATCH");
    params.put("seqName", "AJGLQBXS");
    params.put("subSize", 50);
    batchSaveHandler.save(params);
    params = new HashMap<>();
    params.put("list", paramValues);
    params.put("alias", "AJGLQBXSINFOBATCH");
    params.put("seqName", "AJGLQBXSINFO");
    params.put("subSize", 50);
    batchSaveHandler.save(params);
    //增加待办信息
    if (assistDepts.size() > 0) {
      params = new HashMap<>();
      params.put("list", new ArrayList(assistDepts.values()));
      deptMapperSaveHandler.save(params);

      List<Map<String, Object>> signs = new ArrayList<>(list.size());
      List<Map<String, Object>> depts = new ArrayList(assistDepts.values());
      for (Map<String, Object> map : depts) {
        Map<String, Object> signData = new HashMap<>();
        signData.put("userId", creator);
        signData.put("userName", creatorName);
        signData.put("deptCode", curDeptCode);
        signData.put("deptName", curDeptName);
        signData.put("receiveDeptCode", map.get("deptCode"));
        signData.put("receiveDeptName", map.get("deptName"));
        signData.put("assistId", assistId);
        signData.put("clueNum", map.get("clueCount"));
        signs.add(signData);
      }
      Map<String, Object> signParam = new HashMap<>();
      signParam.put("list", signs);
      qbxsSignSaveHandler.save(signParam);
    }

    Map result = new HashMap();
    result.put("total", list.size());
    result.put("distribute", distribute);
    return result;
  }

  private String getCityName(String str) {
    if (str.contains("杨凌区")) {
      return "杨凌区";
    }
    if (str.contains("西咸新区")) {
      return "西咸新区";
    }
    str = str.substring(0, str.indexOf("市") + 1);
    if (str.contains("省")) {
      return str.substring(str.indexOf("省") + 1, str.length());
    }
    return str;
  }


  private void getDeptInfos(Map<String, Object> zhidui) {
    try {
      Map<String, Object> params = new HashMap<>();
      params.put("deptType", 2);
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "DEPTINFOS");
      List<Map<String, Object>> depts = (List<Map<String, Object>>) baseService.list(params);
      if (depts != null && depts.size() > 0) {
        for (Map<String, Object> map : depts) {
          if (zhidui.containsKey(String.valueOf(map.get("cityName")))) {
            List<Map<String, Object>> vals = (List<Map<String, Object>>) zhidui.get(String.valueOf(map.get("cityName")));
            Map<String, Object> valData = new HashMap<>();
            valData.put("deptCode", map.get("deptCode"));
            valData.put("deptName", map.get("name"));
            vals.add(valData);
            zhidui.put(String.valueOf(map.get("cityName")), vals);
          } else {
            List<Map<String, Object>> vals = new ArrayList<>();
            Map<String, Object> valData = new HashMap<>();
            valData.put("deptCode", map.get("deptCode"));
            valData.put("deptName", map.get("name"));
            vals.add(valData);
            zhidui.put(String.valueOf(map.get("cityName")), vals);
          }
        }
      }
    } catch (Exception e) {
      log.error("GETDEPTIDBYCODE error : " + e.getMessage());
    }
  }

}
