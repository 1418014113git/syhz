package com.nmghr.controller.statistics;

import com.alibaba.fastjson.JSONObject;
import com.nmghr.basic.common.Constant;
import com.nmghr.basic.common.exception.GlobalErrorException;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.util.ValidationUtils;
import com.nmghr.util.DateUtil;
import com.nmghr.util.app.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("unchecked")
@RestController
@RequestMapping("analysis")
public class CaseStatisticsController {
  @Autowired
  @Qualifier("baseService")
  private IBaseService baseService;

  /**
   * 案件趋势分析
   */
  @GetMapping("caseTime")
  @ResponseBody
  public Object caseTime(@RequestParam Map<String, Object> params) throws Exception {
    ValidationUtils.notNull(params.get("start"), "开始时间不能为空!");
    ValidationUtils.notNull(params.get("end"), "截止时间不能为空!");
    if (StringUtils.isEmpty(params.get("dateType"))) {
      return Result.fail("999779", "请选择筛选日期类型");
    }
//    syhFllb,childFlag, deptCode, deptType, totalMonth, totalMode
    if (!StringUtils.isEmpty(params.get("ajzt"))) {
      String[] ajzts = String.valueOf(params.get("ajzt")).split(",");
      if (ajzts.length > 0) {
        params.put("ajzt", Arrays.asList(ajzts));
      }
    } else {
      params.remove("ajzt");
    }
    String ALIAS = "AJANALYSISTIMELA";
    if ("1".equals(String.valueOf(params.get("dateType")))) {
      ALIAS = "AJANALYSISTIMELA";
    }
    if ("2".equals(String.valueOf(params.get("dateType")))) {
      ALIAS = "AJANALYSISTIMEPA";
    }
    if ("5".equals(String.valueOf(params.get("type")))) {
      params.put("type", "date");
    } else if ("1".equals(String.valueOf(params.get("type")))) {
      params.put("type", "year");
    } else if ("2".equals(String.valueOf(params.get("type")))) {
      params.put("type", "quarter");
    } else if ("3".equals(String.valueOf(params.get("type")))) {
      params.put("type", "month");
    } else {
      params.put("type", "cusmonth");
      ValidationUtils.notNull(params.get("totalMonth"), "totalMonth不能为空!");
      ValidationUtils.notNull(params.get("totalMode"), "totalMode不能为空!");
    }
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS);
    return baseService.list(params);
  }

  /**
   * 案件同比/环比分析
   */
  @GetMapping("caseContrast")
  @ResponseBody
  public Object caseContrast(@RequestParam Map<String, Object> params) throws Exception {
    ValidationUtils.notNull(params.get("start"), "开始时间不能为空!");
    ValidationUtils.notNull(params.get("end"), "截止时间不能为空!");
    if (StringUtils.isEmpty(params.get("dateType"))) {
      return Result.fail("999779", "请选择筛选日期类型");
    }
    if (!StringUtils.isEmpty(params.get("ajzt"))) {
      String[] ajzts = String.valueOf(params.get("ajzt")).split(",");
      if (ajzts.length > 0) {
        params.put("ajzt", Arrays.asList(ajzts));
      }
    } else {
      params.remove("ajzt");
    }
    String oType = String.valueOf(params.get("type"));
    String ALIAS = "AJANALYSISTIMELA";
    if ("1".equals(String.valueOf(params.get("dateType")))) {
      ALIAS = "AJANALYSISTIMELA";
    }
    if ("2".equals(String.valueOf(params.get("dateType")))) {
      ALIAS = "AJANALYSISTIMEPA";
    }
    if ("5".equals(String.valueOf(params.get("type")))) {
      params.put("type", "date");
    } else if ("1".equals(String.valueOf(params.get("type")))) {
      params.put("type", "year");
    } else if ("2".equals(String.valueOf(params.get("type")))) {
      params.put("type", "quarter");
    } else if ("3".equals(String.valueOf(params.get("type")))) {
      params.put("type", "month");
    } else {
      params.put("type", "cusmonth");
      ValidationUtils.notNull(params.get("totalMonth"), "totalMonth不能为空!");
      ValidationUtils.notNull(params.get("totalMode"), "totalMode不能为空!");
    }

    //现有统计数据
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS);
    List<Map<String, Object>> oData = (List<Map<String, Object>>) baseService.list(params);
    Map<String, Object> oriMap = new HashMap<>();
    if (oData != null && oData.size() > 0) {
      for (Map<String, Object> m : oData) {
        oriMap.put(String.valueOf(m.get("date")), m);
      }
    }

    Date start = DateUtil.strToDate(String.valueOf(params.get("start")), "yyyy-MM-dd");
    Date end = DateUtil.strToDate(String.valueOf(params.get("end")), "yyyy-MM-dd");

    Calendar t = Calendar.getInstance();
    t.setTime(start);
    t.add(Calendar.YEAR, -1);
    Date start1 = t.getTime();//上一年开始时间
//    t.setTime(end);
//    t.add(Calendar.YEAR, -1);
//    Date end1 = t.getTime();//上一年截止时间

    t.setTime(start);
    t.add(Calendar.MONTH, -1);//环比 开始时间 配置月减一个月
    Date start2 = t.getTime();
//    t.setTime(end);
//    t.add(Calendar.MONTH, -1);//环比 截止时间 配置月减一个月
//    Date end2 = t.getTime();
    if ("5".equals(oType)) {//天
      t.setTime(start);
      t.add(Calendar.DATE, -1);
      start2 = t.getTime();//环比 开始时间 减一天
//      t.setTime(end);
//      t.add(Calendar.DATE, -1);
//      end2 = t.getTime();//环比 截止时间 减一天
    } else if ("1".equals(oType)) {//年
      t.setTime(start);
      t.add(Calendar.YEAR, -1);
      start2 = t.getTime();//环比  开始时间 减一年
//      t.setTime(end);
//      t.add(Calendar.YEAR, -1);
//      end2 = t.getTime();//环比 截止时间 减一年
    } else if ("2".equals(oType)) {//季度
      t.setTime(start);
      t.add(Calendar.MONTH, -3);
      start2 = t.getTime();//环比  开始时间 减3月
//      t.setTime(end);
//      t.add(Calendar.MONTH, -3);
//      end2 = t.getTime();//环比 截止时间 减月
    } else if ("3".equals(oType)) {//自然月
      t.setTime(start);
      t.add(Calendar.MONTH, -1);
      start2 = t.getTime();//环比  开始时间 减一月
//      t.setTime(end);
//      t.add(Calendar.MONTH, -1);
//      end2 = t.getTime();//环比 截止时间 减一月
    }

    //获取同比去年数据
    Map<String, Object> yoyP = getListData(params, start1, end, ALIAS);
    //获取环比减一个单位数据
    Map<String, Object> momP = getListData(params, start2, end, ALIAS);

    Calendar cStart = Calendar.getInstance();
    cStart.setTime(start);
    Calendar cEnd = Calendar.getInstance();
    cEnd.setTime(end);

    //封装数据
    if ("5".equals(oType)) {//天
      getDateData(oriMap, yoyP, momP, cStart, cEnd);
    } else if ("1".equals(oType)) {//年
      getYearData(oriMap, yoyP, cStart, cEnd);
    } else if ("2".equals(oType)) {//季度
      getQuarterData(oriMap, yoyP, momP, cStart, cEnd);
    } else if ("3".equals(oType)) {//自然月
      getMonthData(oriMap, yoyP, momP, cStart, cEnd);
    } else {//配置
      getCusMonthData(oriMap, yoyP, momP, cStart, cEnd);
    }
    return oriMap.values();
  }
  /**
   * 整理按配置月数据
   */
  private void getCusMonthData(Map<String, Object> oData, Map<String, Object> yoyP, Map<String, Object> momP, Calendar start, Calendar end) throws ParseException {
    List<String> times = initList(4, start, end);
    monthData(oData, yoyP, momP, times);
  }

  /**
   * 整理按月数据
   */
  private void getMonthData(Map<String, Object> oData, Map<String, Object> yoyP, Map<String, Object> momP, Calendar start, Calendar end) throws ParseException {
    List<String> times = initList(3, start, end);
    monthData(oData, yoyP, momP, times);
  }

  private void monthData(Map<String, Object> oData, Map<String, Object> yoyP, Map<String, Object> momP, List<String> times) throws ParseException {
    if (oData != null && oData.size() > 0) {
      Calendar cal = Calendar.getInstance();
      for (String key : times) {
        Map<String, Object> m = (Map<String, Object>) oData.get(key);
        if(m==null){
          m = new HashMap<>();
          setData(m,null,0);
          m.put("date", key);
          oData.put(key,m);
        }
        Date date = DateUtil.strToDate(String.valueOf(m.get("date")), "yyyy年MM月");
        //环比
        cal.setTime(date);
        cal.add(Calendar.MONTH, -1);//上一月
        String beforeM = DateUtil.dateFormart(cal.getTime(), "yyyy年MM月");
        Map<String, Object> mom = (Map<String, Object>) momP.get(beforeM);
        setData(m, mom, 1);
        //同比
        cal.add(Calendar.YEAR, -1);//上一年
        cal.add(Calendar.MONTH, 1);//本月
        String beforeY = DateUtil.dateFormart(cal.getTime(), "yyyy年MM月");
        Map<String, Object> yoy = (Map<String, Object>) yoyP.get(beforeY);
        setData(m, yoy, 2);
      }
    }
  }

  /**
   * 整理按季度数据
   */
  private void getQuarterData(Map<String, Object> oData, Map<String, Object> yoyP, Map<String, Object> momP, Calendar start, Calendar end) throws ParseException {
    List<String> times = initList(2, start, end);
    if (oData != null && oData.size() > 0) {
      Calendar cal = Calendar.getInstance();
      Pattern p = Pattern.compile("(\\d{4})年第([一二三四])季度");
      for (String key : times) {
        Map<String, Object> m = (Map<String, Object>) oData.get(key);
        if(m==null){
          m = new HashMap<>();
          setData(m,null,0);
          m.put("date", key);
          oData.put(key,m);
        }
        Matcher matcher = p.matcher(String.valueOf(m.get("date")));
        if (matcher.matches()) {
          cal.set(Calendar.YEAR, Integer.parseInt(matcher.group(1)));//今年
          //设置中间月
          String q = matcher.group(2);
          if ("一".equals(q)) {
            cal.set(Calendar.MONTH, 1);
          }
          if ("二".equals(q)) {
            cal.set(Calendar.MONTH, 4);
          }
          if ("三".equals(q)) {
            cal.set(Calendar.MONTH, 7);
          }
          if ("四".equals(q)) {
            cal.set(Calendar.MONTH, 10);
          }
        } else {
          continue;
        }
        //环比
        cal.add(Calendar.MONTH, -3);//环比上一季度
        String beforeM = getQuarterString(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH));
        Map<String, Object> mom = (Map<String, Object>) momP.get(beforeM);
        setData(m, mom, 1);
        //同比
        cal.add(Calendar.YEAR, -1);//去年
        cal.add(Calendar.MONTH, 3);//去年同时
        String beforeY = getQuarterString(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH));
        Map<String, Object> yoy = (Map<String, Object>) yoyP.get(beforeY);
        setData(m, yoy, 2);
      }
    }
  }



  /**
   * 整理按年份数据
   */
  private void getYearData(Map<String, Object> oData, Map<String, Object> yoyP, Calendar start, Calendar end) throws ParseException {
    List<String> times = initList(1, start, end);
    if (oData != null && oData.size() > 0) {
      Calendar cal = Calendar.getInstance();
      for (String key : times) {
        Map<String, Object> m = (Map<String, Object>) oData.get(key);
        if(m==null){
          m = new HashMap<>();
          setData(m,null,0);
          m.put("date", key);
          oData.put(key,m);
        }
        Date date = DateUtil.strToDate(String.valueOf(m.get("date")), "yyyy年");
        //环比
        cal.setTime(date);
        cal.add(Calendar.YEAR, -1);//前一年
        String beforeY = DateUtil.dateFormart(cal.getTime(), "yyyy年");
        Map<String, Object> mom = (Map<String, Object>) yoyP.get(beforeY);
        setData(m, mom, 1);
      }
    }
  }

  /**
   * 整理按日数据
   */
  private void getDateData(Map<String, Object> oData, Map<String, Object> yoyP, Map<String, Object> momP, Calendar start, Calendar end) throws ParseException {
    List<String> times = initList(5, start, end);
    if (oData != null && oData.size() > 0) {
      Calendar cal = Calendar.getInstance();
      for (String key : times) {
        Map<String, Object> m = (Map<String, Object>) oData.get(key);
        if(m==null){
          m = new HashMap<>();
          setData(m,null,0);
          m.put("date", key);
          oData.put(key,m);
        }
        Date date = DateUtil.strToDate(String.valueOf(m.get("date")), "yyyy-MM-dd");
        //环比
        cal.setTime(date);
        cal.add(Calendar.DATE, -1);//前一日
        String beforeM = DateUtil.dateFormart(cal.getTime(), "yyyy-MM-dd");
        Map<String, Object> mom = (Map<String, Object>) momP.get(beforeM);
        setData(m, mom, 1);
        //同比
        cal.add(Calendar.DATE, 1);//同日
        cal.add(Calendar.YEAR, -1);//去年
        String beforeY = DateUtil.dateFormart(cal.getTime(), "yyyy-MM-dd");
        Map<String, Object> yoy = (Map<String, Object>) yoyP.get(beforeY);
        setData(m, yoy, 2);
      }
    }
  }

  /**
   * 整理同理环比
   */
  private void setData(Map<String, Object> m, Map<String, Object> map, int type) {
    if (m.size()==0) {
      m.put("type1", 0);
      m.put("type2", 0);
      m.put("type3", 0);
      m.put("type4", 0);
      m.put("type5", 0);
      m.put("type6", 0);
      m.put("type7", "0.00");
    }
    if (map != null) {
      if (type == 2) {
        m.put("tb_type1", map.get("type1"));
        m.put("tb_type2", map.get("type2"));
        m.put("tb_type3", map.get("type3"));
        m.put("tb_type4", map.get("type4"));
        m.put("tb_type5", map.get("type5"));
        m.put("tb_type6", map.get("type6"));
        m.put("tb_type7", map.get("type7"));
      }
      if (type == 1) {
        m.put("hb_type1", map.get("type1"));
        m.put("hb_type2", map.get("type2"));
        m.put("hb_type3", map.get("type3"));
        m.put("hb_type4", map.get("type4"));
        m.put("hb_type5", map.get("type5"));
        m.put("hb_type6", map.get("type6"));
        m.put("hb_type7", map.get("type7"));
      }
    } else {
      if (type == 2) {
        m.put("tb_type1", 0);
        m.put("tb_type2", 0);
        m.put("tb_type3", 0);
        m.put("tb_type4", 0);
        m.put("tb_type5", 0);
        m.put("tb_type6", 0);
        m.put("tb_type7", "0.00");
      }
      if (type == 1) {
        m.put("hb_type1", 0);
        m.put("hb_type2", 0);
        m.put("hb_type3", 0);
        m.put("hb_type4", 0);
        m.put("hb_type5", 0);
        m.put("hb_type6", 0);
        m.put("hb_type7", "0.00");
      }
    }

  }

  /**
   * 查询数据
   */
  private Map<String, Object> getListData(Map<String, Object> params, Date start, Date end, String alias) throws Exception {
    Map<String, Object> yoYP = new HashMap<>();
    yoYP.putAll(params);
    yoYP.put("start", DateUtil.dateFormart(start, "yyyy-MM-dd"));
    yoYP.put("end", DateUtil.dateFormart(end, "yyyy-MM-dd"));
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, alias);
    List<Map<String, Object>> yoYData = (List<Map<String, Object>>) baseService.list(yoYP);
    Map<String, Object> yoyMap = new HashMap<>();
    if (yoYData != null && yoYData.size() > 0) {
      for (Map<String, Object> m : yoYData) {
        yoyMap.put(String.valueOf(m.get("date")), m);
      }
    }
    return yoyMap;
  }


  private List<String> initList(int type, Calendar start, Calendar end) {
    List<String> times = new ArrayList<>();
    if (type==5) {//天
      do {
        times.add(DateUtil.dateFormart(start.getTime(), DateUtil.yyyyMMdd));
        start.add(Calendar.DATE, 1);
      } while (start.compareTo(end) <= 0);
    } else if (type==1) {//年
      do {
        times.add(DateUtil.dateFormart(start.getTime(), "YYYY年"));
        start.add(Calendar.YEAR, 1);
      } while (start.get(Calendar.YEAR) <= end.get(Calendar.YEAR));
    } else if (type==2) {//季度
      end.set(Calendar.DATE, 1);
      do {
        times.add(getQuarterString(start.get(Calendar.YEAR), start.get(Calendar.MONTH)));
        start.set(Calendar.DATE, 1);
        start.add(Calendar.MONTH, 3);
      } while (getQuarterBoolean(start, end));
    } else if (type==3) {
      end.set(Calendar.DATE, 1);
      do {
        times.add(DateUtil.dateFormart(start.getTime(), "YYYY年MM月"));
        start.set(Calendar.DATE, 1);
        start.add(Calendar.MONTH, 1);
      } while (start.compareTo(end) <= 0);
    } else {//配置
      int day = 20;
      String mode = "add";
      start = cusMthData(start, day, mode);
      end = cusMthData(end, day, mode);
      end.set(Calendar.DATE, 1);
      do {
        times.add(DateUtil.dateFormart(start.getTime(), "YYYY年MM月"));
        start.set(Calendar.DATE, 1);
        start.add(Calendar.MONTH, 1);
      } while (start.compareTo(end) <= 0);
    }
    return times;
  }

  private int getQuarter(int month) {
    if (0 <= month & month < 3) {
      return 1;
    }
    if (3 <= month & month < 6) {
      return 2;
    }
    if (6 <= month & month < 9) {
      return 3;
    }
    if (9 <= month & month < 12) {
      return 4;
    }
    return 0;
  }

  private boolean getQuarterBoolean(Calendar start, Calendar end) {
    int sy = start.get(Calendar.YEAR);
    int ey = end.get(Calendar.YEAR);
    int sq = getQuarter(start.get(Calendar.MONTH));
    int eq = getQuarter(end.get(Calendar.MONTH));
    if (sy < ey) {
      return true;
    }
    if (sy == ey) {
      return sq <= eq;
    }
    return false;
  }

  private Calendar cusMthData(Calendar cal, int day, String mode) {
    if (cal.get(Calendar.DATE) <= day) {
      if (!"add".equals(mode)) {
        cal.add(Calendar.MONTH, -1);
      }
    } else {
      if ("add".equals(mode)) {
        cal.add(Calendar.MONTH, 1);
      }
    }
    return cal;
  }
  /**
   * 获取季度key
   *
   * @param year
   * @param month
   * @return
   */
  private String getQuarterString(int year, int month) {
    StringBuilder beforeM = new StringBuilder();
    beforeM.append(year).append("年第");
    if (0 <= month & month < 3) {
      beforeM.append("一");
    }
    if (3 <= month & month < 6) {
      beforeM.append("二");
    }
    if (6 <= month & month < 9) {
      beforeM.append("三");
    }
    if (9 <= month & month < 12) {
      beforeM.append("四");
    }
    beforeM.append("季度");
    return beforeM.toString();
  }

}
