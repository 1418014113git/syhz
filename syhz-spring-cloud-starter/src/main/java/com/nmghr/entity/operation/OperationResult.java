/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.nmghr.entity.operation;

import java.util.List;

/**
 * <功能描述/>
 *
 * @author kaven
 * @date 2019年11月23日 下午3:30:19
 * @version 1.0
 */
public class OperationResult {
  private String code;// 返回码，成功 1，失败 2
  private String msg;// 返回描述
  private List<OperationResultData> operations;// 操作对象数组
  private String sign;// 对返回数据 data 的签名
  
  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getMsg() {
    return msg;
  }

  public void setMsg(String msg) {
    this.msg = msg;
  }

  public String getSign() {
    return sign;
  }

  public void setSign(String sign) {
    this.sign = sign;
  }

  public List<OperationResultData> getOperations() {
    return operations;
  }

  public void setOperations(List<OperationResultData> operations) {
    this.operations = operations;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("OperationResult [code=");
    builder.append(code);
    builder.append(", msg=");
    builder.append(msg);
    builder.append(", sign=");
    builder.append(sign);
    builder.append(", operations=");
    builder.append(operations);
    builder.append(']');
    return builder.toString();
  }
}
