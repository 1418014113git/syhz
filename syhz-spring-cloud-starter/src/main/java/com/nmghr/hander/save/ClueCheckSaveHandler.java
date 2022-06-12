package com.nmghr.hander.save;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractSaveHandler;
import com.nmghr.util.NoticeQueueThread;

@Service("cluecheckSaveHandler")
public class ClueCheckSaveHandler extends AbstractSaveHandler {

  public ClueCheckSaveHandler(IBaseService baseService) {
    super(baseService);
  }

  @Override
  @Transactional
  public Object save(Map<String, Object> requestBody) throws Exception {
    // 签收表
    // 添加数据
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "CLUECHECK");
    Map<String, Object> params = (Map<String, Object>) requestBody;
    params.put("clueId", requestBody.get("clueId"));
    Object obj = baseService.save(params);

    Map<String, Object> bSign = new HashMap<String, Object>();
    bSign.put("signUserId", "");
    bSign.put("signTime", null);
    bSign.put("businessTable", "aj_clue_check");
    bSign.put("businessProperty", "id");
    bSign.put("businessValue", obj);
    bSign.put("noticeOrgId", requestBody.get("acceptOrgId"));
    bSign.put("noticeRoleId", "-1");
    bSign.put("noticeTime", new Date());
    bSign.put("noticeUserId", requestBody.get("acceptStaffId"));
    bSign.put("qsStatus", "1");
    bSign.put("parentId", "");
    bSign.put("noticeLx", null);
    bSign.put("updateTime", new Date());
    bSign.put("updateUserId", "");
    bSign.put("businessType", "5");
    bSign.put("deadlineTime", requestBody.get("replyTime"));
    bSign.put("status", "1");
    bSign.put("revokeReason", "");
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "BUSINESSSIGN");
    baseService.save(bSign);
    
    List<Object> ids = new ArrayList<Object>();
    ids.add(requestBody.get("acceptOrgId"));
    Map<String, Object> paras = new HashMap<String, Object>();
    paras.put("ids", ids);
    paras.put("bizId", obj);
    paras.put("type", "CLUE");
    NoticeQueueThread.add(paras);
    return obj;
  }
}
