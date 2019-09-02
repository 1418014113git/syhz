package com.nmghr.hander.save;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractSaveHandler;

@Service("dbcbajSaveHandler")
public class DbCbSaveHandler extends AbstractSaveHandler {

  public DbCbSaveHandler(IBaseService baseService) {
    super(baseService);
  }

  @Override
  @Transactional
  public Object save(Map<String, Object> requestBody) throws Exception {
    // 签收表
    // 添加数据
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "DBCBAJ");
    Object obj = baseService.save(requestBody);
    saveBusinessSign(String.valueOf(obj), requestBody);
    return obj;
  }

  /**
   * 增加业务签收
   * 
   * @param id
   * @param requestBody
   * @throws Exception
   */
  private void saveBusinessSign(String id, Map<String, Object> requestBody) throws Exception {
    String depts = String.valueOf(requestBody.get("urgedDeptId"));
    depts = depts.replaceAll("\\[", "").replaceAll("\\]", "");
    String[] deptIds = depts.split(",");
    for (int i = 0; i < deptIds.length; i++) {
      Map<String, Object> bSign = new HashMap<String, Object>();
      bSign.put("signUserId", "");
      bSign.put("signTime", null);
      bSign.put("businessTable", "aj_supervise_urgent");
      bSign.put("businessProperty", "id");
      bSign.put("businessValue", id);
      bSign.put("noticeOrgId", deptIds[i]);
      bSign.put("noticeRole_id", "-1");
      bSign.put("noticeTime", new Date());
      bSign.put("noticeUserId", "");
      bSign.put("qsStatus", "1");
      bSign.put("parentId", "");
      bSign.put("noticeLx", null);
      bSign.put("updateTime", new Date());
      bSign.put("updateUserId", "");
      bSign.put("businessType", "8");
      bSign.put("deadlineTime", requestBody.get("endDate"));
      bSign.put("status", "1");
      bSign.put("revokeReason", "");
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "BUSINESSSIGN");
      baseService.save(bSign);
    }
  }
}
