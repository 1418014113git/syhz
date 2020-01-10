/**
 * Created by wrx on 2020/1/9
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 线索区域统计——多区域（柱状图）
 */
@Service("clueStatisticsByAreaHandler")
public class ClueStatisticsByAreaHandler extends AbstractQueryHandler {

    public ClueStatisticsByAreaHandler(IBaseService baseService) {
        super(baseService);
    }

    @Override
    public Object list(Map<String, Object> requestMap) throws Exception {
        if (ObjectUtils.isEmpty(requestMap.get("dataStatus"))) {
            requestMap.put("dataStatus", ClueQueryHandler.DATA_STATUS_YES);
        }
        List areaList = null;
        List<Map<String, Object>> dictList = null;
        Map<String, Object> rmap = new HashMap();
        List seriesDataList = new ArrayList();
        // 查询所有线索分类
        rmap.put("codelx", "xsfl");
        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "CODENAMEQUERY");
        dictList = (List)baseService.list(rmap);
        if (ObjectUtils.isEmpty(requestMap.get("clueType"))){
            // 查询所有分类统计
            for (Map dictMap : dictList){
                areaList = assembly("clueType", requestMap, dictMap, seriesDataList);
            }
            requestMap.remove("clueType");
        } else {
            // 查询指定分类统计
            for (Map dictMap : dictList){
                if (dictMap.get("code").equals(requestMap.get("clueType")+"")){
                    areaList = assembly("clueType", requestMap, dictMap, seriesDataList);
                }
            }
        }
        // 查询所有线索来源
        rmap.put("codelx", "xsly");
        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "CODENAMEQUERY");
        dictList = (List)baseService.list(rmap);
        if (ObjectUtils.isEmpty(requestMap.get("clueSource"))){
            // 查询所有来源统计
            for (Map dictMap : dictList){
                assembly("clueSource", requestMap, dictMap, seriesDataList);
            }
            requestMap.remove("clueSource");
        } else {
            // 查询指定来源统计
            for (Map dictMap : dictList){
                if (dictMap.get("code").equals(requestMap.get("clueSource")+"") ){
                    assembly("clueSource", requestMap, dictMap, seriesDataList);
                }
            }
        }
        Map resultMap = new HashMap();
        resultMap.put("XData", areaList);
        resultMap.put("seriesData", seriesDataList);
        return resultMap;
    }

    /**
     * 查询拼装数据结构
     * @param condition
     * @param requestMap
     * @param dictMap
     * @param seriesDataList
     * @throws Exception
     */
    private List assembly(String condition, Map<String, Object> requestMap, Map<String, Object> dictMap, List seriesDataList) throws Exception{
        requestMap.put(condition,dictMap.get("code"));
        List areaList = new ArrayList();
        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "QBXXCLUESTATISTICSBYAREA");
        // 查询结果数组
        List<Map<String,Object>> queryList =  (List)baseService.list(requestMap);
        Map seriesDateMap = new HashMap();
        List result = new ArrayList<>();
        for(Map qmap : queryList){
            result.add(qmap.get("countNum"));
            areaList.add(qmap.get("cityName"));
        }
        seriesDateMap.put("name",dictMap.get("code_name"));
        seriesDateMap.put("data", result);
        seriesDateMap.put("stack", condition);
        seriesDataList.add(seriesDateMap);
        return areaList;
    }
}
