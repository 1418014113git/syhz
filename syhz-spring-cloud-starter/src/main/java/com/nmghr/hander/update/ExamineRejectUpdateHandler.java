/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.nmghr.hander.update;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

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
@Service("examinerejectUpdateHandler")
public class ExamineRejectUpdateHandler extends AbstractUpdateHandler {

  public ExamineRejectUpdateHandler(IBaseService baseService) {
    super(baseService);
  }

  @Override
  @Transactional
  public Object update(String id, Map<String, Object> requestBody) throws Exception {
    // 判断是否需要继续审核 如果继续 添加 workFlow
    String state = (String) requestBody.get("status"); // pass reject continue
    String flowState = "4"; // 驳回
    String orderState = "2"; // 进行中
    String typeState = "2"; // 审核未通过

    String flowId = (String) requestBody.get("flowId");

    Map<String, Object> flowMap = new HashMap<String, Object>();
    flowMap.put("wdFlowStatus", flowState);
    flowMap.put("updateTime", new Timestamp(System.currentTimeMillis()));
    flowMap.put("updateUser", requestBody.get("userName"));
    if (requestBody.get("content") != null) {
      flowMap.put("content", requestBody.get("content"));
    }
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "WORKORDERFLOW");
    baseService.update(flowId, flowMap);

    if(!"0009".equals(requestBody.get("serverType"))) {
      // 不继续 修改workorder 修改 按类型修改指定表
      Map<String, Object> typeMap = new HashMap<String, Object>();
      typeMap.put("id", id);
      typeMap.put("status", typeState);
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, requestBody.get("type"));
      baseService.update(id, typeMap);
    }
    
    Map<String, Object> orderMap = new HashMap<String, Object>();
    orderMap.put("status", orderState);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "WORKORDER");
    Object obj = baseService.update(String.valueOf(requestBody.get("wdId")), orderMap);
    if (!"0006".equals(requestBody.get("serverType")) && !"0009".equals(requestBody.get("serverType"))) {
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
    if ("0007".equals(serverType)) {
      bisType = "6";// 专项任务
      action = "专项任务成果审核";
    }
    if ("0008".equals(serverType)) {
      bisType = "3";// 专项任务
      action = "案件督办结案报告";
    }
    Map<String, Object> params = new HashMap<String, Object>();
    params.put("bizType", bisType);
    params.put("action", action + "审核驳回");
    params.put("bizId", businessId);
    params.put("userId", userId);
    params.put("userName", userName);
    LogQueueThread.add(params);
    return "";
  }

}
