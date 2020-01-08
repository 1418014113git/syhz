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
import com.nmghr.basic.core.page.Paging;
import com.nmghr.basic.core.service.IBaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

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
@RequestMapping("/ajrllist")
public class AjrlController {

  private static final Logger log = LoggerFactory.getLogger(AjrlController.class);
  @Autowired
  @Qualifier("baseService")
  private IBaseService baseService;
// 案件认领列表
  @GetMapping("/pageList")
  @ResponseBody
  public Object getAjRlList(@RequestParam Map<String, Object> requestParam) throws Exception {
      int pageNum = 1;
      int pageSize = 15;
      if (requestParam.get("pageNum") != null) {
          pageNum = Integer.valueOf(String.valueOf(requestParam.get("pageNum")));
      }
      if (requestParam.get("pageSize") != null) {
          pageSize = Integer.valueOf(String.valueOf(requestParam.get("pageSize")));
      }

      if (isXfzf(requestParam)) {
          LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "ajjbxxetlrl");
          List<Map<String,Object>> ajlist = (List<Map<String, Object>>) baseService.list(requestParam);
//          for (Map<String, Object> map : xfzfList) {
//              //查最新的下发转发记录,每个案件查到一条
//              Map<String,Object> param = new HashMap();
//              param.put("status",requestParam.get("status"));
//              param.put("noticeLx",requestParam.get("noticeLx"));
//              param.put("ajbh",String.valueOf(map.get("AJBH")));
//              if(requestParam.get("curDeptCode") != null)
//              param.put("noticeOrgCode",requestParam.get("curDeptCode"));
//              LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJXFZFLATEST");
//              Map<String,Object> latestAj = (Map<String, Object>) baseService.get(param);
//              if(!ajIsIn(String.valueOf(map.get("AJBH")),latestAjList)) {
//                  latestAjList.add(latestAj);
//              }
//          }
          return new Paging(pageSize, pageNum, ajlist.size(), ajlist);
      }
      if("".equals(requestParam.get("status")) || requestParam.get("status") == null){
          requestParam.put("statusStr","3,5,10");
      }
          LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "ajjbxxetlrl");
          return baseService.page(requestParam,pageNum,pageSize);

  }
    //根据案件编号判断是否在集合
    private boolean ajIsIn(String ajbh, List<Map<String, Object>> latestAjList) {
        for (Map<String, Object> map : latestAjList) {
            if(String.valueOf(map.get("AJBH")).equals(ajbh)){
                return true;
            }
        }
      return false;
    }

    private boolean isXfzf(@RequestParam Map<String, Object> requestParam) {
        return "9".equals(requestParam.get("status")) && ("1".equals(requestParam.get("noticeLx")) ||
                ("2".equals(requestParam.get("noticeLx"))));
    }

}
