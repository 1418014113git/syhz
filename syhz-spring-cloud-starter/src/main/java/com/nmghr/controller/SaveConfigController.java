/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.nmghr.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.common.Result;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;

/**
 * 个性化配置保存.
 *
 * @author wujin
 * @date 2018年10月10日 下午5:44:42
 * @version 1.0
 */
@RestController
@RequestMapping("/saveconfig")
public class SaveConfigController {

  @Autowired
  @Qualifier("baseService")
  private IBaseService baseService;

  @SuppressWarnings("unchecked")
  @PostMapping("/save")
  @ResponseBody
  public Object list(@RequestBody Map<String, Object> requestBody) throws Exception {
    Object config = requestBody.get("config");
    Object configlist = requestBody.get("configlist");
    Object userId =requestBody.get("user_id");
    Object deptId=requestBody.get("dept_id");
    Object condition=requestBody.get("condition");
    List<Map<String, Object>> configList = (List<Map<String, Object>>) configlist;
    if (config.equals(0)) {
      for (int i = 0; i < configList.size(); i++) {
        Map<String, Object> map = configList.get(i);
        map.put("user_id", userId);
        map.put("dept_id", deptId);
        map.put("business_type", condition);
        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "SEARCHCONFIGURE");
        baseService.save(map);
      }
    } else if (config.equals(1)) {
      for (int i = 0; i < configList.size(); i++) {
        Map<String, Object> map = configList.get(i);
        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "SEARCHCONFIGURE");
        baseService.update(map.get("id").toString(),map);
      }
    }
    return Result.ok("ok");
  }

}
