/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.nmghr.controller;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;


/**
 * <功能描述/> 附件的上传和下载(MongoDB存储和读取)
 * 
 * @author brook
 * @date 2018年6月29日 下午1:47:05
 * @version 1.0
 */
@RestController
@RequestMapping("/file")
public class MongoFileController {
  private static final Logger log = LoggerFactory.getLogger(FileController.class);
  @Autowired
  private GridFsTemplate gridFsTemplate;
  @Autowired
  MongoDbFactory dbFactory;
  @Resource
  private ResourceLoader resourceLoader;

  /**
   * 从MongoDB下载对应文件编号的文件
   * 
   * @param response
   * @return
   */
  @ResponseBody
  @GetMapping(value = "/downloadFile/{url}")
  public void downloadFile(@PathVariable String url, HttpServletResponse response) {
    try {
      if (null != url && StringUtils.isEmpty(url)) {
        response.setContentType("application/json; charset=utf-8");
        response.getWriter().write("参数不能为空");
      }

      //
      Query query = Query.query(Criteria.where("_id").is(url));
      GridFSFile gfsfile = gridFsTemplate.findOne(query);
      String fileName = gfsfile.getFilename();

      // 创建mongoClient对象，创建一个文件管理类，根据id获取该文件的管理者
      GridFSBucket bucket = GridFSBuckets.create(dbFactory.getDb());

      if (bucket == null) {
        response.setContentType("application/json; charset=utf-8");
        response.getWriter().write("文件已经不存在啦！");
      }
      if (fileName.contains(".jpg") || fileName.contains(".jpeg")) {
        response.setContentType("image/jpeg; charset=utf-8");
      } else if (fileName.contains(".png")) {
        response.setContentType("image/png; charset=utf-8");
      } else if (fileName.contains(".pdf")) {
        response.setContentType("application/pdf; charset=utf-8");
      } else if (fileName.contains(".docx") || fileName.contains(".doc")) {
        response.setContentType("application/msword; charset=utf-8");
      } else if (fileName.contains(".txt")) {
        response.setContentType("text/plain; charset=utf-8");
      } else if (fileName.contains(".xml")) {
        response.setContentType("text/xml; charset=utf-8");
      }else if (fileName.contains(".zip")) {
        response.setContentType("application/zip; charset=utf-8");
      } else{
        response.setContentType("application/octet-stream; charset=utf-8");
      }
      // 下载此文件
      bucket.downloadToStream(new ObjectId(url), response.getOutputStream());
    } catch (Exception e) {
      // TODO: handle exception
      try {
        log.error("downloadFile error", e.getMessage());
        //response.setContentType("application/json; charset=utf-8");
        response.getWriter().write("文件读取异常");
      } catch (IOException e1) {
        // TODO Auto-generated catch block
        e1.printStackTrace();
      }
    }

  }


  @GetMapping("/downloadTemplate/{type}")
  public void downloadTemplate(@PathVariable Integer type, HttpServletResponse response,
                               HttpServletRequest request) {
    log.info("file downloadTemplate method start type {}", type);
    String filePath = null;
    BufferedInputStream bis = null;
    BufferedOutputStream bos = null;

    try {
      if (null != type) {
        ServletOutputStream out = response.getOutputStream();
        filePath = getFilePath(type);

        log.info("file filePath value {}", filePath);

        org.springframework.core.io.Resource resource =
            resourceLoader.getResource("classpath:" + filePath);
        // 配置浏览器下载相关配置
        response.reset();
        response.setContentType(
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet.sheet;charset=utf-8");
        response.setHeader("Content-Length", String.valueOf(resource.getInputStream().available()));
        response.setHeader("Content-Disposition",
            "attachment;filename=" + new String(resource.getFilename().getBytes(), "iso-8859-1"));
        try {
          bis = new BufferedInputStream(resource.getInputStream());
          bos = new BufferedOutputStream(out);
          byte[] buff = new byte[2048];
          int bytesRead;
          while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
            bos.write(buff, 0, bytesRead);
          }
        } catch (IOException e) {
          log.error("downloadTemplate io error", e.getMessage());
          e.getStackTrace();
        } finally {
          if (bis != null)
            bis.close();
          if (bos != null)
            bos.close();
        }
      }
      log.info("file downloadTemplate method end");
    } catch (IOException e) {
      log.error("downloadTemplate io error", e.getMessage());
      e.getStackTrace();
    }
  }

  // 根据类型获取到需要下载的模板文件
  public String getFilePath(int type) {
    String path = null;
    switch (type) {
      case 1:
        path = "template/企业基础信息导入模板.xlsx";
        break;
      case 2:
        path = "template/情报线索导入模板.xlsx";
        break;
      case 3:
        path = "template/个人信息导入模板.xlsx";
        break;
      default:
        path = "template/企业基础信息导入模板.xlsx";
        break;
    }
    return path;
  }


  private void saveFile(String path, InputStream inputStream, String fileName) {

    OutputStream os = null;
    try {
      // 2、保存到临时文件
      // 1K的数据缓冲
      byte[] bs = new byte[10240];
      // 读取到的数据长度
      int len;
      // 输出的文件流保存到本地文件
      File tempFile = new File(path);
      if (!tempFile.exists()) {
        tempFile.mkdirs();
      }
      os = new FileOutputStream(tempFile.getPath() + File.separator + fileName);
      // 开始读取
      while ((len = inputStream.read(bs)) != -1) {
        os.write(bs, 0, len);
      }

    } catch (IOException e) {
      e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      // 完毕，关闭所有链接
      try {
        os.close();
        inputStream.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

}
