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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractSaveHandler;
import com.nmghr.basic.core.util.ValidationUtils;

/**
 * <功能描述/>
 *
 * @author weber  
 * @date 2019年1月27日 下午6:55:31 
 * @version 1.0   
 */
@Service("trestrictedpersonSaveHandler")
public class TrestrictedPersonSaveHandler extends AbstractSaveHandler {

  public TrestrictedPersonSaveHandler(IBaseService baseService) {
    super(baseService);
  }

  @Override
  @Transactional
  public Object save(Map<String, Object> body) throws Exception {
    ValidationUtils.notNull(body.get("userId"), "请添加用户!");
    ValidationUtils.notNull(body.get("userName"), "请输入用户名!");
    ValidationUtils.notNull(body.get("realName"), "请输入姓名!");
    Object obj = null;
    Map<String, Object> params = new HashMap<String, Object>();
    params.put("userId", body.get("userId"));
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "TRESTRICTEDPER");
    List<Map<String, Object>> list = (List<Map<String, Object>>) baseService.list(params);
    if(list == null || list.size() == 0) {
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "TRESTRICTEDPER");//统一防止LocalThread 被修改
      obj = baseService.save(body);
    }
    return obj;
  }
  
}
