/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nmghr.hander.update;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractUpdateHandler;
import com.nmghr.basic.core.util.ValidationUtils;

/**
 * 人员情报线索关联修改
 *
 * @author weber  
 * @date 2019年1月16日 下午7:49:30 
 * @version 1.0   
 */
@Service("personnmqbxsUpdateHandler")
public class PersonNmQbxsUpdateHandler extends AbstractUpdateHandler {

  public PersonNmQbxsUpdateHandler(IBaseService baseService) {
    super(baseService);
  }

  @Override
  @Transactional
  public Object update(String id, Map<String, Object> body) throws Exception {
    ValidationUtils.notNull(id, "参数输入错误!");
    ValidationUtils.notNull(body.get("qbxsJbxxId"), "参数输入错误!");
    //查询线索关联
    Map<String, Object> params = new HashMap<String, Object>();
    params.put("qbxsJbxxId", body.get("qbxsJbxxId"));
    params.put("personId", id);
    params.put("zjhm", body.get("gmsfhm"));
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "PERSONNMQBXS");
    List<Map<String, Object>> list = (List<Map<String, Object>>) baseService.list(params);
    if (list==null || list.size()==0) {
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "PERSONNMQBXS");//统一防止LocalThread被修改
      baseService.save(params); // 无关联添加关联
    } else {
      Map<String, Object> map = list.get(0);
      if (!map.get("zjhm").equals(body.get("gmsfhm"))) {  //有关联身份证号码不同， 修改为最新身份证
        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "PERSONNMQBXS");//统一防止LocalThread被修改
        baseService.update(String.valueOf(map.get("id")), params);
      }
    }
    //修改人员信息
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "PERSONNM");
    return baseService.update(id, body);
  }
  
  
}
