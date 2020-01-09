package com.nmghr.controller.template;

import com.nmghr.basic.common.Result;
import com.nmghr.basic.core.util.ValidationUtils;
import com.nmghr.service.template.CaseManageService;
import com.nmghr.service.template.ReportTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"unchecked"})
@RestController
@RequestMapping("/caseManage")
public class CaseManageController {

  @Autowired
  private CaseManageService caseManageService;

  @Autowired
  private ReportTemplateService reportTemplateService;

  @GetMapping("/template")
  @ResponseBody
  public Object queryTemplate(@RequestParam Map<String, Object> param) {
    ValidationUtils.notNull(param.get("deptCode"), "部门Code不能为空");
    try {
      param.put("reportType", "1");
      param.put("isAdd", "0");
      List<Map<String, Object>> templateColumn = reportTemplateService.query(param);
      return Result.ok(templateColumn);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return Result.ok(null);
  }

  @GetMapping("/filter")
  @ResponseBody
  public Object queryFilter(@RequestParam Map<String, Object> param) {
    ValidationUtils.notNull(param.get("templateId"), "模板ID不能为空");
    ValidationUtils.notNull(param.get("deptCode"), "部门Code不能为空");
    try {
      param.put("isSearch", "1");
      param.put("search", "asc");
      List<Map<String, Object>> templateColumn = reportTemplateService.queryTemplateColumn(param);
      return Result.ok(templateColumn);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return Result.ok(null);
  }

  @GetMapping("/tableTitle")
  @ResponseBody
  public Object queryTableTitle(@RequestParam Map<String, Object> param) {
    ValidationUtils.notNull(param.get("templateId"), "模板ID不能为空");
    ValidationUtils.notNull(param.get("deptCode"), "部门Code不能为空");
    try {
      param.put("isShow", "1");
      param.put("show", "asc");
      List<Map<String, Object>> templateColumn = reportTemplateService.queryTemplateColumn(param);
      return Result.ok(templateColumn);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return Result.ok(null);
  }

  @GetMapping("/caseList")
  @ResponseBody
  public Object queryCaseList(@RequestParam Map<String, Object> param) {
    ValidationUtils.notNull(param.get("curDeptCode"), "当前登录部门Code不能为空");
    ValidationUtils.notNull(param.get("templateId"), "模板ID不能为空");
    try {
//      // 查询条件字段
//      buildFilter(param);
//      // 查询列表字段
//      buildTitle(param);
      // 查询排序字段
      String orderBy = buildOrder(param);
      param.put("orderBy", orderBy);
      if (param.containsKey("area") && !StringUtils.isEmpty(param.get("area"))) {
        String area = String.valueOf(param.get("area"));
        String [] areaArr = area.split(",");
        if (param.containsKey("department") && !StringUtils.isEmpty(param.get("department"))) {
          String department = String.valueOf(param.get("department"));
          String [] departmentArr = department.split(",");
          param.put("deptCategory", "2");
          param.put("deptCode", departmentArr[departmentArr.length - 1]);
        } else {
          if (areaArr.length > 1) {
            param.put("deptCategory", "1");
            param.put("deptCode", areaArr[areaArr.length - 1]);
          }
          param.put("splitLen", areaArr.length == 3 ? 6 : 4);
        }
//        if (areaArr.length == 2 && areaArr[1].equals("610403")) { // 杨凌
//          param.put("deptCategory", "1");
//          param.put("deptCode", areaArr[areaArr.length - 1]);
//        } else {
//        }
      }
      Object pageList = caseManageService.page(param);
      return Result.ok(pageList);
    } catch (Exception e) {
      e.printStackTrace();
      return Result.fail(null);
    }
  }

  private String buildFilter(Map<String, Object> param) throws Exception {
    Map<String, Object> query = new HashMap<>();
    query.put("templateId", param.get("templateId"));
    query.put("isSearch", "1");
    query.put("search", "asc");
    StringBuffer filterBuffer = new StringBuffer();
    List<Map<String, Object>> filterColumn = reportTemplateService.queryTemplateColumn(query);
    for (Map<String, Object> filter : filterColumn) {
      String columnName = String.valueOf(filter.get("columnName"));
      String columnType = String.valueOf(filter.get("columnType"));
      String columnArr[] = columnName.toLowerCase().split("_");
      String filterName = columnArr[0] + (columnArr[1].substring(0, 1).toUpperCase() + columnArr[1].substring(1, columnArr[1].length()));
      if ("1".equals(columnType)) {
        filterBuffer.append("<if test=\"" + filterName + " != null and " + filterName + " != ''\"> and s." + columnName + " like '%${" + filterName + "}%' </if>");
      }
      if ("2".equals(columnType)) {
        String startKey = filterName + "Start";
        String endKey = filterName + "End";
        filterBuffer.append("<if test=\" " + startKey + " != null and " + startKey + " != ''\"> and STR_TO_DATE(s." + columnName + ", '%Y%m%d') &gt;= STR_TO_DATE(#{"+ startKey +"}, '%Y-%m-%d') </if>");
        filterBuffer.append("<if test=\" " + endKey + " != null and " + endKey + " != ''\"> and STR_TO_DATE(s." + columnName + ", '%Y%m%d') &lt;= STR_TO_DATE(#{"+ endKey +"}, '%Y-%m-%d') </if>");
      }
      if ("3".equals(columnType)) {
        filterBuffer.append("<if test=\"\"> and </if>");
      }
    }
    return filterBuffer.toString();
  }

  private String buildTitle(Map<String, Object> param) throws Exception {
    Map<String, Object> query = new HashMap<>();
    query.put("templateId", param.get("templateId"));
    query.put("isShow", "1");
    query.put("show", "asc");
    StringBuffer titleBuffer = new StringBuffer();
    List<Map<String, Object>> titleColumn = reportTemplateService.queryTemplateColumn(query);
    for (Map<String, Object> title : titleColumn) {
      String columnName = String.valueOf(title.get("columnName"));
      String columnType = String.valueOf(title.get("columnType"));
      if ("1".equals(columnType)) {

      }
      if ("2".equals(columnType)) {

      }
      if ("3".equals(columnType)) {

      }
    }
    return titleBuffer.toString();
  }

  private String buildOrder(Map<String, Object> param) throws Exception {
    Map<String, Object> query = new HashMap<>();
    query.put("templateId", param.get("templateId"));
    query.put("isSort", "1");
    query.put("sort", "desc");
    List<Map<String, Object>> orderColumn = reportTemplateService.queryTemplateColumn(query);
    StringBuffer orderBuffer = new StringBuffer();
    int i = 0;
    for (Map<String, Object> column : orderColumn) {
      i ++;
      String columnName = String.valueOf(column.get("columnName"));
      String columnArr[] = columnName.toLowerCase().split("_");
      String orderName = "";
      if (columnArr.length > 1) {
        for (int j = 0; j < columnArr.length; j++) {
          String columnValue = columnArr[j];
          if (j == 0) {
            orderName += columnValue;
          } else {
            orderName += columnValue.substring(0, 1).toUpperCase() + columnValue.substring(1, columnValue.length());
          }
        }
      } else {
        orderName = columnName.toLowerCase();
      }
      String sortType = String.valueOf(column.get("sortType"));
      String orderType = "";
      if ("0".equals(sortType)) {
        orderType = "asc";
      }
      if ("1".equals(sortType)) {
        orderType = "desc";
      }
      orderBuffer.append(", temp." + orderName + " " + orderType);
    }
    return orderBuffer.toString();
  }

}
