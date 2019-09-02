package com.nmghr.job;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.impl.BaseServiceImpl;
import com.nmghr.basic.task.Task;

@Service("expireDataManagerJob")
public class ExpireDataManagerJob implements Task {
  private static Logger logger = LoggerFactory.getLogger(ExpireDataManagerJob.class);

  @Autowired
  private BaseServiceImpl baseService;

  @Override
  @Transactional
  public void run() {
    logger.info("expireDataManagerJob start!");
    try {
      // 案件协查查询已到期的并修改状态
      caseAssistExpire();
      // 全国性协查到期数据处理
      wholeCountryAssistExpire();
      // 督办到期数据处理
      superviseExpire();
    } catch (Exception e) {
      e.getMessage();
    }
    logger.info("expireDataManagerJob end!");
  }

  /**
   * 案件协查到期数据处理
   * 
   * @throws Exception
   */
  private void caseAssistExpire() throws Exception {
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXPIRECASEASSIST");
    Map<String, Object> params = new HashMap<String, Object>();
    Map<String, Object> body = new HashMap<String, Object>();
    baseService.update(params, body);
  }

  /**
   * 全国性协查到期数据处理
   * 
   * @throws Exception
   */
  private void wholeCountryAssistExpire() throws Exception {
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXPIRECOUNTRYASSIST");
    Map<String, Object> params = new HashMap<String, Object>();
    Map<String, Object> body = new HashMap<String, Object>();
    baseService.update(params, body);
  }

  /**
   * 督办到期数据处理
   * 
   * @throws Exception
   */
  private void superviseExpire() throws Exception {
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXPIRESUPERVISE");
    Map<String, Object> params = new HashMap<String, Object>();
    Map<String, Object> body = new HashMap<String, Object>();
    baseService.update(params, body);
  }

}
