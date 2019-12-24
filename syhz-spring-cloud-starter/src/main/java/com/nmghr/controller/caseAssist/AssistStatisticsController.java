package com.nmghr.controller.caseAssist;

import com.alibaba.fastjson.JSONObject;
import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.util.ValidationUtils;
import com.nmghr.service.ajglqbxs.AjglQbxsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@SuppressWarnings("unchecked")
@RestController
@RequestMapping("/assistStatistics")
public class AssistStatisticsController {
  private static final Logger log = LoggerFactory.getLogger(AssistStatisticsController.class);
  @Autowired
  private IBaseService baseService;

  @Autowired
  private AjglQbxsService ajglQbxsService;

  /**
   * 案件协查列表 反馈战果统计
   *
   * @return
   */
  @GetMapping("/assistAj")
  @ResponseBody
  public Object assistAj(@RequestParam Map<String, Object> params) {
    ValidationUtils.notNull(params.get("type"), "type不能为空!");
    ValidationUtils.regexp(params.get("type"), "^[12]$", "非法输入");
    try {
      Map<String, Object> p = new HashMap<>();
      p.put("type", Integer.parseInt(String.valueOf(params.get("type"))));
      if (params.get("fllb") != null && !StringUtils.isEmpty(String.valueOf(params.get("fllb")))) {
        p.put("fllb", Integer.parseInt(String.valueOf(params.get("fllb"))));
      }
      if (!StringUtils.isEmpty(params.get("start"))) {
        p.put("start", params.get("start"));
      }
      if (!StringUtils.isEmpty(params.get("end"))) {
        p.put("end", params.get("end"));
      }
      if (!StringUtils.isEmpty(params.get("deptType"))) {
        p.put("deptType", params.get("deptType"));
      }
      if (!StringUtils.isEmpty(params.get("deptCode"))) {
        p.put("deptCode", params.get("deptCode"));
      }
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "ASSISTINFOTJ");
      List<Map<String, Object>> list = (List<Map<String, Object>>) baseService.list(p);
      if (list == null || list.size() == 0) {
        return new ArrayList<>();
      }
      // 查询发起数量 及所有部门字段
      Map<String, Object> deptCodeMap = new LinkedHashMap<>();
      for (Map m : list) {
        m.put("cyNum", 0);
        m.put("larqCount", 0);
        m.put("parqCount", 0);
        m.put("sajz", 0);
        m.put("zhrys", 0);
        m.put("pzdb", 0);
        m.put("yjss", 0);
        m.put("dhwd", 0);
        m.put("spnum", 0);
        m.put("ypnum", 0);
        m.put("hjnum", 0);
        m.put("fllbnum", 0);
        m.put("xsjl", 0);
        deptCodeMap.put(String.valueOf(m.get("deptCode")), m);
      }
      // 统计部门参与的集群信息
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "ASSISTINFOTJCYINFO");
      List<Map<String, Object>> cylist = (List<Map<String, Object>>) baseService.list(p);
      if (cylist != null && cylist.size() > 0) {
        for (Map m : cylist) {
          Map<String, Object> cyMap = (Map<String, Object>) deptCodeMap.get(String.valueOf(m.get("deptCode")));
          if (cyMap != null) {
            cyMap.put("cyNum", m.get("cyNum"));
          }
        }
      }
      // 统计部门 反馈的案件相关信息
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "ASSISTINFOTJAJBHS");
      List<Map<String, Object>> ajbhlist = (List<Map<String, Object>>) baseService.list(p);
      if (ajbhlist != null && ajbhlist.size() > 0) {
        //整理部门对应的案件编号集合
        Map<String, Object> deptAjbhs = new HashMap<>();
        for (Map m : ajbhlist) {
          String deptCode = String.valueOf(m.get("deptCode"));
          if (deptAjbhs.get(deptCode) != null) {
            List<String> ajbhs = (List<String>) deptAjbhs.get(deptCode);
            ajbhs.addAll(Arrays.asList(String.valueOf(m.get("ajbhs")).split(",")));
            deptAjbhs.put(deptCode, ajbhs);
          } else {
            List<String> ajbhs = new ArrayList<>();
            ajbhs.addAll(Arrays.asList(String.valueOf(m.get("ajbhs")).split(",")));
            deptAjbhs.put(deptCode, ajbhs);
          }
        }
        //组装信息
        for (String key : deptAjbhs.keySet()) {
          List<String> ajbhs = (List<String>) deptAjbhs.get(key);
          Map<String, Object> res = ajglQbxsService.getAjInfoCountData(ajbhs, p.get("fllb"));
          Map<String, Object> temp = (Map<String, Object>) deptCodeMap.get(key);
          temp.putAll(res);
        }
      }
      //整理页面数据
      if (StringUtils.isEmpty(params.get("deptType")) || "1".equals(String.valueOf(params.get("deptType")))) {
        return groupByAj(new ArrayList(deptCodeMap.values()));
      }
      return deptCodeMap.values();
    } catch (Exception e) {

    }
    return new ArrayList<>();
  }

  /**
   * 案件协查列表 反馈战果统计
   *
   * @return
   */
  @GetMapping("/assistFk")
  @ResponseBody
  public Object list(@RequestParam Map<String, Object> params) {
//    deptCode, deptName,cityCode,cityName,    cyNum,    fqNum,    whc,    cs,    cf,    qbxsNum
    ValidationUtils.notNull(params.get("type"), "type不能为空!");
    ValidationUtils.regexp(params.get("type"), "^[12]$", "非法输入");
    try {
      Map<String, Object> p = new HashMap<>();
      p.put("type", Integer.parseInt(String.valueOf(params.get("type"))));
      if (params.get("fllb") != null && !StringUtils.isEmpty(String.valueOf(params.get("fllb")))) {
        p.put("fllb", Integer.parseInt(String.valueOf(params.get("fllb"))));
      }
      if (!StringUtils.isEmpty(params.get("start"))) {
        p.put("start", params.get("start"));
      }
      if (!StringUtils.isEmpty(params.get("end"))) {
        p.put("end", params.get("end"));
      }
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "ASSISTINFOTJ");
      List<Map<String, Object>> list = (List<Map<String, Object>>) baseService.list(p);
      if (list == null || list.size() == 0) {
        return new ArrayList<>();
      }
      // 查询发起数量 及所有部门字段
      Map<String, Object> deptCodeMap = new LinkedHashMap<>();
      for (Map m : list) {
        m.put("cyNum", 0);
        m.put("larqCount", 0);
        m.put("parqCount", 0);
        m.put("sajz", 0);
        m.put("zhrys", 0);
        m.put("pzdb", 0);
        m.put("yjss", 0);
        m.put("dhwd", 0);
        m.put("xsjl", 0);
        m.put("whc", 0);
        m.put("cs", 0);
        m.put("cf", 0);
        m.put("hcl", "0.00");
        m.put("qbxsNum", 0);
        deptCodeMap.put(String.valueOf(m.get("deptCode")), m);
      }
      // 统计部门参与的集群信息
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "ASSISTINFOTJCYINFO");
      List<Map<String, Object>> cylist = (List<Map<String, Object>>) baseService.list(p);
      if (cylist != null && cylist.size() > 0) {
        for (Map m : cylist) {
          Map<String, Object> cyMap = (Map<String, Object>) deptCodeMap.get(String.valueOf(m.get("deptCode")));
          if (cyMap != null) {
            cyMap.put("cyNum", m.get("cyNum"));
          }
        }
      }
      // 统计部门线索查询相关信息
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "ASSISTINFOTJHCINFOS");
      List<Map<String, Object>> qbxslist = (List<Map<String, Object>>) baseService.list(p);
      if (qbxslist != null && qbxslist.size() > 0) {
        for (Map m : qbxslist) {
          Map<String, Object> qbxsMap = (Map<String, Object>) deptCodeMap.get(String.valueOf(m.get("deptCode")));
          if (qbxsMap != null) {
            qbxsMap.put("whc", m.get("whc"));
            qbxsMap.put("cs", m.get("cs"));
            qbxsMap.put("cf", m.get("cf"));
            qbxsMap.put("qbxsNum", m.get("qbxsNum"));
          } else {
            qbxsMap.put("whc", 0);
            qbxsMap.put("cs", 0);
            qbxsMap.put("cf", 0);
            qbxsMap.put("qbxsNum", 0);
          }
        }
      }

      // 统计部门 反馈的案件相关信息
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "ASSISTINFOTJAJBHS");
      List<Map<String, Object>> ajbhlist = (List<Map<String, Object>>) baseService.list(p);
      if (ajbhlist != null && ajbhlist.size() > 0) {
        //整理部门对应的案件编号集合
        Map<String, Object> deptAjbhs = new HashMap<>();
        for (Map m : ajbhlist) {
          String deptCode = String.valueOf(m.get("deptCode"));
          if (deptAjbhs.get(deptCode) != null) {
            List<String> ajbhs = (List<String>) deptAjbhs.get(deptCode);
            ajbhs.addAll(Arrays.asList(String.valueOf(m.get("ajbhs")).split(",")));
            deptAjbhs.put(deptCode, ajbhs);
          } else {
            List<String> ajbhs = new ArrayList<>();
            ajbhs.addAll(Arrays.asList(String.valueOf(m.get("ajbhs")).split(",")));
            deptAjbhs.put(deptCode, ajbhs);
          }
        }
        //组装信息
        for (String key : deptAjbhs.keySet()) {
          List<String> ajbhs = (List<String>) deptAjbhs.get(key);
          Map<String, Object> res = ajglQbxsService.getAjInfoData(ajbhs);
          Map<String, Object> temp = (Map<String, Object>) deptCodeMap.get(key);
          temp.putAll(res);
        }
      }
      //整理页面数据
      if (StringUtils.isEmpty(params.get("deptType")) || "1".equals(String.valueOf(params.get("deptType")))) {
        return groupByFk(new ArrayList(deptCodeMap.values()));
      }
      return deptCodeMap.values();
    } catch (Exception e) {

    }
    return new ArrayList<>();
  }

  private Object groupByAj(List<Map<String, Object>> list) {
    Map<String, Object> city = new LinkedHashMap<>();
    for (Map m : list) {
      String cityName = String.valueOf(m.get("cityName"));
      if (city.get(cityName) != null) {
        Map<String, Object> c = (Map<String, Object>) city.get(cityName);
        c.put("cyNum", Integer.parseInt(String.valueOf(c.get("cyNum"))) + Integer.parseInt(String.valueOf(m.get("cyNum"))));
        c.put("fqNum", Integer.parseInt(String.valueOf(c.get("fqNum"))) + Integer.parseInt(String.valueOf(m.get("fqNum"))));
        c.put("larqCount", Integer.parseInt(String.valueOf(c.get("larqCount"))) + Integer.parseInt(String.valueOf(m.get("larqCount"))));
        c.put("parqCount", Integer.parseInt(String.valueOf(c.get("parqCount"))) + Integer.parseInt(String.valueOf(m.get("parqCount"))));
        c.put("sajz", new BigDecimal(String.valueOf(c.get("sajz"))).add(new BigDecimal(String.valueOf(m.get("sajz")))).setScale(4, RoundingMode.DOWN).toString());
        c.put("zhrys", Integer.parseInt(String.valueOf(c.get("zhrys"))) + Integer.parseInt(String.valueOf(m.get("zhrys"))));
        c.put("pzdb", Integer.parseInt(String.valueOf(c.get("pzdb"))) + Integer.parseInt(String.valueOf(m.get("pzdb"))));
        c.put("yjss", Integer.parseInt(String.valueOf(c.get("yjss"))) + Integer.parseInt(String.valueOf(m.get("yjss"))));
        c.put("dhwd", Integer.parseInt(String.valueOf(c.get("dhwd"))) + Integer.parseInt(String.valueOf(m.get("dhwd"))));
        c.put("spnum", Integer.parseInt(String.valueOf(c.get("spnum"))) + Integer.parseInt(String.valueOf(m.get("spnum"))));
        c.put("ypnum", Integer.parseInt(String.valueOf(c.get("ypnum"))) + Integer.parseInt(String.valueOf(m.get("ypnum"))));
        c.put("hjnum", Integer.parseInt(String.valueOf(c.get("hjnum"))) + Integer.parseInt(String.valueOf(m.get("hjnum"))));
        c.put("fllbnum", Integer.parseInt(String.valueOf(c.get("fllbnum"))) + Integer.parseInt(String.valueOf(m.get("fllbnum"))));
        c.put("xsjl", Integer.parseInt(String.valueOf(c.get("xsjl"))) + Integer.parseInt(String.valueOf(m.get("xsjl"))));
        List<Map<String, Object>> dArray = (List<Map<String, Object>>) c.get("list");
        Map<String, Object> cn = new HashMap<>();
        cn.putAll(m);
        dArray.add(cn);
        c.put("list", dArray);
        city.put(cityName, c);
      } else {
        Map<String, Object> c = new HashMap<>();
        c.putAll(m);
        List<Map<String, Object>> dArray = new ArrayList();
        Map<String, Object> cn = new HashMap<>();
        cn.putAll(m);
        dArray.add(cn);
        c.put("list", dArray);
        city.put(cityName, c);
      }
    }
    return city.values();
  }

  private Object groupByFk(List<Map<String, Object>> list) {
    Map<String, Object> city = new LinkedHashMap<>();
    for (Map m : list) {
      BigDecimal qbxsNum = new BigDecimal(String.valueOf(m.get("qbxsNum")));
      if (qbxsNum.compareTo(BigDecimal.ZERO) > 0) {
        int hc = Integer.parseInt(String.valueOf(m.get("cs"))) + Integer.parseInt(String.valueOf(m.get("cf")));
        m.put("hcl", new BigDecimal(String.valueOf(hc)).divide(qbxsNum, 4, RoundingMode.HALF_UP).multiply(oneHundred).setScale(2, RoundingMode.DOWN).toString());
      } else {
        m.put("hcl", "-");
      }
      String cityName = String.valueOf(m.get("cityName"));
      if (city.get(cityName) != null) {
        Map<String, Object> c = (Map<String, Object>) city.get(cityName);
        c.put("cyNum", Integer.parseInt(String.valueOf(c.get("cyNum"))) + Integer.parseInt(String.valueOf(m.get("cyNum"))));
        c.put("fqNum", Integer.parseInt(String.valueOf(c.get("fqNum"))) + Integer.parseInt(String.valueOf(m.get("fqNum"))));
        c.put("larqCount", Integer.parseInt(String.valueOf(c.get("larqCount"))) + Integer.parseInt(String.valueOf(m.get("larqCount"))));
        c.put("parqCount", Integer.parseInt(String.valueOf(c.get("parqCount"))) + Integer.parseInt(String.valueOf(m.get("parqCount"))));
        c.put("sajz", new BigDecimal(String.valueOf(c.get("sajz"))).add(new BigDecimal(String.valueOf(m.get("sajz")))).setScale(4, RoundingMode.DOWN).toString());
        c.put("zhrys", Integer.parseInt(String.valueOf(c.get("zhrys"))) + Integer.parseInt(String.valueOf(m.get("zhrys"))));
        c.put("pzdb", Integer.parseInt(String.valueOf(c.get("pzdb"))) + Integer.parseInt(String.valueOf(m.get("pzdb"))));
        c.put("yjss", Integer.parseInt(String.valueOf(c.get("yjss"))) + Integer.parseInt(String.valueOf(m.get("yjss"))));
        c.put("dhwd", Integer.parseInt(String.valueOf(c.get("dhwd"))) + Integer.parseInt(String.valueOf(m.get("dhwd"))));
        c.put("xsjl", Integer.parseInt(String.valueOf(c.get("xsjl"))) + Integer.parseInt(String.valueOf(m.get("xsjl"))));
        c.put("whc", Integer.parseInt(String.valueOf(c.get("whc"))) + Integer.parseInt(String.valueOf(m.get("whc"))));
        c.put("cs", Integer.parseInt(String.valueOf(c.get("cs"))) + Integer.parseInt(String.valueOf(m.get("cs"))));
        c.put("cf", Integer.parseInt(String.valueOf(c.get("cf"))) + Integer.parseInt(String.valueOf(m.get("cf"))));
        c.put("qbxsNum", Integer.parseInt(String.valueOf(c.get("qbxsNum"))) + Integer.parseInt(String.valueOf(m.get("qbxsNum"))));

        BigDecimal cQbxsNum = new BigDecimal(String.valueOf(c.get("qbxsNum")));
        if (cQbxsNum.compareTo(BigDecimal.ZERO) > 0) {
          int hc = Integer.parseInt(String.valueOf(c.get("cs"))) + Integer.parseInt(String.valueOf(c.get("cf")));
          c.put("hcl", new BigDecimal(String.valueOf(hc)).divide(cQbxsNum, 4, RoundingMode.HALF_UP).multiply(oneHundred).setScale(2, RoundingMode.DOWN).toString());
        } else {
          c.put("hcl", "-");
        }
        List<Map<String, Object>> dArray = (List<Map<String, Object>>) c.get("list");
        Map<String, Object> cn = new HashMap<>();
        cn.putAll(m);
        dArray.add(cn);
        c.put("list", dArray);
        city.put(cityName, c);
      } else {
        Map<String, Object> c = new HashMap<>();
        c.putAll(m);
        List<Map<String, Object>> dArray = new ArrayList();
        Map<String, Object> cn = new HashMap<>();
        cn.putAll(m);
        dArray.add(cn);
        c.put("list", dArray);
        city.put(cityName, c);
      }
    }
    return city.values();
  }

  private BigDecimal oneHundred = new BigDecimal("100");

}
