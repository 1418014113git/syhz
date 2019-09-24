package com.nmghr.controller;

import com.nmghr.basic.common.Result;
import com.nmghr.basic.core.service.handler.IQueryHandler;
import com.nmghr.basic.core.util.SpringUtils;
import com.nmghr.basic.core.util.ValidationUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/exam")
public class ExamController {
  /**
   * 查询考试试卷
   *
   * @param id id
   * @return Object
   * @throws Exception e
   */
  @SuppressWarnings("unchecked")
  @GetMapping("/{id}")
  public Object preview(@PathVariable("id") String id) throws Exception {
    // 校验表单数据
    validId(id);
    Map<String, Object> params = new HashMap<>();
    params.put("id", id);
    params.put("from", "controller");
    params.put("operator", "exam");
    IQueryHandler queryHandler = SpringUtils.getBean("paperQuestionQueryHandler", IQueryHandler.class);
    return Result.ok(queryHandler.list(params));
  }


  /**
   * 添加考试记录
   * @param requestBody requestBody
   * @return Object
   * @throws Exception e
   */
  @SuppressWarnings("unchecked")
  @PutMapping("/start")
  public Object start(@RequestBody Map<String, Object> requestBody) throws Exception {
    // 校验表单数据
    validStartParams(requestBody);
    Map<String, Object> params = new HashMap<>();
//    params.put("id", id);
//    params.put("from", "controller");
//    params.put("operator", "exam");
    IQueryHandler queryHandler = SpringUtils.getBean("paperQuestionQueryHandler", IQueryHandler.class);
    return Result.ok(queryHandler.list(params));
  }



  /**
   * 添加答题记录
   * @param requestBody requestBody
   * @return Object
   * @throws Exception e
   */
  @SuppressWarnings("unchecked")
  @PostMapping("/saveAnswer")
  public Object saveAnswer(@RequestBody Map<String, Object> requestBody) throws Exception {
    // 校验表单数据
    validSaveAnswerParams(requestBody);
    Map<String, Object> params = new HashMap<>();
//    params.put("id", id);
//    params.put("from", "controller");
//    params.put("operator", "exam");
    IQueryHandler queryHandler = SpringUtils.getBean("paperQuestionQueryHandler", IQueryHandler.class);
    return Result.ok(queryHandler.list(params));
  }

  /**
   * 保存答题记录校验
   * @param requestBody
   */
  @SuppressWarnings("unchecked")
  private void validSaveAnswerParams(Map<String, Object> requestBody) {
    ValidationUtils.notNull(requestBody.get("examinationRecordId"), "考试记录Id不能为空!");
    ValidationUtils.notNull(requestBody.get("questionsId"), "试题Id不能为空!");
    ValidationUtils.notNull(requestBody.get("answer"), "用户答案不能为空!");
    ValidationUtils.notNull(requestBody.get("creator"), "当前用户账号不能为空!");
    ValidationUtils.notNull(requestBody.get("deptCode"), "当前部门编号不能为空!");
    ValidationUtils.notNull(requestBody.get("deptName"), "当前部门名称不能为空!");
  }

  /**
   * 开始考试校验
   * @param requestBody
   */
  @SuppressWarnings("unchecked")
  private void validStartParams(Map<String, Object> requestBody) {
    ValidationUtils.notNull(requestBody.get("userId"), "用户id不能为空!");
    ValidationUtils.notNull(requestBody.get("examinationId"), "考试id不能为空!");
    ValidationUtils.notNull(requestBody.get("creator"), "当前用户账号不能为空!");
    ValidationUtils.notNull(requestBody.get("deptCode"), "当前部门编号不能为空!");
    ValidationUtils.notNull(requestBody.get("deptName"), "当前部门名称不能为空!");
  }

  private void validId(Object id) {
    ValidationUtils.notNull(id, "id不能为空!");
    ValidationUtils.regexp(id, "^\\d+$", "非法输入");
  }
}
