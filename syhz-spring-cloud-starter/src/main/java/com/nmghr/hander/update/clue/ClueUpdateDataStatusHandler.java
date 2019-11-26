package com.nmghr.hander.update.clue;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractUpdateHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * 修改线索
 */
@Service("clueUpdateDataStatusHandler")
public class ClueUpdateDataStatusHandler extends AbstractUpdateHandler {

    public ClueUpdateDataStatusHandler(IBaseService baseService) {
        super(baseService);
    }

    @Override
    @Transactional
    public Object update(String id, Map<String, Object> requestBody) throws Exception {
        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "QBXXCLUDATASTATUSE");
        baseService.update(id, requestBody);
        return true;
    }
}
