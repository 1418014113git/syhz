package com.nmghr.controller.caseAssist;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.common.Result;
import com.nmghr.basic.common.exception.GlobalErrorException;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.page.Paging;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.ISaveHandler;
import com.nmghr.basic.core.service.handler.IUpdateHandler;
import com.nmghr.basic.core.util.SpringUtils;
import com.nmghr.basic.core.util.ValidationUtils;
import com.nmghr.common.WorkOrder;
import com.nmghr.hander.update.cluster.ClusterExamineUpdateHandler;
import com.nmghr.service.ajglqbxs.AjglQbxsService;
import com.nmghr.service.ajglqbxs.AjglSignService;
import com.nmghr.service.ajglqbxs.CaseAssistService;
import com.nmghr.util.Sms4Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * 案件集群战役
 */
@SuppressWarnings("unchecked")
@RestController
@RequestMapping("/casecluster")
public class CaseClusterController {
  private static final Logger log = LoggerFactory.getLogger(CaseClusterController.class);
  @Autowired
  private IBaseService baseService;

  @Autowired
  private ClusterExamineUpdateHandler clusterExamineUpdateHandler;

  @Autowired
  private AjglQbxsService ajglQbxsService;

  @Autowired
  private CaseAssistService caseAssistService;

  @Autowired
  private AjglSignService ajglSignService;

  /**
   * 集群战役列表
   *
   * @return
   */
  @GetMapping("/list")
  @ResponseBody
  public Object list(@RequestParam Map<String, Object> params) {
    ValidationUtils.notNull(params.get("curDeptCode"), "curDeptCode 不能为空!");
    int pageNum = 1, pageSize = 15;
    if (params.get("pageNum") != null && !StringUtils.isEmpty(params.get("pageNum"))) {
      pageNum = Integer.parseInt(String.valueOf(params.get("pageNum")));
    }
    if (params.get("pageSize") != null && !StringUtils.isEmpty(params.get("pageSize"))) {
      pageSize = Integer.parseInt(String.valueOf(params.get("pageSize")));
    }
    if(!StringUtils.isEmpty(params.get("reginCode"))){
      params.put("cityCode",params.get("reginCode"));
    } else {
      if(StringUtils.isEmpty(params.get("cityCode"))){
        if(!StringUtils.isEmpty(params.get("provinceCode"))){
          params.put("cityCode",params.get("provinceCode"));
        }
      }
    }
    if(!StringUtils.isEmpty(params.get("isCheck"))&&Boolean.valueOf(String.valueOf(params.get("isCheck")))){
      params.put("isCheck",0);
    } else {
      params.put("isCheck",3);
    }
    try {
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJCLUSTERASSIST");
      Paging obj = (Paging) baseService.page(params, pageNum, pageSize);
      if(obj==null || obj.getList().size()==0){
        return obj;
      }
      Map<String, Object> temp = new LinkedHashMap<>();
      List<Object> ids = new ArrayList();
      List<Map<String, Object>> pglist = obj.getList();
      for (Map<String, Object> m : pglist) {
        m.put("cityCode", String.valueOf(m.get("applyDeptCode")).substring(0,4)+"00");
        m.put("deptType", getDeptType(String.valueOf(m.get("applyDeptCode"))));
        temp.put(String.valueOf(m.get("clusterId")), m);
        ids.add(m.get("clusterId"));
      }
      Map<String, Object> p = new HashMap<>();
      p.put("assistIds", ids);
      p.put("curDeptCode", params.get("curDeptCode"));
      LocalThreadStorage.put(Constant.CONTROLLER_PAGE, false);
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJCLUSTERASSISTDEPTCLUE");
      List<Map<String, Object>> deptInfo = (List<Map<String, Object>>) baseService.list(p);
      if (deptInfo != null && deptInfo.size() > 0) {
        for (Map<String, Object> m : deptInfo) {
          Map<String, Object> bean = (Map<String, Object>) temp.get(String.valueOf(m.get("clusterId")));
          if (bean != null) {
            if (bean.containsKey("xsCount")) {
              bean.put("xsCount", Integer.parseInt(String.valueOf(bean.get("xsCount"))) + Integer.parseInt(String.valueOf(m.get("xsNum"))));
            } else {
              bean.put("xsCount", Integer.parseInt(String.valueOf(m.get("xsNum"))));
            }
            if (bean.containsKey("hcCount")) {
              bean.put("hcCount", Integer.parseInt(String.valueOf(bean.get("hcCount"))) + Integer.parseInt(String.valueOf(m.get("hc"))));
            } else {
              bean.put("hcCount", Integer.parseInt(String.valueOf(m.get("hc"))));
            }
            if (bean.containsKey("tCount")) {
              bean.put("tCount", Integer.parseInt(String.valueOf(bean.get("tCount"))) + Integer.parseInt(String.valueOf(m.get("tApp"))));
            } else {
              bean.put("tCount", Integer.parseInt(String.valueOf(m.get("tApp"))));
            }
            if (bean.containsKey("sCount")) {
              bean.put("sCount", Integer.parseInt(String.valueOf(bean.get("sCount"))) + Integer.parseInt(String.valueOf(m.get("sApp"))));
            } else {
              bean.put("sCount", Integer.parseInt(String.valueOf(m.get("sApp"))));
            }
            if (bean.containsKey("cityCount")) {
              bean.put("cityCount", Integer.parseInt(String.valueOf(bean.get("cityCount"))) + 1);
            } else {
              bean.put("cityCount", 1);
            }
            m.put("deptName", getCity(String.valueOf(m.get("deptName"))));
            m.put("applyDeptCode",String.valueOf(bean.get("applyDeptCode")));
            m.put("cityCode", String.valueOf(m.get("deptCode")).substring(0,4)+"00");
            m.put("deptType", getDeptType(String.valueOf(m.get("deptCode"))));
            if (bean.containsKey("deptList")) {
              List<Map<String, Object>> array = (List<Map<String, Object>>) bean.get("deptList");
              BigDecimal xsNum = new BigDecimal(String.valueOf(m.get("xsNum")));
              BigDecimal hc = new BigDecimal(String.valueOf(m.get("hc")));
              if (xsNum.compareTo(BigDecimal.ZERO) > 0) {
                hc = hc.divide(xsNum, 2, RoundingMode.DOWN).multiply(new BigDecimal("100")).setScale(0, RoundingMode.DOWN);
                m.put("hcl", hc.intValue());
              } else {
                m.put("hcl", 0);
              }
              array.add(m);
              bean.put("deptList",array);
            } else {
              List<Map<String, Object>> array = new ArrayList<>();
              BigDecimal xsNum = new BigDecimal(String.valueOf(m.get("xsNum")));
              BigDecimal hc = new BigDecimal(String.valueOf(m.get("hc")));
              if (xsNum.compareTo(BigDecimal.ZERO) > 0) {
                hc = hc.divide(xsNum, 2, RoundingMode.DOWN).multiply(new BigDecimal("100")).setScale(0, RoundingMode.DOWN);
                m.put("hcl", hc.intValue());
              } else {
                m.put("hcl", 0);
              }
              array.add(m);
              bean.put("deptList",array);
            }

          }
        }
      }
      return Result.ok(new Paging<>(obj.getPageSize(), obj.getPageNum(), obj.getTotalCount(), new ArrayList<>(temp.values())));
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
  private String getCity(String name){
    if(name.contains("省")){
      if(name.contains("市")){
        return name.substring(3,name.indexOf("市")+1);
      }
      if(name.contains("区")){
        return name.substring(3,name.indexOf("区")+1);
      }
    } else {
      if(name.contains("市")){
        return name.substring(0,name.indexOf("市")+1);
      }
      if(name.contains("区")){
        return name.substring(0,name.indexOf("区")+1);
      }
    }
    return "";
  }
  private int getDeptType(String deptCode){
    if("610000".equals(deptCode.substring(0,6))){
      return 1;
    } else {
      if(!"00".equals(deptCode.substring(deptCode.length()-2,deptCode.length()))){
        return 4;
      } else {
        if("00".equals(deptCode.substring(4,6)) && "0000".equals(deptCode.substring(deptCode.length()-4,deptCode.length()))){
          return 2;
        } else {
          return 3;
        }
      }
    }
  }

  /**
   * 保存集群战役
   *
   * @return
   */
  @PutMapping("/save")
  @ResponseBody
  public Object save(@RequestBody Map<String, Object> body) {
    validParams(body);
    try {
      if (!StringUtils.isEmpty(body.get("passKey"))) {
        body.put("readKey", Sms4Util.Encryption(String.valueOf(body.get("passKey"))));
      } else {
        body.put("readKey", "");
      }
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
   * 集群战役详情密码验证
   *
   * @return
   */
  @PostMapping("/detailPwd")
  @ResponseBody
  public Object detailPwd(@RequestBody Map<String, Object> body) {
    try {
      validId(body.get("assistId"));
      ValidationUtils.notNull(body.get("pwd"), "密码不能为空!");
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJCLUSTERASSISTPWD");
      Map<String, Object> bean = (Map<String, Object>) baseService.get(String.valueOf(body.get("assistId")));
      if (bean == null) {
        return Result.fail("999882", "资源不存在");
      }
      if (bean.get("readKey") != null) {
        if (Sms4Util.Encryption(String.valueOf(body.get("pwd"))).equals(bean.get("readKey"))) {
          return Result.ok(true);
        }
      }
      return Result.fail("999668", "查阅密码不正确，请重新输入。");

    } catch (Exception e) {
      if (e instanceof GlobalErrorException) {
        GlobalErrorException ge = (GlobalErrorException) e;
        if (String.valueOf(ge.getCode()).contains("999")) {
          return Result.fail("999667", ge.getMessage());
        }
      }
    }
    return Result.fail("999668", "查询异常");
  }

  /**
   * 集群战役详情
   *
   * @return
   */
  @GetMapping("/{id}")
  @ResponseBody
  public Object detail(@PathVariable Object id, @RequestParam Map<String, Object> param) {
    try {
      validId(id);
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJCLUSTERASSIST");
      Map<String, Object> bean = (Map<String, Object>) baseService.get(String.valueOf(id));
      bean.putAll(ajglQbxsService.getClueTotal(String.valueOf(id)));
      bean.put("cityCode", String.valueOf(bean.get("applyDeptCode")).substring(0,4)+"00");
      if(!StringUtils.isEmpty(bean.get("readKey"))){
        bean.put("passKey", Sms4Util.Decrypt(String.valueOf(bean.get("readKey"))));
      }
      return Result.ok(bean);
    } catch (Exception e) {
      if (e instanceof GlobalErrorException) {
        GlobalErrorException ge = (GlobalErrorException) e;
        if (String.valueOf(ge.getCode()).contains("999")) {
          return Result.fail("999667", ge.getMessage());
        }
      }
    }
    return Result.fail("999668", "查询异常");
  }

  /**
   * 审核信息
   *
   * @return
   */
  @GetMapping("/examineList")
  @ResponseBody
  public Object examineList(@RequestParam Map<String, Object> param) {
    try {
      validId(param.get("id"));
      Map<String, Object> params = new HashMap<>();
      params.put("wdType", WorkOrder.caseCluster.getType());
      params.put("associationValue", param.get("id"));
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "DETAILWORKORDERLIST");
      return Result.ok(baseService.list(params));
    } catch (Exception e) {
      if (e instanceof GlobalErrorException) {
        GlobalErrorException ge = (GlobalErrorException) e;
        if (String.valueOf(ge.getCode()).contains("999")) {
          return Result.fail("999667", ge.getMessage());
        }
      }
    }
    return Result.fail("999668", "查询异常");
  }

  /**
   * 集群战役删除
   *
   * @param body
   * @return
   */
  @PostMapping("/delete")
  @ResponseBody
  public Object delete(@RequestBody Map<String, Object> body) {
    ValidationUtils.notNull(body.get("clusterId"), "clustId不能为空!");
    try {
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJCLUSTERASSIST");
      baseService.remove(String.valueOf(body.get("clusterId")));
      return true;
    } catch (Exception e) {
      if (e instanceof GlobalErrorException) {
        GlobalErrorException ge = (GlobalErrorException) e;
        if (String.valueOf(ge.getCode()).contains("999")) {
          return Result.fail("999667", ge.getMessage());
        }
      }
    }
    return Result.fail("999887", "请求异常");
  }


  /**
   * 集群战役删除
   *
   * @param body
   * @return
   */
  @PostMapping("/appraise")
  @ResponseBody
  public Object appraise(@RequestBody Map<String, Object> body) {
    ValidationUtils.notNull(body.get("clusterId"), "clustId不能为空!");
    ValidationUtils.notNull(body.get("deptCode"), "deptCode不能为空!");
    ValidationUtils.notNull(body.get("score"), "score不能为空!");
    try {
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJCLUSTERASSISTDEPT");
      return baseService.update(String.valueOf(body.get("clusterId")), body);
    } catch (Exception e) {
      if (e instanceof GlobalErrorException) {
        GlobalErrorException ge = (GlobalErrorException) e;
        if (String.valueOf(ge.getCode()).contains("999")) {
          return Result.fail("999667", ge.getMessage());
        }
      }
    }
    return Result.fail("999887", "请求异常");
  }

  /**
   * 签收信息列表
   *
   * @return
   */
  @GetMapping("/signList")
  @ResponseBody
  public Object signList(String assistId, String deptCode, Integer pageNum, Integer pageSize) {
    if (pageNum == null) {
      pageNum = 1;
    }
    if (pageSize == null) {
      pageSize = 15;
    }
    ValidationUtils.notNull(assistId, "assistId不能为空!");
    try {
      Map<String, Object> p = new HashMap<>();
      p.put("assistId", assistId);
      p.put("assistType", 2);
      if (deptCode != null) {
        p.put("deptCode", deptCode);
      }
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJGLQBXSSIGN");
      return Result.ok(baseService.page(p, pageNum, pageSize));
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
   * 保存案件协查
   *
   * @return
   */
  public Object remove() {
    return null;
  }

  /**
   * 集群战役情况统计
   *
   * @return
   */
  public Object statistics() {
    return null;
  }

  /**
   * 签收
   *
   * @return
   */
  @PostMapping("/signup")
  @ResponseBody
  public Object signup(@RequestBody Map<String, Object> body) {
    ValidationUtils.notNull(body.get("userId"), "userId不能为空!");
    ValidationUtils.notNull(body.get("userName"), "userName不能为空!");
    ValidationUtils.notNull(body.get("deptCode"), "deptCode不能为空!");
    ValidationUtils.notNull(body.get("assistId"), "status不能为空!");
    ValidationUtils.notNull(body.get("signId"), "status不能为空!");
    try {
      body.put("assistType", 2);
      return Result.ok(ajglSignService.signing(body));
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
   * 审核
   *
   * @return
   */
  @PostMapping("/examine")
  @ResponseBody
  public Object examine(@RequestBody Map<String, Object> body) {
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
    if ("6".equals(String.valueOf(body.get("status")))) {
      ValidationUtils.notNull(body.get("acceptDeptId"), "acceptDeptId不能为空!");
      ValidationUtils.notNull(body.get("acceptDeptCode"), "acceptDeptCode不能为空!");
      ValidationUtils.notNull(body.get("acceptDeptName"), "acceptDeptName不能为空!");
    }
    if ("3".equals(String.valueOf(body.get("status")))) {
      ValidationUtils.notNull(body.get("number"), "编号不能为空!");
      ValidationUtils.notNull(body.get("startDate"), "开始时间不能为空!");
      ValidationUtils.notNull(body.get("endDate"), "截止时间不能为空!");
    }
    try {
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJCLUSTERASSISTBYID");
      Map<String, Object> bean = (Map<String, Object>) baseService.get(bsId);
      if (bean == null) {
        return Result.fail("999867", "集群战役信息不存在！");
      }
      if (!"1".equals(String.valueOf(bean.get("status"))) && !"2".equals(String.valueOf(bean.get("status")))) {
        return Result.fail("999867", "非审核状态不能操作！");
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
   * 获取编号
   *
   * @return
   */
  @GetMapping("/number")
  @ResponseBody
  public Object number(String dept) {
    if (dept.length() < 6) {
      return Result.fail("999881", "部门信息异常");
    }
    try {
      return Result.ok(caseAssistService.number(dept, 1));
    } catch (Exception e) {
      if (e instanceof GlobalErrorException) {
        GlobalErrorException ge = (GlobalErrorException) e;
        if (String.valueOf(ge.getCode()).contains("999")) {
          return Result.fail("999667", ge.getMessage());
        }
      }
    }
    return Result.fail("999668", "查询异常");
  }

  /**
   * 审核时或下发修改时判断编号是否重复
   *
   * @return
   */
  @GetMapping("/numberValid")
  @ResponseBody
  public Object numberValid(String dept, String numStr, String id) {
    try {
      ValidationUtils.notNull(dept, "dept不能为空!");
      if (dept.length() < 6) {
        return Result.fail("999881", "部门信息异常");
      }
      Map<String, Object> params = new HashMap<>();
      params.put("deptCode", dept);
      params.put("number", numStr);
      params.put("id", id);
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJCLUSTERNUMBERCHECK");
      List<Map<String, Object>> list = (List<Map<String, Object>>) baseService.list(params);
      return Result.ok((list == null || list.size() == 0));
    } catch (Exception e) {
      if (e instanceof GlobalErrorException) {
        GlobalErrorException ge = (GlobalErrorException) e;
        if (String.valueOf(ge.getCode()).contains("999")) {
          return Result.fail("999667", ge.getMessage());
        }
      }
    }
    return Result.fail("999668", "查询异常");
  }


  private void validParams(Map<String, Object> body) {
    ValidationUtils.notNull(body.get("operator"), "操作类型不能为空!");
    if ("submit".equals(String.valueOf(body.get("operator")))) {
      ValidationUtils.notNull(body.get("id"), "id不能为空!");
    } else {
      ValidationUtils.notNull(body.get("category"), "下发类型不能为空!");
      if ("update".equals(String.valueOf(body.get("operator")))) {
        ValidationUtils.notNull(body.get("id"), "id不能为空!");
      }
      ValidationUtils.notNull(body.get("clusterTitle"), "标题不能为空!");
      ValidationUtils.notNull(body.get("assistContent"), "内容不能为空!");
      if (((String) body.get("assistContent")).length() > 50000) {
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
      if (!"0".equals(status) && !"1".equals(status) && !"5".equals(status)) {
        throw new GlobalErrorException("999667", "status状态异常");
      }
      if ("1".equals(status)) {
        ValidationUtils.notNull(body.get("acceptDeptId"), "上级单位不能为空!");
        ValidationUtils.notNull(body.get("acceptDept"), "上级单位不能为空!");
        ValidationUtils.notNull(body.get("acceptDeptName"), "上级单位不能为空!");
      }
      if ("5".equals(status)) {
        ValidationUtils.notNull(body.get("startDate"), "开始时间不能为空!");
        ValidationUtils.notNull(body.get("endDate"), "截止时间不能为空!");
        ValidationUtils.notNull(body.get("clusterNumber"), "编号不能为空!");
      }
    }
  }

  private void validId(Object id) {
    ValidationUtils.notNull(id, "id不能为空!");
    ValidationUtils.regexp(id, "^\\d+$", "非法输入");
  }

}