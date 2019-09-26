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
import com.nmghr.basic.core.page.Paging;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractQueryHandler;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <子回复列表/>
 *
 * @author guowq.
 * @Date 2019/9/23 0023 - 16:02.
 */
@Service("questionAnswerWithChildAnswerQueryHandler")
public class QuestionAnswerWithChildAnswerQueryHandler extends AbstractQueryHandler {

    public QuestionAnswerWithChildAnswerQueryHandler(IBaseService baseService) {
        super(baseService);
    }

    /** 楼回答的parentAnswerId,默认-1 **/
    private static final int PARENT_ANSWER_ID_LOU = -1;
    /** 层回答数量，默认5条 **/
    private static final int SHOW_ANSWER_NUM_DEFAULT = 5;
    /** 是否启用-启用 **/
    private static final String ENABLE_YSE = "0";

    /**
     * 疑难问答回复查询并查询子回复
     *
     * @param requestMap
     * @param currentPage
     * @param pageSize
     * @return
     * @throws Exception
     */
    @Override
    public Object page(Map<String, Object> requestMap, int currentPage, int pageSize) throws Exception {
        requestMap.put("parentAnswerId", PARENT_ANSWER_ID_LOU);
        if (ObjectUtils.isEmpty(requestMap.get("childAnswerNum"))) {
            requestMap.put("childAnswerNum", SHOW_ANSWER_NUM_DEFAULT);
        }
        if (ObjectUtils.isEmpty(requestMap.get("enable"))) {
            requestMap.put("enable", ENABLE_YSE);
        }
        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "QUESTIONANSWER");
        Paging retPage = (Paging) baseService.page(requestMap, currentPage, pageSize);
        if (retPage == null || retPage.getList() == null || retPage.getList().size() == 0) {
            return new Paging(pageSize, currentPage, 0, new ArrayList<>());
        }
        List<Map<String, Object>> answerList = retPage.getList();
        Map<String, Object> searchMap = new HashMap<>(4);
        searchMap.put("questionId", requestMap.get("questionId"));
        int childAnswerNum = Integer.valueOf(requestMap.get("childAnswerNum").toString());
        List<Map<String, Object>> tempList = new ArrayList<>();
        for (Map<String, Object> answer : answerList) {
            Map<String, Object> retMap = new HashMap<>(32);
            retMap.putAll(answer);
            searchMap.put("parentAnswerId", answer.get("id"));
            Paging retChildPage = (Paging) baseService.page(searchMap, 0, childAnswerNum);
            if (retChildPage != null && retChildPage.getList() != null && retChildPage.getList().size() != 0) {
                retMap.put("childAnswerList", retChildPage.getList());
            }
            tempList.add(retMap);
        }
        return new Paging(pageSize, currentPage, retPage.getTotalCount(), tempList);
    }

}
