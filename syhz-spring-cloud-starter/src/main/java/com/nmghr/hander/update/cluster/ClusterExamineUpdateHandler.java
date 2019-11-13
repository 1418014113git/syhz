package com.nmghr.hander.update.cluster;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractUpdateHandler;
import com.nmghr.hander.save.examine.ExamineSaveHandler;
import com.nmghr.hander.update.examine.ExamineUpdateHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unchecked")
@Service("clusterExamineUpdateHandler")
public class ClusterExamineUpdateHandler extends AbstractUpdateHandler {

  public ClusterExamineUpdateHandler(IBaseService baseService) {
    super(baseService);
  }

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
      updateCluster(bsId, 4);  //审核通过
      //处理审核信息
      return examineUpdateHandler.update(flowId, body);
    }
    if ("4".equals(flowStatus)) {
      updateCluster(bsId, 3); //审核不通过
      //处理审核信息
      return examineUpdateHandler.update(flowId, body);
    }
    if ("6".equals(flowStatus)) {
      updateCluster(bsId, 2); //待上级审核
      examineUpdateHandler.update(flowId, body);
      return null;
    }
    //处理审核信息
    return "except";
  }
  private void updateCluster(String noticeId, int status) throws Exception {
    Map<String, Object> noticeMap = new HashMap<>();
    noticeMap.put("status", status);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJCLUSTERASSIST");
    baseService.update(noticeId, noticeMap);
  }

}
