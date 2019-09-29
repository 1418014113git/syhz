/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nmghr.service;

import com.nmghr.basic.common.Result;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.rmdb.datasource.TargetDataSource;
import com.nmghr.hander.mapper.UserDeptMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

/**
 * <功能描述/>
 *
 * @author weber  
 * @date 2019年4月29日 下午3:19:43 
 * @version 1.0   
 */
@Service("userdeptService")
public class UserDeptService implements IBaseService {

  @Autowired
  @Resource
  UserDeptMapper userDeptMapper;

  @Override
  public Object save(Map<String, Object> requestMap) throws Exception {
    return null;
  }

  @Override
  public Object update(String id, Map<String, Object> requestBody) throws Exception {
    return null;
  }

  @Override
  public Object update(Map<String, Object> requestParam, Map<String, Object> requestBody)
      throws Exception {
    return null;
  }

  @Override
  public void remove(Map<String, Object> requestMap) throws Exception {

  }

  @Override
  public void remove(String id) throws Exception {
  }


  @Override
  public Object get(String id) throws Exception {

    return null;
  }

  @SuppressWarnings("unchecked")

  @Override
  @TargetDataSource(value="hrupms")
  public Object list(Map<String, Object> map) throws Exception {
      return userDeptMapper.getCitys();
  }

  @Override
  @TargetDataSource(value="hrupms")
  public Object page(Map<String, Object> requestMap, int currentPage, int pageSize)
      throws Exception {
    if (requestMap.get("cityId") == null) {
      return Result.fail("880088", "父级部门Id不能为空");
    }
    return userDeptMapper.getCityChild(String.valueOf(requestMap.get("cityId")));
  }
  @Override
  @TargetDataSource(value="hrupms")
  public Object get(Map<String, Object> requestMap) throws Exception {
    if (requestMap.get("depts") == null) {
      return Result.fail("880088", "开放部门不能为空");
    }
    Map<String, Object> totalNumMap = userDeptMapper.getTotalNum(String.valueOf(requestMap.get("depts")));
    if (totalNumMap != null && totalNumMap.get("totalNum") != null) {
      String totalNum = String.valueOf(totalNumMap.get("totalNum"));
      return totalNum;
    }
    else
      return null;
  }

  @Override
  public Object findAll() throws Exception {
    return null;
  }


  public Object findAll(String deptId) throws Exception {
   return  null;
  }

  @Override
  public Object getSequence(String seqName) throws Exception {
    return null;
  }

}
