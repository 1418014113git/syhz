package com.nmghr.hander.save.cluster;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.common.exception.GlobalErrorException;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractSaveHandler;
import com.nmghr.common.WorkOrder;
import com.nmghr.hander.dto.ApproveParam;
import com.nmghr.hander.save.ajglqbxs.QbxsSignSaveHandler;
import com.nmghr.hander.save.examine.ExamineSaveHandler;
import com.nmghr.service.ajglqbxs.CaseAssistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 案件协查
 */
@SuppressWarnings("unchecked")
@Service("caseAssistSubmitSaveHandler")
public class CaseAssistSubmitSaveHandler extends AbstractSaveHandler {

  public CaseAssistSubmitSaveHandler(IBaseService baseService) {
    super(baseService);
  }

  @Autowired
  private ExamineSaveHandler examineSaveHandler;

  @Autowired
  private QbxsSignSaveHandler qbxsSignSaveHandler;

  @Autowired
  private CaseAssistService caseAssistService;

  @Override
  @Transactional
  public Object save(Map<String, Object> body) throws Exception {
    if (!validName(String.valueOf(body.get("curDeptCode")), String.valueOf(body.get("title")), body.get("id"))) {
      throw new GlobalErrorException("999667", "案件协查标题已存在，请确认后重新输入！");
    }
    if(body.get("assistNumber")!=null){
      String assistId = "";
      if (body.containsKey("id") && !StringUtils.isEmpty(body.get("id"))) {
        assistId = String.valueOf(body.get("id"));
      }
      caseAssistService.checkAssistNumber(String.valueOf(body.get("curDeptCode")),String.valueOf(body.get("assistNumber")), assistId);
    }
    if (body.containsKey("status") && null != body.get("status") && "1".equals(String.valueOf(body.get("status")))) {
      Object id = null;
      if(body.get("id")==null){
        id = create(body);
      } else {
        id = body.get("id");
        modify(String.valueOf(id), body);
      }
      // 判断线索是否已导入
      if(!validNum(id)){
        throw new GlobalErrorException("999667", "未导入线索，请导入线索后再提交！");
      }
      createApprove(body, id, false); //创建审核
      return id;
    } else {
      Object id = null;
      if(body.get("id")==null){
        id = create(body);
      } else {
        id = body.get("id");
        modify(String.valueOf(id), body);
      }
      if ("4".equals(String.valueOf(body.get("status")))) {
        // 判断线索是否已导入
        if(!validNum(id)){
          throw new GlobalErrorException("999667", "未导入线索，请导入线索后再提交！");
        }
        createSignInfo(body.get("userId"), body.get("userName"), body.get("curDeptCode"), body.get("curDeptName"), id);
      }
      return id;
    }
  }

  /**
   * 验证是否重名
   */
  private boolean validName(String curDeptCode, String title, Object id) throws Exception {
    Map<String, Object> params = new HashMap<>();
    params.put("curDeptCode", curDeptCode);
    params.put("title", title);
    if (id != null) {
      params.put("id", id);
    }
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJASSISTTITLECHECK");
    Map<String, Object> reset = (Map<String, Object>) baseService.get(params);
    if (reset == null) {
      return true;
    }
    return Integer.parseInt(String.valueOf(reset.get("num"))) < 1;
  }

  /**
   * 查询线索数量
   *
   * @throws Exception e
   */
  private Boolean validNum(Object assistId) throws Exception {
    Map<String, Object> params = new HashMap<>();
    params.put("assistId", assistId);

    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJGLQBXSCHECK");
    Map<String, Object> reset = (Map<String, Object>) baseService.get(params);
    if (reset == null) {
      return false;
    }
    return Integer.parseInt(String.valueOf(reset.get("num"))) > 0;
  }

  /**
   * 修改
   * @param id
   * @param params
   * @throws Exception
   */
  private void modify(String id, Map<String, Object> params) throws Exception {
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJASSIST");
    baseService.update(id, params);
  }

  /**
   * 新增案件协查
   *
   * @param params Map
   * @throws Exception e
   */
  private Object create(Map<String, Object> params) throws Exception {
    if(params.get("assistNumber")==null){
      //自动生成编号
      params.put("assistNumber", caseAssistService.number(String.valueOf(params.get("curDeptCode")), 1));
    }
    if(!StringUtils.isEmpty(params.get("acceptDept"))){
      params.put("checkDeptCode", params.get("acceptDept"));
    }
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJASSIST");
    return baseService.save(params);
  }


  /**
   * 新增审核记录
   *
   * @param params Map
   * @throws Exception e
   */
  private void createApprove(Map<String, Object> params, Object clusterId, Boolean checkFlag) {
    ApproveParam approve = new ApproveParam();
    approve.setWdType(WorkOrder.caseAssist.getType());
    approve.setWdStatus(checkFlag ? 3 : 1);
    approve.setUserId(params.get("userId"));
    approve.setUserName(params.get("userName"));
    approve.setCurDeptId(params.get("curDeptId"));
    approve.setCurDeptName(params.get("curDeptName"));
    approve.setWdTable(WorkOrder.caseAssist.getTable());
    approve.setWdValue(clusterId);
    approve.setAcceptDept(params.get("acceptDeptId"));
    approve.setAcceptDeptName(params.get("acceptDeptName"));
    approve.setAcceptedUser(params.get("acceptedUser"));
    approve.setWfStatus(checkFlag ? 3 : 1);
    examineSaveHandler.createApprove(approve, checkFlag);

  }

  /**
   * 生成签收记录
   *
   * @param creator
   * @param creatorName
   * @param curDeptCode
   * @param curDeptName
   * @param assistId
   * @throws Exception
   */
  private void createSignInfo(Object creator, Object creatorName, Object curDeptCode, Object curDeptName, Object assistId) throws Exception {
    List<Map<String, Object>> signs = new ArrayList<>();
    Map<String, Object> paramMap = new HashMap<>();
    paramMap.put("assistId", assistId);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJASSISTGLQBXSBASECLUECOUNT");
    List<Map<String, Object>> depts = (List<Map<String, Object>>) baseService.list(paramMap);
    for (Map<String, Object> map : depts) {
      Map<String, Object> signData = new HashMap<>();
      signData.put("userId", creator);
      signData.put("userName", creatorName);
      signData.put("deptCode", curDeptCode);
      signData.put("deptName", curDeptName);
      signData.put("receiveDeptCode", map.get("deptCode"));
      signData.put("receiveDeptName", map.get("deptName"));
      signData.put("assistId", assistId);
      signData.put("assistType", 1);
      signData.put("clueNum", map.get("clueCount"));
      signs.add(signData);
    }
    Map<String, Object> signParam = new HashMap<>();
    signParam.put("list", signs);
    qbxsSignSaveHandler.save(signParam);
  }

  @Transactional
  public Object saveDept(Map<String, Object> body) throws Exception {
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJASSISTDEPT");
    List<Map<String, Object>> rs = (List<Map<String, Object>>) baseService.list(body);
    if (rs == null || rs.size() == 0) {
      LocalThreadStorage.put(Constant.CONTROLLER_AUTO_INCREMENT, true);
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJASSISTDEPT");
      return baseService.save(body);
    }
    Map<String, Object> map = rs.get(0);
    return map.get("id");
  }
}
