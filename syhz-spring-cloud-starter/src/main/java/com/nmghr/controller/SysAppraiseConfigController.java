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
import org.springframework.web.bind.annotation.RequestParam;
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
  
  private static final Logger log = LoggerFactory.getLogger(SysAppraiseConfigController.class);
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
        totalList.put("sp", hjConfigDate);
      } else if (category == 2) {
        totalList.put("yp", hjConfigDate);
      } else if (category == 3) {
        totalList.put("hj", hjConfigDate);
      }
    } else {
      List<Map<String, Object>> hjConfigDate = getConfig(1);
      log.info("getConfigData return hj list {}",hjConfigDate);
      totalList.put("sp", hjConfigDate);
      List<Map<String, Object>> spConfigDate = getConfig(2);
      log.info("getConfigData return sp list {}",spConfigDate);
      totalList.put("yp", spConfigDate);
      List<Map<String, Object>> ypConfigDate = getConfig(3);
      log.info("getConfigData return yp list {}",ypConfigDate);
      totalList.put("hj", ypConfigDate);
    }
    return Result.ok(totalList);
  }
  @PostMapping("/updateDatas")
  @ResponseBody
  public Object updateData(@RequestBody List<Map<String, Object>> list) throws Exception {
    if(CollectionUtils.isEmpty(list)) {
      throw new GlobalErrorException("888888", "保存数据不能为空!");
    }else {
      for (Map<String, Object> requestMap : list) {
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
    }
    return Result.ok(null);
  }
  
  
  /**
   * 保存个人创建的需要展示的考核项配置
   * 
   * **/
  @PostMapping("/saveCategoryMaping")
  @ResponseBody
  public Object saveCategoryMaping(@RequestBody List<Map<String, Object>> requestMap) throws Exception {
     validParams(requestMap);
     for (Map<String, Object> map : requestMap) {
       LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "SYSAPPRAISECONFIGBYUSER");
       baseService.save(map);
    }
    
    return Result.ok(null);
  }

  @PostMapping("/configDataListByUser")
  @ResponseBody
  public Object getConfigDataByUser(@RequestBody Map<String, Object> requestMap) throws Exception {
    Map<String, Object> totalList = new HashMap<String, Object>();
    log.info("getConfigDataByUser param{}",requestMap);
    String category = requestMap.get("category")+"";
    String userId = requestMap.get("userId")+"";
    if (null != category && !"0".equals(category)) {
      List<Map<String, Object>> hjConfigDate = getConfigByUser(category,userId);
      log.info("getConfigDataByUser return data list {}",hjConfigDate);
      if (category.equals("1")) {
        totalList.put("sp", hjConfigDate);
      } else if (category.equals("2")) {
        totalList.put("yp", hjConfigDate);
      } else if (category.equals("3")) {
        totalList.put("hj", hjConfigDate);
      }
    } else {
      List<Map<String, Object>> hjConfigDate = getConfigByUser("1",userId);
      log.info("getConfigDataByUser return hj list {}",hjConfigDate);
      totalList.put("sp", hjConfigDate);
      List<Map<String, Object>> spConfigDate = getConfigByUser("2",userId);
      log.info("getConfigDataByUser return sp list {}",spConfigDate);
      totalList.put("yp", spConfigDate);
      List<Map<String, Object>> ypConfigDate = getConfigByUser("3",userId);
      log.info("getConfigDataByUser return yp list {}",ypConfigDate);
      totalList.put("hj", ypConfigDate);
    }
    return Result.ok(totalList);
  }
  
  
  @SuppressWarnings("unchecked")
  public List<Map<String, Object>> getConfig(int category) throws Exception {
    Map<String, Object> requestMap = new HashMap<String, Object>();
    requestMap.put("category", category);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "SYSAPPRAISECONFIG");
    List<Map<String, Object>> dateList = (List<Map<String, Object>>) baseService.list(requestMap);
    return dateList;
  }
  
  @SuppressWarnings("unchecked")
  public List<Map<String, Object>> getConfigByUser(String category,String userId) throws Exception {
    Map<String, Object> requestMap = new HashMap<String, Object>();
    requestMap.put("category", category);
    requestMap.put("userId", userId);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "SYSAPPRAISECONFIGBYUSER");
    List<Map<String, Object>> dateList = (List<Map<String, Object>>) baseService.list(requestMap);
    return dateList;
  }

  
  /**
   * 开始考试校验
   *
   * @param requestBody
   */
  @SuppressWarnings("unchecked")
  private void validParams(List<Map<String, Object>> list) {
    if (CollectionUtils.isEmpty(list)) {
      throw new GlobalErrorException("888888", "保存数据不能为空!");
    }

    for (Map<String, Object> map : list) {
      try {
        ValidationUtils.notNull(map.get("userId"), "创建人ID不能为空!");
        ValidationUtils.notNull(map.get("userName"), "创建人不能为空!");
        ValidationUtils.notNull(map.get("categoryList"), "配置项不能为空!");
      } catch (Exception e) {
        // TODO: handle exception
        e.getStackTrace();
      }

    }
  }
}
