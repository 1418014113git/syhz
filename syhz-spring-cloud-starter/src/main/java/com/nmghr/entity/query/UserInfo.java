/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.nmghr.entity.query;

import java.util.Map;

import com.nmghr.util.SyhzUtil;

/**
 * 用户信息，也就是请求者的用户信息。
 *
 * @author kaven
 * @date 2019年11月23日 下午2:17:39
 * @version 1.0
 */
public class UserInfo {
  private String userId;// 用户 ID，警号
  private String userName;// 用户姓名，姓名
  private String userDeptNo;// 用户所属单位编码，即 12 位公安机关单位编码。
  private String sn;// 证书 SN，针对于使用安全卡或安全类证书
  private String sfzh;// 身份证号
  private Map<Object, Object> extAttr; // 此节点用于动态扩展用户属性，其下可以随意动态增加子节点

  public static UserInfo dataToVo(Map<String, Object> requestBody) {
    Map<String, Object> userInfoMap = (Map<String, Object>) requestBody.get("userInfo");
    UserInfo userInfo = new UserInfo();
    String userId = SyhzUtil.setDate(userInfoMap.get("userId"));
    String userName = SyhzUtil.setDate(userInfoMap.get("userName"));
    String userDeptNo = SyhzUtil.setDate(userInfoMap.get("userDeptNo"));
    String sn = SyhzUtil.setDate(userInfoMap.get("sn"));
    String sfzh = SyhzUtil.setDate(userInfoMap.get("sfzh"));
    Map<Object, Object> extAttr = (Map<Object, Object>) userInfoMap.get("extAttr");
    userInfo.setUserId(userId);
    userInfo.setUserName(userName);
    userInfo.setUserDeptNo(userDeptNo);
    userInfo.setSn(sn);
    userInfo.setSfzh(sfzh);
    userInfo.setExtAttr(extAttr);
    return userInfo;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getUserDeptNo() {
    return userDeptNo;
  }

  public void setUserDeptNo(String userDeptNo) {
    this.userDeptNo = userDeptNo;
  }

  public String getSn() {
    return sn;
  }

  public void setSn(String sn) {
    this.sn = sn;
  }

  public String getSfzh() {
    return sfzh;
  }

  public void setSfzh(String sfzh) {
    this.sfzh = sfzh;
  }

  public Map<Object, Object> getExtAttr() {
    return extAttr;
  }

  public void setExtAttr(Map<Object, Object> extAttr) {
    this.extAttr = extAttr;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("UserInfo [userId=");
    builder.append(userId);
    builder.append(", userName=");
    builder.append(userName);
    builder.append(", userDeptNo=");
    builder.append(userDeptNo);
    builder.append(", sn=");
    builder.append(sn);
    builder.append(", sfzh=");
    builder.append(sfzh);
    builder.append(", extAttr=");
    builder.append(extAttr);
    builder.append(']');
    return builder.toString();
  }
}
