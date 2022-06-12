/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.nmghr.handler.update;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractUpdateHandler;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * <修改停留时长/>
 *
 * @author guowq.
 * @Date 2019/9/24 0024 - 10:32.
 */
@Service(value = "questionLogViewTimeUpdateHandler")
public class QuestionLogViewTimeUpdateHandler extends AbstractUpdateHandler {
    public QuestionLogViewTimeUpdateHandler(IBaseService baseService) {
        super(baseService);
    }

    @Override
    public Object update(String id, Map<String, Object> requestBody) throws Exception {
        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "QUESTIONLOG");
        return super.update(id, requestBody);
    }
}
