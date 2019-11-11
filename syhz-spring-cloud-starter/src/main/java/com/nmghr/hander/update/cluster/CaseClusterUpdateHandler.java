package com.nmghr.hander.update.cluster;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractUpdateHandler;
import com.nmghr.hander.update.examine.ExamineUpdateHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unchecked")
@Service("caseClusterUpdateHandler")
public class CaseClusterUpdateHandler extends AbstractUpdateHandler {


  @Autowired
  private ExamineUpdateHandler examineUpdateHandler;

  public CaseClusterUpdateHandler(IBaseService baseService) {
    super(baseService);
  }
  @Override
  @Transactional
  public Object update(String flowId, Map<String, Object> body) throws Exception {
    //修改flow 表
    String bsId = String.valueOf(body.get("bsId"));
    String flowStatus = String.valueOf(body.get("flowStatus"));

    //处理业务信息
    if ("3".equals(flowStatus)) {
      updateCluster(bsId, 2);  //审核通过
    }
    if ("4".equals(flowStatus)) {
      updateCluster(bsId, 3); //审核不通过
    }
    if ("6".equals(flowStatus)) {
      updateCluster(bsId, 3); //待上级审核
    }
    //处理审核信息
    return examineUpdateHandler.update(flowId, body);
  }


  private void updateCluster(String noticeId, int status) throws Exception {
    Map<String, Object> noticeMap = new HashMap<>();
    noticeMap.put("messageStatus", status);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "BASEMESSAGE");
    baseService.update(noticeId, noticeMap);
  }
}
