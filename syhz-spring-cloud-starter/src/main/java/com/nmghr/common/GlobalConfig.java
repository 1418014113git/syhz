/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nmghr.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * <功能描述/>
 *
 * @author weber  
 * @date 2019年4月19日 下午5:09:27 
 * @version 1.0   
 */
@Configuration
public class GlobalConfig {
  
  // "http://staging430.t.lanxin.cn/cgi-bin/token?grant_type=client_credential&appid=101783&secret=2TMFJ4ZgHGZPTwE_RW"
  public static String reqUrl;
  public static String grantType;
  public static String appid;
  public static String secret;
  // "https://staging430.t.lanxin.cn/cgi-bin/message/custom/send?rand=97&access_token="+access_token, objectString
  public static String linkmsg;
  public static String lxApi;
  public static String roleCode;
  public static String runFlag;

  @Value("${lanxin.reqUrl}")
  public void setReqUrl(String reqUrl) {
    GlobalConfig.reqUrl = reqUrl;
  }
  
  @Value("${lanxin.grantType}")
  public void setGrantType(String grantType) {
    GlobalConfig.grantType = grantType;
  }
  
  @Value("${lanxin.appid}")
  public void setAppid(String appid) {
    GlobalConfig.appid = appid;
  }
  
  @Value("${lanxin.secret}")
  public void setSecret(String secret) {
    GlobalConfig.secret = secret;
  }
  
  @Value("${lanxin.linkmsg}")
  public void setLinkmsg(String linkmsg) {
    GlobalConfig.linkmsg = linkmsg;
  }

  @Value("${lanxin.lxApi}")
  public void setLxApi(String lxApi) {
    GlobalConfig.lxApi = lxApi;
  }
  
  @Value("${lanxin.roleCode}")
  public void setRoleCode(String roleCode) {
    GlobalConfig.roleCode = roleCode;
  }

  @Value("${lanxin.runFlag}")
  public void setRunFlag(String runFlag) {
    GlobalConfig.runFlag = runFlag;
  }
}
