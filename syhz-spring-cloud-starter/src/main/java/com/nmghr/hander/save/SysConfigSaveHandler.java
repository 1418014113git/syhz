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

/**
 * <功能描述/>
 *
 * @author weber  
 * @date 2019年1月21日 下午5:18:01 
 * @version 1.0   
 */
@Service("savesysconfigSaveHandler")
public class SysConfigSaveHandler extends AbstractSaveHandler {

  public SysConfigSaveHandler(IBaseService baseService) {
    super(baseService);
  }
  @Override
  @Transactional
  public Object save(Map<String, Object> requestBody) throws Exception {
    // 查询案件档案配置
    Map<String, Object> params = new HashMap<String, Object>();
    params.put("configKey", requestBody.get("configKey"));
    params.put("category", requestBody.get("category"));
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "SYSCONFIG");
    List<Map<String, Object>> list = (List<Map<String, Object>>) baseService.list(params);
    if(list!=null && list.size()>0) {
      Map<String, Object> map = list.get(0);
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "SYSCONFIG");//统一防止LocalThread被修改
      return baseService.update(String.valueOf(map.get("id")), requestBody);
    }
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "SYSCONFIG");//统一防止LocalThread被修改
    return baseService.save(requestBody);
  }
}
