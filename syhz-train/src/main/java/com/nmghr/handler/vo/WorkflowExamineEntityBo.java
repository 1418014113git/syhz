/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.nmghr.handler.vo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import com.nmghr.util.SyhzUtil;

/**
 * 业务审批Bo
 *
 * @author kaven  
 * @date 2019年9月24日 上午11:12:50 
 * @version 1.0
 */
public class WorkflowExamineEntityBo {
  private String belongSys; // 所属系统(1 知识库 2网上培训)
  private String belongMode; // 所属模块(知识库包含：1 法律法规、2行业标准、3规则制度、4案例指引 网上培训：1在线课程)
  private String belongType; // 所属分类（3环境、1食品、2药品、4综合）
  private String tableId; // 对应表id
  private String workId; // 对应审批表id
  private String creationId; // 创建人
  private String creationName; // 创建人姓名
  private String creationTime; // 创建时间
  private int currentAuditType; // 审核状态（0待审核、1审核中、2审核通过、3审核不通过）
  private String deptCode; // 当前审核部门code
  private String deptAreaCode; // 当前审核部门所属区域
  private String deptName; // 当前审核部门名称
  private String remark; // 当前审核部门意见
  
  public static WorkflowExamineEntityBo workflowExamineMapToEntityBo(Map<String, String> headers, Map<String, Object> requestBody) {
    WorkflowExamineEntityBo workflowEntityBo = new WorkflowExamineEntityBo();
    workflowEntityBo.setBelongSys(SyhzUtil.setDate(requestBody.get("belongSys")));
    workflowEntityBo.setBelongMode(SyhzUtil.setDate(requestBody.get("belongMode")));
    workflowEntityBo.setBelongType(SyhzUtil.setDate(requestBody.get("belongType")));
    workflowEntityBo.setTableId(SyhzUtil.setDate(requestBody.get("tableId")));
    workflowEntityBo.setWorkId(SyhzUtil.setDate(requestBody.get("workId")));
    workflowEntityBo.setCreationId(SyhzUtil.setDate(requestBody.get("creationId")));
    workflowEntityBo.setCreationName(SyhzUtil.setDate(requestBody.get("creationName")));
    
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    String dataStr = simpleDateFormat.format(new Date());
    workflowEntityBo.setCreationTime(dataStr);
    workflowEntityBo.setCurrentAuditType(SyhzUtil.setDateInt(requestBody.get("currentAuditType"))); 
    workflowEntityBo.setRemark(SyhzUtil.setDate(requestBody.get("remark"))); 
    workflowEntityBo.setDeptCode(SyhzUtil.setDate(requestBody.get("deptCode"))); 
    workflowEntityBo.setDeptName(SyhzUtil.setDate(requestBody.get("deptName"))); 
    workflowEntityBo.setDeptAreaCode(SyhzUtil.setDate(requestBody.get("deptAreaCode")));
    
    return workflowEntityBo;
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

  public String getWorkId() {
    return workId;
  }

  public void setWorkId(String workId) {
    this.workId = workId;
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

  public int getCurrentAuditType() {
    return currentAuditType;
  }

  public void setCurrentAuditType(int currentAuditType) {
    this.currentAuditType = currentAuditType;
  }

  public String getDeptCode() {
    return deptCode;
  }

  public void setDeptCode(String deptCode) {
    this.deptCode = deptCode;
  }

  public String getDeptAreaCode() {
    return deptAreaCode;
  }

  public void setDeptAreaCode(String deptAreaCode) {
    this.deptAreaCode = deptAreaCode;
  }

  public String getDeptName() {
    return deptName;
  }

  public void setDeptName(String deptName) {
    this.deptName = deptName;
  }

  public String getRemark() {
    return remark;
  }

  public void setRemark(String remark) {
    this.remark = remark;
  }


}
