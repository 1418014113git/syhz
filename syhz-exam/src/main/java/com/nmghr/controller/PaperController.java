package com.nmghr.controller;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.common.Result;
import com.nmghr.basic.common.exception.GlobalErrorEnum;
import com.nmghr.basic.common.exception.GlobalErrorException;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.ISaveHandler;
import com.nmghr.basic.core.service.handler.IUpdateHandler;
import com.nmghr.basic.core.util.SpringUtils;
import com.nmghr.basic.core.util.ValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
   * @param requestBody body
   * @return Object obj
   * @throws Exception e
   */
  @SuppressWarnings("unchecked")
  @PutMapping("save")
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
  @PostMapping("update")
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
   * 列表
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
  @GetMapping("/{id}")
  public Object detail(@PathVariable("id") String id) throws Exception {
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMPAPER");
    Map<String, Object> paper = (Map<String, Object>) baseService.get(id);
    if (paper == null) {
      return Result.fail(GlobalErrorEnum.PARAM_NOT_VALID.getCode(), "试卷不存在");
    }
    Map<String, Object> param = new HashMap<>();
    param.put("paperId", id);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMPAPERINFOBYPID");
    List<Map<String, Object>> infos = (List<Map<String, Object>>) baseService.list(param);
    if (infos == null) {
      return Result.fail(GlobalErrorEnum.PARAM_NOT_VALID.getCode(), "试卷为空");
    }
    paper.put("info", infos);
    return paper;
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
      return Result.fail(GlobalErrorEnum.PARAM_NOT_VALID.getCode(), "试卷不存在");
    }
    Map<String, Object> result = new HashMap<>();
    result.put("paperName", paper.get("paperName"));
    result.put("remark", paper.get("remark"));

    //查询各个试题
    Map<String, Object> param = new HashMap<>();
    param.put("paperId", id);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMPAPERINFOCHOICES");
    List<Map<String, Object>> choices = (List<Map<String, Object>>) baseService.list(param);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMPAPERINFOFILLGAPS");
    List<Map<String, Object>> fillgaps = (List<Map<String, Object>>) baseService.list(param);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMPAPERINFOJUDGE");
    List<Map<String, Object>> judges = (List<Map<String, Object>>) baseService.list(param);
    //查询试题及关联信息
    settingChoices(result, choices);
    settingSort(result, fillgaps);
    settingSort(result, judges);
    return result;
  }

  /**
   * 根据排序定位
   *
   * @param result
   * @param list
   */
  private void settingSort(Map<String, Object> result, List<Map<String, Object>> list) {
    if (list != null && list.size() > 0) {
      Map<String, Object> map = list.get(0);
      result.put(String.valueOf(map.get("sort")), list);
    }
  }

  /**
   * 整理选择题
   *
   * @param result
   * @param choices
   */
  @SuppressWarnings("unchecked")
  private void settingChoices(Map<String, Object> result, List<Map<String, Object>> choices) {
    if (choices != null && choices.size() > 0) {
      Map<String, Object> res = new HashMap<>();
      for (Map<String, Object> map : choices) {
        Map<String, Object> point = new HashMap<>();
        point.put(String.valueOf(map.get("point")), map.get("value"));
        String key = String.valueOf(map.get("id")) + "#" + map.get("type");
        if (res.get(key) == null) {
          Map<String, Object> question = new HashMap<>();
          question.put("id", map.get("id"));
          question.put("type", map.get("type"));
          question.put("sort", map.get("sort"));
          question.put("score", map.get("score"));
          question.put("name", map.get("name"));
          List<Map<String, Object>> array = new ArrayList<>();
          array.add(point);
          question.put("list", array);
          res.put(key, question);
        } else {
          Map<String, Object> question = (Map<String, Object>) res.get(key);
          List<Map<String, Object>> array = (List<Map<String, Object>>) question.get("list");
          array.add(point);
          res.put(key, question);
        }
      }
      List<Map<String, Object>> list1 = new ArrayList<>();
      List<Map<String, Object>> list2 = new ArrayList<>();
      for (String key : res.keySet()) {
        if (key.contains("#1")) {
          list1.add((Map<String, Object>) res.get(key));
        }
        if (key.contains("#2")) {
          list2.add((Map<String, Object>) res.get(key));
        }
      }
      settingSort(result, list1);
      settingSort(result, list2);
    }
  }

  @SuppressWarnings("unchecked")
  private void validParams(Map<String, Object> requestBody) {
    ValidationUtils.notNull(requestBody.get("paperName"), "题库类型不能为空!");
    ValidationUtils.notNull(requestBody.get("paperType"), "试卷类型不能为空!");
    if (requestBody.get("questionList") == null) {
      throw new GlobalErrorException(GlobalErrorEnum.PARAM_NOT_VALID.getCode(), "试题信息不存在!");
    }
    List<Map<String, Object>> questionList = (List<Map<String, Object>>) requestBody.get("questionList");
    if (questionList.size() == 0) {
      throw new GlobalErrorException(GlobalErrorEnum.PARAM_NOT_VALID.getCode(), "试题信息不存在!");
    }
    for (Map<String, Object> map : questionList) {
      if (map.get("subjectCategoryId") == null) {
        throw new GlobalErrorException(GlobalErrorEnum.PARAM_NOT_VALID.getCode(), "有试题题库Id不存在!");
      }
      if (map.get("questionsId") == null) {
        throw new GlobalErrorException(GlobalErrorEnum.PARAM_NOT_VALID.getCode(), "有试题Id不存在!");
      }
      if (map.get("type") == null) {
        throw new GlobalErrorException(GlobalErrorEnum.PARAM_NOT_VALID.getCode(), "有试题类型不存在!");
      }
      if (map.get("sort") == null) {
        throw new GlobalErrorException(GlobalErrorEnum.PARAM_NOT_VALID.getCode(), "有试题排序不存在!");
      }
      if (map.get("value") == null) {
        throw new GlobalErrorException(GlobalErrorEnum.PARAM_NOT_VALID.getCode(), "有试题分值不存在!");
      }
    }

    ValidationUtils.notNull(requestBody.get("creator"), "当前用户账号不能为空!");
    ValidationUtils.notNull(requestBody.get("deptCode"), "当前部门编号不能为空!");
    ValidationUtils.notNull(requestBody.get("deptName"), "当前部门名称不能为空!");
  }

  private void validId(Object id) {
    ValidationUtils.notNull(id, "id不能为空!");
    ValidationUtils.regexp(id, "^\\d+$", "非法输入");
  }

}
