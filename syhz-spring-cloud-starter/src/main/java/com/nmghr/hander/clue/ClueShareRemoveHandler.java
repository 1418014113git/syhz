package com.nmghr.hander.clue;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractRemoveHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * 线索分享删除
 */
@Service("clueShareRemoveHandler")
public class ClueShareRemoveHandler extends AbstractRemoveHandler {

    public ClueShareRemoveHandler(IBaseService baseService) {
        super(baseService);
    }

    @Override
    @Transactional
    public void remove(Map<String, Object> body) throws Exception {
        Map<String, Object> p = new HashMap<>();
        p.put("clueId", body.get("clueId"));
        p.put("receiveDeptId", body.get("deptId"));
        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "QBXXCLUESHAREDEPT");
        baseService.remove(p);
        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "QBXXCLUESHAREPSON");
        baseService.remove(p);

    }
}
