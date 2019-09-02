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

/**
 * 这个类 已不再使用
 *
 * @author weber
 * @date 2018年7月26日 下午3:46:31
 * @version 1.0
 */
@Service("workexamineUpdateHandler")
public class WorkorderUpdateHandler extends AbstractUpdateHandler {

  public WorkorderUpdateHandler(IBaseService baseService) {
    super(baseService);
  }

  @Override
  @Transactional
  public Object update(String id, Map<String, Object> requestBody) throws Exception {
    // 判断是否需要继续审核 如果继续 添加 workFlow
    String state = (String) requestBody.get("status"); // pass reject continue
    String flowState = "";
    String orderState = "";
    String typeState = "";
    if ("pass".equals(state)) {
      flowState = "3"; // 审核已完成
      orderState = "3"; // 审核已完成
      typeState = "5"; // 4 审核通过 , 5待签收
      if ("0006".equals(requestBody.get("serverType"))) {
        typeState = "4"; // 检验鉴定 为审核通过
      }
    } else if ("reject".equals(state)) {
      flowState = "4"; // 驳回
      orderState = "2"; // 进行中
      typeState = "2"; // 审核未通过
    } else if ("continue".equals(state)) {
      flowState = "3"; // 审核已完成
      orderState = "2"; // 进行中
      typeState = "3"; // 待上级
    } else {
      return null;
    }

    String flowId = (String) requestBody.get("flowId");
    if ("continue".equals(state)) {
      Map<String, Object> params = new HashMap<String, Object>();
      params.put("wdId", requestBody.get("wdId"));
      params.put("acceptedDept", requestBody.get("acpDept"));
      // params.put("acceptedUser", "");
      params.put("acceptedTime", new Timestamp(System.currentTimeMillis()));
      params.put("wdFlowStatus", "1");// flow待审核
      params.put("parentId", flowId);
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "WORKORDERFLOW");
      baseService.save(params);
    }
    Map<String, Object> flowMap = new HashMap<String, Object>();
    flowMap.put("wdFlowStatus", flowState);
    flowMap.put("updateTime", new Timestamp(System.currentTimeMillis()));
    flowMap.put("updateUser", requestBody.get("userName"));
    if (requestBody.get("content") != null) {
      flowMap.put("content", requestBody.get("content"));
    }
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "WORKORDERFLOW");
    baseService.update(flowId, flowMap);

    // 不继续 修改workorder 修改 按类型修改指定表
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

    Map<String, Object> orderMap = new HashMap<String, Object>();
    orderMap.put("status", orderState);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "WORKORDER");
    Object obj = baseService.update(String.valueOf(requestBody.get("wdId")), orderMap);
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
    String actType = "";
    String bisType = "1";
    if ("0003".equals(serverType)) {
      bisType = "3";// 案件督办
    }
    if ("0004".equals(serverType)) {
      bisType = "4";// 全国性协查
    }
    if ("0005".equals(serverType)) {
      bisType = "5";// 案件协查
    }
    if ("reject".equals(status) || "continue".equals(status)) {
      if ("0005".equals(serverType)) {
        actType = "10";
      }
      if ("0004".equals(serverType)) {
        actType = "6";
      }
      if ("0003".equals(serverType)) {
        actType = "8";
      }
    }
    if ("pass".equals(status)) {
      if ("0005".equals(serverType)) {
        actType = "11";
      }
      if ("0004".equals(serverType)) {
        actType = "7";
      }
      if ("0003".equals(serverType)) {
        actType = "9";
      }
    }
    Map<String, Object> params = new HashMap<String, Object>();
    params.put("businessType", bisType);
    params.put("actionType", actType);
    params.put("businessId", businessId);
    params.put("createUserId", userId);
    params.put("createUserName", userName);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "BUSINESSLOG");
    return baseService.save(params);
  }

}
