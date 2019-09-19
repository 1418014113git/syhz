package com.nmghr.handler.save;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.common.exception.GlobalErrorException;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractSaveHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
@Service("simplechoiceSaveHandler")
public class SimpleChoiceSaveHandler extends AbstractSaveHandler {
    public SimpleChoiceSaveHandler(IBaseService baseService) {
        super(baseService);
    }

    @Transactional
    @Override
    public Object save(Map<String, Object> requestBody) throws Exception {
        validParams(requestBody);
        Map<String,Object> simpleChoiceMap = new HashMap();
        //题目Map
        simpleChoiceMap.put("subjectName",requestBody.get("subjectName"));
        //题目类型 1单选 2多选
        simpleChoiceMap.put("choicesType",1);
        //答案
        simpleChoiceMap.put("answer",requestBody.get("answer"));
        //题目解析
        simpleChoiceMap.put("analysis",requestBody.get("analysis"));
        //来源
        simpleChoiceMap.put("source",requestBody.get("source"));
        //排序
        simpleChoiceMap.put("sort",requestBody.get("sort"));
        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMCHOICES");
        Object saveId = baseService.save(simpleChoiceMap);
        //选项Map
        Map<String,Object> simpleChoicePointMap = new HashMap<>();
        simpleChoicePointMap.put("choicesId", saveId);
        simpleChoicePointMap.put("pointValue", requestBody.get("pointA"));
        simpleChoicePointMap.put("point", "A");
        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMCHOICESPOINT");
        baseService.save(simpleChoicePointMap);
        simpleChoicePointMap.clear();
        simpleChoicePointMap.put("choicesId", saveId);
        simpleChoicePointMap.put("pointValue",requestBody.get("pointB"));
        simpleChoicePointMap.put("point", "B");
        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMCHOICESPOINT");
        baseService.save(simpleChoicePointMap);
        simpleChoicePointMap.clear();
        simpleChoicePointMap.put("choicesId", saveId);
        simpleChoicePointMap.put("pointValue", requestBody.get("pointC"));
        simpleChoicePointMap.put("point","C");
        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMCHOICESPOINT");
        baseService.save(simpleChoicePointMap);
        simpleChoicePointMap.clear();
        simpleChoicePointMap.put("choicesId", saveId);
        simpleChoicePointMap.put("pointValue",requestBody.get("pointD"));
        simpleChoicePointMap.put("point","D");
        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMCHOICESPOINT");
        baseService.save(simpleChoicePointMap);
        simpleChoicePointMap.clear();
        //维护题库试题关系表
        Map<String,Object> mapping = new HashMap<>();
        //题库科目Id
        mapping.put("subjectCategoryId",requestBody.get("subjectCategoryId"));
        //试题Id
        mapping.put("questionsId",saveId);
        mapping.put("type",1);
        mapping.put("creator","创建人");
        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMSUBJECTCATEGORYMAPPING");
        baseService.save(mapping);
    return null;
    }

    private void validParams(Map<String, Object> requestBody) {
        if(requestBody.get("subjectCategoryId") == null){
            throw new GlobalErrorException("998001", "模块Id不能为空!");
        }
        if(requestBody.get("subjectName") == null){
            throw new GlobalErrorException("998001", "请输入题目内容!");
        }
        if(requestBody.get("pointA") == null){
            throw new GlobalErrorException("998001", "请输入选项A!");
        }
        if(requestBody.get("pointB") == null){
            throw new GlobalErrorException("998001", "请输入选项B!");
        }
        if(requestBody.get("pointC") == null){
            throw new GlobalErrorException("998001", "请输入选项C!");
        }
        if(requestBody.get("pointD") == null){
            throw new GlobalErrorException("998001", "请输入选项D!");
        }
        if(requestBody.get("answer") == null){
            throw new GlobalErrorException("998001", "请输入正确答案!");
        }
        if(requestBody.get("source") == null){
            throw new GlobalErrorException("998001", "请输入出处!");
        }

    }
}
