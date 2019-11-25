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
@Service("clueUpdateHandler")
public class ClueUpdateHandler extends AbstractUpdateHandler {

    public ClueUpdateHandler(IBaseService baseService) {
        super(baseService);
    }

    @Override
    @Transactional
    public Object update(String id, Map<String, Object> requestBody) throws Exception {
        requestBody.put("collectionLocation", requestBody.get("collectionLocation").toString());
        requestBody.put("collectionLocationLable", requestBody.get("collectionLocationLable").toString()); // 采集地点行政区划名称
        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "QBXXCLUE");
        baseService.update(id, requestBody);
        return true;
    }
}
