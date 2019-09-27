package com.nmghr.hander.save;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.common.exception.GlobalErrorException;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractSaveHandler;
import com.nmghr.common.ExamConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 考试记录添加
 */
@SuppressWarnings("unchecked")
@Service("examAnswerSaveHandler")
public class ExamAnswerSaveHandler extends AbstractSaveHandler {
  private Logger log = LoggerFactory.getLogger(ExamAnswerSaveHandler.class);

  public ExamAnswerSaveHandler(IBaseService baseService) {
    super(baseService);
  }

  @Override
  @Transactional
  public Object save(Map<String, Object> requestBody) {
    if (requestBody.get("from") == null) {
      throw new GlobalErrorException("999997", "参数不正确");
    }
    try {
      String paperId = String.valueOf(requestBody.get("paperId"));
      // 查询某试题是否已存在   存在修改，不存在添加。
      int type = Integer.parseInt(String.valueOf(requestBody.get("type")));
      Map<String, Object> saveParams = new HashMap<>();
      saveParams.put("questionsId", requestBody.get("questionsId"));
      saveParams.put("examinationRecordId", requestBody.get("recordId"));
      saveParams.put("creator", requestBody.get("creator"));
      saveParams.put("deptCode", requestBody.get("deptCode"));
      saveParams.put("deptName", requestBody.get("deptName"));
      saveParams.put("answer", requestBody.get("answer"));
      setAnswer(paperId, requestBody.get("questionsId"), String.valueOf(requestBody.get("answer")), type, saveParams);
      //id存在 直接修改
      if (requestBody.get("answerId")!=null){
        saveParams.put("modifier", requestBody.get("creator"));
        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMANSWER");
        baseService.update(String.valueOf(requestBody.get("answerId")), saveParams);
        return true;
      }
      //id 不存在 判断是否已提交过
      Map<String, Object> params = new HashMap<>();
      params.put("type", type);
      params.put("recordId", requestBody.get("recordId"));
      params.put("questionsId", requestBody.get("questionsId"));
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMANSWERBYQ");
      List<Map<String, Object>> answers = (List<Map<String, Object>>) baseService.list(params);
      if (answers == null || answers.size() == 0) {
        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMANSWER");
        Map<String, Object> re = new HashMap<>();
        re.put("id", baseService.save(saveParams));
        return re;
      } else {
        Map<String, Object> answer = answers.get(0);
        saveParams.put("modifier", requestBody.get("creator"));
        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMANSWER");
        baseService.update(String.valueOf(answer.get("id")), saveParams);
        return true;
      }
    } catch (Exception e) {
      log.error("examRecordSaveHandler save Error: " + e.getMessage());
      throw new GlobalErrorException("999996", e.getMessage());
    }
  }

  /**
   * 设置答案 判断答案
   *
   * @param paperId     paperId
   * @param questionId questionsId
   * @param text        text
   * @param saveParams  saveParams
   * @throws Exception e
   */
  private void setAnswer(Object paperId, Object questionId, String text, int type,
                         Map<String, Object> saveParams) throws Exception {
    saveParams.put("type", type);
    if (type == ExamConstant.CHOICES || type == ExamConstant.MULTISELECT || type == ExamConstant.FILLGAP || type == ExamConstant.JUDGE) {
      // 客观题
      Map<String, Object> params = new HashMap<>();
      params.put("type", type);
      params.put("paperId", paperId);
      params.put("questionId", questionId);
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMPAPERANSWER");
      List<Map<String, Object>> answers = (List<Map<String, Object>>) baseService.list(params);
      if (answers == null || answers.size() == 0) {
        throw new GlobalErrorException("999001", "试题不存在！");
      }
      Map<String, Object> answer = answers.get(0);
      saveParams.put("correctAnswer", answer.get("answer"));
      saveParams.put("answerType", String.valueOf(answer.get("answer")).equals(text) ? 0 : 1);
      saveParams.put("score", String.valueOf(answer.get("answer")).equals(text) ? answer.get("score") : 0);
    } else {
      //主观题
      saveParams.put("correctAnswer", "");
      saveParams.put("answerType", -1);
      saveParams.put("score", 0);
    }

  }

}
