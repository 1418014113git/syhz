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
import com.sargeraswang.util.ExcelUtil.*;
import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ComparatorUtils;
import org.apache.commons.collections.comparators.ComparableComparator;
import org.apache.commons.collections.comparators.ComparatorChain;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.frameworkset.util.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("clue")
public class ClueController {
    @Autowired
    @Qualifier("baseService")
    private IBaseService baseService;

    private static Logger log = LoggerFactory.getLogger(ClueController.class);
    private static Map<Class<?>, CellType[]> validateMap = new HashMap();
    private static SXSSFWorkbook sxssFWorkBrook;
    private static ExcelLogs logs = null;

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

    // 导出前查询记录数
    @PostMapping(value = "/listCount")
    public Object listCount(@RequestBody Map<String, Object> requestParam) throws Exception {
        IQueryHandler queryHandler = SpringUtils.getBean("clueQueryHandler", IQueryHandler.class);
        List resultList = (List)queryHandler.list(requestParam);
        return resultList.size();
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
    public Object excel(@RequestParam Map<String, Object> requestParam, HttpServletResponse response) throws Exception {
        if(!ObjectUtils.isEmpty(requestParam.get("deptList"))){
            IQueryHandler depService = SpringUtils.getBean("clueDeptService", IQueryHandler.class);
            Map<String, Object> p = new HashMap<String, Object>();
            p.put("deptCode", requestParam.get("deptList"));
            List<Map<String, Object>> deptList = (List<Map<String, Object>>)depService.list(p);
            Map<String, Object> deptNow = (Map<String, Object>)depService.get(p);
            deptList.add(deptNow);
            requestParam.put("deptList", deptList);
        }
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
        exportExcel(headersMap, list, os, null, null);
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
        return true;
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
        ValidationUtils.notNull(body.get("collectionCoordinate"), "位置信息不能为空!");
    }

    private void validId(Object id) {
        ValidationUtils.notNull(id, "id不能为空!");
        ValidationUtils.regexp(id, "^\\d+$", "非法输入");
    }

    /**
     * 重写ExcelUtil2019工具类中导出方法
     */
    private <T> void exportExcel(Map<String, String> headers, Collection<T> dataset, OutputStream out, ArrayList<Integer> lockdList, Map<String, Integer> headerMapWidth) {
        exportExcel(headers, dataset, out, (String)null, lockdList, headerMapWidth);
    }
    private <T> void exportExcel(Map<String, String> headers, Collection<T> dataset, OutputStream out, String pattern, ArrayList<Integer> lockedList, Map<String, Integer> headerMapWidth) {
        sxssFWorkBrook = new SXSSFWorkbook();
        SXSSFSheet sxssfSheet = sxssFWorkBrook.createSheet();
        writeSXSSFSheet(sxssfSheet, headers, dataset, pattern, lockedList, false, headerMapWidth);
        try {
            sxssFWorkBrook.write(out);
        } catch (IOException var8) {
            log.error(var8.toString(), var8);
        }
    }
    private <T> void writeSXSSFSheet(SXSSFSheet sheet, Map<String, String> headers, Collection<T> dataset, String pattern, ArrayList<Integer> lockedList, boolean enable, Map<String, Integer> headerMapWidth) {
        if (enable) {
            sheet.protectSheet("edit");
        }
//        int size = false;
        if (!CollectionUtils.isEmpty(dataset)) {
            int var25 = dataset.size();
        }
        if (StringUtils.isEmpty(pattern)) {
            pattern = "yyyy-MM-dd";
        }
        SXSSFRow row = sheet.createRow(0);
        Set<String> keys = headers.keySet();
        Iterator<String> it1 = keys.iterator();
        String key = "";
        int c = 0;
        while(it1.hasNext()) {
            key = (String)it1.next();
            if (headers.containsKey(key)) {
                SXSSFCell cell = row.createCell(c);
                XSSFRichTextString text = new XSSFRichTextString((String)headers.get(key));
                cell.setCellValue(text);
                ++c;
            }
        }
        CellStyle lockstyle = sxssFWorkBrook.createCellStyle();
        CellStyle setBorder = sxssFWorkBrook.createCellStyle();
        setBorder.setWrapText(true);
        if (enable) {
            lockstyle.setLocked(true);
        } else {
            setBorder.setWrapText(false);
            lockstyle.setLocked(false);
            setBorder.setLocked(false);
        }
        Iterator<T> it = dataset.iterator();
        int index = 0;
        int cellNum;
        label107:
        while(it.hasNext()) {
            ++index;
            row = sheet.createRow(index);
            Object t = it.next();
            try {
                if (t instanceof Map) {
                    Map<String, Object> map = (Map)t;
                    cellNum = 0;
                    Iterator it2 = keys.iterator();
                    while(true) {
                        while(true) {
                            if (!it2.hasNext()) {
                                continue label107;
                            }
                            key = (String)it2.next();
                            if (!headers.containsKey(key)) {
                                log.error("Map 中 不存在 key [" + key + "]");
                            } else {
                                Object value = map.get(key);
                                SXSSFCell cell = row.createCell(cellNum);
                                cellNum = setSXSSFCellValue(cell, value, pattern, cellNum, (Field)null, row);
                                if ((CollectionUtils.isEmpty(lockedList) || lockedList.indexOf(cellNum) >= 0) && enable) {
                                    cell.setCellStyle(lockstyle);
                                } else {
                                    cell.setCellStyle(setBorder);
                                }
                                ++cellNum;
                            }
                        }
                    }
                } else {
                    List<FieldForSortting> fields = sortFieldByAnno(t.getClass());
                    cellNum = 0;
                    for(int i = 0; i < fields.size(); ++i) {
                        SXSSFCell cell = row.createCell(cellNum);
                        Field field = ((FieldForSortting)fields.get(i)).getField();
                        field.setAccessible(true);
                        Object value = field.get(t);
                        cellNum = setSXSSFCellValue(cell, value, pattern, cellNum, field, row);
                        if ((CollectionUtils.isEmpty(lockedList) || lockedList.indexOf(i) >= 0) && enable) {
                            cell.setCellStyle(lockstyle);
                        } else {
                            cell.setCellStyle(setBorder);
                        }
                        ++cellNum;
                    }
                }
            } catch (Exception var24) {
                log.error(var24.toString(), var24);
            }
        }
        sheet.trackAllColumnsForAutoSizing();
        for(int i = 0; i < headers.size(); ++i) {
            sheet.autoSizeColumn((short)i);
        }
    }
    private static int setSXSSFCellValue(SXSSFCell cell, Object value, String pattern, int cellNum, Field field, SXSSFRow row) {
        String textValue = null;
        if (value instanceof Integer) {
            int intValue = (Integer)value;
            cell.setCellValue((double)intValue);
        } else if (value instanceof Float) {
            float fValue = (Float)value;
            cell.setCellValue((double)fValue);
        } else if (value instanceof Double) {
            double dValue = (Double)value;
            cell.setCellValue(dValue);
        } else if (value instanceof Long) {
            long longValue = (Long)value;
            cell.setCellValue((double)longValue);
        } else if (value instanceof Boolean) {
            boolean bValue = (Boolean)value;
            cell.setCellValue(bValue);
        } else if (value instanceof Date) {
            Date date = (Date)value;
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            textValue = sdf.format(date);
        } else {
            int j;
            if (value instanceof String[]) {
                String[] strArr = (String[])((String[])value);

                for(j = 0; j < strArr.length; ++j) {
                    String str = strArr[j];
                    cell.setCellValue(str);
                    if (j != strArr.length - 1) {
                        ++cellNum;
                        cell = row.createCell(cellNum);
                    }
                }
            } else if (value instanceof Double[]) {
                Double[] douArr = (Double[])((Double[])value);

                for(j = 0; j < douArr.length; ++j) {
                    Double val = douArr[j];
                    if (val != null) {
                        cell.setCellValue(val);
                    }

                    if (j != douArr.length - 1) {
                        ++cellNum;
                        cell = row.createCell(cellNum);
                    }
                }
            } else {
                String empty = "";
                if (field != null) {
                    ExcelCell anno = (ExcelCell)field.getAnnotation(ExcelCell.class);
                    if (anno != null) {
                        empty = anno.defaultValue();
                    }
                }
                textValue = value == null ? empty : value.toString();
            }
        }
        if (textValue != null) {
            XSSFRichTextString richString = new XSSFRichTextString(textValue);
            cell.setCellValue(richString);
        }
        return cellNum;
    }
    private static List<FieldForSortting> sortFieldByAnno(Class<?> clazz) {
        Field[] fieldsArr = clazz.getDeclaredFields();
        List<FieldForSortting> fields = new ArrayList();
        List<FieldForSortting> annoNullFields = new ArrayList();
        Field[] var4 = fieldsArr;
        int var5 = fieldsArr.length;
        for(int var6 = 0; var6 < var5; ++var6) {
            Field field = var4[var6];
            ExcelCell ec = (ExcelCell)field.getAnnotation(ExcelCell.class);
            if (ec != null) {
                int id = ec.index();
                fields.add(new FieldForSortting(field, id));
            }
        }
        fields.addAll(annoNullFields);
        sortByProperties(fields, true, false, "index");
        return fields;
    }
    private static void sortByProperties(List<? extends Object> list, boolean isNullHigh, boolean isReversed, String... props) {
        if (CollectionUtils.isNotEmpty(list)) {
            Comparator<?> typeComp = ComparableComparator.getInstance();
//            Comparator typeComp;
            if (isNullHigh) {
                typeComp = ComparatorUtils.nullHighComparator(typeComp);
            } else {
                typeComp = ComparatorUtils.nullLowComparator(typeComp);
            }
            if (isReversed) {
                typeComp = ComparatorUtils.reversedComparator(typeComp);
            }
            List<Object> sortCols = new ArrayList();
            if (props != null) {
                String[] var6 = props;
                int var7 = props.length;
                for(int var8 = 0; var8 < var7; ++var8) {
                    String prop = var6[var8];
                    sortCols.add(new BeanComparator(prop, typeComp));
                }
            }
            if (sortCols.size() > 0) {
                Comparator<Object> sortChain = new ComparatorChain(sortCols);
                Collections.sort(list, sortChain);
            }
        }
    }
}
