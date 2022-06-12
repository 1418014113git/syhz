/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.nmghr.controller.app;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.common.Result;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.service.BlueMsgNoticeService;

/**
 * <功能描述/>
 *
 * @author weber
 * @date 2019年4月19日 下午3:12:15
 * @version 1.0
 */
@RestController
@RequestMapping("/bluemsg")
public class BlueMessageController {

  // private static final Logger log = LoggerFactory.getLogger(BlueMessageController.class);

  @Autowired
  private IBaseService baseService;

  @Autowired
  private BlueMsgNoticeService bluemsgNoticeService;

  @PostMapping(value = "/getUserDept")
  @ResponseBody
  public Object getUserDept(@RequestBody Map<String, Object> body, HttpServletResponse response)
      throws Exception {
    if (body == null || body.get("phone") == null
        || StringUtils.isBlank(String.valueOf(body.get("phone")))) {
      return Result.fail();
    }
    return bluemsgNoticeService.get(String.valueOf(body.get("phone")));
  }

  @SuppressWarnings("unchecked")
  @PostMapping(value = "/getBisSigns")
  @ResponseBody
  public Object getBisSigns(@RequestBody Map<String, Object> body, HttpServletResponse response)
      throws Exception {
    if (body == null || body.get("type") == null
        || StringUtils.isBlank(String.valueOf(body.get("type"))) || body.get("id") == null
        || body.get("deptId") == null || StringUtils.isBlank(String.valueOf(body.get("id")))
        || StringUtils.isBlank(String.valueOf(body.get("deptId")))) {
      return Result.fail();
    }
    String deptId = String.valueOf(body.get("deptId"));
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "SIGNBIZLIST");
    List<Map<String, Object>> list = (List<Map<String, Object>>) baseService.list(body);
    return getSignResult(deptId, list, "2");
  }


  @SuppressWarnings("unchecked")
  @PostMapping(value = "/getTztgSigns")
  @ResponseBody
  public Object getTztgSigns(@RequestBody Map<String, Object> body, HttpServletResponse response)
      throws Exception {
    if (body == null || body.get("id") == null || body.get("deptId") == null
        || StringUtils.isBlank(String.valueOf(body.get("id")))
        || StringUtils.isBlank(String.valueOf(body.get("deptId")))) {
      return Result.fail();
    }
    String deptId = String.valueOf(body.get("deptId"));
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "SIGNBIZTZTG");
    List<Map<String, Object>> list = (List<Map<String, Object>>) baseService.list(body);
    return getSignResult(deptId, list, "1");
  }

  /**
   * 获取签收数据
   * 
   * @param deptId
   * @param list
   * @return
   */
  private Map<String, Object> getSignResult(String deptId, List<Map<String, Object>> list,
      String status) {
    int total = 0, wnum = 0, ynum = 0;
    boolean signed = false;
    total = list.size();
    String id = "";
    for (int i = 0; i < list.size(); i++) {
      Map<String, Object> map = list.get(i);
      if (status.equals(String.valueOf(map.get("status")))) {
        ynum++;
      } else {
        wnum++;
      }
      if (deptId.equals(String.valueOf(map.get("deptId")))) {
        id = String.valueOf(map.get("id"));
        if (status.equals(String.valueOf(map.get("status")))) {
          signed = true;
        }
      }
    }
    Map<String, Object> result = new HashMap<String, Object>();
    result.put("total", total);
    result.put("wnum", wnum);
    result.put("ynum", ynum);
    result.put("signed", signed);
    result.put("id", id);
    return result;
  }

}
