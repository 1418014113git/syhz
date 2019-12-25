/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.nmghr.controller;

import com.alibaba.fastjson.JSONObject;
import com.nmghr.basic.common.Constant;
import com.nmghr.basic.common.Result;
import com.nmghr.basic.common.exception.GlobalErrorException;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.ISaveHandler;
import com.nmghr.basic.core.util.SpringUtils;
import com.nmghr.controller.vo.PitchManVO;
import com.sargeraswang.util.ExcelUtil.ExcelUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 摊贩信息导入
 *
 * @author weber
 * @date 2019年5月6日 下午2:10:40
 * @version 1.0
 */
@RestController
@SuppressWarnings("all")
@RequestMapping("/yqreport")
public class YqReportController {
  private static final Logger log = LoggerFactory.getLogger(YqReportController.class);
  @Autowired
  private IBaseService baseService;

  @RequestMapping("/total")
  public Map<String,Object> getTotal() throws Exception {
    Map<String,Object> totalMap = new HashMap<>();
    totalMap.put("day",0);
    totalMap.put("week",0);
    totalMap.put("month",0);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "YQREPORTTOTAL");
   List<Map<String,Object>> result = (List<Map<String, Object>>) baseService.list(null);
    for (Map<String, Object> map : result) {
      if("1".equals(String.valueOf(map.get("category")))){
        totalMap.put("day",map.get("total"));
      }
      if("2".equals(String.valueOf(map.get("category")))){
        totalMap.put("week",map.get("total"));
      }
      if("3".equals(String.valueOf(map.get("category")))){
        totalMap.put("month",map.get("total"));
      }

    }

return totalMap;
  }




}
