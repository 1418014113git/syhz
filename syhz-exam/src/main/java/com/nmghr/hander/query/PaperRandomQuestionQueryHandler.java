package com.nmghr.hander.query;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.common.exception.GlobalErrorException;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractQueryHandler;
import com.nmghr.common.ExamConstant;
import com.nmghr.common.QuestionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 随机组卷时随机试题
 */
@SuppressWarnings("unchecked")
@Service("paperRandomQuestionQueryHandler")
public class PaperRandomQuestionQueryHandler extends AbstractQueryHandler {

  private Logger log = LoggerFactory.getLogger(PaperRandomQuestionQueryHandler.class);

  private String Q_KEY = "data";

  public PaperRandomQuestionQueryHandler(IBaseService baseService) {
    super(baseService);
  }

  /**
   * @param param
   * @return
   * @throws Exception
   */
  @Override
  public Object list(Map<String, Object> param) throws Exception {
    if (param.get("from") == null) {
      throw new GlobalErrorException("999997", "参数不正确");
    }
    if (param.get("operator") != null && "save".equals(String.valueOf(param.get("operator")))) {
      return saveRandomList(param, false);
    } else if (param.get("operator") != null && "preViewSave".equals(String.valueOf(param.get("operator")))) {
      return saveRandomList(param, true);
    }
    return previewRandomList(param);
  }

  /**
   * 预览时随机试题
   *
   * @param reqMap
   * @return
   */
  private Map<String, Object> previewRandomList(Map<String, Object> reqMap) {
    Map<String, Object> randomPaper = new HashMap<>();
    randomPaper.put("paperName", reqMap.get("paperName"));
    randomPaper.put("paperType", reqMap.get("paperType"));
    randomPaper.put("creator", reqMap.get("creator"));
    randomPaper.put("deptCode", reqMap.get("deptCode"));
    randomPaper.put("deptName", reqMap.get("deptName"));
    randomPaper.put("paperStatus", reqMap.get("paperStatus"));
    List<Map<String, Object>> sortList = new ArrayList<>();
    for (QuestionType qt : QuestionType.values()) {
      if (reqMap.get(qt.name()) != null) {
        Map<String, Object> map = (Map<String, Object>) reqMap.get(qt.name());
        map.put(Q_KEY, getPreViewRandomList(map, qt.getType()));
        sortList.add(ExamConstant.setQuestionSort(qt, String.valueOf(map.get("sort"))));
        randomPaper.put(qt.name(), map);
      }
    }
    randomPaper.put("sort", ExamConstant.sortList(sortList));
    return randomPaper;
  }

  /**
   * 添加时随机试题
   *
   * @param reqMap m
   * @param isProView isProView
   * @return Map
   */
  private Map<String, Object> saveRandomList(Map<String, Object> reqMap, boolean isProView) {
    Map<String, Object> result = new HashMap<>();
    List<Map<String, Object>> randomList = new ArrayList<>();
    Map<String, Object> remark = new HashMap<>();
    List<Map<String, Object>> sortList = new ArrayList<>();
    for (QuestionType qt : QuestionType.values()) {
      if (reqMap.get(qt.name()) != null) {
        Map<String, Object> map = (Map<String, Object>) reqMap.get(qt.name());
        getRemark(map, remark, qt.name());
        sortList.add(ExamConstant.setQuestionSort(qt, String.valueOf(map.get("sort"))));
        if (isProView) {
          randomList.addAll(getSaveParams(map, qt.getType()));
        } else {
          randomList.addAll(getRandomList(map, qt.getType()));
        }
      }
    }
    // 排序
    result.put("sort", ExamConstant.sortList(sortList));
    result.put("remark", remark);
    result.put("list", randomList);
    return result;
  }

  private void getRemark(Map<String, Object> map, Map<String, Object> remark, String key) {
    remark.put(key, String.valueOf(map.get("desc")) + ExamConstant.DESCFLAG + String.valueOf(map.get("sort"))
        + ExamConstant.DESCFLAG + String.valueOf(map.get("value"))
        + ExamConstant.DESCFLAG + String.valueOf(map.get("num"))
        + ExamConstant.DESCFLAG + String.valueOf(map.get("cateIds")));
  }

  /**
   * 拼组保存时随机的数据
   *
   * @param paramMap
   * @return
   */
  private List<Map<String, Object>> getSaveParams(Map<String, Object> paramMap, int type) {
    int sort = Integer.parseInt(String.valueOf(paramMap.get("sort")));
    int value = Integer.parseInt(String.valueOf(paramMap.get("value")));
    List<Map<String, Object>> rdList = new ArrayList<>();
    try {
      List<Map<String, Object>> list = (List<Map<String, Object>>) paramMap.get(Q_KEY);
      if (list != null && list.size() > 0) {
        for (Map<String, Object> map : list) {
          Map<String, Object> bean = new HashMap<>();
          bean.put("subjectCategoryId", map.get("subjectCategoryId"));
          bean.put("questionsId", map.get("id"));
          bean.put("type", type);
          bean.put("sort", sort);
          bean.put("value", value);
          rdList.add(bean);
        }
      }
    } catch (Exception e) {
      //log
    }
    return rdList;
  }

  /**
   * 拼组保存时随机的数据
   *
   * @param paramMap
   * @return
   */
  private List<Map<String, Object>> getRandomList(Map<String, Object> paramMap, int type) {
    String cateIds = String.valueOf(paramMap.get("cateIds"));
    int num = Integer.parseInt(String.valueOf(paramMap.get("num")));
    int sort = Integer.parseInt(String.valueOf(paramMap.get("sort")));
    int value = Integer.parseInt(String.valueOf(paramMap.get("value")));
    List<Map<String, Object>> rdList = new ArrayList<>();
    Map<String, Object> param = new HashMap<>();
    param.put("cateIds", cateIds);
    param.put("type", type);
    try {
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMPAPERQUESTION");
      List<Map<String, Object>> list = (List<Map<String, Object>>) baseService.list(param);
      if (list != null && list.size() > 0) {
        if (list.size() < num) {
          log.error("EXAMPAPERQUESTION   by   cateIds :" + cateIds + " size 等于" + list.size() + "数据不够");
          for (Map<String, Object> map : list) {
            map.put("subjectCategoryId", map.get("subCategoryId"));
            map.put("questionsId", map.get("id"));
            map.put("type", type);
            map.put("sort", sort);
            map.put("value", value);
          }
          rdList = list;
        } else {
          List<Integer> idxs = new ArrayList<>();
          Random random = new Random();
          do {
            int idx = random.nextInt(list.size());
            if (!idxs.contains(idx)) {
              Map<String, Object> question = list.get(idx);
              Map<String, Object> bean = new HashMap<>();
              bean.put("subjectCategoryId", question.get("subCategoryId"));
              bean.put("questionsId", question.get("id"));
              bean.put("type", type);
              bean.put("sort", sort);
              bean.put("value", value);
              rdList.add(bean);
              idxs.add(idx);
            }
          } while (rdList.size() < num);
        }
      }
    } catch (Exception e) {
      //log
    }
    return rdList;
  }

  /**
   * 拼组预览时随机的数据
   *
   * @param paramMap
   * @return
   */
  private List<Map<String, Object>> getPreViewRandomList(Map<String, Object> paramMap, int type) {
    String cateIds = String.valueOf(paramMap.get("cateIds"));
    int num = Integer.parseInt(String.valueOf(paramMap.get("num")));
    if(num<1){
      return new ArrayList<>();
    }
    int sort = Integer.parseInt(String.valueOf(paramMap.get("sort")));
    int value = Integer.parseInt(String.valueOf(paramMap.get("value")));
    List<Map<String, Object>> rdList = new ArrayList<>();
    Map<String, Object> param = new HashMap<>();
    param.put("cateIds", cateIds);
    param.put("type", type);
    try {
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMPAPERQUESTION");
      List<Map<String, Object>> list = (List<Map<String, Object>>) baseService.list(param);
      if (list != null && list.size() > 0) {
        if (list.size() < num) {
          log.error("EXAMPAPERQUESTION   by   cateIds :" + cateIds + " size 等于" + list.size() + "数据不够");
          rdList = list;
        } else {
          List<Integer> idxs = new ArrayList<>();
          Random random = new Random();
          do {
            int idx = random.nextInt(list.size());
            if (!idxs.contains(idx)) {
              Map<String, Object> question = list.get(idx);
              Map<String, Object> bean = new HashMap<>();
              bean.put("name", question.get("name"));
              bean.put("subjectCategoryId", question.get("subCategoryId"));
              bean.put("id", question.get("id"));
              bean.put("type", type);
              bean.put("sort", sort);
              bean.put("value", value);
              rdList.add(bean);
              idxs.add(idx);
            }
          } while (rdList.size() < num);
          if (type == 1 || type == 2) {
            rdList = settingChoices(rdList);
          }
        }
      }
    } catch (Exception e) {
      //log
    }
    return rdList;
  }

  /**
   * 选择题，多选题设置选项
   *
   * @param choices
   * @return
   */
  private List<Map<String, Object>> settingChoices(List<Map<String, Object>> choices) {
    if (choices == null || choices.size() == 0) {
      return new ArrayList<>();
    }
    Map<String, Object> result = new HashMap<>();
    StringBuilder ids = new StringBuilder();
    for (Map<String, Object> map : choices) {
      ids.append(",");
      ids.append(map.get("id"));
      result.put(String.valueOf(map.get("id")), map);
    }
    try {
      Map<String, Object> param = new HashMap<>();
      param.put("ids", ids.toString().substring(1));
      param.put("type", 99);
      LocalThreadStorage.clear();
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMPAPERQUESTION");
      List<Map<String, Object>> list = (List<Map<String, Object>>) baseService.list(param);
      if (list != null && list.size() > 0) {
        for (Map<String, Object> map : list) {
          String key = String.valueOf(map.get("id"));
          if (result.get(key) != null) {
            Map<String, Object> question = (Map<String, Object>) result.get(key);
            Map<String, Object> items = (Map<String, Object>) question.get("items");
            if (items == null) {
              items = new HashMap<>();
              items.put(String.valueOf(map.get("point")), map.get("text"));
            } else {
              items.put(String.valueOf(map.get("point")), map.get("text"));
            }
            question.put("items", items);
            result.put(key, question);
          }
        }
      }
    } catch (Exception e) {

    }
    return new ArrayList(result.values());
  }

}
