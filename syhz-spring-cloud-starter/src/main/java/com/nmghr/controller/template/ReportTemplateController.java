package com.nmghr.controller.template;

import com.nmghr.basic.common.Result;
import com.nmghr.basic.core.util.ValidationUtils;
import com.nmghr.service.template.ReportTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@SuppressWarnings({"unchecked"})
@RestController
@RequestMapping("/reportTemplate")
public class ReportTemplateController {

  @Autowired
  private ReportTemplateService reportTemplateService;

  @PostMapping("/validName")
  @ResponseBody
  public Object validName(@RequestBody Map<String, Object> requestBody) {
    ValidationUtils.notNull(requestBody.get("templateName"), "模板名称不能为空");
    ValidationUtils.notNull(requestBody.get("deptCode"), "部门Code不能为空");
    try {
      return Result.ok(reportTemplateService.query(requestBody));
    } catch (Exception e) {
      e.printStackTrace();
      return Result.fail(null);
    }
  }


  @PostMapping("/delete")
  @ResponseBody
  public Object delete(@RequestBody Map<String, Object> requestBody) {
    ValidationUtils.notNull(requestBody.get("templateId"), "模板ID不能为空");
    try {
      reportTemplateService.deleteTemplate(requestBody);
      return Result.ok(requestBody.get("templateId"));
    } catch (Exception e) {
      e.printStackTrace();
      return Result.fail(null);
    }
  }

  @PutMapping("/save")
  @ResponseBody
  public Object save(@RequestBody Map<String, Object> requestBody) {
    ValidationUtils.notNull(requestBody.get("templateName"), "模板名称不能为空");
    ValidationUtils.notNull(requestBody.get("reportType"), "模板类型不能为空");
    ValidationUtils.notNull(requestBody.get("deptId"), "部门ID不能为空");
    ValidationUtils.notNull(requestBody.get("deptCode"), "部门Code不能为空");
    ValidationUtils.notNull(requestBody.get("deptName"), "部门名称不能为空");
    ValidationUtils.notNull(requestBody.get("creationId"), "创建人ID不能为空");
    ValidationUtils.notNull(requestBody.get("creationName"), "创建人姓名不能为空");
    ValidationUtils.notNull(requestBody.get("delAble"), "是否可删除标志不能为空");
    ValidationUtils.notNull(requestBody.get("columnSet"), "模板配置项不能为空");
    try {
      Object id = reportTemplateService.saveTemplate(requestBody);
      return Result.ok(id);
    } catch (Exception e) {
      e.printStackTrace();
      return Result.fail(null);
    }
  }

  @PostMapping("/update")
  @ResponseBody
  public Object update(@RequestBody Map<String, Object> requestBody) {
    ValidationUtils.notNull(requestBody.get("id"), "模板ID不能为空");
    ValidationUtils.notNull(requestBody.get("templateName"), "模板名称不能为空");
    ValidationUtils.notNull(requestBody.get("lastId"), "修改人ID不能为空");
    ValidationUtils.notNull(requestBody.get("lastName"), "修改人姓名不能为空");
    try {
      reportTemplateService.updateTemplate(requestBody);
      return Result.ok(requestBody.get("id"));
    } catch (Exception e) {
      e.printStackTrace();
      return Result.fail(null);
    }
  }

}
