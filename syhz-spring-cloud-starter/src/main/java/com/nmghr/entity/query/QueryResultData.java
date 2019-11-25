/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.nmghr.entity.query;

import java.util.List;

/**
 * <功能描述/>
 *
 * @author kaven
 * @date 2019年11月23日 下午2:40:40
 * @version 1.0
 */
public class QueryResultData {
  private String sourceId; // 数据源 ID
  private List<FieldValue> fieldValues; // 字段对象数组

  public String getSourceId() {
    return sourceId;
  }

  public void setSourceId(String sourceId) {
    this.sourceId = sourceId;
  }

  public List<FieldValue> getFieldValues() {
    return fieldValues;
  }

  public void setFieldValues(List<FieldValue> fieldValues) {
    this.fieldValues = fieldValues;
  }


  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("BaseResultData [sourceId=");
    builder.append(sourceId);
    builder.append(", fieldValues=");
    builder.append(fieldValues);
    builder.append(']');
    return builder.toString();
  }

}
