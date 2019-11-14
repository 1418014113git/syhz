package com.nmghr.hander.update.examine;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.common.exception.GlobalErrorException;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractUpdateHandler;
import com.nmghr.handler.message.QueueConfig;
import com.nmghr.handler.service.SendMessageService;
import com.nmghr.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unchecked")
@Service("examineUpdateHandler")
public class ExamineUpdateHandler extends AbstractUpdateHandler {

  private Logger log = LoggerFactory.getLogger(ExamineUpdateHandler.class);

  @Autowired
  private SendMessageService sendMessageService;

  public ExamineUpdateHandler(IBaseService baseService) {
    super(baseService);
  }

  @Override
  @Transactional
  public Object update(String flowId, Map<String, Object> body) throws Exception {
    //修改flow 表
    String status = String.valueOf(body.get("status"));
    if ("4".equals(status) && body.get("content") == null) {
      throw new GlobalErrorException("999667", "请填写审核意见！");
    }
    // 修改工单流程状态
    String now = DateUtil.dateFormart(new Date(), DateUtil.yyyyMMddHHmmss);
    Map<String, Object> flowMap = new HashMap<String, Object>();
    flowMap.put("wdFlowStatus", status);
    flowMap.put("updateTime", now);
    flowMap.put("updateUser", body.get("userName"));
    if (body.get("content") != null) {
      flowMap.put("content", body.get("content"));
    } else {
      flowMap.put("content", "审核通过");
    }
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "WORKORDERFLOW");
    baseService.update(flowId, flowMap);

    Map<String, Object> orderMap = new HashMap<String, Object>();
    //向上级申请
    if ("6".equals(status)) {
      // 增加工作流
      Map<String, Object> params = new HashMap<String, Object>();
      params.put("wdId", body.get("wdId"));
      params.put("acceptedDept", body.get("acceptDeptId"));
      params.put("acceptedDeptName", body.get("acceptDeptName"));
      params.put("acceptedTime", new Timestamp(System.currentTimeMillis()));
      params.put("wdFlowStatus", "1");// flow待审核
      params.put("parentId", flowId);
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "WORKORDERFLOW");
      baseService.save(params);
      orderMap.put("status", 2);//进行中
    } else {
      orderMap.put("status", 3);//已结束
    }
    //设置本次审批状态为 已结束   修改workorder表
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "WORKORDER");
    Object obj = baseService.update(String.valueOf(body.get("wdId")), orderMap);

    StringBuilder title = new StringBuilder(String.valueOf(body.get("title")));
    StringBuilder content = new StringBuilder();
    content.append("【站内通知】-").append(String.valueOf(body.get("title"))).append("在").append(now);

    //审核通过
    if ("3".equals(status)) {
      title.append("审核通过提醒");
      content.append("审核通过，请及时查阅！");
    }
    // 审核不通过
    if ("4".equals(status)) {
      title.append("审核不通过提醒");
      content.append("审核不通过，请及时查阅！");
    }
    //向上级申请
    if ("6".equals(status)) {
      title.append("审核通过提醒");
      content.append("审核通过，请及时查阅！");
    }
    //发送消息
    Map<String, Object> sendMap = setMap(title, content, body.get("creatorId"),
        body.get("creatorName"), body.get("userId"), body.get("userName"), body.get("curDeptCode"), body.get("curDeptName"));
    sendMessageService.sendMessage(sendMap,QueueConfig.SAVEMESSAGE);
    sendMessageService.sendMessage(sendMap,QueueConfig.TIMELYMESSAGE);
    return obj;
  }



  private Map<String, Object> setMap(Object title, Object content, Object id,
                                     Object name, Object userId, Object userName, Object curDeptCode, Object curDeptName) {
    Map<String, Object> params = new HashMap<>();
    params.put("bussionType", 4);
    params.put("bussionTypeInfo", 404);
    params.put("bussionId", -1);
    params.put("title", title);
    params.put("content", content);
    params.put("status", 0);
    params.put("acceptId", id);
    params.put("acceptName", name);
    params.put("creator", userId);
    params.put("creatorName", userName);
    params.put("deptCode", curDeptCode);
    params.put("deptName", curDeptName);
    params.put("category", 1);//弹出信息
    return params;
  }

}
