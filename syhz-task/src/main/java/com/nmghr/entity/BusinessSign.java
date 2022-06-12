/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nmghr.entity;

import java.sql.Timestamp;


/**
 * <功能描述/>
 *
 * @author brook  
 * @date 2018年8月23日 下午2:20:06 
 * @version 1.0   
 */
public class BusinessSign {
  
  private String id;
  private String singUserId;
  private Timestamp signTime;
  private String businessTable;
  private String businessProperty;
  private String bussinessValue;
  private String noticeOrgId;
  private String noticeRoleId; 
  private Timestamp noticeTime;
  private String noticeUserId; 
  private String qsStatus; 
  private String parentId; 
  private String noticeLx; 
  private Timestamp updateTime; 
  private String updateUserId; 
  private String businessType;
  private String deadlineTime; 
  private String status; 
  private String revokeReason;
  public String getId() {
    return id;
  }
  public void setId(String id) {
    this.id = id;
  }
  public String getSingUserId() {
    return singUserId;
  }
  public void setSingUserId(String singUserId) {
    this.singUserId = singUserId;
  }

  public String getBusinessTable() {
    return businessTable;
  }
  public void setBusinessTable(String businessTable) {
    this.businessTable = businessTable;
  }
  public String getBusinessProperty() {
    return businessProperty;
  }
  public void setBusinessProperty(String businessProperty) {
    this.businessProperty = businessProperty;
  }
  public String getBussinessValue() {
    return bussinessValue;
  }
  public void setBussinessValue(String bussinessValue) {
    this.bussinessValue = bussinessValue;
  }
  public String getNoticeOrgId() {
    return noticeOrgId;
  }
  public void setNoticeOrgId(String noticeOrgId) {
    this.noticeOrgId = noticeOrgId;
  }
  public String getNoticeRoleId() {
    return noticeRoleId;
  }
  public void setNoticeRoleId(String noticeRoleId) {
    this.noticeRoleId = noticeRoleId;
  }

  public String getNoticeUserId() {
    return noticeUserId;
  }
  public void setNoticeUserId(String noticeUserId) {
    this.noticeUserId = noticeUserId;
  }
  public String getQsStatus() {
    return qsStatus;
  }
  public void setQsStatus(String qsStatus) {
    this.qsStatus = qsStatus;
  }
  public String getParentId() {
    return parentId;
  }
  public void setParentId(String parentId) {
    this.parentId = parentId;
  }
  public String getNoticeLx() {
    return noticeLx;
  }
  public void setNoticeLx(String noticeLx) {
    this.noticeLx = noticeLx;
  }
  public Timestamp getSignTime() {
    return signTime;
  }
  public void setSignTime(Timestamp signTime) {
    this.signTime = signTime;
  }
  public Timestamp getNoticeTime() {
    return noticeTime;
  }
  public void setNoticeTime(Timestamp noticeTime) {
    this.noticeTime = noticeTime;
  }
  public Timestamp getUpdateTime() {
    return updateTime;
  }
  public void setUpdateTime(Timestamp updateTime) {
    this.updateTime = updateTime;
  }
  public String getUpdateUserId() {
    return updateUserId;
  }
  public void setUpdateUserId(String updateUserId) {
    this.updateUserId = updateUserId;
  }
  public String getBusinessType() {
    return businessType;
  }
  public void setBusinessType(String businessType) {
    this.businessType = businessType;
  }
  public String getDeadlineTime() {
    return deadlineTime;
  }
  public void setDeadlineTime(String deadlineTime) {
    this.deadlineTime = deadlineTime;
  }
  public String getStatus() {
    return status;
  }
  public void setStatus(String status) {
    this.status = status;
  }
  public String getRevokeReason() {
    return revokeReason;
  }
  public void setRevokeReason(String revokeReason) {
    this.revokeReason = revokeReason;
  } 
  
}
