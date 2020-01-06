package com.nmghr.service.template;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.common.Result;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.util.ValidationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * @author yj
 * @version 1.0
 * @date 2019/11/7 19:15
 **/
@Service("reportTemplateService")
public class ReportTemplateService {

  private final static String REPORT_TEMPLATE = "ajreporttemplate";
  private final static String REPORT_TEMPLATE_COLUMN = "ajreporttemplatecolumn";

  @Autowired
  @Qualifier("baseService")
  private IBaseService baseService;

  public List<Map<String, Object>> query(Map<String, Object> requestBody) throws Exception {
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, REPORT_TEMPLATE);
    return (List<Map<String, Object>>) baseService.list(requestBody);
  }

  public List<Map<String, Object>> queryTemplateColumn(Map<String, Object> requestBody) throws Exception {
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, REPORT_TEMPLATE_COLUMN);
    return (List<Map<String, Object>>) baseService.list(requestBody);
  }

  @Transactional(rollbackFor = Exception.class)
  public void deleteTemplate(Map<String, Object> requestBody) throws Exception {
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, REPORT_TEMPLATE);
    baseService.remove(String.valueOf(requestBody.get("templateId")));

    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, REPORT_TEMPLATE_COLUMN);
    baseService.remove(requestBody);
  }

  @Transactional(rollbackFor = Exception.class)
  public Object saveTemplate(Map<String, Object> requestBody) throws Exception {
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, REPORT_TEMPLATE);
    Object id = baseService.save(requestBody);

    List<Map<String, Object>> columnList = (List<Map<String, Object>>) requestBody.get("columnSet");
    for (Map<String, Object> column : columnList) {
      column.put("templateId", id);
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, REPORT_TEMPLATE_COLUMN);
      if (column.containsKey("sortType") && !StringUtils.isEmpty(column.get("sortType"))) {
        column.put("sortType", String.valueOf(column.get("sortType")));
      }
      baseService.save(column);
    }
    return id;
  }

  @Transactional(rollbackFor = Exception.class)
  public void updateTemplate(Map<String, Object> requestBody) throws Exception {
    String id = String.valueOf(requestBody.get("id"));
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, REPORT_TEMPLATE);
    baseService.update(id, requestBody);
    List<Map<String, Object>> columnList = (List<Map<String, Object>>) requestBody.get("columnSet");
    for (Map<String, Object> column : columnList) {
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, REPORT_TEMPLATE_COLUMN);
      if (column.containsKey("sortType") && !StringUtils.isEmpty(column.get("sortType"))) {
        column.put("sortType", String.valueOf(column.get("sortType")));
      }
      baseService.update(String.valueOf(column.get("id")), column);
    }
  }
}
