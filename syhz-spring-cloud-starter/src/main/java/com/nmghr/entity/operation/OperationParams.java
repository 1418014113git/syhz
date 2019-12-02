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

import java.util.Map;

import com.nmghr.entity.query.QueryParams;
import com.nmghr.entity.query.QueryRequest;
import com.nmghr.util.SyhzUtil;

/**
 * <功能描述/>
 *
 * @author kaven
 * @date 2019年11月23日 下午3:47:35 
 * @version 1.0   
 */
public class OperationParams {
  private OperationRequest data;
  private String sign;
  
  public static OperationParams dataToVo(Map<String, Object> requestBody) {
    Map<String, Object> paramsMap = (Map<String, Object>) requestBody.get("params");
    OperationParams operationParams = new OperationParams();

    OperationRequest operationRequest = OperationRequest.dataToVo(paramsMap);
    operationParams.setData(operationRequest);

    String sign = SyhzUtil.setDate(paramsMap.get("sign"));
    operationParams.setSign(sign);
    
    return operationParams;
  }

  public OperationRequest getData() {
    return data;
  }

  public void setData(OperationRequest data) {
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
    builder.append("OperationParams [data=");
    builder.append(data);
    builder.append(", sign=");
    builder.append(sign);
    builder.append(']');
    return builder.toString();
  }
  
  
}
