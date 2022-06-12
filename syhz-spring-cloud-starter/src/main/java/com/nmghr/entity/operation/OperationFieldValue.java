/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.nmghr.entity.operation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.nmghr.util.SyhzUtil;

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

  public static List<OperationFieldValue> dataToListVo(Object object) {
    List<Object> fieldValueListReq = (List<Object>) object;
    List<OperationFieldValue> fieldValueList = new ArrayList<OperationFieldValue>();
    if (fieldValueListReq.size() >= 1) {
      OperationFieldValue operationFieldValue = null;
      for (Object obj: fieldValueListReq) {
        Map<String,Object> objMap = (Map<String, Object>) obj;
        operationFieldValue = new OperationFieldValue();
        operationFieldValue.setField(SyhzUtil.setDate(objMap.get("field")));
        operationFieldValue.setValue(SyhzUtil.setDate(objMap.get("value")));
        fieldValueList.add(operationFieldValue);
      }
    }
    return fieldValueList;
  }

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
