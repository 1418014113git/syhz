/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.nmghr.hander.save;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.common.exception.GlobalErrorException;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractSaveHandler;

/**
 * <功能描述/>
 *
 * @author weber
 * @date 2018年7月26日 下午3:46:31
 * @version 1.0
 */
@Service("ajzdgxSaveHandler")
public class AJZDGXSaveHandler extends AbstractSaveHandler {

  public AJZDGXSaveHandler(IBaseService baseService) {
    super(baseService);
  }

  @SuppressWarnings("unchecked")
  @Override
  @Transactional
  public Object save(Map<String, Object> requestBody) throws Exception {
    if (requestBody.get("noticeType") == null) {
      throw new GlobalErrorException("998001", "请输入类型!");
    }
    if (requestBody.get("id") == null) {
      throw new GlobalErrorException("998001", "请输入id!");
    }
    String id = String.valueOf(requestBody.get("id"));
    Map<String, Object> ps = new HashMap<String, Object>();
    ps.put("status", "3");
    ps.put("noticeOrgId", requestBody.get("noticeOrgId"));
    ps.put("ajbh", requestBody.get("ajbh"));
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "CHECKAJSIGN");
    List<Map<String, Object>> list = (List<Map<String, Object>>) baseService.list(ps);
    if (list != null && list.size() > 0) {
       return 1;
    }

    // 签收表添加
    requestBody.put("noticeTime", new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));// 通知时间
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "BUSINESSSIGN");
    baseService.save(requestBody);

    // 当前签收状态
    Map<String, Object> param = new HashMap<String, Object>();
    param.put("status", "9");// 9:指定管辖
    param.put("signUserId", requestBody.get("userId"));
    param.put("signUserName", requestBody.get("userName"));
    param.put("signTime", new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
    param.put("updateUserId", requestBody.get("userId"));
    if (requestBody.get("revokeReason") != null) {
      param.put("revokeReason", requestBody.get("revokeReason"));
    }
    param.put("noticeLx", requestBody.get("noticeType"));
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "BUSINESSSIGN");// 防止添加日志LocalThread 重写
    return baseService.update(id, param);
  }

}
