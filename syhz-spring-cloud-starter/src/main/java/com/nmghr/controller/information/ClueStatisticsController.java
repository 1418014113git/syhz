/**
 * Created by wrx on 2020/1/7
 * <p/>
 * Copyright (c) 2015-2015
 * Apache License
 * Version 2.0, January 2004
 * http://www.apache.org/licenses/
 */
package com.nmghr.controller.information;

import com.nmghr.basic.common.exception.GlobalErrorException;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.IQueryHandler;
import com.nmghr.basic.core.util.SpringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("clueStatistics")
public class ClueStatisticsController {

    @Autowired
    @Qualifier("baseService")
    private IBaseService baseService;

    /**
     * 线索分类统计——按分类
     */
    @GetMapping("/byClassify")
    public Object statisticsByClassify(@RequestParam Map<String, Object> requestParam) throws Exception{
        if(requestParam.get("collectionDateStart") == null){
            throw new GlobalErrorException("998001", "采集开始时间不能为空");
        }
        if(requestParam.get("collectionDateEnd") == null){
            throw new GlobalErrorException("998001", "采集结束时间不能为空");
        }
        IQueryHandler queryHandler = SpringUtils.getBean("clueStatisticsByClassifyHandler", IQueryHandler.class);
        return queryHandler.list(requestParam);
    }

    /**
     * 线索分类统计——按分类和时间
     */
    @GetMapping("/byClassifyAndDate")
    public Object statisticsByClassifyAndDate(@RequestParam Map<String, Object> requestParam) throws Exception{
        if(requestParam.get("collectionDateStart") == null){
            throw new GlobalErrorException("998001", "采集开始时间不能为空");
        }
        if(requestParam.get("collectionDateEnd") == null){
            throw new GlobalErrorException("998001", "采集结束时间不能为空");
        }
        if(requestParam.get("timeDimensionType") == null){
            throw new GlobalErrorException("998001", "时间维度类型不能为空");
        }
        IQueryHandler queryHandler = SpringUtils.getBean("clueStatisticsByClassifyAndDateHandler", IQueryHandler.class);
        return queryHandler.list(requestParam);
    }



    /**
     * 线索来源统计——按来源和时间
     */
    @GetMapping("/bySourceAndDate")
    public Object statisticsBySourceAndDate(@RequestParam Map<String, Object> requestParam) throws Exception{
        if(requestParam.get("collectionDateStart") == null){
            throw new GlobalErrorException("998001", "采集开始时间不能为空");
        }
        if(requestParam.get("collectionDateEnd") == null){
            throw new GlobalErrorException("998001", "采集结束时间不能为空");
        }
        if(requestParam.get("timeDimensionType") == null){
            throw new GlobalErrorException("998001", "时间维度类型不能为空");
        }
        IQueryHandler queryHandler = SpringUtils.getBean("clueStatisticsBySourceAndDateHandler", IQueryHandler.class);
        return queryHandler.list(requestParam);
    }

    /**
     * 线索来源统计——按来源
     */
    @GetMapping("/bySource")
    public Object statisticsBySource(@RequestParam Map<String, Object> requestParam) throws Exception{
        if(requestParam.get("collectionDateStart") == null){
            throw new GlobalErrorException("998001", "采集开始时间不能为空");
        }
        if(requestParam.get("collectionDateEnd") == null){
            throw new GlobalErrorException("998001", "采集结束时间不能为空");
        }
        IQueryHandler queryHandler = SpringUtils.getBean("clueStatisticsBySourceHandler", IQueryHandler.class);
        return queryHandler.list(requestParam);
    }

    /**
     * 线索区域统计——多区域
     */
    @RequestMapping("/byArea")
    public Object statisticsByArea(@RequestBody Map<String, Object> requestParam) throws Exception{
        if(requestParam.get("collectionDateStart") == null){
            throw new GlobalErrorException("998001", "采集开始时间不能为空");
        }
        if(requestParam.get("collectionDateEnd") == null){
            throw new GlobalErrorException("998001", "采集结束时间不能为空");
        }
        if(requestParam.get("area") == null){
            throw new GlobalErrorException("998001", "区域集合不能为空");
        }
        IQueryHandler queryHandler = SpringUtils.getBean("clueStatisticsByAreaHandler", IQueryHandler.class);
        return queryHandler.list(requestParam);
    }

    /**
     * 线索区域统计——单一区域
     */
    @RequestMapping("/bySingleArea")
    public Object statisticsBySingleArea(@RequestParam Map<String, Object> requestParam) throws Exception{
        if(requestParam.get("collectionDateStart") == null){
            throw new GlobalErrorException("998001", "采集开始时间不能为空");
        }
        if(requestParam.get("collectionDateEnd") == null){
            throw new GlobalErrorException("998001", "采集结束时间不能为空");
        }
        if(requestParam.get("collectionArea") == null){
            throw new GlobalErrorException("998001", "区域集合不能为空");
        }
        IQueryHandler queryHandler = SpringUtils.getBean("clueStatisticsBySingleAreaHandler", IQueryHandler.class);
        return queryHandler.list(requestParam);
    }

    /**
     * 查询总条数
     * @param requestParam
     * @return
     * @throws Exception
     */
    @GetMapping("/total")
    public Object statisticsByTotal(@RequestParam Map<String, Object> requestParam) throws Exception{
        if(requestParam.get("collectionDateStart") == null){
            throw new GlobalErrorException("998001", "采集开始时间不能为空");
        }
        if(requestParam.get("collectionDateEnd") == null){
            throw new GlobalErrorException("998001", "采集结束时间不能为空");
        }
        IQueryHandler queryHandler = SpringUtils.getBean("ClueStatisticsByTotalHandler", IQueryHandler.class);
        return queryHandler.list(requestParam);
    }

}
