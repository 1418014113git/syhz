package com.nmghr.hander.update.notice;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.common.exception.GlobalErrorException;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractUpdateHandler;
import com.nmghr.handler.message.QueueConfig;
import com.nmghr.handler.service.SendMessageService;
import com.nmghr.service.message.MessageSendService;
import com.nmghr.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 通知审核业务
 */
@SuppressWarnings("unchecked")
@Service("examineSubmitUpdateHandler")
public class ExamineSubmitUpdateHandler extends AbstractUpdateHandler {

  private Logger log = LoggerFactory.getLogger(ExamineSubmitUpdateHandler.class);

  @Autowired
  private SendMessageService sendMessageService;

  @Autowired
  private MessageSendService messageSendService;

  public ExamineSubmitUpdateHandler(IBaseService baseService) {
    super(baseService);
  }

  @Override
  @Transactional
  public Object update(String flowId, Map<String, Object> body) throws Exception {
    //修改flow 表
    String noticeId = String.valueOf(body.get("noticeId"));
    String flowStatus = String.valueOf(body.get("flowStatus"));
    if ("4".equals(flowStatus) && body.get("content") == null) {
      throw new GlobalErrorException("999667", "请填写审核意见！");
    }
    String now = DateUtil.dateFormart(new Date(), DateUtil.yyyyMMddHHmmss);
    Map<String, Object> flowMap = new HashMap<String, Object>();
    flowMap.put("wdFlowStatus", flowStatus);
    flowMap.put("updateTime", now);
    flowMap.put("updateUser", body.get("userName"));
    if (body.get("content") != null) {
      flowMap.put("content", body.get("content"));
    } else {
      flowMap.put("content", "审核通过");
    }
    // 修改工单流程状态
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "WORKORDERFLOW");
    baseService.update(flowId, flowMap);

    StringBuilder title = new StringBuilder(String.valueOf(body.get("title")));
    StringBuilder content = new StringBuilder();
    content.append("【站内通知】-").append(String.valueOf(body.get("title"))).append("在").append(now);

    if ("3".equals(flowStatus)) {
      //设置通知状态为2
      //增加签收业务
      updNotice(noticeId, 2);  //审核通过
      batchSaveData((List<Map<String, Object>>) body.get("depts"), noticeId);
      title.append("审核通过提醒");
      content.append("审核通过，请及时查阅！");
    }
    if ("4".equals(flowStatus)) {
      //设置通知状态为3
      updNotice(noticeId, 3); //审核不通过
      title.append("审核不通过提醒");
      content.append("审核不通过，请及时查阅！");
    }
    //发送消息
    Map<String, Object> sendMap = setMap(title, content, body.get("creatorId"),
        body.get("creatorName"), body.get("userId"), body.get("userName"), body.get("curDeptCode"), body.get("curDeptName"));
    sendMessageService.sendMessage(sendMap,QueueConfig.SAVEMESSAGE);
    sendMessageService.sendMessage(sendMap,QueueConfig.TIMELYMESSAGE);

    //设置本次审批状态为 已结束   修改workorder表
    Map<String, Object> orderMap = new HashMap<String, Object>();
    orderMap.put("status", 3);//已结束
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "WORKORDER");
    Object o= baseService.update(String.valueOf(body.get("wdId")), orderMap);

    //给管理员及接收人发送消息
    messageSendService.getMARUserList(body);
    return o;
  }


  private void updNotice(String noticeId, int status) throws Exception {
    Map<String, Object> noticeMap = new HashMap<>();
    noticeMap.put("messageStatus", status);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "BASEMESSAGE");
    baseService.update(noticeId, noticeMap);
  }

  /**
   * 批量处理增加数据
   *
   * @return boolean
   */
  public void batchSaveData(List<Map<String, Object>> list, Object messageId) {
    if (list == null || list.size() == 0) {
      throw new GlobalErrorException("999997", "提交数据异常");
    }
    log.info("batch list size: " + list.size());
    Long initId = null;
    Map<String, Object> params = new HashMap<>();
    params.put("num", list.size());
    params.put("seqName", "BASEMESSAGESIGN");
    try {
      //修改sequence 表自增id
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "SEQUENCEUPDATE");
      Map<String, Object> map = (Map<String, Object>) baseService.get(params);
      if (map == null) {
        log.error("batch save get EXAMPAPERINFO id is null");
        throw new GlobalErrorException("999997", "提交数据异常");
      }
      //计算初始id
      initId = Long.parseLong(String.valueOf(map.get("id"))) - list.size();
    } catch (Exception e) {
      log.error("batch save get SEQUENCEUPDATE list Error: " + e.getMessage());
      throw new GlobalErrorException("999997", "提交数据有误");
    }

    //拼装需要的参数
    for (Map<String, Object> bean : list) {
      initId++;//增加ID
      bean.put("id", String.valueOf(initId));
      bean.put("messageId", messageId);
    }
    params = new HashMap<>();
    params.put("list", list);
    try {
      //提交数据
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "BASEMESSAGESIGNBATCH");
      baseService.save(params);
    } catch (Exception e) {
      log.error("batch save list Error: " + e.getMessage());
      throw new GlobalErrorException("999996", "提交数据有误");
    }
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
