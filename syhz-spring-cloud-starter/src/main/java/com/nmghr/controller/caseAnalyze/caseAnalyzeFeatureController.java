package com.nmghr.controller.caseAnalyze;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.page.Paging;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.util.SyhzUtil;
import com.sargeraswang.util.ExcelUtil.ExcelUtil2019;

@SuppressWarnings("unchecked")
@RestController
@RequestMapping("/caseAnalyzeFeature")
public class caseAnalyzeFeatureController {

  @Autowired
  @Qualifier("baseService")
  private IBaseService baseService;

  @PostMapping("/ajlbTotal")
  @ResponseBody
  public Object ajlbTotal(@RequestBody Map<String, Object> requestBody) throws Exception {
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "CASEANALYZEFEATUREONE");
    List<Map<String, Object>> totalList = (List<Map<String, Object>>) baseService.list(requestBody);
    return totalList;
  }

  @PostMapping("/ajlbHbTotal")
  @ResponseBody
  public Object ajlbHbTotal(@RequestBody Map<String, Object> requestBody) throws Exception {
    int pageNum = SyhzUtil.setDateInt(requestBody.get("pageNum"));
    int pageSize = SyhzUtil.setDateInt(requestBody.get("pageSize"));
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "CASEANALYZEFEATUREHBONE");
    Paging page = (Paging) baseService.page(requestBody, pageNum, pageSize);
    setHb(requestBody, page);
    return page;
  }

  @PostMapping("/ajzmTotal")
  @ResponseBody
  public Object ajzmTotal(@RequestBody Map<String, Object> requestBody) throws Exception {
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "CASEANALYZEFEATURETWO");
    List<Map<String, Object>> totalList = (List<Map<String, Object>>) baseService.list(requestBody);
    return totalList;
  }

  @PostMapping("/ajzmHbTotal")
  @ResponseBody
  public Object ajzmHbTotal(@RequestBody Map<String, Object> requestBody) throws Exception {
    int pageNum = SyhzUtil.setDateInt(requestBody.get("pageNum"));
    int pageSize = SyhzUtil.setDateInt(requestBody.get("pageSize"));
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "CASEANALYZEFEATUREHBTWO");
    Paging page = (Paging) baseService.page(requestBody, pageNum, pageSize);
    setHb(requestBody, page);
    return page;
  }

  @PostMapping("/ajlyTotal")
  @ResponseBody
  public Object ajlyTotal(@RequestBody Map<String, Object> requestBody) throws Exception {
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "CASEANALYZEFEATURETHREE");
    List<Map<String, Object>> totalList = (List<Map<String, Object>>) baseService.list(requestBody);
    return totalList;
  }

  @PostMapping("/ajlyHbTotal")
  @ResponseBody
  public Object ajlyHbTotal(@RequestBody Map<String, Object> requestBody) throws Exception {
    int pageNum = SyhzUtil.setDateInt(requestBody.get("pageNum"));
    int pageSize = SyhzUtil.setDateInt(requestBody.get("pageSize"));
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "CASEANALYZEFEATUREHBTHREE");
    Paging page = (Paging) baseService.page(requestBody, pageNum, pageSize);
    setHb(requestBody, page);
    return page;
  }

  private void setHb(Map<String, Object> requestBody, Paging page) {
    String pstartDate = SyhzUtil.setDate(requestBody.get("pstartDate"));
    String pendDate = SyhzUtil.setDate(requestBody.get("pendDate"));
    String lstartDate = SyhzUtil.setDate(requestBody.get("lstartDate"));
    String lendDate = SyhzUtil.setDate(requestBody.get("lendDate"));
    if ((!"".equals(pstartDate) && !"".equals(pendDate)) || (!"".equals(lstartDate) && !"".equals(lendDate))) {
    } else {
      List<Map<String, Object>> list = page.getList();
      if (list != null && list.size() > 0) {
        for (Map<String, Object> map : list) {
          map.put("hb", "-");
        }
      }
    }
  }

  @GetMapping(value = "/exporFile")
  @ResponseBody
  public void importFile(@RequestParam Map<String, Object> requestParam, HttpServletRequest request,
      HttpServletResponse response) throws Exception {

    ByteArrayOutputStream os = new ByteArrayOutputStream();
    Map<String, String> headersMap = new LinkedHashMap<String, String>();
    Map<String, Integer> headerMapWidth = new LinkedHashMap<String, Integer>();
    int type = SyhzUtil.setDateInt(requestParam.get("type"));// 1 案件类别 2案件罪名3案件来源
    int flag = SyhzUtil.setDateInt(requestParam.get("flag"));// 1查询当前页，2查询所有

    Map<String, String[]> hm = request.getParameterMap();
    Iterator it = hm.keySet().iterator();

    Map<String, Object> param = new HashMap<String, Object>();
    while (it.hasNext()) {
      String key = it.next().toString();
      String[] values = (String[]) hm.get(key);
      if (values != null && values.length == 1) {
        param.put(key, values[0]);
      }
    }
    String fileName = "下载数据";
    headersMap.put("sequence", "序号");
    headersMap.put("name", "案件来源");
    headersMap.put("num", "数量");
    headersMap.put("hb", "环比 ");
    headerMapWidth.put("0", 1000);
    headerMapWidth.put("2", 2000);
    headerMapWidth.put("3", 2000);

    List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
    if (flag == 2) {

      requestParam.put("pageSize", 0);
    }
    if (type == 1) {
      Paging page = (Paging) ajlbHbTotal(requestParam);
      list = page.getList();
      fileName = "案件类别分析";
      headerMapWidth.put("1", 17000);
    }
    if (type == 2) {
      Paging page = (Paging) ajzmHbTotal(requestParam);
      list = page.getList();
      fileName = "案件罪名分析";
      headerMapWidth.put("1", 17000);
    }
    if (type == 3) {
      Paging page = (Paging) ajlyHbTotal(requestParam);
      list = page.getList();
      fileName = "案件来源分析";
      headerMapWidth.put("1", 5000);
    }
    list = tolist(list);

    Integer[] lockedArray = new Integer[] { 15 };// 锁定列
    ArrayList<Integer> lockedList = new ArrayList<Integer>(Arrays.asList(lockedArray));
    ExcelUtil2019.exportExcel(headersMap, list, os, lockedList, headerMapWidth);
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

  private List<Map<String, Object>> tolist(List<Map<String, Object>> list) {
    int i = 1;
    for (Map<String, Object> map : list) {
      map.put("sequence", i);
      i++;
    }
    return list;

  }
}
