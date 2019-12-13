/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nmghr.controller;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <功能描述/>
 *
 * @author brook  
 * @date 2018年7月25日 上午10:29:38 
 * @version 1.0   
 */
@RestController
@RequestMapping("/ajrl")
public class AjrlTjController {
  
  private static final Logger log = LoggerFactory.getLogger(AjrlTjController.class);
  @Autowired
  @Qualifier("baseService")
  private IBaseService baseService;
  
  @GetMapping("/ajrltj")
  @ResponseBody
  public Object getAjList(@RequestParam Map<String, Object> requestParam) throws Exception {


      if(requestParam.get("cityCode") == null){
          //查案件
          LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJRLTJAJ");
          List<Map<String,Object>> tjList = (List<Map<String, Object>>) baseService.list(requestParam);
          LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "DEPTS");
          List<Map<String,Object>> deptList = (List<Map<String, Object>>) baseService.list(null);
          deptList = this.initValue(deptList);

          for (Map<String, Object> tjInfo : tjList) {
              for (Map<String, Object> deptInfo : deptList) {
                 String tjInfoCityCode = String.valueOf(tjInfo.get("cityCode"));
                 String deptInfoCityCode = String.valueOf(deptInfo.get("cityCode")).substring(0,4);
                 if(tjInfoCityCode.equals(deptInfoCityCode)){
                     //deptInfo.put("data",tjInfo);
                     deptInfo.put("downward",tjInfo.get("downward"));
                     deptInfo.put("left",tjInfo.get("left"));
                     deptInfo.put("forward",tjInfo.get("forward"));
                     deptInfo.put("claimed",tjInfo.get("claimed"));
                     deptInfo.put("toClaimed",tjInfo.get("toClaimed"));
                     deptInfo.put("others",tjInfo.get("others"));
                     break;
                 }
              }
          }
          return deptList;
      }else{
          LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJRLTJAJ");
          List<Map<String,Object>> tjList = (List<Map<String, Object>>) baseService.list(requestParam);
          LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "DEPTS");
          Map<String,Object> param = new HashMap<>();
          param.put("cityCode",String.valueOf(requestParam.get("cityCode")));
          List<Map<String,Object>> deptList = (List<Map<String, Object>>) baseService.list(param);
          deptList = this.initValue(deptList);
          //查地市
          for (Map<String, Object> tjInfo : tjList) {
              for (Map<String, Object> deptInfo : deptList) {
                  if(deptInfo.get("deptCode").equals(tjInfo.get("deptCode"))){
                      deptInfo.put("downward",tjInfo.get("downward"));
                      deptInfo.put("left",tjInfo.get("left"));
                      deptInfo.put("forward",tjInfo.get("forward"));
                      deptInfo.put("claimed",tjInfo.get("claimed"));
                      deptInfo.put("toClaimed",tjInfo.get("toClaimed"));
                      deptInfo.put("others",tjInfo.get("others"));
                      break;
                  }
              }
          }
          return deptList;
      }
  }

  private List<Map<String,Object>> initValue(List<Map<String,Object>> deptList){
      //初始化值
      for (Map<String, Object> initValue : deptList) {
          initValue.put("downward",0);
          initValue.put("left",0);
          initValue.put("forward",0);
          initValue.put("claimed",0);
          initValue.put("toClaimed",0);
          initValue.put("others",0);
      }
      return deptList;
  }
}
