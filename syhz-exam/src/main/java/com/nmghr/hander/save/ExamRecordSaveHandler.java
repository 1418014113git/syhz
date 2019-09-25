package com.nmghr.hander.save;

import com.nmghr.basic.common.Constant;
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
@Service("examRecordSaveHandler")
public class ExamRecordSaveHandler  extends AbstractSaveHandler {
  private Logger log = LoggerFactory.getLogger(ExamRecordSaveHandler.class);

  public ExamRecordSaveHandler(IBaseService baseService) {
    super(baseService);
  }

  /**
   *  #{userId}, #{examinationId}, #{startTime}, #{creator},#{deptCode}, #{deptName}
   * @param requestBody
   * @return
   */
  @SuppressWarnings("unchecked")
  @Override
  @Transactional
  public Object save(Map<String, Object> requestBody) {
    try{
      // 查询考试信息

      //查询考试记录
      Map<String, Object> params = new HashMap<>();
      params.put("userId",requestBody.get("userId"));
      params.put("examinationId",requestBody.get("examinationId"));
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMINATIONRECORDBYUID");
      List<Map<String, Object>> records = (List<Map<String, Object>>) baseService.list(params);
      if (records!=null && records.size()>0) {
        //id, userId, examinationId, startTime, endTime,`submitStatus, `createDate
        for(Map<String, Object> record : records){
          if (record.get("submitStatus")!=null && !"0".equals(String.valueOf(record.get("submitStatus")))) {
            continue;
          }
          if (record.get("endTime")!=null) {
            autoSubmit(requestBody, record);
          }
          if (isEnable(String.valueOf(record.get("startTime")) )) {
            Map<String, Object> result = new HashMap<>();
            result.put("recordId",record.get("Id"));
            // 是否查询上次答题信息
            return result;
          }
        }
        //判断完成考试的次数，判断还能否继续考试。
        return null;
      }
      Map<String, Object> result = new HashMap<>();
      String nowStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
      params.put("startTime", nowStr);
      params.put("creator", requestBody.get("creator"));
      params.put("deptCode", requestBody.get("deptCode"));
      params.put("deptName", requestBody.get("deptName"));
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMINATIONRECORD");
      Object obj = baseService.save(params);
      result.put("recordId",obj);
      result.put("startTime",nowStr);
    } catch (Exception e){
      log.error("examRecordSaveHandler save Error: "+ e.getMessage());
    }
    return null;
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


  private void autoSubmit(Map<String, Object> requestBody, Map<String, Object> record) throws Exception {
    Map<String, Object> updParams = new HashMap<>();
    updParams.put("submitStatus", 2);
    updParams.put("modifier",requestBody.get("creator"));
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMINATIONRECORD");
    baseService.update(String.valueOf(record.get("id")),updParams);
  }


}
