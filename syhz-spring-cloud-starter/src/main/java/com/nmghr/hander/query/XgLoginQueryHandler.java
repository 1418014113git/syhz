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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractQueryHandler;
import com.nmghr.util.DesCryptUtil;

/**
 * <功能描述/>
 *
 * @author brook  
 * @date 2018年10月9日 下午6:00:57 
 * @version 1.0   
 */
@Service("xgLoninQueryHandler")
public class XgLoginQueryHandler extends AbstractQueryHandler {
  private static final Logger log = LoggerFactory.getLogger(XgLoginQueryHandler.class);
  private final static String path = "http://10.100.11.125:80/search/usergrantSearch.jsp?query=";
  private final static String DesCryptUrl = "/search/carsear4/intelligent.jsp@@";
  Pattern p = Pattern.compile("(\\d{18})");

  public XgLoginQueryHandler(IBaseService baseService) {
    super(baseService);
    // TODO Auto-generated constructor stub
  }

  @Override
  public Object list(Map<String, Object> requestMap) throws Exception {
    // 查询工作单 传入部门判断是否正确
    log.info("Xg longin start ");
    try {

      Object uk = requestMap.get("uk");
      log.info("Xg longin start uk {}", uk);
      if (uk == null) {
        throw new Exception("uk参数为null");
      }
      Matcher m = p.matcher(uk.toString());
      m.find();
      String carId = m.group(0);
      log.info("Xg longin carId {}", carId);
      DesCryptUtil des = new DesCryptUtil("sungoalLoginGrant");
      String url = des.encrypt(DesCryptUrl + carId);
      log.info("Xg longin descrypt string {}", path + url);
      return path + url;
    } catch (Exception e) {
      // TODO: handle exception
      log.error("Xg error message {}", e.getMessage());
      return null;
    }
  }

}
