package com.nmghr.hander.query;

import com.alibaba.fastjson.JSONObject;
import com.nmghr.basic.common.Constant;
import com.nmghr.basic.common.exception.GlobalErrorEnum;
import com.nmghr.basic.common.exception.GlobalErrorException;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractQueryHandler;
import com.nmghr.common.ExamConstant;
import com.nmghr.common.QuestionType;
import com.nmghr.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
    String operator = String.valueOf(param.get("operator"));
    if ("listView".equals(operator)) {
      return getListView(param, false);//预览
    } else if ("exam".equals(operator)) {
      return getExamQuestion(param);//考试试卷
    } else if ("examAnswer".equals(operator)) {
      return examedAnswer(param);//考试答题试卷
    } else if ("detailView".equals(operator)) {
      return getDetailView(param); //人工组卷详情
    } else if ("randomDetailView".equals(operator)) {
      return getRandomDetailView(param); // 随机组卷详情
    } else if ("judgeListView".equals(operator)) {
      return getJudgeListView(param);//阅卷
    }
    return new ArrayList();
  }


  private int checkEndDate(Object endDate, int minute) {
    try {
      Calendar cal = Calendar.getInstance();
      cal.setTime(DateUtil.strToDate(String.valueOf(endDate), "yyyy-MM-dd HH:mm:ss"));
      cal.add(Calendar.MINUTE, -1 * minute);
      Calendar now = Calendar.getInstance();
      now.setTime(new Date());
      return cal.compareTo(now);
    } catch (ParseException e) {
      throw new GlobalErrorException("999996", "截止时间异常");
    }
  }

  /**
   * 在线考试
   *
   * @param param Map
   * @return Object
   * @throws Exception Exception
   */
  private Object getExamQuestion(Map<String, Object> param) throws Exception {
    Map<String, Object> result = new HashMap<>();
    //查询考试相关信息
    Map<String, Object> examInfo = getExamInfo(String.valueOf(param.get("id")));
    if (checkEndDate(examInfo.get("endDate"), Integer.parseInt(String.valueOf(examInfo.get("totalDate")))) == -1) {
      throw new GlobalErrorException("999996", "考试结束前" + examInfo.get("totalDate") + "分钟不能进行考试");
    }
    //计算截止时间！！！

    checkExam(param, examInfo);

    setExamInfo(result, examInfo);

    Map<String, Object> paramMap = new HashMap<>();
    paramMap.put("id", examInfo.get("paperId"));
    paramMap.put("paperName", examInfo.get("paperName"));
    paramMap.put("json", examInfo.get("json"));
    result.put("datas", getListView(paramMap, false));
    return result;
  }

  private void setExamInfo(Map<String, Object> result, Map<String, Object> examInfo) {
    result.put("id", examInfo.get("id"));
    result.put("totalDate", examInfo.get("totalDate"));
    result.put("examinationType", examInfo.get("examinationType"));
    result.put("paperName", examInfo.get("paperName"));
    result.put("paperId", examInfo.get("paperId"));
    result.put("startDate", examInfo.get("startDate"));
    result.put("endDate", examInfo.get("endDate"));
    result.put("examinationName", examInfo.get("examinationName"));
    result.put("permitNumber", examInfo.get("permitNumber"));
    result.put("remark", examInfo.get("remark"));
  }

  /**
   * 已考试的试卷
   *
   * @param param
   * @return
   * @throws Exception
   */
  private Object examedAnswer(Map<String, Object> param) throws Exception {
    Map<String, Object> result = new HashMap<>();
    // 查询考试相关信息
    Map<String, Object> examInfo = getExamInfo(String.valueOf(param.get("id")));
    // 查询考试记录
    param.put("permitNumber", examInfo.get("permitNumber"));
    result.putAll(getRecordByUid(param));

    // 保存考试信息
    setExamInfo(result, examInfo);

    //判断是否可以考试
    Date endDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(String.valueOf(examInfo.get("endDate")));
    Date startDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(String.valueOf(examInfo.get("startDate")));
    if (endDate.before(new Date()) || startDate.after(new Date()) || "1".equals(String.valueOf(examInfo.get("examStatus")))) {
      result.put("unable", true);
    }else {
      result.put("unable", false);
    }

    // 封装信息
    Map<String, Object> paramMap = new HashMap<>();
    paramMap.put("id", examInfo.get("paperId"));
    paramMap.put("paperName", examInfo.get("paperName"));
    paramMap.put("recordId", param.get("recordId"));
    paramMap.put("json", examInfo.get("json"));
    result.put("datas", getListView(paramMap, true));
    result.remove("paperRemark");
    return result;
  }

  /**
   * 获取用户考试记录
   *
   * @param param Map
   * @return Map Map
   * @throws Exception e
   */
  private Map<String, Object> getRecordByUid(Map<String, Object> param) throws Exception {
    Map<String, Object> result = new HashMap<>();
    Map<String, Object> params = new HashMap<>();
    params.put("userId", param.get("userId"));
    params.put("examId", param.get("id"));
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMINATIONRECORDBYUID");
    List<Map<String, Object>> records = (List<Map<String, Object>>) baseService.list(params);
    if (records != null && records.size() > 0) {
      int count = 0;
      for (Map<String, Object> record : records) {
        if (record.get("submitStatus") != null && !"0".equals(String.valueOf(record.get("submitStatus")))) {
          count++;
        }
        if (String.valueOf(param.get("recordId")).equals(String.valueOf(record.get("id")))) {
          result.put("examScore", record.get("score"));
          result.put("examArtificialScore", record.get("artificialScore"));
          result.put("examStartTime", record.get("startTime"));
          result.put("examEndTime", record.get("endTime"));
          if (record.get("startTime")!=null && record.get("endTime")!=null) {
            result.put("totalTime", DateUtil.printDifference(String.valueOf(record.get("startTime")), String.valueOf(record.get("endTime"))));
          }
        }
      }
      int enableNum = Integer.parseInt(String.valueOf(param.get("permitNumber"))) - count;
      result.put("enableNum", enableNum >= 0 ? enableNum : 0);
      result.put("unable", enableNum==0);
    }
    return result;
  }

  /**
   * 检查考试信息
   *
   * @param param
   * @param result
   * @throws Exception
   */
  private void checkExam(Map<String, Object> param, Map<String, Object> result) throws Exception {
    int permitNum = Integer.parseInt(String.valueOf(result.get("permitNumber")));
    if (result.get("endDate") == null) {
      throw new GlobalErrorException("999996", "考试截止信息异常");
    }
    if (result.get("startDate") == null) {
      throw new GlobalErrorException("999996", "考试开始信息异常");
    }
    Date endDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(String.valueOf(result.get("endDate")));
    Date startDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(String.valueOf(result.get("startDate")));
    if (endDate.before(new Date())) {
      throw new GlobalErrorException("999996", "本次考试已截止");
    }
    if (startDate.after(new Date())) {
      throw new GlobalErrorException("999996", "本次考试未开始");
    }
    //查询考试记录
    Map<String, Object> params = new HashMap<>();
    params.put("userId", param.get("userId"));
    params.put("examId", param.get("id"));
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMINATIONRECORDBYUID");
    List<Map<String, Object>> records = (List<Map<String, Object>>) baseService.list(params);
    if (records != null && records.size() > 0) {
      int count = 0;
      for (Map<String, Object> record : records) {
        if (record.get("submitStatus") != null && !"0".equals(String.valueOf(record.get("submitStatus")))) {
          count++;
        }
      }
      if (count >= permitNum) {
        throw new GlobalErrorException("999996", "本次考试次数已用完");
      }
    }
  }

  /**
   * 列表预览
   *
   * @param paper
   * @return
   * @throws Exception
   */
  private Object getDetailView(Map<String, Object> paper) throws Exception {
    //查询各个试题
    JSONObject json = (JSONObject) paper.get("json");
    JSONObject sort = json.getJSONObject("sort");
    Map<String, Object> param = new HashMap<>();
    param.put("paperId", paper.get("id"));
    for (QuestionType qt : QuestionType.getDetailValues()) {
      param.put("type", qt.name());
      param.put("types", QuestionType.getTypeArray(qt));
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMPAPERDETAIL");
//      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMPAPERINFOQUESTION");
      List<Map<String, Object>> datas = (List<Map<String, Object>>) baseService.list(param);
      if (qt.equals(QuestionType.choices)) {
        json.put("sort", sort);
        paper.putAll(settingChoices(datas, json, false, false));
      } else if (qt.equals(QuestionType.discuss)) {
        json.put("sort", sort);
        paper.putAll(settingDiscuss(datas, json, false));
      } else {
        json.put("sort", sort.get(qt.name()));
        paper.putAll(settingSort(datas, json, false));
      }
    }
    paper.remove("remark");
    paper.remove("json");
    paper.remove("operator");
    paper.remove("from");
    return paper;
  }

  /**
   * 列表预览
   *
   * @param paper
   * @return
   * @throws Exception
   */
  private Object getRandomDetailView(Map<String, Object> paper) throws Exception {
    JSONObject json = (JSONObject) paper.get("json");
    //查询各个试题
    for (String key : json.keySet()) {
      setSubjectCategory(paper, key, json.getString(key));
    }
    paper.remove("remark");
    paper.remove("json");
    paper.remove("operator");
    paper.remove("from");
    return paper;
  }

  private void setSubjectCategory(Map<String, Object> result, String category, String infos) throws Exception {
    Map<String, Object> param = new HashMap<>();
    String[] array = getRemarkArr(infos);
    Map<String, Object> bean = new HashMap<>();
    if (array != null) {
      param.put("ids", array[4]);
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMSUBJECTCATEGORYLIST");
      bean.put("data", baseService.list(param));
      bean.put("desc", array[0]);
      bean.put("sort", array[1]);
      bean.put("value", array[2]);
      bean.put("num", array[3]);
      bean.put("cateIds", array[4]);
      bean.put("type", QuestionType.valueOf(category).getType());
    }
    result.put(category, bean);
  }


  private String[] getRemarkArr(String infos) {
    if (infos == null) {
      return null;
    }
    if (!infos.contains(ExamConstant.DESCFLAG)) {
      throw new GlobalErrorException(GlobalErrorEnum.PARAM_NOT_VALID.getCode(), "试卷说明错误!");
    }
    String[] arr = infos.split(ExamConstant.DESCFLAG);
    if (arr.length != 5) {
      throw new GlobalErrorException(GlobalErrorEnum.PARAM_NOT_VALID.getCode(), "试卷说明错误!");
    }
    return arr;
  }

  /**
   * 列表预览
   *
   * @param paper
   * @return
   * @throws Exception
   */
  private Object getListView(Map<String, Object> paper, boolean answer) throws Exception {
    Map<String, Object> result = new HashMap<>();
    result.put("paperName", paper.get("paperName"));

    //查询各个试题
    String ALIAS = "EXAMPAPERINFOQUESTION";
    if (answer) {
      ALIAS = "EXAMPAPERQUESTIONANSWER";
    }
    Map<String, Object> param = new HashMap<>();
    if (answer) {
      param.put("recordId", paper.get("recordId"));
    }
    JSONObject json = (JSONObject) paper.get("json");
    JSONObject sort = json.getJSONObject("sort");
    param.put("paperId", paper.get("id"));

    for (QuestionType qt : QuestionType.getDetailValues()) {
      param.put("type", qt.name());
      param.put("types", QuestionType.getTypeArray(qt));
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS);
      List<Map<String, Object>> datas = (List<Map<String, Object>>) baseService.list(param);
      if(datas==null|| datas.size()==0){
        continue;
      }
      if (qt.equals(QuestionType.choices)) {
        json.put("sort", sort);
        result.putAll(settingChoices(datas, json, answer, true));
      } else if (qt.equals(QuestionType.discuss)) {
        json.put("sort", sort);
        result.putAll(settingDiscuss(datas, json, true));
      } else {
        json.put("sort", sort.get(qt.name()));
        result.putAll(settingSort(datas, json, true));
      }
    }
    return result;
  }

  /**
   * 列表预览
   *
   * @param paper
   * @return
   * @throws Exception
   */
  private Object getJudgeListView(Map<String, Object> paper) throws Exception {
    //查询考试相关信息
    Map<String, Object> examInfo = getExamInfo(String.valueOf(paper.get("id")));
    //查询各个试题
    String ALIAS = "EXAMPAPERQUESTIONANSWER";
    Map<String, Object> param = new HashMap<>();
    param.put("recordId", paper.get("recordId"));
    param.put("paperId", examInfo.get("paperId"));
    param.put("type", QuestionType.discuss.name());
    param.put("types", QuestionType.shortAnswer.getType() + "," + QuestionType.discuss.getType() + "," + QuestionType.caseAnalysis.getType());
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS);
    List<Map<String, Object>> discussList = (List<Map<String, Object>>) baseService.list(param);
    //查询试题及关联信息
    Map<String, Object> result = settingDiscuss(discussList, (JSONObject) examInfo.get("json"), true);
    result.put("paperId", examInfo.get("paperId"));
    result.put("paperName", examInfo.get("paperName"));
    return result;
  }

  /**
   * 获取试卷信息
   *
   * @param id
   * @return
   * @throws Exception
   */
  private Map<String, Object> getExamInfo(String id) throws Exception {
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMINATION");
    Map<String, Object> exam = (Map<String, Object>) baseService.get(id);
    //查询试题相关信息
    if (exam == null || exam.get("paperRemark") == null || exam.get("sort") == null) {
      throw new GlobalErrorException("999997", "本次考试异常");
    }
    JSONObject json = null;
    try {
      json = JSONObject.parseObject(String.valueOf(exam.get("paperRemark")));
      json.put("sort", JSONObject.parseObject(String.valueOf(exam.get("sort"))));
    } catch (Exception e) {
      throw new GlobalErrorException("999997", "试卷不完整");
    }
    exam.put("json", json);
    return exam;
  }

  /**
   * 整理选择题
   *
   * @param choices
   */
  private Map<String, Object> settingChoices(List<Map<String, Object>> choices, JSONObject json, boolean answer, boolean isSort) {
    Map<String, Object> result = new HashMap<>();
    if (choices != null && choices.size() > 0) {
      Map<String, Object> res = new HashMap<>();
      for (Map<String, Object> map : choices) {
        Map<String, Object> bean = new HashMap<>();
        bean.put("items", new HashMap<>());
        bean.putAll(map);
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
        if (key.contains(ExamConstant.DESCFLAG + QuestionType.choices.getType())) {
          list1.add((Map<String, Object>) res.get(key));
        }
        if (key.contains(ExamConstant.DESCFLAG + QuestionType.multiSelect.getType())) {
          list2.add((Map<String, Object>) res.get(key));
        }
      }
      JSONObject sort = json.getJSONObject("sort");
      json.put("sort", sort.get(QuestionType.choices.name()));
      result.putAll(settingSort(list1, json, isSort));
      json.put("sort", sort.get(QuestionType.multiSelect.name()));
      result.putAll(settingSort(list2, json, isSort));
    }
    return result;
  }

  /**
   * 整理选择题
   *
   * @param discussList
   */
  @SuppressWarnings("unchecked")
  private Map<String, Object> settingDiscuss(List<Map<String, Object>> discussList, JSONObject json, boolean isSort) {
    Map<String, Object> result = new HashMap<>();
    if (discussList != null && discussList.size() > 0) {
      Map<String, Object> res = new HashMap<>();
      for (Map<String, Object> map : discussList) {
        String key = QuestionType.byType(Integer.parseInt(String.valueOf(map.get("type")))).name();
        if (res.get(key) == null) {
          List<Map<String, Object>> list = new ArrayList<>();
          list.add(map);
          res.put(key, list);
        } else {
          List<Map<String, Object>> list = (List<Map<String, Object>>) res.get(key);
          list.add(map);
          res.put(key, list);
        }
      }
      JSONObject sort = json.getJSONObject("sort");
      for (String key : res.keySet()) {
        if (isSort) {
          json.put("sort", sort.get(key));
        }
        result.putAll(settingSort((List<Map<String, Object>>) res.get(key), json, isSort));
      }
    }
    return result;
  }

  /**
   * 根据排序定位
   *
   * @param list
   */
  private Map<String, Object> settingSort(List<Map<String, Object>> list, JSONObject json, boolean isSort) {
    Map<String, Object> result = new HashMap<>();
    if (list != null && list.size() > 0) {
      Map<String, Object> map = list.get(0);
      String str = json.getString(QuestionType.byType(Integer.parseInt(String.valueOf(map.get("type")))).name());
      if (!str.contains(ExamConstant.DESCFLAG)) {
        throw new GlobalErrorException(GlobalErrorEnum.PARAM_NOT_VALID.getCode(), "试卷说明错误!");
      }
      String[] arr = str.split(ExamConstant.DESCFLAG);
      if (arr.length < 3) {
        throw new GlobalErrorException(GlobalErrorEnum.PARAM_NOT_VALID.getCode(), "试卷说明错误!");
      }
      Map<String, Object> bean = new HashMap<>();
      bean.put("data", list);
      bean.put("desc", arr[0]);
      bean.put("sort", arr[1]);
      bean.put("value", arr[2]);
      bean.put("type", map.get("type"));
      if (isSort) {
        result.put(json.getString("sort"), bean);
      } else {
        result.put(QuestionType.byType(Integer.parseInt(String.valueOf(map.get("type")))).name(), bean);
      }
    }
    return result;
  }

}
