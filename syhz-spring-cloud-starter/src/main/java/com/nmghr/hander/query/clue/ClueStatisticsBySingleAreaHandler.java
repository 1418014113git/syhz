/**
 * Created by wrx on 2020/1/10
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
 * 线索区域统计——单一区域（环状图）
 */
@Service("clueStatisticsBySingleAreaHandler")
public class ClueStatisticsBySingleAreaHandler extends AbstractQueryHandler {

    public ClueStatisticsBySingleAreaHandler(IBaseService baseService) {
        super(baseService);
    }

    @Override
    public Object list(Map<String, Object> requestMap) throws Exception {
        if (ObjectUtils.isEmpty(requestMap.get("dataStatus"))) {
            requestMap.put("dataStatus", ClueQueryHandler.DATA_STATUS_YES);
        }
        List<Map<String, Object>> dictList = null;
        Map<String, Object> rmap = new HashMap();
        // 查询所有线索分类
        rmap.put("codelx", "xsfl");
        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "CODENAMEQUERY");
        List<Object> legendList = new ArrayList();
        dictList = (List)baseService.list(rmap);
        List<Map<String,Object>> queryList = null;
        List<Map<String,Object>> clueTypeList = new ArrayList();;
        List seriesData = new ArrayList();
        if (ObjectUtils.isEmpty(requestMap.get("clueType"))){
            // 查询所有分类统计
            for (Map dictMap : dictList){
                requestMap.put("clueType",dictMap.get("code"));
                LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "QBXXCLUESTATISTICSBYSINGLEAREAONE");
                queryList =  (List)baseService.list(requestMap);
                clueTypeList.add(queryList.get(0));
            }
            requestMap.remove("clueType");
        } else {
            // 查询指定分类统计
            for (Map dictMap : dictList){
                if (dictMap.get("code").equals(requestMap.get("clueType")+"")){
                    requestMap.put("clueType",dictMap.get("code"));
                    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "QBXXCLUESTATISTICSBYSINGLEAREAONE");
                    queryList =  (List)baseService.list(requestMap);
                    clueTypeList.add(queryList.get(0));
                }
            }
        }
        seriesData.add(clueTypeList);
        // 查询所有线索来源
        rmap.put("codelx", "xsly");
        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "CODENAMEQUERY");
        dictList = (List)baseService.list(rmap);
        List<Map<String,Object>> clueSourceList = new ArrayList();
        if (ObjectUtils.isEmpty(requestMap.get("clueSource"))){
            // 查询所有来源统计
            for (Map dictMap : dictList){
                requestMap.put("clueSource",dictMap.get("code"));
                LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "QBXXCLUESTATISTICSBYSINGLEAREATWO");
                queryList =  (List)baseService.list(requestMap);
                clueSourceList.add(queryList.get(0));
            }
            requestMap.remove("clueSource");
        } else {
            // 查询指定来源统计
            for (Map dictMap : dictList){
                if (dictMap.get("code").equals(requestMap.get("clueSource")+"") ){
                    requestMap.put("clueSource",dictMap.get("code"));
                    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "QBXXCLUESTATISTICSBYSINGLEAREATWO");
                    queryList =  (List)baseService.list(requestMap);
                    clueSourceList.add(queryList.get(0));
                }
            }
        }
        seriesData.add(clueSourceList);
        Map resultMap = new HashMap();
        resultMap.put("seriesData", seriesData);
        return resultMap;
    }
}
