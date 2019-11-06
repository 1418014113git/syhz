package com.nmghr.hander.save.cluster;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.common.exception.GlobalErrorException;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractSaveHandler;
import com.nmghr.common.GlobalConfig;
import com.nmghr.common.WorkOrder;
import com.nmghr.hander.dto.ApproveParam;
import com.nmghr.hander.save.examine.ExamineSaveHandler;
import com.nmghr.hander.update.examine.ExamineUpdateHandler;
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

  @Override
  @Transactional
  public Object save(Map<String, Object> body) throws Exception {
    if (!validName(String.valueOf(body.get("curDeptCode")), String.valueOf(body.get("clusterTitle")))) {
      throw new GlobalErrorException("999667", "通知标题重复，请确认后重新输入！");
    }
    if (body.containsKey("status") && null != body.get("status") && "1".equals(String.valueOf(body.get("status")))) {
      Object id = create(body);
      createApprove(body, id, false); //创建审核
      return id;
    } else {
      return create(body);
    }
  }

  /**
   * 验证是否重名
   */
  private boolean validName(String curDeptCode, String title) throws Exception {
    Map<String, Object> params = new HashMap<>();
    params.put("curDeptCode", curDeptCode);
    params.put("title", title);
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
   * @param params Map
   * @throws Exception e
   */
  private Object create(Map<String, Object> params) throws Exception {
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
    approve.setAcceptDept(params.get("acceptDept"));
    approve.setAcceptDeptName(params.get("acceptDeptName"));
    approve.setAcceptedUser(params.get("acceptedUser"));
    approve.setWfStatus(checkFlag ? 3 : 1);
    examineSaveHandler.createApprove(approve, checkFlag);

  }

}
