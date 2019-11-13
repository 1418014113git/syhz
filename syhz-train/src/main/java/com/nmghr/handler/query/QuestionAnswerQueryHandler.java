/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.nmghr.handler.query;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractQueryHandler;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * <回复列表/>
 *
 * @author guowq.
 * @Date 2019/9/23 0023 - 16:02.
 */
@Service("questionAnswerQueryHandler")
public class QuestionAnswerQueryHandler extends AbstractQueryHandler {

    public QuestionAnswerQueryHandler(IBaseService baseService) {
        super(baseService);
    }

    /**
     * 疑难问答回复查询
     *
     * @param requestMap
     * @param currentPage
     * @param pageSize
     * @return
     * @throws Exception
     */
    @Override
    public Object page(Map<String, Object> requestMap, int currentPage, int pageSize) throws Exception {
        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "QUESTIONANSWER");
        return baseService.page(requestMap, currentPage, pageSize);
    }

}
