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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.nmghr.basic.common.Constant;
import com.nmghr.basic.common.exception.GlobalErrorException;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractSaveHandler;
import com.nmghr.controller.vo.BlackCompanyVO;

/**
 * <功能描述/>
 *
 * @author weber  
 * @date 2019年1月27日 上午10:32:14 
 * @version 1.0   
 */
@Service("blackcompanySaveHandler")
public class BlackCompanySaveHandler extends AbstractSaveHandler {

  public BlackCompanySaveHandler(IBaseService baseService) {
    super(baseService);
  }
  
  @SuppressWarnings("unchecked")
  @Override
  @Transactional
  public Object save(Map<String, Object> requestBody) throws Exception {
    if (requestBody.get("list") == null) {
      return null;
    }
    List<BlackCompanyVO> list = (List<BlackCompanyVO>) requestBody.get("list");
    if (list == null) {
      return null;
    }
    for (BlackCompanyVO blackVO : list) {
      if (blackVO.getCompanyId()==null) {
        throw new GlobalErrorException("999103", "企业id有为空数据,请检查后重新导入!");
      }
    }
    List<String> names = new ArrayList<String>();
    for (BlackCompanyVO blackVO : list) {
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "BLACKLIST");
      Object obj = baseService.get(String.valueOf(blackVO.getCompanyId()));
      if(obj==null) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("companyId", blackVO.getCompanyId());
        map.put("dwmc", blackVO.getDwmc());
        map.put("blacklistReason", blackVO.getBlacklistReason());
        map.put("unlawAct", blackVO.getUnlawAct());
        map.put("according", blackVO.getAccording());
        map.put("accordingMessage", blackVO.getAccordingMessage());
        map.put("relevanceSystem", blackVO.getRelevanceSystem());
        map.put("approveDept", "");
        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "BLACKLIST");
        baseService.save(map);
        names.add(blackVO.getDwmc());
      }
    }
    return names;
  }

}
