package com.nmghr.common;

import com.nmghr.basic.common.exception.GlobalErrorException;

public enum QuestionType {
  choices(1,"选择题"),
  multiSelect(2, "多选题"),
  fillGap(3, "填空题"),
  judge(4, "判断题"),
  shortAnswer(5, "简答题"),
  discuss(6, "论述题"),
  caseAnalysis(7, "案例分析题");

  private String text;
  private int type;

  QuestionType(int type, String text) {
    this.text = text;
    this.type = type;
  }

  public String getText() {
    return text;
  }

  public int getType() {
    return type;
  }
  public static QuestionType byType(int type){
    for(QuestionType qt: QuestionType.values()){
      if (qt.type == type) {
        return qt;
      }
    }
    throw new GlobalErrorException("999995", "试题类型异常");
  }

  /**
   * 查询详情时需要的数组
   * @return
   */
  public static QuestionType[] getDetailValues() {
    return new QuestionType[]{choices,fillGap,judge,discuss};
  }
  public static String getTypeArray(QuestionType type) {
    if (type.equals(choices)) {
      return choices.getType()+","+multiSelect.getType();
    }
    if (type.equals(discuss)) {
      return shortAnswer.getType()+","+discuss.getType()+","+caseAnalysis.getType();
    }
    return String.valueOf(type.getType());
  }
}
