/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.nmghr.hander.save;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.common.Result;
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
@Service("ajrlSaveHandler")
public class AJRLSaveHandler extends AbstractSaveHandler {

  private Logger log = LoggerFactory.getLogger(AJRLSaveHandler.class);

  public AJRLSaveHandler(IBaseService baseService) {
    super(baseService);
  }

  @SuppressWarnings("unchecked")
  @Override
  @Transactional
  public Object save(Map<String, Object> requestBody) {
    if (requestBody.get("id") == null ||"".equals(requestBody.get("id"))) {
      log.error("请求参数异常,id不存在!");
      throw new GlobalErrorException("999011", "请求参数异常,id不存在!");
    }
    if (requestBody.get("fllb") == null) {
      log.error("请选择案件类型!");
      throw new GlobalErrorException("999012", "请选择案件类型!");
    }
    if (requestBody.get("AJBH") == null) {
      log.error("案件编号不能为空!");
      throw new GlobalErrorException("999012", "案件编号不能为空!");
    }
    String id = String.valueOf(requestBody.get("id"));
    try {
   // 检查是否已存在
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJJBXXSYH");
      Map<String, Object> exist =
          (Map<String, Object>) baseService.get(String.valueOf(requestBody.get("AJBH")));
      if (exist != null) {
        log.info("案件编号已经存在：{}!", requestBody.get("AJBH"));
        return Result.fail("999012", "该案件已经被认领!");
      }

      // 认领
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJRL");
      baseService.save(requestBody);
      // 修改签收表为已认领
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJJBXXSYH");
      baseService.update(id, requestBody);

      // 修改etl的分类
      Map<String, Object> etl = new HashMap<String, Object>();
      etl.put("fllb", requestBody.get("fllb"));
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJJBXXETLRL");
      baseService.update(String.valueOf(requestBody.get("AJBH")), etl);

      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJJBXXSYH");
      Map<String, Object> map =
          (Map<String, Object>) baseService.get(String.valueOf(requestBody.get("AJBH")));
      if (map == null) {
        throw new Exception("新增认领异常");
      }
      // 推送厅支队消息 2019年之后推送数据
      int status = 1;
      if (map.get("LARQ") == null || "".equals(String.valueOf(map.get("LARQ")))) {
        status = 2;
      }
      if (map.get("LARQ") != null) {
        String larq = String.valueOf(map.get("LARQ"));
        if (larq.length() == 8 && Pattern.compile("^\\d+$").matcher(larq.substring(0, 4)).matches() && Integer.parseInt(larq.substring(0, 4)) >= 2019) {
          status = 2;
          saveBisNotice(requestBody, map.get("id"), status);
        }
      }
      // 向记录表business_log添加数据
      requestBody.put("bizType", "1");
      requestBody.put("action", "案件认领");
      requestBody.put("bizId", map.get("id"));
      requestBody.put("userId", requestBody.get("userId"));
      requestBody.put("userName", requestBody.get("userName"));
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "BUSINESSLOG");
      baseService.save(requestBody);
      return Result.ok(map.get("id"));
    } catch(Exception e) {
      log.error(e.getMessage());
      return  Result.fail("999986",e.getMessage());
    }

  }

  private void saveBisNotice(Map<String, Object> requestBody, Object ajId, int status) throws Exception {
    Map<String, Object> bisNotice = new HashMap<String, Object>();
    bisNotice.put("signOrgCode", requestBody.get("tDeptCode"));
    bisNotice.put("signOrgName", requestBody.get("tDeptName"));
    bisNotice.put("businessType", 1); // 1厅支队确认认领案件
    bisNotice.put("businessTable", 1); // 1厅支队确认认领案件
    bisNotice.put("businessProperty", 1); // 1 id
    bisNotice.put("businessValue", ajId);
    bisNotice.put("status", status);
    bisNotice.put("userId", requestBody.get("userId"));
    bisNotice.put("userName", requestBody.get("userName"));
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "BISNOTICE");
    baseService.save(bisNotice);
  }
}
