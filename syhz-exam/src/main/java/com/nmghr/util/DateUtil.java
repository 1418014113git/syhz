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

}
