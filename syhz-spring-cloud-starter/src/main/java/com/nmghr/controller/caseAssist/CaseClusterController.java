package com.nmghr.controller.caseAssist;

import com.alibaba.fastjson.JSONArray;
import com.nmghr.basic.common.Constant;
import com.nmghr.basic.common.Result;
import com.nmghr.basic.common.exception.GlobalErrorException;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.ISaveHandler;
import com.nmghr.basic.core.service.handler.IUpdateHandler;
import com.nmghr.basic.core.util.SpringUtils;
import com.nmghr.basic.core.util.ValidationUtils;
import com.nmghr.hander.update.cluster.ClusterExamineUpdateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 案件集群战役
 */
@RestController
@RequestMapping("/casecluster")
public class CaseClusterController {
  private static final Logger log = LoggerFactory.getLogger(CaseClusterController.class);
  @Autowired
  private IBaseService baseService;

  @Autowired
  private ClusterExamineUpdateHandler clusterExamineUpdateHandler;

  /**
   * 集群战役列表
   * @return
   */
  @GetMapping("/list")
  @ResponseBody
  public Object list(@RequestParam Map<String, Object> params){
    ValidationUtils.notNull(params.get("curDeptCode"), "curDeptCode 不能为空!");
    int pageNum = 1, pageSize = 15;
    if (params.get("pageNum") != null) {
      pageNum = Integer.parseInt(String.valueOf(params.get("pageNum")));
    }
    if (params.get("pageSize") != null) {
      pageSize = Integer.parseInt(String.valueOf(params.get("pageSize")));
    }
    try {
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJCLUSTERASSIST");
      Object obj = baseService.page(params,pageNum,pageSize);
      return Result.ok(obj);
    } catch (Exception e) {
      if (e instanceof GlobalErrorException) {
        GlobalErrorException ge = (GlobalErrorException) e;
        if ("999667".equals(String.valueOf(ge.getCode()))) {
          return Result.fail("999667", ge.getMessage());
        }
      }
    }
    return Result.fail("999668", "请求异常");
  }

  /**
   * 保存集群战役
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
   * 导出集群战役
   * @return
   */
  public Object export(){
    return null;
  }

  /**
   * 集群战役详情密码验证
   * @return
   */
  public Object detailPwd(){
    return null;
  }

  /**
   * 集群战役详情
   * @return
   */
  public Object detail(){
    return null;
  }

  /**
   * 审核信息
   * @return
   */
  @PostMapping("/examin")
  @ResponseBody
  public Object examineList(@RequestBody Map<String, Object> body){
    ValidationUtils.notNull(body.get("userId"), "userId不能为空!");
    ValidationUtils.notNull(body.get("userName"), "userName不能为空!");
    ValidationUtils.notNull(body.get("status"), "status不能为空!");
    ValidationUtils.notNull(body.get("curDeptId"), "curDeptId不能为空!");
    ValidationUtils.notNull(body.get("curDeptName"), "curDeptName不能为空!");
    ValidationUtils.notNull(body.get("curDeptCode"), "curDeptCode不能为空!");
    ValidationUtils.notNull(body.get("bsId"), "bsId不能为空!");
    ValidationUtils.notNull(body.get("flowId"), "flowId不能为空!");
    ValidationUtils.notNull(body.get("wdId"), "wdId不能为空!");
    String flowId = String.valueOf(body.get("flowId"));
    String bsId = String.valueOf(body.get("bsId"));
    if ("6".equals(String.valueOf(body.get("status")))){
      ValidationUtils.notNull(body.get("acceptDeptId"), "acceptDeptId不能为空!");
      ValidationUtils.notNull(body.get("acceptDeptCode"), "acceptDeptCode不能为空!");
      ValidationUtils.notNull(body.get("acceptDeptName"), "acceptDeptName不能为空!");
    }
    try {
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJCLUSTERASSISTBYID");
      Map<String, Object> bean = (Map<String, Object>) baseService.get(bsId);
      if (bean == null) {
        return Result.fail("999667", "集群战役信息不存在！");
      }
      if (!"1".equals(String.valueOf(bean.get("status")))) {
        return Result.fail("999667", "非审核状态不能操作！");
      }
//      if ("3".equals(status)) {
//        body.put("depts", getDepts(String.valueOf(bean.get("recipient"))));
//      }

      body.put("applyPersonId", bean.get("creatorId"));
      body.put("applyPersonName", bean.get("creatorName"));
      body.put("title", bean.get("title"));

      IUpdateHandler updateHandler = SpringUtils.getBean("clusterExamineUpdateHandler", IUpdateHandler.class);
      Object obj = updateHandler.update(flowId, body);
      return Result.ok(obj);
    } catch (Exception e) {
      if (e instanceof GlobalErrorException) {
        GlobalErrorException ge = (GlobalErrorException) e;
        if (String.valueOf(ge.getCode()).contains("999")) {
          return Result.fail("999667", ge.getMessage());
        }
      }
    }
    return Result.fail("999668", "处理异常");
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
   * 集群战役情况统计
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
    ValidationUtils.notNull(body.get("curDeptId"), "curDeptId不能为空!");
    ValidationUtils.notNull(body.get("curDeptName"), "curDeptName不能为空!");
    ValidationUtils.notNull(body.get("curDeptCode"), "curDeptCode不能为空!");
    ValidationUtils.notNull(body.get("clusterCitys"), "clusterCitys不能为空!");
    ValidationUtils.notNull(body.get("status"), "status!");
    String status = String.valueOf(body.get("status"));
    if (!"0".equals(status)&& !"1".equals(status) && !"5".equals(status)) {
      throw new GlobalErrorException("999667", "status状态异常");
    }
    if("1".equals(status)){
      ValidationUtils.notNull(body.get("acceptDeptId"), "上级单位不能为空!");
      ValidationUtils.notNull(body.get("acceptDept"), "上级单位不能为空!");
      ValidationUtils.notNull(body.get("acceptDeptName"), "上级单位不能为空!");
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
