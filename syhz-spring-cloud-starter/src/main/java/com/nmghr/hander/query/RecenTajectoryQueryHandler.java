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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.page.Paging;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractQueryHandler;
import com.nmghr.basic.core.util.ValidationUtils;
import com.nmghr.controller.vo.TajectoryBean;

/**
 * 时间轴信息拼装
 *
 * @author weber
 * @date 2019年1月21日 上午11:22:15
 * @version 1.0
 */
@Service("recentajectoryQueryHandler")
public class RecenTajectoryQueryHandler extends AbstractQueryHandler {

  public RecenTajectoryQueryHandler(IBaseService baseService) {
    super(baseService);
  }

  /**
   * 时间轴信息拼装
   */
  @Override
  public Object list(Map<String, Object> body) throws Exception {
    ValidationUtils.notNull(body.get("zjhm"), "请输入身份证信息");
    List<TajectoryBean> result = new ArrayList<TajectoryBean>();
    getList(body, result, 1); // 航班信息
    getList(body, result, 2); // 旅客信息
    getList(body, result, 3); // 网吧信息
    getList(body, result, 4); // 火车信息
    Collections.sort(result, new Comparator<TajectoryBean>() {
      public int compare(TajectoryBean p1, TajectoryBean p2) {
        Date date1 = (Date) p1.getDateTime();
        Date date2 = (Date) p2.getDateTime();
        return date1.compareTo(date2) * -1;
      }
    });
    if (result.size() < 10) {
      return result;
    }
    return result.subList(0, 10);
  }

  /**
   * 分页获取列表结果
   * 
   * @param body
   * @param result
   * @param type
   * @throws Exception
   * @throws ParseException
   */
  @SuppressWarnings({"unchecked", "rawtypes"})
  private void getList(Map<String, Object> body, List<TajectoryBean> result, int type)
      throws Exception, ParseException {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    if (type == 1) {
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "PERSONNMAIRENTRYLATELY");// 航班信息
    } else if (type == 2) {
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "PERSONNMPASSENGERLATELY");// 旅客住店信息
    } else if (type == 3) {
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "PERSONNMNETBARLATELY");// 网吧信息
    } else if (type == 4) {
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "PERSONNMTRAINLATELY");// 火车票信息
    }
    Paging page = (Paging) baseService.page(body, 1, 10);
    if (page == null || page.getList() == null || page.getList().size() == 0) {
      return;
    }
    List<Map<String, Object>> list = page.getList();
    for (Map<String, Object> map : list) {
      TajectoryBean bean = new TajectoryBean();
      if (type == 1) {
        bean.setType("airs");
      } else if (type == 2) {
        bean.setType("passengers");
      } else if (type == 3) {
        bean.setType("netbar");
      } else if (type == 4) {
        bean.setType("train");
      }
      if (map.get("time") == null) {
        continue;
      }
      bean.setDateTime(sdf.parse(String.valueOf(map.get("time"))));
      bean.setArgs(JSONObject.toJSONString(map));
      result.add(bean);
    }
  }


}
