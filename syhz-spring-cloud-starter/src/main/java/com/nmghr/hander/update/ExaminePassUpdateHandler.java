/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.nmghr.hander.update;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.nmghr.basic.common.Constant;
import com.nmghr.basic.common.exception.GlobalErrorException;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractUpdateHandler;
import com.nmghr.util.LogQueueThread;
import com.nmghr.util.NoticeQueueThread;

/**
 * <功能描述/>
 *
 * @author weber
 * @date 2018年7月26日 下午3:46:31
 * @version 1.0 0001 人员协查；0002 企业协查；0003 案件督办；0004 全国性案件协查; 0005 案件协查; 0006 申请检验鉴定; 0007 专项任务成果上报; 0008 督办结案报告 0009
 *          无文书申请
 */
@Service("examinepassUpdateHandler")
public class ExaminePassUpdateHandler extends AbstractUpdateHandler {

  private final String BUSINESSSIGN = "BUSINESSSIGN"; // 签收表
  private final String WORKORDER = "WORKORDER"; // 工单
  private final String WORKORDERFLOW = "WORKORDERFLOW"; // 工单流

  public ExaminePassUpdateHandler(IBaseService baseService) {
    super(baseService);
  }

  @Override
  @Transactional
  public Object update(String id, Map<String, Object> requestBody) throws Exception {
    // 判断是否需要继续审核 如果继续 添加 workFlow
    if (requestBody.get("serverType") == null || requestBody.get("flowId") == null
        || requestBody.get("wdId") == null || (!"0009".equals(requestBody.get("serverType"))
            && requestBody.get("businessId") == null)) {
      throw new GlobalErrorException("999011", "请求参数异常!");
    }
    String flowState = "3"; // 审核已完成
    String orderState = "3"; // 审核已完成
    String typeState = "5"; // 4 审核通过 , 5待签收
    String serverType = String.valueOf(requestBody.get("serverType"));
    if ("0007".equals(serverType) || "0008".equals(serverType)) {
      typeState = "3"; // 成果上报 为审核通过
    }
    if ("0006".equals(serverType)) {
      typeState = "6"; // 检验鉴定进行中
    }
    String flowId = (String) requestBody.get("flowId");
    Map<String, Object> flowMap = new HashMap<String, Object>();
    flowMap.put("wdFlowStatus", flowState);
    flowMap.put("updateTime", new Timestamp(System.currentTimeMillis()));
    flowMap.put("updateUser", requestBody.get("userName"));
    if (requestBody.get("content") != null) {
      flowMap.put("content", requestBody.get("content"));
    }
    // 修改工单流程状态
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, WORKORDERFLOW);
    baseService.update(flowId, flowMap);

    if (!"0009".equals(serverType)) {
      // 按类型修改指定表
      updateBusiness(id, requestBody, typeState);
    }

    // 获取业务部门信息 增加签收业务数据
    // 过滤不需要签收的业务
    if (!"0007".equals(serverType) && !"0008".equals(serverType) && !"0006".equals(serverType)
        && !"0009".equals(serverType)) {
      saveBusinessSign(id, requestBody);
    }
    // 修改workorder状态
    Map<String, Object> orderMap = new HashMap<String, Object>();
    orderMap.put("status", orderState);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, WORKORDER);
    Object obj = baseService.update(String.valueOf(requestBody.get("wdId")), orderMap);
    if (!"0006".equals(serverType) && !"0009".equals(serverType)) {
      return saveTimeline(serverType, requestBody.get("businessId"), requestBody.get("userId"),
          requestBody.get("userName"));
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
  @SuppressWarnings("unchecked")
  private void saveBusinessSign(String id, Map<String, Object> requestBody) throws Exception {
    String businessTable = "";
    String deptsField = "";
    int businessType = 0;
    String ALIAS = String.valueOf(requestBody.get("type"));
    if ("DBAJ".equals(requestBody.get("type"))) {
      businessTable = "aj_supervise";
      businessType = 4;
      deptsField = "apply_dept_id";
    }
    if ("CASEASSIST".equals(requestBody.get("type"))) {
      businessTable = "aj_local_investigation";
      businessType = 6;
      deptsField = "assistDeptId";
    }
    if ("INVESTIGATION".equals(requestBody.get("type"))) {
      businessTable = "aj_investigation";
      businessType = 7;
      deptsField = "partake_dept_ids";
    }
    if ("AUTHENTICATE".equals(requestBody.get("type"))) {
      businessTable = "aj_authenticate";
      businessType = 10;
      deptsField = "applyDeptId";
      ALIAS = "AUTHENTICATEBYID";
    }
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS);
    Map<String, Object> map = (Map<String, Object>) baseService.get(id);
    if (map == null || map.get(deptsField) == null) {
      throw new Exception("Bean is null");
    }
    String depts = String.valueOf(map.get(deptsField));
    if (businessType == 4 || businessType == 10) {
      saveBusinesSign(businessTable, id, businessType, depts);
    } else {
      depts = depts.replaceAll("\\[", "").replaceAll("\\]", "");
      String[] deptIds = depts.split(",");
      for (int i = 0; i < deptIds.length; i++) {
        saveBusinesSign(businessTable, id, businessType, deptIds[i]);
      }
    }
  }

  /**
   * 增加签收表信息
   *
   * @param businessTable
   * @param id
   * @param businessType
   * @param orgId
   * @throws Exception
   */
  private void saveBusinesSign(String businessTable, String id, int businessType, String orgId)
      throws Exception {
    Map<String, Object> bSign = new HashMap<String, Object>();
    bSign.put("signUserId", "");
    bSign.put("signTime", null);
    bSign.put("businessTable", businessTable);
    bSign.put("businessProperty", "id");
    bSign.put("businessValue", id);
    bSign.put("noticeOrgId", orgId);
    bSign.put("noticeRole_id", "-1");
    bSign.put("noticeTime", new Date());
    bSign.put("noticeUserId", "");
    bSign.put("qsStatus", "1");
    bSign.put("parentId", "");
    bSign.put("noticeLx", null);
    bSign.put("updateTime", new Date());
    bSign.put("updateUserId", "");
    bSign.put("businessType", businessType);
    bSign.put("deadlineTime", new Date()); // 需要修改过期时间
    bSign.put("status", "1");
    bSign.put("revokeReason", "");
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, BUSINESSSIGN);
    baseService.save(bSign);

    List<Object> ids = new ArrayList<Object>();
    ids.add(orgId);
    Map<String, Object> paras = new HashMap<String, Object>();
    paras.put("ids", ids);
    paras.put("bizId", id);
    if ("aj_supervise".equals(businessTable)) {
      paras.put("type", "SUPERVISE");
    }
    if ("aj_local_investigation".equals(businessTable)) {
      paras.put("type", "ASSIST");
    }
    if ("aj_investigation".equals(businessTable)) {
      paras.put("type", "COUNTRYASSIST");
    }
    NoticeQueueThread.add(paras);
  }


  /**
   * 修改业务信息
   * 
   * @param id
   * @param requestBody
   * @param typeState
   * @throws Exception
   */
  private void updateBusiness(String id, Map<String, Object> requestBody, String typeState)
      throws Exception {
    String type = String.valueOf(requestBody.get("type"));
    Map<String, Object> typeMap = new HashMap<String, Object>();
    typeMap.put("id", id);
    typeMap.put("status", typeState);
    if ("DBAJ".equals(type)) {
      if (requestBody.get("startTime") != null) {
        typeMap.put("startDate", requestBody.get("startTime"));
      }
      if (requestBody.get("endTime") != null) {
        typeMap.put("endDate", requestBody.get("endTime"));
      }
    } else if ("INVESTIGATION".equals(type)) {
      saveFeedBack(id); // 全国性协查审核通过下发指令
    } else if ("DBAJREPORT".equals(type)) {
      typeMap.put("comment", requestBody.get("comment"));
      endSupervise(String.valueOf(requestBody.get("businessId")));// 结束督办
    } else if ("SPECIALTASKREPORT".equals(type)) {
      endSpecialTaskReport(String.valueOf(requestBody.get("businessId"))); // bug让改为已结束 bug号404
    } else {
      if (requestBody.get("startTime") != null) {
        typeMap.put("startTime", requestBody.get("startTime"));
      }
      if (requestBody.get("endTime") != null) {
        typeMap.put("endTime", requestBody.get("endTime"));
      }
    }
    // 修改业务表信息
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, requestBody.get("type"));
    baseService.update(id, typeMap);
  }

  /**
   * 保存时间轴数据
   * 
   * @param status
   * @param serverType
   * @param businessId
   * @param userId
   * @param userName
   * @return
   * @throws Exception
   */

  private Object saveTimeline(Object serverType, Object businessId, Object userId, Object userName)
      throws Exception {
    String action = "";
    String bisType = "1";
    if ("0003".equals(serverType)) {
      bisType = "3";// 案件督办
      action = "案件督办";
    }
    if ("0004".equals(serverType)) {
      bisType = "4";// 全国性协查
      action = "全国性协查";
    }
    if ("0005".equals(serverType)) {
      bisType = "5";// 案件协查
      action = "案件协查";
    }
    if ("0007".equals(serverType)) {
      bisType = "6";// 专项任务
      action = "专项任务成果";
    }
    if ("0008".equals(serverType)) {
      bisType = "3";// 专项任务
      action = "案件督办结案报告";
    }
    Map<String, Object> params = new HashMap<String, Object>();
    params.put("bizType", bisType);
    params.put("action", action + "审核通过");
    params.put("bizId", businessId);
    params.put("userId", userId);
    params.put("userName", userName);
    LogQueueThread.add(params);
    return "";
  }

  /**
   * 修改业务为截至
   * 
   * @param id
   * @param requestBody
   * @param typeState
   * @throws Exception
   */
  private void endSupervise(String id) throws Exception {
    Map<String, Object> typeMap = new HashMap<String, Object>();
    typeMap.put("id", id);
    typeMap.put("status", 7);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "DBAJ");
    baseService.update(id, typeMap);
  }

  /**
   * 修改业务为截至
   * 
   * @param id
   * @param requestBody
   * @param typeState
   * @throws Exception
   */
  private void endSpecialTaskReport(String id) throws Exception {
    Map<String, Object> typeMap = new HashMap<String, Object>();
    typeMap.put("id", id);
    typeMap.put("status", 7);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "SPECIALTASK");
    baseService.update(id, typeMap);
  }

  /**
   * 增加全国协查下发指令
   * 
   * @param map
   * @throws Exception
   */
  private void saveFeedBack(String inegId) throws Exception {
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "INVESTIGATION");
    Map<String, Object> map = (Map<String, Object>) baseService.get(inegId);
    if (map != null && map.get("partake_dept") != null) {
      JSONArray array = JSON.parseArray(String.valueOf(map.get("partake_dept")));
      for (int i = 0; i < array.size(); i++) {
        JSONObject obj = array.getJSONObject(i);
        Map<String, Object> bSign = new HashMap<String, Object>();
        bSign.put("investigationId", inegId);
        bSign.put("createUser", map.get("apply_person_id"));
        bSign.put("createUserName", map.get("apply_person_name"));
        bSign.put("createDeptId", map.get("apply_dept_id"));
        bSign.put("createDeptName", map.get("apply_dept_name"));
        bSign.put("requiredContent", "全国性协查（" + map.get("assist_title") + "）已下发，请尽快反馈");
        bSign.put("noticeDate", new Timestamp(System.currentTimeMillis()));
        bSign.put("status", 1);
        bSign.put("createDate", new Timestamp(System.currentTimeMillis()));
        bSign.put("receiveDeptId", obj.get("id"));
        bSign.put("receiveDeptName", obj.get("name"));
        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "INEGFEEDBACK");
        baseService.save(bSign);
      }
    }
  }

}
