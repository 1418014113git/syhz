/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nmghr.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.page.Paging;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.service.UserExtService;
import com.sargeraswang.util.ExcelUtil.ExcelUtil;

/**
 * <功能描述/>
 *
 * @author weber  
 * @date 2019年4月29日 下午2:46:54 
 * @version 1.0   
 */
@RestController
@RequestMapping("/syslog")
public class LogExcelController {
//  private static final Logger log = LoggerFactory.getLogger(LogExcelController.class);
  @Autowired
  @Qualifier("baseService")
  private IBaseService baseService;
  @Autowired
  private UserExtService userextService;
  
  @SuppressWarnings({"unchecked", "rawtypes"})
  @GetMapping("/list")
  @ResponseBody
  public Object list (@RequestParam Map<String, Object> params) throws Exception {
    int currentPage = 1;
    int pageSize = 10;
    if(params.get("pageNum")!=null) {
      currentPage = Integer.parseInt(String.valueOf(params.get("pageNum")));
    }
    if(params.get("pageSize")!=null) {
      pageSize = Integer.parseInt(String.valueOf(params.get("pageSize")));
    }
    List<Map<String, Object>> list = new ArrayList<>();
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "SYSLOGCOUNT");
    Paging obj = (Paging) baseService.page(params, currentPage, pageSize);
    if(obj!=null && obj.getList()!=null) {
      list = obj.getList();
    }
    if(list == null || list.size() == 0) {
      return new Paging(pageSize, currentPage, 0, new ArrayList<>());
    }
    Map<String, Object> result = new LinkedHashMap<String, Object>();
    List<Object> ids = new ArrayList<>();
    int i = 1;
    for(Map<String, Object> map : list) {
      if(map.get("userId")!=null) {
        ids.add(map.get("userId"));
        result.put(String.valueOf(map.get("userId")) + "#" + String.valueOf(map.get("id")), map);
        map.put("idx", i);
        i++;
      }
    }
    if(ids.size()==0) {
      return new Paging(pageSize, currentPage, 0, new ArrayList<>());
    }
    Map<String, Object> para = new HashMap<String, Object>();
    para.put("ids", ids);
    List<Map<String, Object>> infos = (List<Map<String, Object>>)userextService.list(para);
    if(infos==null || infos.size() == 0) {
      return new Paging(pageSize, currentPage, obj.getTotalCount(), list);
    }
    for(Map<String, Object> map : infos) {
      String id = String.valueOf(map.get("userId"));
      for(String key : result.keySet()) {
        if(id.equals(key.split("#")[0])) {
          Map<String, Object> rs = (Map<String, Object>)result.get(key);
          rs.put("idNum", map.get("uk"));
          rs.put("depName", map.get("depName"));
        }
      }
    }
    return new Paging(pageSize, currentPage, obj.getTotalCount(), new ArrayList<>(result.values()));
  }
  
  @GetMapping("/excel")
  @ResponseBody
  public void excel(@RequestParam Map<String, Object> params, HttpServletRequest request, HttpServletResponse response) throws Exception {
    List<Map<String, Object>> list= getResult(params);
    
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    Map<String, String> headersMap = new LinkedHashMap<String, String>();
    headersMap.put("idx", "序号");
    headersMap.put("depName", "单位及部门");
    headersMap.put("userName", "登录账号");
    headersMap.put("realName", "姓名");
    headersMap.put("idNum", "身份证号码");
    headersMap.put("ip", "操作IP");
    headersMap.put("num", "总查询次数");
    headersMap.put("czyy", "高频操作原因");
    headersMap.put("wgxy", "是否存在违规行为");


    String fileName = "用户操作检查表";
    
    ExcelUtil.exportExcel(headersMap, list, os, null);
    // 配置浏览器下载
    byte[] content = os.toByteArray();
    InputStream is = new ByteArrayInputStream(content);
    response.reset();
    response.setContentType("application/vnd.ms-excel;charset=utf-8");
    response.setHeader("Content-Disposition",
        "attachment;filename=" + new String((fileName + ".xlsx").getBytes(), "iso-8859-1"));
    ServletOutputStream out = response.getOutputStream();
    BufferedInputStream bis = null;
    BufferedOutputStream bos = null;
    try {
      bis = new BufferedInputStream(is);
      bos = new BufferedOutputStream(out);
      byte[] buff = new byte[2048];
      int bytesRead;
      while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
        bos.write(buff, 0, bytesRead);
      }
    } catch (final IOException e) {
      throw e;
    } finally {
      if (bis != null)
        bis.close();
      if (bos != null)
        bos.close();
    }
  }
  
  
  
  @SuppressWarnings({"unchecked", "rawtypes"})
  private List<Map<String, Object>> getResult(Map<String, Object> params) throws Exception {
    int currentPage = 1;
    int pageSize = 10;
    if(params.get("pageNum")!=null) {
      currentPage = Integer.parseInt(String.valueOf(params.get("pageNum")));
    }
    if(params.get("pageSize")!=null) {
      pageSize = Integer.parseInt(String.valueOf(params.get("pageSize")));
    }
    List<Map<String, Object>> list = new ArrayList<>();
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "SYSLOGCOUNT");
    Paging obj = (Paging) baseService.page(params, currentPage, pageSize);
    if(obj!=null && obj.getList()!=null) {
      list = obj.getList();
    }
    if(list == null || list.size() == 0) {
      return new ArrayList<>();
    }
    Map<String, Object> result = new LinkedHashMap<String, Object>();
    List<Object> ids = new ArrayList<>();
    int i = 1;
    for(Map<String, Object> map : list) {
      if(map.get("userId")!=null) {
        ids.add(map.get("userId"));
        result.put(String.valueOf(map.get("userId")) + "#" + String.valueOf(map.get("id")), map);
        map.put("idx", i);
        i++;
      }
    }
    if(ids.size()==0) {
      return new ArrayList<>();
    }
    Map<String, Object> para = new HashMap<String, Object>();
    para.put("ids", ids);
    List<Map<String, Object>> infos = (List<Map<String, Object>>)userextService.list(para);
    if(infos==null || infos.size() == 0) {
      return list;
    }
    for(Map<String, Object> map : infos) {
      String id = String.valueOf(map.get("userId"));
      for(String key : result.keySet()) {
        if(id.equals(key.split("#")[0])) {
          Map<String, Object> rs = (Map<String, Object>)result.get(key);
          rs.put("idNum", map.get("uk"));
          rs.put("depName", map.get("depName"));
        }
      }
    }
    return new ArrayList(result.values());
  }
  
  
  
}
