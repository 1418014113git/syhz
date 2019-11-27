/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nmghr.util.app;

/**
 * <功能描述/>
 *
 * @author kaven
 * @date 2019年11月23日 下午4:12:57 
 * @version 1.0   
 */
public enum SyhzAppErrorEnmu {
  SUCCESS("1", "OK"), 
  ERROR_32700("-32700", "Parse error 语法解析错误"),
  ERROR_32600("-32600", "Invalid Request 无效请求"),
  ERROR_32601("-32601", "Method not found 找不到方法"),
  ERROR_32602("-32602", "Invalid params 无效的参数"),
  ERROR_32603("-32603", "Internal error 内部错误"),
  ERROR_32000("-32000", "Server error 服务端错误"),
  ERROR_32099("-32099", "Server error 服务端错误"),
  ERROR_OTRHE("-32099", "Server error 服务端错误"),
  ERROR_500("-32099", "Server error 服务端错误");
  
  public static final String jsonrpcValue = "2.0";
  public static final String resultCodeValue_1 = "1"; // 返回码，成功 1，失败 2
  public static final String resultCodeValue_2 = "2"; // 返回码，成功 1，失败 2
  public static final String resultMsgValue_1 = "OK"; // 返回描述，特别地失败时，错误的详细描述
  
  public static final String method_query = "query";
  public static final String method_operate = "operate";
  
  private String code;

  private String message;

  SyhzAppErrorEnmu(String code, String message) {
    this.code = code;
    this.message = message;
  }

  public String getCode() {
    return this.code;
  }

  public String getMessage() {
    return this.message;
  } 
}
