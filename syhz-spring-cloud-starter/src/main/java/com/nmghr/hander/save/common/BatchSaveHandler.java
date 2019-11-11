package com.nmghr.hander.save.common;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.common.exception.GlobalErrorException;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractSaveHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
@Service("batchSaveHandler")
public class BatchSaveHandler extends AbstractSaveHandler {
  private Logger log = LoggerFactory.getLogger(BatchSaveHandler.class);

  public BatchSaveHandler(IBaseService baseService) {
    super(baseService);
  }

  @Transactional
  @Override
  public Object save(Map<String, Object> body) {
    if(body.get("alias")==null||body.get("seqName")==null||body.get("list")==null){
      return -1;
    }
    List<Map<String, Object>> list = (List<Map<String, Object>>) body.get("list");
    if(list==null || list.size()==0){
      return -1;
    }
    int subSize = 10;
    if(body.get("subSize")!=null && !"".equals(String.valueOf(body.get("subSize")).trim())){
      subSize = Integer.parseInt(String.valueOf(body.get("subSize")));
    }
    String alias = String.valueOf(body.get("alias"));
    String seqName = String.valueOf(body.get("seqName"));
    if (list.size() > subSize) {
      do {
        if (list.size() > subSize) {
          List<Map<String, Object>> sub = list.subList(0, subSize);
          if (!batchSaveData(sub, alias, seqName)) {
            throw new GlobalErrorException("999651", "提交数据有误");
          }
          list.removeAll(sub);
        } else {
          if (!batchSaveData(list, alias,seqName)) {
            throw new GlobalErrorException("999652", "提交数据有误");
          }
          list.clear();
        }
      } while (list.size() > 0);
    } else {
      if (!batchSaveData(list, alias,seqName)) {
        throw new GlobalErrorException("999653", "提交数据有误");
      }
    }
    return list.size();
  }



  /**
   * 批量处理增加数据
   *
   * @return boolean
   */
  private Boolean batchSaveData(List<Map<String, Object>> list, String alias, String seqName) {
    if (list == null || list.size() == 0) {
      throw new GlobalErrorException("999954", "提交数据异常");
    }
    log.info("batch list size: " + list.size());
    Long initId = null;
    Map<String, Object> params = new HashMap<>();
    params.put("num", list.size());
    params.put("seqName", seqName);
    try {
      //修改sequence 表自增id
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "SEQUENCEUPDATE");
      Map<String, Object> map = (Map<String, Object>) baseService.get(params);
      if (map == null) {
        log.error("batch save get " + alias);
        throw new GlobalErrorException("999997", "提交数据异常");
      }
      //计算初始id
      initId = Long.parseLong(String.valueOf(map.get("id"))) - list.size();
    } catch (Exception e) {
      log.error("batch save get SEQUENCEUPDATE list Error: " + e.getMessage());
      throw new GlobalErrorException("999997", "提交数据有误");
    }

    //拼装需要的参数
    for (Map<String, Object> bean : list) {
      initId++;//增加ID
      bean.put("id", String.valueOf(initId));
    }
    params = new HashMap<>();
    params.put("list", list);
    try {
      //提交数据
      LocalThreadStorage.put(Constant.CONTROLLER_AUTO_INCREMENT, false);
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, alias);
      baseService.save(params);
      return true;
    } catch (Exception e) {
      throw new GlobalErrorException("999997", "提交数据异常");
    }
  }
}
