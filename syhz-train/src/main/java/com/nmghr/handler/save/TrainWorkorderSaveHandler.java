/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.nmghr.handler.save;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import com.nmghr.basic.common.Result;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractSaveHandler;

/**
 * <功能描述/>
 *
 * @author kaven
 * @date 2019年9月23日 上午11:10:34
 * @version 1.0
 */
public class TrainWorkorderSaveHandler extends AbstractSaveHandler {
  private static final Logger LOGGER = LoggerFactory.getLogger(TrainWorkorderSaveHandler.class);

  public TrainWorkorderSaveHandler(IBaseService baseService) {
    super(baseService);
  }

  @Transactional
  public Object save(Map<String, Object> requestBody) throws Exception {

    return Result.ok("保存成功");
  }
}
