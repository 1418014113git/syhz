package com.nmghr.common;

public class ExamConstant {

  /**
   * 选择题
   */
  public static int CHOICES = 1;
  public static String CHOICESNAME = "choices";
  /**
   * 选择题
   */
  public static int MULTISELECT = 2;
  public static String MULTISELECTNAME = "multiSelect";
  /**
   * 填空题
   */
  public static int FILLGAP = 3;
  public static String FILLGAPNAME = "fillGap";
  /**
   * 判断题
   */
  public static int JUDGE = 4;
  public static String JUDGENAME = "judge";
  /**
   * 简答题
   */
  public static int SHORTANSWER = 5;
  public static String SHORTANSWERNAME = "shortAnswer";
  /**
   * 论述题
   */
  public static int DISCUSS = 6;
  public static String DISCUSSNAME = "discuss";
  /**
   * 案例分析题
   */
  public static int CASEANALYSIS = 7;
  public static String CASEANALYSISNAME = "caseAnalysis";


  public static int questionNameToNum(String name) {
    if ("choices".equals(name)) {
      return CHOICES;
    }
    if ("multiSelect".equals(name)) {
      return MULTISELECT;
    }
    if ("fillgap".equals(name)) {
      return FILLGAP;
    }
    if ("judge".equals(name)) {
      return JUDGE;
    }
    if ("shortAnswer".equals(name)) {
      return SHORTANSWER;
    }
    if ("discuss".equals(name)) {
      return DISCUSS;
    }
    if ("caseAnalysis".equals(name)) {
      return CASEANALYSIS;
    }
    return 0;
  }

  public static String questionNumToName(int num) {
    if (CHOICES == num) {
      return CHOICESNAME;
    }
    if (MULTISELECT == num) {
      return MULTISELECTNAME;
    }
    if (FILLGAP == num) {
      return FILLGAPNAME;
    }
    if (JUDGE == num) {
      return JUDGENAME;
    }
    if (SHORTANSWER == num) {
      return SHORTANSWERNAME;
    }
    if (DISCUSS == num) {
      return DISCUSSNAME;
    }
    if (CASEANALYSIS == num) {
      return CASEANALYSISNAME;
    }
    return "";
  }

}
