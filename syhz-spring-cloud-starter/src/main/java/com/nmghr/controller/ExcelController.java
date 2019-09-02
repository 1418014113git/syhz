/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.nmghr.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.nmghr.basic.common.Constant;
import com.nmghr.basic.common.Result;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.page.Paging;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.IQueryHandler;
import com.nmghr.basic.core.service.handler.ISaveHandler;
import com.nmghr.basic.core.util.SpringUtils;
import com.nmghr.controller.vo.BlackCompanyVO;
import com.nmghr.controller.vo.CompanyVO;
import com.nmghr.controller.vo.PersonVO;
import com.nmghr.controller.vo.QBXSVO;
import com.sargeraswang.util.ExcelUtil.ExcelUtil;
import com.nmghr.controller.vo.QBXSVO;

/**
 * <功能描述/>
 *
 * @author brook
 * @date 2018年8月7日 上午11:19:43
 * @version 1.0
 */
@RestController
@RequestMapping("/excel")
public class ExcelController {
  private static final Logger log = LoggerFactory.getLogger(ExcelController.class);
  @Autowired
  @Qualifier("baseService")
  private IBaseService baseService;

  @GetMapping(value = "/exporFile/{alias}")
  @ResponseBody
  public void importFile(@PathVariable String alias, @RequestParam Map<String, Object> requestParam,
      HttpServletRequest request, HttpServletResponse response) throws Exception {


    ByteArrayOutputStream os = new ByteArrayOutputStream();
    Map<String, String> headersMap = new LinkedHashMap<String, String>();
    Integer type = Integer.getInteger(String.valueOf(requestParam.get("type")));

    type = Integer.parseInt(request.getParameter("type"));

    Map<String, String[]> hm = request.getParameterMap();
    Iterator it = hm.keySet().iterator();

    Map<String, Object> param = new HashMap<String, Object>();
    while (it.hasNext()) {
      String key = it.next().toString();
      String[] values = (String[]) hm.get(key);
      if (values != null && values.length == 1) {
        param.put(key, values[0]);
      }
    }
    String fileName = "下载数据";
    if (type == 1) {
      fileName = "企业基础信息";
      headersMap.put("id", "企业主键");
      headersMap.put("DWMC", "企业名称");
      headersMap.put("FRDBXM", "法人名称");
      headersMap.put("YYZZBH", "机构营业执照编号");
      headersMap.put("ENABLE", "是否黑名单");
      headersMap.put("TYSHXYDM", "机构统一社会信用代码");
      headersMap.put("DWXZ_NAME", "单位性质");
      headersMap.put("DJZT", "机构登记状态");
      headersMap.put("DJDW_NAME", "机构登记单位");
      headersMap.put("FRDBZJHM", "机构法人代表证件号码");
      headersMap.put("ZCRQ", "机构注册日期");
      headersMap.put("MLXZ", "机构门楼详址");
    } else if (type == 2) {
      fileName = "情报线索";
      headersMap.put("XSXXBH", "线索编号");
      headersMap.put("BT", "线索标题");
      headersMap.put("XSXXLY_NAME", "线索来源");
      headersMap.put("SSLB_NAME", "线索分类");
      headersMap.put("JJCD_NAME", "线索紧急程度");
      headersMap.put("XSZT_NAME", "线索状态");
      headersMap.put("XXZW", "线索信息正文");
      headersMap.put("SSYY", "线索涉事诱因");
      headersMap.put("BGCX_NAME", "线索报告次序");
      headersMap.put("BXXS_NAME", "线索表现形式");
      headersMap.put("ASDDLB_NAME", "线索案(事)地点类别");
      headersMap.put("ASSJ", "线索案(事)时间");
      headersMap.put("TBR", "线索填报人");
      headersMap.put("CJR", "线索采集人");
      headersMap.put("SHR", "线索审核人");
    } else if (type == 3) {
      fileName = "人员信息";
      headersMap.put("id", "人员编号");
      headersMap.put("XM", "姓名");
      headersMap.put("XBNAME", "性别");
      headersMap.put("MZNAME", "民族");
      headersMap.put("HJDSSXQ", "籍贯");
      headersMap.put("HYZKNAME", "婚姻");
      headersMap.put("CSRQ", "出生日期");
      headersMap.put("ZJHM", "证件号码");
      headersMap.put("ZYLBNAME", "职业类别");
      headersMap.put("ZZXZ", "住址详址");
      headersMap.put("HJDXZ", "户籍地详址");
      headersMap.put("WHCDNAME", "文化程度");
      headersMap.put("LXDH", "联系方式");
    } else if (type == 4) {
      fileName = "业务日志";
      headersMap.put("user_name", "用户名");
      headersMap.put("nick_name", "姓名");
      headersMap.put("action", "请求方法");
      headersMap.put("ip_adress", "Ip地址");
      headersMap.put("args", "请求参数");
      headersMap.put("return_data", "返回数据");
      headersMap.put("create_date", "创建时间");
    }
    requestParam.remove("type");
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, alias.toUpperCase());
    alias = Constant.getHandlerBeanName(alias, Constant.OPERATOR_QUERY);
    List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
    if(type == 4) {
      int curPage = 1;
      int pageSize = 10;
      if (param.get("pageNum") != null) {
        curPage = Integer.parseInt(String.valueOf(param.get("pageNum")));
      }
      if (param.get("pageSize") != null) {
        pageSize = Integer.parseInt(String.valueOf(param.get("pageSize")));
      }
      Paging obj = (Paging) baseService.page(param, curPage, pageSize);
      if(obj!=null && obj.getList()!=null) {
        list = obj.getList();
        List<Map<String, Object>> newlist = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
          Map<String, Object> map = list.get(i);
          String data = String.valueOf(map.get("return_data"));
          String args = String.valueOf(map.get("args"));
          if(data.length()>1000) {
            data = data.substring(0, 1000);
          }
          if(args.length()>1000) {
            args = args.substring(0, 1000);
          }
          map.put("return_data", data);
          map.put("args", args);
          newlist.add(map);
        }
        list = newlist;
      }
    } else {
      list = (List<Map<String, Object>>) baseService.list(param);
      if (type == 2) {
        List<Map<String, Object>> newlist = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
          Map<String, Object> map = list.get(i);
          String xxzw = map.get("XXZW") + "";
          String xxzwStr = delHTMLTag(xxzw);
          map.put("XXZW", xxzwStr);
          newlist.add(map);
        }
        list = newlist;
      }
    }
    
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

  /**
   * @mathod Excel导入
   * @param mulFile 导入文件 前端导入文件名称为 file
   * @param type 导入文件类型 1 企业,2 情报线索 3 人员
   * 
   **/
  @PostMapping(value = "/uploadFile")
  @ResponseBody
  public Object uploadFile(@RequestParam("file") MultipartFile mulFile,
      @RequestParam("type") int type, HttpServletRequest request) {
    log.info("excel uploadFile file start {}{}", mulFile, type);
    Map<String, Object> result = new HashMap<String, Object>();
    try {
      if (null != mulFile && type > 0) {
        if (type == 1) {
          List<CompanyVO> list =
              (List<CompanyVO>) ExcelUtil.importExcel(CompanyVO.class, mulFile.getInputStream(), 0);

          if (!CollectionUtils.isEmpty(list))
            log.info("excel uploadFile file query size {}", list.size());
          initCompany(list);
        } else if (type == 2) {
          List<QBXSVO> list =
              (List<QBXSVO>) ExcelUtil.importExcel(QBXSVO.class, mulFile.getInputStream(), 0);

          if (!CollectionUtils.isEmpty(list))
            log.info("excel uploadFile file query size {}", list.size());
          initQBXSVO(list);
        } else if (type == 3) {
          List<PersonVO> list =
              (List<PersonVO>) ExcelUtil.importExcel(PersonVO.class, mulFile.getInputStream(), 0);
          if (!CollectionUtils.isEmpty(list))
            log.info("excel uploadFile file query size {}", list.size());
          initPerson(list);
        } else if (type == 4) {
          List<BlackCompanyVO> list =
              (List<BlackCompanyVO>) ExcelUtil.importExcel(BlackCompanyVO.class, mulFile.getInputStream(), 0);
          if (!CollectionUtils.isEmpty(list))
            log.info("excel uploadFile file query size {}", list.size());
          result.put("result", initBlackCompany(list));
        }
      }
      return Result.ok(result);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (Exception e) {
      // TODO Auto-generated catch block
      log.error("excel uploadFile error", e.getMessage());
      e.printStackTrace();
    }
    log.info("excel uploadFile file end");
    return Result.fail("999999", "导入异常");
  }


  public void initCompany(List<CompanyVO> list) throws Exception {
    Map<String, Object> map = null;
    for (CompanyVO companyVO : list) {
      map = new HashMap<String, Object>();
      map.put("DWMC", companyVO.getDwmc());
      map.put("TYSHXYDM", companyVO.getTyshxydm());
      map.put("DWXZ", "");
      map.put("DWXZNAME", companyVO.getDwxz());
      map.put("DJZT", companyVO.getDjzt());
      map.put("DJDW", companyVO.getDjdw());
      map.put("FRDBXM", companyVO.getFrdbxm());
      map.put("FRDBZJHM", companyVO.getFrdbzjhm());
      map.put("ZCRQ", companyVO.getZcrq());
      map.put("MLXZ", companyVO.getMlxz());
      map.put("XYLB", "");
      map.put("DWLB", "");
      map.put("JYFWZY", "");
      map.put("KYRQ", null);
      map.put("TYRQ", null);
      map.put("FZRQ", null);
      map.put("ZCZB", 0);
      map.put("SYH_FLLB", "");
      map.put("SYH_SJLDSJ", null);
      map.put("SYH_SJGXSJ", null);
      map.put("SYH_AVAILABLE_STATUS", null);
      map.put("SYH_SJLY", "");
      map.put("YYZZBH", "");
      map.put("SWDJH", "");
      map.put("ZZJGDM", "");
      map.put("ATTACHMENT", "");
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJCOMPANY");
      Object serverId = baseService.save(map);
    }

  }

  public void initPerson(List<PersonVO> list) throws Exception {
    Map<String, Object> map = null;

    for (PersonVO personVO : list) {
      map = new HashMap<String, Object>();
      map.put("XM", personVO.getXm());
      map.put("XBNAME", personVO.getXbname());
      map.put("MZNAME", personVO.getMzname());
      map.put("CSRQ", personVO.getCsrq());
      map.put("HJDSSXQ", personVO.getHjdssxq());
      map.put("ZJHM", personVO.getZjhm());
      map.put("HYZKNAME", personVO.getHyzkname());
      map.put("ZYLBNAME", personVO.getZylbname());
      map.put("ZZXZ", personVO.getZzxz());
      map.put("HJDXZ", personVO.getHjdxz());
      map.put("WHCDNAME", personVO.getWhcdname());
      map.put("LXDH", personVO.getLxdh());

      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJPERSON");
      Object serverId = baseService.save(map);
    }

  }

  public void initQBXSVO(List<QBXSVO> list) throws Exception {
    Map<String, Object> map = null;

    for (QBXSVO qbxsVO : list) {
      map = new HashMap<String, Object>();
      map.put("BT", qbxsVO.getBT());
      map.put("SSLB_NAME", qbxsVO.getSSLB_NAME());
      map.put("JJCD_NAME", qbxsVO.getJJCD_NAME());
      map.put("XXZW", qbxsVO.getXXZW());
      map.put("SSYY", qbxsVO.getSSYY());
      map.put("BGCX_NAME", qbxsVO.getBGCX_NAME());
      map.put("BXXS_NAME", qbxsVO.getBXXS_NAME());
      map.put("ASDDLB_NAME", qbxsVO.getASDDLB_NAME());
      map.put("ASSJ", qbxsVO.getASSJ());
      map.put("TBR", qbxsVO.getTBR());
      map.put("CJR", qbxsVO.getCJR());
      map.put("SHR", qbxsVO.getSHR());
      map.put("XSLX", qbxsVO.getXSLX());

      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "INTELL");
      Object serverId = baseService.save(map);
    }

  }
  
  public Object initBlackCompany(List<BlackCompanyVO> list) throws Exception {
    Map<String, Object> map = new HashMap<String, Object>();
    map.put("list", list);
    ISaveHandler saveHandler = SpringUtils.getBean("blackcompanySaveHandler", ISaveHandler.class);
    return saveHandler.save(map);
  }

  public static String delHTMLTag(String htmlStr) {
    String regEx_script = "<script[^>]*?>[\\s\\S]*?<\\/script>"; // 定义script的正则表达式
    String regEx_style = "<style[^>]*?>[\\s\\S]*?<\\/style>"; // 定义style的正则表达式
    String regEx_html = "<[^>]+>"; // 定义HTML标签的正则表达式

    Pattern p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
    Matcher m_script = p_script.matcher(htmlStr);
    htmlStr = m_script.replaceAll(""); // 过滤script标签

    Pattern p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
    Matcher m_style = p_style.matcher(htmlStr);
    htmlStr = m_style.replaceAll(""); // 过滤style标签

    Pattern p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
    Matcher m_html = p_html.matcher(htmlStr);
    htmlStr = m_html.replaceAll(""); // 过滤html标签

    return htmlStr.trim(); // 返回文本字符串
  }
}
