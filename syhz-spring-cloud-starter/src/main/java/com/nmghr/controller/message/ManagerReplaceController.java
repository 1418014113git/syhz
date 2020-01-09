package com.nmghr.controller.message;

import com.alibaba.fastjson.JSONArray;
import com.nmghr.basic.common.Constant;
import com.nmghr.basic.common.Result;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.util.ValidationUtils;
import com.nmghr.service.DeptNameService;
import com.nmghr.service.message.MessageSendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@SuppressWarnings({"unchecked"})
@RestController
@RequestMapping("/mreplace")
public class ManagerReplaceController {
  @Autowired
  @Qualifier("baseService")
  private IBaseService baseService;

  @Autowired
  private DeptNameService deptNameService;

  @Autowired
  private MessageSendService messageSendService;

  /**
   * 保存接收人
   *
   * @param body
   * @return
   */
  @PostMapping("/save")
  @ResponseBody
  public Object save(@RequestBody Map<String, Object> body) {
    ValidationUtils.notNull(body.get("userId"), "userId 不能为空!");
    ValidationUtils.notNull(body.get("replaceIds"), "replaceIds 不能为空!");
    ValidationUtils.notNull(body.get("curDeptCode"), "curDeptCode 不能为空!");
    try {
      JSONArray.parseArray(String.valueOf(body.get("replaceIds")));
    } catch (Exception e) {
      return Result.fail("999667", "replaceIds 应为数组格式的字符串");
    }

    try {
      Map<String, Object> queryP = new HashMap<>();
      queryP.put("deptCode", body.get("curDeptCode"));
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "UMANAGERREPLACE");
      Map<String, Object> map = (Map<String, Object>) baseService.get(queryP);
      if (map == null || map.size() == 0) {
        Map<String, Object> saveP = new HashMap<>();
        saveP.put("replaceIds", body.get("replaceIds"));
        saveP.put("deptCode", body.get("curDeptCode"));
        saveP.put("uId", body.get("userId"));
        saveP.put("deptId", body.get("curDeptId"));
        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "UMANAGERREPLACE");
        return Result.ok(baseService.save(saveP));
      } else {
        Map<String, Object> updP = new HashMap<>();
        updP.put("replaceIds", body.get("replaceIds"));
        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "UMANAGERREPLACE");
        baseService.update(String.valueOf(map.get("id")), updP);
        return Result.ok(map.get("id"));
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return Result.fail("999667", "处理异常");
  }


  @GetMapping("/query")
  @ResponseBody
  public Object query(@RequestParam Map<String, Object> params) {
    ValidationUtils.notNull(params.get("curDeptCode"), "curDeptCode 不能为空!");
    try {
      Map<String, Object> query = new HashMap<>();
      query.put("deptCode", params.get("curDeptCode"));
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "UMANAGERREPLACE");
      Map<String, Object> map = (Map<String, Object>) baseService.get(query);
      if (map == null || map.size() == 0) {
        return new HashMap<>();
      }
      return map;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return Result.fail("999667", "查询失败");
  }

  @GetMapping("/replace")
  @ResponseBody
  public Object replace(@RequestParam Map<String, Object> params) {
    ValidationUtils.notNull(params.get("curDeptId"), "curDeptId 不能为空!");
    ValidationUtils.notNull(params.get("curUserId"), "curUserId 不能为空!");
    try {
      List queryP = new ArrayList();
      queryP.add(params.get("curDeptId"));
      List<Map<String, Object>> depts = messageSendService.getUserList(queryP);
      boolean flag = false;
      for (Map<String, Object> dept : depts) {
        if (String.valueOf(dept.get("id")).equals(String.valueOf(params.get("curUserId")))) {
          flag = true;
          break;
        }
      }
      return Result.ok(flag);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return Result.fail("999667", "查询失败");
  }

  @PostMapping("/search")
  @ResponseBody
  public Object search(@RequestBody Map<String, Object> params) {
    try {
      Map<String, Object> query = new HashMap<>();
      if (!StringUtils.isEmpty(params.get("deptIds"))) {
        String[] array = String.valueOf(params.get("deptIds")).split(",");
        if (array.length > 0) {
          List<Object> ids = new ArrayList<>();
          //查询部门接收人
          List<Map<String, Object>> depts = messageSendService.getUserList(Arrays.asList(array));
          if (depts != null && depts.size() > 0) {
            for (Map<String, Object> m : depts) {
              ids.add(m.get("id"));
            }
          }
          //查询管理人
          query.put("deptIds", Arrays.asList(array));
          query.put("queryType", "managerUserList");
          List<Map<String, Object>> users = (List<Map<String, Object>>) deptNameService.list(query);
          if (users != null && users.size() > 0) {
            for (Map<String, Object> m : users) {
              ids.add(m.get("id"));
            }
          }
          query.put("receiveIds", ids);
        }
      }
      if (!StringUtils.isEmpty(params.get("name"))) {
        query.put("name", params.get("name"));
      }
      query.put("queryType", "receiveList");
      return deptNameService.list(query);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return Result.fail("999667", "查询失败");
  }


  private void validId(Object id) {
    ValidationUtils.notNull(id, "id不能为空!");
    ValidationUtils.regexp(id, "^\\d+$", "非法输入");
  }

}
