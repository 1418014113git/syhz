/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.nmghr.controller;

import com.nmghr.basic.common.Result;
import com.nmghr.basic.core.service.handler.IQueryHandler;
import com.nmghr.basic.core.service.handler.ISaveHandler;
import com.nmghr.basic.core.service.handler.IUpdateHandler;
import com.nmghr.basic.core.util.SpringUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <疑难问答/>
 *
 * @author guowq.
 * @Date 2019/9/23 0023 - 11:56.
 */
@RestController
@RequestMapping("question")
public class QuestionController {

    /** 是否启用浏览数量+1操作 **/
    private static final int OPER_ADD_VIEWNUM_MARK_YES = 1;
    /** 是否启用添加浏览记录操作 **/
    private static final int OPER_ADD_VIEWLOG_MARK_YES = 1;
    /** 操作类型-查询问题 **/
    private static final int QUESTION_LOG_OPT_TYPE_VIEW = 0;
    /** 操作类型-回答问题 **/
    private static final int QUESTION_LOG_OPT_TYPE_ANSWER = 1;


    /**
     * 查询问题列表
     *
     * @param requestParam
     * @return
     * @throws Exception
     */
    @GetMapping(value = "/page")
    public Object pageQuestion(@RequestParam Map<String, Object> requestParam) throws Exception {
        int currentPage = Integer.valueOf(requestParam.get("currentPage") + "");
        int pageSize = Integer.valueOf(requestParam.get("pageSize") + "");
        IQueryHandler queryHandler = SpringUtils.getBean("questionsQueryHandler", IQueryHandler.class);
        return queryHandler.page(requestParam, currentPage, pageSize);
    }

    /**
     * 查看问题详情
     *
     * @param requestParam
     * @return
     * @throws Exception
     */
    @GetMapping(value = "/info")
    @Transactional
    public Object infoQuestion(@RequestParam Map<String, Object> requestParam, HttpServletRequest request) throws Exception {
        if (ObjectUtils.isEmpty(requestParam.get("id"))) {
            return Result.fail("999996", "id不能为空");
        }
        /** 是否执行浏览数量+1操作，默认执行 **/
        if (ObjectUtils.isEmpty(requestParam.get("addViewNumMark"))) {
            requestParam.put("addViewNumMark", OPER_ADD_VIEWNUM_MARK_YES);
        }
        /** 是否启用添加浏览记录操作，默认执行 **/
        if (ObjectUtils.isEmpty(requestParam.get("addViewLogMark"))) {
            requestParam.put("addViewLogMark", OPER_ADD_VIEWLOG_MARK_YES);
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
            requestParam.put("viewTime", df.format(new Date()));
        }
        String id = requestParam.get("id") + "";
        IQueryHandler queryHandler = SpringUtils.getBean("questionsQueryHandler", IQueryHandler.class);
        Map<String, Object> retQuestionInfoMap = (Map<String, Object>) queryHandler.get(id);
        if (!ObjectUtils.isEmpty(retQuestionInfoMap) && !ObjectUtils.isEmpty(retQuestionInfoMap.get("id"))) {
            /** 更新浏览数量 **/
            if (Integer.valueOf(requestParam.get("addViewNumMark").toString()) == OPER_ADD_VIEWNUM_MARK_YES) {
                IUpdateHandler viewNumUpdateHandler = SpringUtils.getBean("questionViewNumUpdateHandler", IUpdateHandler.class);
                viewNumUpdateHandler.update(retQuestionInfoMap.get("id").toString(), requestParam);
            }
            /** 添加浏览记录 **/
            if (Integer.valueOf(requestParam.get("addViewLogMark").toString()) == OPER_ADD_VIEWLOG_MARK_YES) {
                if (ObjectUtils.isEmpty(requestParam.get("belongAreaCode"))) {
                    return Result.fail("999996", "所属区域不能为空");
                }
                if (ObjectUtils.isEmpty(requestParam.get("belongDepCode"))) {
                    return Result.fail("999996", "部门Code不能为空");
                }
                if (ObjectUtils.isEmpty(requestParam.get("belongDepName"))) {
                    return Result.fail("999996", "部门名称不能为空");
                }
                if (ObjectUtils.isEmpty(requestParam.get("creationId"))) {
                    return Result.fail("999996", "创建人Id不能为空");
                }
                if (ObjectUtils.isEmpty(requestParam.get("creationName"))) {
                    return Result.fail("999996", "创建人用户名不能为空");
                }
                requestParam.put("lastId", requestParam.get("creationId"));
                requestParam.put("lastName", requestParam.get("creationName"));
                ISaveHandler addViewLogSaveHandler = SpringUtils.getBean("questionLogSaveHandler", ISaveHandler.class);
                Map<String, Object> viewLogMap = new HashMap<>(16);
                viewLogMap.putAll(requestParam);
                viewLogMap.put("quId", retQuestionInfoMap.get("id"));
                viewLogMap.put("optType", QUESTION_LOG_OPT_TYPE_VIEW);
                viewLogMap.put("ip", request.getRemoteAddr());
                Object retViewLog = addViewLogSaveHandler.save(viewLogMap);
                if (!ObjectUtils.isEmpty(retViewLog)) {
                    retQuestionInfoMap.put("viewLogId", retViewLog);
                }
            }
        }
        return retQuestionInfoMap;
    }

    /**
     * 新建问题
     *
     * @param requestParam
     * @return
     * @throws Exception
     */
    @PutMapping(value = "/save")
    public Object saveQuestion(@RequestBody Map<String, Object> requestParam) throws Exception {
        if (ObjectUtils.isEmpty(requestParam.get("quTitle"))) {
            return Result.fail("999996", "问题标题不能为空");
        }
        if (ObjectUtils.isEmpty(requestParam.get("quType"))) {
            return Result.fail("999996", "问题类别不能为空");
        }
        if (ObjectUtils.isEmpty(requestParam.get("introduction"))) {
            return Result.fail("999996", "简介不能为空");
        }
        if (ObjectUtils.isEmpty(requestParam.get("belongAreaCode"))) {
            return Result.fail("999996", "所属区域不能为空");
        }
        if (ObjectUtils.isEmpty(requestParam.get("belongDepCode"))) {
            return Result.fail("999996", "部门code不能为空");
        }
        if (ObjectUtils.isEmpty(requestParam.get("belongDepName"))) {
            return Result.fail("999996", "部门名称不能为空");
        }
        if (ObjectUtils.isEmpty(requestParam.get("creationId"))) {
            return Result.fail("999996", "创建人id不能为空");
        }
        if (ObjectUtils.isEmpty(requestParam.get("creationName"))) {
            return Result.fail("999996", "创建人用户名不能为空");
        }
        ISaveHandler saveHandler = SpringUtils.getBean("questionsSaveHandler", ISaveHandler.class);
        return saveHandler.save(requestParam);
    }

    /**
     * 修改问题
     *
     * @param requestParam
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/edit")
    public Object editQuestion(@RequestBody Map<String, Object> requestParam) throws Exception {
        if (ObjectUtils.isEmpty(requestParam.get("introduction"))) {
            return Result.fail("999996", "简介不能为空");
        }
        if (ObjectUtils.isEmpty(requestParam.get("id"))) {
            return Result.fail("999996", "问题id不能为空");
        }
        if (ObjectUtils.isEmpty(requestParam.get("lastId"))) {
            return Result.fail("999996", "修改人id不能为空");
        }
        if (ObjectUtils.isEmpty(requestParam.get("lastName"))) {
            return Result.fail("999996", "修改人用户名不能为空");
        }
        String id = requestParam.get("id") + "";
        IUpdateHandler updateHandler = SpringUtils.getBean("questionUpdateHandler", IUpdateHandler.class);
        return updateHandler.update(id, requestParam);
    }

    /**
     * 删除问题
     *
     * @param requestParam
     * @return
     * @throws Exception
     */
    @DeleteMapping(value = "/delete")
    public Object delQuestion(@RequestBody Map<String, Object> requestParam) throws Exception {
        if (ObjectUtils.isEmpty(requestParam.get("id"))) {
            return Result.fail("999996", "问题Id不能为空");
        }
        if (ObjectUtils.isEmpty(requestParam.get("lastId"))) {
            return Result.fail("999996", "修改人id不能为空");
        }
        if (ObjectUtils.isEmpty(requestParam.get("lastName"))) {
            return Result.fail("999996", "修改人用户名不能为空");
        }
        IUpdateHandler updateHandler = SpringUtils.getBean("questionDelHandler", IUpdateHandler.class);
        return updateHandler.update(requestParam.get("id").toString(), requestParam);
    }

    /**
     * 查询回复
     *
     * @param requestParam
     * @return
     * @throws Exception
     */
    @GetMapping(value = "/page/answerWithChild")
    public Object pageAnswerWidthChild(@RequestParam Map<String, Object> requestParam) throws Exception {
        int currentPage = Integer.valueOf(requestParam.get("currentPage").toString());
        int pageSize = Integer.valueOf(requestParam.get("pageSize").toString());
        if (ObjectUtils.isEmpty(requestParam.get("questionId"))) {
            return Result.fail("999996", "问题不能为空");
        }
        IQueryHandler queryHandler = SpringUtils.getBean("questionAnswerWithChildAnswerQueryHandler", IQueryHandler.class);
        return queryHandler.page(requestParam, currentPage, pageSize);
    }

    /**
     * 查询子回复
     *
     * @param requestParam
     * @return
     * @throws Exception
     */
    @GetMapping(value = "/page/answer")
    public Object pageAnswer(@RequestParam Map<String, Object> requestParam) throws Exception {
        int currentPage = Integer.valueOf(requestParam.get("currentPage") + "");
        int pageSize = Integer.valueOf(requestParam.get("pageSize") + "");
        if (ObjectUtils.isEmpty(requestParam.get("questionId"))) {
            return Result.fail("999996", "问题不能为空");
        }
        if (ObjectUtils.isEmpty(requestParam.get("parentAnswerId"))) {
            return Result.fail("999996", "父回复ID不能为空");
        }
        IQueryHandler queryHandler = SpringUtils.getBean("questionAnswerQueryHandler", IQueryHandler.class);
        return queryHandler.page(requestParam, currentPage, pageSize);
    }

    /**
     * 新建回复
     * @param requestBody
     * @param request
     * @return
     * @throws Exception
     */
    @PutMapping(value = "/save/answer")
    public Object saveAnswer(@RequestBody Map<String, Object> requestBody, HttpServletRequest request) throws Exception {
        if (ObjectUtils.isEmpty(requestBody.get("belongAreaCode"))) {
            return Result.fail("999996", "所属区域不能为空");
        }
        if (ObjectUtils.isEmpty(requestBody.get("belongDepCode"))) {
            return Result.fail("999996", "部门Code不能为空");
        }
        if (ObjectUtils.isEmpty(requestBody.get("belongDepName"))) {
            return Result.fail("999996", "部门名称不能为空");
        }
        if (ObjectUtils.isEmpty(requestBody.get("creationId"))) {
            return Result.fail("999996", "创建人Id不能为空");
        }
        if (ObjectUtils.isEmpty(requestBody.get("creationName"))) {
            return Result.fail("999996", "创建人用户名不能为空");
        }
        if (ObjectUtils.isEmpty(requestBody.get("questionId"))) {
            return Result.fail("999996", "问题ID不能为空");
        }
        if (ObjectUtils.isEmpty(requestBody.get("viewTime"))) {
            return Result.fail("999996", "浏览时间不能为空");
        }
        if (ObjectUtils.isEmpty(requestBody.get("stopTime"))) {
            return Result.fail("999996", "停留时长不能为空");
        }
        if (ObjectUtils.isEmpty(requestBody.get("answerRemark"))) {
            return Result.fail("999996", "回复内容不能为空");
        }
        requestBody.put("ip", request.getRemoteAddr());
        requestBody.put("lastId", requestBody.get("creationId").toString());
        requestBody.put("lastName", requestBody.get("creationName").toString());
        ISaveHandler saveHandler = SpringUtils.getBean("questionAnswerSaveHandler", ISaveHandler.class);
        return saveHandler.save(requestBody);
    }

    /**
     * 保存日志记录
     *
     * @param requestBody
     * @param request
     * @return
     * @throws Exception
     */
    @PutMapping(value = "/save/questionLog")
    public Object saveQuestionLog(@RequestBody Map<String, Object> requestBody, HttpServletRequest request) throws Exception {
        if (ObjectUtils.isEmpty(requestBody.get("belongAreaCode"))) {
            return Result.fail("999996", "所属区域不能为空");
        }
        if (ObjectUtils.isEmpty(requestBody.get("belongDepCode"))) {
            return Result.fail("999996", "部门Code不能为空");
        }
        if (ObjectUtils.isEmpty(requestBody.get("belongDepName"))) {
            return Result.fail("999996", "部门名称不能为空");
        }
        if (ObjectUtils.isEmpty(requestBody.get("creationId"))) {
            return Result.fail("999996", "创建人Id不能为空");
        }
        if (ObjectUtils.isEmpty(requestBody.get("creationName"))) {
            return Result.fail("999996", "创建人用户名不能为空");
        }
        if (ObjectUtils.isEmpty(requestBody.get("quId"))) {
            return Result.fail("999996", "问题ID不能为空");
        }
        if (ObjectUtils.isEmpty(requestBody.get("optType"))) {
            return Result.fail("999996", "操作类型不能为空");
        }
        if (ObjectUtils.isEmpty(requestBody.get("viewTime"))) {
            requestBody.put("viewTime", null);
        }
        if (ObjectUtils.isEmpty(requestBody.get("stopTime"))) {
            requestBody.put("stopTime", 0);
        }
        requestBody.put("ip", request.getRemoteAddr());
        requestBody.put("lastId", requestBody.get("creationId"));
        requestBody.put("lastName", requestBody.get("creationName"));
        ISaveHandler addViewLogSaveHandler = SpringUtils.getBean("questionLogSaveHandler", ISaveHandler.class);
        return addViewLogSaveHandler.save(requestBody);
    }

    /**
     * 修改停留时长s
     *
     * @param requestBody
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/edit/questionLogViewTime")
    public Object questionLogViewTime(@RequestBody Map<String, Object> requestBody) throws Exception {
        if (ObjectUtils.isEmpty(requestBody.get("id"))) {
            return Result.fail("999996", "记录ID不能为空");
        }
        if (ObjectUtils.isEmpty(requestBody.get("stopTime"))) {
            return Result.fail("999996", "停留时长不能为空");
        }
        if (Integer.valueOf(requestBody.get("stopTime").toString()) < 0) {
            return Result.fail("999996", "停留时长不能小于0");
        }
        IUpdateHandler updateHandler = SpringUtils.getBean("questionLogViewTimeUpdateHandler", IUpdateHandler.class);
        return updateHandler.update(requestBody.get("id").toString(), requestBody);
    }

}


