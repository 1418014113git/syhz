package com.nmghr.hander.save;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.common.exception.GlobalErrorException;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractSaveHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    // 查询试卷
    try {
      // 查询本地记录的所有answer 计算主观题分数
      Map<String, Object> params = new HashMap<>();
      params.put("id", String.valueOf(requestBody.get("recordId")));
      params.put("type", "kg");
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMRECORDCOUNT");
      Map<String, Object> count = (Map<String, Object>) baseService.get(params);
      if (count == null) {
        throw new GlobalErrorException("999997", "计算异常稍后重试");
      }

      String nowStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
      params = new HashMap<>();
      params.put("modifier", requestBody.get("creator"));
      params.put("userId", requestBody.get("userId"));
      params.put("endTime", nowStr);
      params.put("score", count.get("score"));
      params.put("correctNumber", count.get("rightNum"));
      params.put("incorrectNumber", count.get("wrongNum"));
      params.put("submitStatus", requestBody.get("submitStatus"));
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMINATIONRECORD");
      baseService.update(String.valueOf(requestBody.get("recordId")), params);
      return true;
    } catch (Exception e) {
      log.error("submitRecordSaveHandler save Error: " + e.getMessage());
      throw new GlobalErrorException("999996", e.getMessage());
    }
  }

}
