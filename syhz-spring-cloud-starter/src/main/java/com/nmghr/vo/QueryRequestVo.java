/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.nmghr.vo;

import java.util.Map;

import com.nmghr.entity.query.QueryParams;
import com.nmghr.util.SyhzUtil;

/**
 * 请求查询公共参数封装
 *
 * @author kaven
 * @date 2019年11月23日 下午2:15:39
 * @version 1.0
 */
public class QueryRequestVo {
  private String jsonrpc;
  private String method;
  private String id;
  private QueryParams params;

  /**
   * 将接收的参数转化我Vo实体
   * 
   * @param requestBody
   * @return
   */
  public static QueryRequestVo dataToVo(Map<String, Object> requestBody) {
    QueryRequestVo baseRequestVo = new QueryRequestVo();
    String jsonrpc = SyhzUtil.setDate(requestBody.get("jsonrpc"));
    String method = SyhzUtil.setDate(requestBody.get("method"));
    String id = SyhzUtil.setDate(requestBody.get("id"));
    QueryParams queryParams = QueryParams.dataToVo(requestBody);
    baseRequestVo.setJsonrpc(jsonrpc);
    baseRequestVo.setMethod(method);
    baseRequestVo.setId(id);
    baseRequestVo.setParams(queryParams);
    return baseRequestVo;
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

  public QueryParams getParams() {
    return params;
  }

  public void setParams(QueryParams params) {
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
