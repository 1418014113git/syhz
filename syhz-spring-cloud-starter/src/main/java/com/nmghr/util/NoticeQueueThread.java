package com.nmghr.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.nmghr.service.BlueMsgNoticeService;

public class NoticeQueueThread extends Thread {
  private static final Logger log = LoggerFactory.getLogger(NoticeQueueThread.class);
  private static ArrayBlockingQueue<Map<String, Object>> queue =
      new ArrayBlockingQueue<Map<String, Object>>(2000);

  protected BlueMsgNoticeService bluemsgNoticeService;

  // "http://staging430.t.lanxin.cn/cgi-bin/token?grant_type=client_credential&appid=101783&secret=2TMFJ4ZgHGZPTwE_RW"
  private String reqUrl;
  private String grantType;
  private String appid;
  private String secret;
  // "https://staging430.t.lanxin.cn/cgi-bin/message/custom/send?rand=97&access_token="+access_token, objectString
  private String linkmsg;
  private String lxApi;
  private String roleCode;
  private String runFlag;

  private String TOKEN="";
  private int TIMER = 0;

  public NoticeQueueThread(BlueMsgNoticeService bluemsgNoticeService, String reqUrl,
      String grantType, String appid, String secret, String linkmsg, String lxApi, String roleCode, String runFlag) {
    this.bluemsgNoticeService = bluemsgNoticeService;
    this.reqUrl = reqUrl;
    this.grantType = grantType;
    this.appid = appid;
    this.secret = secret;
    this.linkmsg = linkmsg;
    this.lxApi = lxApi;
    this.roleCode = roleCode;
    this.runFlag = runFlag;

  }

  public static void add(Map<String, Object> msg) {
    log.info("蓝信增加queque" + JSON.toJSONString(msg));
    queue.offer(msg);
  }

  @SuppressWarnings("unchecked")
  @Override
  public void run() {
     if("run".equals(runFlag)){
      while (true) {
        try {
          if(TIMER == 0) {
            getToken();
          }
          Thread.sleep(1000);
          Map<String, Object> task = queue.poll();
          if (task != null) {
            Map<String, Object> phonesPara = new HashMap<String, Object>();
            phonesPara.put("roleCode", roleCode);
            phonesPara.put("ids", task.get("ids"));
            log.info("phonesPara 参数：{}", JSON.toJSONString(phonesPara));
            List<Map<String, Object>> list =
                (List<Map<String, Object>>) bluemsgNoticeService.list(phonesPara);
            log.info("phones: " + JSONObject.toJSONString(list));
            if (list != null && list.size() > 0) {
              JSONObject params = (JSONObject) JSONObject.toJSON(task);
              List<String> phoneList = new ArrayList<String>();
              for(Map<String, Object> map : list) {
                phoneList.add(String.valueOf(map.get("phone")));
              }
              params.put("phones", phoneList);
              notice(params);
            }
          }
          TIMER++;
          if(TIMER > 3000) {
            getToken();
          }
        } catch (Exception e) {
          log.error("error", e);
        }
      }
    }
  }

  private void getToken() throws IOException {
    log.info("调用getToken方法");
    String returnString = OkHttpUtil.getStringFromServer(reqUrl + "?grant_type=" + grantType + "&appid=" + appid + "&secret=" + secret);
    JSONObject json = (JSONObject) JSON.parse(returnString);
    String errcode = json.getString("errcode");
    log.info("获取token返回" + JSON.toJSONString(json));
    if ("0".equals(errcode)) {
      TOKEN = json.getString("access_token");
      TIMER=1;
    }
  }

  private void notice(JSONObject params) throws IOException {
    log.info("调用notice方法{}",JSON.toJSONString(params));
    log.info("TOKEN : {}",TOKEN);
    log.info("TIMER : {}",TIMER);
    if (!"".equals(TOKEN)) {
      String StartJson = "{}";
      JSONObject joOjbect = JSON.parseObject(StartJson);

      String StartUrl = "{}";
      JSONObject joOjbectUrl = JSON.parseObject(StartUrl);
      joOjbectUrl.put("url",
          linkmsg + "type=" + params.getString("type")
              + "&bizId=" + params.getLongValue("bizId"));
      joOjbect.put("tousers", params.get("phones"));
      joOjbect.put("msgtype", "link");
      joOjbect.put("link", joOjbectUrl);
      String objectString = joOjbect.toJSONString();
      log.info("推送的信息: " + objectString);
      String value = OkHttpUtil.post(lxApi + "&access_token=" + TOKEN, objectString);
      log.info("蓝信返回: "+ value);
    }
  }


}
