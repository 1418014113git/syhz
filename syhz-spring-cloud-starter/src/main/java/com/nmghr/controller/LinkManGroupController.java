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
import com.nmghr.basic.common.Result;
import com.nmghr.basic.common.exception.GlobalErrorException;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.util.ValidationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <功能描述/>
 *
 * @author zhanghang
 * @date 2019年4月29日 下午2:46:54 
 * @version 1.0   
 */
@RestController
@RequestMapping("/group")
public class LinkManGroupController {

  @Autowired
  @Qualifier("baseService")
  private IBaseService baseService;

/*
组列表及成员查询
 */
  @SuppressWarnings("unchecked")
  @GetMapping("/groupinfo")
  @ResponseBody
  public Object getGroup(@RequestParam Map<String, Object> requestParam) throws Exception {
    validateParam(requestParam,1);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "BASEGROUPBYUSERANDDEPT");
    List<Map<String,Object>> groupList = (List<Map<String, Object>>) baseService.list(requestParam);
    if(groupList!=null && groupList.size() > 0){
      for (Map<String, Object> group : groupList) {
        Map<String,Object> idMap = new HashMap<>();
        idMap.put("groupId",Integer.valueOf(String.valueOf(group.get("groupId"))));
        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "BASEGROUPDETAIL");
        List<Map<String, Object>> items = (List<Map<String, Object>>) baseService.list(idMap);
        //group.put("items", items);
        List<Integer> deptIds = new ArrayList<>();
        if(items!=null && items.size() > 0){
          for (Map<String, Object> item : items) {
            deptIds.add(Integer.valueOf(String.valueOf(item.get("itemId"))));
          }
        }
        group.put("detail",deptIds);

      }
      
    }
    return groupList;
  }

/*
分页查询组列表
 */
  @GetMapping("/groupinfopage")
  @ResponseBody
  public Object getGroupPage(@RequestParam Map<String, Object> requestParam) throws Exception {
    int pageNum = 1, pageSize = 15;
    if (requestParam.get("pageNum") != null && !"".equals(String.valueOf(requestParam.get("pageNum")).trim())) {
      pageNum = Integer.parseInt(String.valueOf(requestParam.get("pageNum")));
    }
    if (requestParam.get("pageSize") != null && !"".equals(String.valueOf(requestParam.get("pageSize")).trim())) {
      pageSize = Integer.parseInt(String.valueOf(requestParam.get("pageSize")));
    }
    validateParam(requestParam,1);

    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "BASEGROUPBYUSERANDDEPT");
     return baseService.page(requestParam, pageNum, pageSize);

  }

  @GetMapping("/checkGroupNameRepeat")
  @ResponseBody
  public Object checkRepeat(@RequestParam Map<String, Object> requestParam) throws Exception {
    validateParam(requestParam,3);
    Map<String, Object> resultMap = new HashMap<>();

    //组名查重
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "BASEGROUPNAMECHECK");
    Map<String, Object> num = (Map<String, Object>) baseService.get(requestParam);
    if (num != null) {
      if (Integer.valueOf(String.valueOf(num.get("num"))) > 0) {
        resultMap.put("type", 1);
        resultMap.put("msg", "组名重复，请确认后重新输入！");
        return  resultMap;
      }else{
        return resultMap;
      }
    }else {
      return resultMap;
    }
  }
  /*
删除组
 */
  @GetMapping("/delgroup")
  @ResponseBody
  public Object delGroup(@RequestParam Map<String, Object> requestParam) throws Exception {

    validateParam(requestParam,2);
    //判断是否被使用
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "BASEGROUPCHECKINUSE");
    Map<String,Object> check = new HashMap<>();
    String groupId = String.valueOf(requestParam.get("groupId"));
    String groupJson = '"'+"group"+'"'+":"+groupId;
    check.put("groupJson",groupJson);
    List<Map<String,Object>> jsons = (List<Map<String, Object>>) baseService.list(check);
   if(jsons!=null && jsons.size() > 0){
      throw new GlobalErrorException("998001", "该常用组正在使用，无法删除！");
   }


    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "BASEGROUP");
    baseService.update(String.valueOf(requestParam.get("groupId")),requestParam);
    //物理删除明细表
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "BASEGROUPDETAIL");
    Map<String,Object> param = new HashMap<>();
    param.put("groupId",String.valueOf(requestParam.get("groupId")));
    baseService.remove(param);
    return Result.ok(null);

  }

  /*
组详情查询
 */
  @GetMapping("/groupDetail")
  @ResponseBody
  public Object getGroupDetail(@RequestParam Map<String, Object> requestParam) throws Exception {

    //查当前组
    validateParam(requestParam,2);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "BASEGROUP");
    Map<String,Object> group = (Map<String, Object>) baseService.get(requestParam);
    if(group!=null) {
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "BASEGROUPDETAIL");
      List<Map<String, Object>> items = (List<Map<String, Object>>) baseService.list(requestParam);
      group.put("items", items);
      return group;
    }
    return new ArrayList<>();
  }

  private void validateParam(Map<String, Object> requestBody,int type) {
    if(type == 1) {
      //组列表和分页
      ValidationUtils.notNull(requestBody.get("creatorId"), "创建人Id不能为空!");
      ValidationUtils.notNull(requestBody.get("deptCode"), "创建人部门编号不能为空!");
    }
    if(type == 2){
      //详情
      ValidationUtils.notNull(requestBody.get("groupId"), "组Id不能为空!");
    }
    if(type == 3){
      //组名查重
      ValidationUtils.notNull(requestBody.get("groupName"), "组名不能为空!");
    }
  }

}
