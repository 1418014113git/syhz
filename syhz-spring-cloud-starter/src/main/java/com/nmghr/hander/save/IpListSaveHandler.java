/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.nmghr.hander.save;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.common.exception.GlobalErrorEnum;
import com.nmghr.basic.common.exception.GlobalErrorException;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractSaveHandler;
import com.nmghr.basic.core.util.ValidationUtils;
import com.nmghr.util.IPUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * <功能描述/>
 *
 * @author weber
 * @date 2018年7月26日 下午3:46:31
 * @version 1.0
 */
@Service("iplistSaveHandler")
public class IpListSaveHandler extends AbstractSaveHandler {

  public IpListSaveHandler(IBaseService baseService) {
    super(baseService);
  }


  /**
   * 黑白名单添加/修改
   */
  @Override
  @Transactional
  public Object save(Map<String, Object> requestBody) throws Exception {
    validateParam(requestBody);
    String ipAddressStart = String.valueOf(requestBody.get("ipAddressStart"));
    String ipAddressEnd = String.valueOf(requestBody.get("ipAddressEnd"));
    //requestBody.get("")
    if(ipAddressStart.equals(ipAddressEnd)) {
      requestBody.put("ipAddress", ipAddressStart);
    }else{
      requestBody.put("ipAddress",ipAddressStart+"~"+ipAddressEnd);
    }
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "IPREPEATCHECK");
    Map<String,Object> param = new HashMap<>();
    if(requestBody.get("id") !=null || !"".equals(String.valueOf(requestBody.get("id"))))
    {
      param.put("id",String.valueOf(requestBody.get("id")));
    }
    param.put("ipAddress",String.valueOf(requestBody.get("ipAddress")));
    Map<String,Object> ipCount = (Map<String, Object>) baseService.get(param);
    if(ipCount!= null && Integer.valueOf(String.valueOf(ipCount.get("num"))) > 0){
      throw new GlobalErrorException(GlobalErrorEnum.PARAM_NOT_VALID.getCode(), "ip地址重复,请确认后重新填写");
    }
    if(requestBody.get("id") !=null){
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "IPCONTROL");
      return baseService.update(String.valueOf(requestBody.get("id")),requestBody);
    }
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "IPCONTROL");
    return baseService.save(requestBody);
  }

  private void validateParam(Map<String, Object> requestBody) {

    ValidationUtils.notNull(requestBody.get("ipAddressStart"),"ip地址段开始不能为空");
    ValidationUtils.notNull(requestBody.get("ipAddressEnd"),"ip地址段结束不能为空");
    ValidationUtils.notNull(requestBody.get("endTime"),"有效期不能为空");
    String ipAddressStart = String.valueOf(requestBody.get("ipAddressStart"));
    String ipAddressEnd = String.valueOf(requestBody.get("ipAddressEnd"));
    if(!ipAddressEnd.equals(ipAddressStart)){
      if(IPUtil.getIp2long2(ipAddressStart) > IPUtil.getIp2long2(ipAddressEnd)){
        ValidationUtils.notNull(requestBody.get("endDate"),"请输入正确的IP地址段");
      }
    }
  }
}
