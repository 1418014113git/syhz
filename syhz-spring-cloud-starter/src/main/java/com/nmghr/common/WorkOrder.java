package com.nmghr.common;

import com.nmghr.basic.common.exception.GlobalErrorException;

public enum WorkOrder {
  caseSupervision("0003","案件督办", "DBAJINFO", "status"),
  caseCluster("0004", "集群战役", "CASECLUSTER", "status"),
  caseAssist("0005", "案件协查", "CASEASSIST", "status"),
  appraisal("0006", "申请检验鉴定", "AUTHENTICATE", "status"),
  specialTaskResult("0007", "专项任务成果上报", "SPECIALTASKRESULT", "status"),
  supervisionReport("0008", "督办结案报告", "DBAJREPORTAJINFO", "status"),
  noDocuments("0009", "无文书审核", "AJFLWS", "status"),
  notice("0010", "站内通知审核", "BASEMESSAGE", "messageStatus");

  private String text; //文字说明
  private String type;//wd-type
  private String table;//修改时CONTROLLER_ALIAS
  private String statusKey;//修改时sql 参数

  WorkOrder(String type, String text, String wdTable, String statusKey) {
    this.text = text;
    this.type = type;
    this.table = wdTable;
    this.statusKey = statusKey;
  }

  public String getStatusKey() {
    return statusKey;
  }

  public String getText() {
    return text;
  }

  public String getTable() {
    return table;
  }

  public String getType() {
    return type;
  }
  public static WorkOrder byType(String type){
    for(WorkOrder wo: WorkOrder.values()){
      if (wo.type.equals(type)) {
        return wo;
      }
    }
    throw new GlobalErrorException("999995", "审核类型异常");
  }
}
