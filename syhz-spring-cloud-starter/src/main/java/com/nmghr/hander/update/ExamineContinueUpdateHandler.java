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
 * <功能描述/>
 *
 * @author weber
 * @date 2018年7月26日 下午3:46:31
 * @version 1.0
 */
@Service("examinecontinueUpdateHandler")
public class ExamineContinueUpdateHandler extends AbstractUpdateHandler {

  private static final String WORKORDER_FLOW = "WORKORDERFLOW"; // 工作流业务表
  private static final String BUSINESS_LOG = "BUSINESSLOG"; // 工作流业务表

  public ExamineContinueUpdateHandler(IBaseService baseService) {
    super(baseService);
  }

  @Override
  @Transactional
  public Object update(String id, Map<String, Object> requestBody) throws Exception {
    // 判断是否需要继续审核 如果继续 添加 workFlow
    String state = (String) requestBody.get("status"); // pass reject continue
    String flowState = "6"; // 审核已完成带上级审核
    String orderState = "2"; // 进行中
    String typeState = "3"; // 待上级

    String flowId = (String) requestBody.get("flowId");
    // 增加工作流
    Map<String, Object> params = new HashMap<String, Object>();
    params.put("wdId", requestBody.get("wdId"));
    params.put("acceptedDept", requestBody.get("acpDept"));
    params.put("acceptedTime", new Timestamp(System.currentTimeMillis()));
    params.put("wdFlowStatus", "1");// flow待审核
    params.put("parentId", flowId);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, WORKORDER_FLOW);
    baseService.save(params);

    // 修改原工作流
    Map<String, Object> flowMap = new HashMap<String, Object>();
    flowMap.put("wdFlowStatus", flowState);
    flowMap.put("updateTime", new Timestamp(System.currentTimeMillis()));
    flowMap.put("updateUser", requestBody.get("userName"));
    if (requestBody.get("content") != null) {
      flowMap.put("content", requestBody.get("content"));
    }
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "WORKORDERFLOW");
    baseService.update(flowId, flowMap);

    // 修改业务表数据
    Map<String, Object> typeMap = new HashMap<String, Object>();
    typeMap.put("id", id);
    typeMap.put("status", typeState);
    if (requestBody.get("startTime") != null) {
      typeMap.put("startTime", requestBody.get("startTime"));
    }
    if (requestBody.get("endTime") != null) {
      typeMap.put("endTime", requestBody.get("endTime"));
    }
    if ("0003".equals(requestBody.get("serverType"))) {
      typeMap.put("startDate", new Timestamp(System.currentTimeMillis()));
    }
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, requestBody.get("type"));
    baseService.update(id, typeMap);

    // 修改workOrder
    Map<String, Object> orderMap = new HashMap<String, Object>();
    orderMap.put("status", orderState);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "WORKORDER");
    Object obj = baseService.update(String.valueOf(requestBody.get("wdId")), orderMap);

    // 添加时间轴
    if (!"0006".equals(requestBody.get("serverType"))) {
      return saveTimeline(state, requestBody.get("serverType"), requestBody.get("businessId"),
          requestBody.get("userId"), requestBody.get("userName"));
    }
    return obj;
  }

  /**
   * 保存时间轴数据
   */
  private Object saveTimeline(String status, Object serverType, Object businessId, Object userId,
      Object userName) throws Exception {
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
    Map<String, Object> params = new HashMap<String, Object>();
    params.put("bizType", bisType);
    params.put("action", action + "向上级申请");
    params.put("bizId", businessId);
    params.put("userId", userId);
    params.put("userName", userName);
    LogQueueThread.add(params);
    return "";
  }

}
