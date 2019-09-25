package com.nmghr.hander.query;

import com.alibaba.fastjson.JSONObject;
import com.nmghr.basic.common.Constant;
import com.nmghr.basic.common.Result;
import com.nmghr.basic.common.exception.GlobalErrorEnum;
import com.nmghr.basic.common.exception.GlobalErrorException;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractQueryHandler;
import com.nmghr.common.ExamConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@SuppressWarnings("unchecked")
@Service("paperQuestionQueryHandler")
public class PaperQuestionQueryHandler extends AbstractQueryHandler {
  private Logger log = LoggerFactory.getLogger(PaperQuestionQueryHandler.class);

  public PaperQuestionQueryHandler(IBaseService baseService) {
    super(baseService);
  }

  /**
   * @param param paper
   * @return Object o
   * @throws Exception e
   */
  @Override
  public Object list(Map<String, Object> param) throws Exception {
    if (param.get("from") == null) {
      throw new GlobalErrorException("999997", "参数不正确");
    }
    if (param.get("operator") != null && "listView".equals(String.valueOf(param.get("operator")))) {
      return getListView(param);
    } else if(param.get("operator") != null && "exam".equals(String.valueOf(param.get("operator")))){
      return getExamQuestion(param);
    }
    return new ArrayList();
  }

  /**
   * 在线考试
   * @param param
   * @return
   * @throws Exception
   */
  private Object getExamQuestion(Map<String, Object> param) throws Exception {
    //查询考试相关信息
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMINATION");
    Map<String, Object> result = (Map<String, Object>) baseService.get(String.valueOf(param.get("id")));
    //查询试题相关信息
    if (result.get("paperRemark") == null) {
      throw new GlobalErrorException("999997", "本次考试异常");
    }
    JSONObject json = null;
    try {
      json = JSONObject.parseObject(String.valueOf(result.get("paperRemark")));
    } catch (Exception e) {
      throw new GlobalErrorException("999997", "试卷不完整");
    }
    if (json == null) {
      throw new GlobalErrorException("999997", "试卷不完整");
    }
    Map<String, Object> paramMap = new HashMap<>();
    paramMap.put("id",result.get("paperId"));
    paramMap.put("paperName",result.get("paperName"));
    paramMap.put("json", json);
    result.put("datas",getListView(paramMap));
    return result;
  }

  /**
   * 列表预览
   * @param paper
   * @return
   * @throws Exception
   */
  private Object getListView(Map<String, Object> paper) throws Exception {
    Map<String, Object> result = new HashMap<>();
    result.put("paperName", paper.get("paperName"));

    //查询各个试题
    String ALIAS = "EXAMPAPERINFOQUESTION";
    Map<String, Object> param = new HashMap<>();
    param.put("paperId", paper.get("id"));
    param.put("type", ExamConstant.CHOICESNAME);
    param.put("types", ExamConstant.CHOICES + "," + ExamConstant.MULTISELECT);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS);
    List<Map<String, Object>> choices = (List<Map<String, Object>>) baseService.list(param);

    param.put("type", ExamConstant.FILLGAPNAME);
    param.put("types", ExamConstant.FILLGAP);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS);
    List<Map<String, Object>> fillGaps = (List<Map<String, Object>>) baseService.list(param);

    param.put("type", ExamConstant.JUDGENAME);
    param.put("types", ExamConstant.JUDGE);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS);
    List<Map<String, Object>> judges = (List<Map<String, Object>>) baseService.list(param);

    param.put("type", ExamConstant.DISCUSSNAME);
    param.put("types", ExamConstant.SHORTANSWER + "," + ExamConstant.DISCUSS + "," + ExamConstant.CASEANALYSIS);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS);
    List<Map<String, Object>> discussList = (List<Map<String, Object>>) baseService.list(param);

    JSONObject json = (JSONObject) paper.get("json");

    //查询试题及关联信息
    settingChoices(result, choices, json);
    settingSort(result, fillGaps, json);
    settingSort(result, judges, json);
    settingDiscuss(result, discussList, json);
    return result;
  }


  /**
   * 整理选择题
   *
   * @param result
   * @param choices
   */
  @SuppressWarnings("unchecked")
  private void settingChoices(Map<String, Object> result, List<Map<String, Object>> choices, JSONObject json) {
    if (choices != null && choices.size() > 0) {
      Map<String, Object> res = new HashMap<>();
      for (Map<String, Object> map : choices) {
        Map<String, Object> bean = new HashMap<>();
        bean.put("items",new HashMap<>());
        bean.put("id", map.get("id"));
        bean.put("type", map.get("type"));
        bean.put("name", map.get("name"));
        res.put(String.valueOf(map.get("id")) + ExamConstant.DESCFLAG + map.get("type"), bean);
      }
      for (Map<String, Object> map : choices) {
        String key = String.valueOf(map.get("id")) + ExamConstant.DESCFLAG + map.get("type");
        if (res.get(key) != null) {
          Map<String, Object> question = (Map<String, Object>) res.get(key);
          Map<String, Object> items = (Map<String, Object>) question.get("items");
          items.put(String.valueOf(map.get("point")), map.get("value"));
          question.put("items", items);
          res.put(key, question);
        }
      }
      List<Map<String, Object>> list1 = new ArrayList<>();
      List<Map<String, Object>> list2 = new ArrayList<>();
      for (String key : res.keySet()) {
        if (key.contains(ExamConstant.DESCFLAG + ExamConstant.CHOICES)) {
          list1.add((Map<String, Object>) res.get(key));
        }
        if (key.contains(ExamConstant.DESCFLAG + ExamConstant.MULTISELECT)) {
          list2.add((Map<String, Object>) res.get(key));
        }
      }
      settingSort(result, list1, json);
      settingSort(result, list2, json);
    }
  }

  /**
   * 整理选择题
   *
   * @param result
   * @param discussList
   */
  @SuppressWarnings("unchecked")
  private void settingDiscuss(Map<String, Object> result, List<Map<String, Object>> discussList, JSONObject json) {
    if (discussList != null && discussList.size() > 0) {
      Map<String, Object> res = new HashMap<>();
      for (Map<String, Object> map : discussList) {
        res.put(String.valueOf(map.get("id")) + ExamConstant.DESCFLAG + map.get("type"), map);
      }
      List<Map<String, Object>> list1 = new ArrayList<>();
      List<Map<String, Object>> list2 = new ArrayList<>();
      List<Map<String, Object>> list3 = new ArrayList<>();
      for (String key : res.keySet()) {
        if (key.contains(ExamConstant.DESCFLAG + ExamConstant.SHORTANSWER)) {
          list1.add((Map<String, Object>) res.get(key));
        }
        if (key.contains(ExamConstant.DESCFLAG + ExamConstant.DISCUSS)) {
          list2.add((Map<String, Object>) res.get(key));
        }
        if (key.contains(ExamConstant.DESCFLAG + ExamConstant.CASEANALYSIS)) {
          list3.add((Map<String, Object>) res.get(key));
        }
      }
      settingSort(result, list1, json);
      settingSort(result, list2, json);
      settingSort(result, list3, json);
    }
  }

  /**
   * 根据排序定位
   *
   * @param result
   * @param list
   */
  private void settingSort(Map<String, Object> result, List<Map<String, Object>> list, JSONObject json) {
    if (list != null && list.size() > 0) {
      Map<String, Object> map = list.get(0);
      String str = json.getString(ExamConstant.questionNumToName(Integer.parseInt(String.valueOf(map.get("type")))));
      if (!str.contains(ExamConstant.DESCFLAG)) {
        throw new GlobalErrorException(GlobalErrorEnum.PARAM_NOT_VALID.getCode(), "试卷说明错误!");
      }
      String[] arr = str.split(ExamConstant.DESCFLAG);
      if (arr.length != 3) {
        throw new GlobalErrorException(GlobalErrorEnum.PARAM_NOT_VALID.getCode(), "试卷说明错误!");
      }
      Map<String, Object> bean = new HashMap<>();
      bean.put("data", list);
      bean.put("desc", arr[0]);
      result.put(ExamConstant.sortToText(Integer.parseInt(arr[1])), bean);
    }
  }

}
