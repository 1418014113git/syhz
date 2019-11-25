package com.nmghr.hander.query.clue;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractQueryHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * 查询线索
 */
@Service("clueShareQueryHandler")
public class ClueShareQueryHandler extends AbstractQueryHandler {

    public ClueShareQueryHandler(IBaseService baseService) {
        super(baseService);
    }

    @Override
    @Transactional
    public Object list(Map<String, Object> requestMap) throws Exception {
        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "QBXXCLUESHAREPSONDETAIL");
        return baseService.list(requestMap);
    }
}
