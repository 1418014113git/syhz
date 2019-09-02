/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.nmghr.controller.ajarchives;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.common.Result;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;

/**
 * <功能描述/>
 *
 * @author weber
 * @date 2019年7月8日 下午4:19:37
 * @version 1.0
 */
@RestController
public class TodoItemsController {
  @Autowired
  @Qualifier("baseService")
  private IBaseService baseService;

  @SuppressWarnings("unchecked")
  @GetMapping("/archives/toDoItems")
  @ResponseBody
  public Object toDoItems(@RequestParam Map<String, Object> requestParams) throws Exception {
    if (requestParams.get("ajbh") == null) {
      return Result.fail("99998", "案件编号不能为空");
    }
    if (requestParams.get("deptId") == null) {
      return Result.fail("99998", "部门不能为空");
    }

    Map<String, Object> xcMap = new HashMap<String, Object>();
    Map<String, Object> dbMap = new HashMap<String, Object>();
    Map<String, Object> jdMap = new HashMap<String, Object>();
    // 初始化访问参数
    Map<String, Object> params = new HashMap<String, Object>();
    params.put("ajbh", requestParams.get("ajbh"));
    params.put("deptId", requestParams.get("deptId"));
    // 协查审核待办条数
    params.put("type", "xc");
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJDADBWORKORDER");
    List<Map<String, Object>> xcwk = (List<Map<String, Object>>) baseService.list(params);
    if (xcwk != null && xcwk.size() > 0) {
      for (Map<String, Object> map : xcwk) {
        if(map!=null && map.get("id")!=null) {
          xcMap.put(String.valueOf(map.get("id")), 1);
        }
      }
    }
    // 督办审核待办条数
    params.put("type", "db");
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJDADBWORKORDER");
    List<Map<String, Object>> dbwk = (List<Map<String, Object>>) baseService.list(params);
    if (dbwk != null && dbwk.size() > 0) {
      for (Map<String, Object> map : dbwk) {
        if(map!=null && map.get("id")!=null) {
          dbMap.put(String.valueOf(map.get("id")), 1);
        }
      }
    }
    // 检验鉴定审核待办条数
    params.put("type", "jd");
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJDADBWORKORDER");
    List<Map<String, Object>> jdwk = (List<Map<String, Object>>) baseService.list(params);
    if (jdwk != null && jdwk.size() > 0) {
      for (Map<String, Object> map : jdwk) {
        if(map!=null && map.get("id")!=null) {
          jdMap.put(String.valueOf(map.get("id")), 1);
        }
      }
    }

    // 协查签收待办条数
    params.put("type", "xc");
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJDADBSIGN");
    List<Map<String, Object>> xcsign = (List<Map<String, Object>>) baseService.list(params);
    if (xcsign != null && xcsign.size() > 0) {
      for (Map<String, Object> map : xcsign) {
        if(map!=null && map.get("id")!=null) {
          xcMap.put(String.valueOf(map.get("id")), 1);
        }
      }
    }
    // 督办签收待办条数
    params.put("type", "db");
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJDADBSIGN");
    List<Map<String, Object>> dbsign = (List<Map<String, Object>>) baseService.list(params);
    if (dbsign != null && dbsign.size() > 0) {
      for (Map<String, Object> map : dbwk) {
        if(map!=null && map.get("id")!=null) {
          dbMap.put(String.valueOf(map.get("id")), 1);
        }
      }
    }

    // 督办催办待办条数
    params.remove("type");
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJDADBSUPERVISE");
    List<Map<String, Object>> dbcb = (List<Map<String, Object>>) baseService.list(params);
    if (dbcb != null && dbcb.size() > 0) {
      for (Map<String, Object> map : dbwk) {
        if(map!=null && map.get("id")!=null) {
          dbMap.put(String.valueOf(map.get("id")), 1);
        }
      }
    }
    Map<String, Object> rest = new HashMap<String, Object>();
    rest.put("xcNum", xcMap.keySet().size());
    rest.put("dbNum", dbMap.keySet().size());
    rest.put("jdNum", jdMap.keySet().size());
    return Result.ok(rest);
  }

}
