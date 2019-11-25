/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.nmghr.entity.query;

/**
 * 字段对象数组
 *
 * @author kaven
 * @date 2019年11月23日 下午2:45:48
 * @version 1.0
 */
public class FieldValue {
  private String field;// 字段名称
  private String value;// 字段值
  private int isCode;// 标记是否为字典，1 是，0 否。若是字典，则 codeValue 不得
  private String codeValue;// value 的字典代码，当 isCode 为 1 时，不得为空

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

  public int getIsCode() {
    return isCode;
  }

  public void setIsCode(int isCode) {
    this.isCode = isCode;
  }

  public String getCodeValue() {
    return codeValue;
  }

  public void setCodeValue(String codeValue) {
    this.codeValue = codeValue;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("FieldValue [field=");
    builder.append(field);
    builder.append(", value=");
    builder.append(value);
    builder.append(", isCode=");
    builder.append(isCode);
    builder.append(", codeValue=");
    builder.append(codeValue);
    builder.append(']');
    return builder.toString();
  }

}
