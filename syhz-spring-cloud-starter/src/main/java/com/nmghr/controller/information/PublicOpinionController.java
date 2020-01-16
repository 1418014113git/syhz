/**
 *  * Created by zj on 2020/1/14
 *  * <p/>
 *  * Copyright (c) 2015-2015
 *  * Apache License
 *  * Version 2.0, January 2004
 *  * http://www.apache.org/licenses/
 *  
 */
package com.nmghr.controller.information;


import com.nmghr.basic.common.Constant;
import com.nmghr.basic.common.Result;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.util.ValidationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("publicOpinion")
public class PublicOpinionController {

    @Autowired
    @Qualifier("baseService")
    private IBaseService baseService;


    // 查询单个舆情信息
    @GetMapping("/getOne")
    @ResponseBody
    public Object getOne(@RequestParam Map<String, Object> requestParam) throws Exception {
        ValidationUtils.notNull(requestParam.get("id"), "id不能为空!");
        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "PUBLICOPINION");
        Object obj = baseService.get(requestParam.get("id").toString());
        return Result.ok(obj);
    }
}
