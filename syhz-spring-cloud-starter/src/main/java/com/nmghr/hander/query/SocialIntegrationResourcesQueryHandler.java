package com.nmghr.hander.query;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractQueryHandler;

@Service("socialintegrationresourcesQueryHandler")
public class SocialIntegrationResourcesQueryHandler extends AbstractQueryHandler {

  public SocialIntegrationResourcesQueryHandler(IBaseService baseService) {
    super(baseService);
  }

  @SuppressWarnings("unchecked")
  @Override
  public Object list(Map<String, Object> map) throws Exception {
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "TWYCT");// 医疗机构患者信息
    Map<String, Object> twymsg = (Map<String, Object>) baseService.get("");
    twymsg.put("from", "医疗机构患者信息");
    twymsg.put("unit", "卫计委");
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "TWXCT");// 新生儿信息
    Map<String, Object> twxmsg = (Map<String, Object>) baseService.get("");
    twxmsg.put("from", "新生儿信息");
    twxmsg.put("unit", "卫计委");
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "TSYCT");// 食药检的医疗机构信息
    Map<String, Object> tsymsg = (Map<String, Object>) baseService.get("");
    tsymsg.put("from", "医疗机构信息");
    tsymsg.put("unit", "食药局");
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "ACOCT");// 检验机构信息
    Map<String, Object> acomsg = (Map<String, Object>) baseService.get("");
    acomsg.put("from", "检验机构信息");
    acomsg.put("unit", "食药局");
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "TSQCT");// 行政许可信息
    Map<String, Object> tsqmsg = (Map<String, Object>) baseService.get("");
    tsqmsg.put("from", "行政许可信息");
    tsqmsg.put("unit", "食药局");
    // -------------------------------------------------------------
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "THBTFSPFDAY");// 废水废气排放日信息
    Map<String, Object> fspfday = (Map<String, Object>) baseService.get("");
    fspfday.put("from", "废水废气排放日信息");
    fspfday.put("unit", "环保厅");
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "THBTGYWRYQD");// 工业污染源名录及污染物排放清单
    Map<String, Object> gywryqd = (Map<String, Object>) baseService.get("");
    gywryqd.put("from", "工业污染源名录及污染物排放清单信息");
    gywryqd.put("unit", "环保厅");
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "THBTPWSBQYXX");// 排污申报企业信息
    Map<String, Object> pwsbqyxx = (Map<String, Object>) baseService.get("");
    pwsbqyxx.put("from", "排污申报企业信息");
    pwsbqyxx.put("unit", "环保厅");
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "THBTQYJBXX");// 污染严重企业信息
    Map<String, Object> qyjbxx = (Map<String, Object>) baseService.get("");
    qyjbxx.put("from", "污染严重企业信息");
    qyjbxx.put("unit", "环保厅");
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "THBTQZZX");// 申请法院强制执行信息
    Map<String, Object> qzzx = (Map<String, Object>) baseService.get("");
    qzzx.put("from", "申请法院强制执行信息");
    qzzx.put("unit", "环保厅");
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "THBTXZCFXX");// 行政处罚信息
    Map<String, Object> xzcfxx = (Map<String, Object>) baseService.get("");
    xzcfxx.put("from", "行政处罚信息");
    xzcfxx.put("unit", "环保厅");
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "THBTYZCBPFQY");// 严重超标排放企业信息
    Map<String, Object> yzcbpfqy = (Map<String, Object>) baseService.get("");
    yzcbpfqy.put("from", "严重超标排放企业信息");
    yzcbpfqy.put("unit", "环保厅");
    
    List<Map<String, Object>> result = new ArrayList<>();
    result.add(twymsg);
    result.add(twxmsg);
    result.add(tsymsg);
    result.add(acomsg);
    result.add(tsqmsg);
    result.add(fspfday);
    result.add(gywryqd);
    result.add(pwsbqyxx);
    result.add(qyjbxx);
    result.add(qzzx);
    result.add(xzcfxx);
    result.add(yzcbpfqy);
    return result;
  }

}
