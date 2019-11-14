package com.nmghr.service.message;

import com.alibaba.fastjson.JSON;
import com.nmghr.basic.core.service.handler.ISaveHandler;
import com.nmghr.basic.core.util.SpringUtils;
import com.nmghr.handler.message.QueueConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/***
 *
 */
@Component
public class MessageConsumerServiceImpl {
  Logger logger = LoggerFactory.getLogger(MessageConsumerServiceImpl.class);


  // 知识库审核消息
  @JmsListener(destination = QueueConfig.SAVEMESSAGE, containerFactory = "queueContainerFactory")
  public void consumeNoticeQueue(String message) throws Exception {
    Map<String, Object> map = JSON.parseObject(message);
    logger.info("message", "sys_message");
    ISaveHandler saveHandler = SpringUtils.getBean("messagesSaveHandler", ISaveHandler.class);
    saveHandler.save(map);// 保存到数据库
  }

}