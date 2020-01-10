/**
 * Created by wrx on 2020/1/7
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 线索来源统计——按来源
 */
@Service("clueStatisticsBySourceHandler")
public class ClueStatisticsBySourceHandler extends AbstractQueryHandler {

    public ClueStatisticsBySourceHandler(IBaseService baseService) {
        super(baseService);
    }

    @Override
    public Object list(Map<String, Object> requestMap) throws Exception {
        if (ObjectUtils.isEmpty(requestMap.get("dataStatus"))) {
            requestMap.put("dataStatus", ClueQueryHandler.DATA_STATUS_YES);
        }
        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "QBXXCLUESTATISTICSBYSOURCE");
        List<Map<String,Object>> queryList =  (List)baseService.list(requestMap);
        Map resultMap = new HashMap();
        resultMap.put("total", 0);
        resultMap.put("seriesData", queryList);
        return resultMap;
    }
}
