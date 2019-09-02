/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nmghr.hander.query;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.rmdb.datasource.TargetDataSource;

/**
 * <功能描述/>
 *
 * @author brook  
 * @date 2018年9月14日 下午4:03:37 
 * @version 1.0   
 */
@Service("depService")
public class DepService implements IBaseService {

  @Autowired
  IBaseService baseService;
  

 

  @Override
  public Object save(Map<String, Object> requestMap) throws Exception {
 // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Object update(String id, Map<String, Object> requestBody) throws Exception {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Object update(Map<String, Object> requestParam, Map<String, Object> requestBody)
      throws Exception {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void remove(Map<String, Object> requestMap) throws Exception {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void remove(String id) throws Exception {
    // TODO Auto-generated method stub
    
  }

  @Override
  @TargetDataSource(value="hrupms")
  public Object get(Map<String, Object> requestMap) throws Exception {
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "DEPNAME");
    Object obj = baseService.get(requestMap);
    return obj;
  }

  @Override
  @TargetDataSource(value="hrupms")
  public Object get(String id) throws Exception {
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "DEPQUERY");
    Object obj = baseService.get(id);
    return obj;
  }

  @Override
  @TargetDataSource(value="hrupms")
  public Object list(Map<String, Object> requestMap) throws Exception {
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "DEPIDS");
    Object obj = baseService.list(requestMap);
    return obj;
  }

  @Override
  public Object page(Map<String, Object> requestMap, int currentPage, int pageSize)
      throws Exception {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Object findAll() throws Exception {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Object getSequence(String seqName) throws Exception {
    // TODO Auto-generated method stub
    return null;
  }
}
