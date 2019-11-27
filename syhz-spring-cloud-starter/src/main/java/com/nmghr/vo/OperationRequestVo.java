/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.nmghr.vo;

import java.util.Map;

import com.nmghr.entity.operation.OperationParams;
import com.nmghr.util.SyhzUtil;

/**
 * 操作公共参数封装
 *
 * @author kaven
 * @date 2019年11月23日 下午2:15:39
 * @version 1.0
 */
public class OperationRequestVo {
  private String jsonrpc;
  private String method;
  private String id;
  private OperationParams params;

  /**
   * 将接收的参数转化我Vo实体
   * 
   * @param requestBody
   * @return
   */
  public static OperationRequestVo dataToVo(Map<String, Object> requestBody) {
    OperationRequestVo operationRequestVo = new OperationRequestVo();
    String jsonrpc = SyhzUtil.setDate(requestBody.get("jsonrpc"));
    String method = SyhzUtil.setDate(requestBody.get("method"));
    String id = SyhzUtil.setDate(requestBody.get("id"));
    OperationParams operationParams = OperationParams.dataToVo(requestBody);
    operationRequestVo.setJsonrpc(jsonrpc);
    operationRequestVo.setMethod(method);
    operationRequestVo.setId(id);
    operationRequestVo.setParams(operationParams);
    return operationRequestVo;
  }

  public String getJsonrpc() {
    return jsonrpc;
  }

  public void setJsonrpc(String jsonrpc) {
    this.jsonrpc = jsonrpc;
  }

  public String getMethod() {
    return method;
  }

  public void setMethod(String method) {
    this.method = method;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public OperationParams getParams() {
    return params;
  }

  public void setParams(OperationParams params) {
    this.params = params;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("BaseRequestVo [jsonrpc=");
    builder.append(jsonrpc);
    builder.append(", method=");
    builder.append(method);
    builder.append(", id=");
    builder.append(id);
    builder.append(", params=");
    builder.append(params);
    builder.append(']');
    return builder.toString();
  }

}
