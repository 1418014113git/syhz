/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.nmghr.util.app;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.nmghr.common.AppeErrorException;
import com.nmghr.entity.operation.Operation;
import com.nmghr.entity.operation.OperationFieldValue;
import com.nmghr.entity.operation.OperationParams;
import com.nmghr.entity.operation.OperationRequest;
import com.nmghr.entity.operation.OperationRequestData;
import com.nmghr.entity.query.FieldValue;
import com.nmghr.entity.query.Page;
import com.nmghr.entity.query.QueryParams;
import com.nmghr.entity.query.QueryRequest;
import com.nmghr.entity.query.QueryResult;
import com.nmghr.entity.query.QueryResultData;
import com.nmghr.entity.query.UserInfo;
import com.nmghr.util.SyhzUtil;
import com.nmghr.vo.OperationRequestVo;
import com.nmghr.vo.QueryRequestVo;

/**
 * <功能描述/>
 *
 * @author kaven
 * @date 2019年11月25日 上午10:54:29
 * @version 1.0
 */
public class AppVerifyUtils {

  /**
   * 查询参数验证
   * 
   * @param queryRequestVo
   */
  public static void verifyQueryParams(QueryRequestVo queryRequestVo) {
    verifyCommonParam(queryRequestVo.getJsonrpc(), queryRequestVo.getMethod(),queryRequestVo.getId());
    verifyBodyParam(queryRequestVo.getParams());
  }

  /**
   * 操作参数验证
   * 
   * @param operationRequestVo
   */
  public static void verifyOperationParams(OperationRequestVo operationRequestVo) {
    verifyCommonParam(operationRequestVo.getJsonrpc(), operationRequestVo.getMethod(),
        operationRequestVo.getId());
    verifyOperationBodyParam(operationRequestVo.getParams());
  }

  /**
   * 查询条件获取
   * 
   * @param conditionMap
   * @param queryRequestVo
   */
  public static void getQueryCondition(Map<String, Object> conditionMap,
      QueryRequestVo queryRequestVo) {
    String condition = queryRequestVo.getParams().getData().getCondition();
    if (condition.indexOf("and") >= 0) {
      String[] conditionArr = condition.split("and");
      for (String conditionStr : conditionArr) {
        String conditionData = conditionStr.replaceAll(" ", "").replaceAll("'", "");
        String[] conditionDataArr = conditionData.split("=");
        conditionMap.put(conditionDataArr[0], conditionDataArr[1]);
      }
    }
  }

  /**
   * 获取save/update操作条件
   * 
   * @param operationMap
   * @param operationRequestVo
   */
  public static void getOperationCondition(Map<String, Object> operationMap,
      OperationRequestVo operationRequestVo) {
    List<Operation> operationList = operationRequestVo.getParams().getData().getOperations();
    for (Operation operation : operationList) {
      List<OperationRequestData> dataList = operation.getData();
      for (OperationRequestData operationRequestData : dataList) {
        List<OperationFieldValue> fieldValues = operationRequestData.getFieldValues();
        for (OperationFieldValue fieldValue : fieldValues) {
          operationMap.put(fieldValue.getField(), fieldValue.getValue());
        }
      }
    }
  }
  
  public static QueryResult setQueryResult(String sign, int pageNo,int pageSize,int total,String sourceId,
      List<Map<String, Object>> listData) {
    Page page = new Page();
    page.setPageNo(pageNo);
    page.setPageSize(pageSize);
    page.setTotal(total);
    
    List<QueryResultData> data = new ArrayList<QueryResultData>();
    QueryResultData aueryResultData = new QueryResultData();
    aueryResultData.setSourceId(sourceId);
    List<FieldValue> fieldValues = null;
    FieldValue fieldValue = new FieldValue();
    for (Map<String, Object> map : listData) {
      fieldValues = new ArrayList<FieldValue>();
      Set<String> keySet = map.keySet();
      for (String key : keySet) {
        fieldValue = new FieldValue();
        fieldValue.setField(key);
        fieldValue.setValue(SyhzUtil.setDate(map.get(key)));
        fieldValue.setIsCode(0);
        fieldValue.setCodeValue("");
        fieldValues.add(fieldValue);
      }
      aueryResultData.setFieldValues(fieldValues);
      data.add(aueryResultData);
    }
    return new QueryResult(data, page, sign);
  }
  

  /**
   * 参数头验证
   * 
   * @param queryRequestVo
   */
  private static void verifyCommonParam(String jsonrpc, String method, String id) {
    SyhzAppValidationUtil.notNull(jsonrpc, "jsonrpc版本不能为空");
    if (!SyhzAppErrorEnmu.jsonrpcValue.equals(jsonrpc)) {
      throw new AppeErrorException(SyhzAppErrorEnmu.ERROR_32600.getCode(), "jsonrpc版本不匹配");
    }

    SyhzAppValidationUtil.notNull(method, "RPC方法名不能为空");
    if (!SyhzAppErrorEnmu.method_query.equals(method)) {

    } else if (!SyhzAppErrorEnmu.method_operate.equals(method)) {

    } else {
      throw new AppeErrorException(method, "未知的RPC方法名");
    }

    SyhzAppValidationUtil.notNull(id, "客户端ID不能为空");
  }

  /**
   * 验证查询请求参照
   * 
   * @param params
   */
  private static void verifyBodyParam(QueryParams params) {
    if (params != null) {
      verifyBodyQueryRequest(params.getData());
    }
  }

  /**
   * 验证查询请求数据
   * 
   * @param queryRequest
   */
  private static void verifyBodyQueryRequest(QueryRequest queryRequest) {
    if (queryRequest != null) {
      String version = queryRequest.getVersion(); // 协议版本号，格式为 yyyyMMdd
      SyhzAppValidationUtil.notNull(version, "协议版本号不能为空，格式为 yyyyMMdd");
      SyhzAppValidationUtil.max(version, 8, "协议版本号不合法，长度为8位，格式为 yyyyMMdd");

      String sessionId = queryRequest.getSessionId(); // 会话 ID，建立连接接口返回结果中的 sessionId。

      SyhzAppValidationUtil.notNull(sessionId, "会话 ID不能为空");
      SyhzAppValidationUtil.max(sessionId, 32, "回话ID不合法，长度为32位");

      UserInfo userInfo = queryRequest.getUserInfo();// 用户信息
      verifyBodyUserInfo(userInfo);

      String dataObjId = queryRequest.getDataObjId();// 资源名称
      SyhzAppValidationUtil.notNull(dataObjId, "资源名称不能为空");
    }
  }

  /**
   * 验证用户信息参数
   * 
   * @param userInfo
   */
  private static void verifyBodyUserInfo(UserInfo userInfo) {
    SyhzAppValidationUtil.notNull(userInfo, "用户信息不能为空");
    if (userInfo != null) {
      String userId = userInfo.getUserId();// 用户 ID，警号
      SyhzAppValidationUtil.notNull(userId, "警号不能为空");

      String userName = userInfo.getUserName();// 用户姓名，姓名
      SyhzAppValidationUtil.notNull(userName, "姓名不能为空");

      String userDeptNo = userInfo.getUserDeptNo();// 用户所属单位编码，即 12 位公安机关单位编码。
      SyhzAppValidationUtil.notNull(userDeptNo, "单位编码不能为空");

      String sn = userInfo.getSn();// 证书 SN，针对于使用安全卡或安全类证书
      SyhzAppValidationUtil.notNull(sn, "证书 SN不能为空");
    }
  }


  /**
   * 验证操作请求参照
   * 
   * @param params
   */
  private static void verifyOperationBodyParam(OperationParams params) {
    if (params != null) {
      verifyOperationBodyQueryRequest(params.getData());
    }
  }

  /**
   * 验证操作请求数据
   * 
   * @param queryRequest
   */
  private static void verifyOperationBodyQueryRequest(OperationRequest operationRequest) {
    if (operationRequest != null) {
      String version = operationRequest.getVersion(); // 协议版本号，格式为 yyyyMMdd
      SyhzAppValidationUtil.notNull(version, "协议版本号不能为空，格式为 yyyyMMdd");
      String sessionId = operationRequest.getSessionId(); // 会话 ID，建立连接接口返回结果中的 sessionId。

      SyhzAppValidationUtil.notNull(sessionId, "会话 ID不能为空");
      UserInfo userInfo = operationRequest.getUserInfo();// 用户信息
      verifyBodyUserInfo(userInfo);

      int transaction = operationRequest.getTransaction();// 开启事务标记
      SyhzAppValidationUtil.max(transaction, 1, "开启事务标记超出范围 只能是0或1");

      List<Operation> operations = operationRequest.getOperations();
      verifyOperationBodyOperations(operations);
    }
  }

  private static void verifyOperationBodyOperations(List<Operation> operations) {
    if (operations.size() >= 1) {
      for (Operation operation : operations) {
        verifyOperationBodyOperation(operation);
      }
    } else {
      SyhzAppValidationUtil.notNull(operations, "操作对象数组不能为空");
    }
  }

  private static void verifyOperationBodyOperation(Operation operation) {
    int operationType = operation.getOperationType();
    SyhzAppValidationUtil.notNull(operationType, "操作类型不能为空");
    SyhzAppValidationUtil.min(operationType, 1, "操作类型不能小于1");
    SyhzAppValidationUtil.max(operationType, 3, "操作类型不能大于3");

    String operationId = operation.getOperationId();
    SyhzAppValidationUtil.notNull(operationId, "操作 ID不能为空");

    String dataObjId = operation.getDataObjId();
    SyhzAppValidationUtil.notNull(dataObjId, "资源名称不能为空");

    if (operationType == 1 || operationType == 2) { // 如果是 保存、修改 操作则data数组对象不能为空
      List<OperationRequestData> data = operation.getData();
      if (data.size() == 0) {
        SyhzAppValidationUtil.notNull(operationType, "操作类型为新增、修改时，操作对象数组不能为空");
      }
    } else if (operationType == 3) { // 如果是 删除操作则 condition不能为空
      String condition = operation.getCondition();
      SyhzAppValidationUtil.notNull(condition, "操作类型为删除时，操作条件不能为空");
    }
  }

}
