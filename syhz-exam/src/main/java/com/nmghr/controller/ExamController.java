package com.nmghr.controller;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.common.Result;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.IQueryHandler;
import com.nmghr.basic.core.service.handler.ISaveHandler;
import com.nmghr.basic.core.util.SpringUtils;
import com.nmghr.basic.core.util.ValidationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/exam")
public class ExamController {

  @Autowired
  @Qualifier("baseService")
  private IBaseService baseService;


  /**
   * 查询考试试卷
   *
   * @param id id
   * @return Object
   * @throws Exception e
   */
  @SuppressWarnings("unchecked")
  @GetMapping("/{id}")
  public Object exam(@PathVariable("id") String id, HttpServletRequest request) throws Exception {
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
   * 查询已考试试卷
   *
   * @param id id
   * @return Object
   * @throws Exception e
   */
  @SuppressWarnings("unchecked")
  @GetMapping("/examPaper/{id}")
  public Object examPaper(@PathVariable("id") String id, @RequestParam Map<String, Object> param) throws Exception {
    // 校验表单数据
    validId(id);
    ValidationUtils.notNull(param.get("userId"), "用户id不能为空!");
    ValidationUtils.notNull(param.get("recordId"), "考试记录id不能为空!");
    Map<String, Object> params = new HashMap<>();
    params.put("id", id);
    params.put("from", "controller");
    params.put("userId", param.get("userId"));
    params.put("recordId", param.get("recordId"));
    params.put("operator", "examAnswer");
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
    requestBody.put("from", "controller");
    ISaveHandler saveHandler = SpringUtils.getBean("examRecordSaveHandler", ISaveHandler.class);
    Object obj = saveHandler.save(requestBody);
    if(obj==null){
      return Result.fail("999998", "添加考试异常");
    }
    return Result.ok(obj);
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
    // 检查考试信息
    // 检查考试记录信息
    // 校验表单数据
    validSaveAnswerParams(requestBody);
    requestBody.put("from", "controller");
    requestBody.put("paperId", "2013");
    ISaveHandler saveHandler = SpringUtils.getBean("examAnswerSaveHandler", ISaveHandler.class);
    return Result.ok(saveHandler.save(requestBody));
  }

  /**
   * 提交试卷
   * @param requestBody requestBody
   * @return Object
   * @throws Exception e
   */
  @SuppressWarnings("unchecked")
  @PostMapping("/submitAnswer")
  public Object submitAnswer(@RequestBody Map<String, Object> requestBody) throws Exception {
    // 校验表单数据
    ValidationUtils.notNull(requestBody.get("recordId"), "考试记录id不能为空!");
    ValidationUtils.notNull(requestBody.get("submitStatus"), "提交类型不能为空!");
    ValidationUtils.notNull(requestBody.get("creator"), "当前用户账号不能为空!");

    requestBody.put("from", "controller");
    ISaveHandler saveHandler = SpringUtils.getBean("submitRecordSaveHandler", ISaveHandler.class);
    return Result.ok(saveHandler.save(requestBody));
  }

  /**
   * 提交试卷
   * @return Object
   * @throws Exception e
   */
  @SuppressWarnings("unchecked")
  @GetMapping("/systemTime")
  public Object systemTime() {
    return Result.ok(new Date().getTime());
  }
  /**
   * 考试记录
   * @return Object
   * @throws Exception e
   */
  @SuppressWarnings("unchecked")
  @GetMapping("/examRecord")
  public Object examRecord(@RequestParam Map<String, Object> param) throws Exception {
    ValidationUtils.notNull(param.get("examId"), "考试信息Id不能为空!");
    ValidationUtils.notNull(param.get("userId"), "用户Id不能为空!");
    Map<String, Object> params = new HashMap<>();
    params.put("userId", param.get("userId"));
    params.put("examId", param.get("examId"));
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMINATIONRECORDBYUID");
    return Result.ok(baseService.list(params));
  }

  /**
   * 保存答题记录校验
   * @param requestBody
   */
  @SuppressWarnings("unchecked")
  private void validSaveAnswerParams(Map<String, Object> requestBody) {
    ValidationUtils.notNull(requestBody.get("examId"), "考试信息Id不能为空!");
    ValidationUtils.notNull(requestBody.get("recordId"), "考试记录Id不能为空!");
    ValidationUtils.notNull(requestBody.get("questionsId"), "试题Id不能为空!");
    ValidationUtils.notNull(requestBody.get("type"), "考题类型不能为空!");
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
    ValidationUtils.notNull(requestBody.get("examId"), "考试id不能为空!");
    ValidationUtils.notNull(requestBody.get("creator"), "当前用户账号不能为空!");
    ValidationUtils.notNull(requestBody.get("deptCode"), "当前部门编号不能为空!");
    ValidationUtils.notNull(requestBody.get("deptName"), "当前部门名称不能为空!");
  }

  private void validId(Object id) {
    ValidationUtils.notNull(id, "id不能为空!");
    ValidationUtils.regexp(id, "^\\d+$", "非法输入");
  }
}
