package com.nmghr.controller.vo;

public class UserScoreInfo {

    private String userId;
    private Integer range;
    private String deptCode;

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    private String flag;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getRange() {
        return range;
    }

    public void setRange(Integer range) {
        this.range = range;
    }

    public String getDeptCode() {
        return deptCode;
    }

    public void setDeptCode(String deptCode) {
        this.deptCode = deptCode;
    }

    @Override
    public String toString() {
        return "UserScoreInfo{" +
                "userId='" + userId + '\'' +
                ", range=" + range +
                ", deptCode='" + deptCode + '\'' +
                '}';
    }
}
