package com.nmghr.hander.save;

import com.alibaba.fastjson.JSONObject;
import com.nmghr.basic.common.Constant;
import com.nmghr.basic.common.exception.GlobalErrorException;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractSaveHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@SuppressWarnings("unchecked")
@Service("submitRecordSaveHandler")
public class ExamSubmitRecordSaveHandler extends AbstractSaveHandler {

  private Logger log = LoggerFactory.getLogger(ExamAnswerSaveHandler.class);

  public ExamSubmitRecordSaveHandler(IBaseService baseService) {
    super(baseService);
  }

  @Override
  @Transactional
  public Object save(Map<String, Object> requestBody) {
    if (requestBody.get("from") == null) {
      throw new GlobalErrorException("999997", "参数不正确");
    }
    try {
      // 查询试卷
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMINATION");
      Map<String, Object> exam = (Map<String, Object>) baseService.get(String.valueOf(requestBody.get("examId")));
      //查询试题相关信息
      validExamInfo(exam);

      // 查询本地记录的所有answer 计算主观题分数
      Map<String, Object> params = new HashMap<>();
      params.put("paperId", exam.get("paperId"));
      params.put("recordId", requestBody.get("recordId"));
      params.put("types", "1,2,3,4");
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMRECORDCOUNT");
      Map<String, Object> count = (Map<String, Object>) baseService.get(params);
      if (count == null) {
        count = new HashMap<>();
        count.put("score",0);
        count.put("rightNum",0);
        count.put("wrongNum",0);
      }

      String nowStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
      params = new HashMap<>();
      params.put("modifier", requestBody.get("creator"));
      params.put("userId", requestBody.get("userId"));
      params.put("endTime", nowStr);
      params.put("score", count.get("score"));
      params.put("correctNumber", count.get("rightNum"));
      params.put("incorrectNumber", count.get("wrongNum"));
      if (requestBody.get("examType")!=null && "nonexistent".equals(String.valueOf(requestBody.get("examType")))) {
        params.put("submitStatus", 3);//不需要阅卷
      } else {
        params.put("submitStatus", requestBody.get("submitStatus"));
      }
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMINATIONRECORD");
      baseService.update(String.valueOf(requestBody.get("recordId")), params);
      return true;
    } catch (Exception e) {
      log.error("submitRecordSaveHandler save Error: " + e.getMessage());
      throw new GlobalErrorException("999996", e.getMessage());
    }
  }

  private void validExamInfo(Map<String, Object> exam) throws ParseException {
    if (exam.get("endDate") == null || "".equals(String.valueOf(exam.get("endDate")).trim())) {
      throw new GlobalErrorException("999996", "考试截止信息异常");
    }
    if (exam.get("startDate") == null || "".equals(String.valueOf(exam.get("startDate")).trim())) {
      throw new GlobalErrorException("999996", "考试开始信息异常");
    }
    Date endDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(String.valueOf(exam.get("endDate")));
    //截止提交时间延后5分钟
    Calendar cal = Calendar.getInstance();
    cal.setTime(endDate);
    cal.add(Calendar.MINUTE,5);
    endDate = cal.getTime();

    Date startDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(String.valueOf(exam.get("startDate")));
    if(endDate.before(new Date())){
      throw new GlobalErrorException("999996", "本次考试已截止");
    }
    if(startDate.after(new Date())){
      throw new GlobalErrorException("999996", "本次考试未开始");
    }
  }


}
