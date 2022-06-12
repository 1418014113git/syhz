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

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.common.Result;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.IQueryHandler;
import com.nmghr.basic.core.service.handler.ISaveHandler;
import com.nmghr.basic.core.service.handler.IUpdateHandler;
import com.nmghr.basic.core.util.SpringUtils;

/**
 * <功能描述/>
 *
 * @author brook  
 * @date 2018年7月25日 上午10:29:38 
 * @version 1.0   
 */
@RestController
@RequestMapping("/timeLine")
public class TimeLineController {
  
  private static final Logger log = LoggerFactory.getLogger(TimeLineController.class);
  @Autowired
  @Qualifier("baseService")
  private IBaseService baseService;
  
  @PutMapping("/{alias}")
  @ResponseBody
  public Object save(@PathVariable String alias, @RequestBody Map<String, Object> requestBody,
      @RequestHeader Map<String, Object> requestHeader) throws Exception {
     requestBody.put("TimeLineAlias", alias.toUpperCase());
     ISaveHandler saveHandler = SpringUtils.getBean("timelineSaveHandler", ISaveHandler.class);
     Object object = saveHandler.save(requestBody);
    return Result.ok(object);
  }
  @PostMapping("/{alias}/{id}")
  @ResponseBody
  public Object update(@PathVariable String alias, @PathVariable String id, @RequestBody Map<String, Object> requestBody,
		  @RequestHeader Map<String, Object> requestHeader) throws Exception {
	  requestBody.put("TimeLineAlias", alias.toUpperCase());
	  IUpdateHandler updateHandler = SpringUtils.getBean("timelineUpdateHandler", IUpdateHandler.class);
	  Object object = updateHandler.update(id, requestBody);
	  return Result.ok(object);
  }

}
