/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nmghr.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.sf.jsqlparser.expression.DateTimeLiteralExpression.DateTime;
/**
 * <功能描述/>
 *
 * @author brook  
 * @date 2018年8月23日 下午3:43:19 
 * @version 1.0   
 */
public class DateUtil {

  /**
   * date -> int 转换，转换为秒为单位的时间戳
   * 
   * @param date
   * @return int 秒为单位的时间戳
   */
  public static int Date2int(Date date) {

      Long timeLong = date.getTime() / 1000;
      return timeLong.intValue();

  }

  /**
   * int -> String 以秒为单位的时间戳转换为"yyyy-MM-dd"格式的字符串
   * 
   * @param time
   * @return String "yyyy-MM-dd"
   */
  public static String int2DateString(int time) {

      SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
      return simpleDateFormat.format(int2Date(time));
  }

  /**
   * int -> date 以秒为单位的时间戳转为date
   * 
   * @param time
   * @return date
   */
  public static Date int2Date(int time) {

      Long timeLong = (long) time;
      return new Date(timeLong * 1000);

  }

  

  /**
   * date -> String date转为字符串"yyyy-MM-dd"
   * 
   * @param date
   * @return String yyyy-MM-dd
   */
  public static String Date2String(Date date) {

      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
      return sdf.format(date);
  }

  /**
   * date -> Timestamp date转为unix时间戳
   * 
   * @param date
   * @return Timestamp
   */
  public static Timestamp Date2Timestamp(Date date) {

      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      return Timestamp.valueOf(sdf.format(date));

  }

  /**
   * timestamp -> date unix时间戳转为date
   * 
   * @param timestamp
   * @return date
   */
  public static Date Timestamp2Date(Timestamp timestamp) {

      Date date = new Date();
      date = timestamp;
      return date;
  }
  
  /**
   * String  -> date 按照字符串与格式转换
   * @param date
   * @param format
   * @return
   */
  public static Date String2Date(String date,String format) {
      
      try {
          SimpleDateFormat sdf = new SimpleDateFormat(format);
          return sdf.parse(date);
      } catch (ParseException e) {
          return new Date();
      }
  }
}
