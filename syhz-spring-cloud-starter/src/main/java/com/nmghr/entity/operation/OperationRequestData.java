/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.nmghr.entity.operation;

import java.util.List;

/**
 * <功能描述/>
 *
 * @author kaven
 * @date 2019年11月23日 下午3:57:00
 * @version 1.0
 */
public class OperationRequestData {
  private List<OperationFieldValue> fieldValues;

  public List<OperationFieldValue> getFieldValues() {
    return fieldValues;
  }

  public void setFieldValues(List<OperationFieldValue> fieldValues) {
    this.fieldValues = fieldValues;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("OperationRequestData [fieldValues=");
    builder.append(fieldValues);
    builder.append(']');
    return builder.toString();
  }

}
