package com.nmghr.hander.query;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.page.Paging;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractQueryHandler;

@Service("taskCountQueryHandler")
public class TaskQbxsAjCountQueryHandler extends AbstractQueryHandler {

  public TaskQbxsAjCountQueryHandler(IBaseService baseService) {
    super(baseService);
    // TODO Auto-generated constructor stub
  }

  @Override
  public Object list(Map<String, Object> requestMap) throws Exception {
    // 任务
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJSPECIALTASKCOUNT");
    List<Map<String, Object>> tlist = (List<Map<String, Object>>) super.list(requestMap);
    // 线索
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "QBXSJBXXCOUNT");
    List<Map<String, Object>> xlist = (List<Map<String, Object>>) super.list(requestMap);
    // 案件
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJJBXXSYHCOUNT");
    List<Map<String, Object>> alist = (List<Map<String, Object>>) super.list(requestMap);
    // 提取key
    List<Long> tNum = getNumList(tlist, requestMap);
    List<Long> xNum = getNumList(xlist, requestMap);
    List<Long> aNum = getNumList(alist, requestMap);

    // 封装结果
    Map<String, Object> result = new HashMap<String, Object>();
    result.put("xAxis", sortKey(new ArrayList(getKey(requestMap).keySet())));
    result.put("tData", tNum);
    result.put("xData", xNum);
    result.put("aData", aNum);
    return result;
  }

  public List<Long> getNumList(List<Map<String, Object>> data, Map<String, Object> requestMap)
      throws ParseException {
    Map<String, Long> key = getKey(requestMap);
    List<Integer> list = new ArrayList<Integer>();
    for (Iterator iterator = data.iterator(); iterator.hasNext();) {
      Map<String, Object> map = (Map<String, Object>) iterator.next();
      if (map.get("date") != null && key.get(map.get("date")) != null) {
        key.put((String) map.get("date"), (Long) map.get("num"));
      }
    }
    return new ArrayList(key.values());
  }

  public Map<String, Long> getKey(Map<String, Object> requestMap) throws ParseException {
    String startTime = (String) requestMap.get("startTime");
    String endTime = (String) requestMap.get("endTime");

    Map<String, Long> key = new TreeMap<String, Long>();
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
    Calendar cale = Calendar.getInstance();
    Date startDate = null;
    Date endDate = null;
    if (StringUtils.isNotBlank(endTime)) {
      startDate = format.parse(startTime);
      endDate = format.parse(endTime);
      cale.setTime(endDate);
    }
    cale.add(Calendar.MONTH, 1);
    for (int i = 0; i < 12; i++) {
      cale.add(Calendar.MONTH, -1);
      Date date = cale.getTime();
      if (startDate != null && startDate.getTime() > cale.getTime().getTime()) {
        break;
      }
      String dateStr = format.format(date);
      key.put(dateStr, 0L);
    }
    return key;
  }

  private List<String> sortKey(List<String> key) {
    for (int i = 0; i < key.size(); i++) {
      key.set(i, Integer.parseInt(key.get(i).substring(key.get(i).indexOf("-") + 1)) + "月");
    }
    return key;
  }



}
