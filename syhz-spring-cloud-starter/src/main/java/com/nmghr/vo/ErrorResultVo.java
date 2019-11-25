/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.nmghr.vo;

import com.nmghr.entity.ErrorEntity;

/**
 * <功能描述/>
 *
 * @author kaven
 * @date 2019年11月23日 下午4:09:25
 * @version 1.0
 */
public class ErrorResultVo {
  private String jsonrpc;
  private String id;
  private ErrorEntity error;// 错误信息

  ErrorResultVo(String jsonrpc, String id, String code, String message) {
    this.setJsonrpc(jsonrpc);
    this.setId(id);
    ErrorEntity error = new ErrorEntity(code, message);
    this.setError(error);
  }

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

  public ErrorEntity getError() {
    return error;
  }

  public void setError(ErrorEntity error) {
    this.error = error;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("ErrorResultVo [jsonrpc=");
    builder.append(jsonrpc);
    builder.append(", id=");
    builder.append(id);
    builder.append(", error=");
    builder.append(error);
    builder.append(']');
    return builder.toString();
  }

}
