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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.common.Result;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.hander.query.AJAnalysisService;

/**
 * <功能描述/>
 *
 * @author wangpengwei  
 * @date 2020年1月6日 下午3:31:21 
 * @version 1.0 
 * 案件分析研判统计报表 
 */
@RestController
@RequestMapping("/ajfxyp")
public class AJAnalysisController {

  private static final Logger log = LoggerFactory.getLogger(AjrlController.class);
  @Autowired
  @Qualifier("baseService")
  private IBaseService baseService;
  
  @Autowired
  private AJAnalysisService ajAnalysisService;
  @PostMapping("/dataList")
  @ResponseBody
  public Object getData(@RequestBody Map<String, Object> requestMap) throws Exception {
    Map<String, Object> list = new HashMap<String, Object>();
    // 根据deptType类型确定是总队、支队、大队权限查询对应的地图数据信息
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJGLAJFXYPKJTJMAP");
    List<Map<String, Object>> mapDateList = (List<Map<String, Object>>) baseService.list(requestMap);

    String deptType = requestMap.get("deptType") + "";
    //部门类型为总队，支队的情况(detpType 1和2)
    if(!"".equals(deptType) && null != deptType) {
      List<Map<String, Object>> cityList = (List<Map<String, Object>>) ajAnalysisService.list(requestMap);
      if (!CollectionUtils.isEmpty(cityList)) {
        for (Map<String, Object> map : cityList) {
          
          // 查询所有的地市下统计报表所需要的数据项目
          String deptCode = map.get("orgCode") + "";
          LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJGLAJFXYPKJTJZHU");
           if(deptType.equals("1") ) {
             //第二次的查询，总队和支行存在有下级单位的情况
             requestMap.put("deptType", 2);

           }else {
             requestMap.put("deptType", 3);
           }
           requestMap.put("deptCode", deptCode);
          Map<String, Object> obj = (Map<String, Object>) baseService.get(requestMap);
          int count = 0;
          //对页面中报表所需要的展示项做初始化
          initDate(map,obj,count);
      }

    }
      list.put("mapData", mapDateList);
      list.put("zhuAndTableData", cityList);
    }
    return Result.ok(list);
  }
  
  @GetMapping("/detailList")
  @ResponseBody
  public Object getAJInfoByDetail(@RequestParam Map<String, Object> requestParam) throws Exception {
    
    return null;
  }
  public void initDate(Map<String, Object> map,Map<String, Object> obj,int count) {
    if(null != obj) {
      map.put("chengqu", obj.get("chengqu"));
      map.put("jiaoqu", obj.get("jiaoqu"));
      map.put("zheng", obj.get("zheng"));
      map.put("xiangcun", obj.get("xiangcun"));
      map.put("linqu", obj.get("linqu"));
      map.put("deptType", obj.get("deptType"));
      map.put("other", obj.get("other"));

      count = Integer.valueOf(obj.get("chengqu")+"") +Integer.valueOf(obj.get("jiaoqu")+"") + 
              Integer.valueOf(obj.get("zheng")+"") + Integer.valueOf(obj.get("xiangcun")+"")+
              Integer.valueOf(obj.get("linqu")+"")+Integer.valueOf(obj.get("other")+"");
    }else {
      map.put("chengqu", 0);
      map.put("jiaoqu", 0);
      map.put("zheng", 0);
      map.put("xiangcun", 0);
      map.put("linqu", 0);
      map.put("other", 0);
      map.put("deptType",-1);
    }
    map.put("total",count);
  }
}

