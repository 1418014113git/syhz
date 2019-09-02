/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nmghr.controller.sysConfig;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.common.exception.GlobalErrorException;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;

/**
 * 案件达标数相关操作
 *
 * @author weber  
 * @date 2019年6月6日 上午10:33:01 
 * @version 1.0   
 */
@RestController
public class StandardController {
  
  @Autowired
  @Qualifier("baseService")
  private IBaseService baseService;
  
  /**
   * demo 测试
   * 
   * @param params
   * @return
   * @throws Exception
   */
  @SuppressWarnings("unchecked")
  @GetMapping("/standard")
  @ResponseBody
  public Object standard(@RequestParam Map<String, Object> params) throws Exception {
    if(params.get("configGroup")==null) {
      throw new GlobalErrorException("99950","configGroup 参数不能为空");
    }
    if(params.get("configKey")==null) {
      throw new GlobalErrorException("99950","configKey 参数不能为空");
    }
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "SYSCONFIG");
    List<Map<String, Object>> list = (List<Map<String, Object>>) baseService.list(params);
    if(list == null || list.size()==0) {
      Map<String, Object> p = new HashMap<String, Object>();
      p.put("configGroup", "aj_standard");
      p.put("configKey", "0");
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "SYSCONFIG");
      List<Map<String, Object>> oris = (List<Map<String, Object>>) baseService.list(p);
      if(oris!=null && oris.size()>0) {
        Map<String, Object> map = oris.get(0);
        map.remove("id");
        return map;
      }
      return oris;
    }
    return list.get(0);
  }
  
  /**
   * demo 测试
   * 
   * @param params
   * @return
   * @throws Exception
   */
  @SuppressWarnings("unchecked")
  @PostMapping("/standard/save")
  @ResponseBody
  public Object standardSave(@RequestBody Map<String, Object> params) throws Exception {
    if(params.get("configGroup")==null) {
      throw new GlobalErrorException("99950","configGroup 参数不能为空");
    }
    if(params.get("configKey")==null) {
      throw new GlobalErrorException("99950","configKey 参数不能为空");
    }
    //防止提交错误数据
    params.put("category", 3);
    params.put("configGroup", "aj_standard");
    if(params.get("id")==null) {
      //查询是否已经存在
      Map<String, Object> p = new HashMap<String, Object>();
      p.put("configGroup", "aj_standard");
      p.put("configKey", String.valueOf(params.get("configKey")));
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "SYSCONFIG");
      List<Map<String, Object>> list = (List<Map<String, Object>>) baseService.list(p);
      if(list == null || list.size()==0) {
        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "SYSCONFIG");
        return baseService.save(params);
      }
      params.put("id", list.get(0).get("id"));
    }
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "SYSCONFIG");
    return baseService.update(String.valueOf(params.get("id")), params);
  }

}
