package com.nmghr.common;

import java.util.*;

public class ExamConstant {

  public static String EXAMINATION_ID_ISNULL = "当前修改的考试信息主键不能为空";
  public static String DATAERRORSTART = "开始日期必须小于结束日期";

  public static String sortToText(int sort) {
    if (1 == sort) {
      return "one";
    }
    if (2 == sort) {
      return "two";
    }
    if (3 == sort) {
      return "three";
    }
    if (4 == sort) {
      return "four";
    }
    if (5 == sort) {
      return "five";
    }
    if (6 == sort) {
      return "six";
    }
    if (7 == sort) {
      return "seven";
    }
    return "";
  }

  /**
   * 分隔符 标识
   */
  public static String DESCFLAG = "#";


  /**
   * list 分值 排序
   * @param list
   * @return
   */
  public static Map<String, Object> sortList(List<Map<String, Object>> list) {
    Collections.sort(list, (Comparator<Map>) (p1, p2) -> {
      Integer sort1 = (int) p1.get("sort");
      Integer sort2 = (int) p2.get("sort");
      if (sort1 > sort2) {
        return 1;
      } else if (sort1 < sort2) {
        return -1;
      } else {
        return ((Integer) p1.get("type")).compareTo((Integer) p2.get("type"));
      }
    });
    Map<String, Object> result = new HashMap<>();
    int i = 1;
    for (Map<String, Object> map : list) {
      result.put((String) map.get("name"), ExamConstant.sortToText(i));
      i++;
    }
    return result;
  }

  /**
   * 整理分数
   * @param type QuestionType
   * @param sort String
   * @return
   */
  public static Map<String, Object> setQuestionSort(QuestionType type, String sort) {
    Map<String, Object> vo = new HashMap<>();
    vo.put("name", type.name());
    vo.put("sort", Integer.parseInt(sort));
    vo.put("type", type.getType());
    return vo;
  }

}
