package com.nmghr.hander.save;

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

/**
 * 保存试卷
 */
@Service("paperSaveHandler")
public class PaperSaveHandler extends AbstractSaveHandler {
  private Logger log = LoggerFactory.getLogger(PaperSaveHandler.class);

  public PaperSaveHandler(IBaseService baseService) {
    super(baseService);
  }

  /**
   * #{paperName}, #{paperType}, #{creator}, #{deptCode}, #{deptName}, #{remark}
   * @param requestBody
   * @return
   */
  @SuppressWarnings("unchecked")
  @Override
  @Transactional
  public Object save(Map<String, Object> requestBody) {
    if (requestBody.get("from")==null) {
      throw new GlobalErrorException("999997", "参数不正确");
    }
    try{
      Map<String, Object> params = new HashMap<String, Object>();
      params.put("paperName", requestBody.get("paperName"));
      params.put("paperType", requestBody.get("paperType"));
      params.put("creator", requestBody.get("creator"));
      params.put("deptCode", requestBody.get("deptCode"));
      params.put("deptName", requestBody.get("deptName"));
      params.put("remark", requestBody.get("remark"));
      if (requestBody.get("paperStatus") == null) {
        params.put("paperStatus", 1);
      }
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMPAPER");
      Object paperObj = baseService.save(params);
      if (paperObj == null) {
        throw new GlobalErrorException("999997", "保存数据异常");
      }
      if(batchSaveData(requestBody, paperObj)){
        return paperObj;
      }
    }catch (Exception e){
      log.error("paperSaveHandler ERROR : " + e.getMessage());
    }
    return false;
  }

  /**
   * 批量处理增加数据
   * #{id}, #{paperId}, #{subjectCategoryId}, #{questionsId}, #{type}, #{sort}, #{value}, #{creator}, #{deptCode}, #{deptName}
   * @param requestBody body
   * @return boolean
   */
  @SuppressWarnings("unchecked")
  private boolean batchSaveData(Map<String, Object> requestBody, Object paperId) {
    List<Map<String, Object>> list = (List<Map<String, Object>>) requestBody.get("questionList");
    log.info("batch list size: "+ list.size());
    Long initId = null;
    Map<String, Object> params = new HashMap<String, Object>();
    params.put("num", list.size());
    params.put("seqName", "EXAMPAPERINFO");
    try {
      //修改sequence 表自增id
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "SEQUENCEUPDATE");
      Map<String, Object> map = (Map<String, Object>) baseService.get(params);
      if (map==null) {
        log.error("batch save get EXAMPAPERINFO id is null");
        throw new GlobalErrorException("999997", "提交数据异常");
      }
      //计算初始id
      initId = Long.parseLong(String.valueOf(map.get("id"))) - list.size();
    } catch (Exception e) {
      log.error("batch save get SEQUENCEUPDATE list Error: "+ e.getMessage());
      throw new GlobalErrorException("999997", "提交数据有误");
    }

    //拼装需要的参数
    for (Map<String, Object> bean : list) {
      initId++;//增加ID
      bean.put("id", String.valueOf(initId));
      bean.put("paperId", paperId);
      bean.put("creator", requestBody.get("creator"));
      bean.put("deptCode", requestBody.get("deptCode"));
      bean.put("deptName", requestBody.get("deptName"));
    }
    params = new HashMap<>();
    params.put("list", list);
    try {
      //提交数据
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMPAPERINFOBATCH");
      baseService.save(params);
      return true;
    } catch (Exception e) {
      log.error("batch save list Error: "+ e.getMessage());
      throw new GlobalErrorException("999996", "提交数据有误");
    }
  }
}