package com.nmghr.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import com.nmghr.common.GlobalConfig;
import com.nmghr.service.BlueMsgNoticeService;

@Component
public class NoticeEventListener implements ApplicationListener<ApplicationReadyEvent>, Ordered {

  @Autowired
  protected BlueMsgNoticeService bluemsgNoticeService;

  @Override
  public int getOrder() {
    return 100;
  }

  public void onApplicationEvent(ApplicationReadyEvent arg0) {
    new NoticeQueueThread(bluemsgNoticeService, GlobalConfig.reqUrl, GlobalConfig.grantType,
        GlobalConfig.appid, GlobalConfig.secret, GlobalConfig.linkmsg, GlobalConfig.lxApi,
        GlobalConfig.roleCode, GlobalConfig.runFlag).start();
  }

}
