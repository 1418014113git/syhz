package com.nmghr.hander.dto;

import com.nmghr.common.BusinessSign;

public class BusinessSignParam {
  private BusinessSign businessSign;
  private Object deptCode;
  private Object deptName;
  private Object valueId;

  public BusinessSign getBusinessSign() {
    return businessSign;
  }

  public void setBusinessSign(BusinessSign businessSign) {
    this.businessSign = businessSign;
  }

  public Object getDeptCode() {
    return deptCode;
  }

  public void setDeptCode(Object deptCode) {
    this.deptCode = deptCode;
  }

  public Object getDeptName() {
    return deptName;
  }

  public void setDeptName(Object deptName) {
    this.deptName = deptName;
  }

  public Object getValueId() {
    return valueId;
  }

  public void setValueId(Object valueId) {
    this.valueId = valueId;
  }
}
