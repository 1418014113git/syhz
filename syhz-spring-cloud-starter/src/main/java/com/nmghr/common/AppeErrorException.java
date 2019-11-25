/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.nmghr.common;

import com.nmghr.basic.common.exception.ErrorInfoInterface;
import com.nmghr.basic.common.exception.GlobalErrorException;

/**
 * <功能描述/>
 *
 * @author kaven
 * @date 2019年11月25日 下午4:47:20
 * @version 1.0
 */
public class AppeErrorException extends GlobalErrorException {

  private static final long serialVersionUID = 1L;
  private String code; // 错误码
  private String message;// 错误详细信息
  
  public AppeErrorException(String code, String message) {
    super(code, message, null);
    this.code = code;
    this.message = message;
  }
  
  public AppeErrorException(ErrorInfoInterface errorInfo, Object[] args) {
    super(errorInfo, args);
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("AppeErrorException [code=");
    builder.append(code);
    builder.append(", message=");
    builder.append(message);
    builder.append(']');
    return builder.toString();
  }
}
