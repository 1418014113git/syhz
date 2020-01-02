/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.nmghr.hander.save;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.nmghr.basic.common.Constant;
import com.nmghr.basic.common.exception.GlobalErrorEnum;
import com.nmghr.basic.common.exception.GlobalErrorException;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractSaveHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <功能描述/>
 *
 * @author weber
 * @date 2018年7月26日 下午3:46:31
 * @version 1.0
 */
@Service("yqreportSaveHandler")
public class yqReportSaveHandler extends AbstractSaveHandler {

  private static final String dateStringFormat1 ="yyyy.MM.dd";
  private static final String dateStringFormat2 ="yyyy-MM-dd";
  private static final String dateStringFormat3 ="yyyy.MM";
  private static final String dateStringFormat4 ="yyyyMMdd";

  public yqReportSaveHandler(IBaseService baseService) {
    super(baseService);
  }


  /**
   * 增加舆情报告
   */
  @Override
  @Transactional
  public Object save(Map<String, Object> requestBody) throws Exception {
    // 添加主表
    String attachment = String.valueOf(requestBody.get("attachment"));

    String category = String.valueOf(requestBody.get("category"));
    if(category == null || "null".equals(category) || "".equals(category)){
      throw new GlobalErrorException(GlobalErrorEnum.PARAM_NOT_VALID.getCode(), "请选择报告类型");
    }

    if (attachment != null && !"".equals(attachment)) {
       JSONArray attachments = JSONArray.parseArray(attachment);
      for (int i = 0; i < attachments.size(); i++) {
        JSONObject jsonObject = attachments.getJSONObject(i);
        String name = String.valueOf(jsonObject.get("name"));

//        String name = String.valueOf(requestBody.get("name"));
//        String category = String.valueOf(requestBody.get("category"));
        try {

          if (null != name) {
            SimpleDateFormat sdf = new SimpleDateFormat(dateStringFormat1);
            SimpleDateFormat sdf2 = new SimpleDateFormat(dateStringFormat2);
            SimpleDateFormat sdf3 = new SimpleDateFormat(dateStringFormat3);
            SimpleDateFormat sdf4 = new SimpleDateFormat(dateStringFormat4);
            //日报
            if ("1".equals(category)) {
              String shortName = name.substring(0,name.lastIndexOf("."));
              if(!isDate(shortName,false)){
                throw new GlobalErrorException(GlobalErrorEnum.PARAM_NOT_VALID.getCode(), "报告保存失败，请确认文件名是否正确");
              }
              String time = shortName.substring(0,8);
              Date createTime = sdf4.parse(time);
              requestBody.put("createTime", createTime);

              Date date = getInit(-1,time,false);
              String dayString = sdf.format(date) + "日报";
              requestBody.put("title", dayString);
            }
            // 周报
            if ("2".equals(category)) {
              String shortName = name.substring(0,name.lastIndexOf("."));

              String[] time = shortName.split("-");
              if(time!=null && time.length == 2) {
                if(!isDate(time[0],false) || !isDate(time[1],false)){
                  throw new GlobalErrorException(GlobalErrorEnum.PARAM_NOT_VALID.getCode(), "报告保存失败，请确认文件名是否正确");
                }

                String startTime = time[0].substring(0, 8);
                String endTime = time[1].substring(0, 8);
                Date createTime = sdf4.parse(endTime);
                requestBody.put("createTime", createTime);
                Date date1 = getInit(0, startTime, false);
                Date date2 = getInit(-1, endTime, false);
                String startDateString = sdf.format(date1);
                String endDateString = sdf.format(date2);
                String weekTitleOK = startDateString + "-" + endDateString + "周报";
                requestBody.put("title", weekTitleOK);
              }else{
                  throw new GlobalErrorException(GlobalErrorEnum.PARAM_NOT_VALID.getCode(), "报告保存失败，请确认文件名是否正确");
                }
            }
            // 月报
            if ("3".equals(category)) {
              String shortName = name.substring(0,name.lastIndexOf("."));
              String[] time = shortName.split("-");

              if(time!=null && time.length == 2) {
                if(!isDate(time[0],true) || !isDate(time[1],true)){
                  throw new GlobalErrorException(GlobalErrorEnum.PARAM_NOT_VALID.getCode(), "报告保存失败，请确认文件名是否正确");
                }
                String startTime = time[0];
                Date createTime = sdf4.parse(time[1]);
                requestBody.put("createTime", createTime);
                Date date = sdf4.parse(startTime);
                String monthString = sdf3.format(date) + "月报";
                requestBody.put("title", monthString);
              }else {
                  throw new GlobalErrorException(GlobalErrorEnum.PARAM_NOT_VALID.getCode(), "报告保存失败，请确认文件名是否正确");
              }
            }

            //检查title
            LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "YQREPORTTITLECHECK");
            Map<String,Object> countMap = (Map<String, Object>) baseService.get(requestBody);
            if(countMap!=null && countMap.get("count")!=null){
              if(Integer.valueOf(String.valueOf(countMap.get("count")))>0){
                throw new GlobalErrorException(GlobalErrorEnum.PARAM_NOT_VALID.getCode(), "报告标题重复，请确认后重新上传");
              }
            }

            requestBody.put("fileName", name);
            requestBody.put("attachment", jsonObject.toJSONString());
            LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "YQREPORT");
            baseService.save(requestBody);
          }
        } catch (Exception e) {
          if(e.getClass() == GlobalErrorException.class) {
            throw new GlobalErrorException(GlobalErrorEnum.PARAM_NOT_VALID.getCode(),
                    e.getMessage());
          }else{
            throw new GlobalErrorException(GlobalErrorEnum.PARAM_NOT_VALID.getCode(),
                    "报告上传失败，请联系管理员！");
          }
        }

      }
    }
    return null;
  }

  public Date getInit(int day,String dateStr,boolean useNow) throws ParseException {
    Date date = null;
    if(useNow){
      date = new Date();
    }else{
      date = new SimpleDateFormat("yyyyMMdd").parse(dateStr);
    }
    //Calendar calendar =new GregorianCalendar();
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.add(calendar.DATE, day);
    date = calendar.getTime();
    return date;
  }


  /**
   * 功能：判断字符串是否为日期格式
   *
   * @param strDate 文件名称
   * @param flag 是否为8位日期格式,如果是则为true,否则为false
   * @return
   */
  public static boolean isDate(String strDate, boolean flag) {
    String el = "";
    if (flag) {
      el = "(([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})(((0[13578]|1[02])(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)(0[1-9]|[12][0-9]|30))|(02(0[1-9]|[1][0-9]|2[0-8]))))|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00))0229)";
    } else {
      el = "^((([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})(((0[13578]|1[02])(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)(0[1-9]|[12][0-9]|30))|(02(0[1-9]|[1][0-9]|2[0-8]))))|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00))0229))([0-1]?[0-9]|2[0-3])([0-5][0-9])([0-5][0-9])$";
    }
    Pattern pattern = Pattern.compile(el);
    Matcher m = pattern.matcher(strDate);
    if (m.matches()) {
      return true;
    } else {
      return false;
    }
  }

}
