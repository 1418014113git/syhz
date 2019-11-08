package com.nmghr.common;

import com.nmghr.basic.common.exception.GlobalErrorException;

public enum BusinessSign {
  caseVerify(2,"案件认领", ""),
  caseSupervise(4,"案件督办", "aj_supervise"),
  clueCirculation(5, "线索流转", ""),
  caseAssist(6, "案件协查", "aj_clue_assist"),
  caseCluster(7, "集群战役", "aj_cluster_assist"),
  superviseUrgent(8, "案件督办催办", ""),
  specialTask(9, "专项任务", ""),
  inspectionAppraisal(10, "检验鉴定", "aj_authenticate");

  private String text;
  private int type;
  private String table;

  BusinessSign(int type, String text, String wdTable) {
    this.text = text;
    this.type = type;
    this.table = wdTable;
  }


  public String getText() {
    return text;
  }

  public String getTable() {
    return table;
  }

  public int getType() {
    return type;
  }
  public static BusinessSign byType(int type){
    for(BusinessSign wo: BusinessSign.values()){
      if (wo.type == type) {
        return wo;
      }
    }
    throw new GlobalErrorException("999995", "审核类型异常");
  }
}
