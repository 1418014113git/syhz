/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.nmghr.handler.vo;

import java.util.Map;

import com.nmghr.util.SyhzUtil;

/**
 * 积分名下记录表
 *
 * @author kaven
 * @date 2019年9月25日 上午10:57:39
 * @version 1.0
 */
public class FractionEntityBo {
  private String belongSys; // 所属系统(1 知识库 2网上培训)
  private String belongMode; // 所属模块(知识库包含：1 法律法规、2行业标准、3规则制度、4案例指引 网上培训：1在线课程)
  private String belongType; // 所属分类（3环境、1食品、2药品、4综合）
  private String tableId; // 对应表id
  
  private int branch; // 单次增加积分数
  private int maxBranch; // 每天最多增加积分数
  
  private int fractionType; // 获得积分分类 0登陆 1学习资料 2资料上传 3资料下载 4学习时长
  private int fractionReckon; // 增加积分 0增加 1减少
  private int fractionNumber; // 获得积分数量 1为+1 -1为减少
  private String fractionTime; // 获得积分时间
  private String fractionUserId; // 获得人
  private String fractionUserName; // 获得人姓名
  private String fractionAreaCode; // 获得人部门所属区域
  private String fractionDeptCode; // 获得人所属部门code
  private String fractionDeptName; // 获得人 所属部门名称

  private String creationId; // 创建人
  private String creationName; // 创建人姓名
  private String creationTime; // 创建时间
  private String remark; // 备注

  public static FractionEntityBo mapDataToBoData(Map<String, Object> requestBody) {
    FractionEntityBo fractionEntityBo = new FractionEntityBo();
    fractionEntityBo.setBelongSys(SyhzUtil.setDate(requestBody.get("belongSys")));
    fractionEntityBo.setBelongMode(SyhzUtil.setDate(requestBody.get("belongMode")));
    fractionEntityBo.setBelongType(SyhzUtil.setDate(requestBody.get("belongType")));
    fractionEntityBo.setTableId(SyhzUtil.setDate(requestBody.get("tableId")));
    fractionEntityBo.setCreationId(SyhzUtil.setDate(requestBody.get("creationId")));
    fractionEntityBo.setCreationName(SyhzUtil.setDate(requestBody.get("creationName")));
    fractionEntityBo.setRemark(SyhzUtil.setDate(requestBody.get("remark")));
    
    fractionEntityBo.setBranch(SyhzUtil.setDateInt(requestBody.get("branch")));
    fractionEntityBo.setMaxBranch(SyhzUtil.setDateInt(requestBody.get("maxBranch")));
    
    fractionEntityBo.setFractionType(SyhzUtil.setDateInt(requestBody.get("fractionType")));
    fractionEntityBo.setFractionReckon(SyhzUtil.setDateInt(requestBody.get("fractionReckon")));
    fractionEntityBo.setFractionNumber(0);
    fractionEntityBo.setFractionTime(SyhzUtil.setDate(requestBody.get("fractionTime")));
    fractionEntityBo.setFractionUserId(SyhzUtil.setDate(requestBody.get("fractionUserId")));
    fractionEntityBo.setFractionUserName(SyhzUtil.setDate(requestBody.get("fractionUserName")));
    
    fractionEntityBo.setFractionDeptCode(SyhzUtil.setDate(requestBody.get("fractionDeptCode")));
    fractionEntityBo.setFractionDeptName(SyhzUtil.setDate(requestBody.get("fractionDeptName")));
    fractionEntityBo.setFractionAreaCode(SyhzUtil.setDate(requestBody.get("fractionAreaCode")));
    return fractionEntityBo;
  }

  public String getBelongSys() {
    return belongSys;
  }

  public void setBelongSys(String belongSys) {
    this.belongSys = belongSys;
  }

  public String getBelongMode() {
    return belongMode;
  }

  public void setBelongMode(String belongMode) {
    this.belongMode = belongMode;
  }

  public String getBelongType() {
    return belongType;
  }

  public void setBelongType(String belongType) {
    this.belongType = belongType;
  }

  public String getTableId() {
    return tableId;
  }

  public void setTableId(String tableId) {
    this.tableId = tableId;
  }

  public int getFractionType() {
    return fractionType;
  }

  public void setFractionType(int fractionType) {
    this.fractionType = fractionType;
  }

  public int getFractionNumber() {
    return fractionNumber;
  }

  public void setFractionNumber(int fractionNumber) {
    this.fractionNumber = fractionNumber;
  }

  public String getFractionTime() {
    return fractionTime;
  }

  public void setFractionTime(String fractionTime) {
    this.fractionTime = fractionTime;
  }

  public String getFractionUserId() {
    return fractionUserId;
  }

  public void setFractionUserId(String fractionUserId) {
    this.fractionUserId = fractionUserId;
  }

  public String getFractionUserName() {
    return fractionUserName;
  }

  public void setFractionUserName(String fractionUserName) {
    this.fractionUserName = fractionUserName;
  }

  public String getFractionAreaCode() {
    return fractionAreaCode;
  }

  public void setFractionAreaCode(String fractionAreaCode) {
    this.fractionAreaCode = fractionAreaCode;
  }

  public String getFractionDeptCode() {
    return fractionDeptCode;
  }

  public void setFractionDeptCode(String fractionDeptCode) {
    this.fractionDeptCode = fractionDeptCode;
  }

  public String getFractionDeptName() {
    return fractionDeptName;
  }

  public void setFractionDeptName(String fractionDeptName) {
    this.fractionDeptName = fractionDeptName;
  }

  public String getCreationId() {
    return creationId;
  }

  public void setCreationId(String creationId) {
    this.creationId = creationId;
  }

  public String getCreationName() {
    return creationName;
  }

  public void setCreationName(String creationName) {
    this.creationName = creationName;
  }

  public String getCreationTime() {
    return creationTime;
  }

  public void setCreationTime(String creationTime) {
    this.creationTime = creationTime;
  }

  public String getRemark() {
    return remark;
  }

  public void setRemark(String remark) {
    this.remark = remark;
  }

  public int getBranch() {
    return branch;
  }

  public void setBranch(int branch) {
    this.branch = branch;
  }

  public int getMaxBranch() {
    return maxBranch;
  }

  public void setMaxBranch(int maxBranch) {
    this.maxBranch = maxBranch;
  }

  public int getFractionReckon() {
    return fractionReckon;
  }

  public void setFractionReckon(int fractionReckon) {
    this.fractionReckon = fractionReckon;
  }

}
