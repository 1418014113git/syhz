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
 * 创建审批数据Bo
 *
 * @author kaven
 * @date 2019年9月23日 上午11:34:41
 * @version 1.0
 */
public class WorkflowEntityBo {
  private String belongSys; // 所属系统(1 知识库 2网上培训)
  private String belongMode; // 所属模块(知识库包含：1 法律法规、2行业标准、3规则制度、4案例指引 网上培训：1在线课程)
  private String belongType; // 所属分类（3环境、1食品、2药品、4综合）
  private String tableId; // 对应表id
  private String creationId; // 创建人
  private String creationName; // 创建人姓名
  private String creationTime; // 创建时间
  private int auditStatus; // 总审核状态（0待审核、1审核中、2审核通过、3审核不通过）
  private String auditCurrentNode; // 当前审核节点（0部门 1市级 2省级）
  private String currentAuditDepCode; // 当前审核部门code
  private String currentAuditType; // 当前审核状态（1审核中、2审核通过、3审核不通过）
  private DeptEntityBo myDept; // 本单位
  private DeptEntityBo cityDept; // 市级
  private DeptEntityBo provinceDept; // 省级
  
  public static WorkflowEntityBo workflowMapToEntityBo(Map<String, String> headers, Map<String, Object> requestBody) {
    WorkflowEntityBo workflowEntityBo = new WorkflowEntityBo();
    workflowEntityBo.setBelongSys(SyhzUtil.setDate(requestBody.get("belongSys")));
    workflowEntityBo.setBelongMode(SyhzUtil.setDate(requestBody.get("belongMode")));
    workflowEntityBo.setBelongType(SyhzUtil.setDate(requestBody.get("belongType")));
    workflowEntityBo.setTableId(SyhzUtil.setDate(requestBody.get("tableId")));
    workflowEntityBo.setCreationId(SyhzUtil.setDate(requestBody.get("creationId")));
    workflowEntityBo.setCreationName(SyhzUtil.setDate(requestBody.get("creationName")));
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    String dataStr = simpleDateFormat.format(new Date());
    workflowEntityBo.setCreationTime(dataStr);
    workflowEntityBo.setAuditStatus(1); // 默认为 0待审核
    
    Object myDept = requestBody.get("myDept");
    DeptEntityBo myDeptBo = DeptEntityBo.workflowMapToEntityBo(myDept);
    if(myDeptBo != null) {
      workflowEntityBo.setMyDept(myDeptBo);
    }
    
    Object cityDept = requestBody.get("cityDept");
    DeptEntityBo cityDeptBo = DeptEntityBo.workflowMapToEntityBo(cityDept);
    if(cityDeptBo != null) {
      workflowEntityBo.setCityDept(cityDeptBo);
    }
    
    Object provinceDept = requestBody.get("provinceDept");
    DeptEntityBo provinceDeptBo = DeptEntityBo.workflowMapToEntityBo(provinceDept);
    if(provinceDeptBo != null) {
      workflowEntityBo.setProvinceDept(provinceDeptBo);
    }
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

  public int getAuditStatus() {
    return auditStatus;
  }

  public void setAuditStatus(int auditStatus) {
    this.auditStatus = auditStatus;
  }

  public String getAuditCurrentNode() {
    return auditCurrentNode;
  }

  public void setAuditCurrentNode(String auditCurrentNode) {
    this.auditCurrentNode = auditCurrentNode;
  }

  public String getCurrentAuditDepCode() {
    return currentAuditDepCode;
  }

  public void setCurrentAuditDepCode(String currentAuditDepCode) {
    this.currentAuditDepCode = currentAuditDepCode;
  }

  public String getCurrentAuditType() {
    return currentAuditType;
  }

  public void setCurrentAuditType(String currentAuditType) {
    this.currentAuditType = currentAuditType;
  }

  public DeptEntityBo getMyDept() {
    return myDept;
  }

  public void setMyDept(DeptEntityBo myDept) {
    this.myDept = myDept;
  }

  public DeptEntityBo getCityDept() {
    return cityDept;
  }

  public void setCityDept(DeptEntityBo cityDept) {
    this.cityDept = cityDept;
  }

  public DeptEntityBo getProvinceDept() {
    return provinceDept;
  }

  public void setProvinceDept(DeptEntityBo provinceDept) {
    this.provinceDept = provinceDept;
  }

}
