/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.nmghr.vo;

import com.nmghr.entity.operation.OperationResult;

/**
 * 操作公共返回封装
 *
 * @author kaven
 * @date 2019年11月23日 下午2:15:39
 * @version 1.0
 */
public class OperationResultVo {
  private String jsonrpc;// jsonrpc 版本，固定为 2.0
  private String id;// 请求中的 id，返回同样的 id
  private OperationResult result;// 返回结果

  public String getJsonrpc() {
    return jsonrpc;
  }

  public void setJsonrpc(String jsonrpc) {
    this.jsonrpc = jsonrpc;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public OperationResult getResult() {
    return result;
  }

  public void setResult(OperationResult result) {
    this.result = result;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("QueryResultVo [jsonrpc=");
    builder.append(jsonrpc);
    builder.append(", id=");
    builder.append(id);
    builder.append(", result=");
    builder.append(result);
    builder.append(']');
    return builder.toString();
  }

}
