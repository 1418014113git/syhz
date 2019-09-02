package com.nmghr.hander.update;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractUpdateHandler;

@Service("specialtaskupdUpdateHandler")
public class SpecialTasksUpdateHandler extends AbstractUpdateHandler {

  public SpecialTasksUpdateHandler(IBaseService baseService) {
    super(baseService);
  }

  /**
   * 修改发布专项任务
   */
  @Override
  @Transactional
  public Object update(String id, Map<String, Object> requestBody) throws Exception {
    if ("5".equals(requestBody.get("status"))) { // 目前没有审核  操作下一状态 签收中   5增加签收数据
      saveBusinessSign(id, requestBody);
    }
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "SPECIALTASK");
    return baseService.update(id, requestBody);
  }

  /**
   * 增加业务签收
   * 
   * @param id
   * @param requestBody
   * @throws Exception
   */
  private void saveBusinessSign(String id, Map<String, Object> requestBody) throws Exception {
    String depts = String.valueOf(requestBody.get("deptList"));
    JSONArray array = JSONArray.parseArray(depts);
    for (int i = 0; i < array.size(); i++) {
      JSONObject json = array.getJSONObject(i);
      Map<String, Object> bSign = new HashMap<String, Object>();
      bSign.put("signUserId", "");
      bSign.put("signTime", null);
      bSign.put("businessTable", "aj_special_task");
      bSign.put("businessProperty", "id");
      bSign.put("businessValue", id);
      bSign.put("noticeOrgId", json.get("id"));
      bSign.put("noticeOrgName", json.get("name"));
      bSign.put("noticeRole_id", "-1");
      bSign.put("noticeTime", new Date());
      bSign.put("noticeUserId", "");
      bSign.put("qsStatus", "1");
      bSign.put("parentId", "");
      bSign.put("noticeLx", null);
      bSign.put("updateTime", new Date());
      bSign.put("updateUserId", "");
      bSign.put("businessType", "9");
      bSign.put("deadlineTime", requestBody.get("endDate"));
      bSign.put("status", "1");
      bSign.put("revokeReason", "");
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "BUSINESSSIGN");
      baseService.save(bSign);
    }
  }

}
