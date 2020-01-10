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
        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "QBXXCLUESTATISTICSBYCLASSIFY");
        List<Map<String,Object>> queryList =  (List)baseService.list(requestMap);
        List seriesData = new ArrayList();
        seriesData.add(queryList);
        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "QBXXCLUESTATISTICSBYSOURCE");
        queryList =  (List)baseService.list(requestMap);
        seriesData.add(queryList);
        Map resultMap = new HashMap();
        resultMap.put("seriesData", seriesData);
        return resultMap;
    }
}
