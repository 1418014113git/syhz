package com.nmghr.hander.update.cluster;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.common.exception.GlobalErrorException;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractUpdateHandler;
import com.nmghr.common.WorkOrder;
import com.nmghr.hander.dto.ApproveParam;
import com.nmghr.hander.save.examine.ExamineSaveHandler;
import com.nmghr.hander.update.examine.ExamineUpdateHandler;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
@Service("caseClusterUpdateHandler")
public class CaseClusterUpdateHandler extends AbstractUpdateHandler {

  @Autowired
  private ExamineSaveHandler examineSaveHandler;

  public CaseClusterUpdateHandler(IBaseService baseService) {
    super(baseService);
  }
  @Override
  @Transactional
  public Object update(String id, Map<String, Object> body) throws Exception {
    if (null == id || StringUtils.isEmpty(id)) {
      throw new GlobalErrorException("999667", "id不能为空！");
    }
    if (!validName(String.valueOf(body.get("userId")), String.valueOf(body.get("title")), body.get("id"))) {
      throw new GlobalErrorException("999667", "通知标题重复，请确认后重新输入！");
    }
    if (body.containsKey("status") && null != body.get("status") && "1".equals(String.valueOf(body.get("status")))) {
      modify(String.valueOf(body.get("id")), body);
      createApprove(body, id, false); //创建审核
      return id;
    } else {
      modify(String.valueOf(body.get("id")), body);
      return id;
    }
  }
  /**
   * 修改通知数据
   */
  private boolean validName(String userId, String title, Object id) throws Exception {
    Map<String, Object> params = new HashMap<>();
    params.put("userId", userId);
    params.put("title", title);
    if (id != null) {
      params.put("id", id);
    }
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "BASEMESSAGENAMECHECK");
    Map<String, Object> reset = (Map<String, Object>) baseService.get(params);
    if (reset == null) {
      return true;
    }
    return Integer.parseInt(String.valueOf(reset.get("num"))) < 1;
  }

  private void modify(String noticeId, Map<String, Object> params) throws Exception {
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "BASEMESSAGE");
    baseService.update(noticeId, params);
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

}
