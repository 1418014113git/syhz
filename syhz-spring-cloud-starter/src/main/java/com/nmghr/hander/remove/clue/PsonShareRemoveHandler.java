package com.nmghr.hander.remove.clue;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractRemoveHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * 线索分享删除
 */
@Service("psonShareRemoveHandler")
public class PsonShareRemoveHandler extends AbstractRemoveHandler {

    public PsonShareRemoveHandler(IBaseService baseService) {
        super(baseService);
    }

    @Override
    @Transactional
    public void remove(Map<String, Object> body) throws Exception {
        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "QBXXCLUESHAREPSONDELETEBYID");
        baseService.remove(body);
    }
}
