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

@Service("directdbajSaveHandler")
public class DirectDbajSaveHandler extends AbstractSaveHandler {

  private final String DBAJ = "DBAJ";
  private final String BUSINESSSIGN = "BUSINESSSIGN"; // 签收表
  private final String BUSINESSTABLE = "aj_supervise"; // 签收表

  public DirectDbajSaveHandler(IBaseService baseService) {
    super(baseService);
  }

  @Override
  @Transactional
  public Object save(Map<String, Object> requestBody) throws Exception {
    // 签收表
    // 添加数据
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, DBAJ);
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
    List<Object> ids = new ArrayList<Object>();
    String depts = String.valueOf(requestBody.get("superviseDeptId"));
    depts = depts.replaceAll("\\[", "").replaceAll("\\]", "");
    String[] deptIds = depts.split(",");
    for (int i = 0; i < deptIds.length; i++) {
      Map<String, Object> bSign = new HashMap<String, Object>();
      bSign.put("signUserId", "");
      bSign.put("signTime", null);
      bSign.put("businessTable", BUSINESSTABLE);
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
      bSign.put("businessType", "4");
      bSign.put("deadlineTime", new Date());
      bSign.put("status", "1");
      bSign.put("revokeReason", "");
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, BUSINESSSIGN);
      baseService.save(bSign);
      ids.add(deptIds[i]);
    }
    Map<String, Object> paras = new HashMap<String, Object>();
    paras.put("ids", ids);
    paras.put("bizId", id);
    paras.put("type", "SUPERVISE");
    NoticeQueueThread.add(paras);
  }
}
