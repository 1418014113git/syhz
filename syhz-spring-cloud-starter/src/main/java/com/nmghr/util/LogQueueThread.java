package com.nmghr.util;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;

public class LogQueueThread extends Thread {
  private static final Logger LOGGER = LoggerFactory.getLogger(LogQueueThread.class);
  private static ArrayBlockingQueue<Map<String, Object>> queue =
      new ArrayBlockingQueue<Map<String, Object>>(2000);

  protected IBaseService baseService;

  public LogQueueThread(IBaseService baseService) {
    this.baseService = baseService;
  }

  public static void add(Map<String, Object> msg) {
    queue.offer(msg);
  }

  @Override
  public void run() {
    while (true) {
      try {
        Thread.sleep(1000);
        Map<String, Object> task = queue.poll();
        if (task != null) {
          LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "BUSINESSLOG");
          baseService.save(task);
        }
      } catch (Exception e) {
        LOGGER.error("error", e);
      }
    }
  }

}
