/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.nmghr.entity.operation;

import java.util.List;
import java.util.Map;

import com.nmghr.entity.query.UserInfo;
import com.nmghr.util.SyhzUtil;

/**
 * <功能描述/>
 *
 * @author kaven
 * @date 2019年11月23日 下午3:50:03
 * @version 1.0
 */
public class OperationRequest {
  private String version; // 协议版本号，格式为 yyyyMMdd
  private String sessionId; // 会话 ID，建立连接接口返回结果中的 sessionId。
  private UserInfo userInfo;// 用户信息
  private int transaction; // 开启事务标记，1 为开启，0 不开启。
  private List<Operation> operations;

  public static OperationRequest dataToVo(Map<String, Object> requestBody) {
    Map<String, Object> paramsMapData = (Map<String, Object>) requestBody.get("data");
    OperationRequest operationRequest = new OperationRequest();

    String version = SyhzUtil.setDate(paramsMapData.get("version"));
    operationRequest.setVersion(version);
    
    String sessionId = SyhzUtil.setDate(paramsMapData.get("sessionId"));
    operationRequest.setSessionId(sessionId);
    
    int transaction = SyhzUtil.setDateInt(paramsMapData.get("transaction"));
    operationRequest.setTransaction(transaction);
    
    UserInfo userInfo = UserInfo.dataToVo(paramsMapData);
    operationRequest.setUserInfo(userInfo);

    List<Operation> operations = Operation.dataToListVo(paramsMapData);
    operationRequest.setOperations(operations);

    return operationRequest;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public String getSessionId() {
    return sessionId;
  }

  public void setSessionId(String sessionId) {
    this.sessionId = sessionId;
  }

  public UserInfo getUserInfo() {
    return userInfo;
  }

  public void setUserInfo(UserInfo userInfo) {
    this.userInfo = userInfo;
  }

  public int getTransaction() {
    return transaction;
  }

  public void setTransaction(int transaction) {
    this.transaction = transaction;
  }

	public List<Operation> getOperations() {
    return operations;
  }

  public void setOperations(List<Operation> operations) {
    this.operations = operations;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("OperationRequest [version=");
    builder.append(version);
    builder.append(", sessionId=");
    builder.append(sessionId);
    builder.append(", userInfo=");
    builder.append(userInfo);
    builder.append(", transaction=");
    builder.append(transaction);
    builder.append(", operations=");
    builder.append(operations);
    builder.append(']');
    return builder.toString();
  }


}
