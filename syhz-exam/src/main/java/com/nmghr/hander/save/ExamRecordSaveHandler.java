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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 考试记录添加
 */
@SuppressWarnings("unchecked")
@Service("examRecordSaveHandler")
public class ExamRecordSaveHandler extends AbstractSaveHandler {
  private Logger log = LoggerFactory.getLogger(ExamRecordSaveHandler.class);

  public ExamRecordSaveHandler(IBaseService baseService) {
    super(baseService);
  }

  /**
   * #{userId}, #{examinationId}, #{startTime}, #{creator},#{deptCode}, #{deptName}
   *
   * @param requestBody
   * @return
   */
  @Override
  @Transactional
  public Object save(Map<String, Object> requestBody) {
    if (requestBody.get("from") == null) {
      throw new GlobalErrorException("999997", "参数不正确");
    }
    try {
      // 查询考试信息
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMINATION");
      Map<String, Object> map = (Map<String, Object>) baseService.get(String.valueOf(requestBody.get("examId")));
      if (map == null) {
        throw new GlobalErrorException("999996", "考试信息不存在");
      }
      int permitNum = Integer.parseInt(String.valueOf(map.get("permitNumber")));
      if (map.get("endDate") == null) {
        throw new GlobalErrorException("999996", "考试截止信息异常");
      }
      SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      Date endDate = format.parse(String.valueOf(map.get("endDate")));
      Date startDate = format.parse(String.valueOf(map.get("startDate")));
      if (endDate.before(new Date())) {
        throw new GlobalErrorException("999996", "本次考试已截止");
      }
      if (startDate.after(new Date())) {
        throw new GlobalErrorException("999996", "本次考试未开始");
      }
      //查询考试记录
      checkRecord(requestBody, permitNum);
      Map<String, Object> result = new HashMap<>();
      String nowStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
      Map<String, Object> params = new HashMap<>();
      params.put("userId", requestBody.get("userId"));
      params.put("userName", requestBody.get("userName"));
      params.put("realName", requestBody.get("realName"));
      params.put("examinationId", requestBody.get("examId"));
      params.put("startTime", nowStr);
      params.put("creator", requestBody.get("creator"));
      params.put("deptCode", requestBody.get("deptCode"));
      params.put("deptName", requestBody.get("deptName"));
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMINATIONRECORD");
      Object obj = baseService.save(params);
      result.put("recordId", obj);
      result.put("startTime", nowStr);
      return result;
    } catch (Exception e) {
      log.error("examRecordSaveHandler save Error: " + e.getMessage());
      throw new GlobalErrorException("999996", e.getMessage());
    }
  }

  private void checkRecord(Map<String, Object> requestBody, int permitNum) throws Exception {
    Map<String, Object> params = new HashMap<>();
    params.put("userId", requestBody.get("userId"));
    params.put("examId", requestBody.get("examId"));
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMINATIONRECORDBYUID");
    List<Map<String, Object>> records = (List<Map<String, Object>>) baseService.list(params);
    if (records != null && records.size() > 0) {
      //id, userId, examinationId, startTime, endTime,`submitStatus, `createDate
      int count = 0;
      for (Map<String, Object> record : records) {
        if (record.get("submitStatus") != null && !"0".equals(String.valueOf(record.get("submitStatus")))) {
          count++;
          continue;
        }
        if (record.get("endTime") != null) {
          autoSubmit(requestBody, String.valueOf(record.get("id")));// 已截止但状态 为0  ，自动提交
          continue;
        }
        //异常考试信息 删除操作
        if (isEnable(String.valueOf(record.get("startTime")))) {
          delErrorRecord(requestBody.get("examId"), requestBody.get("userId"), record.get("id"));
        }
      }
      if (count >= permitNum) {
        throw new GlobalErrorException("999996", "本次考试次数已用完");
      }
      //判断完成考试的次数，判断还能否继续考试。
    }
  }

  private void delErrorRecord(Object examId, Object userId, Object recordId) throws Exception {
    Map<String, Object> delParam = new HashMap<>();
    delParam.put("recordId", recordId);
    delParam.put("examId", examId);
    delParam.put("userId", userId);
    // 删除未完成的记录
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMINATIONRECORD");
    baseService.remove(delParam);
  }

  private boolean isEnable(String startTime) {
    if (startTime == null || "".equals(startTime)) {
      return false;
    }
    try {
      Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(startTime);
      Calendar cal = Calendar.getInstance();
      cal.setTime(new Date());
      cal.add(Calendar.HOUR_OF_DAY, -1);
      Date before = cal.getTime();

      return date.after(before);

    } catch (ParseException e) {
      e.printStackTrace();
    }
    return false;
  }


  /**
   * 修改为已提交
   *
   * @param requestBody
   * @param id id
   * @throws Exception
   */
  private void autoSubmit(Map<String, Object> requestBody, String id) throws Exception {
    Map<String, Object> updParams = new HashMap<>();
    updParams.put("submitStatus", 2);
    updParams.put("modifier", requestBody.get("creator"));
    updParams.put("userId", requestBody.get("userId"));
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMINATIONRECORD");
    baseService.update(id, updParams);
  }


}
