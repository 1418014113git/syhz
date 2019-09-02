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
import java.io.InputStream;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
/**
 * <功能描述/>
 *
 * @author brook
 * @date 2018年8月7日 上午11:19:43
 * @version 1.0
 */
@RestController
@RequestMapping("/file")
public class FileController {
  private static final Logger log = LoggerFactory.getLogger(ExcelController.class);
  @Resource
  private ResourceLoader resourceLoader;

  @GetMapping(value = "/attachment/file/{id}")
  @ResponseBody
public void downloadTemplate(@PathVariable String id, HttpServletResponse response,
    HttpServletRequest request) {
  InputStream inputStream = null;
  ServletOutputStream servletOutputStream = null;
  try {
    String fileName = "";
    String path = "";
    if (null != id) {
      if ("1".equals(id)) {
        path = "template/谷歌and插件and安装插件说明.zip";
        fileName = "谷歌and插件and安装插件说明.zip";
      } else if ("2".equals(id)) {
        path = "template/用户使用手册_[公安食药环侦实战应用平台建设].doc";
        fileName = "用户使用手册_[公安食药环侦实战应用平台建设].doc";
      }
      org.springframework.core.io.Resource resource =
          resourceLoader.getResource("classpath:" + path);
      response.setContentType("application/vnd.ms-excel");
      response.addHeader("Cache-Control", "no-cache, no-store, must-revalidate");
      response.addHeader("charset", "utf-8");
      response.addHeader("Pragma", "no-cache");
      response.addHeader("Content-Length", String.valueOf(resource.getInputStream().available()));
      response.setHeader("Content-Disposition",
          "attachment;filename=" + new String(fileName.getBytes(), "iso-8859-1"));
      inputStream = resource.getInputStream();
      servletOutputStream = response.getOutputStream();
      IOUtils.copy(inputStream, servletOutputStream);
      response.flushBuffer();
    }

  } catch (Exception e) {
    e.printStackTrace();
  } finally {
    try {
      if (servletOutputStream != null) {
        servletOutputStream.close();
        servletOutputStream = null;
      }
      if (inputStream != null) {
        inputStream.close();
        inputStream = null;
      }
      // 召唤jvm的垃圾回收器
      System.gc();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
}
