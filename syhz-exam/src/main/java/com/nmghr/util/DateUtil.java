/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.nmghr.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * <功能描述/>
 * 考试模块时间工具类
 * @author wangpengwei
 * @date 2019年9月24日 下午8:49:58
 * 
 * @version 1.0
 */
public class DateUtil {


  /**
   * @description: 两个String类型，按照日期格式对比
   * @param dateOne
   * @param dateTwo
   * @param dateFormatType：yyyy-MM-dd / yyyy-MM-dd HH:mm:ss /等
   * @return -1，0，1，100  返回类型：-1：dateOne小于dateTwo， 0：dateOne=dateTwo ，1：dateOne大于dateTwo
   * @throws
   */
  public static int compareTime(String dateOne, String dateTwo, String dateFormatType) {

    DateFormat df = new SimpleDateFormat(dateFormatType);
    Calendar calendarStart = Calendar.getInstance();
    Calendar calendarEnd = Calendar.getInstance();

    try {
      calendarStart.setTime(df.parse(dateOne));
      calendarEnd.setTime(df.parse(dateTwo));
    } catch (ParseException e) {
      e.printStackTrace();
      return 100;
    }

    int result = calendarStart.compareTo(calendarEnd);
    if (result > 0) {
      result = 1;
    } else if (result < 0) {
      result = -1;
    } else {
      result = 0;
    }
    return result;
  }
  
 /* public static void main(String[] args) {
    
    DateUtil obj = new DateUtil();
    
    try {
      
      String stringData = obj.printDifference("2019-09-26 11:30:10", "2019-09-26 12:35:25");
      System.out.println(stringData);

    } catch (ParseException e) {
      e.printStackTrace();
    }

  }*/

  //1 minute = 60 seconds
  //1 hour = 60 x 60 = 3600
  //1 day = 3600 x 24 = 86400
  public String printDifference(String startDate, String endDate) throws ParseException{
      
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Date StartDate = simpleDateFormat.parse(startDate);
    Date EndDate = simpleDateFormat.parse(endDate);
      //milliseconds
      long different = EndDate.getTime() - StartDate.getTime();

   /*   System.out.println("startDate : " + startDate);
      System.out.println("endDate : "+ endDate);
      System.out.println("different : " + different);
*/
      long secondsInMilli = 1000;
      long minutesInMilli = secondsInMilli * 60;
      long hoursInMilli = minutesInMilli * 60;
      long daysInMilli = hoursInMilli * 24;

      /*long elapsedDays = different / daysInMilli;
      different = different % daysInMilli;*/

      long elapsedHours = different / hoursInMilli;
      different = different % hoursInMilli;

      long elapsedMinutes = different / minutesInMilli;
      different = different % minutesInMilli;

      long elapsedSeconds = different / secondsInMilli;

      String dataString = elapsedHours +"小时"+ elapsedMinutes +"分钟" + elapsedSeconds+"秒";

      //System.out.printf("%d小时%d分钟%d秒%n", elapsedHours, elapsedMinutes, elapsedSeconds);
      
      return dataString;

  }
}
