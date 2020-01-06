/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.nmghr.entity.query;

import java.util.List;

import com.nmghr.util.app.SyhzAppErrorEnmu;

/**
 * <功能描述/>
 *
 * @author kaven
 * @date 2019年11月23日 下午2:40:40
 * @version 1.0
 */
public class QueryResult {
  private String code;// 返回码
  private String msg;// 返回描述
  private List<QueryResultData> data; // 数据信息
  private Page page;// 分页信息
  private String sign;// 对返回数据 data 的签名

  QueryResult(String code, String msg, List<QueryResultData> data, Page page, String sign) {
    this.code = code;
    this.msg = msg;
    this.sign = sign;
    this.data = data;
    this.page = page;
  }

  public QueryResult(List<QueryResultData> data, Page page, String sign) {
    this.code = SyhzAppErrorEnmu.resultCodeValue_1;
    this.msg = SyhzAppErrorEnmu.resultMsgValue_1;
    this.sign = sign;
    this.data = data;
    this.page = page;
  }


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

  public List<QueryResultData> getData() {
    return data;
  }

  public void setData(List<QueryResultData> data) {
    this.data = data;
  }

  public Page getPage() {
    return page;
  }

  public void setPage(Page page) {
    this.page = page;
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
    builder.append("BaseResult [code=");
    builder.append(code);
    builder.append(", msg=");
    builder.append(msg);
    builder.append(", data=");
    builder.append(data);
    builder.append(", page=");
    builder.append(page);
    builder.append(", sign=");
    builder.append(sign);
    builder.append(']');
    return builder.toString();
  }


}
