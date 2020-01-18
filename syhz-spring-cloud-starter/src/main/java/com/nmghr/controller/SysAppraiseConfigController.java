/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nmghr.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.common.Result;
import com.nmghr.basic.common.exception.GlobalErrorException;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.util.ValidationUtils;
import com.nmghr.util.SyhzUtil;

/**
 * <功能描述/> 考核评比配置项配置
 * 
 * @author brook
 * @date 2020年1月15日 上午10:33:57
 * @version 1.0
 */
@RestController
@RequestMapping("/khpb")
public class SysAppraiseConfigController {

  private static final Logger log = LoggerFactory.getLogger(SysAppraiseConfigController.class);
  @Autowired
  @Qualifier("baseService")
  private IBaseService baseService;

  @GetMapping("/configDataList/{category}")
  @ResponseBody
  public Object getConfigData(@PathVariable Integer category) throws Exception {
    Map<String, Object> totalList = new HashMap<String, Object>();
    log.info("getConfigData param{}", category);

    if (null != category && category > 0) {
      List<Map<String, Object>> hjConfigDate = getConfig(category);
      log.info("getConfigData return data list {}", hjConfigDate);
      if (category == 1) {
        totalList.put("sp", hjConfigDate);
      } else if (category == 2) {
        totalList.put("yp", hjConfigDate);
      } else if (category == 3) {
        totalList.put("hj", hjConfigDate);
      }
    } else {
      List<Map<String, Object>> hjConfigDate = getConfig(1);
      log.info("getConfigData return hj list {}", hjConfigDate);
      totalList.put("sp", hjConfigDate);
      List<Map<String, Object>> spConfigDate = getConfig(2);
      log.info("getConfigData return sp list {}", spConfigDate);
      totalList.put("yp", spConfigDate);
      List<Map<String, Object>> ypConfigDate = getConfig(3);
      log.info("getConfigData return yp list {}", ypConfigDate);
      totalList.put("hj", ypConfigDate);
    }
    return Result.ok(totalList);
  }

  @PostMapping("/updateDatas")
  @ResponseBody
  public Object updateData(@RequestBody List<Map<String, Object>> list) throws Exception {
    if (CollectionUtils.isEmpty(list)) {
      throw new GlobalErrorException("888888", "保存数据不能为空!");
    } else {
      for (Map<String, Object> requestMap : list) {
        String categoryType = requestMap.get("categoryType") + "";
        if (null != categoryType && !"".equals(categoryType)) {
          if ("1".equals(categoryType)) {
            requestMap.put("category", 1);

          } else if ("2".equals(categoryType)) {
            requestMap.put("category", 2);
          } else if ("3".equals(categoryType)) {
            requestMap.put("category", 3);
          }
          LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "SYSAPPRAISECONFIG");
          baseService.update("-1", requestMap);
        }

      }
    }
    return Result.ok(null);
  }

  /**
   * 保存个人创建的需要展示的考核项配置
   * 
   **/
  @PostMapping("/saveCategoryMaping")
  @ResponseBody
  public Object saveCategoryMaping(@RequestBody List<Map<String, Object>> requestMap) throws Exception {
    validParams(requestMap);
    for (Map<String, Object> map : requestMap) {
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "SYSAPPRAISECONFIGBYUSER");
      baseService.save(map);
    }

    return Result.ok(null);
  }

  @PostMapping("/configDataListByUser")
  @ResponseBody
  public Object getConfigDataByUser(@RequestBody Map<String, Object> requestMap) throws Exception {
    Map<String, Object> totalList = new HashMap<String, Object>();
    log.info("getConfigDataByUser param{}", requestMap);
    String category = requestMap.get("category") + "";
    String userId = requestMap.get("userId") + "";
    if (null != category && !"0".equals(category)) {
      List<Map<String, Object>> hjConfigDate = getConfigByUser(category, userId);
      log.info("getConfigDataByUser return data list {}", hjConfigDate);
      if (category.equals("1")) {
        totalList.put("sp", hjConfigDate);
      } else if (category.equals("2")) {
        totalList.put("yp", hjConfigDate);
      } else if (category.equals("3")) {
        totalList.put("hj", hjConfigDate);
      }
    } else {
      List<Map<String, Object>> hjConfigDate = getConfigByUser("1", userId);
      log.info("getConfigDataByUser return hj list {}", hjConfigDate);
      totalList.put("sp", hjConfigDate);
      List<Map<String, Object>> spConfigDate = getConfigByUser("2", userId);
      log.info("getConfigDataByUser return sp list {}", spConfigDate);
      totalList.put("yp", spConfigDate);
      List<Map<String, Object>> ypConfigDate = getConfigByUser("3", userId);
      log.info("getConfigDataByUser return yp list {}", ypConfigDate);
      totalList.put("hj", ypConfigDate);
    }
    return Result.ok(totalList);
  }

  @SuppressWarnings("unchecked")
  public List<Map<String, Object>> getConfig(int category) throws Exception {
    Map<String, Object> requestMap = new HashMap<String, Object>();
    requestMap.put("category", category);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "SYSAPPRAISECONFIG");
    List<Map<String, Object>> dateList = (List<Map<String, Object>>) baseService.list(requestMap);
    return dateList;
  }

  @SuppressWarnings("unchecked")
  public List<Map<String, Object>> getConfigByUser(String category, String userId) throws Exception {
    Map<String, Object> requestMap = new HashMap<String, Object>();
    requestMap.put("category", category);
    requestMap.put("userId", userId);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "SYSAPPRAISECONFIGBYUSER");
    List<Map<String, Object>> dateList = (List<Map<String, Object>>) baseService.list(requestMap);
    return dateList;
  }

  /**
   * 开始考试校验
   *
   * @param requestBody
   */
  @SuppressWarnings("unchecked")
  private void validParams(List<Map<String, Object>> list) {
    if (CollectionUtils.isEmpty(list)) {
      throw new GlobalErrorException("888888", "保存数据不能为空!");
    }

    for (Map<String, Object> map : list) {
      try {
        ValidationUtils.notNull(map.get("userId"), "创建人ID不能为空!");
        ValidationUtils.notNull(map.get("userName"), "创建人不能为空!");
        ValidationUtils.notNull(map.get("categoryList"), "配置项不能为空!");
      } catch (Exception e) {
        // TODO: handle exception
        e.getStackTrace();
      }

    }
  }

  @GetMapping(value = "/total")
  @ResponseBody
  public Object total(@RequestParam Map<String, Object> params) throws Exception {
    Map<String, Object> configMap = new HashMap<String, Object>();
    List<Map<String, Object>> y = new ArrayList<Map<String, Object>>();
    Map<String, Object> reponseMap = new HashMap<String, Object>();

    int caseNum = 0;// 案件状态统计
    int dbNum = 0;// 督办统计
    int xyrNum = 0;// 抓获人数统计
    int xcNum = 0;// 协查统计
    int zyNum = 0;// 战役统计

    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "SYSAPPRASIEPERSON");
    List<Map<String, Object>> yList = (List<Map<String, Object>>) baseService.list(params);
    if (yList == null || yList.size() == 0) {
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "SYSAPPRASIEALL");
      yList = (List<Map<String, Object>>) baseService.list(params);
    }
    for (Map<String, Object> map : yList) {
      int level = SyhzUtil.setDateInt(map.get("level"));
      String sign = SyhzUtil.setDate(map.get("sign"));
      String category = SyhzUtil.setDate(map.get("category"));
      if (level == 3) {
        String yN = sign + category.substring(0, 1);
        configMap.put(yN, map);
        params.put(yN, 1);
        map.put("yN", yN);
        y.add(map);
        if ("la".equals(sign) || "pa".equals(sign) || "xa".equals(sign) || "ja".equals(sign)) {
          caseNum++;// 如果有立案，结案，销案，破案 加一
        } else if (sign.contains("daj")) {
          dbNum++;// 如果有督办相关 加一
        } else if ("zhrs".equals(sign)) {
          xyrNum++;// 如果有抓获人数相关 加一
        } else if ("aqwcxcaj".equals(sign) || "aqwwcxcaj".equals(sign)) {
          xcNum++;// 如果有协查相关 加一
        } else if ("aqwcjqhcaj".equals(sign) || "aqwwcjqhcaj".equals(sign)) {
          zyNum++;// 如果有战役相关 加一
        }
      }
    }
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "SYSCONFIGX");
    List<Map<String, Object>> xList = (List<Map<String, Object>>) baseService.list(params);
    if (caseNum > 0) {// 有案件状态的统计项目
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "SYSAPPRAISECASE");
      List<Map<String, Object>> caseList = (List<Map<String, Object>>) baseService.list(params);
      setList(xList, caseList);
    }
    if (caseNum > 0) {// 有督办的统计项目
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "SYSCONFIGKHPB");
      List<Map<String, Object>> configList = (List<Map<String, Object>>) baseService.list(params);
      for (Map<String, Object> config : configList) {
        String configKey = SyhzUtil.setDate(config.get("configKey"));
        int configValue = SyhzUtil.setDateInt(config.get("configValue"));
        params.put(configKey, configValue);
      }
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "SYSAPPRAISEDB");
      List<Map<String, Object>> dbList = (List<Map<String, Object>>) baseService.list(params);
      setList(xList, dbList);
    }

    if (xyrNum > 0) {// 有抓获人数的统计项目
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "SYSAPPRAISEXYR");
      List<Map<String, Object>> caseList = (List<Map<String, Object>>) baseService.list(params);
      setList(xList, caseList);
    }

    if (xcNum > 0) {// 有协查的统计项目
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "SYSAPPRAISEXC");
      List<Map<String, Object>> caseList = (List<Map<String, Object>>) baseService.list(params);
      setList(xList, caseList);
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "SYSAPPRAISEXCJ");
      Map<String, Object> xchj = (Map<String, Object>) baseService.get(params);
      reponseMap.put("xzhj", xchj);
    }

    if (zyNum > 0) {// 有战役的统计项目
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "SYSAPPRAISEZY");
      List<Map<String, Object>> caseList = (List<Map<String, Object>>) baseService.list(params);
      setList(xList, caseList);
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "SYSAPPRAISEZYHJ");
      Map<String, Object> xchj = (Map<String, Object>) baseService.get(params);
      reponseMap.put("zyhj", xchj);
    }
    setGrade(xList, configMap, y);
    setTableHead(yList);// 表头加计分列
    reponseMap.put("tableHead", yList);
    reponseMap.put("list", xList);
    return reponseMap;
  }

  private void setList(List<Map<String, Object>> xList, List<Map<String, Object>> lsit) {
    for (int i = 0; i < xList.size(); i++) {
      Map<String, Object> map1 = xList.get(i);
      Map<String, Object> map2 = lsit.get(i);
      map1.putAll(map2);
    }
  }

  private void setGrade(List<Map<String, Object>> list, Map<String, Object> configMap, List<Map<String, Object>> y) {
    for (Map<String, Object> ynMap : y) {
      String yn = SyhzUtil.setDate(ynMap.get("yN"));
      String parent = SyhzUtil.setDate(ynMap.get("parentId"));
      Map<String, Object> yMap = (Map<String, Object>) configMap.get(yn);
      BigDecimal target = new BigDecimal(SyhzUtil.setDate(yMap.get("targetNumber")));// 合格数
      BigDecimal addValue = new BigDecimal(SyhzUtil.setDate(yMap.get("addValue"))); // 加分
      BigDecimal subValue = new BigDecimal(SyhzUtil.setDate(yMap.get("subtractValue")));// 减分
      String type = yn.substring(yn.length() - 1, yn.length());
      for (Map<String, Object> xMap : list) {
        if ("".equals(SyhzUtil.setDate(xMap.get(yn)))) {
          xMap.put(yn, "0");
        }
        BigDecimal num = new BigDecimal(SyhzUtil.setDate(xMap.get(yn)));
        if ("".equals(SyhzUtil.setDate(xMap.get("gradeB" + type)))) {
          xMap.put("gradeB" + type, "0");
        }
        if ("".equals(SyhzUtil.setDate(xMap.get("gradeA" + parent)))) {
          xMap.put("gradeA" + parent, "0");
        }
        if ("".equals(SyhzUtil.setDate(xMap.get("gradeC")))) {
          xMap.put("gradeC", "0");
        }
        BigDecimal grade = new BigDecimal("0");
        BigDecimal gradeA = new BigDecimal(SyhzUtil.setDate(xMap.get("gradeA" + parent)));
        BigDecimal gradeB = new BigDecimal(SyhzUtil.setDate(xMap.get("gradeB" + type)));
        BigDecimal gradeC = new BigDecimal(SyhzUtil.setDate(xMap.get("gradeC")));

        if (!yn.contains("wwc") && !yn.contains("wzj")) {// 是否为未完成减分项
          if (num.compareTo(target) == 1) {
            grade = (num.subtract(target)).multiply(addValue);
          } else if (num.compareTo(target) == 0) {
          } else if (num.compareTo(target) == -1) {
            grade = ((target.subtract(num)).multiply(subValue)).negate();
          }
        } else {
          grade = num.multiply(subValue).negate();
        }
        gradeA = gradeA.add(grade);
        gradeB = gradeB.add(grade);
        gradeC = gradeC.add(grade);
        xMap.put("gradeA" + parent, gradeA);
        xMap.put("gradeB" + type, gradeB);
        xMap.put("gradeC", gradeC);
        xMap.put(yn, num);
      }

    }
  }

  // 表头处理
  private void setTableHead(List<Map<String, Object>> yList) {
    List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
    for (Map<String, Object> map : yList) {
      int level = SyhzUtil.setDateInt(map.get("level"));
      if (level == 2) {// 加入三级合计
        int id = SyhzUtil.setDateInt(map.get("id"));
        String category = SyhzUtil.setDate(map.get("category"));
        Map<String, Object> th = new HashMap<String, Object>();
        th.put("parentId", id);
        th.put("id", id + 100);
        th.put("category", category);
        th.put("sign", "gradeA" + id);
        th.put("categoryName", "计分");
        th.put("level", 3);
        list.add(th);
      }
      if (level == 1) {// 加入二级合计
        int id = SyhzUtil.setDateInt(map.get("id"));
        String category = SyhzUtil.setDate(map.get("category"));
        Map<String, Object> th = new HashMap<String, Object>();
        th.put("parentId", id);
        th.put("id", id + 100);
        th.put("sign", "gradeB");
        th.put("categoryName", "计分");
        th.put("level", 2);
        list.add(th);
      }
    }
    Map<String, Object> map = new HashMap<String, Object>();
    map.put("parentId", 1);
    map.put("id", 101);
    map.put("sign", "gradeC");
    map.put("categoryName", "总计分");
    map.put("level", 1);
    yList.add(map);// 加入一级合计
    yList.addAll(list);
  }

}
