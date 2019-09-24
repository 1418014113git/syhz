/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.nmghr.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.common.Result;
import com.nmghr.basic.common.exception.GlobalErrorEnum;
import com.nmghr.basic.common.exception.GlobalErrorException;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.util.ValidationUtils;
import com.nmghr.common.ExamConstant;
import com.nmghr.util.DateUtil;


/**
 * <功能描述/> 考试管理
 * 
 * @author wangpengwei
 * @date 2019年9月24日 下午4:44:51
 * @version 1.0
 */
@RestController
@RequestMapping("/examination")
public class ExaminationController {

  @Autowired
  @Qualifier("baseService")
  private IBaseService baseService;


  @PostMapping("save")
  public Object save(@RequestBody Map<String, Object> requestBody) throws Exception {
    // 校验表单数据
    validParams(1, requestBody);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMINATION");
    Object examinationId = baseService.save(requestBody);
    return Result.ok(examinationId);
  }


  @PostMapping("update")
  public Object update(@RequestBody Map<String, Object> requestBody) throws Exception {

    if (requestBody.get("examinationId") == null || "".equals(requestBody.get("examinationId"))) {
      Result.fail("99999999", ExamConstant.EXAMINATION_ID_ISNULL);
    }
    // 校验表单数据
    validParams(2, requestBody);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMINATION");
    Object examinationId = baseService.update(String.valueOf(requestBody.get("id")), requestBody);
    return Result.ok(examinationId);
  }

  private void validParams(int status, Map<String, Object> requestBody) {

    /**
     * 
     * 
     * ${id}, ${examinationName}, ${type}, ${paperId}, ${examinationType},${totaldate}, ${permitNumber}, ${startDate},
     * //${endDate}, ${open_depts}, ${remark}, ${creator}, NULL, NULL, ${deptCode}, ${deptName}
     * 
     **/
    ValidationUtils.notNull(requestBody.get("examinationName"), "考试名称不能为空!");
    ValidationUtils.notNull(requestBody.get("type"), "组卷方式不能为空!");
    ValidationUtils.notNull(requestBody.get("paperId"), "所选试卷不能为空!");
    ValidationUtils.notNull(requestBody.get("examinationType"), "试题类型不能为空!");
    ValidationUtils.notNull(requestBody.get("totalDate"), "考试时间不能为空!");
    ValidationUtils.notNull(requestBody.get("permitNumber"), "允许考试次数不能为空!");
    ValidationUtils.notNull(requestBody.get("startDate"), "考试开始时间不能为空!");
    ValidationUtils.notNull(requestBody.get("endDate"), "考试结束时间不能为空!");
    ValidationUtils.notNull(requestBody.get("openDepts"), "开放部门不能为空!");
    if (status == 1) {
      ValidationUtils.notNull(requestBody.get("creator"), "创建人不能为空!");
    } else {
      ValidationUtils.notNull(requestBody.get("modifier"), "修改人不能为空!");
    }

    ValidationUtils.notNull(requestBody.get("deptCode"), "创建部门编码不能为空!");
    ValidationUtils.notNull(requestBody.get("deptName"), "创建部门名称不能为空!");
    ValidationUtils.regexp(requestBody.get("examinationName"), "[\\u4e00-\\u9fa5_a-zA-Z0-9_]{1,50}",
        "考试名称为1-50位汉字、数字、字母、下划线不能以下划线开头和结尾");
    int number = DateUtil.compareTime(String.valueOf(requestBody.get("startDate")),
        String.valueOf(requestBody.get("endDate")), "yyyy-MM-dd HH:mm:ss");
    if (number == 1) {
      throw new GlobalErrorException(GlobalErrorEnum.PARAM_NOT_VALID.getCode(),
          ExamConstant.DATAERRORSTART);
    }
  }
}
