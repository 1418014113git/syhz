package com.nmghr.hander.update.cluster;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractUpdateHandler;
import com.nmghr.hander.save.ajglqbxs.QbxsSignSaveHandler;
import com.nmghr.hander.update.examine.ExamineUpdateHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 集群战役审核业务
 */
@SuppressWarnings("unchecked")
@Service("clusterExamineUpdateHandler")
public class ClusterExamineUpdateHandler extends AbstractUpdateHandler {

  public ClusterExamineUpdateHandler(IBaseService baseService) {
    super(baseService);
  }

  @Autowired
  private QbxsSignSaveHandler qbxsSignSaveHandler;
  @Autowired
  private ExamineUpdateHandler examineUpdateHandler;

  @Override
  @Transactional
  public Object update(String flowId, Map<String, Object> body) throws Exception {
    //修改flow 表
    String bsId = String.valueOf(body.get("bsId"));
    String flowStatus = String.valueOf(body.get("status"));

    //处理业务信息
    if ("3".equals(flowStatus)) {
      Map<String, Object> p = new HashMap<>();
      p.put("clusterNumber", body.get("number"));
      p.put("startDate", body.get("startDate"));
      p.put("endDate", body.get("endDate"));
      updateCluster(bsId, 4, p);  //审核通过
      //处理审核信息
      examineUpdateHandler.update(flowId, body);
      // 增加待办信息
      createSignInfo(body.get("userId"), body.get("userName"), body.get("curDeptCode"), body.get("curDeptName"), body.get("bsId"));
      return bsId;
    }
    if ("4".equals(flowStatus)) {
      updateCluster(bsId, 3, null); //审核不通过
      //处理审核信息
      return examineUpdateHandler.update(flowId, body);
    }
    if ("6".equals(flowStatus)) {
      updateCluster(bsId, 2, null); //待上级审核
      examineUpdateHandler.update(flowId, body);
      return bsId;
    }
    //处理审核信息
    return "except";
  }

  /**
   * 修改集群信息状态
   *
   * @param noticeId
   * @param status
   * @throws Exception
   */
  private void updateCluster(String noticeId, int status, Map<String, Object> body) throws Exception {
    Map<String, Object> paramMap = new HashMap<>();
    paramMap.put("status", status);
    if (body != null) {
      paramMap.putAll(body);
    }
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJCLUSTERASSIST");
    baseService.update(noticeId, paramMap);
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
    List<Map<String, Object>> beans = (List<Map<String, Object>>) baseService.list(paramMap);

    for (Map<String, Object> bean : beans) {
      Map<String, Object> signData = new HashMap<>();
      signData.put("userId", creator);
      signData.put("userName", creatorName);
      signData.put("deptCode", curDeptCode);
      signData.put("deptName", curDeptName);
      signData.put("receiveDeptCode", bean.get("deptCode"));
      signData.put("receiveDeptName", bean.get("deptName"));
      signData.put("assistId", assistId);
      signData.put("assistType", 2);
      signData.put("clueNum", bean.get("clueCount"));
      signs.add(signData);
    }
    Map<String, Object> signParam = new HashMap<>();
    signParam.put("list", signs);
    qbxsSignSaveHandler.save(signParam);
  }
}
