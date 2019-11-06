package com.nmghr.controller.caseAssist;

import com.alibaba.fastjson.JSONArray;
import com.nmghr.basic.common.Result;
import com.nmghr.basic.common.exception.GlobalErrorException;
import com.nmghr.basic.core.service.handler.ISaveHandler;
import com.nmghr.basic.core.util.SpringUtils;
import com.nmghr.basic.core.util.ValidationUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 案件协查
 */
@RestController
@RequestMapping("/caseassist")
public class CaseAssistController {

  /**
   * 案件协查列表
   * @return
   */
  public Object list(){
    return null;
  }

  /**
   * 保存案件协查
   * @return
   */
  @PutMapping("/save")
  @ResponseBody
  public Object save(@RequestBody Map<String, Object> body){
    validParams(body);
    try {
      ISaveHandler saveHandler = SpringUtils.getBean("caseClusterSubmitSaveHandler", ISaveHandler.class);
      Object obj = saveHandler.save(body);
      return Result.ok(obj);
    } catch (Exception e) {
      if (e instanceof GlobalErrorException) {
        GlobalErrorException ge = (GlobalErrorException) e;
        if (String.valueOf(ge.getCode()).contains("999")) {
          return Result.fail("999667", ge.getMessage());
        }
      }
    }
    return Result.fail("999668", "保存失败");
  }

  /**
   * 导出案件协查
   * @return
   */
  public Object export(){
    return null;
  }

  /**
   * 案件协查详情密码验证
   * @return
   */
  public Object detailPwd(){
    return null;
  }

  /**
   * 案件协查详情
   * @return
   */
  public Object detail(){
    return null;
  }

  /**
   * 审核信息
   * @return
   */
  public Object examineList(){
    return null;
  }
  /**
   * 签收信息
   * @return
   */
  public Object signList(){
    return null;
  }

  /**
   * 保存案件协查
   * @return
   */
  public Object remove(){
    return null;
  }

  /**
   * 案件协查情况统计
   * @return
   */
  public Object statistics(){
    return null;
  }

  /**
   * 签收
   * @return
   */
  public Object signup(){
    return null;
  }
  /**
   * 审核
   * @return
   */
  public Object examine(){
    return null;
  }


  private void validParams(Map<String, Object> body) {
    ValidationUtils.notNull(body.get("clusterTitle"), "标题不能为空!");
    ValidationUtils.notNull(body.get("assistContent"), "内容不能为空!");
    if(((String)body.get("assistContent")).length()>50000){
      throw new GlobalErrorException("999667", "正文内容过长！");
    }
    ValidationUtils.notNull(body.get("userId"), "userId不能为空!");
    ValidationUtils.notNull(body.get("userName"), "userName不能为空!");
    ValidationUtils.notNull(body.get("applyPersonPhone"), "curDeptId不能为空!");
    ValidationUtils.notNull(body.get("curDeptName"), "curDeptName不能为空!");
    ValidationUtils.notNull(body.get("curDeptCode"), "curDeptCode不能为空!");
    ValidationUtils.notNull(body.get("clusterCitys"), "curDeptCode不能为空!");
    ValidationUtils.notNull(body.get("status"), "status!");
    String status = (String) body.get("status");
    if (!"0".equals(status)&& !"1".equals(status) && !"5".equals(status)) {
      throw new GlobalErrorException("999667", "status状态异常");
    }
    if("5".equals(status)){
      ValidationUtils.notNull(body.get("startDate"), "开始时间不能为空!");
      ValidationUtils.notNull(body.get("endDate"), "截止时间不能为空!");
      ValidationUtils.notNull(body.get("clusterNumber"), "编号不能为空!");
    }
  }

  private void validId(Object id) {
    ValidationUtils.notNull(id, "id不能为空!");
    ValidationUtils.regexp(id, "^\\d+$", "非法输入");
  }

}
