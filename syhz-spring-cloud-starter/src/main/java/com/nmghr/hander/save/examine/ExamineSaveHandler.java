package com.nmghr.hander.save.examine;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.common.exception.GlobalErrorException;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractSaveHandler;
import com.nmghr.hander.dto.ApproveParam;
import com.nmghr.util.DateUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * 保存审核信息
 */
@SuppressWarnings("unchecked")
@Service("examineSaveHandler")
public class ExamineSaveHandler extends AbstractSaveHandler {
  public ExamineSaveHandler(IBaseService baseService) {
    super(baseService);
  }

  /**
   * 新增审核记录
   *
   * @param params Map
   * @throws Exception e
   */
  @Transactional
  public void createApprove(ApproveParam params, Boolean checkFlag){
    try{
      Map<String, Object> approve = new HashMap<>();
      approve.put("type", params.getWdType());
      approve.put("status", params.getWdStatus());
      approve.put("user", params.getUserId());
      approve.put("userName", params.getUserName());
      approve.put("dept", params.getCurDeptId());
      approve.put("deptName", params.getCurDeptName());
      approve.put("acceptDept", params.getAcceptDept());
      approve.put("acceptDeptName", params.getAcceptDeptName());
      approve.put("table", params.getWdTable());
      approve.put("value", params.getWdValue());
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "WORKORDER");
      Object orderId = baseService.save(approve);

      Map<String, Object> flow = new HashMap<String, Object>();
      flow.put("wdId", orderId); // wd_id 工单id
      flow.put("acceptedDept", params.getAcceptDept()); // accepted_dept 工单流接收部门ID
      flow.put("acceptedDeptName", params.getAcceptDeptName());
      flow.put("acceptedUser", params.getAcceptedUser()); // accepted_user 工单流接收人
      flow.put("wdFlowStatus", params.getWfStatus()); // wd_flow_status 工单流转状态: 1 待审批; 2 审批中; 3 已完成; 4驳回; 5已过期
      if (checkFlag) {
        Object now = DateUtil.dateFormart(new Date(), DateUtil.yyyyMMddHHmmss);
        flow.put("acceptedTime", now);
        flow.put("updateTime", now);
        flow.put("updateUser", params.getUserName());
        flow.put("content", "审核通过");
      }
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "WORKORDERFLOW");
      baseService.save(flow);
    } catch (Exception e){
      throw new GlobalErrorException("999667","审核信息保存异常");
    }
  }
}
