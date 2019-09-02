package com.nmghr.controller;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.util.WordUtils;

@RestController
public class AuthenticateController {
  private static final Logger log = LoggerFactory.getLogger(AuthenticateController.class);
  @Autowired
  @Qualifier("baseService")
  private IBaseService baseService;
  @Resource
  private ResourceLoader resourceLoader;
  @Value("${file.path}")
  private String filePath;
  
  @SuppressWarnings({"unchecked", "rawtypes"})
  @GetMapping(value = "/authenticate/doc/{id}")
  @ResponseBody
  public void exportFile(@PathVariable String id, @RequestParam Map<String, Object> requestParam,
      HttpServletRequest request, HttpServletResponse response) throws Exception {
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AUTHENTICATE");
    Object obj = baseService.get(id);

    Map<String, String> data = new HashMap<String, String>();
    Map<String, Object> authenticate = (Map<String, Object>) obj;
    Set<String> keys = authenticate.keySet();
    for (Iterator iterator = keys.iterator(); iterator.hasNext();) {
      String key = (String) iterator.next();
      data.put(key, authenticate.get(key).toString());
    }
    if(data.get("updatePerson")==null) {
      data.put("updatePerson", " ");
    }
    data.put("copyPhone", data.get("copyPersonPhone"));
    data.put("expiryDate", data.get("dayNum"));
    this.exportWord(response, request, data);
  }

  public void exportWord(HttpServletResponse response, HttpServletRequest request,
      Map<String, String> data) throws FileNotFoundException {
    log.info("import Word method start");
    String filePath = this.filePath;// demo.docx 检验鉴定委托书.docx
 /*   BufferedInputStream bis = null;
    BufferedOutputStream bos = null;*/

    log.info("file filePath value {}", filePath);

    /*org.springframework.core.io.Resource resource =
        resourceLoader.getResource("classpath:" + filePath);*/

    
    InputStream inStream = new FileInputStream(filePath);// 文件的存放路径
    try {
      // InputStream inputStrean =
      // org.springframework.util.ClassUtils.class.getClassLoader().getResourceAsStream("template/检验鉴定委托书.docx");
      // InputStream inputStrean = new FileInputStream(this.filePath);
      // 配置浏览器下载相关配置
      response.reset();
      response.setContentType("application/msword;charset=utf-8");
      response.setHeader("Content-Length", String.valueOf(inStream.available()));
      response.setHeader("Content-Disposition",
          "attachment;filename=" + new String("检验鉴定报告.docx".getBytes(), "iso-8859-1"));

      WordUtils.createWord(inStream, response, data, null);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    log.info("import Word method end");
  }

/*public static void main(String[] args) throws FileNotFoundException {
  InputStream inStream = new FileInputStream("D://111//检验鉴定委托书.docx");// 文件的存放路径
  System.out.println(inStream.toString());
}*/

}
