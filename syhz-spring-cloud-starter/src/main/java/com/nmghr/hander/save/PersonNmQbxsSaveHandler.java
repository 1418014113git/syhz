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
import java.util.Iterator;
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
 * 给线索绑定人员ID
 *
 * @author weber  
 * @date 2019年1月16日 下午7:39:30 
 * @version 1.0   
 */
@Service("personnmqbxsSaveHandler")
public class PersonNmQbxsSaveHandler extends AbstractSaveHandler {

  public PersonNmQbxsSaveHandler(IBaseService baseService) {
    super(baseService);
  }

  @Override
  @Transactional
  public Object save(Map<String, Object> body) throws Exception {
    ValidationUtils.notNull(body.get("qbxsJbxxId"), "请添加线索!");
    
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "PERSONNM");
    Object id = savePerson(body);
    
    Map<String, Object> params = new HashMap<String, Object>();
    params.put("qbxsJbxxId", body.get("qbxsJbxxId"));
    params.put("personId", id);
    params.put("zjhm", body.get("gmsfhm"));
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "PERSONNMQBXS");
    List<Map<String, Object>> list = (List<Map<String, Object>>) baseService.list(params);
    if (list==null || list.size()==0) {
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "PERSONNMQBXS");//统一防止Local被重改
      baseService.save(params);
    } else {
      Map<String, Object> map = list.get(0);
      if (!map.get("zjhm").equals(body.get("gmsfhm"))) {
        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "PERSONNMQBXS");//统一防止Local被重改
        baseService.update(String.valueOf(map.get("id")), params);
      }
    }
    return id;
  }
  
  /**
   * 如果身份证存在就修改
   * @param body
   * @return
   * @throws Exception
   */
  private Object savePerson(Map<String, Object> body) throws Exception {
    Map<String, Object> params = new HashMap<String, Object>();
    params.put("gmsfhm", body.get("gmsfhm"));
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "PERSONNM");
    List<Map<String, Object>> list = (List<Map<String, Object>>) baseService.list(params);
    if (list == null || list.size() == 0) {
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "PERSONNM");//统一方式LocalThread被修改
      return baseService.save(body);//添加
    } else {
      Map<String, Object> map = list.get(0);//修改
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "PERSONNM");//统一方式LocalThread被修改
      baseService.update(String.valueOf(map.get("id")), body);
      return map.get("id");
    }
  }
  
}
