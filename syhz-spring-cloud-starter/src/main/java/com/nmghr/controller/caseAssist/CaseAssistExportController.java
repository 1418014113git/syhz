package com.nmghr.controller.caseAssist;

import com.alibaba.fastjson.JSONObject;
import com.nmghr.basic.common.Constant;
import com.nmghr.basic.common.exception.GlobalErrorException;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.util.ValidationUtils;
import com.nmghr.util.DateUtil;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.util.*;

@SuppressWarnings("unchecked")
@RestController
public class CaseAssistExportController {

  private Logger log = LoggerFactory.getLogger(CaseAssistExportController.class);

  @Autowired
  private IBaseService baseService;

  @RequestMapping(value = "/cluster/export")
  public void clusterExport(String clusterIds, String curDeptName,String realName,String curUserPhone,HttpServletResponse response) throws Exception {
    ValidationUtils.notNull(clusterIds, "clusterIds不能为空!");
    ValidationUtils.notNull(curDeptName, "curDeptName不能为空!");
    ValidationUtils.notNull(realName, "realName不能为空!");
    ValidationUtils.notNull(curUserPhone, "curUserPhone不能为空!");

    Map<String, Object> params = new HashMap<>();
    params.put("configKey", "clusterExport");
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "SYSCONFIG");
    List<Map<String, Object>> configs = (List<Map<String, Object>>) baseService.list(params);
    if (configs == null || configs.size() == 0) {
      throw new GlobalErrorException("999889", "导出配置信息不存在");
    }
    Map<String, Object> config = configs.get(0);
    if (config == null || config.get("configValue") == null) {
      throw new GlobalErrorException("999889", "导出配置信息不存在");
    }
    JSONObject json = null;
    try {
      json = JSONObject.parseObject(String.valueOf(config.get("configValue")));
      json.getString("url");
      json.getString("remark");
    } catch (Exception e) {
      throw new GlobalErrorException("999889", "导出配置信息错误");
    }

    params = new HashMap<>();
    params.put("clusterIds",clusterIds);
    List<Map<String, Object>> list = getList(params);
    XSSFWorkbook wb = null;
    try {
      // excel模板路径
      wb = setSheets(curDeptName,realName, curUserPhone, list, json);

      String fileName = "涉案线索协查参与地战果反馈表";
      ByteArrayOutputStream os = new ByteArrayOutputStream();
      wb.write(os);
      byte[] content = os.toByteArray();
      InputStream is = new ByteArrayInputStream(content);
      // 设置response参数，可以打开下载页面
      response.reset();
      response.setContentType("application/vnd.ms-excel;charset=utf-8");
      response.setHeader("Content-Disposition", "attachment;filename=" + new String((fileName + ".xlsx").getBytes(), "iso-8859-1"));
      ServletOutputStream sout = response.getOutputStream();
      BufferedInputStream bis = null;
      BufferedOutputStream bos = null;

      try {
        bis = new BufferedInputStream(is);
        bos = new BufferedOutputStream(sout);
        byte[] buff = new byte[2048];
        int bytesRead;
        // Simple read/write loop.
        while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
          bos.write(buff, 0, bytesRead);
        }
      } catch (Exception e) {
        log.error("导出excel出现异常:", e);
      } finally {
        if (bis != null)
          bis.close();
        if (bos != null)
          bos.close();
      }

    } catch (Exception e) {
      log.error("导出excel出现异常:", e);
    }

  }

  private XSSFWorkbook setSheets(String curDeptName,String realName,String curUserPhone, List<Map<String, Object>> list, JSONObject config) throws IOException {
    XSSFWorkbook wb;
    URL url = new URL(config.getString("url"));
    wb = new XSSFWorkbook(url.openStream());
    // 读取excel模板
    // 读取了模板内所有sheet内容
    XSSFSheet sheet = wb.getSheetAt(0);
    // 在相应的单元格进行赋值
    XSSFCellStyle cStyle = wb.createCellStyle();
    cStyle.setBorderBottom(BorderStyle.THIN);
    cStyle.setBorderLeft(BorderStyle.THIN);
    cStyle.setBorderRight(BorderStyle.THIN);
    cStyle.setBorderTop(BorderStyle.THIN);

    XSSFRow gd = sheet.getRow(4);
    XSSFCell cellt = gd.getCell(0);
    cellt.setCellValue("填报单位：" + curDeptName + "                     填报时间："
        + DateUtil.dateFormart(new Date(), "yyyy年MM月dd日"));// 标识
    if (list.size() > 0) {
      int rowIndex = 17;
      int i = 1;
      int size = list.size();
      for (Map<String, Object> bean : list) {
        XSSFRow row = sheet.createRow(rowIndex);
        XSSFCell c0 = row.getCell(0);
        if (null == c0) {
          c0 = row.createCell(0);
        }
        if (i == size) {
          c0.setCellValue("总数");
        } else {
          c0.setCellValue(String.valueOf(bean.get("clusterNumber")));// 编号
        }
        i++;
        c0.setCellStyle(cStyle);
        XSSFCell c1 = row.createCell(1);
        c1.setCellValue(Double.parseDouble(String.valueOf(bean.get("xsNum"))));// 线索总数
        c1.setCellStyle(cStyle);
        XSSFCell c2 = row.createCell(2);
        c2.setCellValue(Double.parseDouble(String.valueOf(bean.get("cs"))));// 查实
        c2.setCellStyle(cStyle);
        XSSFCell c3 = row.createCell(3);
        c3.setCellValue(Double.parseDouble(String.valueOf(bean.get("cf"))));// 查否
        c3.setCellStyle(cStyle);
        XSSFCell c4 = row.createCell(4);
        c4.setCellValue(Double.parseDouble(String.valueOf(bean.get("ysajList"))));// 移送
        c4.setCellStyle(cStyle);
        XSSFCell c5 = row.createCell(5);
        c5.setCellValue(Double.parseDouble(String.valueOf(bean.get("larqCount"))));// 立案
        c5.setCellStyle(cStyle);
        XSSFCell c6 = row.createCell(6);
        c6.setCellValue(Double.parseDouble(String.valueOf(bean.get("parqCount"))));// 破案
        c6.setCellStyle(cStyle);
        XSSFCell c7 = row.createCell(7);
        c7.setCellValue(Double.parseDouble(String.valueOf(bean.get("zhrys"))));// 抓获
        c7.setCellStyle(cStyle);
        XSSFCell c8 = row.createCell(8);
        c8.setCellValue(Double.parseDouble(String.valueOf(bean.get("xsjl"))));// 刑事拘留
        c8.setCellStyle(cStyle);
        XSSFCell c9 = row.createCell(9);
        c9.setCellValue(Double.parseDouble(String.valueOf(bean.get("pzdb"))));// 批准逮捕
        c9.setCellStyle(cStyle);
        XSSFCell c10 = row.createCell(10);
        c10.setCellValue(Double.parseDouble(String.valueOf(bean.get("yjss"))));// 移交诉讼
        c10.setCellStyle(cStyle);
        XSSFCell c11 = row.createCell(11);
        c11.setCellValue(Double.parseDouble(String.valueOf(bean.get("dhwd"))));// 捣毁窝点
        c11.setCellStyle(cStyle);
        XSSFCell c12 = row.createCell(12);
        c12.setCellValue(Double.parseDouble(String.valueOf(bean.get("sajz"))));// 涉案金额
        c12.setCellStyle(cStyle);
        rowIndex++;
      }
      CellRangeAddress kong = new CellRangeAddress(rowIndex, rowIndex, 0, 12); //空行
      sheet.addMergedRegion(kong);
      RegionUtil.setBorderBottom(BorderStyle.THIN, kong, sheet);
      RegionUtil.setBorderLeft(BorderStyle.THIN, kong, sheet);
      RegionUtil.setBorderRight(BorderStyle.THIN, kong, sheet);
      RegionUtil.setBorderTop(BorderStyle.THIN, kong, sheet);

      rowIndex++;
      XSSFRow shrow = sheet.createRow(rowIndex);
      XSSFCell sfrC = shrow.createCell(0);
      sfrC.setCellValue("审核人：");
      sfrC.setCellStyle(cStyle);
      shrow.createCell(1).setCellValue(realName);
      shrow.createCell(6).setCellValue("填报人：");
      shrow.createCell(8).setCellValue(realName+"/"+curUserPhone);

      CellRangeAddress shrA = new CellRangeAddress(rowIndex, rowIndex, 1, 5); //审核人
      sheet.addMergedRegion(shrA);
      RegionUtil.setBorderBottom(BorderStyle.THIN, shrA, sheet);
      RegionUtil.setBorderLeft(BorderStyle.THIN, shrA, sheet);
      RegionUtil.setBorderRight(BorderStyle.THIN, shrA, sheet);
      RegionUtil.setBorderTop(BorderStyle.THIN, shrA, sheet);

      CellRangeAddress tbrAT = new CellRangeAddress(rowIndex, rowIndex, 6, 7); //填报人标题
      sheet.addMergedRegion(tbrAT);
      RegionUtil.setBorderBottom(BorderStyle.THIN, tbrAT, sheet);
      RegionUtil.setBorderLeft(BorderStyle.THIN, tbrAT, sheet);
      RegionUtil.setBorderRight(BorderStyle.THIN, tbrAT, sheet);
      RegionUtil.setBorderTop(BorderStyle.THIN, tbrAT, sheet);

      CellRangeAddress tbrA = new CellRangeAddress(rowIndex, rowIndex, 8, 12); //填报人
      sheet.addMergedRegion(tbrA);
      RegionUtil.setBorderBottom(BorderStyle.THIN, tbrA, sheet);
      RegionUtil.setBorderLeft(BorderStyle.THIN, tbrA, sheet);
      RegionUtil.setBorderRight(BorderStyle.THIN, tbrA, sheet);
      RegionUtil.setBorderTop(BorderStyle.THIN, tbrA, sheet);

      rowIndex++;

      XSSFCell remarkC = sheet.createRow(rowIndex).createCell(0);
      remarkC.setCellValue(config.getString("remark"));

      CellRangeAddress cra = new CellRangeAddress(rowIndex, rowIndex + 3, 0, 12); //说明
      sheet.addMergedRegion(cra);
      RegionUtil.setBorderBottom(BorderStyle.THIN, cra, sheet);
      RegionUtil.setBorderLeft(BorderStyle.THIN, cra, sheet);
      RegionUtil.setBorderRight(BorderStyle.THIN, cra, sheet);
      RegionUtil.setBorderTop(BorderStyle.THIN, cra, sheet);

      XSSFCellStyle cellTextStyle = wb.createCellStyle();
      cellTextStyle.setAlignment(HorizontalAlignment.LEFT);
      cellTextStyle.setVerticalAlignment(VerticalAlignment.TOP);
      cellTextStyle.setWrapText(true);
      remarkC.setCellStyle(cellTextStyle);
    }

    return wb;
  }

  public List<Map<String, Object>> getList(Map<String, Object> params) {
    try {
      //查询
      String type = "";
      if (params.containsKey("type") && !StringUtils.isEmpty(params.get("type"))) {
        type = String.valueOf(params.get("type"));
      }
      if ("".equals(type) || "2".equals(type)) { // 集群战役
        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJCLUSTERASSISTEXPORT");
      }
      if ("1".equals(type)) { // 案件协查
        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJASSISTEXPORT");
      }
      List<Map<String, Object>> list = (List<Map<String, Object>>) baseService.list(params);
      if (list == null || list.size() == 0) {
        return new ArrayList<>();
      }
      int xsNumSum = 0, csSum = 0, cfSum = 0, whcSum = 0, laNum = 0, paNum = 0, dhwdSum = 0, pzdbSum = 0,
          xsjlSum = 0, ysajList = 0,zhrysSum=0, yjssSum=0;
      double sajzSum = 0;
      for (Map<String, Object> m : list) {
        m.put("cityCode", String.valueOf(m.get("applyDeptCode")).substring(0, 4) + "00");
        if (m.get("ysajList") != null) {
          String[] str = String.valueOf(m.get("ysajList")).replaceAll("\\]", "").replaceAll("\\[", "").split(",");
          m.put("ysajList", str.length);
          ysajList += str.length;
        } else {
          m.put("ysajList", 0);
        }
        if (m.get("zbajList") != null) {
          int dhwd = 0, pzdb = 0, zhrys=0,yjss=0;
          double sajz = 0;
          String[] zbs = String.valueOf(m.get("zbajList")).split("_");
          List<String> ajbhs = new ArrayList<>();
          for (String s : zbs) {
            Map<String, Object> zbmap = JSONObject.toJavaObject(JSONObject.parseObject(s), Map.class);
            for (String key : zbmap.keySet()) {
              String[] info = String.valueOf(zbmap.get(key)).split(",");
              if(info.length==6){
                dhwd += Integer.parseInt(info[1]);
                sajz += Double.parseDouble(info[2]);
                pzdb += Integer.parseInt(info[3]);
                zhrys += Integer.parseInt(info[4]);
                yjss += Integer.parseInt(info[5]);
              }
              ajbhs.add(key);
            }
          }
          m.put("dhwd", dhwd);
          m.put("sajz", sajz);
          m.put("pzdb", pzdb);
          m.put("zhrys", zhrys);
          m.put("yjss", yjss);
          Map res = getAjInfoData(ajbhs);
          m.putAll(res);
          dhwdSum += dhwd;
          sajzSum += sajz;
          pzdbSum += pzdb;
          zhrysSum += zhrys;
          yjssSum += yjss;
          if (res.containsKey("xsjl") && !org.springframework.util.StringUtils.isEmpty(res.get("xsjl"))) {
            xsjlSum += Integer.parseInt(String.valueOf(res.get("xsjl")));
          }
          if (res.containsKey("larqCount") && !org.springframework.util.StringUtils.isEmpty(res.get("larqCount"))) {
            laNum += Integer.parseInt(String.valueOf(res.get("larqCount")));
          }
          if (res.containsKey("parqCount") && !org.springframework.util.StringUtils.isEmpty(res.get("parqCount"))) {
            paNum += Integer.parseInt(String.valueOf(res.get("parqCount")));
          }
        } else {
          m.put("dhwd", 0);
          m.put("sajz", 0);
          m.put("pzdb", 0);
          m.put("zhrys", 0);
          m.put("yjss", 0);
          m.put("xsjl", 0);
          m.put("larqCount", 0);
          m.put("parqCount", 0);
        }
        m.remove("zbajList");
        int cs = Integer.parseInt(String.valueOf(m.get("cs")));
        int cf = Integer.parseInt(String.valueOf(m.get("cf")));
        int whc = Integer.parseInt(String.valueOf(m.get("whc")));
        int xsNum = Integer.parseInt(String.valueOf(m.get("xsNum")));
        csSum += cs;
        cfSum += cf;
        whcSum += whc;
        xsNumSum += xsNum;
        int hcs = cs + cf;
        BigDecimal hc = new BigDecimal(String.valueOf(hcs));
        if (xsNum > 0) {
          hc = hc.divide(new BigDecimal(String.valueOf(xsNum)), 2, RoundingMode.DOWN).multiply(new BigDecimal("100")).setScale(0, RoundingMode.DOWN);
          m.put("hcl", hc.intValue());
        } else {
          m.put("hcl", 0);
        }
      }
      Map<String, Object> count = new HashMap<>();
      count.put("deptName", "-");
      count.put("deptCode", "-");
      count.put("xsNum", xsNumSum);
      count.put("cf", cfSum);
      count.put("cs", csSum);
      count.put("whc", whcSum);
      count.put("larqCount", laNum);
      count.put("parqCount", paNum);
      count.put("dhwd", dhwdSum);
      count.put("pzdb", pzdbSum);
      count.put("zhrys", zhrysSum);
      count.put("yjss", yjssSum);
      count.put("xsjl", xsjlSum);
      count.put("sajz", sajzSum);
      count.put("ysajList", ysajList);

      BigDecimal hcSum = new BigDecimal(String.valueOf(csSum + cfSum));
      if (xsNumSum > 0) {
        hcSum = hcSum.divide(new BigDecimal(String.valueOf(xsNumSum)), 2, RoundingMode.DOWN).multiply(new BigDecimal("100")).setScale(0, RoundingMode.DOWN);
        count.put("hcl", hcSum.intValue());
      } else {
        count.put("hcl", 0);
      }
      list.add(count);
      return list;
    } catch (Exception e) {
    }
    return new ArrayList<>();
  }

  private Map<String, Integer> getAjInfoData(List<String> ajbhs) throws Exception {
    Map<String, Integer> res = new HashMap<>();
    res.put("xsjl", 0);
    res.put("larqCount", 0);
    res.put("parqCount", 0);
    Map<String, Object> params = new HashMap<>();
    params.put("ajbhs", ajbhs);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJCLUSTERFEEDBACKAJINFO");
    List<Map<String, Object>> ajList = (List<Map<String, Object>>) baseService.list(params);
    if (ajList == null || ajList.size() == 0) {
      return res;
    }
    int xsjl = 0, larqCount = 0, parqCount = 0;
    for (Map<String, Object> aj : ajList) {
      if (!org.springframework.util.StringUtils.isEmpty(aj.get("ryclcs"))) {
        xsjl += Integer.parseInt(String.valueOf(aj.get("ryclcs")));
      }
      if (!org.springframework.util.StringUtils.isEmpty(aj.get("LARQ"))) {
        larqCount++;
      }
      if (!org.springframework.util.StringUtils.isEmpty(aj.get("PARQ"))) {
        parqCount++;
      }
    }
    res.put("xsjl", xsjl);
    res.put("larqCount", larqCount);
    res.put("parqCount", parqCount);
    return res;
  }


}