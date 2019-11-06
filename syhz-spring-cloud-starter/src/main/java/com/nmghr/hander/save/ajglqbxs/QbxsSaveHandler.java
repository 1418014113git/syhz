package com.nmghr.hander.save.ajglqbxs;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.common.exception.GlobalErrorException;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractSaveHandler;
import com.nmghr.controller.vo.PitchManVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
@Service("qbxsSaveHandler")
public class QbxsSaveHandler extends AbstractSaveHandler {

  private Logger log = LoggerFactory.getLogger(QbxsSaveHandler.class);

  public QbxsSaveHandler(IBaseService baseService) {
    super(baseService);
  }

  /**
   * 批量导入数据增加方法
   */
  @Transactional
  @Override
  public Object save(Map<String, Object> body) {
    //添加标题信息
    //添加明细数据
    List<Map<String, Object>> list = (List<Map<String, Object>>) body.get("list");
    if (list == null || list.size() == 0) {
      return null;
    }
    Object type = body.get("category");
    Object creator = body.get("creator");
    Object curDeptCode = body.get("curDeptCode");
    Object curDeptName = body.get("curDeptName");
    Object assistId = body.get("assistId");
    Map<String, Object> bean = list.get(0);
    List<Map<String, Object>> paramTitles = new ArrayList<>(bean.keySet().size());
    int tIdx = 0;
//    (#{item.id}, #{item.qbxsType}, #{item.qbxsIndex}, #{item.titleItem}, #{item.creator},
// #{item.deptCode}, #{item.deptName}, #{item.assistId})
    for (String str : bean.keySet()) {
      Map<String, Object> p = new HashMap<>();
      p.put("qbxsType", type);
      p.put("qbxsIndex", tIdx);
      p.put("titleItem", str);
      p.put("creator", creator);
      p.put("deptCode", curDeptCode);
      p.put("deptName", curDeptName);
      p.put("assistId", assistId);
      paramTitles.add(p);
      tIdx++;
    }

//    (#{item.id}, #{item.assistId}, #{item.ajglQbxsIdx}, #{item.rowIndex}, #{item.columnIndex},
// #{item.titleIndex}, #{item.value}, #{item.creator})
    List<Map<String, Object>> paramValues = new ArrayList<>(list.size());
    int size = list.size();
    for (int i = 0; i < size; i++) {
      Map<String, Object> values = list.get(i);
      int vIdx = 0;
      Object titleIndex = values.get("序号");
      String addr = String.valueOf(values.get("地址"));
      for (Object str : values.values()) {
        Map<String, Object> p = new HashMap<>();
        p.put("assistId", assistId);
        p.put("ajglQbxsIdx", assistId + "_" + i);
        p.put("rowIndex", vIdx);
        p.put("columnIndex", i);
        p.put("titleIndex", titleIndex);
        p.put("value", str);
        p.put("creator", creator);
        p.put("addr", addr);
        paramValues.add(p);
        vIdx++;
      }
    }

    batchSave(paramTitles, 1);
    batchSave(paramValues, 2);
    return null;
  }

  private void batchSave(List<Map<String, Object>> list, int type) {
    int subSize = 20;
    if (list.size() > subSize) {
      do {
        if (list.size() > subSize) {
          List<Map<String, Object>> sub = list.subList(0, subSize);
          if (!batchSaveData(sub, type)) {
            throw new GlobalErrorException("999668", "提交数据有误");
          }
          list.removeAll(sub);
        } else {
          if (!batchSaveData(list, type)) {
            throw new GlobalErrorException("999668", "提交数据有误");
          }
          list.clear();
        }
      } while (list.size() > 0);
    } else {
      if (!batchSaveData(list, type)) {
        throw new GlobalErrorException("999668", "提交数据有误");
      }
    }
  }


  /**
   * 批量处理增加数据
   *
   * @return boolean
   */
  private boolean batchSaveData(List<Map<String, Object>> list, int type) {
    if (list == null || list.size() == 0) {
      throw new GlobalErrorException("999997", "提交数据异常");
    }
    log.info("batch list size: " + list.size());
    Long initId = null;
    Map<String, Object> params = new HashMap<>();
    params.put("num", list.size());
    params.put("seqName", type == 1 ? "AJGLQBXSBATCH" : "AJGLQBXSINFOBATCH");
    try {
      //修改sequence 表自增id
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "SEQUENCEUPDATE");
      Map<String, Object> map = (Map<String, Object>) baseService.get(params);
      if (map == null) {
        log.error("batch save get " + (type == 1 ? "AJGLQBXSBATCH" : "AJGLQBXSINFOBATCH") + " id is null");
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
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, type == 1 ? "AJGLQBXSBATCH" : "AJGLQBXSINFOBATCH");
      baseService.save(params);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

}
