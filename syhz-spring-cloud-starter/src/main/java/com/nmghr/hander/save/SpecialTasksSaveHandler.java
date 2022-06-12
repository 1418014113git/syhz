package com.nmghr.hander.save;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractSaveHandler;
import com.nmghr.util.NoticeQueueThread;

@Service("specialtasksaveSaveHandler")
public class SpecialTasksSaveHandler extends AbstractSaveHandler {

  public SpecialTasksSaveHandler(IBaseService baseService) {
    super(baseService);
  }

  @Override
  @Transactional
  public Object save(Map<String, Object> requestBody) throws Exception {
    // 添加数据
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "SPECIALTASK");
    Object obj = baseService.save(requestBody);
    // 签收表
    if ("5".equals(requestBody.get("status"))) { // 目前没有审核  操作下一状态 签收中   5增加签收数据
      saveBusinessSign(String.valueOf(obj), requestBody);
    }
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
    String depts = String.valueOf(requestBody.get("deptList"));
    JSONArray array = JSONArray.parseArray(depts);
    List<Object> ids = new ArrayList<Object>();
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
      ids.add(json.get("id"));
    }
    Map<String, Object> paras = new HashMap<String, Object>();
    paras.put("ids", ids);
    paras.put("bizId", id);
    paras.put("type", "SPECIAL");
    NoticeQueueThread.add(paras);
  }

}
