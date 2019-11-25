/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nmghr.util.app;

import java.util.regex.Pattern;

import com.nmghr.entity.ErrorEntity;


/**
 * <功能描述/>
 *
 * @author kaven  
 * @date 2019年11月25日 上午11:43:56 
 * @version 1.0
 */
public class SyhzAppValidationUtil {
  
  public static void notNull(Object value, String message) {
    if (value == null || value.toString().trim().length() == 0) {
      throw new ErrorEntity(SyhzAppErrorEnmu.ERROR_32600.getCode(),message);
    }
  }

  public static void max(Object value, Integer max, String message) {
    if (value == null || (max != null && value.toString().trim().length() > max)) {
      throw new ErrorEntity(SyhzAppErrorEnmu.ERROR_32600.getCode(),message);
    }
  }

  public static void min(Object value, Integer min, String message) {
    if (value == null || (min != null && value.toString().trim().length() < min)) {
      throw new ErrorEntity(SyhzAppErrorEnmu.ERROR_32600.getCode(),message);    }
  }

  public static void length(Object value, Integer min, Integer max, String message) {
    min(value, min, message);
    max(value, max, message);
  }

  public static void regexp(Object value, String expression, String message) {
    if (value == null || !Pattern.matches(expression, value.toString())) {
      throw new ErrorEntity(SyhzAppErrorEnmu.ERROR_32600.getCode(),message);    }
  }
}
