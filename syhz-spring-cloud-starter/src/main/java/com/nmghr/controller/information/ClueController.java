/**
 * Created by wrx on 2019/11/19
 * <p/>
 * Copyright (c) 2015-2015
 * Apache License
 * Version 2.0, January 2004
 * http://www.apache.org/licenses/
 */
package com.nmghr.controller.information;

import com.nmghr.basic.core.service.handler.IQueryHandler;
import com.nmghr.basic.core.util.SpringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("clue")
public class ClueController {

    /**
     * 查询线索列表
     *
     * @param requestParam
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/page")
    public Object queryByPage(@RequestBody Map<String, Object> requestParam) throws Exception {
        int currentPage = Integer.valueOf(requestParam.get("currentPage") + "");
        int pageSize = Integer.valueOf(requestParam.get("pageSize") + "");
        IQueryHandler queryHandler = SpringUtils.getBean("clueQueryHandler", IQueryHandler.class);
        return queryHandler.page(requestParam, currentPage, pageSize);
    }
}
