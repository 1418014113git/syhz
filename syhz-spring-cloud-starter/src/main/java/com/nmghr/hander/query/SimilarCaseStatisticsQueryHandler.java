/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.nmghr.hander.query;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractQueryHandler;

/**
 * <功能描述/>
 *
 * @author weber
 * @date 2019年7月9日 下午3:05:06
 * @version 1.0
 */
@Service("similarCaseStatisticsQueryHandler")
public class SimilarCaseStatisticsQueryHandler extends AbstractQueryHandler {

  public SimilarCaseStatisticsQueryHandler(IBaseService baseService) {
    super(baseService);
  }

  @SuppressWarnings({"unchecked"})
  @Override
  public Object list(Map<String, Object> params) throws Exception {
    String deptCode = String.valueOf(params.get("deptCode"));
    deptCode = deptCode.substring(0, 6);
    if("150000".equals(deptCode)) {
      deptCode = "150100";
    }
    JSONObject result = new JSONObject();
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "SIMILARCASESTATIC");
    List<Map<String, Object>> list = (List<Map<String, Object>>) baseService.list(params);
    if (list != null && list.size() > 0) {
      settingData(params, deptCode, result, list);
    }
    return result;
  }

  private void settingData(Map<String, Object> params, String deptCode, JSONObject result,
      List<Map<String, Object>> list) {
    int afd3 = 0, afd7 = 0, afd15 = 0, bed3 = 0, bed7 = 0, bed15 = 0,
        afd3C = 0, afd7C = 0, afd15C = 0, bed3C = 0, bed7C = 0, bed15C = 0;
    Date sta = str2Date(String.valueOf(params.get("time")));
    if(sta==null) {
      return;
    }
    Calendar cal = Calendar.getInstance();
    cal.setTime(sta);
    cal.add(Calendar.DATE, 3);
    Date afDay3 = cal.getTime();
    cal.add(Calendar.DATE, 4);
    Date afDay7 = cal.getTime();
    cal.add(Calendar.DATE, 8);
    Date afDay15 = cal.getTime();
    cal.setTime(sta);
    cal.add(Calendar.DATE, -3);
    Date beDay3 = cal.getTime();
    cal.add(Calendar.DATE, -4);
    Date beDay7 = cal.getTime();
    cal.add(Calendar.DATE, -8);
    Date beDay15 = cal.getTime();
    
    JSONArray afd3Arr = new JSONArray();
    JSONArray afd7Arr = new JSONArray();
    JSONArray afd15Arr = new JSONArray();
    JSONArray bed3Arr = new JSONArray();
    JSONArray bed7Arr = new JSONArray();
    JSONArray bed15Arr = new JSONArray();
    
    for (Map<String, Object> map : list) {
      if (map != null) {
        String cityCode = String.valueOf(map.get("cityCode"));
        Date fxsj = str2Date(String.valueOf(map.get("fxsj")));
        if(fxsj==null) {
          continue;
        }
        int flag = 0;
        if (fxsj.compareTo(sta) > -1 && fxsj.compareTo(afDay3) < 1) {
          afd3++;
          if(deptCode.equals(cityCode)) {
            afd3C++;
          }
          afd3Arr.add(map);
        }
        if (fxsj.compareTo(sta) > -1 && fxsj.compareTo(afDay7) < 1) {
          afd7++;
          if(deptCode.equals(cityCode)) {
            afd7C++;
          }
          afd7Arr.add(map);
        }
        if (fxsj.compareTo(sta) > -1 && fxsj.compareTo(afDay15) < 1) {
          afd15++;
          if(deptCode.equals(cityCode)) {
            afd15C++;
          }
          afd15Arr.add(map);
        }
        if (fxsj.compareTo(sta) < 1 && fxsj.compareTo(beDay3) > -1) {
          bed3++;
          if(deptCode.equals(cityCode)) {
            bed3C++;
          }
          bed3Arr.add(map);
        }
        if (fxsj.compareTo(sta) < 1 && fxsj.compareTo(beDay7) > -1) {
          bed7++;
          if(deptCode.equals(cityCode)) {
            bed7C++;
          }
          bed7Arr.add(map);
        }
        if (fxsj.compareTo(sta) < 1 && fxsj.compareTo(beDay15) > -1) {
          bed15++;
          if(deptCode.equals(cityCode)) {
            bed15C++;
          }
          bed15Arr.add(map);
        }
      }
    }
    Map<String, Object> ad3 = new HashMap<>();
    ad3.put("city", afd3C);
    ad3.put("all", afd3);
    ad3.put("tips", afd3Arr);
    result.put("ad3", ad3);
    Map<String, Object> ad7 = new HashMap<>();
    ad7.put("city", afd7C);
    ad7.put("all", afd7);
    ad7.put("tips", afd7Arr);
    result.put("ad7", ad7);
    Map<String, Object> ad15 = new HashMap<>();
    ad15.put("city", afd15C);
    ad15.put("all", afd15);
    ad15.put("tips", afd15Arr);
    result.put("ad15", ad15);
    Map<String, Object> bd3 = new HashMap<>();
    bd3.put("city", bed3C);
    bd3.put("all", bed3);
    bd3.put("tips", bed3Arr);
    result.put("bd3", bd3);
    Map<String, Object> bd7 = new HashMap<>();
    bd7.put("city", bed7C);
    bd7.put("all", bed7);
    bd7.put("tips", bed7Arr);
    result.put("bd7", bd7);
    Map<String, Object> bd15 = new HashMap<>();
    bd15.put("city", bed15C);
    bd15.put("all", bed15);
    bd15.put("tips", bed15Arr);
    result.put("bd15", bd15);
  }

  /**
   * 时间格式化
   */
  private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

  private Date str2Date(String date) {
    if (date != null && !"0".equals(date) && date.length()>7) {
      try {
        return dateFormat.parse(date.substring(0, 8));
      } catch (ParseException e) {
        e.printStackTrace();
      }
    }
    return null;
  }
}