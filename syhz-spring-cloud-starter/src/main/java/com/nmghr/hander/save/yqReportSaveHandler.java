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
    if (attachment != null && !"".equals(attachment)) {
      JSONArray attachments = JSONArray.parseArray(attachment);
      for (int i = 0; i < attachments.size(); i++) {
        JSONObject jsonObject = attachments.getJSONObject(i);
        String name = String.valueOf(jsonObject.get("name"));
        String category = String.valueOf(requestBody.get("category"));
        try {
          if (null != name) {
            SimpleDateFormat sdf = new SimpleDateFormat(dateStringFormat1);
            SimpleDateFormat sdf2 = new SimpleDateFormat(dateStringFormat2);
            SimpleDateFormat sdf3 = new SimpleDateFormat(dateStringFormat3);
            SimpleDateFormat sdf4 = new SimpleDateFormat(dateStringFormat4);

            //日报
            if ("1".equals(category)) {
              String shortName = name.substring(0,name.lastIndexOf("."));
              if("".equals(shortName) || shortName.length() != 14 ){
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
              if("".equals(shortName) || shortName.length() != 29 || !shortName.contains("-") ){
                throw new GlobalErrorException(GlobalErrorEnum.PARAM_NOT_VALID.getCode(), "报告上传失败，请联系管理员！");
              }
              String[] time = shortName.split("-");
              if(time!=null && time.length == 2) {
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
              }
            }
            // 月报
            if ("3".equals(category)) {

              String shortName = name.substring(0,name.lastIndexOf("."));
              if("".equals(shortName) || shortName.length() != 17 || !shortName.contains("-") ){
                throw new GlobalErrorException(GlobalErrorEnum.PARAM_NOT_VALID.getCode(), "报告上传失败，请联系管理员！");
              }
              String[] time = shortName.split("-");
              if(time!=null && time.length == 2) {
                String startTime = time[0].substring(0, 8);
                Date createTime = sdf4.parse(time[1].substring(0, 8));
                requestBody.put("createTime", createTime);
                Date date = sdf4.parse(startTime);
                String monthString = sdf3.format(date) + "月报";
                requestBody.put("title", monthString);
              }
            }

            requestBody.put("fileName", name);
            requestBody.put("attachment", jsonObject.toJSONString());
            LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "YQREPORT");
            baseService.save(requestBody);
          }
        } catch (Exception e) {
          throw new GlobalErrorException(GlobalErrorEnum.PARAM_NOT_VALID.getCode(),
              "报告上传失败，请联系管理员！");
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
}
