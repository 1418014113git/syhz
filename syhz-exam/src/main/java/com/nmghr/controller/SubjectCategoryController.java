/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.nmghr.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.common.Result;
import com.nmghr.basic.common.exception.GlobalErrorEnum;
import com.nmghr.basic.common.exception.GlobalErrorException;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import com.nmghr.basic.core.util.ValidationUtils;


/**
 * <功能描述/>
 * 题库类型控层
 *
 * @author wangpengwei
 * @version 1.0
 * @date 2019年9月17日 下午7:24:25
 */
@RestController
@RequestMapping("/subjectCategory")
public class SubjectCategoryController {

  @Autowired
  @Qualifier("baseService")
  private IBaseService baseService;

  @PutMapping("save")
  public Object save(@RequestBody Map<String, Object> requestBody) throws Exception {
    //校验表单数据
    validParams(requestBody);
    Map<String, Object> param = new HashMap<>();
    param.put("parentId",requestBody.get("parentId"));
    param.put("categoryName",requestBody.get("categoryName"));
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMSUBJECTCATEINSTCHECK");
    Map<String, Object> result = (Map<String, Object>) baseService.get(param);
    if(result!=null && result.get("num")!=null &&Integer.parseInt(String.valueOf(result.get("num")))>0){
      return Result.fail(GlobalErrorEnum.PARAM_NOT_VALID.getCode(), "该名称已存在此父类中");
    }
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMSUBJECTCATEGORY");
    return baseService.save(requestBody);
  }

  /**
   * 修改
   * @param requestBody
   * @return
   * @throws Exception
   */
  @PostMapping("update")
  public Object update(@RequestBody Map<String, Object> requestBody) throws Exception {
    //校验表单数据
    validId(requestBody.get("id"));
    Map<String, Object> param = new HashMap<>();
    param.put("parentId",requestBody.get("parentId"));
    param.put("categoryName",requestBody.get("categoryName"));
    param.put("id",requestBody.get("id"));
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMSUBJECTCATEINSTCHECK");
    Map<String, Object> result = (Map<String, Object>) baseService.get(param);
    if(result!=null && result.get("num")!=null &&Integer.parseInt(String.valueOf(result.get("num")))>0){
      return Result.fail(GlobalErrorEnum.PARAM_NOT_VALID.getCode(), "该名称已存在此父类中");
    }
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMSUBJECTCATEGORY");
    return baseService.update(String.valueOf(requestBody.get("id")), requestBody);
  }

  /**
   * 详情
   * @param id
   * @return
   * @throws Exception
   */
  @GetMapping("/{id}")
  public Object detail(@PathVariable("id")String id) throws Exception {
    //校验表单数据
    validId(id);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMSUBJECTCATEGORY");
    return baseService.get(id);
  }

  /**
   * 删除
   * @param requestBody
   * @return
   * @throws Exception
   */
  @DeleteMapping("/delete")
  public Object detail(@RequestParam Map<String, Object> requestBody) throws Exception {
    //校验表单数据
    validId(requestBody.get("id"));
    Map<String, Object> param = new HashMap<>();
    param.put("parentId",requestBody.get("parentId"));
    param.put("categoryName",requestBody.get("categoryName"));
    param.put("id",requestBody.get("id"));
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMSUBJECTCATEDELCHECK");
    Map<String, Object> result = (Map<String, Object>) baseService.get(param);
    if(result!=null && result.get("num")!=null &&Integer.parseInt(String.valueOf(result.get("num")))>0){
      return Result.fail(GlobalErrorEnum.PARAM_NOT_VALID.getCode(), "该名称下存在试卷");
    }
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMSUBJECTCATEGORYDEL");
    return baseService.update(String.valueOf(requestBody.get("id")),new HashMap<>());
  }


  private void validParams(Map<String, Object> requestBody) {
    ValidationUtils.notNull(requestBody.get("categoryName"), "题库类型不能为空!");
    ValidationUtils.notNull(requestBody.get("sort"), "题库类型排序次序不能为空!");
    ValidationUtils.notNull(requestBody.get("creator"), "当前用户账户不能为空!");
    ValidationUtils.notNull(requestBody.get("parentId"), "上级id不能为空!");
    ValidationUtils.notNull(requestBody.get("deptCode"), "当前部门编号不能为空!");
    ValidationUtils.notNull(requestBody.get("deptName"), "当前部门名称不能为空!");
    Matcher m = Pattern.compile("[！@#￥%……&*$]").matcher(String.valueOf(requestBody.get("categoryName")));
    if (m.find()) {
      throw new GlobalErrorException(GlobalErrorEnum.PARAM_NOT_VALID.getCode(), "不能输入特殊字符！@#￥%……&*$");
    }
  }

  private void validId(Object id){
    ValidationUtils.notNull(id, "id不能为空!");
    ValidationUtils.regexp(id, "^\\d+$", "非法输入");
  }

}
