package com.nmghr.controller;

import com.alibaba.fastjson.JSONObject;
import com.nmghr.basic.common.Constant;
import com.nmghr.basic.common.Result;
import com.nmghr.basic.common.exception.GlobalErrorEnum;
import com.nmghr.basic.common.exception.GlobalErrorException;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.page.Paging;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.IQueryHandler;
import com.nmghr.basic.core.service.handler.ISaveHandler;
import com.nmghr.basic.core.service.handler.IUpdateHandler;
import com.nmghr.basic.core.util.SpringUtils;
import com.nmghr.basic.core.util.ValidationUtils;
import com.nmghr.common.QuestionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 试卷管理
 */
@RestController
@RequestMapping("/paper")
public class PaperController {

  @Autowired
  @Qualifier("baseService")
  private IBaseService baseService;

  /**
   * 随机组卷 保存
   *
   * @param requestBody body
   * @return Object obj
   * @throws Exception e
   */
  @SuppressWarnings("unchecked")
  @PutMapping("/random/save")
  public Object saveRandom(@RequestBody Map<String, Object> requestBody) throws Exception {
    //校验表单数据
    randomValidParams(requestBody);

    //根据题库查询题目试题， 根据设置 随机选择试题并返回预览，
    Map<String, Object> param = new HashMap<>();
    param.put("paperName", requestBody.get("paperName"));
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMPAPERCHECK");
    Map<String, Object> result = (Map<String, Object>) baseService.get(param);
    if (result != null && result.get("num") != null && Integer.parseInt(String.valueOf(result.get("num"))) > 0) {
      return Result.fail(GlobalErrorEnum.PARAM_NOT_VALID.getCode(), "试卷名称已存在");
    }
    requestBody.put("from", "controller");
    requestBody.put("operator", "save");
    IQueryHandler queryHandler = SpringUtils.getBean("paperRandomQuestionQueryHandler", IQueryHandler.class);
    Map<String, Object> resData = (Map<String, Object>) queryHandler.list(requestBody);
    if (resData == null) {
      return Result.fail(GlobalErrorEnum.PARAM_NOT_VALID.getCode(), "试卷随机试题异常，请检查试题是否正确");
    }
    Map<String, Object> saveParam = new HashMap<>();
    saveParam.put("paperName", requestBody.get("paperName"));
    saveParam.put("paperType", requestBody.get("paperType"));
    saveParam.put("creator", requestBody.get("creator"));
    saveParam.put("deptCode", requestBody.get("deptCode"));
    saveParam.put("deptName", requestBody.get("deptName"));
    saveParam.put("paperStatus", requestBody.get("paperStatus"));
    saveParam.put("remark", resData.get("remark"));
    saveParam.put("sort", resData.get("sort"));
    saveParam.put("questionList", resData.get("list"));
    saveParam.put("from", "controller");
    ISaveHandler saveHandler = SpringUtils.getBean("paperSaveHandler", ISaveHandler.class);
    return Result.ok(saveHandler.save(saveParam));
  }

  /**
   * 随机组卷预览
   *
   * @param requestBody body
   * @return Object obj
   * @throws Exception e
   */
  @SuppressWarnings("unchecked")
  @PutMapping("/random/preView")
  public Object randomPreView(@RequestBody Map<String, Object> requestBody) throws Exception {
    //校验表单数据
    randomValidParams(requestBody);
    //根据题库查询题目试题， 根据设置 随机选择试题并返回预览，
    requestBody.put("from", "controller");
    requestBody.put("operator", "random");
    IQueryHandler queryHandler = SpringUtils.getBean("paperRandomQuestionQueryHandler", IQueryHandler.class);
    return Result.ok(queryHandler.list(requestBody));
  }

  /**
   * 修改试卷
   *
   * @param requestBody body
   * @return Object obj
   * @throws Exception e
   */
  @SuppressWarnings("unchecked")
  @PostMapping("/random/update")
  public Object updateRandom(@RequestBody Map<String, Object> requestBody) throws Exception {
    //校验表单数据
    validId(requestBody.get("id"));
    randomValidParams(requestBody);
    Map<String, Object> param = new HashMap<>();
    param.put("paperName", requestBody.get("paperName"));
    param.put("id", requestBody.get("id"));
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMPAPERCHECK");
    Map<String, Object> result = (Map<String, Object>) baseService.get(param);
    if (result != null && result.get("num") != null && Integer.parseInt(String.valueOf(result.get("num"))) > 0) {
      return Result.fail(GlobalErrorEnum.PARAM_NOT_VALID.getCode(), "试卷名称已存在");
    }

    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMPAPER");
    Map<String, Object> bean = (Map<String, Object>) baseService.get(String.valueOf(requestBody.get("id")));
    if (bean != null && bean.get("paperStatus") != null && Integer.parseInt(String.valueOf(bean.get("paperStatus"))) == 2) {
      return Result.fail(GlobalErrorEnum.PARAM_NOT_VALID.getCode(), "试卷已发布不能修改");
    }

    //随机试题
    requestBody.put("from", "controller");
    requestBody.put("operator", "save");
    IQueryHandler queryHandler = SpringUtils.getBean("paperRandomQuestionQueryHandler", IQueryHandler.class);
    Map<String, Object> resData = (Map<String, Object>) queryHandler.list(requestBody);
    if (resData == null) {
      return Result.fail(GlobalErrorEnum.PARAM_NOT_VALID.getCode(), "试卷随机试题异常，请检查试题是否正确");
    }
    Map<String, Object> saveParam = new HashMap<>();
    saveParam.put("id", requestBody.get("id"));
    saveParam.put("paperName", requestBody.get("paperName"));
    saveParam.put("paperType", requestBody.get("paperType"));
    saveParam.put("creator", requestBody.get("creator"));
    saveParam.put("deptCode", requestBody.get("deptCode"));
    saveParam.put("deptName", requestBody.get("deptName"));
    saveParam.put("paperStatus", requestBody.get("paperStatus"));
    saveParam.put("remark", resData.get("remark"));
    saveParam.put("sort", resData.get("sort"));
    saveParam.put("questionList", resData.get("list"));
    saveParam.put("from", "controller");
    IUpdateHandler updateHandler = SpringUtils.getBean("paperUpdateHandler", IUpdateHandler.class);
    return Result.ok(updateHandler.update(String.valueOf(requestBody.get("id")), saveParam));
  }

  @SuppressWarnings("unchecked")
  @PutMapping("/random/preViewSave")
  public Object preViewSave(@RequestBody Map<String, Object> requestBody) throws Exception {
    //校验表单数据
    randomValidParams(requestBody);

    //根据题库查询题目试题， 根据设置 随机选择试题并返回预览，
    Map<String, Object> param = new HashMap<>();
    param.put("paperName", requestBody.get("paperName"));
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMPAPERCHECK");
    Map<String, Object> result = (Map<String, Object>) baseService.get(param);
    if (result != null && result.get("num") != null && Integer.parseInt(String.valueOf(result.get("num"))) > 0) {
      return Result.fail(GlobalErrorEnum.PARAM_NOT_VALID.getCode(), "试卷名称已存在");
    }
    requestBody.put("from", "controller");
    requestBody.put("operator", "preViewSave");
    IQueryHandler queryHandler = SpringUtils.getBean("paperRandomQuestionQueryHandler", IQueryHandler.class);
    Map<String, Object> resData = (Map<String, Object>) queryHandler.list(requestBody);
    if (resData == null) {
      return Result.fail(GlobalErrorEnum.PARAM_NOT_VALID.getCode(), "试卷随机试题异常，请检查试题是否正确");
    }
    Map<String, Object> saveParam = new HashMap<>();
    saveParam.put("paperName", requestBody.get("paperName"));
    saveParam.put("paperType", requestBody.get("paperType"));
    saveParam.put("creator", requestBody.get("creator"));
    saveParam.put("deptCode", requestBody.get("deptCode"));
    saveParam.put("deptName", requestBody.get("deptName"));
    saveParam.put("paperStatus", requestBody.get("paperStatus"));
    saveParam.put("remark", resData.get("remark"));
    saveParam.put("sort", resData.get("sort"));
    saveParam.put("questionList", resData.get("list"));
    saveParam.put("from", "controller");
    ISaveHandler saveHandler = SpringUtils.getBean("paperSaveHandler", ISaveHandler.class);
    return Result.ok(saveHandler.save(saveParam));
  }


  /**
   * 人工组卷
   *
   * @param requestBody body
   * @return Object obj
   * @throws Exception e
   */
  @SuppressWarnings("unchecked")
  @PutMapping("/save")
  public Object save(@RequestBody Map<String, Object> requestBody) throws Exception {
    //校验表单数据
    validParams(requestBody);
    Map<String, Object> param = new HashMap<>();
    param.put("paperName", requestBody.get("paperName"));
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMPAPERCHECK");
    Map<String, Object> result = (Map<String, Object>) baseService.get(param);
    if (result != null && result.get("num") != null && Integer.parseInt(String.valueOf(result.get("num"))) > 0) {
      return Result.fail(GlobalErrorEnum.PARAM_NOT_VALID.getCode(), "试卷名称已存在");
    }
    requestBody.put("from", "controller");
    ISaveHandler saveHandler = SpringUtils.getBean("paperSaveHandler", ISaveHandler.class);
    return Result.ok(saveHandler.save(requestBody));
  }

  /**
   * 修改试卷
   *
   * @param requestBody body
   * @return Object obj
   * @throws Exception e
   */
  @SuppressWarnings("unchecked")
  @PostMapping("/update")
  public Object update(@RequestBody Map<String, Object> requestBody) throws Exception {
    //校验表单数据
    validId(requestBody.get("id"));
    validParams(requestBody);
    Map<String, Object> param = new HashMap<>();
    param.put("paperName", requestBody.get("paperName"));
    param.put("id", requestBody.get("id"));
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMPAPERCHECK");
    Map<String, Object> result = (Map<String, Object>) baseService.get(param);
    if (result != null && result.get("num") != null && Integer.parseInt(String.valueOf(result.get("num"))) > 0) {
      return Result.fail(GlobalErrorEnum.PARAM_NOT_VALID.getCode(), "试卷名称已存在");
    }

    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMPAPER");
    Map<String, Object> bean = (Map<String, Object>) baseService.get(String.valueOf(requestBody.get("id")));
    if (bean != null && bean.get("paperStatus") != null && Integer.parseInt(String.valueOf(bean.get("paperStatus"))) == 2) {
      return Result.fail(GlobalErrorEnum.PARAM_NOT_VALID.getCode(), "试卷已发布不能修改");
    }


    requestBody.put("from", "controller");
    IUpdateHandler updateHandler = SpringUtils.getBean("paperUpdateHandler", IUpdateHandler.class);
    return Result.ok(updateHandler.update(String.valueOf(requestBody.get("id")), requestBody));
  }

  /**
   * 发布
   *
   * @param requestBody
   * @return
   * @throws Exception
   */
  @SuppressWarnings("unchecked")
  @PostMapping("/release")
  public Object release(@RequestBody Map<String, Object> requestBody) throws Exception {
    //校验表单数据
    validId(requestBody.get("id"));
    ValidationUtils.notNull(requestBody.get("modifier"), "当前用户账号不能为空!");
    Map<String, Object> param = new HashMap<>();
    param.put("id", requestBody.get("id"));
    param.put("paperStatus", 2);
    param.put("modifier", requestBody.get("modifier"));
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMPAPER");
    return baseService.update(String.valueOf(requestBody.get("id")), param);
  }

  /**
   * 删除
   *
   * @param requestBody
   * @return
   * @throws Exception
   */
  @SuppressWarnings("unchecked")
  @PostMapping("/delete")
  public Object delete(@RequestBody Map<String, Object> requestBody) throws Exception {
    //校验表单数据
    validId(requestBody.get("id"));
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMPAPER");
    Map<String, Object> result = (Map<String, Object>) baseService.get(String.valueOf(requestBody.get("id")));
    if (result != null && result.get("paperStatus") != null && "2".equals(String.valueOf(result.get("paperStatus")))) {
      return Result.fail(GlobalErrorEnum.PARAM_NOT_VALID.getCode(), "试卷已发布不能删除");
    }
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMPAPERDEL");
    return baseService.update(String.valueOf(requestBody.get("id")), new HashMap<>());
  }

  /**
   * 试卷列表
   *
   * @param param param
   * @return Object
   * @throws Exception e
   */
  @GetMapping("/list")
  public Object list(@RequestParam Map<String, Object> param) throws Exception {
    int pageNum = 1, pageSize = 10;
    if (param.get("pageNum") != null) {
      pageNum = Integer.parseInt(String.valueOf(param.get("pageNum")));
    }
    if (param.get("pageSize") != null) {
      pageSize = Integer.parseInt(String.valueOf(param.get("pageSize")));
    }
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMPAPER");
    return baseService.page(param, pageNum, pageSize);
  }

  /**
   * 详情
   *
   * @param id param
   * @return Object
   * @throws Exception e
   */
  @SuppressWarnings("unchecked")
  @GetMapping("/{id}")
  public Object detail(@PathVariable("id") String id, @RequestParam Map<String, Object> param) throws Exception {
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMPAPER");
    Map<String, Object> paper = (Map<String, Object>) baseService.get(id);
    if (paper == null) {
      return Result.fail(GlobalErrorEnum.PARAM_NOT_VALID.getCode(), "试卷不能为空");
    }
    if (paper.get("remark") == null) {
      return Result.fail(GlobalErrorEnum.PARAM_NOT_VALID.getCode(), "试卷不完整");
    }
    JSONObject json = null;
    try {
      json = JSONObject.parseObject(String.valueOf(paper.get("remark")));
    } catch (Exception e) {
      return Result.fail(GlobalErrorEnum.PARAM_NOT_VALID.getCode(), "试卷不完整");
    }
    paper.put("json", json);
    paper.put("from", "controller");
    if("auto".equals(String.valueOf(param.get("type")))){
      paper.put("operator", "randomDetailView");
    } else {
      paper.put("operator", "detailView");
    }
    IQueryHandler queryHandler = SpringUtils.getBean("paperQuestionQueryHandler", IQueryHandler.class);
    return Result.ok(queryHandler.list(paper));
  }

  /**
   * 预览
   *
   * @param id id
   * @return Object
   * @throws Exception e
   */
  @SuppressWarnings("unchecked")
  @GetMapping("/preview/{id}")
  public Object preview(@PathVariable("id") String id) throws Exception {
    // 校验表单数据
    validId(id);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMPAPER");
    Map<String, Object> paper = (Map<String, Object>) baseService.get(id);
    if (paper == null || (paper.get("paperStatus") != null && "1".equals(String.valueOf(paper.get("paperStatus"))))) {
      return Result.fail(GlobalErrorEnum.PARAM_NOT_VALID.getCode(), "试卷不能为空");
    }
    if (paper.get("remark") == null || paper.get("sort") == null) {
      return Result.fail(GlobalErrorEnum.PARAM_NOT_VALID.getCode(), "试卷不完整");
    }
    JSONObject json = null;
    try {
      json = JSONObject.parseObject(String.valueOf(paper.get("remark")));
      json.put("sort", JSONObject.parseObject(String.valueOf(paper.get("sort"))));
    } catch (Exception e) {
      return Result.fail(GlobalErrorEnum.PARAM_NOT_VALID.getCode(), "试卷不完整");
    }
    paper.put("json", json);
    paper.put("from", "controller");
    paper.put("operator", "listView");
    IQueryHandler queryHandler = SpringUtils.getBean("paperQuestionQueryHandler", IQueryHandler.class);
    return Result.ok(queryHandler.list(paper));
  }


  /**
   * 试题列表
   *
   * @param param param
   * @return Object
   * @throws Exception e
   */
  @SuppressWarnings("unchecked")
  @GetMapping("/questionList")
  public Object questionList(@RequestParam Map<String, Object> param) throws Exception {
    int pageNum = 1, pageSize = 10;
    if (param.get("pageNum") != null && !"".equals(String.valueOf(param.get("pageSize")).trim())) {
      pageNum = Integer.parseInt(String.valueOf(param.get("pageNum")));
    }
    if (param.get("pageSize") != null && !"".equals(String.valueOf(param.get("pageSize")).trim())) {
      pageSize = Integer.parseInt(String.valueOf(param.get("pageSize")));
    }
    if (param.get("type") == null || "".equals(String.valueOf(param.get("pageSize")).trim())) {
      throw new GlobalErrorException(GlobalErrorEnum.PARAM_NOT_VALID.getCode(), "试题类型不能为空!");
    }
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMPAPERQUESTION");
    Paging paging = (Paging) baseService.page(param, pageNum, pageSize);
    List<Map<String, Object>> list = paging.getList();
    if ("1".equals(String.valueOf(param.get("type"))) || "2".equals(String.valueOf(param.get("type")))) {
      list = settingChoices(list);
      return new Paging<>(pageSize, pageNum, paging.getTotalCount(), list);
    }
    return paging;
  }




  /**
   * 整理选择题
   *
   * @param choices
   */
  @SuppressWarnings("unchecked")
  private List<Map<String, Object>> settingChoices(List<Map<String, Object>> choices) {
    if (choices == null || choices.size() == 0) {
      return new ArrayList<>();
    }
    Map<String, Object> result = new HashMap<>();
    StringBuilder ids = new StringBuilder();
    for (Map<String, Object> map : choices) {
      ids.append(",");
      ids.append(map.get("id"));
      result.put(String.valueOf(map.get("id")), map);
    }
    try {
      Map<String, Object> param = new HashMap<>();
      param.put("ids", ids.toString().substring(1));
      param.put("type", 99);
      LocalThreadStorage.clear();
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMPAPERQUESTION");
      List<Map<String, Object>> list = (List<Map<String, Object>>) baseService.list(param);
      if (list != null && list.size() > 0) {
        for (Map<String, Object> map : list) {
          String key = String.valueOf(map.get("id"));
          if (result.get(key) != null) {
            Map<String, Object> question = (Map<String, Object>) result.get(key);
            Map<String, Object> items = (Map<String, Object>) question.get("items");
            if (items == null) {
              items = new HashMap<>();
              items.put(String.valueOf(map.get("point")), map.get("text"));
            } else {
              items.put(String.valueOf(map.get("point")), map.get("text"));
            }
            question.put("items", items);
            result.put(key, question);
          }
        }
      }
    } catch (Exception e) {

    }
    return new ArrayList(result.values());
  }




  /**
   * 自动组卷校验
   *
   * @param requestBody requestBody
   */
  @SuppressWarnings("unchecked")
  private void randomValidParams(Map<String, Object> requestBody) {
    ValidationUtils.notNull(requestBody.get("paperName"), "题库类型不能为空!");
    ValidationUtils.notNull(requestBody.get("paperType"), "试卷类型不能为空!");
    if (!"2".equals(String.valueOf(requestBody.get("paperType")))) {
      throw new GlobalErrorException(GlobalErrorEnum.PARAM_NOT_VALID.getCode(), "随机组卷类型不正确!");
    }
    ValidationUtils.notNull(requestBody.get("creator"), "当前用户账号不能为空!");
    ValidationUtils.notNull(requestBody.get("deptCode"), "当前部门编号不能为空!");
    ValidationUtils.notNull(requestBody.get("deptName"), "当前部门名称不能为空!");
    for (QuestionType qt : QuestionType.values()) {
      randomValidQuestions((Map<String, Object>) requestBody.get(qt.name()), qt.getType());
    }
  }

  /**
   * 随机组卷校验
   *
   * @param bean bean
   */
  @SuppressWarnings("unchecked")
  private void randomValidQuestions(Map<String, Object> bean, int type) {
    String text = QuestionType.byType(type).name();
    if (bean != null) {
      if (bean.get("sort") == null) {
        throw new GlobalErrorException(GlobalErrorEnum.PARAM_NOT_VALID.getCode(), text + "大题排序不能为空!");
      }
      if (bean.get("value") == null) {
        throw new GlobalErrorException(GlobalErrorEnum.PARAM_NOT_VALID.getCode(), text + "试题分值不能为空!");
      }
      if (bean.get("desc") == null) {
        throw new GlobalErrorException(GlobalErrorEnum.PARAM_NOT_VALID.getCode(), text + "大题说明不能为空!");
      }
      if (bean.get("num") == null) {
        throw new GlobalErrorException(GlobalErrorEnum.PARAM_NOT_VALID.getCode(), text + "试题数量不能为空!");
      }
      if (bean.get("cateIds") == null) {
        throw new GlobalErrorException(GlobalErrorEnum.PARAM_NOT_VALID.getCode(), text + "试题题库类型不能为空!");
      }
    }
  }

  /**
   * 人工组卷校验
   *
   * @param requestBody requestBody
   */
  @SuppressWarnings("unchecked")
  private void validParams(Map<String, Object> requestBody) {
    ValidationUtils.notNull(requestBody.get("paperName"), "题库类型不能为空!");
    ValidationUtils.notNull(requestBody.get("paperType"), "试卷类型不能为空!");
    ValidationUtils.notNull(requestBody.get("creator"), "当前用户账号不能为空!");
    ValidationUtils.notNull(requestBody.get("deptCode"), "当前部门编号不能为空!");
    ValidationUtils.notNull(requestBody.get("deptName"), "当前部门名称不能为空!");
    for (QuestionType qt : QuestionType.values()) {
      validQuestions((Map<String, Object>) requestBody.get(qt.name()), qt.getType());
    }
  }

  /**
   * 人工组卷校验
   *
   * @param bean bean
   */
  @SuppressWarnings("unchecked")
  private void validQuestions(Map<String, Object> bean, int type) {
    String text = QuestionType.byType(type).name();
    if (bean != null) {
      if (bean.get("sort") == null) {
        throw new GlobalErrorException(GlobalErrorEnum.PARAM_NOT_VALID.getCode(), text + "试题排序不能为空!");
      }
      if (bean.get("value") == null) {
        throw new GlobalErrorException(GlobalErrorEnum.PARAM_NOT_VALID.getCode(), text + "试题分值不能为空!");
      }
      if (bean.get("desc") == null) {
        throw new GlobalErrorException(GlobalErrorEnum.PARAM_NOT_VALID.getCode(), text + "试题说明不能为空!");
      }
      if (bean.get("data") == null) {
        throw new GlobalErrorException(GlobalErrorEnum.PARAM_NOT_VALID.getCode(), text + "试题数据不能为空!");
      }
      List<Map<String, Object>> datas = (List<Map<String, Object>>) bean.get("data");
      if (datas == null || datas.size() == 0) {
        throw new GlobalErrorException(GlobalErrorEnum.PARAM_NOT_VALID.getCode(), text + "试题数据不能为空!");
      }
      for (Map<String, Object> q : datas) {
        if (q.get("subjectCategoryId") == null) {
          throw new GlobalErrorException(GlobalErrorEnum.PARAM_NOT_VALID.getCode(), text + "试题题库Id不能为空!");
        }
        if (q.get("questionsId") == null) {
          throw new GlobalErrorException(GlobalErrorEnum.PARAM_NOT_VALID.getCode(), text + "试题Id不能为空!");
        }
      }
    }
  }

  private void validId(Object id) {
    ValidationUtils.notNull(id, "id不能为空!");
    ValidationUtils.regexp(id, "^\\d+$", "非法输入");
  }

}
