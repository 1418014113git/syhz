/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.nmghr.controller.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.entity.operation.OperationResult;
import com.nmghr.entity.operation.OperationResultData;
import com.nmghr.entity.query.FieldValue;
import com.nmghr.entity.query.Page;
import com.nmghr.entity.query.QueryResult;
import com.nmghr.entity.query.QueryResultData;
import com.nmghr.util.SyhzUtil;
import com.nmghr.util.app.AppVerifyUtils;
import com.nmghr.util.app.Result;
import com.nmghr.vo.OperationRequestVo;
import com.nmghr.vo.QueryRequestVo;

/**
 * 消息相关处理
 *
 * @author kaven
 * @date 2019年11月23日 下午2:30:18
 * @version 1.0
 */
@RestController
@RequestMapping("/hsyzapp")
public class AppMessageController {
  private static final String ALIAS_SYS_MESSAGES_PAGE = "sysMessagesPage";

  @Autowired
  private IBaseService baseService;

  @PostMapping(value = "/message")
  @ResponseBody
  public Object getMessageList(@RequestBody Map<String, Object> requestBody) throws Exception {
    // 将请求参照转化为Vo
    QueryRequestVo queryRequestVo = QueryRequestVo.dataToVo(requestBody);
    // 验证查询请求参数的必填项
    AppVerifyUtils.verifyQueryParams(queryRequestVo);
    
    Map<String, Object> conditionMap = new HashMap<String, Object>();
    // userId = '1' and deptCode='610000530000'
    // 获取查询条件组织为map
    AppVerifyUtils.getQueryCondition(conditionMap, queryRequestVo);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS_SYS_MESSAGES_PAGE);
    List<Map<String, Object>> messageList =(List<Map<String, Object>>) baseService.list(conditionMap);


    String sign = "这是签名";
    Page page = new Page();
    page.setPageNo(1);
    page.setPageSize(10);
    page.setTotal(2);

    List<QueryResultData> data = new ArrayList<QueryResultData>();
    QueryResultData aueryResultData = new QueryResultData();
    aueryResultData.setSourceId("这是数据来源id---sourceId");
    List<FieldValue> fieldValues = null;
    FieldValue fieldValue = new FieldValue();
    for (Map<String, Object> map : messageList) {
      fieldValues = new ArrayList<FieldValue>();
      Set<String> keySet = map.keySet();
      for (String key : keySet) {
        fieldValue = new FieldValue();
        fieldValue.setField(key);
        fieldValue.setValue(SyhzUtil.setDate(map.get(key)));
        fieldValue.setIsCode(0);
        fieldValues.add(fieldValue);
      }
      aueryResultData.setFieldValues(fieldValues);
      data.add(aueryResultData);
    }
    QueryResult result = new QueryResult(data, page, sign);

    return Result.ok(queryRequestVo.getJsonrpc(), queryRequestVo.getId(), result);
  }
  
  @PostMapping(value = "/message/save")
  @ResponseBody
  public Object saveMessageInfo(@RequestBody Map<String, Object> requestBody) throws Exception {
    // 将参数转化为operationVo
    OperationRequestVo operationRequestVo = OperationRequestVo.dataToVo(requestBody);
    
    // 验证参数
    AppVerifyUtils.verifyOperationParams(operationRequestVo);
    
    Map<String, Object> operationMap = new HashMap<String, Object>();
    // 获取参数
    AppVerifyUtils.getOperationCondition(operationMap, operationRequestVo);
    
//    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS_SYS_MESSAGES_PAGE);
//    Object operationObj = baseService.save(operationMap);
    Object operationObj = "11111";
    
    OperationResult result = new OperationResult();
    List<OperationResultData> operations = new ArrayList<OperationResultData>();
    OperationResultData operationResultData = new OperationResultData();
    operationResultData.setOperationId(operationObj+"");
    operationResultData.setOperationCode("1");
    operations.add(operationResultData);
    
    return Result.ok(operationRequestVo.getJsonrpc(), operationRequestVo.getId(), result);
  }
}
