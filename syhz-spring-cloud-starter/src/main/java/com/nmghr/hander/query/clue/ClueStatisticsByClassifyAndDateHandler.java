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
import com.nmghr.basic.core.service.handler.IQueryHandler;
import com.nmghr.basic.core.service.handler.impl.AbstractQueryHandler;
import com.nmghr.basic.core.util.SpringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 线索分类统计——按分类和时间统计
 */
@Service("clueStatisticsByClassifyAndDateHandler")
public class ClueStatisticsByClassifyAndDateHandler extends AbstractQueryHandler {

    public ClueStatisticsByClassifyAndDateHandler(IBaseService baseService) {
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
            DateList = getDaysList(startTime, endTime);
        } else if ("2".equals(requestMap.get("timeDimensionType").toString()) ||
                "3".equals(requestMap.get("timeDimensionType").toString())){
            // 获取月份
            DateList = getMonthList(startTime, endTime,"yyyy-MM", Calendar.MONTH);
        } else if ("4".equals(requestMap.get("timeDimensionType").toString())){
            // 获取季度
            DateList = getQuarterList(startTime, endTime);
        } else {
            // 获取年份
            DateList = getMonthList(startTime, endTime, "yyyy", Calendar.YEAR);
        }
        requestMap.put("dateList", DateList);

        Map<String, Object> rmap = new HashMap();
        rmap.put("codelx", "xsfl");
        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "CODENAMEQUERY");
        List<Map<String, Object>> dictList = (List)baseService.list(rmap);
        List seriesDataList = new ArrayList();
        for (Map dictMap : dictList){
            requestMap.put("clueType",dictMap.get("code"));
            LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "QBXXCLUESTATISTICSBYCLASSIFYANDDATE");
            // 查询结果数组
            List<Map<String,Object>> queryList =  (List)baseService.list(requestMap);
            Map seriesDateMap = new HashMap();
            List result = new ArrayList<>();
            for(Map qmap : queryList){
                result.add(qmap.get("countNum"));
            }
            seriesDateMap.put("name",dictMap.get("code_name"));
            seriesDateMap.put("data", result);
            seriesDataList.add(seriesDateMap);
        }
        resultMap.put("seriesData", seriesDataList);
        resultMap.put("total", 0);
        return resultMap;
    }

    /**
     * 获取时间段内的所有日期
     * @param begintTime
     * @param endTime
     * @return
     */
    public static List<String> getDaysList(Long begintTime, Long endTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date dBegin = new Date(begintTime);
        Date dEnd = new Date(endTime);
        List<String> daysStrList = new ArrayList<String>();
        daysStrList.add(sdf.format(dBegin));
        Calendar calBegin = Calendar.getInstance();
        calBegin.setTime(dBegin);
        Calendar calEnd = Calendar.getInstance();
        calEnd.setTime(dEnd);
        while (dEnd.after(calBegin.getTime())) {
            calBegin.add(Calendar.DAY_OF_MONTH, 1);
            String dayStr = sdf.format(calBegin.getTime());
            daysStrList.add(dayStr);
        }
        return daysStrList;
    }

    /**
     * 获取时间段内的月份/年份
     * @param begintTime
     * @param endTime
     * @param sdfStr
     * @param field
     * @return
     */
    public static List<String> getMonthList(Long begintTime, Long endTime, String sdfStr, int field){
        SimpleDateFormat sdf = new SimpleDateFormat(sdfStr);
        ArrayList<String> result = new ArrayList<String>();
        Calendar min = Calendar.getInstance();
        Calendar max = Calendar.getInstance();
        min.setTime(new Date(begintTime));
        min.set(min.get(Calendar.YEAR), min.get(Calendar.MONTH), 1);
        max.setTime(new Date(endTime));
        max.set(max.get(Calendar.YEAR), max.get(Calendar.MONTH), 2);

        Calendar curr = min;
        while (curr.before(max)) {
            result.add(sdf.format(curr.getTime()));
            curr.add(field, 1);
        }
        return result;
    }

    /**
     * 获取时间段内的所有季度
     * @param beginDate
     * @param endDate
     * @return
     */
    public static List<String> getQuarterList(Long beginDate,Long endDate){
        List<String> rangeSet = null;
        SimpleDateFormat sdf = null;
        Date begin_date = null;
        Date end_date = null;
        String[] numStr = null;
        String Q = null;
        String last = "";
        rangeSet = new java.util.ArrayList<String>();
        sdf = new SimpleDateFormat("yyyy-MM");
        begin_date = new Date(beginDate);//定义起始日期
        end_date = new Date(endDate);//定义结束日期
        Calendar dd = Calendar.getInstance();//定义日期实例
        dd.setTime(begin_date);//设置日期起始时间
        while(!dd.getTime().after(end_date)){//判断是否到结束日期
            numStr=  sdf.format(dd.getTime()).split("-",0);
            Q = numStr[0].toString()+"-" + getQuarter(Integer.valueOf(numStr[1]));
            if(!last.equals(Q)) {
                rangeSet.add(Q);
                last = Q;
            }
            dd.add(Calendar.MONTH, 1);//进行当前日期月份加1
        }
        return rangeSet;
    }


    /**
     * 判断某月在哪一季度
     * @param month
     * @return
     */
    private static int getQuarter(int month) {
        if(month == 1 || month == 2 || month == 3){
            return 1;
        }else if(month == 4 || month == 5 || month == 6){
            return  2;
        }else if(month == 7 || month == 8 || month == 9){
            return 3;
        }else{
            return 4;
        }
    }

}
