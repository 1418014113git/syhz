/**
 * Created by wrx on 2019/11/19
 * <p/>
 * Copyright (c) 2015-2015
 * Apache License
 * Version 2.0, January 2004
 * http://www.apache.org/licenses/
 */
package com.nmghr.controller.information;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.common.Result;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.IQueryHandler;
import com.nmghr.basic.core.service.handler.IRemoveHandler;
import com.nmghr.basic.core.service.handler.ISaveHandler;
import com.nmghr.basic.core.service.handler.IUpdateHandler;
import com.nmghr.basic.core.util.SpringUtils;
import com.nmghr.basic.core.util.ValidationUtils;
import com.sargeraswang.util.ExcelUtil.ExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("clue")
public class ClueController {
    @Autowired
    @Qualifier("baseService")
    private IBaseService baseService;

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

    // 添加线索
    @PutMapping("/save")
    @ResponseBody
    public Object save(@RequestBody Map<String, Object> body) throws Exception {
        validParams(body);
        ISaveHandler saveHandler = SpringUtils.getBean("clueSaveHandler", ISaveHandler.class);
        Object obj = saveHandler.save(body);
        return Result.ok(obj);
    }

    // 分享线索
    @PutMapping("/share")
    @ResponseBody
    public Object share(@RequestBody Map<String, Object> body) throws Exception {
        ISaveHandler saveHandler = SpringUtils.getBean("clueShareHandler", ISaveHandler.class);
        Object obj = saveHandler.save(body);
        return Result.ok(obj);
    }

    // 查询单个线索
    @GetMapping("/getOne")
    @ResponseBody
    public Object getOne(@RequestParam Map<String, Object> body) throws Exception {
        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "QBXXCLUE");
        Object obj = baseService.get(body.get("id").toString());
        return Result.ok(obj);
    }
    // 查询单个线索的分享记录
    @GetMapping("/getShareDeptByClueId")
    @ResponseBody
    public Object getShareDeptByClueId(@RequestParam Map<String, Object> body) throws Exception {
        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "QBXXCLUESHAREDEPTDETAIL");
        Object obj = baseService.list(body);
        return Result.ok(obj);
    }
    // 查询单个线索的分享记录
    @GetMapping("/getSharePsonByDeptId")
    @ResponseBody
    public Object getSharePsonByClueId(@RequestParam Map<String, Object> body) throws Exception {
        IQueryHandler queryHandler = SpringUtils.getBean("clueShareQueryHandler", IQueryHandler.class);
        Object obj = queryHandler.list(body);
        return Result.ok(obj);
    }

    // 修改线索
    @PostMapping("/update")
    @ResponseBody
    public Object update(@RequestBody Map<String, Object> body) throws Exception {
        validId(body.get("id"));
        validParams(body);
        IUpdateHandler updateHandler = SpringUtils.getBean("clueUpdateHandler", IUpdateHandler.class);
        Object obj = updateHandler.update(String.valueOf(body.get("id")), body);
        return Result.ok(obj);
    }

    // 修改线索数据状态
    @PostMapping("/updateDataStatus")
    @ResponseBody
    public Object updateDataStatus(@RequestBody Map<String, Object> body) throws Exception {
        validId(body.get("id"));
        IUpdateHandler updateHandler = SpringUtils.getBean("clueUpdateDataStatusHandler", IUpdateHandler.class);
        Object obj = updateHandler.update(String.valueOf(body.get("id")), body);
        return Result.ok(obj);
    }

    // 撤回线索分享
    @PostMapping("/shareRemove")
    @ResponseBody
    public Object shareRemove(@RequestBody Map<String, Object> body) throws Exception {
        IRemoveHandler removeHandler = SpringUtils.getBean("clueShareRemoveHandler", IRemoveHandler.class);
        removeHandler.remove(body);
        return Result.ok("");
    }

    // 撤回线索分享deleteDeptShare
    @PostMapping("/deleteDeptShare")
    @ResponseBody
    public Object deleteDeptShare(@RequestBody Map<String, Object> body) throws Exception {
        IRemoveHandler removeHandler = SpringUtils.getBean("deptShareRemoveHandler", IRemoveHandler.class);
        removeHandler.remove(body);
        return Result.ok("");
    }

    // 撤回线索分享psonDeptShare
    @PostMapping("/deletePsonShare")
    @ResponseBody
    public Object deletePsonShare(@RequestBody Map<String, Object> body) throws Exception {
        IRemoveHandler removeHandler = SpringUtils.getBean("psonShareRemoveHandler", IRemoveHandler.class);
        removeHandler.remove(body);
        return Result.ok("");
    }

    @GetMapping("/excel")
    public void excel(@RequestParam Map<String, Object> requestParam, HttpServletResponse response) throws Exception {
        IQueryHandler depService = SpringUtils.getBean("clueDeptService", IQueryHandler.class);
        Map<String, Object> p = new HashMap<String, Object>();
        p.put("deptCode", requestParam.get("deptList"));
        List<Map<String, Object>> deptList = (List<Map<String, Object>>)depService.list(p);
        Map<String, Object> deptNow = (Map<String, Object>)depService.get(p);
        deptList.add(deptNow);
        requestParam.put("deptList", deptList);
        requestParam.put("dataStatus", 1);
        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "QBXXCLUE");
        List<Map<String, Object>> list = (List<Map<String, Object>>) baseService.list(requestParam);

        for (Map map :list){
            if (Integer.parseInt(map.get("clueSortId").toString()) == 1){
                map.put("clueSortId","环境");
            }else if (Integer.parseInt(map.get("clueSortId").toString()) == 2){
                map.put("clueSortId","食品");
            }else if (Integer.parseInt(map.get("clueSortId").toString()) == 3){
                map.put("clueSortId","药品");
            }else if(Integer.parseInt(map.get("clueSortId").toString()) == 4){
                map.put("clueSortId","综合");
            }
            if (Integer.parseInt(map.get("collectionTypeId").toString()) == 1){
                map.put("collectionTypeId","摸排");
            }else if(Integer.parseInt(map.get("collectionTypeId").toString()) == 2){
                map.put("collectionTypeId","举报");
            }
        }

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Map<String, String> headersMap = new LinkedHashMap<String, String>();
        headersMap.put("clueNumber", "线索编号");
        headersMap.put("clueSortId", "线索分类");
        headersMap.put("collectionTypeId", "采集类型");
        headersMap.put("clueName", "线索标题");
        headersMap.put("clueContent", "线索内容");
        headersMap.put("locationDetailed", "详细地址");
        headersMap.put("submitDeptName", "填报单位");
        headersMap.put("submitPersonName", "填报人");
        headersMap.put("submitTime", "填报时间");


        String fileName = "情报线索";

        ExcelUtil.exportExcel(headersMap, list, os, null);
        // 配置浏览器下载
        byte[] content = os.toByteArray();
        InputStream is = new ByteArrayInputStream(content);
        response.reset();
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        response.setHeader("Content-Disposition",
                "attachment;filename=" + new String((fileName + ".xlsx").getBytes(), "iso-8859-1"));
        ServletOutputStream out = response.getOutputStream();
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            bis = new BufferedInputStream(is);
            bos = new BufferedOutputStream(out);
            byte[] buff = new byte[2048];
            int bytesRead;
            while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
                bos.write(buff, 0, bytesRead);
            }
        } catch (final IOException e) {
            throw e;
        } finally {
            if (bis != null)
                bis.close();
            if (bos != null)
                bos.close();
        }
    }

    private void validParams(Map<String, Object> body) {
        ValidationUtils.notNull(body.get("submitPersonName"), "填报人员不能为空!");
        ValidationUtils.notNull(body.get("submitDeptName"), "填报单位不能为空!");
        ValidationUtils.notNull(body.get("submitTime"), "填报时间不能为空!");
        ValidationUtils.notNull(body.get("clueName"), "线索标题不能为空!");
        ValidationUtils.notNull(body.get("clueContent"), "线索内容不能为空!");
        ValidationUtils.notNull(body.get("clueSortId"), "线索分类不能为空!");
        ValidationUtils.notNull(body.get("collectionTypeId"), "采集类型不能为空!");
        ValidationUtils.notNull(body.get("collectionLocation"), "采集地点不能为空!");
        ValidationUtils.notNull(body.get("locationDetailed"), "详细地址不能为空!");
//    ValidationUtils.notNull(body.get("collectionCoordinate"), "位置信息不能为空!");
    }

    private void validId(Object id) {
        ValidationUtils.notNull(id, "id不能为空!");
        ValidationUtils.regexp(id, "^\\d+$", "非法输入");
    }
}
