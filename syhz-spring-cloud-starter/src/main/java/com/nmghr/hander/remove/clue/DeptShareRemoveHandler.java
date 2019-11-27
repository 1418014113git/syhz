package com.nmghr.hander.remove.clue;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractRemoveHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * 线索分享删除
 */
@Service("deptShareRemoveHandler")
public class DeptShareRemoveHandler extends AbstractRemoveHandler {

    public DeptShareRemoveHandler(IBaseService baseService) {
        super(baseService);
    }

    @Override
    @Transactional
    public void remove(Map<String, Object> body) throws Exception {
        if(StringUtils.isEmpty(body.get("shareDepartmentId"))){
            LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "QBXXCLUESHAREDEPTDELTE");
            baseService.remove(body);
        }else{
            LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "QBXXCLUESHAREPSONDELETE");
            baseService.remove(body);
        }

    }
}
