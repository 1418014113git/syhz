package com.nmghr.hander.save.cluster;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.common.exception.GlobalErrorException;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractSaveHandler;
import com.nmghr.common.GlobalConfig;
import com.nmghr.common.WorkOrder;
import com.nmghr.hander.dto.ApproveParam;
import com.nmghr.hander.save.ajglqbxs.QbxsSignSaveHandler;
import com.nmghr.hander.save.examine.ExamineSaveHandler;
import com.nmghr.hander.update.examine.ExamineUpdateHandler;
import com.nmghr.hander.update.notice.ExamineSubmitUpdateHandler;
import com.nmghr.handler.message.QueueConfig;
import com.nmghr.handler.service.SendMessageService;
import com.nmghr.service.ajglqbxs.CaseAssistService;
import com.nmghr.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * 案件集群战役
 */
@SuppressWarnings("unchecked")
@Service("caseClusterSubmitSaveHandler")
public class CaseClusterSubmitSaveHandler extends AbstractSaveHandler {

  public CaseClusterSubmitSaveHandler(IBaseService baseService) {
    super(baseService);
  }

  @Autowired
  private ExamineSaveHandler examineSaveHandler;

  @Autowired
  private QbxsSignSaveHandler qbxsSignSaveHandler;

  @Autowired
  private CaseAssistService caseAssistService;
  @Autowired
  private SendMessageService sendMessageService;

  @Override
  @Transactional
  public Object save(Map<String, Object> body) throws Exception {
    if (!validName(String.valueOf(body.get("curDeptCode")), String.valueOf(body.get("clusterTitle")), body.get("id"))) {
      throw new GlobalErrorException("999667", "集群战役标题已存在，请确认后重新输入！");
    }
    if(!StringUtils.isEmpty(body.get("clusterNumber"))){
      caseAssistService.checkNumber(String.valueOf(body.get("curDeptCode")),String.valueOf(body.get("clusterNumber")), body.get("id"), String.valueOf(body.get("category")));
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
      if ("5".equals(String.valueOf(body.get("status")))) {
        // 判断线索是否已导入
        if(!validNum(id)){
          throw new GlobalErrorException("999667", "未导入线索，请导入线索后再提交！");
        }
        createSignInfo(body.get("userId"), body.get("userName"), body.get("curDeptCode"), body.get("curDeptName"), id);
        //增加线索流转记录
        Map<String, Object> p = new HashMap<>();
        p.put("assistType", 2);
        p.put("type", "all");
        p.put("id", id);
        p.put("userId", body.get("userId"));
        p.put("userName", body.get("userName"));
        p.put("curDeptCode", body.get("curDeptCode"));
        p.put("curDeptName", body.get("curDeptName"));
        sendMessageService.sendMessage(p, QueueConfig.AJGLQBXSRECORD);
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
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJCLUSTERASSISTTITLECHECK");
    Map<String, Object> reset = (Map<String, Object>) baseService.get(params);
    if (reset == null) {
      return true;
    }
    return Integer.parseInt(String.valueOf(reset.get("num"))) < 1;
  }

  /**
   * 新增通知数据
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

  private void modify(String id, Map<String, Object> params) throws Exception {
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJCLUSTERASSIST");
    baseService.update(id, params);
  }

  /**
   * 新增集群战役
   *
   * @param params Map
   * @throws Exception e
   */
  private Object create(Map<String, Object> params) throws Exception {
    if(StringUtils.isEmpty(params.get("clusterNumber"))){
      //自动生成编号
      params.put("clusterNumber", caseAssistService.number(String.valueOf(params.get("curDeptCode")), 1));
    }
    if(!StringUtils.isEmpty(params.get("acceptDept"))){
      params.put("checkDeptCode", params.get("acceptDept"));
    }
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJCLUSTERASSIST");
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
    approve.setWdType(WorkOrder.caseCluster.getType());
    approve.setWdStatus(checkFlag ? 3 : 1);
    approve.setUserId(params.get("userId"));
    approve.setUserName(params.get("userName"));
    approve.setCurDeptId(params.get("curDeptId"));
    approve.setCurDeptName(params.get("curDeptName"));
    approve.setWdTable(WorkOrder.caseCluster.getTable());
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
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJGLQBXSBASECLUECOUNT");
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
      signData.put("clueNum", map.get("clueCount"));
      signData.put("assistType", 2);
      signs.add(signData);
    }
    Map<String, Object> signParam = new HashMap<>();
    signParam.put("list", signs);
    qbxsSignSaveHandler.save(signParam);
  }

}
