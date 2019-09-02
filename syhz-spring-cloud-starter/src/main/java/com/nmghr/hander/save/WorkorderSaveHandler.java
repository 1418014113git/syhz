/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.nmghr.hander.save;

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
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractSaveHandler;
import com.nmghr.util.LogQueueThread;
import com.nmghr.util.NoticeQueueThread;

/**
 * <功能描述/>
 *
 * @author brook
 * @date 2018年7月25日 上午11:07:01
 * @version 1.0
 */
@Service("workorderSaveHandler")
public class WorkorderSaveHandler extends AbstractSaveHandler {

  public WorkorderSaveHandler(IBaseService baseService) {
    super(baseService);
  }

  /**
   * 提交需要审核的业务 
   * 1.添加或修改当前业务的数据 (有非必选审核，原审核后增加签收数据，非必选时，这里增加签收数据) 
   * 2.根据类型添加时间轴 
   * 3.保存2个工单表 0001 人员协查；0002 企业协查；0003 案件督办；0004
   * 全国性案件协查; 0005 案件协查; 0006 申请检验鉴定; 0007 专项任务成果上报; 0008 督办结案报告
   */
  @Override
  @Transactional
  public Object save(Map<String, Object> requestBody) throws Exception {
    String operatorName = String.valueOf(requestBody.get("operatorName"));
    String serverType = String.valueOf(requestBody.get("serverType"));

    //案件协查，全国协查 没有审核部门时状态直接进行中
    if (("0004".equals(serverType) || "0005".equals(serverType))
        && (requestBody.get("approveDept") == null || "".equals(requestBody.get("approveDept")))) {
      requestBody.put("status", 5);
    }

    Object serverId = requestBody.get("serverId");
    if (serverId == null) {
      // 新增业务表数据
      requestBody.remove("serverType");
      requestBody.remove("operatorName");
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, operatorName);
      serverId = baseService.save(requestBody); // 获取保存到业务表的主键id
    } else {
      // 修改业务信息
      requestBody.remove("operatorName");
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, operatorName);
      baseService.update(String.valueOf(serverId), requestBody);
    }
    // 如果没有审核部门 增加签收数据
    if (("0004".equals(serverType) || "0005".equals(serverType))
        && (requestBody.get("approveDept") == null || "".equals(requestBody.get("approveDept")))) {
      saveBusinessSign(String.valueOf(serverId), serverType);
      if ("0004".equals(serverType)) {
        // 全国协查直接发布时增加直接下发指令
        saveFeedBack(requestBody, serverId);
      }
      return serverId;
    }


    if (!"0006".equals(serverType) && !"0008".equals(serverType)) {
      saveTimeline(serverType, serverId, requestBody.get("userId"), requestBody.get("userName"));
    } else if ("0008".equals(serverType)) {
      saveTimeline(serverType, requestBody.get("bizId"), requestBody.get("userId"),
          requestBody.get("userName"));
    }
    Map<String, Object> workOrder = initWorkOrder(requestBody, operatorName, serverType, serverId);
    // 保存工单主表信息
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "workorder".toUpperCase());
    Object orderId = baseService.save(workOrder);

    // 保存工单明细表信息
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "workorderFlow".toUpperCase());
    return baseService.save(initWorkOrderFlow(requestBody, orderId));
  }

  /**
   * 初始化工单表需要的数据
   * 
   * @param map
   * @param operator
   * @param serverType
   * @param serverId
   * @return
   */
  public Map<String, Object> initWorkOrder(Map<String, Object> map, String operator,
      String serverType, Object serverId) {
    Map<String, Object> orderMap = new HashMap<String, Object>();
    // 0001 人员协查;0002 企业协查;0003 案件督办;0004 案件催办; 0005 案件协查;0006 检验鉴定;
    orderMap.put("type", serverType);
    orderMap.put("status", 1);
    orderMap.put("user", map.get("userId"));
    orderMap.put("dept", map.get("deptId"));
    orderMap.put("userName", map.get("userName"));
    orderMap.put("deptName", map.get("deptName"));
    orderMap.put("acceptDept", map.get("acpDept"));
    orderMap.put("acceptDeptName", map.get("acpDeptName"));
    orderMap.put("table", operator);
    orderMap.put("value", serverId);
    return orderMap;
  }

  /**
   * 初始化工单表需要的数据
   * 
   * @param map
   * @param id
   * @return
   */
  public Map<String, Object> initWorkOrderFlow(Map<String, Object> map, Object id) {
    Map<String, Object> orderMap = new HashMap<String, Object>();
    orderMap.put("wdId", id); // wd_id 工单id
    orderMap.put("acceptedDept", map.get("acpDept")); // accepted_dept 工单流接收部门ID
    orderMap.put("acceptedDeptName", map.get("acpDeptName"));
    orderMap.put("acceptedUser", map.get("acpUser")); // accepted_user 工单流接收人
    orderMap.put("wdFlowStatus", 1); // wd_flow_status 工单流转状态: 1 待审批; 2 审批中; 3 已完成; 4驳回; 5已过期
    return orderMap;
  }

  /**
   * 保存时间轴数据
   * 
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
    String bisType = "";
    if ("0003".equals(serverType)) {
      bisType = "3";// 案件督办
      action = "提交案件督办审核";
    }
    if ("0004".equals(serverType)) {
      bisType = "4";// 全国性协查
      action = "提交全国性协查审核";
    }
    if ("0005".equals(serverType)) {
      bisType = "5";// 案件协查
      action = "提交案件协查审核";
    }
    if ("0007".equals(serverType)) {
      bisType = "6";// 专项任务
      action = "提交专项任务成果审核";
    }
    if ("0008".equals(serverType)) {
      bisType = "3";// 案件协查
      action = "提交督办案件结案报告审核";
    }
    Map<String, Object> params = new HashMap<String, Object>();
    params.put("bizType", bisType);
    params.put("action", action);
    params.put("bizId", businessId);
    params.put("userId", userId);
    params.put("userName", userName);
    LogQueueThread.add(params);
    return "";
  }

  /**
   * 增加业务签收
   * 
   * 根据ID type 查询业务数据，获取参与的部门，遍历增加签收数据 0001 人员协查; 0002 企业协查; 0003 案件督办; 0004 全国性案件协查; 0005 案件协查; 0006 申请检验鉴定; 0007
   * 专项任务成果上报; 0008 督办结案报告
   * 
   * @param id
   * @param requestBody
   * @throws Exception
   */
  @SuppressWarnings("unchecked")
  private void saveBusinessSign(String id, String serverType) throws Exception {
    String businessTable = "";
    String deptsField = "";
    int businessType = 0;
    String ALIAS = "";
    if ("0003".equals(serverType)) {
      businessTable = "aj_supervise";
      businessType = 4;
      deptsField = "apply_dept_id";
      ALIAS = "DBAJ";
    }
    if ("0005".equals(serverType)) {
      businessTable = "aj_local_investigation";
      businessType = 6;
      deptsField = "assistDeptId";
      ALIAS = "CASEASSIST";
    }
    if ("0004".equals(serverType)) {
      businessTable = "aj_investigation";
      businessType = 7;
      deptsField = "partake_dept_ids";
      ALIAS = "INVESTIGATION";
    }
    if ("0006".equals(serverType)) {
      businessTable = "aj_authenticate";
      businessType = 10;
      deptsField = "applyDeptId";
      ALIAS = "AUTHENTICATEBYID";
    }
    // 查询业务数据
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS);
    Map<String, Object> map = (Map<String, Object>) baseService.get(id);
    if (map == null || map.get(deptsField) == null) {
      throw new Exception("Bean is null");
    }
    // 获取要签收的部门并添加
    String depts = String.valueOf(map.get(deptsField));
    if (businessType == 4 || businessType == 10) { // 单部门签收
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
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "BUSINESSSIGN");
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
   * 增加全国协查下发指令
   * 
   * @param map
   * @throws Exception
   */
  private void saveFeedBack(Map<String, Object> map, Object inegId) throws Exception {
    if (map.get("partakeDept") == null && "".equals(map.get("partakeDept"))) {
      return;
    }
    JSONArray array = JSON.parseArray(String.valueOf(map.get("partakeDept")));
    for (int i = 0; i < array.size(); i++) {
      JSONObject obj = array.getJSONObject(i);
      Map<String, Object> bSign = new HashMap<String, Object>();
      bSign.put("investigationId", inegId);
      bSign.put("createUser", map.get("applyPersonId"));
      bSign.put("createUserName", map.get("applyPersonName"));
      bSign.put("createDeptId", map.get("applyDeptId"));
      bSign.put("createDeptName", map.get("applyDeptName"));
      bSign.put("requiredContent", "全国性协查（" + map.get("assistTitle") + "）已下发，请尽快反馈");
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
