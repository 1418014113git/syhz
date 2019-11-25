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
 * 数据源对象
 *
 * @author kaven
 * @date 2019年11月23日 下午2:18:29
 * @version 1.0
 */
public class Source {
  /*
   * 数据源 ID，可指定多个数据源，多个以英文逗号分隔。 如果为空，则表示不指定数据源。默认值为空。 由第三方应用服务或第三方数据请求服务提供。
   */
  private String sourceId;
  
  public static Source dataToVo(Map<String, Object> requestBody) {
    Map<String, Object> sourceMap = (Map<String, Object>) requestBody.get("source");
    Source source = new Source();
    String sourceId = SyhzUtil.setDate(sourceMap.get("sourceId"));
    source.setSourceId(sourceId);
    return source;
  }

  public String getSourceId() {
    return sourceId;
  }

  public void setSourceId(String sourceId) {
    this.sourceId = sourceId;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("Source [sourceId=");
    builder.append(sourceId);
    builder.append(']');
    return builder.toString();
  }


}
