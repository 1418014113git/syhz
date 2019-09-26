package com.nmghr.hander.update;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.common.exception.GlobalErrorException;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractUpdateHandler;
import com.nmghr.common.ExamConstant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
@Service("examJudgeUpdateHandler")
public class ExamJudgeUpdateHandler extends AbstractUpdateHandler {

  public ExamJudgeUpdateHandler(IBaseService baseService) {
    super(baseService);
  }

  @Transactional
  @Override
  public Object update(String id, Map<String, Object> requestBody) throws Exception {
    //查询考试信息  判断分数不能
    //修改answer得分
    int score = 0;
    List<Map<String, Object>> list = (List<Map<String, Object>>) requestBody.get("data");
    for(Map<String, Object> map : list) {
      Map<String, Object> params = new HashMap<>();
      params.put("id",map.get("answerId"));
      params.put("examinationRecordId",id);
      params.put("questionsId",map.get("questionsId"));
      params.put("score",map.get("score"));
      params.put("answerType",2);
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMANSWER");
      baseService.update(String.valueOf(map.get("answerId")), params);
      int value = Integer.parseInt(String.valueOf(map.get("score")));
      score += value ;
    }

    Map<String, Object> params = new HashMap<>();
    params.put("recordId", id);
    params.put("paperId", requestBody.get("paperId"));
    params.put("type", "zg");
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMRECORDCOUNT");
    Map<String, Object> count = (Map<String, Object>) baseService.get(params);
    if (count == null) {
      throw new GlobalErrorException("999997", "计算异常稍后重试");
    }

    //修改考试记录总分等信息
    params = new HashMap<>();
    params.put("correctNumber", count.get("rightNum"));
    params.put("incorrectNumber", count.get("wrongNum"));
    params.put("submitStatus",3);
    params.put("modifier",requestBody.get("creator"));
    params.put("artificialScore",score);
    params.put("userId",requestBody.get("userId"));
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMINATIONRECORD");
    return baseService.update(id, params);
  }
}
