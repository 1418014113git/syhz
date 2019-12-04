package com.nmghr.controller.message;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.nmghr.basic.common.Constant;
import com.nmghr.basic.common.Result;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.util.ValidationUtils;
import com.nmghr.handler.message.QueueConfig;
import com.nmghr.handler.service.SendMessageService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"unchecked"})
@RestController
@RequestMapping("/message")
public class MessageController {
  @Autowired
  @Qualifier("baseService")
  private IBaseService baseService;

  @Autowired
  private SendMessageService sendMessageService;

  @PutMapping("/send")
  @ResponseBody
  public Object save(@RequestBody Map<String, Object> body) {
    ValidationUtils.notNull(body.get("userId"), "userId 不能为空!");
    ValidationUtils.notNull(body.get("userName"), "userName 不能为空!");
    ValidationUtils.notNull(body.get("curDeptId"), "curDeptId 不能为空!");
    ValidationUtils.notNull(body.get("curDeptName"), "curDeptName 不能为空!");
    ValidationUtils.notNull(body.get("curDeptCode"), "curDeptCode 不能为空!");
    ValidationUtils.notNull(body.get("title"), "title 不能为空!");
    ValidationUtils.notNull(body.get("content"), "content 不能为空!");
    String content = String.valueOf(body.get("content"));
    if(content.length()>500){
      return Result.fail("999667","内容长度不能超过500字符");
    }
    ValidationUtils.notNull(body.get("recipient"), "recipient 不能为空!");

    JSONArray array = JSONArray.parseArray(String.valueOf(body.get("recipient")));
    List<Object> names = new ArrayList();
    for (int i = 0; i < array.size(); i++) {
      JSONObject json = array.getJSONObject(i);
      names.add(json.get("id")+"_"+json.get("name"));
      Map<String, Object> params = setMap(body.get("title"),body.get("content"),json.get("id"), json.get("name"),
          body.get("userId"), body.get("userName"), body.get("curDeptName"),body.get("curDeptCode"), null);
      sendMessageService.sendMessage(params, QueueConfig.SAVEMESSAGE);
      sendMessageService.sendMessage(params, QueueConfig.TIMELYMESSAGE);
    }
    Map<String, Object> params = setMap(body.get("title"),body.get("content"), "", names.get(0),
        body.get("userId"), body.get("userName"), body.get("curDeptCode"),body.get("curDeptName"), StringUtils.join(names.toArray(), ","));
    sendMessageService.sendMessage(params, QueueConfig.SAVEMESSAGE);
    return array.size();
  }

  private Map<String, Object> setMap(Object title, Object content, Object id,
                                     Object name, Object userId, Object userName, Object curDeptCode, Object curDeptName, Object remark) {
    Map<String, Object> params = new HashMap<>();
    params.put("bussionType", 4);
    params.put("bussionTypeInfo", 403);
    params.put("bussionId", -1);
    params.put("title", title);
    params.put("content", content);
    params.put("status", 0);
    params.put("acceptId", id);
    params.put("acceptName", name);
    params.put("creator", userId);
    params.put("creatorName", userName);
    params.put("deptCode", curDeptCode);
    params.put("deptName", curDeptName);
    params.put("category", 1);//弹出信息
    if(remark!=null){
      params.put("remark", remark);//弹出信息
    }
    return params;
  }

  @GetMapping("/query")
  @ResponseBody
  public Object query(@RequestParam Map<String, Object> params) {
    ValidationUtils.notNull(params.get("userId"), "userId不能为空!");
    int pageNum = 1, pageSize = 15;
    if (params.get("pageNum") != null) {
      pageNum = Integer.parseInt(String.valueOf(params.get("pageNum")));
    }
    if (params.get("pageSize") != null) {
      pageSize = Integer.parseInt(String.valueOf(params.get("pageSize")));
    }
    try {
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "BASESYSMESSAGELIST");
      return baseService.page(params, pageNum, pageSize);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return Result.fail("999667","查询失败");
  }

  @GetMapping("/unread/{userId}")
  @ResponseBody
  public Object unread(@PathVariable Object userId, @RequestParam Map<String, Object> params) {
    validId(userId);
    try {
      params.put("userId", userId);
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "BASESYSMESSAGESHOW");
      Object obj = baseService.get(params);
      Map<String, Object> result = new HashMap<>();
      result.put("time" ,System.currentTimeMillis()/1000);
      if(obj ==null){
        return result;
      }
      obj = baseService.get(params);
      result.putAll((Map<String, Object>) obj);
      return result;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return Result.fail("999667","查询失败");
  }

  @PostMapping("/delete")
  @ResponseBody
  public Object delete(@RequestBody Map<String, Object> body) {
    ValidationUtils.notNull(body.get("messagesId"), "messagesId不能为空!");
    ValidationUtils.notNull(body.get("userId"), "userId不能为空!");
    try {
      List<Object> list = (List<Object>) body.get("messagesId");
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "SYSMESSAGESDEL");
      return baseService.update("", body);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return Result.fail("999667","删除失败");
  }

  private void validId(Object id) {
    ValidationUtils.notNull(id, "id不能为空!");
    ValidationUtils.regexp(id, "^\\d+$", "非法输入");
  }

}
