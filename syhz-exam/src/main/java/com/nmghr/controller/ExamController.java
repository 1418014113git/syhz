package com.nmghr.controller;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.common.Result;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.page.Paging;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.IQueryHandler;
import com.nmghr.basic.core.service.handler.ISaveHandler;
import com.nmghr.basic.core.service.handler.IUpdateHandler;
import com.nmghr.basic.core.util.SpringUtils;
import com.nmghr.basic.core.util.ValidationUtils;
import com.nmghr.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

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
   *
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
    if (obj == null) {
      return Result.fail("999998", "添加考试异常");
    }
    return Result.ok(obj);
  }


  /**
   * 添加答题记录
   *
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
    ISaveHandler saveHandler = SpringUtils.getBean("examAnswerSaveHandler", ISaveHandler.class);
    return Result.ok(saveHandler.save(requestBody));
  }

  /**
   * 提交试卷
   *
   * @param requestBody requestBody
   * @return Object
   * @throws Exception e
   */
  @SuppressWarnings("unchecked")
  @PostMapping("/submitAnswer")
  public Object submitAnswer(@RequestBody Map<String, Object> requestBody) throws Exception {
    // 校验表单数据
    ValidationUtils.notNull(requestBody.get("userId"), "userId不能为空!");
    ValidationUtils.notNull(requestBody.get("examId"), "examId不能为空!");
    ValidationUtils.notNull(requestBody.get("recordId"), "recordId不能为空!");
    ValidationUtils.notNull(requestBody.get("submitStatus"), "提交类型不能为空!");
    ValidationUtils.notNull(requestBody.get("creator"), "当前用户账号不能为空!");

    requestBody.put("from", "controller");
    ISaveHandler saveHandler = SpringUtils.getBean("submitRecordSaveHandler", ISaveHandler.class);
    return Result.ok(saveHandler.save(requestBody));
  }

  /**
   * 获取系统时间
   *
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
   *
   * @return Object
   * @throws Exception e
   */
  @SuppressWarnings("unchecked")
  @GetMapping("/examRecord")
  public Object examRecord(@RequestParam Map<String, Object> param) throws Exception {
    ValidationUtils.notNull(param.get("examId"), "考试信息Id不能为空!");
    ValidationUtils.notNull(param.get("userId"), "用户Id不能为空!");
    param.put("type","record");
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMINATIONRECORDBYUID");
    List<Map<String,Object>> list = (List<Map<String, Object>>) baseService.list(param);
    for (int i = 0; i < list.size(); i++) {
      Map<String, Object> map = list.get(i);
      if (map.get("startTime") == null || map.get("endTime") == null) {
        continue;
      }
      map.put("totalTime", DateUtil.printDifference(String.valueOf(map.get("startTime")), String.valueOf(map.get("endTime"))));
    }
    return list;
  }

  /**
   * 人工阅卷列表
   * @param param
   * @return
   * @throws Exception
   */
  @SuppressWarnings("unchecked")
  @GetMapping("/subjectiveList")
  public Object subjectiveList(@RequestParam Map<String, Object> param) throws Exception {
    int pageNum = 1, pageSize = 15;
    if (param.get("pageNum") != null && !"".equals(String.valueOf(param.get("pageNum")).trim())) {
      pageNum = Integer.parseInt(String.valueOf(param.get("pageNum")));
    }
    if (param.get("pageSize") != null && !"".equals(String.valueOf(param.get("pageSize")).trim())) {
      pageSize = Integer.parseInt(String.valueOf(param.get("pageSize")));
    }
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMRECORDLIST");
    Paging page = (Paging) baseService.page(param, pageNum, pageSize);
    if (page == null || page.getList() == null || page.getList().size() == 0) {
      return new ArrayList<>();
    }
    List<Map<String, Object>> list = page.getList();
    List<Object> result = new ArrayList<>();
    for (int i = 0; i < list.size(); i++) {
      Map<String, Object> map = list.get(i);
      if (map.get("startTime") == null || map.get("endTime") == null) {
        continue;
      }
      if ("3".equals(String.valueOf(map.get("status")))) {
        map.put("status", "end");
      } else {
        map.put("status", "start");
      }

      map.put("totalTime", DateUtil.printDifference(String.valueOf(map.get("startTime")), String.valueOf(map.get("endTime"))));
      result.add(map);
    }
    return new Paging<>(pageSize, pageNum, page.getTotalCount(), result);
  }


  /**
   * 主观题阅卷获取试题
   *
   * @return Object
   * @throws Exception e
   */
  @SuppressWarnings("unchecked")
  @GetMapping("/subjectiveQuestions")
  public Object subjectiveQuestions(@RequestParam Map<String, Object> param) throws Exception {
    // 校验表单数据
    validId(param.get("id"));
    ValidationUtils.notNull(param.get("userId"), "用户id不能为空!");
    ValidationUtils.notNull(param.get("recordId"), "考试记录id不能为空!");
    Map<String, Object> params = new HashMap<>();
    params.put("id", param.get("id"));
    params.put("from", "controller");
    params.put("userId", param.get("userId"));
    params.put("recordId", param.get("recordId"));
    params.put("operator", "judgeListView");
    IQueryHandler queryHandler = SpringUtils.getBean("paperQuestionQueryHandler", IQueryHandler.class);
    return Result.ok(queryHandler.list(params));
  }

  /**
   * 主观题阅卷
   *
   * @return Object
   * @throws Exception e
   */
  @SuppressWarnings("unchecked")
  @PostMapping("/subjectiveJudge")
  public Object subjectiveJudge(@RequestBody Map<String, Object> param) throws Exception {
    //查询考试信息  判断分数不能

    validId(param.get("recordId"));
    ValidationUtils.notNull(param.get("userId"), "用户id不能为空!");
    ValidationUtils.notNull(param.get("paperId"), "用户id不能为空!");
    ValidationUtils.notNull(param.get("creator"), "用户id不能为空!");
    Map<String, Object> params = new HashMap<>();

    List<Map<String, Object>> list = (List<Map<String, Object>>) param.get("data");
    if (list == null || list.size() == 0) {
      return Result.fail("999998", "阅卷信息不能为空");
    }

    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMPAPERGETTYPESCORE");
    params.put("paperId", param.get("paperId"));
    List<Map<String, Object>> score = (List<Map<String, Object>>) baseService.list(params);
    if (score == null || score.size() == 0) {
      return Result.fail("999998", "题型信息异常");
    }
    Map<String, Object> scoreMap = new HashMap<>();
    for (Map<String, Object> map : score) {
      scoreMap.put(String.valueOf(map.get("type")), map.get("value"));
    }

    for (Map<String, Object> map : list) {
      if (map.get("answerId") == null || "".equals(String.valueOf(map.get("answerId")).trim())) {
        return Result.fail("999998", "answerId不能为空");
      }
      if (map.get("questionsId") == null || "".equals(String.valueOf(map.get("questionsId")).trim())) {
        return Result.fail("999998", "questionsId不能为空");
      }
      if (map.get("score") == null || "".equals(String.valueOf(map.get("score")).trim())) {
        return Result.fail("999998", "score不能为空");
      }
      if (map.get("type") == null || "".equals(String.valueOf(map.get("type")).trim())) {
        return Result.fail("999998", "type不能为空");
      }
      if (Integer.parseInt(String.valueOf(scoreMap.get(String.valueOf(map.get("type"))))) < Integer.parseInt(String.valueOf(map.get("score")))) {
        return Result.fail("999998", "分数设置过高，最多" + scoreMap.get(String.valueOf(map.get("type"))) + "分");
      }
    }

    params = new HashMap<>();
    params.put("from", "controller");
    params.put("userId", param.get("userId"));
    params.put("recordId", param.get("recordId"));
    params.put("paperId", param.get("paperId"));
    params.put("creator", param.get("creator"));
    params.put("operator", "judgeListView");
    params.put("data", list);
    IUpdateHandler updateHandler = SpringUtils.getBean("examJudgeUpdateHandler", IUpdateHandler.class);
    return Result.ok(updateHandler.update(String.valueOf(param.get("recordId")), params));
  }

  /**
   * 保存答题记录校验
   *
   * @param requestBody
   */
  @SuppressWarnings("unchecked")
  private void validSaveAnswerParams(Map<String, Object> requestBody) {
    ValidationUtils.notNull(requestBody.get("examId"), "考试信息Id不能为空!");
    ValidationUtils.notNull(requestBody.get("recordId"), "考试记录Id不能为空!");
    ValidationUtils.notNull(requestBody.get("questionsId"), "试题Id不能为空!");
    ValidationUtils.notNull(requestBody.get("type"), "考题类型不能为空!");
//    ValidationUtils.notNull(requestBody.get("answer"), "用户答案不能为空!");
    ValidationUtils.notNull(requestBody.get("creator"), "当前用户账号不能为空!");
    ValidationUtils.notNull(requestBody.get("deptCode"), "当前部门编号不能为空!");
    ValidationUtils.notNull(requestBody.get("deptName"), "当前部门名称不能为空!");
  }

  /**
   * 开始考试校验
   *
   * @param requestBody
   */
  @SuppressWarnings("unchecked")
  private void validStartParams(Map<String, Object> requestBody) {
    ValidationUtils.notNull(requestBody.get("userId"), "用户id不能为空!");
    ValidationUtils.notNull(requestBody.get("userName"), "用户账号不能为空!");
    ValidationUtils.notNull(requestBody.get("realName"), "用户姓名不能为空!");
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
