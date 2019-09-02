/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.nmghr.controller.statistics;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.nmghr.basic.common.Constant;
import com.nmghr.basic.common.Result;
import com.nmghr.basic.common.exception.GlobalErrorException;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;

/**
 * 统计功能
 *
 * @author weber
 * @date 2019年5月16日 上午9:31:53
 * @version 1.0
 */
@RestController
@RequestMapping("statistics")
public class StatisticsController {

  @Autowired
  @Qualifier("baseService")
  private IBaseService baseService;

  /**
   * demo 测试
   * 
   * @param params
   * @return
   * @throws Exception
   */
  @GetMapping("/ajrl")
  @ResponseBody
  public Object ajrl(@RequestParam Map<String, Object> params) throws Exception {
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "TJAJRL");
    return baseService.list(params);
  }



  /**
   * 侦破案件与打击处理列表
   * 
   * @param params
   * @param category
   * @return
   * @throws Exception
   */
  @GetMapping("/list/{category}")
  @ResponseBody
  public Object caseList(@RequestParam Map<String, Object> params, @PathVariable String category)
      throws Exception {
    if (params.get("pageNum") == null || params.get("pageSize") == null) {
      return new ArrayList<>();
    }
    int pageNum = Integer.valueOf(String.valueOf(params.get("pageNum")));
    int pageSize = Integer.valueOf(String.valueOf(params.get("pageSize")));
    // 案件与处理措施列表
    if ("case".equals(category)) {
      return caseList(params, pageNum, pageSize);
    }
    // 督办查询
    if ("supervise".equals(category)) {
      return superviseList(params, pageNum, pageSize);
    }
    // 全国协查查询
    if ("assist".equals(category)) {
      return assistList(params, pageNum, pageSize);
    }
    return new ArrayList<>();
  }


  /**
   * 案件侦破与打击处理
   * 
   * @param params
   * @return
   * @throws Exception
   */
  @SuppressWarnings({"unchecked", "rawtypes"})
  @GetMapping("/djcl")
  @ResponseBody
  public Object djclaj(@RequestParam Map<String, Object> params) throws Exception {
    String type = String.valueOf(params.get("type"));
    // 获取案件达标数
    JSONObject stander = getStandard();
    if (stander == null) {
      throw new GlobalErrorException("99950", "案件达标数未正确配置！");
    }
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "TJSTRIKE");
    List<Map<String, Object>> handles = (List<Map<String, Object>>) baseService.list(params);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "TJSOLVECASE");
    List<Map<String, Object>> solveCases = (List<Map<String, Object>>) baseService.list(params);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "TJSUPERVISE");
    List<Map<String, Object>> supervises = (List<Map<String, Object>>) baseService.list(params);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "TJCOUNTRYINEG");
    List<Map<String, Object>> nationalAssists =
        (List<Map<String, Object>>) baseService.list(params);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "TJINEGNOFEEDBACK");
    List<Map<String, Object>> noFeedBacks = (List<Map<String, Object>>) baseService.list(params);


    Map<String, Object> solveMap = initMap();
    // 侦破刑事案件
    if (solveCases != null) {
      for (Map<String, Object> map : solveCases) {
        solveMap.put(String.valueOf(map.get("cityCode")), map);
      }
      solveCases = new ArrayList(solveMap.values());
      for (Map<String, Object> map : solveCases) {
        if ("year".equals(type)) {
          if (map.get("total") != null) {
            int n = stander.getIntValue(String.valueOf(map.get("cityCode")));
            map.put("integral", calculate(new BigDecimal(String.valueOf(map.get("total"))),
                new BigDecimal(String.valueOf(n))));
          }
        } else {
          map.put("integral", 0);
        }
      }
    } else {
      solveCases = new ArrayList<>();
    }

    // 督办处理
    Collection<Map<String, Object>> dbList = null;
    if (supervises != null) {
      Map<String, Map<String, Object>> city = handleDbData(supervises);
      dbList = city.values();
    } else {
      dbList = new ArrayList<>();
    }

    // 打击处理
    if (handles != null) {
      for (Map<String, Object> map : handles) {
        Integer sum = 0;
        if (map.get("sueTotal") != null) {
          sum += Integer.valueOf(String.valueOf(map.get("sueTotal")));
        }
        if (map.get("arrestTotal") != null) {
          sum += Integer.valueOf(String.valueOf(map.get("arrestTotal")));
        }
        map.put("integral", new BigDecimal("0.3").multiply(new BigDecimal(sum.toString())));
      }
    } else {
      handles = new ArrayList<>();
    }

    // 全国协查发起数
    if (nationalAssists != null) {
      for (Map<String, Object> map : nationalAssists) {
        if (map.get("launchTotal") != null) {
          map.put("launchIntegral",
              new BigDecimal("2").multiply(new BigDecimal(String.valueOf(map.get("launchTotal")))));
        }
      }
    } else {
      nationalAssists = new ArrayList<>();
    }

    // 不按时反馈
    if (noFeedBacks != null) {
      for (Map<String, Object> map : noFeedBacks) {
        if (map.get("noFBTotal") != null) {
          map.put("noFBIntegral", new BigDecimal("0.5").multiply(
              new BigDecimal(String.valueOf(map.get("noFBTotal"))).multiply(new BigDecimal("-1"))));
        }
      }
    } else {
      noFeedBacks = new ArrayList<>();
    }

    Map<String, Object> result = new HashMap<String, Object>();
    result.put("solveCases", solveCases);// 侦破案件
    result.put("supervises", dbList);// 督办
    result.put("handle", handles);// 打击处理
    result.put("nationalAssist", nationalAssists);// 全国协查
    result.put("noFeedBacks", noFeedBacks);// 协查未反馈
    return Result.ok(result);
  }

  /**
   * 获取案件达标数
   * 
   * @return
   * @throws Exception
   */
  @SuppressWarnings("unchecked")
  private JSONObject getStandard() throws Exception {
    Map<String, Object> params = new HashMap<String, Object>();
    params.put("configGroup", "aj_standard");
    params.put("configKey", Calendar.getInstance().get(Calendar.YEAR));
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "SYSCONFIG");
    List<Map<String, Object>> list = (List<Map<String, Object>>) baseService.list(params);
    if (list == null || list.size() == 0) {
      Map<String, Object> p = new HashMap<String, Object>();
      p.put("configGroup", "aj_standard");
      p.put("configKey", "0");
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "SYSCONFIG");
      List<Map<String, Object>> oris = (List<Map<String, Object>>) baseService.list(p);
      if (oris != null && oris.size() > 0) {
        return JSONObject.parseObject(String.valueOf(oris.get(0).get("configValue")));
      }
      return null;
    }
    if (list != null && list.size() > 0) {
      return JSONObject.parseObject(String.valueOf(list.get(0).get("configValue")));
    }
    return null;
  }


  /**
   * 处理督办数据
   * 
   * @param supervises
   * @return
   */
  private Map<String, Map<String, Object>> handleDbData(List<Map<String, Object>> supervises) {
    Map<String, List<String>> ajbhs = new HashMap<String, List<String>>();
    for (Map<String, Object> map : supervises) {
      String city = String.valueOf(map.get("cityCode"));
      if (ajbhs.get(city) != null) {
        List<String> list = ajbhs.get(city);
        list.add(String.valueOf(map.get("ajbh")) + "-" + String.valueOf(map.get("level")));
        ajbhs.put(city, list);
      } else {
        List<String> list = new ArrayList<String>();
        list.add(String.valueOf(map.get("ajbh")) + "-" + String.valueOf(map.get("level")));
        ajbhs.put(city, list);
      }
    }
    List<String> already = new ArrayList<>();
    Map<String, Map<String, Object>> city = new HashMap<String, Map<String, Object>>();
    for (Map<String, Object> map : supervises) {
      String key = String.valueOf(map.get("cityCode"));
      String name = String.valueOf(map.get("cityName"));
      String ajbhCode =
          key + "-" + String.valueOf(map.get("ajbh") + "-" + String.valueOf(map.get("level")));
      if (!already.contains(ajbhCode)) {
        if (city.get(key) != null) {
          Map<String, Object> data = city.get(key);
          setDbData(map, data, ajbhs);
          city.put(key, data);
        } else {
          Map<String, Object> data = new HashMap<String, Object>();
          data.put("cityName", name);
          data.put("cityCode", key);
          data.put("superviseB1", 0);
          data.put("superviseB2", 0);
          data.put("superviseT1", 0);
          data.put("superviseT2", 0);
          data.put("integral", 0);
          setDbData(map, data, ajbhs);
          city.put(key, data);
        }
      }
      already.add(ajbhCode);
    }
    return city;
  }

  /**
   * 处理督办数据
   * 
   * @param map
   * @param data
   * @param ajbhs
   */
  private void setDbData(Map<String, Object> map, Map<String, Object> data,
      Map<String, List<String>> ajbhs) {
    String level = String.valueOf(map.get("level"));
    String key = String.valueOf(map.get("cityCode"));
    String ajbh = String.valueOf(map.get("ajbh"));
    String type = "2".equals(level) ? "B" : "T";
    int n = 1;
    if (map.get("endDate") != null) {
      if (map.get("parq") == null) {
        // 截止日期小于当前日期并且没有破案日期为未破获
        Date end = str2Date(String.valueOf(map.get("endDate")));
        if (end != null && end.compareTo(new Date()) == -1) {
          String dbkey = "supervise" + type + "2";
          data.put(dbkey, Double.parseDouble(String.valueOf(data.get(dbkey))) + 1);// 未破获
          n = -1;
        }
      } else {
        Date parq = str2Date(String.valueOf(map.get("parq")));
        Date end = str2Date(String.valueOf(map.get("endDate")));
        if (parq != null && end != null && parq.compareTo(end) == 1) {
          String dbkey = "supervise" + type + "2";
          data.put(dbkey, Double.parseDouble(String.valueOf(data.get(dbkey))) + 1);// 未破获
          n = -1;
        } else {
          String dbkey = "supervise" + type + "1";
          data.put(dbkey, Double.parseDouble(String.valueOf(data.get(dbkey))) + 1);// 破获
          n = 1;
        }
      }
    }
    List<String> list = ajbhs.get(key);
    if (list.contains(ajbh + "-2") && list.contains(ajbh + "-3")) {
      data.put("integral", Double.parseDouble(String.valueOf(data.get("integral"))) + 2 * n);
    } else {
      if (list.contains(ajbh + "-2")) {
        data.put("integral", Double.parseDouble(String.valueOf(data.get("integral"))) + 2 * n);
      }
      if (list.contains(ajbh + "-3")) {
        data.put("integral", Double.parseDouble(String.valueOf(data.get("integral"))) + 0.5 * n);
      }
    }
  }


  /**
   * 案件列表
   * 
   * @param params
   * @param pageNum
   * @param pageSize
   * @return
   * @throws Exception
   */
  private Object caseList(Map<String, Object> params, int pageNum, int pageSize) throws Exception {
    if (params.get("type") == null) {
      return new ArrayList<>();
    }
    String type = String.valueOf(params.get("type"));
    if ("solveCases".equals(type)) {// 侦破案件列表查询
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "TJLISTSOLVECASE");
      return baseService.page(params, pageNum, pageSize);
    } else if ("handle".equals(type)) {

      // 打击处理列表查询，查询人员信息与案件信息
      if (params.get("cslb") != null) {
        if ("1".equals(String.valueOf(params.get("cslb")))) {
          params.put("cslb", "9999");
        }
        if ("2".equals(String.valueOf(params.get("cslb")))) {
          params.put("cslb", "9998");
        }
      }
      //LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "TJLISTSTRIKE");
      //携带人员处理措施表数据
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "TJLISTSTRIKERYCLCS");

      return baseService.page(params, pageNum, pageSize);
    } else {
      return new ArrayList<>();
    }
  }


  /**
   * 督办列表
   * 
   * @param params
   * @param pageNum
   * @param pageSize
   * @return
   * @throws Exception
   */
  private Object superviseList(Map<String, Object> params, int pageNum, int pageSize)
      throws Exception {
    if (params.get("status") == null || params.get("level") == null) {
      return new ArrayList<>();
    }
    if (!"01".contains(String.valueOf(params.get("status")))
        || !"23".contains(String.valueOf(params.get("level")))) {
      return new ArrayList<>();
    }
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "TJLISTSUPERVISE");
    return baseService.page(params, pageNum, pageSize);
  }

  /**
   * 全国协查列表
   * 
   * @param params
   * @param pageNum
   * @param pageSize
   * @return
   * @throws Exception
   */
  private Object assistList(Map<String, Object> params, int pageNum, int pageSize)
      throws Exception {
    if (params.get("type") == null) {
      return new ArrayList<>();
    }
    String type = String.valueOf(params.get("type"));
    if ("0".equals(type)) {
      // 发起数查询列表
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "TJLISTLAUNCHINEG");
      return baseService.page(params, pageNum, pageSize);
    } else if ("1".equals(type)) {
      // 不反馈查询列表
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "TJLISTNOFBINEG");
      return baseService.page(params, pageNum, pageSize);
    }
    return new ArrayList<>();
  }



  /**
   * -10
   */
  private BigDecimal FTEN = new BigDecimal("-10");

  /**
   * 和案件达标数对比 每10% 加1或减1
   * 
   * @param n
   * @param m
   * @return
   */
  public int calculate(BigDecimal n, BigDecimal m) {
    return BigDecimal.ONE.subtract(n.divide(m, 2, RoundingMode.DOWN)).multiply(FTEN).intValue();
  }

  /**
   * 时间格式化
   */
  private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

  private Date str2Date(String date) {
    if (date != null && !"0".equals(date)) {
      try {
        return dateFormat.parse(date);
      } catch (ParseException e) {
        e.printStackTrace();
      }
    }
    return null;
  }

  /**
   * 初始化数据
   * @return
   */
  private Map<String, Object> initMap() {
    Map<String, Object> map = new HashMap<String, Object>();
    // 150100 呼和浩特市
    Map<String, Object> hhht = new HashMap<String, Object>();
    hhht.put("cityName", "呼和浩特市");
    hhht.put("cityCode", "150100");
    hhht.put("food", 0);
    hhht.put("drugs", 0);
    hhht.put("environment", 0);
    hhht.put("total", 0);
    map.put("150100", hhht);
    // 150200 包头市
    Map<String, Object> bt = new HashMap<String, Object>();
    bt.put("cityName", "包头市");
    bt.put("cityCode", "150200");
    bt.put("food", 0);
    bt.put("drugs", 0);
    bt.put("environment", 0);
    bt.put("total", 0);
    map.put("150200", bt);
    // 150300 乌海市
    Map<String, Object> whs = new HashMap<String, Object>();
    whs.put("cityName", "乌海市");
    whs.put("cityCode", "150300");
    whs.put("food", 0);
    whs.put("drugs", 0);
    whs.put("environment", 0);
    whs.put("total", 0);
    map.put("150300", whs);
    // 150400 赤峰市
    Map<String, Object> cfs = new HashMap<String, Object>();
    cfs.put("cityName", "赤峰市");
    cfs.put("cityCode", "150400");
    cfs.put("food", 0);
    cfs.put("drugs", 0);
    cfs.put("environment", 0);
    cfs.put("total", 0);
    map.put("150400", cfs);
    // 150500 通辽市
    Map<String, Object> tls = new HashMap<String, Object>();
    tls.put("cityName", "通辽市");
    tls.put("cityCode", "150500");
    tls.put("food", 0);
    tls.put("drugs", 0);
    tls.put("environment", 0);
    tls.put("total", 0);
    map.put("150500", tls);
    // 150600 鄂尔多斯市
    Map<String, Object> eeds = new HashMap<String, Object>();
    eeds.put("cityName", "鄂尔多斯市");
    eeds.put("cityCode", "150600");
    eeds.put("food", 0);
    eeds.put("drugs", 0);
    eeds.put("environment", 0);
    eeds.put("total", 0);
    map.put("150600", eeds);
    // 150700 呼伦贝尔市
    Map<String, Object> hlbr = new HashMap<String, Object>();
    hlbr.put("cityName", "呼伦贝尔市");
    hlbr.put("cityCode", "150700");
    hlbr.put("food", 0);
    hlbr.put("drugs", 0);
    hlbr.put("environment", 0);
    hlbr.put("total", 0);
    map.put("150700", hlbr);
    // 150800 巴彦淖尔市
    Map<String, Object> byzr = new HashMap<String, Object>();
    byzr.put("cityName", "巴彦淖尔市");
    byzr.put("cityCode", "150800");
    byzr.put("food", 0);
    byzr.put("drugs", 0);
    byzr.put("environment", 0);
    byzr.put("total", 0);
    map.put("150800", byzr);
    // 150900 乌兰察布市
    Map<String, Object> wlcb = new HashMap<String, Object>();
    wlcb.put("cityName", "乌兰察布市");
    wlcb.put("cityCode", "150900");
    wlcb.put("food", 0);
    wlcb.put("drugs", 0);
    wlcb.put("environment", 0);
    wlcb.put("total", 0);
    map.put("150900", wlcb);
    // 152200 兴安盟
    Map<String, Object> xam = new HashMap<String, Object>();
    xam.put("cityName", "兴安盟");
    xam.put("cityCode", "152200");
    xam.put("food", 0);
    xam.put("drugs", 0);
    xam.put("environment", 0);
    xam.put("total", 0);
    map.put("152200", xam);
    // 152500 锡林郭勒盟
    Map<String, Object> xlglm = new HashMap<String, Object>();
    xlglm.put("cityName", "锡林郭勒盟");
    xlglm.put("cityCode", "152500");
    xlglm.put("food", 0);
    xlglm.put("drugs", 0);
    xlglm.put("environment", 0);
    xlglm.put("total", 0);
    map.put("152500", xlglm);
    // 152900 阿拉善盟
    Map<String, Object> alsm = new HashMap<String, Object>();
    alsm.put("cityName", "阿拉善盟");
    alsm.put("cityCode", "152900");
    alsm.put("food", 0);
    alsm.put("drugs", 0);
    alsm.put("environment", 0);
    alsm.put("total", 0);
    map.put("152900", alsm);
    return map;
  }


}
