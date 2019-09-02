/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.nmghr.hander.update;

import java.util.Map;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractUpdateHandler;
import com.nmghr.util.LogQueueThread;

/**
 * 审核被驳回后调用此方法增加工单流
 *
 * @author weber
 * @date 2018年7月26日 下午3:46:31
 * @version 1.0
 */
@Service("assistrejectUpdateHandler")
public class AssistRejectUpdateHandler extends AbstractUpdateHandler {

  private final String CASE_ASSIST = "CASEASSIST";
  private final String DBAJ = "DBAJ";
  private final String INVESTIGATION = "INVESTIGATION";
  private final String AUTHENTICATE = "AUTHENTICATE";
  private final String WORKORDER_BY_TYPE = "WORKORDERBYTYPE";
  private final String WORKORDER = "WORKORDER";
  private final String WORKORDER_FLOW = "WORKORDERFLOW";
  private final String SPECIALTASKREPORT = "SPECIALTASKREPORT";
  private final String DBAJREPORT = "DBAJREPORT";

  public AssistRejectUpdateHandler(IBaseService baseService) {
    super(baseService);
  }

  @Override
  @Transactional
  public Object update(String id, Map<String, Object> requestBody) throws Exception {
    String type = (String) requestBody.get("type");
    if (type == null || "".equals(type)) {
      return null;
    }
    // 修改原业务变化得数据
    Map<String, Object> business = requestBody;
    business.put("status", '1');// 将状态修改为待审核
    if ("0005".equals(type)) {// 案件协查修改状态
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, CASE_ASSIST);
      baseService.update(id, business);
    } else if ("0003".equals(type)) {// 案件督办修改状态
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, DBAJ);
      baseService.update(id, business);
    } else if ("0004".equals(type)) {// 全国性协查修改状态
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, INVESTIGATION);
      baseService.update(id, business);
    } else if ("0006".equals(type)) {// 申请检验鉴定
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, AUTHENTICATE);
      baseService.update(id, business);
    } else if ("0007".equals(type)) {// 成果上报
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, SPECIALTASKREPORT);
      baseService.update(id, business);
    } else if ("0008".equals(type)) {// 上报结案报告
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, DBAJREPORT);
      baseService.update(id, business);
    } else {
      return null;
    }

    // 查询workorder
    Map<String, Object> orderMap = new HashMap<String, Object>();
    orderMap.put("val", id);
    orderMap.put("status", "2"); // 已受理
    orderMap.put("type", type);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, WORKORDER_BY_TYPE);
    Map<String, Object> order = (Map<String, Object>) baseService.get(orderMap);
    // 修改 workorder
    orderMap.clear();
    orderMap.put("status", "1"); // 待审核
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, WORKORDER);
    baseService.update(String.valueOf(order.get("id")), orderMap);
    // 增加workflow
    Map<String, Object> params = new HashMap<String, Object>();
    params.put("wdId", order.get("id"));
    params.put("acceptedDept", requestBody.get("approveDept"));
    params.put("acceptedDeptName", requestBody.get("approveDeptName"));
    // params.put("acceptedUser", "");
    params.put("acceptedTime", new Timestamp(System.currentTimeMillis()));
    params.put("wdFlowStatus", "1");// flow 待审核
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, WORKORDER_FLOW);
    Object result = baseService.save(params);
    if (!"0006".equals(type) && !"0008".equals(type)) {
      saveTimeline(type, id, requestBody.get("userId"), requestBody.get("userName"));
    }
    if ("0008".equals(type)) {
      saveTimeline(type, requestBody.get("bizId"), requestBody.get("userId"), requestBody.get("userName"));
    }
    return result;
  }

  /**
   * 保存时间轴数据
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

}
