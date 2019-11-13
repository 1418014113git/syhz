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
import org.springframework.util.ObjectUtils;

import java.util.Map;

/**
 * <问题修改/>
 *
 * @author guowq.
 * @Date 2019/9/23 0023 - 16:48.
 */
@Service(value = "questionUpdateHandler")
public class QuestionUpdateHandler extends AbstractUpdateHandler {
    /** 是否启用-启用　**/
    private static final int ENABLE_YES = 0;
    /** 是否启用-禁用　**/
    private static final int ENABLE_NO = 1;

    public QuestionUpdateHandler(IBaseService baseService) {
        super(baseService);
    }

    @Override
    public Object update(String id, Map<String, Object> requestBody) throws Exception {
        if (ObjectUtils.isEmpty(requestBody.get("enable"))) {
            requestBody.put("enable", ENABLE_YES);
        }
        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "QUESTION");
        return baseService.update(id, requestBody);
    }
}
