/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nmghr.entity.operation;

import java.util.List;

/**
 * <功能描述/>
 *
 * @author kaven
 * @date 2019年11月23日 下午3:54:57 
 * @version 1.0   
 */
public class Operation {
  private int operationType;
  private String operationId;
  private String sourceId;
  private String dataObjId;
  private String condition;
  private List<OperationRequestData> data;
  
  
  public int getOperationType() {
    return operationType;
  }
  public void setOperationType(int operationType) {
    this.operationType = operationType;
  }
  public String getOperationId() {
    return operationId;
  }
  public void setOperationId(String operationId) {
    this.operationId = operationId;
  }
  public String getSourceId() {
    return sourceId;
  }
  public void setSourceId(String sourceId) {
    this.sourceId = sourceId;
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
  public List<OperationRequestData> getData() {
    return data;
  }
  public void setData(List<OperationRequestData> data) {
    this.data = data;
  }
  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("Operation [operationType=");
    builder.append(operationType);
    builder.append(", operationId=");
    builder.append(operationId);
    builder.append(", sourceId=");
    builder.append(sourceId);
    builder.append(", dataObjId=");
    builder.append(dataObjId);
    builder.append(", condition=");
    builder.append(condition);
    builder.append(']');
    return builder.toString();
  }
  
}
