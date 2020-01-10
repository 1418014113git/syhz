/**
 * Created by wrx on 2020/1/8
 * <p/>
 * Copyright (c) 2015-2015
 * Apache License
 * Version 2.0, January 2004
 * http://www.apache.org/licenses/
 */
package com.nmghr.hander.query.clue;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractQueryHandler;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.*;

/**
 * 线索分类统计——按分类和时间统计
 */
@Service("clueStatisticsBySourceAndDateHandler")
public class ClueStatisticsBySourceAndDateHandler extends AbstractQueryHandler {

    public ClueStatisticsBySourceAndDateHandler(IBaseService baseService) {
        super(baseService);
    }

    @Override
    public Object list(Map<String, Object> requestMap) throws Exception {
        Map resultMap = new HashMap();
        if (ObjectUtils.isEmpty(requestMap.get("dataStatus"))) {
            requestMap.put("dataStatus", ClueQueryHandler.DATA_STATUS_YES);
        }
        Long startTime = Long.valueOf((String)requestMap.get("collectionDateStart"));
        Long endTime = Long.valueOf((String)requestMap.get("collectionDateEnd"));
        // 日期数组
        List<String> DateList = null;
        if ("1".equals(requestMap.get("timeDimensionType").toString())){
            // 获取日期
            DateList = ClueStatisticsByClassifyAndDateHandler.getDaysList(startTime, endTime);
        } else if ("2".equals(requestMap.get("timeDimensionType").toString()) ||
                "3".equals(requestMap.get("timeDimensionType").toString())){
            // 获取月份
            DateList = ClueStatisticsByClassifyAndDateHandler.getMonthList(startTime, endTime,"yyyy-MM", Calendar.MONTH);
        } else if ("4".equals(requestMap.get("timeDimensionType").toString())){
            // 获取季度
            DateList = ClueStatisticsByClassifyAndDateHandler.getQuarterList(startTime, endTime);
        } else {
            // 获取年份
            DateList = ClueStatisticsByClassifyAndDateHandler.getMonthList(startTime, endTime, "yyyy", Calendar.YEAR);
        }
        requestMap.put("dateList", DateList);

        Map<String, Object> rmap = new HashMap();
        rmap.put("codelx", "xsly");
        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "CODENAMEQUERY");
        List<Map<String, Object>> dictList = (List)baseService.list(rmap);
        List seriesDataList = new ArrayList();
        List<Object> legendList = new ArrayList();
        if (ObjectUtils.isEmpty(requestMap.get("clueSource"))){
            for (Map dictMap : dictList){
                requestMap.put("clueSource",dictMap.get("code"));
                LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "QBXXCLUESTATISTICSBYCLASSIFYANDDATE");
                // 查询结果数组
                List<Map<String,Object>> queryList =  (List)baseService.list(requestMap);
                Map seriesDateMap = new HashMap();
                List result = new ArrayList<>();
                for(Map qmap : queryList){
                    result.add(qmap.get("countNum"));
                }
                seriesDateMap.put("type","line");
                seriesDateMap.put("name",dictMap.get("code_name"));
                seriesDateMap.put("data", result);
                seriesDataList.add(seriesDateMap);
                legendList.add(dictMap.get("code_name"));
            }
        }else {
            LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "QBXXCLUESTATISTICSBYCLASSIFYANDDATE");
            // 查询结果数组
            List<Map<String,Object>> queryList =  (List)baseService.list(requestMap);
            Map seriesDateMap = new HashMap();
            List result = new ArrayList<>();
            for(Map qmap : queryList){
                result.add(qmap.get("countNum"));
            }
            seriesDateMap.put("type","line");
            for (Map dictMap : dictList){
                if (dictMap.get("code").equals(requestMap.get("clueSource"))){
                    seriesDateMap.put("name",dictMap.get("code_name"));
                    legendList.add(dictMap.get("code_name"));
                }
            }
            seriesDateMap.put("data", result);
            seriesDataList.add(seriesDateMap);
        }
        Object[] legendArray = legendList.toArray();
        resultMap.put("legendData",legendArray);
        resultMap.put("xData", DateList);
        resultMap.put("seriesData", seriesDataList);
        resultMap.put("total", 0);
        return resultMap;
    }
}
