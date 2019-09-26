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
import com.nmghr.basic.common.exception.GlobalErrorException;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractSaveHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * <回复问题/>
 *
 * @author guowq.
 * @Date 2019/9/23 0023 - 18:02.
 */
@Service("questionAnswerSaveHandler")
public class QuestionAnswerSaveHandler extends AbstractSaveHandler {
    /** 是否启用-启用 **/
    private static final int ENABLE_YES = 0;
    /** 是否启用-停用 **/
    private static final int ENABLE_NO = 1;
    /** 是否删除-未删除 **/
    private static final int DEL_FLAG_YES = 0;
    /** 是否删除-已删除 **/
    private static final int DEL_FLAG_NO = 1;
    /** 楼回答的parentAnswerId,默认-1 **/
    private static final int PARENT_ANSWER_ID_LOU = -1;
    /** 操作类型-查询问题 **/
    private static final int QUESTION_LOG_OPT_TYPE_VIEW = 0;
    /** 操作类型-回答问题 **/
    private static final int QUESTION_LOG_OPT_TYPE_ANSWER = 1;

    public QuestionAnswerSaveHandler(IBaseService baseService) {
        super(baseService);
    }

    @Override
    @Transactional
    public Object save(Map<String, Object> requestBody) throws Exception {
        if (ObjectUtils.isEmpty(requestBody.get("enable"))) {
            requestBody.put("enable", ENABLE_YES);
        }
        if (ObjectUtils.isEmpty(requestBody.get("delFlag"))) {
            requestBody.put("delFlag", DEL_FLAG_YES);
        }
        /** 默认为楼回复 **/
        if (ObjectUtils.isEmpty(requestBody.get("parentAnswerId"))) {
            requestBody.put("parentAnswerId", PARENT_ANSWER_ID_LOU);
        }
        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "QUESTIONANSWER");
        Object obj = baseService.save(requestBody);
        if (!ObjectUtils.isEmpty(obj)) {
            /** 更新最后回复人信息 **/
            if (Integer.valueOf(requestBody.get("parentAnswerId").toString()) == PARENT_ANSWER_ID_LOU) {
                editQuestionAnswerUserInfo(requestBody);
            } else {
                editAnswerAnswerUserInfo(requestBody);
            }
            /** 保存日志 **/
            requestBody.put("optType", QUESTION_LOG_OPT_TYPE_ANSWER);
            requestBody.put("quId", requestBody.get("questionId"));
            LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "QUESTIONLOG");
            baseService.save(requestBody);
        }
        return obj;
    }

    /**
     * 修改问题的最后回复人信息
     *
     * @param paramMap
     * @throws Exception
     */
    private void editQuestionAnswerUserInfo(Map<String, Object> paramMap) throws Exception {
        Map<String, Object> tempMap = new HashMap<>(4);
        tempMap.put("lastAnswerUserId", paramMap.get("creationId"));
        tempMap.put("lastAnswerUserName", paramMap.get("creationName"));
        tempMap.put("questionId", paramMap.get("questionId"));
        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "QUESTIONFINAL");
        Object retObj = baseService.update(paramMap.get("questionId").toString(), tempMap);
        if (ObjectUtils.isEmpty(retObj) || Integer.valueOf(retObj.toString()) != 1) {
            throw new GlobalErrorException("999996", "修改最后回复人失败");
        }
    }

    /**
     * 修改楼回复的最后回复信息
     *
     * @param paramMap
     * @throws Exception
     */
    private void editAnswerAnswerUserInfo(Map<String, Object> paramMap) throws Exception {
        Map<String, Object> tempMap = new HashMap<>(4);
        tempMap.put("answerUserId", paramMap.get("creationId"));
        tempMap.put("answerUserName", paramMap.get("creationName"));
        tempMap.put("parentAnswerId", paramMap.get("parentAnswerId"));
        tempMap.put("questionId", paramMap.get("questionId"));
        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "QUESTIONANSWERANSWER");
        Object retObjUser = baseService.update(paramMap.get("parentAnswerId").toString(), tempMap);
        if (ObjectUtils.isEmpty(retObjUser) || Integer.valueOf(retObjUser.toString()) != 1) {
            throw new GlobalErrorException("999996", "修改最后回复人失败");
        }
        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "QUESTIONFINALNUM");
        Object retObjNum = baseService.update(paramMap.get("questionId").toString(), tempMap);
        if (ObjectUtils.isEmpty(retObjNum) || Integer.valueOf(retObjNum.toString()) != 1) {
            throw new GlobalErrorException("999996", "修改问题回复数量失败");
        }
    }

}
