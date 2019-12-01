package com.nmghr.controller.caseAssist;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.nmghr.basic.common.Constant;
import com.nmghr.basic.common.Result;
import com.nmghr.basic.common.exception.GlobalErrorException;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.ISaveHandler;
import com.nmghr.basic.core.util.SpringUtils;
import com.nmghr.basic.core.util.ValidationUtils;
import com.nmghr.common.WorkOrder;
import com.nmghr.service.ajglqbxs.AjglQbxsFeedBackService;
import com.nmghr.service.ajglqbxs.AjglQbxsService;
import com.sargeraswang.util.ExcelUtil.ExcelUtil;
import org.apache.commons.collections.IteratorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.util.*;

/**
 * 案件协查线索
 */
@SuppressWarnings("unchecked")
@RestController
@RequestMapping("/caseassistclue")
public class CaseAssistClueController {

  private static final Logger log = LoggerFactory.getLogger(CaseAssistClueController.class);
  @Autowired
  private IBaseService baseService;

  @Autowired
  private AjglQbxsService ajglQbxsService;

  @Autowired
  private AjglQbxsFeedBackService ajglQbxsFeedBackService;

  /**
   * 案件协查列表
   *
   * @return
   */
  @PostMapping(value = "/upload")
  @ResponseBody
  public Object clueImport(@RequestParam("file") MultipartFile mulFile,
                           @RequestParam("type") Object type,
                           @RequestParam("userId") Object userId,
                           @RequestParam("userName") Object userName,
                           @RequestParam("category") Object category,
                           @RequestParam("curDeptCode") Object curDeptCode,
                           @RequestParam("curDeptName") Object curDeptName,
                           @RequestParam("assistId") Object assistId,Object xfType) {
    try {
      if (null != mulFile) {
        Collection<LinkedHashMap> list = ExcelUtil.importExcel(LinkedHashMap.class, mulFile.getInputStream(), 0);
        if (!CollectionUtils.isEmpty(list)) {
          log.info("excel uploadFile file query size {}", list.size());
          if (list.size() > 1000) {
            log.error("excel uploadFile error, Maximum length exceeds 1000 ");
            throw new GlobalErrorException("99952", "最多不能超过1000条");
          }

          List<LinkedHashMap<String, Object>> params = IteratorUtils.toList(list.iterator());
//          List<LinkedHashMap<String, Object>> params = IteratorUtils.toList(list.iterator());
          if (params.size() > 0) {
            LinkedHashMap<String, Object> map = params.get(0);
//            LinkedHashMap<String, Object> map = params.get(0);
            List<String> keys = IteratorUtils.toList(map.keySet().iterator());
            StringBuilder err = new StringBuilder();
            if (!keys.contains("序号")) {
              err.append("标题必须包含《序号》;");
            }
            if (!keys.contains("地址")) {
              err.append("标题必须包含《地址》;");
            }
            if (err.length() > 0) {
              return Result.fail("999668", err.toString());
            }
          }
          Map<String, Object> data = new HashMap<>();
          data.put("type", type);
          data.put("category", category);
          data.put("userId", userId);
          data.put("userName", userName);
          data.put("curDeptCode", curDeptCode);
          data.put("curDeptName", curDeptName);
          data.put("assistId", assistId);
          data.put("xfType", xfType);
          data.put("list", params);
          ISaveHandler saveHandler = SpringUtils.getBean("qbxsSaveHandler", ISaveHandler.class);
          Object obj = saveHandler.save(data);
          return Result.ok(obj);
        } else {
          return Result.fail("99954", "上传文件为空");
        }
      } else {
        return Result.fail("99954", "上传文件为空");
      }
    } catch (IllegalArgumentException e) {
      log.error("excel uploadFile error", e.getMessage());
      return Result.fail("99951", "上传文件错误");
    } catch (FileNotFoundException e) {
      log.error("excel uploadFile error", e.getMessage());
      return Result.fail("99954", "上传文件为空");
    } catch (GlobalErrorException e) {
      return Result.fail("999668", e.getMessage());
    } catch (Exception e) {
      log.error("excel uploadFile error", e.getMessage());
    }
    return Result.fail("999669", "保存失败");
  }

  /**
   * 协查线索列表
   *
   * @return
   */
  public Object clueList() {
    return null;
  }

  /**
   * 协查线索列表简版
   *
   * @return
   */
  @GetMapping("/simpleList")
  @ResponseBody
  public Object simpleList(@RequestParam Map<String, Object> param) {
    try {
      validId(param.get("assistId"));
      Map<String, Object> params = new HashMap<>();
      params.put("assistId", param.get("assistId"));
      String type = "";
      if (param.containsKey("type") && !StringUtils.isEmpty(param.get("type"))) {
        type = String.valueOf(param.get("type"));
      }
      if ("".equals(type) || "2".equals(type)) { // 集群战役
        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJGLQBXSBASECLUECOUNT");
      }
      if ("1".equals(type)) { // 案件协查
        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJASSISTGLQBXSBASECLUECOUNT");
      }
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
   * 协查线索分发
   *
   * @return
   */
  @PostMapping("/distribute")
  @ResponseBody
  public Object distribute(@RequestBody Map<String, Object> body) {
    ValidationUtils.notNull(body.get("assistId"), "assistId不能为空!");
    ValidationUtils.notNull(body.get("ids"), "线索不能为空!");
    ValidationUtils.notNull(body.get("acceptDeptName"), "acceptDeptName不能为空!");
    ValidationUtils.notNull(body.get("acceptDeptCode"), "acceptDeptCode不能为空!");
    ValidationUtils.notNull(body.get("curDeptCode"), "curDeptCode不能为空!");
    if (body.get("curDeptType") != null) {
      ValidationUtils.notNull(body.get("curDeptName"), "curDeptName不能为空!");
      ValidationUtils.notNull(body.get("userId"), "userId不能为空!");
      ValidationUtils.notNull(body.get("userName"), "userName不能为空!");
    }

    try {
      body.put("assistType",2);
      Object obj = ajglQbxsService.distributeClue(body);
      return Result.ok(obj);
    } catch (Exception e) {
      if (e instanceof GlobalErrorException) {
        GlobalErrorException ge = (GlobalErrorException) e;
        if (String.valueOf(ge.getCode()).contains("999")) {
          return Result.fail("999667", ge.getMessage());
        }
      }
    }
    return Result.fail("999668", "提交失败");
  }

  /**
   * 协查线索取消分发
   *
   * @return
   */
  @PostMapping("/cancelDistribute")
  @ResponseBody
  public Object cancelDistribute(@RequestBody Map<String, Object> body) {
    ValidationUtils.notNull(body.get("assistId"), "assistId不能为空!");
    ValidationUtils.notNull(body.get("qbxsId"), "qbxsId不能为空!");

    try {
      Object obj = ajglQbxsService.cancelDistribute(body.get("assistId"), body.get("qbxsId"),body.get("qbxsDeptId"));
      return Result.ok(obj);
    } catch (Exception e) {
      if (e instanceof GlobalErrorException) {
        GlobalErrorException ge = (GlobalErrorException) e;
        if (String.valueOf(ge.getCode()).contains("999")) {
          return Result.fail("999667", ge.getMessage());
        }
      }
    }
    return Result.fail("999668", "提交失败");
  }

  /**
   * 协查线索 删除
   *
   * @return
   */
  @PostMapping("/delete")
  @ResponseBody
  public Object delClue(@RequestBody Map<String, Object> body) {
    ValidationUtils.notNull(body.get("assistId"), "assistId不能为空!");
    ValidationUtils.notNull(body.get("qbxsId"), "qbxsId不能为空!");

    try {
      Object obj = ajglQbxsService.removeClue(body.get("assistId"), body.get("qbxsId"), body.get("qbxsDeptId"));
      return Result.ok(obj);
    } catch (Exception e) {
      if (e instanceof GlobalErrorException) {
        GlobalErrorException ge = (GlobalErrorException) e;
        if (String.valueOf(ge.getCode()).contains("999")) {
          return Result.fail("999667", ge.getMessage());
        }
      }
    }
    return Result.fail("999668", "提交失败");
  }

  /**
   * 反馈线索列表
   *
   * @return
   */
  @GetMapping("/getCluesNum")
  @ResponseBody
  public Object getCluesNum(@RequestParam Map<String, Object> body) {
    ValidationUtils.notNull(body.get("assistId"), "集群战役Id不能为空!");
    try {
      Object obj = ajglQbxsService.getClueTotal(String.valueOf(body.get("assistId")));
      return Result.ok(obj);
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
   * 反馈线索列表
   *
   * @return
   */
  @GetMapping("/feedBackClues")
  @ResponseBody
  public Object feedBackClues(@RequestParam Map<String, Object> body) {
    ValidationUtils.notNull(body.get("assistId"), "集群战役Id不能为空!");
    try {
      if(body.get("reginCode")!=null){
        body.put("cityCode",body.get("reginCode"));
      } else {
        if(body.get("cityCode")!=null){
          body.put("cityCode",body.get("cityCode"));
        }
      }
      Object obj = ajglQbxsService.feedBackList(body);
      return Result.ok(obj);
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
   * 线索反馈
   *
   * @return
   */
  @PostMapping("/feedBack")
  @ResponseBody
  public Object feedBack(@RequestBody Map<String, Object> body) {
    ValidationUtils.notNull(body.get("type"), "处理类型不能为空!");
    ValidationUtils.notNull(body.get("fbId"), "反馈id不能为空!");
    if ("result".equals(body.get("type"))) {
      ValidationUtils.notNull(body.get("qbxsResult"), "反馈结果不能为空!");
    } else if ("saveSyajs".equals(body.get("type")) || "deleteSyajs".equals(body.get("type"))) {
      ValidationUtils.notNull(body.get("syajs"), "syajs不能为空!");
    } else if ("saveZbxss".equals(body.get("type")) || "deleteZbxss".equals(body.get("type"))) {
      ValidationUtils.notNull(body.get("zbxss"), "zbxss不能为空!");
    } else {
      return Result.fail("999887", "参数异常");
    }
    try {
      return Result.ok(ajglQbxsFeedBackService.feedBack(body));
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
   * 反馈线索列表
   *
   * @return
   */
  @GetMapping("/feedBack/detail")
  @ResponseBody
  public Object feedBackAJList(@RequestParam Map<String, Object> param) {
    ValidationUtils.notNull(param.get("fbId"), "fbId不能为空!");
    try {
      if ("detail".equals(param.get("type"))) {
        return ajglQbxsService.feedBackDetail(param);
      }
      if ("ys".equals(param.get("type"))) {
        return ajglQbxsService.feedBackSyAJList(param);
      }
      if ("zb".equals(param.get("type"))) {
        return ajglQbxsService.feedBackZbAJList(param);
      }
      return new ArrayList<>();
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
   * 线索协查战国反馈表
   *
   * @return
   */
  @GetMapping("/detailCount")
  @ResponseBody
  public Object detailCount(@RequestParam Map<String, Object> requestMap) {
    try {
      return ajglQbxsService.feedBackResultList(requestMap);
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
   * 案件协查情况统计
   *
   * @return
   */
  @GetMapping("/ajSearch")
  @ResponseBody
  public Object ajSearch(@RequestParam Map<String, Object> requestMap) {
    try {
      Map<String, Object> params = new HashMap<>();
      params.put("fbId", requestMap.get("fbId"));
      params.put("assistId", requestMap.get("assistId"));
      params.put("type", requestMap.get("type"));
      List ajbhs = getAjbhs(params);
      params = new HashMap<>();
      params.put("departCode", requestMap.get("deptCode"));
      params.put("ajmc", requestMap.get("ajmc"));
      if (ajbhs != null) {
        params.put("ajbhs", ajbhs);
      }
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJASSISTSEARCH");
      return baseService.list(params);
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
   * 案件协查集群战役待反馈
   *
   * @return
   */
  @GetMapping("/assistNeedFeedBack")
  @ResponseBody
  public Object assistNeedFeedBack(@RequestParam Map<String, Object> param) {
    ValidationUtils.notNull(param.get("deptCode"), "deptCode不能为空!");
    try {
      List<Map<String, Object>> result = new ArrayList();
      Map<String, Object> map = new HashMap<>();
      map.put("assistType", 2);
      map.put("num", 0);
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJCLUSTERNEEDFEEDBACK");
      Map<String, Object> obj= (Map<String, Object>) baseService.get(String.valueOf(param.get("deptCode")));
      if(obj!=null && obj.containsKey("num")){
        map.putAll(obj);
      }
      result.add(map);
//      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJCLUSTERNEEDFEEDBACK");
//      baseService.get(dept);
      map = new HashMap<>();
      map.put("assistType", 1);
      map.put("num", 0);
      result.add(map);
      return result;
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


  private List getAjbhs(Map<String, Object> params) throws Exception {
    if ("1".equals(params.get("type"))) {
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJCLUSTERFEEDBACK");
      Map<String, Object> fb = (Map<String, Object>) baseService.get(params);
      if (fb == null) {
        return null;
      }
      Object ys = fb.get("syajs");
      if (ys == null || StringUtils.isEmpty(ys)) {
        return null;
      }
      return JSONArray.parseArray(String.valueOf(ys)).toJavaList(String.class);
    }
    if ("2".equals(params.get("type"))) {
      //查询
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJCLUSTERFEEDBACK");
      Map<String, Object> fb = (Map<String, Object>) baseService.get(params);
      if (fb == null) {
        return null;
      }
      Object zb = fb.get("zbxss");
      if (zb == null || StringUtils.isEmpty(zb)) {
        return null;
      }
      Map<String, Object> zbJson = JSONObject.toJavaObject(JSONObject.parseObject(String.valueOf(zb)), Map.class);
      if (zbJson == null) {
        return null;
      }
      List<String> ajbhs = IteratorUtils.toList(zbJson.keySet().iterator());
      if (ajbhs == null || ajbhs.size() == 0) {
        return null;
      } else {
        return ajbhs;
      }
    }
    return null;
  }


  private void validId(Object id) {
    ValidationUtils.notNull(id, "id不能为空!");
    ValidationUtils.regexp(id, "^\\d+$", "非法输入");
  }

}

