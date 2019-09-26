/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.nmghr.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.nmghr.basic.common.Result;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.handler.service.TrainWorkorderExamineService;
import com.nmghr.handler.service.TrainWorkorderService;

/**
 * 审核信息服务
 *
 * @author kaven
 * @date 2019年9月23日 上午11:05:31
 * @version 1.0
 */
@RestController
public class TrainWorkorderController {
  private static final Logger LOGGER = LoggerFactory.getLogger(TrainWorkorderController.class);
  
  @Autowired
  @Qualifier("baseService")
  private IBaseService baseService;
  
  @Autowired
  TrainWorkorderService trainWorkorderService;
  
  @Autowired
  TrainWorkorderExamineService trainWorkorderExamineService;
  
  @PostMapping("/workorder/flow")
  public Object workorderFlow(@RequestBody Map<String, Object> requestBody,
      @RequestHeader Map<String, String> headers) {
    try {
      trainWorkorderService.createWorkflowData(baseService, headers, requestBody);
      return Result.ok("ok");
    } catch (Exception e) {
      LOGGER.error("TrainWorkorderController.workorderFlow.error:", e.toString());
      return Result.fail();
    }

  }
  
  @PostMapping("/work/node")
  public Object workorderFlowNode(@RequestBody Map<String, Object> requestBody,
      @RequestHeader Map<String, String> headers) {
    try {
      trainWorkorderExamineService.examineWorkFlowData(baseService, headers, requestBody);
      return Result.ok("ok");
    } catch (Exception e) {
      LOGGER.error("TrainWorkorderController.workorderFlow.error:", e.toString());
      return Result.fail();
    }

  }
}
