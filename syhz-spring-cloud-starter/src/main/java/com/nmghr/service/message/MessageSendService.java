package com.nmghr.service.message;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.handler.message.QueueConfig;
import com.nmghr.handler.service.SendMessageService;
import com.nmghr.service.DeptNameService;
import com.nmghr.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * 管理员接收人
 */
@SuppressWarnings("unchecked")
@Service("messageSendService")
public class MessageSendService {
  private Logger log = LoggerFactory.getLogger(MessageSendService.class);

  @Autowired
  private IBaseService baseService;

  @Autowired
  private DeptNameService deptNameService;

  @Autowired
  private SendMessageService sendMessageService;

  /**
   * 查询管理员接收人
   *
   */
  public List<Map<String, Object>> getUserList(List<Object> deptIds) throws Exception {
    if(deptIds==null || deptIds.size()==0){
      return new ArrayList<>();
    }
    Map<String, Object> result = new HashMap<>();
    Map<String, Object> queryP = new HashMap<>();
    queryP.put("deptIds",deptIds);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "UMANAGERREPLACELIST");
    List<Map<String, Object>> replaces = (List<Map<String, Object>>) baseService.list(queryP);
    if(replaces!=null&&replaces.size()>0){
      for (Map<String, Object> map : replaces) {
        String replaceIds = String.valueOf(map.get("replaceIds"));
        JSONArray array = JSON.parseArray(replaceIds);
        for (int i=0;i<array.size();i++){
          Map<String, Object> m = array.getObject(i,Map.class);
          if(!StringUtils.isEmpty(m.get("id"))){
            result.put(String.valueOf(m.get("id")),m);
          }
        }
      }
    }
    return new ArrayList(result.values());
  }
  /**
   * 查询管理员接收人
   *
   */
  public Object getUserList(List<Object> deptIds, List<Map<String, Object>> list, Map<String, Object> body) throws Exception {
    if(deptIds==null || deptIds.size()==0){
      return new ArrayList<>();
    }
    Map<String, Object> result = new HashMap<>();
    Map<String, Object> queryP = new HashMap<>();
    queryP.put("deptIds",deptIds);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "UMANAGERREPLACELIST");
    List<Map<String, Object>> replaces = (List<Map<String, Object>>) baseService.list(queryP);
    if(replaces!=null&&replaces.size()>0){
      for (Map<String, Object> map : replaces) {
        String replaceIds = String.valueOf(map.get("replaceIds"));
        JSONArray array = JSON.parseArray(replaceIds);
        for (int i=0;i<array.size();i++){
          Map<String, Object> m = array.getObject(i,Map.class);
          result.put(String.valueOf(m.get("id")),m);
        }
      }
    }

    List<Map<String, Object>> array = new ArrayList();
    list.addAll(array);

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
        params.put("creatorName", body.get("userName"));
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

    return list.size();
  }

  /**
   * 查询管理员接收人
   *
   */
  public Object getMARUserList(Map<String, Object> body) throws Exception {
    // 查询所有部门管理员用户
    Map<String, Object> result = (Map<String, Object>) body.get("managers");
    if(result==null){
      result = new HashMap<>();
    }
    List<Map<String, Object>> depts = (List<Map<String, Object>>) body.get("depts");
    if(depts==null || depts.size()==0){
      return new ArrayList<>();
    }
    List<Object> deptIds = new ArrayList<>();
    for (Map<String, Object> d:depts){
      deptIds.add(d.get("receiverDeptId"));
    }

    //查询管理员得代理接收人
    Map<String, Object> queryUserP = new HashMap<>();
    queryUserP.put("deptIds",deptIds);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "UMANAGERREPLACELIST");
    List<Map<String, Object>> replaces = (List<Map<String, Object>>) baseService.list(queryUserP);
    if(replaces!=null&&replaces.size()>0){
      for (Map<String, Object> map : replaces) {
        String replaceIds = String.valueOf(map.get("replaceIds"));
        JSONArray array = JSON.parseArray(replaceIds);
        for (int i=0;i<array.size();i++){
          Map<String, Object> m = array.getObject(i,Map.class);
          m.put("userId", m.get("id"));
          result.put(String.valueOf(m.get("id")),m);
        }
      }
    }
    //增加额外签收人
    if(!StringUtils.isEmpty(body.get("recipientUser"))){
      JSONArray array = JSONArray.parseArray(String.valueOf(body.get("recipientUser")));
      for (int i=0;i<array.size();i++){
        Map<String, Object> m = new HashMap<>();
        JSONObject obj = array.getJSONObject(i);
        m.put("userId",obj.get("id"));
        m.put("realName",obj.get("name"));
        result.put(String.valueOf(m.get("userId")),m);
      }
    }
    List<Map<String, Object>> array = new ArrayList(result.values());
    for (Map<String, Object> bean : array) {
      if (bean != null && bean.containsKey("userId")) {
        Map<String, Object> params = new HashMap<>();
        params.put("bussionType", 4);
        params.put("bussionTypeInfo", 405);
        params.put("bussionId", -1);
        params.put("title", body.get("curDeptName") + "发布了一则通知！");
        params.put("content", body.get("curDeptName") + "发布了一则关于“" + body.get("title") + "”的通知，请尽快使用管理员账号登录平台并签收！");
        params.put("status", 0);
        params.put("creator", body.get("userId"));
        params.put("creatorName", body.get("userName"));
        params.put("deptCode", body.get("curDeptCode"));
        params.put("deptName", body.get("curDeptName"));
        params.put("acceptId", bean.get("userId"));
        params.put("acceptName", bean.get("realName"));
        params.put("category", 1);//弹出信息
        sendMessageService.sendMessage(params, QueueConfig.TIMELYMESSAGE);
        sendMessageService.sendMessage(params, QueueConfig.SAVEMESSAGE);
        log.info("通知发送消息：id: "+ bean.get("userId")+ ", name: " + bean.get("realName"));
      }
    }
    return array.size();
  }

}
