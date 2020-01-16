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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.common.Result;
import com.nmghr.basic.common.exception.GlobalErrorException;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.util.ValidationUtils;

/**
 * <功能描述/>
 * 考核评比配置项配置
 * @author brook  
 * @date 2020年1月15日 上午10:33:57 
 * @version 1.0   
 */
@RestController
@RequestMapping("/khpb")
public class SysAppraiseConfigController {
  
  private static final Logger log = LoggerFactory.getLogger(AjrlController.class);
  @Autowired
  @Qualifier("baseService")
  private IBaseService baseService;
  
  @GetMapping("/configDataList/{category}")
  @ResponseBody
  public Object getConfigData(@PathVariable Integer category) throws Exception {
    Map<String, Object> totalList = new HashMap<String, Object>();
    log.info("getConfigData param{}",category);
    
    if (null != category && category > 0) {
      List<Map<String, Object>> hjConfigDate = getConfig(category);
      log.info("getConfigData return data list {}",hjConfigDate);
      if (category == 1) {
        totalList.put("hj", hjConfigDate);
      } else if (category == 2) {
        totalList.put("sp", hjConfigDate);
      } else if (category == 3) {
        totalList.put("yp", hjConfigDate);
      }
    } else {
      List<Map<String, Object>> hjConfigDate = getConfig(1);
      log.info("getConfigData return hj list {}",hjConfigDate);
      totalList.put("hj", hjConfigDate);
      List<Map<String, Object>> spConfigDate = getConfig(2);
      log.info("getConfigData return sp list {}",spConfigDate);
      totalList.put("sp", spConfigDate);
      List<Map<String, Object>> ypConfigDate = getConfig(3);
      log.info("getConfigData return yp list {}",ypConfigDate);
      totalList.put("yp", ypConfigDate);
    }
    return Result.ok(totalList);
  }
  @PostMapping("/updateDatas")
  @ResponseBody
  public Object updateData(@RequestBody Map<String, Object> requestMap) throws Exception {
    if(CollectionUtils.isEmpty(requestMap) && CollectionUtils.isEmpty((List<Map<String, Object>>) requestMap.get("dataList"))) {
      throw new GlobalErrorException("888888", "保存数据不能为空!");
    }else {
      String categoryType = requestMap.get("categoryType") +"";
      if(null != categoryType && !"".equals(categoryType)) {
         if("1".equals(categoryType)) {
           requestMap.put("category", 1);
         }else if ("2".equals(categoryType)) {
           requestMap.put("category", 2);
         }else if ("3".equals(categoryType)) {
           requestMap.put("category", 3);
         }
         LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "SYSAPPRAISECONFIG");
         baseService.update("-1", requestMap);
      }
    }
    return Result.ok(null);
  }
  
  
  /**
   * 保存个人创建的需要展示的考核项配置
   * 
   * **/
  @PostMapping("/saveCategoryMaping")
  @ResponseBody
  public Object saveCategoryMaping(@RequestBody Map<String, Object> requestMap) throws Exception {
     validParams(requestMap);
     LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "SYSAPPRAISECONFIGBYUSER");
     baseService.save(requestMap);
    return Result.ok(null);
  }
  
  @SuppressWarnings("unchecked")
  public List<Map<String, Object>> getConfig(int category) throws Exception {
    Map<String, Object> requestMap = new HashMap<String, Object>();
    requestMap.put("category", category);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "SYSAPPRAISECONFIG");
    List<Map<String, Object>> dateList = (List<Map<String, Object>>) baseService.list(requestMap);
    return dateList;
  }

  
  /**
   * 开始考试校验
   *
   * @param requestBody
   */
  @SuppressWarnings("unchecked")
  private void validParams(Map<String, Object> requestBody) {
    ValidationUtils.notNull(requestBody.get("userId"), "创建人ID不能为空!");
    ValidationUtils.notNull(requestBody.get("userName"), "创建人不能为空!");
    ValidationUtils.notNull(requestBody.get("categoryList"), "配置项不能为空!"); 
  }
}
