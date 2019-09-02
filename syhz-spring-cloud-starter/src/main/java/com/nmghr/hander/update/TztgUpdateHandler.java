/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.nmghr.hander.update;

import java.util.Map;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractUpdateHandler;

/**
 * <功能描述/>
 *
 * @author weber
 * @date 2018年7月26日 下午3:46:31
 * @version 1.0
 */
@Service("tztgUpdateHandler")
public class TztgUpdateHandler extends AbstractUpdateHandler {

  public TztgUpdateHandler(IBaseService baseService) {
    super(baseService);
  }

  @SuppressWarnings("unchecked")
  @Override
  @Transactional
  public Object update(String id, Map<String, Object> requestBody) throws Exception {
    // 修改主体表
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "TZTG");
    Object obj = baseService.update(id, requestBody);
    // 删除签收数据
    Map<String, Object> delParames = new HashMap<String, Object>();
    delParames.put("noticeId", id);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "TZTGSIGN");
    baseService.remove(delParames);
    // 新增签收数据
    List<Map<String, Object>> deptIds = (List<Map<String, Object>>) requestBody.get("deptIds");
    if (deptIds != null && deptIds.size() > 0) {
      for (Map<String, Object> map : deptIds) {
        if (map != null) {
          requestBody.put("noticeId", id);
          requestBody.put("noticeOrgId", map.get("id"));
          requestBody.put("noticeOrgName", map.get("name"));
          LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "TZTGSIGN");
          baseService.save(requestBody);
        }
      }
    }
    return obj;
  }

}
