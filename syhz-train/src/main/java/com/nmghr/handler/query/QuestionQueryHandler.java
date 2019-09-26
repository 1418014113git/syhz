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
import com.nmghr.basic.common.Result;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractQueryHandler;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Map;

/**
 * <疑难问答/>
 *
 * @author guowq.
 * @Date 2019/9/23 0023 - 16:02.
 */
@Service("questionsQueryHandler")
public class QuestionQueryHandler extends AbstractQueryHandler {
    /** 是否启用-启用 **/
    private static final String ENABLE_YES = "0";
    /** 是否启用-禁用 **/
    private static final String ENABLE_NO = "1";
    /** 是否删除-已删除 **/
    private static final String DEL_FLAG_YES = "1";
    /** 是否删除-未删除 **/
    private static final String DEL_FLAG_NO = "0";

    public QuestionQueryHandler(IBaseService baseService) {
        super(baseService);
    }

    /**
     * 疑难问答列表查询
     *
     * @param requestMap
     * @param currentPage
     * @param pageSize
     * @return
     * @throws Exception
     */
    @Override
    public Object page(Map<String, Object> requestMap, int currentPage, int pageSize) throws Exception {
        if (ObjectUtils.isEmpty(requestMap.get("enable"))) {
            requestMap.put("enable", ENABLE_YES);
        }
        if (ObjectUtils.isEmpty(requestMap.get("delFlag"))) {
            requestMap.put("delFlag", DEL_FLAG_NO);
        }
        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "QUESTION");
        return baseService.page(requestMap, currentPage, pageSize);
    }

    /**
     * 问题详情
     *
     * @param id
     * @return
     * @throws Exception
     */
    @Override
    public Object get(String id) throws Exception {
        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "QUESTION");
        return baseService.get(id);
    }
}
