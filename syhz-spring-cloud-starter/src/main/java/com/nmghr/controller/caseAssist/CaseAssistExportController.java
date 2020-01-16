package com.nmghr.controller.caseAssist;

import com.alibaba.fastjson.JSONObject;
import com.nmghr.basic.common.Constant;
import com.nmghr.basic.common.exception.GlobalErrorException;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.page.Paging;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.util.ValidationUtils;
import com.nmghr.service.ajglqbxs.AjglQbxsService;
import com.nmghr.util.DateUtil;
import com.sargeraswang.util.ExcelUtil.ExcelUtil;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

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
  @Autowired
  private AjglQbxsService ajglQbxsService;

  @RequestMapping(value = "/cluster/export")
  public void clusterExport(@RequestParam Map<String, Object> req, HttpServletResponse response) throws Exception {
    ValidationUtils.notNull(req.get("curDeptName"), "curDeptName不能为空!");
    ValidationUtils.notNull(req.get("realName"), "realName不能为空!");
    ValidationUtils.notNull(req.get("curUserPhone"), "curUserPhone不能为空!");
    ValidationUtils.notNull(req.get("category"), "category不能为空!");

    String curDeptName = String.valueOf(req.get("curDeptName"));
    String realName = String.valueOf(req.get("realName"));
    String curUserPhone = String.valueOf(req.get("curUserPhone"));
    String category = String.valueOf(req.get("category"));

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

    List<Map<String, Object>> list = new ArrayList<>();
    List<Object> ids = new ArrayList<>();
    if ("1".equals(category)) {
      Map<String, Object> p = new HashMap<>();
      p.putAll(req);
      if (!StringUtils.isEmpty(p.get("reginCode"))) {
        p.put("cityCode", p.get("reginCode"));
      } else {
        if (StringUtils.isEmpty(p.get("cityCode"))) {
          if (!StringUtils.isEmpty(p.get("provinceCode"))) {
            p.put("cityCode", p.get("provinceCode"));
          }
        }
      }
      if (!StringUtils.isEmpty(req.get("isCheck")) && Boolean.valueOf(String.valueOf(req.get("isCheck")))) {
        p.put("isCheck", 0);
      } else {
        p.put("isCheck", 3);
      }
      p.remove("curDeptName");
      p.remove("realName");
      p.remove("curUserPhone");
      p.remove("category");
      ids = getClusterAllIds(p);
    } else {
      ValidationUtils.notNull(req.get("clusterIds"), "clusterIds不能为空!");
      String clusterIds = String.valueOf(req.get("clusterIds"));
      ids = Arrays.asList(clusterIds.split(","));
    }
    params = new HashMap<>();
    if (ids != null && ids.size() > 0) {
      params.put("clusterIds", ids);
    }
    list = getList(params, 2);
    if (list == null) {
      list = new ArrayList<>();
    }

    XSSFWorkbook wb = null;
    try {
      // excel模板路径
      wb = setSheets(curDeptName, realName, curUserPhone, list, json);
      String fileName = "集群战役-协查战果反馈表";
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

  private List<Object> getClusterAllIds(Map<String, Object> req) throws Exception {
    List<Object> res = new ArrayList<>();
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJCLUSTERASSIST");
    List<Map<String, Object>> obj = (List<Map<String, Object>>) baseService.list(req);
    if (obj != null && obj.size() > 0) {
      for (Map<String, Object> m : obj) {
        res.add(m.get("clusterId"));
      }
    }
    return res;
  }

  private List<Object> getAssistAllIds(Map<String, Object> req) throws Exception {
    List<Object> res = new ArrayList<>();
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJASSIST");
    List<Map<String, Object>> obj = (List<Map<String, Object>>) baseService.list(req);
    if (obj != null && obj.size() > 0) {
      for (Map<String, Object> m : obj) {
        res.add(m.get("assistId"));
      }
    }
    return res;
  }

  @RequestMapping(value = "/caseAssist/export")
  public void caseAssistExport(@RequestParam Map<String, Object> req, HttpServletResponse response) throws Exception {
    ValidationUtils.notNull(req.get("curDeptName"), "curDeptName不能为空!");
    ValidationUtils.notNull(req.get("realName"), "realName不能为空!");
    ValidationUtils.notNull(req.get("curUserPhone"), "curUserPhone不能为空!");
    ValidationUtils.notNull(req.get("category"), "category不能为空!");

    String curDeptName = String.valueOf(req.get("curDeptName"));
    String realName = String.valueOf(req.get("realName"));
    String curUserPhone = String.valueOf(req.get("curUserPhone"));
    String category = String.valueOf(req.get("category"));

    Map<String, Object> params = new HashMap<>();
    params.put("configKey", "assistExport");
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
    } catch (Exception e) {
      throw new GlobalErrorException("999889", "导出配置信息错误");
    }

    List<Map<String, Object>> list = new ArrayList<>();
    List<Object> ids = new ArrayList<>();
    if ("1".equals(category)) {
      Map<String, Object> p = new HashMap<>();
      p.putAll(req);
      if (!StringUtils.isEmpty(p.get("reginCode"))) {
        p.put("cityCode", p.get("reginCode"));
      } else {
        if (StringUtils.isEmpty(p.get("cityCode"))) {
          if (!StringUtils.isEmpty(p.get("provinceCode"))) {
            p.put("cityCode", p.get("provinceCode"));
          }
        }
      }
      if (!StringUtils.isEmpty(req.get("isCheck")) && Boolean.valueOf(String.valueOf(req.get("isCheck")))) {
        p.put("isCheck", 0);
      } else {
        p.put("isCheck", 3);
      }
      p.remove("curDeptName");
      p.remove("realName");
      p.remove("curUserPhone");
      p.remove("category");
      ids = getAssistAllIds(p);
    } else {
      ValidationUtils.notNull(req.get("assistIds"), "assistIds不能为空!");
      String assistIds = String.valueOf(req.get("assistIds"));
      ids = Arrays.asList(assistIds.split(","));
    }
    params = new HashMap<>();
    if (ids != null && ids.size() > 0) {
      params.put("assistIds", ids);
    }
    list = getList(params, 1);
    if (list == null) {
      list = new ArrayList<>();
    }
    XSSFWorkbook wb = null;
    try {
      // excel模板路径
      wb = setSheets(curDeptName, realName, curUserPhone, list, json);
      String fileName = "案件协查-协查战果反馈表";
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

  private XSSFWorkbook setSheets(String curDeptName, String realName, String curUserPhone, List<Map<String, Object>> list, JSONObject config) throws IOException {
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
          c0.setCellValue(String.valueOf(bean.get("assistNumber")));// 编号
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
        c4.setCellValue(Double.parseDouble(String.valueOf(bean.get("ysxz"))));// 移送
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
      shrow.createCell(8).setCellValue(realName + "/" + curUserPhone);

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

  public List<Map<String, Object>> getList(Map<String, Object> params, int type) {
    try {
      //查询1.案件协查  2.集群战役
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, type == 2 ? "AJCLUSTERASSISTEXPORT" : "AJASSISTEXPORT");
      List<Map<String, Object>> list = (List<Map<String, Object>>) baseService.list(params);
      if (list == null || list.size() == 0) {
        return new ArrayList<>();
      }
      int xsNumSum = 0, csSum = 0, cfSum = 0, whcSum = 0, laNum = 0, paNum = 0, dhwdSum = 0, pzdbSum = 0,
          xsjlSum = 0, zhrysSum = 0, yjssSum = 0, ysxzSum = 0;
      double sajzSum = 0;
      for (Map<String, Object> m : list) {
        m.put("cityCode", String.valueOf(m.get("applyDeptCode")).substring(0, 4) + "00");
        ysxzSum += Integer.parseInt(String.valueOf(m.get("ysxz")));//移送行政处理次数
        if (!StringUtils.isEmpty(m.get("zbajList"))) {
          String[] zbs = String.valueOf(m.get("zbajList")).split(",");
          List<String> ajbhs = Arrays.asList(zbs);
          Map res = ajglQbxsService.getAjInfoData(ajbhs);
          m.putAll(res);
          if (res.containsKey("dhwd") && !StringUtils.isEmpty(res.get("dhwd"))) {
            dhwdSum += Integer.parseInt(String.valueOf(res.get("dhwd")));
          }
          if (res.containsKey("sajz") && !StringUtils.isEmpty(res.get("sajz"))) {
            sajzSum += Double.parseDouble(String.valueOf(res.get("sajz")));
          }
          if (res.containsKey("pzdb") && !StringUtils.isEmpty(res.get("pzdb"))) {
            pzdbSum += Integer.parseInt(String.valueOf(res.get("pzdb")));
          }
          if (res.containsKey("zhrys") && !StringUtils.isEmpty(res.get("zhrys"))) {
            zhrysSum += Integer.parseInt(String.valueOf(res.get("zhrys")));
          }
          if (res.containsKey("yjss") && !StringUtils.isEmpty(res.get("yjss"))) {
            yjssSum += Integer.parseInt(String.valueOf(res.get("yjss")));
          }
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
          m.put("dhwd", 0);//捣毁窝点
          m.put("sajz", "0.00");//涉案价值
          m.put("pzdb", 0);//逮捕
          m.put("zhrys", 0);//抓获
          m.put("yjss", 0);//移诉
          m.put("xsjl", 0);//刑拘
          m.put("larqCount", 0);//立案起
          m.put("parqCount", 0);//破案起
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
        int total = whc + hcs;
        BigDecimal hc = new BigDecimal(String.valueOf(hcs));
        if (total > 0) {
          m.put("hcl", hc.divide(new BigDecimal(String.valueOf(total)), 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100")).setScale(2, RoundingMode.DOWN).toString());
        } else {
          m.put("hcl", '-');
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
      count.put("sajz", new BigDecimal(String.valueOf(sajzSum)).setScale(2, RoundingMode.HALF_UP).toString());
      count.put("ysxz", ysxzSum);

      BigDecimal hcSum = new BigDecimal(String.valueOf(csSum + cfSum));
      if (xsNumSum > 0) {
        count.put("hcl", hcSum.divide(new BigDecimal(String.valueOf(xsNumSum)), 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100")).setScale(2, RoundingMode.DOWN).toString());
      } else {
        count.put("hcl", "-");
      }
      list.add(count);
      return list;
    } catch (Exception e) {
    }
    return new ArrayList<>();
  }


  /**
   * 集群战役线索导出
   *
   * @param req
   * @param response
   * @throws Exception
   */
  @RequestMapping(value = "/cluster/export/clue")
  public void clusterClueExport(@RequestParam Map<String, Object> req, HttpServletResponse response) throws Exception {
    ValidationUtils.notNull(req.get("clusterId"), "clusterId不能为空!");
    ValidationUtils.notNull(req.get("deptType"), "deptType不能为空!");
    ValidationUtils.notNull(req.get("deptCode"), "deptCode不能为空!");
    if ("2".equals(String.valueOf(req.get("deptType")))) {
      ValidationUtils.notNull(req.get("category"), "category不能为空!");
    }
    List<Object> heads = getTitle(req.get("clusterId"), 2);
    if (heads.size() > 0) {
      Map<String, Object> datas = getCluesList(req.get("clusterId"), 2, Integer.parseInt(String.valueOf(req.get("deptType"))),
          req.get("category"), String.valueOf(req.get("deptCode")));
      outPutData(response, heads, datas);
    }
  }

  /**
   * 案件协查线索导出
   *
   * @param req
   * @param response
   * @throws Exception
   */
  @RequestMapping(value = "/assist/clue/export")
  public void assistClueExport(@RequestParam Map<String, Object> req, HttpServletResponse response) throws Exception {
    ValidationUtils.notNull(req.get("assistId"), "assistId不能为空!");
    ValidationUtils.notNull(req.get("deptType"), "deptType不能为空!");
    ValidationUtils.notNull(req.get("deptCode"), "deptCode不能为空!");
    if ("2".equals(String.valueOf(req.get("deptType")))) {
      ValidationUtils.notNull(req.get("category"), "category不能为空!");
    }
    List<Object> heads = getTitle(req.get("assistId"), 1);
    if (heads.size() > 0) {
      Map<String, Object> datas = getCluesList(req.get("assistId"), 1, Integer.parseInt(String.valueOf(req.get("deptType"))),
          req.get("category"), String.valueOf(req.get("deptCode")));
      outPutData(response, heads, datas);
    }
  }

  private void outPutData(HttpServletResponse response, List<Object> heads, Map<String, Object> datas) {
    XSSFWorkbook wb = null;
    try {
      // excel模板路径
      wb = setCluesSheets(heads, datas);
      String fileName = "案件协查-协查战果反馈表";
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


  private List<Object> getTitle(Object id, int assistType) throws Exception {
    Map<String, Object> params = new HashMap<>();
    params.put("assistType", assistType);
    params.put("assistId", id);
    //查询标题
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJGLQBXSBYASSIST");
    List<Map<String, Object>> titles = (List<Map<String, Object>>) baseService.list(params);
    if (titles == null || titles.size() == 0) {
      return new ArrayList();
    }
    List<Object> heads = new ArrayList<>();
    for (Map<String, Object> map : titles) {
      heads.add(map.get("title"));
    }
    return heads;
  }

  private Map<String, Object> getCluesList(Object id, int assistType, int deptType, Object category, String deptCode) throws Exception {
    Map<String, Object> result = new LinkedHashMap<>();
    Map<String, Object> params = new HashMap<>();
    params.put("id", id);
    params.put("assistType", assistType);
    params.put("deptType", deptType);
    if (category != null && !StringUtils.isEmpty(String.valueOf(category))) {
      params.put("category", Integer.parseInt(String.valueOf(category)));
    }
    params.put("deptCode", deptCode);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJGLQBXSINFOEXPORT");
    List<Map<String, Object>> list = (List<Map<String, Object>>) baseService.list(params);
    if (list == null || list.size() == 0) {
      return result;
    }

    for (Map<String, Object> map : list) {
      String key = String.valueOf(map.get("columnIndex"));
      if (result.containsKey(key)) {
        List<Object> m = (List<Object>) result.get(key);
        m.add(map.get("value"));
        result.put(key, m);
      } else {
        List<Object> m = new ArrayList<>();
        m.add(map.get("value"));
        result.put(key, m);
      }
    }
    return result;
  }

  private XSSFWorkbook setCluesSheets(List<Object> heads, Map<String, Object> datas) {
    XSSFWorkbook wb = new XSSFWorkbook();
    // 读取excel模板
    // 读取了模板内所有sheet内容
    XSSFSheet sheet = wb.createSheet("线索列表");
    // 在相应的单元格进行赋值
    XSSFCellStyle cStyle = wb.createCellStyle();
    cStyle.setBorderBottom(BorderStyle.THIN);
    cStyle.setBorderLeft(BorderStyle.THIN);
    cStyle.setBorderRight(BorderStyle.THIN);
    cStyle.setBorderTop(BorderStyle.THIN);

    XSSFRow head = sheet.createRow(0);
    for (int i = 0; i < heads.size(); i++) {
      XSSFCell c = head.createCell(i);
      c.setCellValue(String.valueOf(heads.get(i)));
      c.setCellStyle(cStyle);
    }
    if (datas.size() > 0) {
      int rowIndex = 1;
      for (String key : datas.keySet()) {
        XSSFRow row = sheet.createRow(rowIndex);
        List<Object> list = (List<Object>) datas.get(key);
        for (int j = 0; j < list.size(); j++) {
          XSSFCell c1 = row.createCell(j);
          c1.setCellValue(String.valueOf(list.get(j)));// 线索总数
          c1.setCellStyle(cStyle);
        }
        rowIndex++;
      }
      setSizeColumn(sheet, heads.size());
    }
    return wb;
  }
  private void setSizeColumn(XSSFSheet sheet, int size) {
    for (int columnNum = 0; columnNum <= size; columnNum++) {
      int columnWidth = sheet.getColumnWidth(columnNum) / 256;
      for (int rowNum = 0; rowNum < sheet.getLastRowNum(); rowNum++) {
        XSSFRow currentRow;
        //当前行未被使用过
        if (sheet.getRow(rowNum) == null) {
          currentRow = sheet.createRow(rowNum);
        } else {
          currentRow = sheet.getRow(rowNum);
        }

        if (currentRow.getCell(columnNum) != null) {
          XSSFCell currentCell = currentRow.getCell(columnNum);
          int length = currentCell.getStringCellValue().getBytes().length;
          if (columnWidth < length) {
            columnWidth = length;
          }
        }
      }
      sheet.setColumnWidth(columnNum, columnWidth * 256);
    }
  }

}
