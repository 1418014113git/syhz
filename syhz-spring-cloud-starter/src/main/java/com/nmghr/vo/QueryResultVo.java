/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.nmghr.vo;

/**
 * 查询公共返回封装
 *
 * @author kaven
 * @date 2019年11月23日 下午2:15:39
 * @version 1.0
 */
public class QueryResultVo {

  private String code;
  private String msg;
  private String data;

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getMsg() {
    return msg;
  }

  public void setMsg(String msg) {
    this.msg = msg;
  }

  public String getData() {
    return data;
  }

  public void setData(String data) {
    this.data = data;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("QueryResultVo [code=");
    builder.append(code);
    builder.append(", msg=");
    builder.append(msg);
    builder.append(", data=");
    builder.append(data);
    builder.append(']');
    return builder.toString();
  }
}
