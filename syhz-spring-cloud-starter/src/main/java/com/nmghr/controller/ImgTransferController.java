/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.nmghr.controller;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import sun.misc.BASE64Decoder;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/**
 * <功能描述/> 附件的上传和下载(MongoDB存储和读取)
 *
 * @author brook
 * @date 2018年6月29日 下午1:47:05
 * @version 1.0
 */
@RestController
@RequestMapping("/imgFile")
public class ImgTransferController {
  private static final Logger log = LoggerFactory.getLogger(FileController.class);
  @Autowired
  private GridFsTemplate gridFsTemplate;
  @Autowired
  MongoDbFactory dbFactory;
  @Resource
  private ResourceLoader resourceLoader;
  @Autowired
  private IBaseService baseService;

  /**
   * 根据Id查base64图片
   *
   * @param response
   * @return
   */
  @ResponseBody
  @GetMapping(value = "/downloadImg/{id}")
  public void downloadFile(@PathVariable String id, HttpServletResponse response) {
    try {
      BASE64Decoder decoder = new BASE64Decoder();
      if (null != id && StringUtils.isEmpty(id)) {
        response.setContentType("application/json; charset=utf-8");
        response.getWriter().write("参数不能为空");
      }
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "YCIMGES");
      Map<String,Object> param = new HashMap<>();
      param.put("id",id);
      Map<String,Object> img = (Map<String, Object>) baseService.get(param);
      if(img!=null){
        //向前端返回图片
        String url = String.valueOf(img.get("url"));
        byte[] bytes = decoder.decodeBuffer(url);
        String fileName = String.valueOf(img.get("file_name"));
        //String contentType = String.valueOf(img.get("content_type"));
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
        response.getOutputStream().write(bytes);
      }

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

}
