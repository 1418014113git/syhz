/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.nmghr.controller;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nmghr.service.UserDeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.common.Result;
import com.nmghr.basic.common.exception.GlobalErrorEnum;
import com.nmghr.basic.common.exception.GlobalErrorException;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.page.Paging;
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
  @Autowired
  @Qualifier("userdeptService")
  private UserDeptService userdeptService;


  @PostMapping("save")
  public Object save(@RequestBody Map<String, Object> requestBody) throws Exception {
    // 校验表单数据
    validParams(1, requestBody);
    //考试名称查重
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMINATIONCOUNTBYNAMEDEPT");
    Map<String,Object> result = (Map<String, Object>) baseService.get(requestBody);
    if (result != null && result.get("num") != null && Integer.parseInt(String.valueOf(result.get("num"))) > 0) {
      return Result.fail(GlobalErrorEnum.PARAM_NOT_VALID.getCode(), "考试名称重复");
    }

    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMINATION");
    Object examinationId = baseService.save(requestBody);
    return Result.ok(examinationId);
  }

  @PostMapping("update")
  public Object update(@RequestBody Map<String, Object> requestBody) throws Exception {

    if (requestBody.get("id") == null || "".equals(requestBody.get("id"))) {
      return Result.fail("99999999", ExamConstant.EXAMINATION_ID_ISNULL);
    }
    //不是发布考试，则校验
    if(!"1".equals(String.valueOf(requestBody.get("status")))) {
      // 校验表单数据
      validParams(2, requestBody);
      Result checkResult = (Result) checkExamination(requestBody);
      if (checkResult != null && !checkResult.isSuccess()) {
        //返回false
        return checkResult;
      }
      //考试名称查重(除了当前记录)
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMINATIONCOUNTBYNAMEIDDEPT");
      Map<String, Object> result = (Map<String, Object>) baseService.get(requestBody);
      if (result != null && result.get("num") != null && Integer.parseInt(String.valueOf(result.get("num"))) > 0) {
        return Result.fail(GlobalErrorEnum.PARAM_NOT_VALID.getCode(), "考试名称重复");
      }
    }
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMINATION");
    Object examinationId = baseService.update(String.valueOf(requestBody.get("id")), requestBody);
    return Result.ok(examinationId);
  }

  @GetMapping("checkexamination")
  public Object checkExamination(@RequestParam Map<String, Object> requestBody) throws Exception {
    ValidationUtils.notNull(requestBody.get("id"), "考试Id不能为空!");
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMINATIONCHECK");
    Map<String,Object> result = (Map<String, Object>) baseService.get(String.valueOf(requestBody.get("id")));
    if (result != null && result.get("num") != null && Integer.parseInt(String.valueOf(result.get("num"))) > 0) {
      return Result.fail(GlobalErrorEnum.PARAM_NOT_VALID.getCode(), "考试已经开始，暂不能修改或删除");
    }
    //无异常
    return Result.ok(null);
  }
  @DeleteMapping("delete")
  public Object delete(@RequestParam Map<String, Object> requestBody) throws Exception {
    // 校验表单数据
    ValidationUtils.notNull(requestBody.get("id"), "考试Id不能为空!");
    Result checkResult = (Result) checkExamination(requestBody);
    if(checkResult!=null && !checkResult.isSuccess()){
      //返回false
      return checkResult;
    }
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMINATION");
    Object examinationId = baseService.update(String.valueOf(requestBody.get("id")),requestBody);
    return Result.ok(examinationId);
  }
    /*
  前20条成绩
   */
  @ResponseBody
  @GetMapping("scorelist")
  public Object scoreList(@RequestParam Map<String, Object> requestParam) throws Exception {
    ValidationUtils.notNull(requestParam.get("examId"), "考试Id不能为空!");
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMSCORELIST");
    List<Map<String,Object>> scoreList = (List<Map<String, Object>>) baseService.list(requestParam);
    if(scoreList!=null && scoreList.size() > 0) {
      for (Map<String, Object> score : scoreList) {
        Timestamp start = (Timestamp) score.get("startTime");
        Timestamp end = (Timestamp) score.get("endTime");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateStr = DateUtil.printDifference(sdf.format(start), sdf.format(end));
        score.put("dateStr",dateStr);
      }
    }
    return  scoreList;
  }
    /*
    所有成绩列表
     */
  @ResponseBody
  @GetMapping("allscorelist")
  public Object allScoreList(@RequestParam Map<String, Object> requestParam) throws Exception {
    int pageNum = 1, pageSize = 15;
    if (requestParam.get("pageNum") != null && !"".equals(String.valueOf(requestParam.get("pageNum")).trim())) {
      pageNum = Integer.parseInt(String.valueOf(requestParam.get("pageNum")));
    }
    if (requestParam.get("pageSize") != null && !"".equals(String.valueOf(requestParam.get("pageSize")).trim())) {
      pageSize = Integer.parseInt(String.valueOf(requestParam.get("pageSize")));
    }
    ValidationUtils.notNull(requestParam.get("id"), "考试Id不能为空!");
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMSCORELISTALL");
    Paging paging = (Paging) baseService.page(requestParam,pageNum,pageSize);
    if(paging!=null){
      List<Map<String,Object>> scoreList = paging.getList();
      if(scoreList!=null && scoreList.size() > 0){
        for (Map<String, Object> score : scoreList) {
          Timestamp start = (Timestamp) score.get("startTime");
          Timestamp end = (Timestamp) score.get("endTime");
          SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
          String dateStr = DateUtil.printDifference(sdf.format(start), sdf.format(end));
          score.put("dateStr",dateStr);
        }
        paging.setList(scoreList);
      }
    }
    return paging;
    //return  scoreList;
  }


  /*
   所有成绩列表无分页
    */
  @ResponseBody
  @GetMapping("allscorelistnopage")
  public Object allScoreListNoPage(@RequestParam Map<String, Object> requestParam) throws Exception {

    ValidationUtils.notNull(requestParam.get("id"), "考试Id不能为空!");
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMSCORELISTALL");
    List<Map<String,Object>> scoreList = (List<Map<String, Object>>) baseService.list(requestParam);

      if(scoreList!=null && scoreList.size() > 0){
        for (Map<String, Object> score : scoreList) {
            String start = String.valueOf(score.get("startTime"));
            String end = String.valueOf(score.get("endTime"));
          //String dateStr = "";
           if(end == null || end.equals("null")){
             score.put("dateStr","--");
           }else{
             score.put("dateStr",DateUtil.printDifference(start,end));
           }
        }
        return  scoreList;
      }
    return new ArrayList<>();
  }

  @PostMapping("examinationList")
  public Object examinationList(@RequestBody Map<String, Object> requestBody) throws Exception {
    Integer currentPage = (Integer) requestBody.get("pageNum");
    Integer pageSize = (Integer) requestBody.get("pageSize");
    if (currentPage == null) {
        currentPage = 1;
    }
    if (pageSize == null) {
        pageSize = 10;
    }
    //查询到已经参加考试的记录信息
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMINATIONLIST");
    Paging obj = (Paging) baseService.page(requestBody, currentPage, pageSize);
    if (obj != null && obj.getList() != null) {
      List<Map<String, Object>> list = obj.getList();
      List<Map<String, Object>> newlist = new ArrayList<>();
      for (int i = 0; i < list.size(); i++) {
        Map<String, Object> map = list.get(i);
        String startTime = String.valueOf(map.get("startTime"));
        String endTime = String.valueOf(map.get("endTime"));
        String timeNumber = DateUtil.printDifference(startTime, endTime);
        map.remove("startTime");
        map.remove("endTime");
        map.put("startTime", timeNumber);
      }
    }

    //无异常
    return Result.ok(obj);
  }

   /*
 考试列表
  */
  @ResponseBody
  @GetMapping("findAllExamination")
  public Object findAllExam(@RequestParam Map<String, Object> requestParam) throws Exception {

    int pageNum = 1, pageSize = 15;
    if (requestParam.get("pageNum") != null && !"".equals(String.valueOf(requestParam.get("pageNum")).trim())) {
      pageNum = Integer.parseInt(String.valueOf(requestParam.get("pageNum")));
    }
    if (requestParam.get("pageSize") != null && !"".equals(String.valueOf(requestParam.get("pageSize")).trim())) {
      pageSize = Integer.parseInt(String.valueOf(requestParam.get("pageSize")));
    }
    ValidationUtils.notNull(requestParam.get("deptCode"), "部门编号不能为空!");
    ArrayList<String> deptCodesArr = new ArrayList<>();

    String deptCode = String.valueOf(requestParam.get("deptCode"));
    deptCodesArr.add(deptCode);
    Map<String,Object> par = new HashMap<>();
    par.put("deptCode",deptCode);
    //根据deptCode查询子部门
    List<Map<String,Object>> childDept = (List<Map<String, Object>>) userdeptService.save(par);
    //获取子部门的部门编号
    if(childDept!=null && childDept.size() > 0) {
      for (Map<String, Object> child : childDept) {
        if (child.get("deptCode") != null) {
          deptCodesArr.add(String.valueOf(child.get("deptCode")));
        }
      }
    }
    String deptCodes = StringUtils.collectionToDelimitedString(deptCodesArr, ",");
    requestParam.put("deptCodes",deptCodes);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMINATION");

      Paging paging = (Paging) baseService.page(requestParam,pageNum,pageSize);
      return paging;
  }

  /*
   开放阅卷人所在部门的考试列表
 */
  @ResponseBody
  @GetMapping("findAllExaminationMark")
  public Object findAllExaminationRemark(@RequestParam Map<String, Object> requestParam) throws Exception {
    int pageNum = 1, pageSize = 15;
    if (requestParam.get("pageNum") != null && !"".equals(String.valueOf(requestParam.get("pageNum")).trim())) {
      pageNum = Integer.parseInt(String.valueOf(requestParam.get("pageNum")));
    }
    if (requestParam.get("pageSize") != null && !"".equals(String.valueOf(requestParam.get("pageSize")).trim())) {
      pageSize = Integer.parseInt(String.valueOf(requestParam.get("pageSize")));
    }
    ValidationUtils.notNull(requestParam.get("userId"), "当前用户ID不能为空!");
    requestParam.put("userId",String.valueOf(requestParam.get("userId")));
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMINTATIONBYREMARK");
    Paging paging = (Paging) baseService.page(requestParam,pageNum,pageSize);
    return paging;
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
