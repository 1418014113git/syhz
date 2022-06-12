/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.nmghr.hander.save;

import java.util.Map;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractSaveHandler;
import com.nmghr.util.NoticeQueueThread;

/**
 * <功能描述/>
 *
 * @author weber
 * @date 2018年7月26日 下午3:46:31
 * @version 1.0
 */
@Service("tztgSaveHandler")
public class TztgSaveHandler extends AbstractSaveHandler {

  public TztgSaveHandler(IBaseService baseService) {
    super(baseService);
  }

  /**
   * 增加通知通告
   */
  @Override
  @Transactional
  public Object save(Map<String, Object> requestBody) throws Exception {
    // 添加主表
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "TZTG");
    Object obj = baseService.save(requestBody);

    List<Map<String, Object>> deptIds = (List<Map<String, Object>>) requestBody.get("deptIds");
    if (deptIds != null && deptIds.size() > 0) {
      // 添加签收表
      List<Object> ids = new ArrayList<Object>();
      for (Map<String, Object> map : deptIds) {
        if (map != null) {
          requestBody.put("noticeId", obj);
          requestBody.put("noticeOrgId", map.get("id"));
          requestBody.put("noticeOrgName", map.get("name"));
          LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "TZTGSIGN");
          baseService.save(requestBody);
          ids.add(map.get("id"));
        }
      }
      Map<String, Object> paras = new HashMap<String, Object>();
      paras.put("ids", ids);
      paras.put("bizId", obj);
      paras.put("type", "NOTICE");
      NoticeQueueThread.add(paras);
    }
    return obj;
  }
}
