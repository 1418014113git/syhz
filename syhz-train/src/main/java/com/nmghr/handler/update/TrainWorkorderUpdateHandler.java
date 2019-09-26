/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nmghr.handler.update;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractUpdateHandler;

/**
 * <功能描述/>
 *
 * @author kaven
 * @date 2019年9月23日 上午11:10:52 
 * @version 1.0   
 */
public class TrainWorkorderUpdateHandler extends AbstractUpdateHandler{
  private static final Logger LOGGER = LoggerFactory.getLogger(TrainWorkorderUpdateHandler.class);

  public TrainWorkorderUpdateHandler(IBaseService baseService) {
    super(baseService);
  }
  @Override
  @Transactional
  public Object update(String id, Map<String, Object> requestBody) throws Exception {
    
    return "1";
  }
}
