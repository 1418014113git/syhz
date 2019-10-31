package com.nmghr.controller.message;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.nmghr.basic.common.Constant;
import com.nmghr.basic.common.Result;
import com.nmghr.basic.common.exception.GlobalErrorEnum;
import com.nmghr.basic.common.exception.GlobalErrorException;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.page.Paging;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.ISaveHandler;
import com.nmghr.basic.core.service.handler.IUpdateHandler;
import com.nmghr.basic.core.util.SpringUtils;
import com.nmghr.basic.core.util.ValidationUtils;
import com.nmghr.common.GlobalConfig;
import com.nmghr.handler.message.QueueConfig;
import com.nmghr.handler.service.SendMessageService;
import com.nmghr.service.DeptNameService;
import com.nmghr.util.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@SuppressWarnings({"unchecked", "CatchMayIgnoreException"})
@RestController
@RequestMapping("/notice")
public class NoticeController {
  @Autowired
  @Qualifier("baseService")
  private IBaseService baseService;

  @Autowired
  @Qualifier("deptNameService")
  private DeptNameService deptNameService;

  @Autowired
  private SendMessageService sendMessageService;


  @PutMapping("/save")
  @ResponseBody
  public Object save(@RequestBody Map<String, Object> body) {
    validParams(body);
    try {
      if ("true".equals(String.valueOf(body.get("checkFlag"))) && "1".equals(String.valueOf(body.get("messageStatus")))) {
        body.put("depts", getDepts(String.valueOf(body.get("recipient"))));
      }
      ISaveHandler saveHandler = SpringUtils.getBean("noticeSubmitSaveHandler", ISaveHandler.class);
      Object obj = saveHandler.save(body);
      return Result.ok(obj);
    } catch (Exception e) {
      if (e instanceof GlobalErrorException) {
        GlobalErrorException ge = (GlobalErrorException) e;
        if ("999667".equals(String.valueOf(ge.getCode()))) {
          return Result.fail("999667", ge.getMessage());
        }
      }
    }
    return Result.fail("999668", "保存失败");
  }

  @PostMapping("/update")
  @ResponseBody
  public Object update(@RequestBody Map<String, Object> body) throws Exception {
    validId(body.get("id"));
    validParams(body);
    try {
      if ("true".equals(String.valueOf(body.get("checkFlag"))) && "1".equals(String.valueOf(body.get("messageStatus")))) {
        body.put("depts", getDepts(String.valueOf(body.get("recipient"))));
      }
      IUpdateHandler updateHandler = SpringUtils.getBean("noticeSubmitUpdateHandler", IUpdateHandler.class);
      Object obj = updateHandler.update(String.valueOf(body.get("id")), body);
      return Result.ok(obj);
    } catch (Exception e) {
      if (e instanceof GlobalErrorException) {
        GlobalErrorException ge = (GlobalErrorException) e;
        if ("999667".equals(String.valueOf(ge.getCode()))) {
          return Result.fail("999667", ge.getMessage());
        }
      }
    }
    return Result.fail("999668", "修改失败");
  }

  @GetMapping("/{id}")
  @ResponseBody
  public Object getOne(@PathVariable String id) throws Exception {
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "BASEMESSAGE");
    Object obj = baseService.get(id);
    return Result.ok(obj);
  }

  @GetMapping("/detail/{id}")
  @ResponseBody
  public Object detail(@PathVariable String id, @RequestParam Map<String, Object> reqParam) throws Exception {
    ValidationUtils.notNull(reqParam.get("curDeptId"), "curDeptId不能为空!");
    validId(id);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "BASEMESSAGE");
    Map<String, Object> result = (Map<String, Object>) baseService.get(id);
    if (result == null) {
      return Result.fail("999667", "通知不存在！");
    }
    //查询是否有上一次信息
    if (result.get("parentId") != null && !"".equals(String.valueOf(result.get("parentId")).trim())) {
      getParentInfo(result);
    }
    if ("0".equals(String.valueOf(result.get("messageStatus")))) {
      //为0 表示草稿没有签收信息
      return Result.ok(result);
    }
    //查询签收信息
    Map<String, Object> params = new HashMap<>();
    params.put("messageId", id);
    params.put("receiverDeptId", reqParam.get("curDeptId"));
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "BASEMESSAGESIGN");
    List<Map<String, Object>> signs = (List<Map<String, Object>>) baseService.list(params);
    if (signs != null && signs.size() > 0) {
      result.put("signInfo", signs.get(0));
    }
    return Result.ok(result);
  }

  /**
   * 获取上一层信息
   *
   * @param result void
   * @throws Exception e
   */
  private void getParentInfo(Map<String, Object> result) throws Exception {
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "BASEMESSAGE");
    Map<String, Object> parent = (Map<String, Object>) baseService.get(String.valueOf(result.get("parentId")));
    Object deptName = result.get("creatorDeptName");
    Object userName = result.get("creatorName");
    Object time = result.get("createTime");
    result.put("forwardDeptName", deptName);
    result.put("forwardUserName", userName);
    result.put("forwardTime", time);
    result.put("creatorDeptName", parent.get("creatorDeptName"));
    result.put("creatorName", parent.get("creatorName"));
    result.put("createTime", parent.get("createTime"));
  }

  /**
   * 列表
   *
   * @param params Map
   * @return Object
   * @throws Exception e
   */
  @GetMapping("/list")
  @ResponseBody
  public Object list(@RequestParam Map<String, Object> params) throws Exception {
    ValidationUtils.notNull(params.get("checkFlag"), "checkFlag 不能为空!");
    ValidationUtils.notNull(params.get("userId"), "userId 不能为空!");
    ValidationUtils.notNull(params.get("curDeptId"), "curDeptId 不能为空!");
    int pageNum = 1, pageSize = 15;
    if (params.get("pageNum") != null) {
      pageNum = Integer.parseInt(String.valueOf(params.get("pageNum")));
    }
    if (params.get("pageSize") != null) {
      pageSize = Integer.parseInt(String.valueOf(params.get("pageSize")));
    }
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "BASEMESSAGELIST");
    Object obj = baseService.page(params, pageNum, pageSize);
    return Result.ok(obj);
  }

  /**
   * 列表
   *
   * @param params Map
   * @return Object
   * @throws Exception e
   */
  @GetMapping("/home")
  @ResponseBody
  public Object home(@RequestParam Map<String, Object> params) throws Exception {
    ValidationUtils.notNull(params.get("checkFlag"), "checkFlag不能为空!");
    ValidationUtils.notNull(params.get("userId"), "userId 不能为空!");
    ValidationUtils.notNull(params.get("curDeptId"), "curDeptId 不能为空!");
    int pageNum = 1, pageSize = 5;
    if (params.get("pageNum") != null) {
      pageNum = Integer.parseInt(String.valueOf(params.get("pageNum")));
    }
    if (params.get("pageSize") != null) {
      pageSize = Integer.parseInt(String.valueOf(params.get("pageSize")));
    }
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "BASEMESSAGELIST");
    Object obj = baseService.page(params, pageNum, pageSize);
    if (obj == null) {
      return Result.ok(new HashMap<>());
    }
    Paging page = (Paging) obj;
    Map<String, Object> result = new HashMap<>();
    result.put("total", page.getTotalCount());
    result.put("allData", page.getList());

    if ("true".equals(String.valueOf(params.get("checkFlag")))) {
      params.put("signStatus", 1);
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "BASEMESSAGELIST");
      Object unObj = baseService.page(params, pageNum, pageSize);
      if (obj != null) {
        Paging unList = (Paging) unObj;
        result.put("unSignNum", unList.getTotalCount());
        result.put("unSignData", unList.getList());
      }
    }
    return Result.ok(result);
  }


  @PostMapping("/validName")
  @ResponseBody
  public Object validName(@RequestBody Map<String, Object> body) throws Exception {
    ValidationUtils.notNull(body.get("userId"), "userId 不能为空!");
    ValidationUtils.notNull(body.get("title"), "title 不能为空!");
    Map<String, Object> params = new HashMap<>();
    params.put("userId", body.get("userId"));
    params.put("title", body.get("title"));
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "BASEMESSAGENAMECHECK");
    return Result.ok(baseService.list(params));
  }

  /**
   * 签收或者阅读
   * type 等于sign 为签收，等于read为阅读
   *
   * @param type String
   * @param body Map
   * @return Object
   * @throws Exception e
   */
  @PostMapping("/{type}")
  @ResponseBody
  public Object signAndRead(@PathVariable String type, @RequestBody Map<String, Object> body) throws Exception {
    ValidationUtils.notNull(body.get("id"), "id不能为空!");
    ValidationUtils.notNull(body.get("userId"), "userId不能为空!");
    ValidationUtils.notNull(body.get("userName"), "userName不能为空!");
    Map<String, Object> params = new HashMap<>();
    params.put("receiverId", body.get("userId"));
    params.put("receiverName", body.get("userName"));
    if ("sign".equals(type)) {
      params.put("receiveTime", DateUtil.dateFormart(new Date(), DateUtil.yyyyMMddHHmmss));
      params.put("signStatus", body.get("signStatus"));
    }
    if ("read".equals(type)) {
      params.put("readTime", DateUtil.dateFormart(new Date(), DateUtil.yyyyMMddHHmmss));
      params.put("readStatus", body.get("readStatus"));
      params.remove("receiverName");
    }
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "BASEMESSAGESIGN");
    return Result.ok(baseService.update(String.valueOf(body.get("id")), params));
  }

  /**
   * 返回签收信息
   *
   * @param body Map
   * @return Object
   * @throws Exception e
   */
  @PostMapping("/examineInfo")
  @ResponseBody
  public Object examineInfo(@RequestBody Map<String, Object> body) throws Exception {
    if (body.get("curDeptId") == null || "".equals(String.valueOf(body.get("curDeptId")).trim())) {
      return Result.fail("999667", "curDeptId 不能为空");
    }
    if (body.get("id") == null || "".equals(String.valueOf(body.get("id")).trim())) {
      return Result.fail("999667", "id 不能为空");
    }
    Map<String, Object> params = new HashMap<>();
    params.put("id", body.get("id"));
    params.put("curDeptId", body.get("curDeptId"));
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "BASEMESSAGEWORKORDERFLOW");
    return Result.ok(baseService.list(params));
  }


  /**
   * 提交审核
   *
   * @param body Map
   * @return Object
   */
  @PostMapping("/examineSubmit")
  @ResponseBody
  public Object examineSubmit(@RequestBody Map<String, Object> body) {
    ValidationUtils.notNull(body.get("noticeId"), "messageId不能为空!");
    ValidationUtils.notNull(body.get("flowStatus"), "flowStatus不能为空!");
    ValidationUtils.notNull(body.get("userName"), "用户名不能为空!");
    ValidationUtils.notNull(body.get("userId"), "userId不能为空!");
    ValidationUtils.notNull(body.get("curDeptCode"), "curDeptCode不能为空!");
    ValidationUtils.notNull(body.get("curDeptName"), "curDeptName不能为空!");
    ValidationUtils.notNull(body.get("wdId"), "wdId不能为空!");
    ValidationUtils.notNull(body.get("flowId"), "flowId不能为空!");
    try {
      String flowStatus = String.valueOf(body.get("flowStatus"));
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "BASEMESSAGE");
      Map<String, Object> bean = (Map<String, Object>) baseService.get(String.valueOf(body.get("noticeId")));
      if (bean == null) {
        return Result.fail("999667", "通知不存在！");
      }
      if (!"1".equals(String.valueOf(bean.get("messageStatus")))) {
        return Result.fail("999667", "非审核状态不能操作！");
      }
      if ("3".equals(flowStatus)) {
        body.put("depts", getDepts(String.valueOf(bean.get("recipient"))));
      }

      body.put("creatorId", bean.get("creatorId"));
      body.put("creatorName", bean.get("creatorName"));
      body.put("title", bean.get("title"));
      IUpdateHandler updateHandler = SpringUtils.getBean("examineSubmitUpdateHandler", IUpdateHandler.class);
      Object obj = updateHandler.update(String.valueOf(body.get("flowId")), body);
      return Result.ok(obj);
    } catch (Exception e) {
      if (e instanceof GlobalErrorException) {
        GlobalErrorException ge = (GlobalErrorException) e;
        if ("999667".equals(String.valueOf(ge.getCode()))) {
          return Result.fail("999667", ge.getMessage());
        }
      }
    }
    return Result.fail("999668", "修改失败");
  }

  private List<Map<String, Object>> getDepts(String recipient) throws Exception {
    JSONArray groups = JSONArray.parseArray(recipient);
    Map<String, Object> deptIds = new HashMap<>();
    for (int i = 0; i < groups.size(); i++) {
      JSONObject obj = groups.getJSONObject(i);
      JSONArray array = obj.getJSONArray("list");
      for (int j = 0; j < array.size(); j++) {
        String id = array.getString(j);
        deptIds.put(id, id);
      }
    }
    return getDeptName(new ArrayList(deptIds.values()));
  }

  /**
   * 删除
   *
   * @param body
   * @return
   */
  @PostMapping("/delete")
  @ResponseBody
  public Object delete(@RequestBody Map<String, Object> body) {
    ValidationUtils.notNull(body.get("id"), "id不能为空!");
    ValidationUtils.regexp(body.get("id"), "^\\d+$", "非法输入");
    ValidationUtils.notNull(body.get("creatorId"), "id不能为空!");
    ValidationUtils.regexp(body.get("creatorId"), "^\\d+$", "非法输入");
    try {
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "BASEMESSAGE");
      Map<String, Object> bean = (Map<String, Object>) baseService.get(String.valueOf(body.get("id")));
      if (bean == null) {
        return Result.fail("999667", "通知不存在！");
      }
      String status = String.valueOf(bean.get("messageStatus"));
      if ("2".equals(status)) {
        return Result.fail("999667", "审核通过的通知不能删除！");
      }
      Map<String, Object> params = new HashMap<>();
      params.put("id", body.get("id"));
      params.put("creatorId", body.get("creatorId"));
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "BASEMESSAGEDEL");
      baseService.update(String.valueOf(body.get("id")), params);
      return Result.ok(true);
    } catch (Exception e) {
    }
    return Result.fail("999668", "删除失败");
  }


  /**
   * 签收提醒
   *
   * @param body
   * @return
   */
  @PostMapping("/signRemind")
  @ResponseBody
  public Object signRemind(@RequestBody Map<String, Object> body) {
    ValidationUtils.notNull(body.get("signId"), "signId不能为空!");
    ValidationUtils.notNull(body.get("title"), "title不能为空!");
    ValidationUtils.notNull(body.get("creatorName"), "creatorName不能为空!");
    ValidationUtils.notNull(body.get("creatorDate"), "creatorDate不能为空!");
    ValidationUtils.notNull(body.get("userId"), "userId不能为空!");
    ValidationUtils.notNull(body.get("curDeptCode"), "curDeptCode不能为空!");
    ValidationUtils.notNull(body.get("curDeptName"), "curDeptName不能为空!");
    ValidationUtils.notNull(body.get("acceptDeptId"), "acceptDeptId不能为空!");
    try {

      Map<String, Object> codePara = new HashMap<>();
      codePara.put("mananercode", "mananercode");
      codePara.put("queryType", "dictCode");
      List<Map<String, Object>> mCodes = (List<Map<String, Object>>) deptNameService.list(codePara);
      if (mCodes == null || mCodes.size() == 0) {
        return Result.fail("999667", "请联系管理员设置管理者code！");
      }
      List<Object> codes = new ArrayList<>();
      for (Map<String, Object> map : mCodes) {
        codes.add(map.get("dictCode"));
      }

      Map<String, Object> map = new HashMap<>();
      map.put("deptId", body.get("acceptDeptId"));
      map.put("roleCodes", StringUtils.join(codes, ","));
      map.put("queryType", "managerUserId");
      List<Map<String, Object>> list = (List<Map<String, Object>>) deptNameService.list(map);
      if (list == null || list.size() == 0) {
        return Result.fail("999667", "该部门未查询到管理人员");
      }
      for (Map<String, Object> bean : list) {
        if (bean != null && bean.containsKey("userId")) {
          Map<String, Object> params = new HashMap<>();
          params.put("bussionType", 4);
          params.put("bussionTypeInfo", 405);
          params.put("bussionId", -1);
          params.put("title", body.get("title") + "签收提醒");
          params.put("content", body.get("creatorName") + "与" + body.get("creatorDate") + "发布的" + body.get("title") + "通知您还未签收，请及时查阅并签收！");
          params.put("status", 0);
          params.put("creator", body.get("userId"));
          params.put("deptCode", body.get("curDeptCode"));
          params.put("deptName", body.get("curDeptName"));
          params.put("acceptId", bean.get("userId"));
          params.put("category", 1);//弹出信息
          sendMessageService.sendMessage(params, QueueConfig.TIMELYMESSAGE);
          sendMessageService.sendMessage(params, QueueConfig.SAVEMESSAGE);
          params = new HashMap<>();
          params.put("remindTime", DateUtil.dateFormart(new Date(), DateUtil.yyyyMMddHHmmss));
          LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "BASEMESSAGESIGN");
          baseService.update(String.valueOf(body.get("signId")), params);
        }
      }
      return Result.ok(list.size());
    } catch (Exception e) {
    }
    return Result.fail("999668", "操作失败");
  }

  private List<Map<String, Object>> getDeptName(List<Object> ids) throws Exception {
    if (ids == null || ids.size() == 0) {
      throw new GlobalErrorException("999667", "接收人不能为空");
    }
    Map<String, Object> params = new HashMap<>();
    params.put("ids", ids);
    params.put("queryType", "deptName");
    Object obj = deptNameService.list(params);
    if (obj == null) {
      throw new GlobalErrorException("999667", "部门信息异常");
    }
    return (List<Map<String, Object>>) obj;
  }

  private void validParams(@RequestBody Map<String, Object> body) {
    ValidationUtils.notNull(body.get("title"), "标题不能为空!");
    ValidationUtils.notNull(body.get("content"), "内容不能为空!");
    ValidationUtils.notNull(body.get("userId"), "userId不能为空!");
    ValidationUtils.notNull(body.get("userName"), "userName不能为空!");
    ValidationUtils.notNull(body.get("curDeptId"), "curDeptId不能为空!");
    ValidationUtils.notNull(body.get("curDeptName"), "curDeptName不能为空!");
    ValidationUtils.notNull(body.get("curDeptCode"), "curDeptCode不能为空!");
    ValidationUtils.notNull(body.get("messageStatus"), "messageStatus不能为空!");
    ValidationUtils.notNull(body.get("recipient"), "接收人不能为空!");
    JSONArray groups = JSONArray.parseArray(String.valueOf(body.get("recipient")));
    if (groups == null || groups.size() == 0) {
      throw new GlobalErrorException("999667", "接收人不能为空");
    }
  }

  private void validId(Object id) {
    ValidationUtils.notNull(id, "id不能为空!");
    ValidationUtils.regexp(id, "^\\d+$", "非法输入");
  }

}
