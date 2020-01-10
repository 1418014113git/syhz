package com.nmghr.controller.caseAnalyze;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.service.DeptNameService;
import com.nmghr.util.SyhzUtil;

@SuppressWarnings("unchecked")
@RestController
@RequestMapping("/caseAnalyzeDevel")
public class caseAnalyzeDevelController {

  @Autowired
  @Qualifier("baseService")
  private IBaseService baseService;
  @Autowired
  private DeptNameService DeptNameService;

  @PostMapping("/numTotal")
  @ResponseBody
  public Object numTotal(@RequestBody Map<String, Object> requestBody) throws Exception {
    requestBody.put("flag", 1);
    List<Map<String, Object>> cityList = (List<Map<String, Object>>) DeptNameService.get(requestBody);
    int isExpandTable = SyhzUtil.setDateInt(requestBody.get("isExpandTable"));// 1展开查询
    if (isExpandTable != 1) {
      Map<String, Object> map = new HashMap<String, Object>();
      map.put("cityName", "总计");
      map.put("Code", "1");
      cityList.add(map);
    }
    for (Map<String, Object> city : cityList) {
      if ("1".equals(SyhzUtil.setDate(city.get("Code")))) {
        city.put("deptCode", SyhzUtil.setDate(requestBody.get("departCode")));
        requestBody.put("departCode", "");
      }
      city.putAll(requestBody);
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "CASEANALYZEDEVELONE");
      Map<String, Object> totalList = (Map<String, Object>) baseService.get(city);
      city.put("totalList", totalList);

    }
    return cityList;
  }

  @PostMapping("/one")
  @ResponseBody
  // 案件类型统计
  public Object get(@RequestBody Map<String, Object> requestBody) throws Exception {
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "CASEANALYZEDEVELTWO");
    Map<String, Object> totalList = (Map<String, Object>) baseService.get(requestBody);
    return totalList;
  }

  @PostMapping("/ajztTotal")
  @ResponseBody
  // 案件状态统计
  public Object ajztTotal(@RequestBody Map<String, Object> requestBody) throws Exception {
    requestBody.put("flag", 1);
    List<Map<String, Object>> cityList = (List<Map<String, Object>>) DeptNameService.get(requestBody);
    int isExpandTable = SyhzUtil.setDateInt(requestBody.get("isExpandTable"));// 1展开查询
    if (isExpandTable != 1) {
      Map<String, Object> map = new HashMap<String, Object>();
      map.put("cityName", "总计");
      map.put("Code", "1");
      cityList.add(map);
    }
    for (Map<String, Object> city : cityList) {
      if ("1".equals(SyhzUtil.setDate(city.get("Code")))) {
        city.put("deptCode", SyhzUtil.setDate(requestBody.get("departCode")));
        requestBody.put("departCode", "");
      }
      city.putAll(requestBody);
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "CASEANALYZEDEVELTHREE");
      Map<String, Object> totalList = (Map<String, Object>) baseService.get(city);
      city.put("totalList", totalList);

    }
    return cityList;
  }
}
