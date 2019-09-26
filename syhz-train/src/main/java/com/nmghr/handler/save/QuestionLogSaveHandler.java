/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.nmghr.handler.save;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractSaveHandler;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * <添加日志/>
 *
 * @author guowq.
 * @Date 2019/9/24 0024 - 10:14.
 */
@Service(value = "questionLogSaveHandler")
public class QuestionLogSaveHandler extends AbstractSaveHandler {
    public QuestionLogSaveHandler(IBaseService baseService) {
        super(baseService);
    }

    @Override
    public Object save(Map<String, Object> requestBody) throws Exception {
        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "QUESTIONLOG");
        return baseService.save(requestBody);
    }
}
