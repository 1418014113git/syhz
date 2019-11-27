/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.nmghr.entity.query;

import java.util.Map;

import com.nmghr.util.SyhzUtil;

/**
 * <功能描述/>
 *
 * @author kaven
 * @date 2019年11月23日 下午2:36:48
 * @version 1.0
 */
public class QueryParams {
  private QueryRequest data;
  private String sign;

  public static QueryParams dataToVo(Map<String, Object> requestBody) {
    Map<String, Object> paramsMap = (Map<String, Object>) requestBody.get("params");
    QueryParams aueryParams = new QueryParams();

    QueryRequest queryRequest = QueryRequest.dataToVo(paramsMap);
    aueryParams.setData(queryRequest);

    String sign = SyhzUtil.setDate(paramsMap.get("sign"));
    aueryParams.setSign(sign);
    
    return aueryParams;
  }

  public QueryRequest getData() {
    return data;
  }

  public void setData(QueryRequest data) {
    this.data = data;
  }

  public String getSign() {
    return sign;
  }

  public void setSign(String sign) {
    this.sign = sign;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("BaseParams [data=");
    builder.append(data);
    builder.append(", sign=");
    builder.append(sign);
    builder.append(']');
    return builder.toString();
  }

}
