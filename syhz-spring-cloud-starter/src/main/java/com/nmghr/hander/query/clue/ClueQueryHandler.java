package com.nmghr.hander.query.clue;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractQueryHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.Map;

/**
 * 查询线索
 */
@Service("clueQueryHandler")
public class ClueQueryHandler extends AbstractQueryHandler {

    /** 是否启用-启用 **/
    private static final String DATA_STATUS_YES = "1";
    /** 是否启用-禁用 **/
    private static final String DATA_STATUS_NO = "0";

    public ClueQueryHandler(IBaseService baseService) {
        super(baseService);
    }

    @Override
    @Transactional
    public Object list(Map<String, Object> requestMap) throws Exception {
        requestMap.put("collectionLocation", requestMap.get("collectionLocation").toString());
        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "QBXXCLUE");
        return baseService.list(requestMap);
    }
    /**
     * 分页查询
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
