package com.nmghr.service.ajglqbxs;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.common.exception.GlobalErrorException;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.page.Paging;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.hander.save.ajglqbxs.QbxsSignSaveHandler;
import com.nmghr.hander.save.cluster.CaseAssistSubmitSaveHandler;
import com.nmghr.hander.save.cluster.DeptMapperSaveHandler;
import com.nmghr.hander.save.common.BatchSaveHandler;
import com.nmghr.handler.message.QueueConfig;
import com.nmghr.handler.service.SendMessageService;
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
  @Autowired
  private SendMessageService sendMessageService;

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
    baseP.put("ids", Arrays.asList(ids.split(",")));
    baseP.put("qbxsDistribute", 2);
    baseP.put("qbxsResult", 1);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJGLQBXSBASEBATCHUPDATE");
    baseService.update("", baseP);

    //删除线索部门关系信息
    Map<String, Object> param = new HashMap<>();
    param.put("qbxsIds", Arrays.asList(ids.split(",")));
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
    String ids = String.valueOf(body.get("ids"));//线索ids
    List<Object> qbxsIds = Arrays.asList(ids.split(","));
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
    baseP.put("ids", qbxsIds);
    baseP.put("qbxsDistribute", 2);
    baseP.put("qbxsResult", 1);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJGLQBXSBASEBATCHUPDATE");
    baseService.update("", baseP);

    //修改关联表为已下发
    Map<String, Object> p = new HashMap<>();
    p.put("qbxsIds", qbxsIds);
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
      createSignInfo(body, qbxsIds.size());
      saveRecords(body, qbxsIds);
    }
    return getClueTotal(String.valueOf(body.get("assistId")));
  }

  private void saveRecords(Map<String, Object> body, List<Object> qbxsIds) {
    List<Map<String, Object>> datas = new ArrayList<>();
    //下发线索增加流转记录
    for(int i=0;i<qbxsIds.size();i++){
      Map<String, Object> map = new HashMap<>();
      map.put("qbxsId", qbxsIds.get(i));
      map.put("assistType", body.get("assistType"));
      map.put("assistId", body.get("assistId"));
      map.put("receiveCode", body.get("acceptDeptCode"));
      map.put("receiveName", body.get("acceptDeptName"));
      map.put("createName", body.get("curDeptName"));
      map.put("createCode", body.get("curDeptCode"));
      map.put("creatorId", body.get("userId"));
      map.put("creatorName", body.get("userName"));
      map.put("optCategory", 3);
      datas.add(map);
    }
    Map<String, Object> param = new HashMap<>();
    param.put("type", "batch");
    param.put("list", datas);
    sendMessageService.sendMessage(param, QueueConfig.AJGLQBXSRECORD);
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
      for (Map<String, Object> m : deptInfos) {
        delSignDept(m.get("deptId"), body.get("assistId"), m.get("deptCode"), "1".equals(type) ? 1 : 2);
      }
    }
  }

  private void delSignDept(Object assistDeptId, Object assistId, Object code, int type) throws Exception {
    List<Object> codes = new ArrayList<>();
    codes.add(code);
    Map<String, Object> delP = new HashMap<>();
    delP.put("assistDeptId", assistDeptId);
    delP.put("assistType", type);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJGLQBXSDEPT");
    List<Map<String, Object>> clueDeptList = (List<Map<String, Object>>) baseService.list(delP);
    delP.put("signDelFlag", "nodel"); // 删除签收
    if (clueDeptList == null || clueDeptList.size() < 1) {
      delP.put("signDelFlag", "del"); // 删除签收
    }
    delP.put("codes", codes);
    delP.put("assistId", assistId);

    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJGLQBXSSIGNDEL");
    baseService.remove(delP);
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
    if("addRecord".equals(body.get("opt"))){
      //增加记录
      Map<String, Object> map = new HashMap<>();
      map.put("qbxsId", body.get("qbxsId"));
      map.put("assistType", body.get("assistType"));
      map.put("assistId", assistId);
      map.put("createName", body.get("curDeptName"));
      map.put("createCode", body.get("curDeptCode"));
      map.put("creatorId", body.get("userId"));
      map.put("creatorName", body.get("userName"));
      map.put("optCategory", 7);
      List<Map<String, Object>> datas = new ArrayList<>();
      datas.add(map);
      Map<String, Object> param = new HashMap<>();
      param.put("type", "batch");
      param.put("list", datas);
      sendMessageService.sendMessage(param, QueueConfig.AJGLQBXSRECORD);
    }
    return getClueTotal(String.valueOf(assistId));
  }

  private void delAndCancel(Map<String, Object> body) throws Exception {
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJGLQBXSDEPTONE");
    Map<String, Object> map = (Map<String, Object>) baseService.get(String.valueOf(body.get("qbxsDeptId")));
    //删线索部门关系信息及签收反馈信息
    Map<String, Object> baseP = new HashMap<>();
    baseP.put("qbxsIds", Arrays.asList(String.valueOf(body.get("qbxsId")).split(",")));
    baseP.put("qbxsDeptIds", body.get("qbxsDeptId"));
    baseP.put("assistId", body.get("assistId"));
    baseP.put("assistType", "1".equals(String.valueOf(body.get("assistType"))) ? 1 : 2);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJGLQBXSDEPTDEL");
    baseService.update("", baseP);
    if (map != null) {
      delSignDept(map.get("deptId"), body.get("assistId"), body.get("receiveCode"), "1".equals(String.valueOf(body.get("assistType"))) ? 1 : 2);
    }
  }

  /**
   * 取消分发
   *
   * @return
   * @throws Exception
   */
  public Map<String, Object> cancelDistribute(Map<String, Object> body) throws Exception {
    long s = System.currentTimeMillis();
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
    //处理线索状态
    Map<String, Object> baseP = new HashMap<>();
    baseP.put("ids", Arrays.asList(String.valueOf(qbxsId).split(",")));
    baseP.put("qbxsDistribute", 1);
    baseP.put("qbxsResult", 1);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJGLQBXSBASEBATCHUPDATE");
    baseService.update("", baseP);
    //处理关联
    if (body.get("qbxsDeptId") != null) {
      delAndCancel(body);
    }
    System.out.println("AAA"+(System.currentTimeMillis()-s));
    s = System.currentTimeMillis();
    if("addRecord".equals(body.get("opt"))){
      //增加记录
      Map<String, Object> map = new HashMap<>();
      map.put("qbxsId", qbxsId);
      map.put("assistType", body.get("assistType"));
      map.put("assistId", assistId);
//      map.put("receiveCode", body.get("acceptDeptCode"));
//      map.put("receiveName", body.get("acceptDeptName"));
      map.put("createName", body.get("curDeptName"));
      map.put("createCode", body.get("curDeptCode"));
      map.put("creatorId", body.get("userId"));
      map.put("creatorName", body.get("userName"));
      map.put("optCategory", 5);
      List<Map<String, Object>> datas = new ArrayList<>();
      datas.add(map);
      Map<String, Object> param = new HashMap<>();
      param.put("type", "batch");
      param.put("list", datas);
      sendMessageService.sendMessage(param, QueueConfig.AJGLQBXSRECORD);
    }
    System.out.println("BBB"+(System.currentTimeMillis()-s));
    return getClueTotal(String.valueOf(assistId));
  }

  /**
   * 转回线索
   *
   * @return
   * @throws Exception
   */
  public Map<String, Object> qbxsReturn(Map<String, Object> body) throws Exception {
    long s = System.currentTimeMillis();
    Object assistId = body.get("assistId");
    Object qbxsId = body.get("qbxsId");
    Map<String, Object> delMap = new HashMap<>();
    delMap.put("assistId", assistId);
    delMap.put("qbxsId", qbxsId);
    delMap.put("assistType", "1".equals(String.valueOf(body.get("assistType"))) ? 1 : 2);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJGLQBXSBASE");
    //处理线索状态
    Map<String, Object> baseP = new HashMap<>();
    baseP.put("ids", Arrays.asList(String.valueOf(qbxsId).split(",")));
    baseP.put("qbxsDistribute", 1);
    baseP.put("qbxsResult", 1);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJGLQBXSBASEBATCHUPDATE");
    baseService.update("", baseP);
    //处理关联
    if (body.get("qbxsDeptId") != null) {
      delAndCancel(body);
    }
    //如果是只给总队增加部门关联

    System.out.println("AAA"+(System.currentTimeMillis()-s));
    s = System.currentTimeMillis();
    if("addRecord".equals(body.get("opt"))){
      //增加记录
      Map<String, Object> map = new HashMap<>();
      map.put("qbxsId", qbxsId);
      map.put("assistType", body.get("assistType"));
      map.put("assistId", assistId);
      map.put("receiveCode", body.get("acceptDeptCode"));
      map.put("receiveName", body.get("acceptDeptName"));
      map.put("createName", body.get("curDeptName"));
      map.put("createCode", body.get("curDeptCode"));
      map.put("creatorId", body.get("userId"));
      map.put("creatorName", body.get("userName"));
      map.put("optCategory", 4);
      List<Map<String, Object>> datas = new ArrayList<>();
      datas.add(map);
      Map<String, Object> param = new HashMap<>();
      param.put("type", "batch");
      param.put("list", datas);
      sendMessageService.sendMessage(param, QueueConfig.AJGLQBXSRECORD);
    }
    System.out.println("BBB"+(System.currentTimeMillis()-s));
    return getClueTotal(String.valueOf(assistId));
  }

//  private void reBackMaster(Map<String, Object> body) throws Exception {
//    String qbxsId = String.valueOf(body.get("qbxsId"));//线索ids
//    List<Object> qbxsIds = Arrays.asList(qbxsId.split(","));
//    // 分配部门
//    String assistType = String.valueOf(body.get("assistType"));
//    //增加协查分配的部门信息
//    Map<String, Object> deptP = new HashMap<>();
//    deptP.put("deptCode", body.get("receiveDept"));
//    deptP.put("deptName", body.get("receiveDeptName"));
//    Object id = "";
//    if ("2".equals(assistType)) { // 集群战役
//      deptP.put("clusterId", body.get("assistId"));
//      id = deptMapperSaveHandler.save(deptP);
//    }
//    if ("1".equals(assistType)) { // 案件协查
//      deptP.put("assistId", body.get("assistId"));
//      id = caseAssistSubmitSaveHandler.saveDept(deptP);
//    }
//    //修改base表为未分发
//    Map<String, Object> baseP = new HashMap<>();
//    baseP.put("ids", qbxsIds);
//    baseP.put("qbxsDistribute", 1);
//    baseP.put("qbxsResult", 1);
//    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJGLQBXSBASEBATCHUPDATE");
//    baseService.update("", baseP);
//
//    //删除当前单位以外要修改的线索部门关系信息
//    reIssue(body, qbxsId, assistType);
//
//    //增加新的关联信息
//    saveMapper(qbxsId, id, body.get("assistType"));
//  }


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

    if (!StringUtils.isEmpty(requestMap.get("qbxsResult"))) {
      String qbxsResult = String.valueOf(requestMap.get("qbxsResult"));
      String[] res = qbxsResult.split(",");
      requestMap.put("qbxsResult", Arrays.asList(res));
    } else {
      requestMap.put("qbxsResult", null);
    }

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
    Map<String, Object> result = new LinkedHashMap<>();
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
    Map<String, Object> cityMap = new HashMap<>();
    if ("2".equals(String.valueOf(requestMap.get("showType")))) {
      cityMap = getRegin("61");
    }
    Map<String, Object> baseData = new LinkedHashMap<>();
    StringBuilder sbd = new StringBuilder();
    List<Map<String, Object>> list = pages.getList();
    for (Map<String, Object> base : list) {
      sbd.append(",").append(base.get("qbxsId"));
      base.put("ysxz", "2".equals(String.valueOf(base.get("handleResult"))) ? 1 : 0);
      baseData.put(String.valueOf(base.get("qbxsId")), base);
      if (!StringUtils.isEmpty(base.get("zbxsAjbhs"))) {
        Map res = getAjInfoData(ajbhCheck(String.valueOf(base.get("zbxsAjbhs"))));
        base.putAll(res);
      } else {
        base.put("dhwd", 0);
        base.put("sajz", "0.00");
        base.put("pzdb", 0);
        base.put("xsjl", 0);
        base.put("zhrys", 0);
        base.put("yjss", 0);
        base.put("larqCount", 0);
        base.put("parqCount", 0);
      }
      base.remove("zbajList");
      if ("2".equals(String.valueOf(requestMap.get("showType")))) {
        String cityName = String.valueOf(cityMap.get(String.valueOf(base.get("receiveCode")).substring(0, 6)));
        base.put("cityName", cityName.replace("陕西省", ""));
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
    Map<String, Object> result = new LinkedHashMap<>();
    result.put("titles", heads);

    //根据条件查询线索
    if ("".equals(type) || "2".equals(type)) { // 集群战役
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJGLQBXSBASE");
    }
    if ("1".equals(type)) { // 案件协查
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJASSISTGLQBXSBASE");
    }
    Map<String, Object> baseData = new LinkedHashMap<>();
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
      if ("execute".equals(requestMap.get("queryType"))) {
        base.put("distributeAble", String.valueOf(base.get("receiveCode")).equals(String.valueOf(requestMap.get("deptCode"))) ? 1 : 2);
      }

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
   * 处理各列字段
   *
   * @param baseData
   * @param values
   */
  private Map<String, Object> valusHandle(Map<String, Object> baseData, List<Map<String, Object>> values) {
    Map<String, Object> valMap = new HashMap<>();
    for (Map<String, Object> map : values) {
      if (valMap.containsKey(String.valueOf(map.get("qbxsId")))) {
        List<Object> vals = (List<Object>) valMap.get(String.valueOf(map.get("qbxsId")));
        vals.add(map.get("value"));
        valMap.put(String.valueOf(map.get("qbxsId")), vals);
      } else {
        List<Object> vals = new ArrayList<>();
        vals.add(map.get("value"));
        valMap.put(String.valueOf(map.get("qbxsId")), vals);
      }
    }
    for (String key : baseData.keySet()) {
      Map<String, Object> baseInfo = (Map<String, Object>) baseData.get(key);
      baseInfo.put("data", valMap.get(key));
    }
    return baseData;
  }

  public Object feedBackDetail(Map<String, Object> requestMap) throws Exception {
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "1".equals(String.valueOf(requestMap.get("assistType"))) ? "AJASSISTFEEDBACK" : "AJCLUSTERFEEDBACK");
    Map<String, Object> fb = (Map<String, Object>) baseService.get(requestMap);
    if (fb == null) {
      return new HashMap<>();
    }
    //fbId,assistId,ajglQbxsId,assistDeptId,handleResult,zbxss,backResult,backFiles
    //处理案件统计
    if (!StringUtils.isEmpty(fb.get("zbxss"))) {
      fb.put("zbxssList", ajinfo(Arrays.asList(String.valueOf(fb.get("zbxss")).split(","))));
    } else {
      fb.put("zbxssList", new ArrayList<>());
      fb.put("zbxss", "");
    }
    return fb;
  }

  /**
   * 线索反馈时 案件侦办列表
   *
   * @throws Exception
   */
  private Object ajinfo(List<String> ajbhs) throws Exception {
    if (ajbhs == null || ajbhs.size() == 0) {
      return new ArrayList();
    }
    Map<String, Object> params = new HashMap<>();
    params.put("ajbhs", ajbhs);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJASSISTFEEDBACKAJLIST");
    List<Map<String, Object>> ajList = (List<Map<String, Object>>) baseService.list(params);
    if (ajList == null || ajList.size() == 0) {
      return new ArrayList();
    }
    List zbajList = new ArrayList();
    for (Map<String, Object> aj : ajList) {
      Map<String, Object> o = new HashMap<>();
      o.put("ajmc", aj.get("ajmc"));
      o.put("ajbh", aj.get("ajbh"));
      o.put("ajztName", aj.get("ajztName"));
      o.put("ajId", aj.get("ajId"));
      if (!StringUtils.isEmpty(aj.get("larq"))) {
        o.put("larq", aj.get("larq"));
      } else {
        o.put("larq", "");
      }
      if (!StringUtils.isEmpty(aj.get("parq"))) {
        o.put("parq", aj.get("parq"));
      } else {
        o.put("parq", "");
      }
      if (!StringUtils.isEmpty(aj.get("dhwds"))) {
        o.put("dhwd", aj.get("dhwds"));
      } else {
        o.put("dhwd", 0);
      }
      if (!StringUtils.isEmpty(aj.get("sajz"))) {
        o.put("sajz", aj.get("sajz"));
      } else {
        o.put("sajz", "0.00");
      }
      if (!StringUtils.isEmpty(aj.get("dbrys"))) {
        o.put("pzdb", aj.get("dbrys"));
      } else {
        o.put("pzdb", 0);
      }
      if (!StringUtils.isEmpty(aj.get("zhrys"))) {
        o.put("zhrys", aj.get("zhrys"));
      } else {
        o.put("zhrys", 0);
      }
      if (!StringUtils.isEmpty(aj.get("ysrys"))) {
        o.put("yjss", aj.get("ysrys"));
      } else {
        o.put("yjss", 0);
      }
      if (!StringUtils.isEmpty(aj.get("ryclcs"))) {
        o.put("ryclcs", aj.get("ryclcs"));
      } else {
        o.put("ryclcs", 0);
      }
      zbajList.add(o);
    }
    return zbajList;
  }


  /**
   * 地市区县反馈战果
   *
   * @param requestMap
   * @return
   * @throws Exception
   */
  public Object feedBackResultList(Map<String, Object> requestMap) throws Exception {
    //查询1.案件协查  2.集群战役
    String type = String.valueOf(requestMap.get("type"));
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "1".equals(type) ? "AJASSISTTJFKXX" : "AJCLUSTERTJFKXX");
    List<Map<String, Object>> list = (List<Map<String, Object>>) baseService.list(requestMap);
    if (list == null || list.size() == 0) {
      return new ArrayList();
    }
    int xsNumSum = 0, csSum = 0, cfSum = 0, whcSum = 0, laNum = 0, paNum = 0, dhwdSum = 0, pzdbSum = 0,
        xsjlSum = 0, zhrysSum = 0, yjssSum = 0, ysxzSum = 0;
    double sajzSum = 0;
    Map<String, Object> resList = new LinkedHashMap<>();
    for (Map<String, Object> m : list) {
      m.put("deptType", getDeptType(String.valueOf(m.get("deptCode"))));
      ysxzSum += Integer.parseInt(String.valueOf(m.get("ysxz")));//移送行政处理次数
      if (!StringUtils.isEmpty(m.get("zbajList"))) {
        Map res = getAjInfoData(ajbhCheck(String.valueOf(m.get("zbajList"))));
        m.putAll(res);
        if (res.containsKey("dhwd") && !StringUtils.isEmpty(res.get("dhwd"))) {
          dhwdSum += Integer.parseInt(String.valueOf(res.get("dhwd")));
        }
        if (res.containsKey("sajz") && !StringUtils.isEmpty(res.get("sajz"))) {
          sajzSum += Double.parseDouble(String.valueOf(res.get("sajz")));
        }
        if (res.containsKey("pzdb") && !StringUtils.isEmpty(res.get("pzdb"))) {
          pzdbSum += Integer.parseInt(String.valueOf(res.get("pzdb")));
        }
        if (res.containsKey("zhrys") && !StringUtils.isEmpty(res.get("zhrys"))) {
          zhrysSum += Integer.parseInt(String.valueOf(res.get("zhrys")));
        }
        if (res.containsKey("yjss") && !StringUtils.isEmpty(res.get("yjss"))) {
          yjssSum += Integer.parseInt(String.valueOf(res.get("yjss")));
        }
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
        m.put("dhwd", 0);//捣毁窝点
        m.put("sajz", "0.00");//涉案价值
        m.put("pzdb", 0);//逮捕
        m.put("zhrys", 0);//抓获
        m.put("yjss", 0);//移诉
        m.put("xsjl", 0);//刑拘
        m.put("larqCount", 0);//立案起
        m.put("parqCount", 0);//破案起
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
      if (total > 0) {
        m.put("hcl", new BigDecimal(String.valueOf(hcs)).divide(new BigDecimal(String.valueOf(total)), 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100")).setScale(2, RoundingMode.DOWN).toString());
      } else {
        m.put("hcl", "-");
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
    count.put("sajz", new BigDecimal(String.valueOf(sajzSum)).setScale(2, RoundingMode.HALF_UP).toString());
    count.put("ysxz", ysxzSum);

    BigDecimal hcSum = new BigDecimal(String.valueOf(csSum + cfSum));
    if (xsNumSum > 0) {
      count.put("hcl", hcSum.divide(new BigDecimal(String.valueOf(xsNumSum)), 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100")).setScale(2, RoundingMode.DOWN).toString());
    } else {
      count.put("hcl", "-");
    }

    if (!StringUtils.isEmpty(requestMap.get("curDeptCode")) && ("1".equals(requestMap.get("deptType")) || "2".equals(requestMap.get("deptType")))) {
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
          data.remove("score");
          data.remove("commentText");
          data.remove("signStatus");
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

  public Map<String, Object> getAjInfoCountData(List<String> ajbhs, Object fllb) throws Exception {
    Map<String, Object> res = new HashMap<>();
    res.put("larqCount", 0);//立案起
    res.put("parqCount", 0);//破案起
    res.put("zhrys", 0);//抓获
    res.put("xsjl", 0);//刑拘
    res.put("pzdb", 0);//逮捕
    res.put("yjss", 0);//移诉
    res.put("dhwd", 0);//捣毁窝点
    res.put("sajz", "0.00");//涉案价值
    if(ajbhs==null || ajbhs.size()==0){
      return res;
    }
    ajbhs = ajbhCheck(ajbhs);
    Map<String, Object> params = new HashMap<>();
    params.put("ajbhs", ajbhs);
    if (fllb != null) {
      params.put("fllb", fllb);
    }
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJASSISTFEEDBACKAJCOUNT");
    List<Map<String, Object>> ajList = (List<Map<String, Object>>) baseService.list(params);
    if (ajList == null || ajList.size() == 0) {
      return res;
    }
    Map<String, Object> ajinfo = ajList.get(0);
    if(ajinfo==null || ajinfo.size()==0){
      return res;
    }
    if(!StringUtils.isEmpty(ajinfo.get("sajz"))){
      ajinfo.put("sajz", new BigDecimal(String.valueOf(ajinfo.get("sajz"))).setScale(2, RoundingMode.HALF_UP).toString());
    }
    return ajinfo;
  }


  public Map<String, Object> getAjInfoData(List<String> ajbhs) throws Exception {
    Map<String, Object> res = new HashMap<>();
    res.put("larqCount", 0);//立案起
    res.put("parqCount", 0);//破案起
    res.put("zhrys", 0);//抓获
    res.put("xsjl", 0);//刑拘
    res.put("pzdb", 0);//逮捕
    res.put("yjss", 0);//移诉
    res.put("dhwd", 0);//捣毁窝点
    res.put("sajz", "0.00");//涉案价值
    if (ajbhs == null || ajbhs.size() == 0) {
      return res;
    }
    ajbhs = ajbhCheck(ajbhs);
    Map<String, Object> params = new HashMap<>();
    params.put("ajbhs", ajbhs);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJASSISTFEEDBACKAJINFO");
    List<Map<String, Object>> ajList = (List<Map<String, Object>>) baseService.list(params);
    if (ajList == null || ajList.size() == 0) {
      return res;
    }
    Map<String, Object> ajinfo = ajList.get(0);
    if(ajinfo==null || ajinfo.size()==0){
      return res;
    }
    if(!StringUtils.isEmpty(ajinfo.get("sajz"))){
      ajinfo.put("sajz", new BigDecimal(String.valueOf(ajinfo.get("sajz"))).setScale(2, RoundingMode.HALF_UP).toString());
    }
    return ajinfo;
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

  private Map<String, Object> getRegin(String code) throws Exception {
    Map<String, Object> params = new HashMap<>();
    params.put("lx", "xzqh");
    params.put("code", code);
    LocalThreadStorage.put(Constant.CONTROLLER_PAGE, false);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "TCPCODELEFTLIKE");
    List<Map<String, Object>> maps = (List<Map<String, Object>>) baseService.list(params);
    Map<String, Object> result = new HashMap<>();
    for (Map<String, Object> map : maps) {
      result.put(String.valueOf(map.get("code")), map.get("codeName"));
    }
    return result;
  }

  /**
   * 案件编号去重
   *
   * @return
   */
  private List<String> ajbhCheck(String ajbh) {
    List<String> ajbhs = new ArrayList<>();
    if (!ajbh.contains(",")) {
      ajbhs.add(ajbh);
      return ajbhs;
    }
    Map<String, Object> map = new HashMap<>();
    String[] zbajbhs = ajbh.split(",");
    for (String s : zbajbhs) {
      if (!StringUtils.isEmpty(s)) {
        map.put(s, s);
      }
    }
    return new ArrayList(map.values());
  }
  /**
   * 案件编号去重
   *
   * @return
   */
  private List<String> ajbhCheck(List<String> ajbhs) {
    Map<String, Object> map = new HashMap<>();
    for (String s : ajbhs) {
      if (!StringUtils.isEmpty(s)) {
        map.put(s, s);
      }
    }
    return new ArrayList(map.values());
  }

}
