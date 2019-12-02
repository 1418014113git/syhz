/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.nmghr.entity.operation;

/**
 * <功能描述/>
 *
 * @author kaven
 * @date 2019年11月23日 下午3:31:37
 * @version 1.0
 */
public class OperationResultData {
  private String operationId;// 操作 ID
  private String operationCode;// 操作结果标识，1 成功，2 失败 1
  private String operationMsg;// 操作结果消息，对结果标识的详细描述
  private int operationNum;// 操作记录数，用于说明更新和删除操作的记录数

  public String getOperationId() {
    return operationId;
  }

  public void setOperationId(String operationId) {
    this.operationId = operationId;
  }

  public String getOperationCode() {
    return operationCode;
  }

  public void setOperationCode(String operationCode) {
    this.operationCode = operationCode;
  }

  public String getOperationMsg() {
    return operationMsg;
  }

  public void setOperationMsg(String operationMsg) {
    this.operationMsg = operationMsg;
  }

  public int getOperationNum() {
    return operationNum;
  }

  public void setOperationNum(int operationNum) {
    this.operationNum = operationNum;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("OperationResultData [operationId=");
    builder.append(operationId);
    builder.append(", operationCode=");
    builder.append(operationCode);
    builder.append(", operationMsg=");
    builder.append(operationMsg);
    builder.append(", operationNum=");
    builder.append(operationNum);
    builder.append(']');
    return builder.toString();
  }
}
