package com.nmghr.hander.save.notice;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.common.exception.GlobalErrorException;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractSaveHandler;
import com.nmghr.hander.update.notice.ExamineSubmitUpdateHandler;
import com.nmghr.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 通知的新增类
 */
@SuppressWarnings("unchecked")
@Service("noticeSubmitSaveHandler")
public class NoticeSubmitSaveHandler extends AbstractSaveHandler {

  public NoticeSubmitSaveHandler(IBaseService baseService) {
    super(baseService);
  }

  @Autowired
  @Qualifier("examineSubmitUpdateHandler")
  private ExamineSubmitUpdateHandler examineSubmitUpdateHandler;

  @Override
  @Transactional
  public Object save(Map<String, Object> body) throws Exception {
    if (!validName(String.valueOf(body.get("userId")), String.valueOf(body.get("title")))) {
      throw new GlobalErrorException("999667", "通知标题重复，请确认后重新输入！");
    }
    if (body.containsKey("messageStatus") && null != body.get("messageStatus") && "1".equals(String.valueOf(body.get("messageStatus")))) {
      if ("true".equals(String.valueOf(body.get("checkFlag")))) {
        body.put("messageStatus", 2);
        Object id = create(body);
        createApprove(body, id, true); // 直接审核通过
        examineSubmitUpdateHandler.batchSaveData((List<Map<String, Object>>) body.get("depts"), id);
        return id;
      }
      body.put("messageStatus", 1);
      Object id = create(body);
      createApprove(body, id, false); //创建审核
      return id;
    } else {
      body.put("messageStatus", 0);
      return create(body);
    }
  }

  /**
   * 修改通知数据
   */
  private boolean validName(String userId, String title) throws Exception {
    Map<String, Object> params = new HashMap<>();
    params.put("userId", userId);
    params.put("title", title);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "BASEMESSAGENAMECHECK");
    Map<String, Object> reset = (Map<String, Object>) baseService.get(params);
    if (reset == null) {
      return true;
    }
    return Integer.parseInt(String.valueOf(reset.get("num"))) < 1;
  }

  /**
   * 新增通知数据
   *
   * @param params Map
   * @throws Exception e
   */
  private Object create(Map<String, Object> params) throws Exception {
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "BASEMESSAGE");
    return baseService.save(params);
  }

  /**
   * 新增审核记录
   *
   * @param params Map
   * @throws Exception e
   */
  private void createApprove(Map<String, Object> params, Object msgId, Boolean checkFlag) throws Exception {
    Map<String, Object> approve = new HashMap<>();
    approve.put("type", "0010");
    approve.put("status", checkFlag ? 3 : 1);
    approve.put("user", params.get("userId"));
    approve.put("userName", params.get("userName"));
    approve.put("dept", params.get("curDeptId"));
    approve.put("deptName", params.get("curDeptName"));
    approve.put("acceptDept", params.get("curDeptId"));
    approve.put("acceptDeptName", params.get("curDeptName"));
    approve.put("table", "BASEMESSAGELIST");
    approve.put("value", msgId);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "WORKORDER");
    Object orderId = baseService.save(approve);

    Map<String, Object> flow = new HashMap<String, Object>();
    flow.put("wdId", orderId); // wd_id 工单id
    flow.put("acceptedDept", params.get("curDeptId")); // accepted_dept 工单流接收部门ID
    flow.put("acceptedDeptName", params.get("curDeptName"));
    flow.put("acceptedUser", params.get("userId")); // accepted_user 工单流接收人
    flow.put("wdFlowStatus", checkFlag ? 3 : 1); // wd_flow_status 工单流转状态: 1 待审批; 2 审批中; 3 已完成; 4驳回; 5已过期
    if (checkFlag) {
      Object now = DateUtil.dateFormart(new Date(), DateUtil.yyyyMMddHHmmss);
      flow.put("acceptedTime", now);
      flow.put("updateTime", now);
      flow.put("updateUser", params.get("userName"));
      flow.put("content", "审核通过");
    }
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "WORKORDERFLOW");
    baseService.save(flow);
  }

}
