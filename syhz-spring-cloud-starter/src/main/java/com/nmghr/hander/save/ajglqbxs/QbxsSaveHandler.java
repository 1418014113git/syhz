package com.nmghr.hander.save.ajglqbxs;

import com.alibaba.fastjson.JSON;
import com.nmghr.basic.common.Constant;
import com.nmghr.basic.common.exception.GlobalErrorException;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractSaveHandler;
import com.nmghr.hander.save.cluster.CaseAssistSubmitSaveHandler;
import com.nmghr.hander.save.cluster.DeptMapperSaveHandler;
import com.nmghr.hander.save.common.BatchSaveHandler;
import com.nmghr.handler.message.QueueConfig;
import com.nmghr.handler.service.SendMessageService;
import com.nmghr.service.ajglqbxs.AjglQbxsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("unchecked")
@Service("qbxsSaveHandler")
public class QbxsSaveHandler extends AbstractSaveHandler {

  private Logger log = LoggerFactory.getLogger(QbxsSaveHandler.class);

  public QbxsSaveHandler(IBaseService baseService) {
    super(baseService);
  }

  @Autowired
  private BatchSaveHandler batchSaveHandler;
  @Autowired
  private DeptMapperSaveHandler deptMapperSaveHandler;
  @Autowired
  private CaseAssistSubmitSaveHandler caseAssistSubmitSaveHandler;
  @Autowired
  private AjglQbxsService ajglQbxsService;
  @Autowired
  private QbxsSignSaveHandler qbxsSignSaveHandler;

  @Autowired
  private SendMessageService sendMessageService;

  /**
   * 批量导入数据增加方法
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public Object save(Map<String, Object> body) throws Exception {
    //添加标题信息
    //添加明细数据
    List<LinkedHashMap<String, Object>> list = (List<LinkedHashMap<String, Object>>) body.get("list");
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
    if ("noData".equals(body.get("checkResult"))) {
      saveHeads(type, creator, curDeptCode, curDeptName, assistId, list.get(0));
    }
    List<LinkedHashMap<String, Object>> addDatas = new ArrayList<>();
    List<LinkedHashMap<String, Object>> updDatas = new ArrayList<>();
    //检查编号并修改 分析需要新增的线索
    Map<String, Object> bhMap = new HashMap<>();
    Map<String, Object> addrMap = new HashMap<>();
    Map<String, Object> bhP = new HashMap<>();
    bhP.put("id", assistId);
    bhP.put("type", type);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJGLQBXSINFOBHLIST");
    List<Map<String, Object>> bhList = (List<Map<String, Object>>) baseService.list(bhP);
    for (Map<String, Object> map : bhList) {
      bhMap.put(String.valueOf(map.get("number")), map.get("qbxsId"));
      addrMap.put(String.valueOf(map.get("address") + "_" + map.get("number")), map.get("qbxsId"));
    }
    Map<String, Object> idxMap = new HashMap<>();
    LinkedHashMap<String, Object> keyMap = list.get(0);
    int idx = 0;
    for (String key : keyMap.keySet()) {
      idxMap.put(key, idx);
      idx++;
    }
    List<Object> updQxIds = new ArrayList<>();
    Map<String, Object> updAddrMap = new HashMap<>();
    for (LinkedHashMap<String, Object> map : list) {
      String num = String.valueOf(map.get("序号"));
      if (bhMap.containsKey(num)) {
        map.put("qbxsId", bhMap.get(num));
        updDatas.add(map);
        updQxIds.add(bhMap.get(num));
        if (!addrMap.containsKey(String.valueOf(map.get("地址")) + "_" + num)) {
          updAddrMap.put(String.valueOf(bhMap.get(num)), String.valueOf(map.get("地址")));
        }
      } else {
        addDatas.add(map);
      }
    }
    if (updDatas.size() > 0) {
      Map<String, Object> msgP = new HashMap<>();
      msgP.put("assistType", type);
      msgP.put("id", assistId);
      msgP.put("updDatas", updDatas);
      msgP.put("updQxIds", updQxIds);
      msgP.put("updAddrMap", updAddrMap);
      msgP.put("category", category);
      msgP.put("creator", creator);
      msgP.put("idxMap", idxMap);
      sendMessageService.sendMessage(msgP, QueueConfig.AJGLQBXSINFO);
    }

    if (addDatas.size() > 0) {
      List<Map<String, Object>> resets =
          (List<Map<String, Object>>) saveBaseAndDept(type, category, assistId, addDatas, String.valueOf(body.get("xfType")), String.valueOf(curDeptCode));

      List<Map<String, Object>> paramValues = new ArrayList<>(addDatas.size());
      int size = addDatas.size();
      for (int i = 0; i < size; i++) {
        Map<String, Object> values = addDatas.get(i);
        Map<String, Object> base = resets.get(i);
        int vIdx = 0;
        for (Object str : values.values()) {
          Map<String, Object> p = new HashMap<>();
          p.put("assistId", assistId);
          p.put("qbxsId", base.get("id"));
          p.put("rowIndex", vIdx);
          p.put("columnIndex", i);
          p.put("value", str);
          p.put("qbxsCategory", category);
          p.put("creator", creator);
          paramValues.add(p);
          vIdx++;
        }
      }
      Map<String, Object> params2 = new HashMap<>();
      params2.put("list", paramValues);
      params2.put("alias", "AJGLQBXSINFOBATCH");
      params2.put("seqName", "AJGLQBXSINFO");
      params2.put("subSize", 50);
      batchSaveHandler.save(params2);

      if ("addRecord".equals(body.get("opt"))) {
        Map<String, Object> signData = new HashMap<>();
        //增加记录
        List<Map<String, Object>> datas = new ArrayList<>();
        for (Map<String, Object> m : resets) {
          if (!StringUtils.isEmpty(m.get("receiveCode"))) {
            Map<String, Object> map = new HashMap<>();
            map.put("qbxsId", m.get("id"));
            map.put("assistType", type);
            map.put("assistId", assistId);
            map.put("receiveName", m.get("receiveName"));
            map.put("receiveCode", m.get("receiveCode"));
            map.put("createName", body.get("curDeptName"));
            map.put("createCode", body.get("curDeptCode"));
            map.put("creatorId", body.get("userId"));
            map.put("creatorName", body.get("userName"));
            map.put("optCategory", 1);
            datas.add(map);
            //组装签收数据
            packageSignData(body, type, assistId, signData, m.get("receiveCode"),m.get("receiveName"));
          }
        }
        //增加签收收据
        createSignInfo(new ArrayList(signData.values()));

        Map<String, Object> param = new HashMap<>();
        param.put("type", "batch");
        param.put("list", datas);
        sendMessageService.sendMessage(param, QueueConfig.AJGLQBXSRECORD);
      }
    }
    return ajglQbxsService.getClueTotal(String.valueOf(assistId));
  }

  private void packageSignData(Map<String, Object> body, Object type, Object assistId, Map<String, Object> signData, Object receiveCode, Object receiveName) {
    if (signData.get(String.valueOf(receiveCode)) != null) {
      Map<String, Object> sign = (Map<String, Object>) signData.get(String.valueOf(receiveCode));
      sign.put("clueNum", Integer.parseInt(String.valueOf(sign.get("clueNum"))) + 1);
    } else {
      Map<String, Object> sign = new HashMap<>();
      sign.put("userId", body.get("userId"));
      sign.put("userName", body.get("userName"));
      sign.put("deptCode", body.get("curDeptCode"));
      sign.put("deptName", body.get("curDeptName"));
      sign.put("receiveDeptCode", receiveCode);
      sign.put("receiveDeptName", receiveName);
      sign.put("assistId", assistId);
      sign.put("clueNum", 1);
      sign.put("assistType", type);
      signData.put(String.valueOf(receiveCode), sign);
    }
  }


  private Object saveBaseAndDept(Object type, Object category, Object assistId, List<LinkedHashMap<String, Object>> list, String xfType, String curDeptCode) throws Exception {
    Map<String, Object> zhidui = getDeptInfos(xfType, curDeptCode);

    int size = list.size();
    List<Map<String, Object>> baseList = new ArrayList<>(size);

    for (int i = 0; i < size; i++) {
      Map<String, Object> values = list.get(i);
      String addr = String.valueOf(values.get("地址"));
      List<Map<String, Object>> depts = getDepts(zhidui, addr, xfType);
      log.info(addr + "  ----  " + JSON.toJSONString(depts));
      Boolean ff = false;
      Object deptCode = null;
      Object deptName = null;
      Object deptId = null;
      if (depts != null && depts.size() == 1) {
        Map<String, Object> dept = depts.get(0);
        Map<String, Object> p = new HashMap<>();
        //组装分发数据   线索分发 增加关联单位，增加待办
        if ("2".equals(String.valueOf(type))) {
          dept.put("clusterId", assistId);
          p.putAll(dept);
          // 分配部门
          deptId = deptMapperSaveHandler.save(p);
        }
        if ("1".equals(String.valueOf(type))) {
          dept.put("assistId", assistId);
          p.putAll(dept);
          // 分配部门
          deptId = caseAssistSubmitSaveHandler.saveDept(p);
        }
        ff = true;
        deptCode = p.get("deptCode");
        deptName = p.get("deptName");
      }
      Map<String, Object> base = new HashMap<>();
      base.put("assistId", assistId);
      base.put("serialNumber", values.get("序号"));
      base.put("address", addr);
      base.put("qbxsDistribute", ff ? 2 : 1); // 1未分发
      base.put("qbxsCategory", category);
      base.put("qbxsSign", 1);//未签收
      base.put("qbxsResult", 1);//未反馈
      base.put("assistType", type);
      if (ff && deptId != null) {
        base.put("receiveCode", deptCode);
        base.put("receiveName", deptName);
        base.put("deptId", deptId);
      }
      baseList.add(base);
    }
    List<Map<String, Object>> qbxsDeptList = new ArrayList<>(size);
    Map<String, Object> params = new HashMap<>();
    params.put("list", baseList);
    params.put("alias", "AJGLQBXSBASEBATCH");
    params.put("seqName", "AJGLQBXSBASE");
    params.put("subSize", 20);
    List<Map<String, Object>> baseRes = (List<Map<String, Object>>) batchSaveHandler.save(params);
    for (Map<String, Object> map : baseRes) {
      if (map.get("receiveCode") != null) {
        Map<String, Object> deptMap = new HashMap<>();
        deptMap.put("assistDeptId", map.get("deptId"));
        deptMap.put("deptCode", map.get("receiveCode"));
        deptMap.put("qbxsId", map.get("id"));
        deptMap.put("assistType", type);
        deptMap.put("transferred", 1);
        qbxsDeptList.add(deptMap);
      }
    }
    params = new HashMap<>();
    params.put("list", qbxsDeptList);
    params.put("alias", "AJGLQBXSDEPTBATCH");
    params.put("seqName", "AJGLQBXSDEPT");
    params.put("subSize", 20);
    batchSaveHandler.save(params);
    return baseRes;
  }


  private void saveHeads(Object type, Object creator, Object curDeptCode, Object curDeptName, Object assistId, Map<String, Object> bean) {
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
    Map<String, Object> params = new HashMap<>();
    params.put("list", paramTitles);
    params.put("alias", "AJGLQBXSBATCH");
    params.put("seqName", "AJGLQBXS");
    params.put("subSize", 50);
    batchSaveHandler.save(params);
  }

  Pattern pattern = Pattern.compile("市(.*)[区|县]");

  private List<Map<String, Object>> getDepts(Map<String, Object> zhidui, String addr, String xfType) {
    if ("zdxf".equals(xfType)) {
      Matcher matcher = pattern.matcher(addr);
      if (matcher.find()) {
        String k = matcher.group(1);
        for (String key : zhidui.keySet()) {
          if (key.contains(k)) {
            return (List<Map<String, Object>>) zhidui.get(key);
          }
        }
      }
      return new ArrayList<>();
    }
    List<Map<String, Object>> depts = (List<Map<String, Object>>) zhidui.get(getCityName(addr, xfType));
    return depts;
  }


  private String getCityName(String addr, String xfType) {
    addr = addr.replaceAll("[`~!@#$%^&*()_\\-+=<>?:\"{}|,.\\/;'\\[\\]·~！@#￥%……&*（）——\\-+={}|《》？：“”【】、；‘’，。、]", "");
    if ("zdxf".equals(xfType)) {
    }
    if (addr.contains("杨凌区") || addr.contains("杨凌示范区")) {
      return "杨凌区";
    }
    if (addr.contains("西咸新区")) {
      return "西咸新区";
    }
    addr = addr.replaceAll("陕西", "");
    addr = addr.substring(0, addr.indexOf("市") + 1);
    if (addr.contains("省")) {
      return addr.substring(addr.indexOf("省") + 1, addr.length());
    }
    return addr;
  }


  private Map<String, Object> getDeptInfos(String xfType, String deptCode) {
    Map<String, Object> result = new HashMap<>();
    try {
      Map<String, Object> params = new HashMap<>();
      if ("zdxf".equals(xfType)) { //支队下发
        params.put("deptType", 3);
        params.put("deptCode", deptCode.substring(0, 4));
      } else {
        params.put("deptType", 2);
      }
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "DEPTINFOS");
      List<Map<String, Object>> depts = (List<Map<String, Object>>) baseService.list(params);
      if (depts != null && depts.size() > 0) {
        for (Map<String, Object> map : depts) {
          String key = String.valueOf(map.get("cityName"));
          if ("zdxf".equals(xfType)) {
            key = String.valueOf(map.get("name"));
          }
          if (result.containsKey(key)) {
            List<Map<String, Object>> vals = (List<Map<String, Object>>) result.get(key);
            Map<String, Object> valData = new HashMap<>();
            valData.put("deptCode", map.get("deptCode"));
            valData.put("deptName", map.get("name"));
            vals.add(valData);
            result.put(key, vals);
          } else {
            List<Map<String, Object>> vals = new ArrayList<>();
            Map<String, Object> valData = new HashMap<>();
            valData.put("deptCode", map.get("deptCode"));
            valData.put("deptName", map.get("name"));
            vals.add(valData);
            result.put(key, vals);
          }
        }
      }
    } catch (Exception e) {
      log.error("DEPTINFOS error : " + e.getMessage());
    }
    return result;
  }


  /**
   * 生成签收记录
   *
   * @throws Exception
   */
  private void createSignInfo(List<Map<String, Object>> signs) throws Exception {
    Map<String, Object> signParam = new HashMap<>();
    signParam.put("list", signs);
    qbxsSignSaveHandler.save(signParam);
  }


}
