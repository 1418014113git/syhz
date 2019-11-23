/**
 * Created by wrx on 2019/11/20
 * <p/>
 * Copyright (c) 2015-2015
 * Apache License
 * Version 2.0, January 2004
 * http://www.apache.org/licenses/
 */
package com.nmghr.handler.query;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractQueryHandler;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Map;

@Service("clueQueryHandler")
public class ClueQueryHandler extends AbstractQueryHandler {

    /** 是否启用-启用 **/
    private static final String DATA_STATUS_YES = "1";
    /** 是否启用-禁用 **/
    private static final String DATA_STATUS_NO = "0";

    public ClueQueryHandler(IBaseService baseService) {
        super(baseService);
    }

    /**
     * 疑难问答列表查询
     *
     * @param requestMap
     * @param currentPage
     * @param pageSize
     * @return
     * @throws Exception
     */
    @Override
    public Object page(Map<String, Object> requestMap, int currentPage, int pageSize) throws Exception {
        if (ObjectUtils.isEmpty(requestMap.get("dataStatus"))) {
            requestMap.put("dataStatus", DATA_STATUS_YES);
        }
        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "QBXXCLUE");
        return baseService.page(requestMap, currentPage, pageSize);
    }
}
