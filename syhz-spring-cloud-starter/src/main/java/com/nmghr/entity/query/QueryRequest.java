/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.nmghr.entity.query;

import java.util.Map;

import com.nmghr.entity.query.Page;
import com.nmghr.entity.query.Source;
import com.nmghr.entity.query.UserInfo;
import com.nmghr.util.SyhzUtil;

/**
 * 请求查询公共参数封装
 *
 * @author kaven
 * @date 2019年11月23日 下午2:15:39
 * @version 1.0
 */
public class QueryRequest {
  private String version; // 协议版本号，格式为 yyyyMMdd
  private String sessionId; // 会话 ID，建立连接接口返回结果中的 sessionId。
  private UserInfo userInfo;// 用户信息
  private Source source;// 数据源对象
  private String dataObjId;// 资源名称
  private String condition;// 查询条件
  private String fields;// 查询字段
  private String orderBy;// 排序字段。
  private Page page;// 分页对象
  
  public static QueryRequest dataToVo(Map<String, Object> requestBody) {
    Map<String, Object> paramsMapData = (Map<String, Object>) requestBody.get("data");
    QueryRequest queryRequest =new QueryRequest();
    
    String version = SyhzUtil.setDate(paramsMapData.get("version"));
    String sessionId = SyhzUtil.setDate(paramsMapData.get("sessionId"));
    String dataObjId = SyhzUtil.setDate(paramsMapData.get("dataObjId"));
    String condition = SyhzUtil.setDate(paramsMapData.get("condition"));
    String fields = SyhzUtil.setDate(paramsMapData.get("fields"));
    UserInfo userInfo = UserInfo.dataToVo(paramsMapData);
    Source source = Source.dataToVo(paramsMapData);
    Page page = Page.dataToVo(paramsMapData);
    
    queryRequest.setVersion(version);
    queryRequest.setSessionId(sessionId);
    queryRequest.setDataObjId(dataObjId);
    queryRequest.setCondition(condition);
    queryRequest.setFields(fields);
    queryRequest.setUserInfo(userInfo);
    queryRequest.setSource(source);
    queryRequest.setPage(page);
    
    return queryRequest;
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

  public Source getSource() {
    return source;
  }

  public void setSource(Source source) {
    this.source = source;
  }

  public String getDataObjId() {
    return dataObjId;
  }

  public void setDataObjId(String dataObjId) {
    this.dataObjId = dataObjId;
  }

  public String getCondition() {
    return condition;
  }

  public void setCondition(String condition) {
    this.condition = condition;
  }

  public String getFields() {
    return fields;
  }

  public void setFields(String fields) {
    this.fields = fields;
  }

  public String getOrderBy() {
    return orderBy;
  }

  public void setOrderBy(String orderBy) {
    this.orderBy = orderBy;
  }

  public Page getPage() {
    return page;
  }

  public void setPage(Page page) {
    this.page = page;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("QueryParameterVo [version=");
    builder.append(version);
    builder.append(", sessionId=");
    builder.append(sessionId);
    builder.append(", userInfo=");
    builder.append(userInfo);
    builder.append(", source=");
    builder.append(source);
    builder.append(", dataObjId=");
    builder.append(dataObjId);
    builder.append(", condition=");
    builder.append(condition);
    builder.append(", fields=");
    builder.append(fields);
    builder.append(", orderBy=");
    builder.append(orderBy);
    builder.append(", page=");
    builder.append(page);
    builder.append(']');
    return builder.toString();
  }

}
