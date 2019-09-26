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
import org.springframework.util.ObjectUtils;

import java.util.Map;

/**
 * <添加问题/>
 *
 * @author guowq.
 * @Date 2019/9/23 0023 - 16:28.
 */
@Service("questionsSaveHandler")
public class QuestionsSaveHandler extends AbstractSaveHandler {
    /** 是否启用-启用 **/
    private static final int ENABLE_YES = 0;
    /** 是否删除-删除 **/
    private static final int DEL_FLAG_NO = 0;
    /** 浏览数量-初始化 **/
    private static final int VIEW_NUMBER_DEFAULT = 0;
    /** 回答数量-初始化 **/
    private static final int ANSWER_NUMBER_DEFAULT = 0;

    public QuestionsSaveHandler(IBaseService baseService) {
        super(baseService);
    }

    @Override
    public Object save(Map<String, Object> requestBody) throws Exception {
        if (ObjectUtils.isEmpty(requestBody.get("enable"))) {
            requestBody.put("enable", ENABLE_YES);
        }
        requestBody.put("delFlag", DEL_FLAG_NO);
        requestBody.put("viewNumber", VIEW_NUMBER_DEFAULT);
        requestBody.put("answerNumber", ANSWER_NUMBER_DEFAULT);
        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "QUESTION");
        return baseService.save(requestBody);
    }
}
