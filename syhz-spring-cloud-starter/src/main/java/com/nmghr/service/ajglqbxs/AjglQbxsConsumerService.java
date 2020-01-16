package com.nmghr.service.ajglqbxs;

import com.alibaba.fastjson.JSON;
import com.nmghr.handler.message.QueueConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class AjglQbxsConsumerService {
  Logger logger = LoggerFactory.getLogger(AjglQbxsConsumerService.class);

  @Autowired
  private AjglQbxsRecordService ajglQbxsRecordService;

  @Autowired
  private AjglQbxsInfoService ajglQbxsInfoService;


  @JmsListener(destination = QueueConfig.AJGLQBXSRECORD, containerFactory = "queueContainerFactory")
  public void consumeQbxsRecordQueue(String message) throws Exception {
    logger.info("message", QueueConfig.AJGLQBXSRECORD);
    Map<String, Object> map = JSON.parseObject(message);
    if("all".equals(String.valueOf(map.get("type")))){
      ajglQbxsRecordService.allSave(map);
    } else {
      ajglQbxsRecordService.batchSave(map);
    }
  }

  /**
   * 更新线索信息
   * @param message
   * @throws Exception
   */
  @JmsListener(destination = QueueConfig.AJGLQBXSINFO, containerFactory = "queueContainerFactory")
  public void consumeQbxsInfoQueue(String message) throws Exception {
    logger.info("message", QueueConfig.AJGLQBXSINFO);
    Map<String, Object> map = JSON.parseObject(message);
    ajglQbxsInfoService.modifyQbxsInfo(map);
  }

}
