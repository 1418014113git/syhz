/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.nmghr.util.app;

/**
 * 返回结果工具类.
 *
 * @author kaven  
 * @date 2019年11月25日 上午11:44:07 
 * @version 1.0
 */
public class Result extends com.nmghr.basic.common.Result {

  private String jsonrpc;
  private String id;
  private Object result;

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

  public Object getResult() {
    return result;
  }

  public void setResult(Object result) {
    this.result = result;
  }
  
  protected Result(Object code, String message) {
    super(code, message);
  }
  
  public Result(String jsonrpc, String id, Object result) {
    super("", "");
    this.jsonrpc = jsonrpc;
    this.id = id;
    this.result = result;
  }

  /**
   * 创建返回结果.
   * 
   * @param code 返回码
   * @param message 返回信息
   * @param data 返回数据
   * @param desc 返回描述
   * @return Result
   */
  public static Result create(String jsonrpc, String id, Object result) {
    return new Result(jsonrpc, id, result);
  }

  /**
   * 创建返回成功结果.
   * 
   * @param data 数据
   * @return Result
   */
  public static Result ok(String jsonrpc, String id, Object result) {
    return new Result(jsonrpc, id, result);
  }


  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("Result [jsonrpc=");
    builder.append(jsonrpc);
    builder.append(", id=");
    builder.append(id);
    builder.append(", result=");
    builder.append(result);
    builder.append(']');
    return builder.toString();
  }

}
