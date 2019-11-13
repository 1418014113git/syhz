package com.nmghr.hander.save;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.common.Result;
import com.nmghr.basic.common.exception.GlobalErrorException;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractSaveHandler;
import com.nmghr.basic.core.util.ValidationUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("groupSaveHandler")
public class GroupSaveHandler extends AbstractSaveHandler {

    public GroupSaveHandler(IBaseService baseService) {
        super(baseService);
    }

    @Override
    @Transactional
    public Object save(Map<String, Object> requestBody) throws Exception {

        validate(requestBody);

        //组名去重
        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "BASEGROUPNAMECHECK");
        Map<String,Object> num = (Map<String, Object>) baseService.get(requestBody);
        if(num!=null){
            if(Integer.valueOf(String.valueOf(num.get("num"))) > 0)
            throw new GlobalErrorException("998001", "组名重复，请确认后重新输入！");
        }

        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "BASEGROUP");
        Integer id = (Integer) baseService.save(requestBody);

        List<Integer> deptIds = (List<Integer>) requestBody.get("deptIds");

        if (deptIds != null && deptIds.size() > 0) {
            for (Integer deptId : deptIds) {
                Map<String, Object> param = new HashMap<>();
                param.put("groupId", id);
                param.put("itemId", deptId);
                param.put("itemType", 2);
                //存储明细表即组员信息
                LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "BASEGROUPDETAIL");
                baseService.save(param);
            }
        }
        return Result.ok(id);
    }

    private void validate(Map<String, Object> requestBody) {
        ValidationUtils.notNull(String.valueOf(requestBody.get("groupName")), "组名不能为空");
        ValidationUtils.notNull(String.valueOf(requestBody.get("deptIds")), "组成员不能为空");
    }

}
