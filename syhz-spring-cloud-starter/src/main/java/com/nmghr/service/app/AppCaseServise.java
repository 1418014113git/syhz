package com.nmghr.service.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.page.Paging;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.util.ValidationUtils;
import com.nmghr.entity.query.Page;
import com.nmghr.entity.query.QueryResult;
import com.nmghr.service.template.ReportTemplateService;
import com.nmghr.util.SyhzUtil;
import com.nmghr.util.app.AppVerifyUtils;
import com.nmghr.util.app.Result;
import com.nmghr.vo.QueryRequestVo;

/**
 * APP案件管理
 * 
 * @author heijiantao
 * @date 2019年12月4日
 * @version 1.0
 */
@Service
public class AppCaseServise {

  @Autowired
  private ReportTemplateService reportTemplateService;

  // 首页待审核
  public Object workGroup(QueryRequestVo queryRequestVo, Map<String, Object> requestBody, Map<String, Object> param,
      IBaseService baseService) throws Exception {
    param.put("id", param.get("deptCode"));
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "APPWORKGROUPONE");// 审核代办1
    Map<String, Object> map1 = (Map<String, Object>) baseService.get(param);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "APPWORKGROUPTWO");// 知识库审待审核
    Map<String, Object> map2 = (Map<String, Object>) baseService.get(param);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "APPWORKGROUPTHREE");// 签收代办2
    Map<String, Object> map3 = (Map<String, Object>) baseService.get(param);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "APPWORKGROUPFOUR");// 其他代办4
    Map<String, Object> map4 = (Map<String, Object>) baseService.get(param);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "APPWORKGROUPFIVE");// 催办代办3
    Map<String, Object> map5 = (Map<String, Object>) baseService.get(param);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "MERGEWAIT");// 重复合并
    Map<String, Object> map6 = (Map<String, Object>) baseService.get(param);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "APPSIGNCOUNTAJRL");// 案件认领
    Map<String, Object> map7 = (Map<String, Object>) baseService.get(param);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJCLUSTERNEEDFEEDBACK");// 集群战役待反馈
    Map<String, Object> map8 = (Map<String, Object>) baseService.get(param);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "APPBASEMESSAGESIGNCOUNT");// 站内通知签收代办
    Map<String, Object> map9 = (Map<String, Object>) baseService.get(param);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJGLQBXSSIGNHOME");// 集群战役签收代办
    List<Map<String, Object>> map10 = (List<Map<String, Object>>) baseService.list(param);
    int num = SyhzUtil.setDateInt(map1.get("num")) + SyhzUtil.setDateInt(map2.get("num"));// 审核待办
    int num2 = SyhzUtil.setDateInt(map3.get("num")) + SyhzUtil.setDateInt(map9.get("num"));// 签收待办
    int num3 = SyhzUtil.setDateInt(map4.get("num")) + SyhzUtil.setDateInt(map5.get("num"))
        + SyhzUtil.setDateInt(map8.get("num")) + SyhzUtil.setDateInt(map7.get("num"));// 其他待办
    int num4 = SyhzUtil.setDateInt(map6.get("num"));// 催办待办
    if (map10 != null && map10.size() > 0) {
      for (Map<String, Object> map : map10) {
        num2 += SyhzUtil.setDateInt(map.get("num"));
      }
    }
    map1.put("num", num);
    map3.put("num", num2);
    map4.put("num", num3);
    map5.put("num", num4);
    List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
    list.add(map1);
    list.add(map3);
    list.add(map4);
    list.add(map5);

    QueryResult result = AppVerifyUtils.setQueryResult(queryRequestVo, list);
    return Result.ok(queryRequestVo.getJsonrpc(), queryRequestVo.getId(), result);
  }

  // 案件列表
  public Object caseList(QueryRequestVo queryRequestVo, Map<String, Object> requestBody, Map<String, Object> param,
      IBaseService baseService) throws Exception {

    // 获取分页信息
    Page page = AppVerifyUtils.getQueryPage(queryRequestVo);
    int pageSize = page.getPageSize();
    int pageNo = page.getPageNo();
    // 查询
    ValidationUtils.notNull(param.get("curDeptCode"), "当前登录部门Code不能为空");
    ValidationUtils.notNull(param.get("templateId"), "模板ID不能为空");

    String orderBy = buildOrder(param);
    param.put("orderBy", orderBy);
    if (param.containsKey("area") && !StringUtils.isEmpty(param.get("area"))) {
      String area = String.valueOf(param.get("area"));
      String[] areaArr = area.split(",");
      if (param.containsKey("department") && !StringUtils.isEmpty(param.get("department"))) {
        String department = String.valueOf(param.get("department"));
        String[] departmentArr = department.split(",");
        param.put("deptCategory", "2");
        param.put("deptCode", departmentArr[departmentArr.length - 1]);
      } else {
        if (areaArr.length > 1) {
          param.put("deptCategory", "1");
          param.put("deptCode", areaArr[areaArr.length - 1]);
          if (areaArr.length <= 2) {
            param.put("splitLen", "4");
          } else if (areaArr.length == 3) {
            param.put("splitLen", "6");

          }
        }
      }

    }
    param.put("pageNum", pageNo);
    param.put("pageSize", pageSize);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "APPAJJBXXSYH");

    // 返回分页信息
    Paging pageMap = (Paging) baseService.page(param, pageNo, pageSize);
    List<Map<String, Object>> messageList = (List<Map<String, Object>>) pageMap.getList();
    int total = (int) pageMap.getTotalCount();
    QueryResult result = AppVerifyUtils.setQueryPageResult(queryRequestVo, pageNo, pageSize, total, messageList);
    return Result.ok(queryRequestVo.getJsonrpc(), queryRequestVo.getId(), result);
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
      i++;
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

  // 案件详情
  public Object caseDetail(QueryRequestVo queryRequestVo, Map<String, Object> requestBody,
      Map<String, Object> conditionMap, IBaseService baseService) throws Exception {

    // 查询
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "APPAJJXXSYHID");
    conditionMap.put("id", conditionMap.get("ajlb"));
    Map<String, Object> pageMap = (Map<String, Object>) baseService.get(conditionMap);
    List<Map<String, Object>> messageList = new ArrayList<Map<String, Object>>();
    messageList.add(pageMap);
    QueryResult result = AppVerifyUtils.setQueryResult(queryRequestVo, messageList);
    return Result.ok(queryRequestVo.getJsonrpc(), queryRequestVo.getId(), result);
  }

  // 案件罪名
  public Object caseAjzm(QueryRequestVo queryRequestVo, Map<String, Object> requestBody,
      Map<String, Object> conditionMap, IBaseService baseService) throws Exception {

    // 查询
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJZM");
    List<Map<String, Object>> messageList = (List<Map<String, Object>>) baseService.list(conditionMap);

    QueryResult result = AppVerifyUtils.setQueryResult(queryRequestVo, messageList);
    return Result.ok(queryRequestVo.getJsonrpc(), queryRequestVo.getId(), result);
  }

  public Object caseAjzmCode(QueryRequestVo queryRequestVo, Map<String, Object> requestBody,
      Map<String, Object> conditionMap, IBaseService baseService) throws Exception {

    // 查询
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJZMCODE");
    List<Map<String, Object>> messageList = (List<Map<String, Object>>) baseService.list(conditionMap);

    QueryResult result = AppVerifyUtils.setQueryResult(queryRequestVo, messageList);
    return Result.ok(queryRequestVo.getJsonrpc(), queryRequestVo.getId(), result);
  }

  // 案件类别
  public Object caseAjlb(QueryRequestVo queryRequestVo, Map<String, Object> requestBody,
      Map<String, Object> conditionMap, IBaseService baseService) throws Exception {

    // 查询
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "GETAJLB");
    List<Map<String, Object>> messageList = (List<Map<String, Object>>) baseService.list(conditionMap);

    QueryResult result = AppVerifyUtils.setQueryResult(queryRequestVo, messageList);
    return Result.ok(queryRequestVo.getJsonrpc(), queryRequestVo.getId(), result);
  }

  // 案例code
  public Object caseTcpcode(QueryRequestVo queryRequestVo, Map<String, Object> requestBody,
      Map<String, Object> conditionMap, IBaseService baseService) throws Exception {

    // 查询
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "APPTCPCODE");
    List<Map<String, Object>> messageList = (List<Map<String, Object>>) baseService.list(conditionMap);

    QueryResult result = AppVerifyUtils.setQueryResult(queryRequestVo, messageList);
    return Result.ok(queryRequestVo.getJsonrpc(), queryRequestVo.getId(), result);
  }

}
