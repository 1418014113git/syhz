package com.nmghr.service.ajglqbxs;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.nmghr.basic.common.Constant;
import com.nmghr.basic.common.exception.GlobalErrorException;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.page.Paging;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.hander.save.ajglqbxs.QbxsSignSaveHandler;
import com.nmghr.hander.save.cluster.CaseAssistSubmitSaveHandler;
import com.nmghr.hander.save.cluster.DeptMapperSaveHandler;
import com.nmghr.hander.save.common.BatchSaveHandler;
import org.apache.commons.collections.IteratorUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@SuppressWarnings("unchecked")
@Service("ajglQbxsService")
public class AjglQbxsService {

  @Autowired
  private IBaseService baseService;

  @Autowired
  private DeptMapperSaveHandler deptMapperSaveHandler;

  @Autowired
  private CaseAssistSubmitSaveHandler caseAssistSubmitSaveHandler;

  @Autowired
  private QbxsSignSaveHandler qbxsSignSaveHandler;

  @Autowired
  private BatchSaveHandler batchSaveHandler;

  /**
   * 线索分发 创建部门分发线索
   *
   * @param body
   * @return
   * @throws Exception
   */
  @Transactional
  public Object distributeClue(Map<String, Object> body) throws Exception {
    // 查询未分配的线索 ids
    //增加部门表数据  assistId deptcode name cluecount
    //修改线索状态  qbxs_base
    String ids = String.valueOf(body.get("ids"));
    // 分配部门
    String type = "";
    if (body.containsKey("type") && !StringUtils.isEmpty(body.get("type"))) {
      type = String.valueOf(body.get("type"));
    }
    //增加协查分配的部门信息
    Object id = "";
    if ("".equals(type) || "2".equals(type)) { // 集群战役
      body.put("assistType", 2);
      Map<String, Object> deptP = new HashMap<>();
      deptP.put("clusterId", body.get("assistId"));
      deptP.put("deptCode", body.get("acceptDeptCode"));
      deptP.put("deptName", body.get("acceptDeptName"));
      id = deptMapperSaveHandler.save(deptP);
    }
    if ("1".equals(type)) { // 案件协查
      body.put("assistType", 1);
      Map<String, Object> deptP = new HashMap<>();
      deptP.put("assistId", body.get("assistId"));
      deptP.put("deptCode", body.get("acceptDeptCode"));
      deptP.put("deptName", body.get("acceptDeptName"));
      id = caseAssistSubmitSaveHandler.saveDept(deptP);
    }
    //修改base表为已分发
    Map<String, Object> baseP = new HashMap<>();
    baseP.put("ids", ids);
    baseP.put("qbxsDistribute", 2);
    baseP.put("qbxsResult", 1);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJGLQBXSBASEBATCHUPDATE");
    baseService.update("", baseP);

    //删除线索部门关系信息
    Map<String, Object> param = new HashMap<>();
    param.put("qbxsIds", ids);
    param.put("assistId", body.get("assistId"));
    param.put("type", "1".equals(type) ? 1 : 2);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJGLQBXSDEPTDEL");
    baseService.remove(param);
    //增加新的关联信息
    saveMapper(ids, id, body.get("assistType"));

    return getClueTotal(String.valueOf(body.get("assistId")));
  }

  /**
   * 线索分发 支队下发线索
   *
   * @param body
   * @return
   * @throws Exception
   */
  @Transactional
  public Object issue(Map<String, Object> body) throws Exception {
    // 查询未分配的线索 ids
    //增加部门表数据  assistId deptcode name cluecount
    //修改线索状态  qbxs_base
    String ids = String.valueOf(body.get("ids"));
    // 分配部门
    String type = "";
    if (body.containsKey("type") && !StringUtils.isEmpty(body.get("type"))) {
      type = String.valueOf(body.get("type"));
    }
    //增加协查分配的部门信息
    Map<String, Object> deptP = new HashMap<>();
    deptP.put("deptCode", body.get("acceptDeptCode"));
    deptP.put("deptName", body.get("acceptDeptName"));
    Object id = "";
    if ("".equals(type) || "2".equals(type)) { // 集群战役
      body.put("assistType", 2);
      deptP.put("clusterId", body.get("assistId"));
      id = deptMapperSaveHandler.save(deptP);
    }
    if ("1".equals(type)) { // 案件协查
      body.put("assistType", 1);
      deptP.put("assistId", body.get("assistId"));
      id = caseAssistSubmitSaveHandler.saveDept(deptP);
    }
    //修改base表为已分发
    Map<String, Object> baseP = new HashMap<>();
    baseP.put("ids", ids);
    baseP.put("qbxsDistribute", 2);
    baseP.put("qbxsResult", 1);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJGLQBXSBASEBATCHUPDATE");
    baseService.update("", baseP);

    //修改关联表为已下发
    Map<String, Object> p = new HashMap<>();
    p.put("qbxsIds", ids);
    p.put("deptCode", body.get("curDeptCode"));
    p.put("assistId", body.get("assistId"));
    p.put("type", "1".equals(type) ? 1 : 2);
    p.put("transferred", 2);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJGLQBXSDEPTISSUE");
    baseService.update("", p);//修改关联表为已下发

    //删除当前单位以外要修改的线索部门关系信息
    reIssue(body, ids, type);

    //增加新的关联信息
    saveMapper(ids, id, body.get("assistType"));

    // 如果是支队需要增加签收信息
    if (body.get("curDeptType") != null && "2".equals(String.valueOf(body.get("curDeptType")))) {
      String[] arrs = ids.split(",");
      createSignInfo(body, arrs.length);
    }
    return getClueTotal(String.valueOf(body.get("assistId")));
  }

  /**
   * 删除处理已分配的数据
   *
   * @param body
   * @param ids
   * @param type
   * @throws Exception
   */
  private void reIssue(Map<String, Object> body, String ids, String type) throws Exception {
    Map<String, Object> param = new HashMap<>();
    param.put("qbxsIds", ids);
    param.put("noDeptCode", body.get("curDeptCode"));
    param.put("assistId", body.get("assistId"));
    param.put("type", "1".equals(type) ? 1 : 2);
    //查询需要删除的关联id, 部门code
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJGLQBXSDEPTGETIDCODE");
    List<Map<String, Object>> deptInfos = (List<Map<String, Object>>) baseService.list(param);
    if (deptInfos != null && deptInfos.size() > 0) {
      List<Object> qdIds = new ArrayList<>();
      List<Object> codes = new ArrayList<>();
      for (Map<String, Object> m : deptInfos) {
        qdIds.add(m.get("id"));
        codes.add(m.get("deptCode"));
      }
      param = new HashMap<>();
      param.put("qbxsIds", ids);
      param.put("ids", qdIds);
      param.put("assistId", body.get("assistId"));
      param.put("codes", codes);
      param.put("assistType", "1".equals(type) ? 1 : 2);
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJGLQBXSDEPTREISSUE");
      baseService.remove(param);
    }
  }

  private void saveMapper(String ids, Object id, Object assistType) {
    List<Map<String, Object>> qbxsDeptList = new ArrayList<>();
    String[] arr = ids.split(",");
    for (String qbxsId : arr) {
      Map<String, Object> deptMap = new HashMap<>();
      deptMap.put("assistDeptId", id);
      deptMap.put("qbxsId", qbxsId);
      deptMap.put("transferred", 1);
      deptMap.put("assistType", assistType);
      qbxsDeptList.add(deptMap);
    }
    Map<String, Object> params = new HashMap<>();
    params.put("list", qbxsDeptList);
    params.put("alias", "AJGLQBXSDEPTBATCH");
    params.put("seqName", "AJGLQBXSDEPT");
    params.put("subSize", 20);
    batchSaveHandler.save(params);
  }

  private void createSignInfo(Map<String, Object> body, int num) throws Exception {
    List<Map<String, Object>> signs = new ArrayList<>();
    Map<String, Object> signData = new HashMap<>();
    signData.put("userId", body.get("userId"));
    signData.put("userName", body.get("userName"));
    signData.put("deptCode", body.get("curDeptCode"));
    signData.put("deptName", body.get("curDeptName"));
    signData.put("receiveDeptCode", body.get("acceptDeptCode"));
    signData.put("receiveDeptName", body.get("acceptDeptName"));
    signData.put("assistId", body.get("assistId"));
    signData.put("assistType", body.get("assistType"));
    signData.put("clueNum", num);
    signs.add(signData);
    Map<String, Object> signParam = new HashMap<>();
    signParam.put("list", signs);
    qbxsSignSaveHandler.save(signParam);
  }

  /**
   * 根据业务id 获取线索的总数和已分配的数量
   *
   * @param assistId
   * @return
   * @throws Exception
   */
  public Map<String, Object> getClueTotal(String assistId) throws Exception {
    Map<String, Object> result = new HashMap<>();
    Map<String, Object> param = new HashMap<>();
    param.put("assistId", assistId);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJGLQBXSBASENUMTOTAL");
    Map<String, Object> clue = (Map<String, Object>) baseService.get(param);
    result.put("clueTotal", 0);
    result.put("clueDistribute", 0);
    if (clue != null && clue.containsKey("total")) {
      result.put("clueTotal", clue.get("total"));
    }
    if (clue != null && clue.containsKey("distribute")) {
      result.put("clueDistribute", clue.get("distribute"));
    }
    return result;
  }

  /**
   * 根据业务id 获取线索的总数和已分配的数量
   *
   * @return
   * @throws Exception
   */
  public Map<String, Object> removeClue(Map<String, Object> body) throws Exception {
    Object assistId = body.get("assistId");
    Map<String, Object> delMap = new HashMap<>();
    delMap.put("assistId", assistId);
    delMap.put("qbxsId", body.get("qbxsId"));
    delMap.put("assistType", "1".equals(String.valueOf(body.get("assistType"))) ? 1 : 2);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJGLQBXSBASE");
    Map<String, Object> clue = (Map<String, Object>) baseService.get(delMap);
    if (clue == null) {
      throw new GlobalErrorException("999680", "线索不存在");
    }
    if ("2".equals(String.valueOf(clue.get("qbxsSign")))) {
      throw new GlobalErrorException("999681", "线索已签收不能删除");
    }
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJGLQBXSBASEDEL");
    baseService.remove(delMap);
    if (body.get("qbxsDeptId") != null) {
      delAndCancel(body);

    }
    return getClueTotal(String.valueOf(assistId));
  }

  private void delAndCancel(Map<String, Object> body) throws Exception {
    //删线索部门关系信息及签收反馈信息
    Map<String, Object> baseP = new HashMap<>();
    baseP.put("qbxsIds", body.get("qbxsId"));
    baseP.put("qbxsDeptIds", body.get("qbxsDeptId"));
    baseP.put("assistId", body.get("assistId"));
    if (!StringUtils.isEmpty(body.get("receiveCode"))) {
      List codes = new ArrayList<>();
      codes.add(body.get("receiveCode"));
      baseP.put("codes", codes);
    }
    baseP.put("assistType", "1".equals(String.valueOf(body.get("assistType"))) ? 1 : 2);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJGLQBXSDEPTDEL");
    baseService.update("", baseP);
  }

  /**
   * 根据业务id 获取线索的总数和已分配的数量
   *
   * @return
   * @throws Exception
   */
  public Map<String, Object> cancelDistribute(Map<String, Object> body) throws Exception {
    Object assistId = body.get("assistId");
    Object qbxsId = body.get("qbxsId");
    Map<String, Object> delMap = new HashMap<>();
    delMap.put("assistId", assistId);
    delMap.put("qbxsId", qbxsId);
    delMap.put("assistType", "1".equals(String.valueOf(body.get("assistType"))) ? 1 : 2);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJGLQBXSBASE");
    Map<String, Object> clue = (Map<String, Object>) baseService.get(delMap);
    if (clue == null) {
      throw new GlobalErrorException("999680", "线索不存在");
    }
    if ("2".equals(String.valueOf(clue.get("qbxsSign")))) {
      throw new GlobalErrorException("999681", "线索已签收不能取消");
    }
    if ("1".equals(String.valueOf(clue.get("qbxsDistribute")))) {
      throw new GlobalErrorException("999681", "线索已取消不能重复取消");
    }
    Map<String, Object> baseP = new HashMap<>();
    baseP.put("ids", qbxsId);
    baseP.put("qbxsDistribute", 1);
    baseP.put("qbxsResult", 1);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJGLQBXSBASEBATCHUPDATE");
    baseService.update("", baseP);
    //处理关联
    if (body.get("qbxsDeptId") != null) {
      delAndCancel(body);
    }
    return getClueTotal(String.valueOf(assistId));
  }

  /**
   * 线索反馈列表
   *
   * @param requestMap
   * @return
   * @throws Exception
   */
  public Object feedBackList(Map<String, Object> requestMap) throws Exception {
    int pageNum = Integer.parseInt(String.valueOf(requestMap.get("pageNum")));
    int pageSize = Integer.parseInt(String.valueOf(requestMap.get("pageSize")));

    Map<String, Object> params = new HashMap<>();
    params.put("assistType", requestMap.get("assistType"));
    if (StringUtils.isEmpty(requestMap.get("assistType"))) {
      params.put("assistType", 2);
    }
    params.put("assistId", requestMap.get("assistId"));
    //查询标题
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJGLQBXSBYASSIST");
    List<Map<String, Object>> titles = (List<Map<String, Object>>) baseService.list(params);
    if (titles == null || titles.size() == 0) {
      return new ArrayList();
    }
    List<Object> heads = new ArrayList<>();
    for (Map<String, Object> map : titles) {
      heads.add(map.get("title"));
    }
    Map<String, Object> result = new HashMap<>();
    result.put("titles", heads);

    //根据条件查询线索
    if (StringUtils.isEmpty(requestMap.get("assistType")) || "2".equals(String.valueOf(requestMap.get("assistType")))) {
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJGLQBXSBASEFEEDBACELIST");
    } else if ("1".equals(String.valueOf(requestMap.get("assistType")))) {
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJASSISTQBXSFEEDBACELIST");
    } else {
      return new ArrayList();
    }
    Paging pages = (Paging) baseService.page(requestMap, pageNum, pageSize);
    if (pages == null || pages.getList() == null || pages.getList().size() == 0) {
      result.put("list", new ArrayList<>());
      return result;
    }
    Map<String, Object> baseData = new HashMap<>();
    StringBuilder sbd = new StringBuilder();
    List<Map<String, Object>> list = pages.getList();
    for (Map<String, Object> base : list) {
      sbd.append(",").append(base.get("qbxsId"));
      baseData.put(String.valueOf(base.get("qbxsId")), base);
      if (base.get("syajAjbhs") != null) {
        String[] str = String.valueOf(base.get("syajAjbhs")).replaceAll("\\]", "").replaceAll("\\[", "").split(",");
        base.put("syajs", str.length);
      } else {
        base.put("syajs", 0);
      }
      if (base.get("zbxsAjbhs") != null) {
        int dhwd = 0, pzdb = 0, zhrys = 0, yjss = 0;
        double sajz = 0;
        List<String> ajbhs = new ArrayList<>();
        Map<String, Object> zbmap = JSONObject.toJavaObject(JSONObject.parseObject(String.valueOf(base.get("zbxsAjbhs"))), Map.class);
        for (String key : zbmap.keySet()) {
          String[] info = String.valueOf(zbmap.get(key)).split(",");
          if (info.length == 6) {
            dhwd += Integer.parseInt(info[1]);
            sajz += Double.parseDouble(info[2]);
            pzdb += Integer.parseInt(info[3]);
            zhrys += Integer.parseInt(info[4]);
            yjss += Integer.parseInt(info[5]);
          }
          ajbhs.add(key);
        }
        base.put("dhwd", dhwd);
        base.put("sajz", sajz);
        base.put("pzdb", pzdb);
        base.put("zhrys", zhrys);
        base.put("yjss", yjss);
        base.putAll(getAjInfoData(ajbhs));
      } else {
        base.put("dhwd", 0);
        base.put("sajz", 0);
        base.put("pzdb", 0);
        base.put("xsjl", 0);
        base.put("zhrys", 0);
        base.put("yjss", 0);
        base.put("larqCount", 0);
        base.put("parqCount", 0);
      }
      base.remove("zbajList");
      if ("2".equals(String.valueOf(requestMap.get("showType")))) {
        base.put("cityName", getRegin(String.valueOf(base.get("receiveCode")).substring(0, 6)));
      } else {
        base.put("cityName", getCity(String.valueOf(base.get("receiveName"))));
      }
      base.put("cityCode", String.valueOf(base.get("receiveCode")).substring(0, 4) + "00");
    }
    params = new HashMap<>();
    params.put("assistId", requestMap.get("assistId"));
    params.put("ids", sbd.toString().substring(1));
    LocalThreadStorage.put(Constant.CONTROLLER_PAGE, false);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJGLQBXSINFOBYASSIST");
    List<Map<String, Object>> values = (List<Map<String, Object>>) baseService.list(params);
    if (values == null || values.size() == 0) {
      return result;
    }
    //整合处理线索列表
    Map<String, Object> valMap = valusHandle(baseData, values);
    result.put("list", valMap.values());
    result.put("totalCount", pages.getTotalCount());
    result.put("pageSize", pages.getPageSize());
    result.put("pageNum", pages.getPageNum());
    return result;
  }

  /**
   * 线索列表
   *
   * @param requestMap
   * @return
   * @throws Exception
   */
  public Object ajglQbxsList(Map<String, Object> requestMap) throws Exception {
    int pageNum = Integer.parseInt(String.valueOf(requestMap.get("pageNum")));
    int pageSize = Integer.parseInt(String.valueOf(requestMap.get("pageSize")));
    String type = "";
    if (requestMap.containsKey("type") && !StringUtils.isEmpty(requestMap.get("type"))) {
      type = String.valueOf(requestMap.get("type"));
    }
    //查询标题
    Map<String, Object> params = new HashMap<>();
    params.put("assistId", requestMap.get("assistId"));
    if ("".equals(type) || "2".equals(type)) { // 集群战役
      params.put("assistType", 2);
    }
    if ("1".equals(type)) { // 案件协查
      params.put("assistType", 1);
    }
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJGLQBXSBYASSIST");
    List<Map<String, Object>> titles = (List<Map<String, Object>>) baseService.list(params);
    if (titles == null || titles.size() == 0) {
      return new ArrayList();
    }
    List<Object> heads = new ArrayList<>();
    for (Map<String, Object> map : titles) {
      heads.add(map.get("title"));
    }
    Map<String, Object> result = new HashMap<>();
    result.put("titles", heads);

    //根据条件查询线索
    if ("".equals(type) || "2".equals(type)) { // 集群战役
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJGLQBXSBASE");
    }
    if ("1".equals(type)) { // 案件协查
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJASSISTGLQBXSBASE");
    }
    Map<String, Object> baseData = new HashMap<>();
    StringBuilder sbd = new StringBuilder();
    Paging pages = (Paging) baseService.page(requestMap, pageNum, pageSize);
    if (pages == null || pages.getList() == null || pages.getList().size() == 0) {
      result.put("list", new ArrayList<>());
      return result;
    }
    List<Map<String, Object>> list = pages.getList();
    for (int i = 0; i < list.size(); i++) {
      Map<String, Object> base = list.get(i);
      sbd.append(",").append(base.get("qbxsId"));
      baseData.put(String.valueOf(base.get("qbxsId")), base);
    }
    params = new HashMap<>();
    params.put("assistId", requestMap.get("assistId"));
    params.put("ids", sbd.toString().substring(1));
    LocalThreadStorage.put(Constant.CONTROLLER_PAGE, false);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJGLQBXSINFOBYASSIST");
    List<Map<String, Object>> values = (List<Map<String, Object>>) baseService.list(params);
    if (values == null || values.size() == 0) {
      return result;
    }
    //整合处理线索列表
    Map<String, Object> valMap = valusHandle(baseData, values);
    result.put("list", valMap.values());
    result.put("totalCount", pages.getTotalCount());
    result.put("pageSize", pages.getPageSize());
    result.put("pageNum", pages.getPageNum());

//    LocalThreadStorage.getBoolean(Constant.CONTROLLER_PAGE);
//    Page page = PageHelper.startPage(pageNum, pageSize);
//    List newList = Arrays.asList(valMap.values());
//    LocalThreadStorage.put(Constant.CONTROLLER_PAGE_TOTALCOUNT, page.getTotal());
//    Paging paging = new Paging(pageSize, pageNum, page.getTotal(), newList);
//    result.put("totalCount", paging.getTotalCount());
//    result.put("pageSize", paging.getPageSize());
//    result.put("pageNum", paging.getPageNum());
    return result;
  }

  /**
   * 处理各列字段
   *
   * @param baseData
   * @param values
   */
  private Map<String, Object> valusHandle(Map<String, Object> baseData, List<Map<String, Object>> values) {
    Map<String, Object> valMap = new LinkedHashMap<>();
    for (Map<String, Object> map : values) {
      if (valMap.containsKey(String.valueOf(map.get("qbxsId")))) {
        Map<String, Object> valData = (Map<String, Object>) valMap.get(String.valueOf(map.get("qbxsId")));
        List<Object> vals = (List<Object>) valData.get("data");
        vals.add(map.get("value"));
        valMap.put(String.valueOf(map.get("qbxsId")), valData);
      } else {
        Map<String, Object> baseInfo = (Map<String, Object>) baseData.get(String.valueOf(map.get("qbxsId")));
        List<Object> vals = new ArrayList<>();
        vals.add(map.get("value"));
        Map<String, Object> valData = new HashMap<String, Object>();
        valData.putAll(baseInfo);
        valData.put("data", vals);
        valMap.put(String.valueOf(map.get("qbxsId")), valData);
      }
    }
    return valMap;
  }

  public Object feedBackDetail(Map<String, Object> requestMap) throws Exception {
    //查询
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "1".equals(String.valueOf(requestMap.get("assistType"))) ? "AJASSISTFEEDBACK" : "AJCLUSTERFEEDBACK");
    Map<String, Object> fb = (Map<String, Object>) baseService.get(requestMap);
    if (fb == null) {
      return new HashMap<>();
    }
    fb.put("syajs", StringUtils.isEmpty(fb.get("syajs")) ? 0 : 1);
    fb.put("zbxss", StringUtils.isEmpty(fb.get("zbxss")) ? 0 : 1);
    return fb;
  }

  public Object feedBackSyAJList(Map<String, Object> requestMap) throws Exception {
    //查询
    String assistType = String.valueOf(requestMap.get("assistType"));
    String feedBack_alias = "";
    String aj_alias = "";
    if ("1".equals(assistType)) { // 案件协查
      feedBack_alias = "AJASSISTFEEDBACK";
      aj_alias = "AJASSISTFEEDBACKAJINFO";
    }
    if ("".equals(assistType) || "2".equals(assistType)) { // 集群战役
      feedBack_alias = "AJCLUSTERFEEDBACK";
      aj_alias = "AJCLUSTERFEEDBACKAJINFO";
    }
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, feedBack_alias);
    Map<String, Object> fb = (Map<String, Object>) baseService.get(requestMap);
    if (fb == null) {
      return new ArrayList();
    }
    Object ys = fb.get("syajs");
    if (ys == null || StringUtils.isEmpty(ys)) {
      return new ArrayList();
    }
    List<String> ajbhs = JSONArray.toJavaObject(JSONArray.parseArray(String.valueOf(ys)), List.class);
    if (ajbhs == null || ajbhs.size() == 0) {
      return new ArrayList();
    }
    Map<String, Object> params = new HashMap<>();
    params.put("ajbhs", ajbhs);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, aj_alias);
    List<Map<String, Object>> ajList = (List<Map<String, Object>>) baseService.list(params);
    if (ajList == null || ajList.size() == 0) {
      return new ArrayList();
    }
    List syajList = new ArrayList();
    for (Map<String, Object> aj : ajList) {
      if (ajbhs.contains(String.valueOf(aj.get("AJBH")))) {
        Map<String, Object> o = new HashMap<>();
        o.put("ajmc", aj.get("AJMC"));
        o.put("ajbh", aj.get("AJBH"));
        o.put("ajlbName", aj.get("AJLB_NAME"));
        o.put("ajId", aj.get("ajId"));
        syajList.add(o);
      }
    }
    return syajList;
  }

  /**
   * 线索反馈时 案件侦办列表
   *
   * @param requestMap
   * @return
   * @throws Exception
   */
  public Object feedBackZbAJList(Map<String, Object> requestMap) throws Exception {
    //查询
    String assistType = String.valueOf(requestMap.get("assistType"));
    String feedBack_alias = "";
    String aj_alias = "";
    if ("1".equals(assistType)) { // 案件协查
      feedBack_alias = "AJASSISTFEEDBACK";
      aj_alias = "AJASSISTFEEDBACKAJINFO";
    }
    if ("".equals(assistType) || "2".equals(assistType)) { // 集群战役
      feedBack_alias = "AJCLUSTERFEEDBACK";
      aj_alias = "AJCLUSTERFEEDBACKAJINFO";
    }
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, feedBack_alias);
    Map<String, Object> fb = (Map<String, Object>) baseService.get(requestMap);
    if (fb == null) {
      return new ArrayList();
    }
    Object zb = fb.get("zbxss");
    if (zb == null || StringUtils.isEmpty(zb)) {
      return new ArrayList();
    }
    Map<String, Object> zbJson = JSONObject.toJavaObject(JSONObject.parseObject(String.valueOf(zb)), Map.class);
    if (zbJson == null) {
      return new ArrayList();
    }
    List<String> ajbhs = IteratorUtils.toList(zbJson.keySet().iterator());
    if (ajbhs == null || ajbhs.size() == 0) {
      return new ArrayList();
    }
    Map<String, Object> params = new HashMap<>();
    params.put("ajbhs", ajbhs);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, aj_alias);
    List<Map<String, Object>> ajList = (List<Map<String, Object>>) baseService.list(params);
    if (ajList == null || ajList.size() == 0) {
      return new ArrayList();
    }
    List zbajList = new ArrayList();
    int ajCount = 0, larqCount = 0, parqCount = 0, dh = 0, xsjl = 0, pzdb = 0, zhrys = 0, yjss = 0;
    double sajz = 0;
    for (Map<String, Object> aj : ajList) {
      if (ajbhs.contains(String.valueOf(aj.get("AJBH")))) {
        String zbInfo = String.valueOf(zbJson.get(String.valueOf(aj.get("AJBH"))));
        String[] infos = zbInfo.split(",");
        Map<String, Object> o = new HashMap<>();
        o.put("ajmc", aj.get("AJMC"));
        o.put("ajbh", aj.get("AJBH"));
        o.put("ajztName", aj.get("AJZT_NAME"));
        o.put("ajId", aj.get("ajId"));
        if (!StringUtils.isEmpty(aj.get("LARQ"))) {
          o.put("larq", aj.get("LARQ"));
          larqCount++;
        } else {
          o.put("larq", 0);
        }
        if (!StringUtils.isEmpty(aj.get("PARQ"))) {
          o.put("parq", aj.get("PARQ"));
          parqCount++;
        } else {
          o.put("parq", 0);
        }
        if (infos.length > 1 && !StringUtils.isEmpty(infos[1])) {
          o.put("dhwd", infos[1]);
          dh += Integer.parseInt(String.valueOf(infos[1]));
        } else {
          o.put("dhwd", 0);
        }
        if (infos.length > 2 && !StringUtils.isEmpty(infos[2])) {
          double sajzOri = Double.parseDouble(infos[2]);
          if (sajzOri > 0) {
            o.put("sajz", sajzOri);
            sajz += sajzOri;
          } else {
            if (!StringUtils.isEmpty(aj.get("SAJZ"))) {
              o.put("sajz", aj.get("SAJZ"));
              sajz += Double.parseDouble(String.valueOf(aj.get("SAJZ")));
            } else {
              o.put("sajz", 0);
            }
          }
        } else {
          o.put("sajz", 0);
        }
        if (infos.length > 3 && !StringUtils.isEmpty(infos[3])) {
          o.put("pzdb", infos[3]);
          pzdb += Integer.parseInt(String.valueOf(infos[3]));
        } else {
          o.put("pzdb", 0);
        }
        if (infos.length > 4 && !StringUtils.isEmpty(infos[4])) {
          int zhrysOri = Integer.parseInt(String.valueOf(infos[4]));
          if (zhrysOri > 0) {
            o.put("zhrys", zhrysOri);
            zhrys += zhrysOri;
          } else {
            if (!StringUtils.isEmpty(aj.get("ZHRYS"))) {
              o.put("zhrys", aj.get("ZHRYS"));
              zhrys += Integer.parseInt(String.valueOf(aj.get("ZHRYS")));
            } else {
              o.put("zhrys", 0);
            }
          }
        } else {
          o.put("zhrys", 0);
        }
        if (infos.length > 5 && !StringUtils.isEmpty(infos[5])) {
          o.put("yjss", infos[5]);
          yjss += Integer.parseInt(String.valueOf(infos[5]));
        } else {
          o.put("yjss", 0);
        }
        if (!StringUtils.isEmpty(aj.get("ryclcs"))) {
          o.put("ryclcs", aj.get("ryclcs"));
          xsjl += Integer.parseInt(String.valueOf(aj.get("ryclcs")));
        } else {
          o.put("ryclcs", 0);
        }
        zbajList.add(o);
        ajCount++;
      }
    }

    Map<String, Object> count = new HashMap<>();
    count.put("ajmc", ajCount);
    count.put("ajztName", "-");
    count.put("ajbh", "-");
    count.put("larq", larqCount);
    count.put("parq", parqCount);
    count.put("dhwd", dh);
    count.put("pzdb", pzdb);
    count.put("sajz", sajz);
    count.put("ryclcs", xsjl);
    count.put("zhrys", zhrys);
    count.put("yjss", yjss);
    zbajList.add(count);
    return zbajList;
  }


  public Object feedBackResultList(Map<String, Object> requestMap) throws Exception {
    //查询
    String type = "";
    if (requestMap.containsKey("type") && !StringUtils.isEmpty(requestMap.get("type"))) {
      type = String.valueOf(requestMap.get("type"));
    }
    // 1.案件协查  2.集群战役
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "1".equals(type) ? "AJASSISTTJFKXX" : "AJCLUSTERTJFKXX");
    List<Map<String, Object>> list = (List<Map<String, Object>>) baseService.list(requestMap);
    if (list == null || list.size() == 0) {
      return new ArrayList();
    }
    int xsNumSum = 0, csSum = 0, cfSum = 0, whcSum = 0, laNum = 0, paNum = 0, dhwdSum = 0, pzdbSum = 0,
        xsjlSum = 0, ysajList = 0, zhrysSum = 0, yjssSum = 0;
    double sajzSum = 0;
    Map<String, Object> resList = new LinkedHashMap<>();
    for (Map<String, Object> m : list) {
      m.put("deptType", getDeptType(String.valueOf(m.get("deptCode"))));
      if (m.get("ysajList") != null) {
        String[] str = String.valueOf(m.get("ysajList")).replaceAll("\\]", "").replaceAll("\\[", "").split(",");
        m.put("ysajList", str.length);
        ysajList += str.length;
      } else {
        m.put("ysajList", 0);
      }
      if (m.get("zbajList") != null) {
        int dhwd = 0, pzdb = 0, zhrys = 0, yjss = 0;
        double sajz = 0;
        String[] zbs = String.valueOf(m.get("zbajList")).split("_");
        List<String> ajbhs = new ArrayList<>();
        for (String s : zbs) {
          Map<String, Object> zbmap = JSONObject.toJavaObject(JSONObject.parseObject(s), Map.class);
          for (String key : zbmap.keySet()) {
            String[] info = String.valueOf(zbmap.get(key)).split(",");
            if (info.length == 6) {
              dhwd += Integer.parseInt(info[1]);
              sajz += Double.parseDouble(info[2]);
              pzdb += Integer.parseInt(info[3]);
              zhrys += Integer.parseInt(info[4]);
              yjss += Integer.parseInt(info[5]);
            }
            ajbhs.add(key);
          }
        }
        m.put("dhwd", dhwd);
        m.put("sajz", sajz);
        m.put("pzdb", pzdb);
        m.put("zhrys", zhrys);
        m.put("yjss", yjss);
        Map res = getAjInfoData(ajbhs);
        m.putAll(res);
        dhwdSum += dhwd;
        sajzSum += sajz;
        pzdbSum += pzdb;
        zhrysSum += zhrys;
        yjssSum += yjss;
        if (res.containsKey("xsjl") && !StringUtils.isEmpty(res.get("xsjl"))) {
          xsjlSum += Integer.parseInt(String.valueOf(res.get("xsjl")));
        }
        if (res.containsKey("larqCount") && !StringUtils.isEmpty(res.get("larqCount"))) {
          laNum += Integer.parseInt(String.valueOf(res.get("larqCount")));
        }
        if (res.containsKey("parqCount") && !StringUtils.isEmpty(res.get("parqCount"))) {
          paNum += Integer.parseInt(String.valueOf(res.get("parqCount")));
        }
      } else {
        m.put("dhwd", 0);
        m.put("sajz", 0);
        m.put("pzdb", 0);
        m.put("zhrys", 0);
        m.put("yjss", 0);
        m.put("xsjl", 0);
        m.put("larqCount", 0);
        m.put("parqCount", 0);
      }
      m.remove("zbajList");
      int cs = Integer.parseInt(String.valueOf(m.get("cs")));
      int cf = Integer.parseInt(String.valueOf(m.get("cf")));
      int whc = Integer.parseInt(String.valueOf(m.get("whc")));
      xsNumSum += Integer.parseInt(String.valueOf(m.get("xsNum")));
      csSum += cs;
      cfSum += cf;
      whcSum += whc;
      int hcs = cs + cf;
      int total = whc + hcs;
      if (whc == 0) {
        m.put("hcl", 100);
      } else {
        m.put("hcl", new BigDecimal(String.valueOf(hcs)).divide(new BigDecimal(String.valueOf(total)), 2, RoundingMode.DOWN).multiply(new BigDecimal("100")).setScale(0, RoundingMode.DOWN));
      }
      resList.put(String.valueOf(m.get("cityCode")), m);
    }

    Map<String, Object> count = new HashMap<>();
    count.put("deptName", "-");
    count.put("deptCode", "-");
    count.put("xsNum", xsNumSum);
    count.put("cf", cfSum);
    count.put("cs", csSum);
    count.put("whc", whcSum);
    count.put("larqCount", laNum);
    count.put("parqCount", paNum);
    count.put("dhwd", dhwdSum);
    count.put("pzdb", pzdbSum);
    count.put("zhrys", zhrysSum);
    count.put("yjss", yjssSum);
    count.put("xsjl", xsjlSum);
    count.put("sajz", sajzSum);
    count.put("ysajList", ysajList);

    BigDecimal hcSum = new BigDecimal(String.valueOf(csSum + cfSum));
    if (xsNumSum > 0) {
      hcSum = hcSum.divide(new BigDecimal(String.valueOf(xsNumSum)), 2, RoundingMode.DOWN).multiply(new BigDecimal("100")).setScale(0, RoundingMode.DOWN);
      count.put("hcl", hcSum.intValue());
    } else {
      count.put("hcl", 0);
    }

    if ("1".equals(requestMap.get("curDeptType")) || "2".equals(requestMap.get("curDeptType"))) {
      //厅或者支队查看所有支队，这里查询所有的支队在和数据封装
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "1".equals(type) ? "AJASSISTTJZDFKXX" : "AJCLUSTERTJZDFKXX");
      List<Map<String, Object>> deptRes = (List<Map<String, Object>>) baseService.list(requestMap);
      if (deptRes == null || deptRes.size() == 0) {
        return new ArrayList();
      }
      List<Map<String, Object>> result = new ArrayList<>();
      for (Map<String, Object> map : deptRes) {
        Map<String, Object> data = (Map<String, Object>) resList.get(String.valueOf(map.get("cityCode")));
        if (data != null) {
          data.remove("deptCode");
          data.remove("deptName");
          map.putAll(data);
          map.put("deptType", getDeptType(String.valueOf(map.get("deptCode"))));
          result.add(map);
        }
      }
      result.add(count);
      return result;
    }
    list.add(count);
    return list;
  }

  private int getDeptType(String deptCode) {
    if (StringUtils.isEmpty(deptCode)) {
      return 0;
    }
    if ("610000".equals(deptCode.substring(0, 6))) {
      return 1;
    } else {
      if (!"00".equals(deptCode.substring(deptCode.length() - 2, deptCode.length()))) {
        return 4;
      } else {
        if ("00".equals(deptCode.substring(4, 6)) && "0000".equals(deptCode.substring(deptCode.length() - 4, deptCode.length()))) {
          return 2;
        } else {
          return 3;
        }
      }
    }
  }

  private Map<String, Integer> getAjInfoData(List<String> ajbhs) throws Exception {
    Map<String, Integer> res = new HashMap<>();
    res.put("xsjl", 0);
    res.put("larqCount", 0);
    res.put("parqCount", 0);
    Map<String, Object> params = new HashMap<>();
    params.put("ajbhs", ajbhs);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJCLUSTERFEEDBACKAJINFO");
    List<Map<String, Object>> ajList = (List<Map<String, Object>>) baseService.list(params);
    if (ajList == null || ajList.size() == 0) {
      return res;
    }
    int xsjl = 0, larqCount = 0, parqCount = 0;
    for (Map<String, Object> aj : ajList) {
      if (!StringUtils.isEmpty(aj.get("ryclcs"))) {
        xsjl += Integer.parseInt(String.valueOf(aj.get("ryclcs")));
      }
      if (!StringUtils.isEmpty(aj.get("LARQ"))) {
        larqCount++;
      }
      if (!StringUtils.isEmpty(aj.get("PARQ"))) {
        parqCount++;
      }
    }
    res.put("xsjl", xsjl);
    res.put("larqCount", larqCount);
    res.put("parqCount", parqCount);
    return res;
  }

  private String getCity(String name) {
    if (name.contains("省")) {
      if (name.contains("市")) {
        return name.substring(3, name.indexOf("市") + 1);
      }
      if (name.contains("区")) {
        return name.substring(3, name.indexOf("区") + 1);
      }
    } else {
      if (name.contains("市")) {
        return name.substring(0, name.indexOf("市") + 1);
      }
      if (name.contains("区")) {
        return name.substring(0, name.indexOf("区") + 1);
      }
    }
    return "";
  }

  private String getRegin(String code) throws Exception {
    Map<String, Object> params = new HashMap<>();
    params.put("lx", "xzqh");
    params.put("code", code.substring(0, 4));
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "TCPCODELEFTLIKE");
    List<Map<String, Object>> maps = (List<Map<String, Object>>) baseService.list(params);
    Map<String, Object> result = new HashMap<>();
    for (Map<String, Object> map : maps) {
      result.put(String.valueOf(map.get("code")), map.get("codeName"));
    }
    String str = String.valueOf(result.get(code));
    return str.replace("陕西省","");
  }


}
