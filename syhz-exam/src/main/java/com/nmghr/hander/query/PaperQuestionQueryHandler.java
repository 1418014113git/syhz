package com.nmghr.hander.query;

import com.alibaba.fastjson.JSONObject;
import com.nmghr.basic.common.Constant;
import com.nmghr.basic.common.exception.GlobalErrorEnum;
import com.nmghr.basic.common.exception.GlobalErrorException;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractQueryHandler;
import com.nmghr.common.ExamConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    String operator = String.valueOf(param.get("operator"));
    if ("listView".equals(operator)) {
      return getListView(param, false);
    } else if("exam".equals(operator)){
      return getExamQuestion(param);
    } else if ("examAnswer".equals(operator)){
      return examedAnswer(param);
    } else if ("detailView".equals(operator)){
      return getDetailView(param);
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

//    checkExam(param, result);


    Map<String, Object> paramMap = new HashMap<>();
    paramMap.put("id",result.get("paperId"));
    paramMap.put("paperName",result.get("paperName"));
    paramMap.put("json", json);
    result.put("datas",getListView(paramMap, false));
    return result;
  }
  /**
   * 已考试的试卷
   * @param param
   * @return
   * @throws Exception
   */
  private Object examedAnswer(Map<String, Object> param) throws Exception {
    //查询考试相关信息
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMINATION");
    Map<String, Object> result = (Map<String, Object>) baseService.get(String.valueOf(param.get("id")));
    //查询试题相关信息
    if (result== null || result.get("paperRemark") == null) {
      throw new GlobalErrorException("999997", "考试信息不存在");
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

    //查询考试记录
    Map<String, Object> params = new HashMap<>();
    params.put("userId",param.get("userId"));
    params.put("examId",param.get("id"));
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMINATIONRECORDBYUID");
    List<Map<String, Object>> records = (List<Map<String, Object>>) baseService.list(params);
    if (records!=null && records.size()>0) {
      int count = 0;
      for(Map<String, Object> record : records){
        if (record.get("submitStatus")!=null && !"0".equals(String.valueOf(record.get("submitStatus")))) {
          count ++ ;
        }
        if(String.valueOf(param.get("recordId")).equals(String.valueOf(record.get("id")))){
          result.put("examScore", record.get("score"));
          result.put("examArtificialScore", record.get("artificialScore"));
          result.put("examStartTime", record.get("startTime"));
          result.put("examEndTime", record.get("endTime"));
        }
      }
      int enableNum = Integer.parseInt(String.valueOf(result.get("permitNumber")))- count;
      result.put("enableNum", enableNum>=0?enableNum: 0);
    }

    Map<String, Object> paramMap = new HashMap<>();
    paramMap.put("id",result.get("paperId"));
    paramMap.put("paperName",result.get("paperName"));
    paramMap.put("recordId",param.get("recordId"));
    paramMap.put("json", json);
    result.put("datas",getListView(paramMap, true));
    result.remove("paperRemark");
    return result;
  }

//  /**
//   * 检查考试信息
//   * @param param
//   * @param result
//   * @throws Exception
//   */
//  private void checkExam(Map<String, Object> param, Map<String, Object> result) throws Exception {
//    int permitNum = Integer.parseInt(String.valueOf(result.get("permitNumber")));
//    if (result.get("endDate") == null) {
//      throw new GlobalErrorException("999996", "考试截止信息异常");
//    }
//    if (result.get("startDate") == null) {
//      throw new GlobalErrorException("999996", "考试开始信息异常");
//    }
//    Date endDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(String.valueOf(result.get("endDate")));
//    Date startDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(String.valueOf(result.get("startDate")));
//    if(endDate.before(new Date())){
//      throw new GlobalErrorException("999996", "本次考试已截止");
//    }
//    if(startDate.after(new Date())){
//      throw new GlobalErrorException("999996", "本次考试未开始");
//    }
//    //查询考试记录
//    Map<String, Object> params = new HashMap<>();
//    params.put("userId",param.get("userId"));
//    params.put("examinationId",param.get("id"));
//    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMINATIONRECORDBYUID");
//    List<Map<String, Object>> records = (List<Map<String, Object>>) baseService.list(params);
//    if (records!=null && records.size()>0) {
//      int count = 0;
//      for(Map<String, Object> record : records){
//        if (record.get("submitStatus")!=null && !"0".equals(String.valueOf(record.get("submitStatus")))) {
//          count ++ ;
//        }
//      }
//      if(count>=permitNum){
//        throw new GlobalErrorException("999996", "本次考试次数已用完");
//      }
//    }
//  }

  /**
   * 列表预览
   * @param paper
   * @return
   * @throws Exception
   */
  private Object getDetailView(Map<String, Object> paper) throws Exception {
    Map<String, Object> result = new HashMap<>();
    result.put("paperName", paper.get("paperName"));

    //查询各个试题
    String ALIAS = "EXAMPAPERDETAIL";
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
    settingDiscuss(result, choices, json, false);
    settingSort(result, fillGaps, json, false);
    settingSort(result, judges, json, false);
    settingDiscuss(result, discussList, json, false);
    return result;
  }

  /**
   * 列表预览
   * @param paper
   * @return
   * @throws Exception
   */
  private Object getListView(Map<String, Object> paper, boolean answer) throws Exception {
    Map<String, Object> result = new HashMap<>();
    result.put("paperName", paper.get("paperName"));

    //查询各个试题
    String ALIAS = "EXAMPAPERINFOQUESTION";
    if(answer){
      ALIAS = "EXAMPAPERQUESTIONANSWER";
    }
    Map<String, Object> param = new HashMap<>();
    if(answer){
      param.put("recordId", paper.get("recordId"));
    }
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
    settingChoices(result, choices, json, answer);
    settingSort(result, fillGaps, json, true);
    settingSort(result, judges, json, true);
    settingDiscuss(result, discussList, json, true);
    return result;
  }


  /**
   * 整理选择题
   *
   * @param result
   * @param choices
   */
  private void settingChoices(Map<String, Object> result, List<Map<String, Object>> choices, JSONObject json, boolean answer) {
    if (choices != null && choices.size() > 0) {
      Map<String, Object> res = new HashMap<>();
      for (Map<String, Object> map : choices) {
        Map<String, Object> bean = new HashMap<>();
        bean.put("items",new HashMap<>());
        bean.put("id", map.get("id"));
        bean.put("type", map.get("type"));
        bean.put("name", map.get("name"));
        if(answer){
          bean.put("rightAnswer", map.get("rightAnswer"));
          bean.put("answer", map.get("answer"));
        }
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
      settingSort(result, list1, json, true);
      settingSort(result, list2, json, true);
    }
  }

  /**
   * 整理选择题
   *
   * @param result
   * @param discussList
   */
  @SuppressWarnings("unchecked")
  private void settingDiscuss(Map<String, Object> result, List<Map<String, Object>> discussList, JSONObject json, boolean isSort) {
    if (discussList != null && discussList.size() > 0) {
      Map<String, Object> res = new HashMap<>();
      for (Map<String, Object> map : discussList) {
        String key = String.valueOf(map.get("type"));
        if(res.get(key)==null){
          List<Map<String, Object>> list = new ArrayList<>();
          list.add(map);
          res.put(key, list);
        } else {
          List<Map<String, Object>> list = (List<Map<String, Object>>) res.get(key);
          list.add(map);
          res.put(key, list);
        }
      }
      for (String key : res.keySet()) {
        settingSort(result, (List<Map<String, Object>>) res.get(key), json, isSort);
      }

//      Map<String, Object> res = new HashMap<>();
//      for (Map<String, Object> map : discussList) {
//        res.put(String.valueOf(map.get("id")) + ExamConstant.DESCFLAG + map.get("type"), map);
//      }
//      List<Map<String, Object>> list1 = new ArrayList<>();
//      List<Map<String, Object>> list2 = new ArrayList<>();
//      List<Map<String, Object>> list3 = new ArrayList<>();
//      for (String key : res.keySet()) {
//        if (key.contains(ExamConstant.DESCFLAG + ExamConstant.SHORTANSWER)) {
//          list1.add((Map<String, Object>) res.get(key));
//        }
//        if (key.contains(ExamConstant.DESCFLAG + ExamConstant.DISCUSS)) {
//          list2.add((Map<String, Object>) res.get(key));
//        }
//        if (key.contains(ExamConstant.DESCFLAG + ExamConstant.CASEANALYSIS)) {
//          list3.add((Map<String, Object>) res.get(key));
//        }
//      }
//      settingSort(result, list1, json);
//      settingSort(result, list2, json);
//      settingSort(result, list3, json);
    }
  }

  /**
   * 根据排序定位
   *
   * @param result
   * @param list
   */
  private void settingSort(Map<String, Object> result, List<Map<String, Object>> list, JSONObject json, boolean isSort) {
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
      bean.put("sort", arr[1]);
      bean.put("value", arr[2]);
      bean.put("type", map.get("type"));
      if(isSort){
        result.put(ExamConstant.sortToText(Integer.parseInt(arr[1])), bean);
      } else {
        result.put(ExamConstant.questionNumToName(Integer.parseInt(String.valueOf(map.get("type")))), bean);
      }
    }
  }

}
