/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.nmghr.entity.operation;

/**
 * 字段对象数组
 *
 * @author kaven
 * @date 2019年11月23日 下午2:45:48
 * @version 1.0
 */
public class OperationFieldValue {
  private String field;// 字段名称
  private String value;// 字段值

  public String getField() {
    return field;
  }

  public void setField(String field) {
    this.field = field;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }


  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("FieldValue [field=");
    builder.append(field);
    builder.append(", value=");
    builder.append(value);
    builder.append(']');
    return builder.toString();
  }

}
