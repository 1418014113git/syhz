/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nmghr.hander.save;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractSaveHandler;
import com.nmghr.basic.core.util.ValidationUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <功能描述/>
 *
 * @author weber  
 * @date 2019年1月27日 下午6:55:31 
 * @version 1.0   
 */
@Service("sysnoticeSaveHandler")
public class SysNoticeSaveHandler extends AbstractSaveHandler {

  public SysNoticeSaveHandler(IBaseService baseService) {
    super(baseService);
  }

  @Override
  @Transactional
  public Object save(Map<String, Object> body) throws Exception {
    ValidationUtils.notNull(body.get("title"), "标题不能为空!");
    ValidationUtils.notNull(body.get("publishPersonId"), "发布人ID不能为空!");
    ValidationUtils.notNull(body.get("publishPersonName"), "发布人不能为空!");
    ValidationUtils.notNull(body.get("publishDate"), "发布时间不能为空!");
    ValidationUtils.notNull(body.get("startDate"), "公告开始时间不能为空!");
    ValidationUtils.notNull(body.get("endDate"), "公告结束时间不能为空!");

    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "SYSNOTICE");
     return baseService.save(body);
  }
}
