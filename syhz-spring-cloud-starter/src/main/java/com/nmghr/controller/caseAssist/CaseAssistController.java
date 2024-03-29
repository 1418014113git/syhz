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
import com.nmghr.service.ajglqbxs.AjglQbxsService;
import com.nmghr.service.ajglqbxs.AjglSignService;
import com.nmghr.service.ajglqbxs.CaseAssistService;
import com.nmghr.util.Sms4Util;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * 案件协查
 */
@SuppressWarnings("unchecked")
@RestController
@RequestMapping("/caseAssist")
public class CaseAssistController {
  private static final Logger log = LoggerFactory.getLogger(CaseAssistController.class);
  @Autowired
  private IBaseService baseService;

  @Autowired
  private AjglQbxsService ajglQbxsService;

  @Autowired
  private CaseAssistService caseAssistService;

  @Autowired
  private AjglSignService ajglSignService;

  /**
   * 案件协查列表
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
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJASSIST");
      Paging obj = (Paging) baseService.page(params, pageNum, pageSize);
      if(obj==null || obj.getList().size()==0){
        return obj;
      }
      Map<String, Object> temp = new LinkedHashMap<>();
      List<Object> ids = new ArrayList();
      List<Map<String, Object>> pglist = obj.getList();
      for (Map<String, Object> m : pglist) {
        m.put("cityCode", String.valueOf(m.get("applyDeptCode")).substring(0,4)+"00");
        temp.put(String.valueOf(m.get("assistId")), m);
        ids.add(m.get("assistId"));
      }
      Map<String, Object> p = new HashMap<>();
      p.put("assistIds", ids);
      p.put("curDeptCode", params.get("curDeptCode"));
      LocalThreadStorage.put(Constant.CONTROLLER_PAGE, false);
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJASSISTDEPTCLUE");
      List<Map<String, Object>> deptInfo = (List<Map<String, Object>>) baseService.list(p);
      if (deptInfo != null && deptInfo.size() > 0) {
        for (Map<String, Object> m : deptInfo) {
          if(StringUtils.isEmpty(m.get("xsNum"))){
            continue;
          }
          Map<String, Object> bean = (Map<String, Object>) temp.get(String.valueOf(m.get("assistId")));
          if (bean != null) {
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
            if (bean.containsKey("sTotal")) {
              bean.put("sTotal", Integer.parseInt(String.valueOf(bean.get("sTotal"))) + Integer.parseInt(String.valueOf(m.get("sAppSum"))));
            } else {
              bean.put("sTotal", Integer.parseInt(String.valueOf(m.get("sAppSum"))));
            }
            if (bean.containsKey("cityCount")) {
              bean.put("cityCount", Integer.parseInt(String.valueOf(bean.get("cityCount"))) + 1);
              bean.put("tTotal", Integer.parseInt(String.valueOf(bean.get("tTotal"))) + 1);
            } else {
              bean.put("cityCount", 1);
              bean.put("tTotal", 1);
            }
            m.put("deptName", getCity(String.valueOf(m.get("deptName"))));
            if (bean.containsKey("deptList")) {
              List<Map<String, Object>> array = (List<Map<String, Object>>) bean.get("deptList");
              BigDecimal xsNum = new BigDecimal(String.valueOf(m.get("xsNum")));
              BigDecimal hc = new BigDecimal(String.valueOf(m.get("hc")));
              if (xsNum.compareTo(BigDecimal.ZERO) > 0) {
                m.put("hcl",  hc.divide(xsNum, 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100")).setScale(2, RoundingMode.DOWN).toString());
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
                m.put("hcl", hc.divide(xsNum, 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100")).setScale(2, RoundingMode.DOWN).toString());
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
    return "-";
  }

  /**
   * 保存案件协查
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
      ISaveHandler saveHandler = SpringUtils.getBean("caseAssistSubmitSaveHandler", ISaveHandler.class);
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
   * 案件协查详情密码验证
   *
   * @return
   */
  @PostMapping("/detailPwd")
  @ResponseBody
  public Object detailPwd(@RequestBody Map<String, Object> body) {
    try {
      validId(body.get("assistId"));
      ValidationUtils.notNull(body.get("pwd"), "密码不能为空!");
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJASSISTPWD");
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
   * 案件协查详情
   *
   * @return
   */
  @GetMapping("/{id}")
  @ResponseBody
  public Object detail(@PathVariable Object id, @RequestParam Map<String, Object> param) {
    try {
      validId(id);
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJASSIST");
      Map<String, Object> bean = (Map<String, Object>) baseService.get(String.valueOf(id));
      bean.putAll(ajglQbxsService.getClueTotal(String.valueOf(id)));

      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJJBXXSYH");
      Map<String, Object> caseBean = (Map<String, Object>) baseService.get(String.valueOf(bean.get("ajbh")));
      bean.putAll(caseBean);
      bean.put("cityCode", String.valueOf(bean.get("applyDeptCode")).substring(0,4)+"00");
      if(!StringUtils.isEmpty(bean.get("readKey"))){
        bean.put("readKey", Sms4Util.Decrypt(String.valueOf(bean.get("readKey"))));
      }
      setCityNum(id, bean);
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

  private void setCityNum(Object id, Map<String, Object> bean) throws Exception {
    Map<String, Object> params = new HashMap<>();
    params.put("assistId", id);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJASSISTDEPTNUMS");
    List<Object> list = (List<Object>) baseService.list(params);
    bean.put("cityNum", list!=null?list.size():0);
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
      params.put("wdType", WorkOrder.caseAssist.getType());
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
   * 案件协查删除
   *
   * @param body
   * @return
   */
  @PostMapping("/delete")
  @ResponseBody
  public Object delete(@RequestBody Map<String, Object> body) {
    ValidationUtils.notNull(body.get("assistId"), "协查ID不能为空!");
    try {
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJASSIST");
      baseService.remove(String.valueOf(body.get("assistId")));
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
   * 案件协查删除
   *
   * @param body
   * @return
   */
  @PostMapping("/appraise")
  @ResponseBody
  public Object appraise(@RequestBody Map<String, Object> body) {
    ValidationUtils.notNull(body.get("assistId"), "assistId不能为空!");
    ValidationUtils.notNull(body.get("deptCode"), "deptCode不能为空!");
    ValidationUtils.notNull(body.get("score"), "score不能为空!");
    try {
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJASSISTDEPT");
      return baseService.update(String.valueOf(body.get("assistId")), body);
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
  public Object signList(String assistId, String deptCode, String deptType, String curDeptCode, Integer pageNum, Integer pageSize) {
    if (pageNum == null) {
      pageNum = 1;
    }
    if (pageSize == null) {
      pageSize = 15;
    }
    ValidationUtils.notNull(assistId, "assistId不能为空!");
    ValidationUtils.notNull(deptType, "deptType不能为空!");
    if(!"123".contains(deptType)){
      return Result.fail("999667", "deptType不正确");
    }
    try {
      Map<String, Object> p = new HashMap<>();
      p.put("assistId", assistId);
      p.put("assistType", 1);
      p.put("deptType",Integer.parseInt(deptType));
      if(!StringUtils.isEmpty(curDeptCode)){
        p.put("curDeptCode", curDeptCode);
      }
      if (!StringUtils.isEmpty(deptCode)) {
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
   * 案件协查情况统计
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
//    ValidationUtils.notNull(body.get("deptName"), "deptName不能为空!");
    ValidationUtils.notNull(body.get("assistId"), "status不能为空!");
    ValidationUtils.notNull(body.get("signId"), "status不能为空!");
    try {
      body.put("assistType", 1);
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
    ValidationUtils.notNull(body.get("bsId"), "协查ID不能为空!");
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
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJASSISTBYID");
      Map<String, Object> bean = (Map<String, Object>) baseService.get(bsId);
      if (bean == null) {
        return Result.fail("999867", "案件协查信息不存在！");
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

      IUpdateHandler updateHandler = SpringUtils.getBean("assistExamineUpdateHandler", IUpdateHandler.class);
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
  public Object numberValid(String dept, String numStr) {
    try {
      ValidationUtils.notNull(dept, "dept不能为空!");
      if (dept.length() < 6) {
        return Result.fail("999881", "部门信息异常");
      }
      String key = caseAssistService.getKey(dept,1);
      Map<String, Object> params = new HashMap<>();
      params.put("key", key);
      params.put("keyLen", key.length());
      params.put("number", numStr);
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJASSISTNUMBERCHECK");
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
    ValidationUtils.notNull(body.get("category"), "下发类型不能为空!");
    if ("submit".equals(String.valueOf(body.get("operator")))) {
      ValidationUtils.notNull(body.get("id"), "id不能为空!");
    } else {
      if ("update".equals(String.valueOf(body.get("operator")))) {
        ValidationUtils.notNull(body.get("id"), "id不能为空!");
      }
      ValidationUtils.notNull(body.get("title"), "标题不能为空!");
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
//      ValidationUtils.notNull(body.get("citys"), "Citys不能为空!");
      ValidationUtils.notNull(body.get("status"), "status!");
      String status = String.valueOf(body.get("status"));
      if ("1".equals(status)) {
        ValidationUtils.notNull(body.get("acceptDeptId"), "上级单位不能为空!");
        ValidationUtils.notNull(body.get("acceptDept"), "上级单位不能为空!");
        ValidationUtils.notNull(body.get("acceptDeptName"), "上级单位不能为空!");
      }
      if ("5".equals(status)) {
        ValidationUtils.notNull(body.get("startDate"), "开始时间不能为空!");
        ValidationUtils.notNull(body.get("endDate"), "截止时间不能为空!");
        ValidationUtils.notNull(body.get("assistNumber"), "编号不能为空!");
      }
    }
  }

  private void validId(Object id) {
    ValidationUtils.notNull(id, "id不能为空!");
    ValidationUtils.regexp(id, "^\\d+$", "非法输入");
  }

}