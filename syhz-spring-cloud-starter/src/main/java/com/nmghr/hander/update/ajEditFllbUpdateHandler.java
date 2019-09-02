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
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.common.exception.GlobalErrorException;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractUpdateHandler;

/**
 * 修改案件分类
 *
 * @author weber  
 * @date 2019年6月5日 下午4:15:35 
 * @version 1.0   
 */
@Service("ajeditfllbUpdateHandler")
public class ajEditFllbUpdateHandler extends AbstractUpdateHandler {

  public ajEditFllbUpdateHandler(IBaseService baseService) {
    super(baseService);
  }

  @Override
  @Transactional
  public Object update(String id, Map<String, Object> requestBody) throws Exception {
    String ajbh = (String) requestBody.get("ajbh");
    if (ajbh == null || "".equals(ajbh)) {
      throw new GlobalErrorException("99950", "案件编号不能为空");
    }
    //修改syh 分类
    Map<String, Object> syh = new HashMap<String, Object>();
    syh.put("fllb", requestBody.get("fllb"));
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJSYH");
    baseService.update(String.valueOf(requestBody.get("ajbh")), syh);

    //修改etl 分类
    Map<String, Object> etl = new HashMap<String, Object>();
    etl.put("fllb", requestBody.get("fllb"));
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJJBXXETLRL");
    return baseService.update(String.valueOf(requestBody.get("ajbh")), etl);
  }
}
