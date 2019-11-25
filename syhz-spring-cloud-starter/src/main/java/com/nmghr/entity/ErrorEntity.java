/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.nmghr.entity;

import com.nmghr.basic.common.exception.GlobalErrorException;

/**
 * <功能描述/>
 *
 * @author kaven
 * @date 2019年11月23日 下午4:09:53
 * @version 1.0
 */
public class ErrorEntity extends GlobalErrorException{
  private static final long serialVersionUID = 1L;
  private String code; // 错误码
  private String message;// 错误详细信息

  public ErrorEntity(String code, String message) {
    super(code, message, null);
    this.code = code;
    this.message = message;
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
    builder.append("Error [code=");
    builder.append(code);
    builder.append(", message=");
    builder.append(message);
    builder.append(']');
    return builder.toString();
  }

}
