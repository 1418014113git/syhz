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

@Service("mutichoiceSaveHandler")
public class MutiChoiceSaveHandler extends AbstractSaveHandler {
    public MutiChoiceSaveHandler(IBaseService baseService) {
        super(baseService);
    }

    @Transactional
    @Override
    public Object save(Map<String, Object> requestBody) throws Exception {
        validParams(requestBody);
        Map<String,Object> mutiChoiceMap = new HashMap();
        //题目Map
        mutiChoiceMap.put("subjectName",requestBody.get("subjectName"));
        //题目类型 1单选 2多选
        mutiChoiceMap.put("choicesType",2);
        //答案
        mutiChoiceMap.put("answer",requestBody.get("answer"));
        //题目解析
        mutiChoiceMap.put("analysis",requestBody.get("analysis"));
        //来源
        mutiChoiceMap.put("source",requestBody.get("source"));
        //排序
        mutiChoiceMap.put("sort",requestBody.get("sort"));
        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMCHOICES");
        Object saveId = baseService.save(mutiChoiceMap);
        //选项Map
        Map<String,Object> mutiChoicePointMap = new HashMap<>();
        mutiChoicePointMap.put("choicesId", saveId);
        mutiChoicePointMap.put("pointValue", requestBody.get("pointA"));
        mutiChoicePointMap.put("point", "A");
        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMCHOICESPOINT");
        baseService.save(mutiChoicePointMap);
        mutiChoicePointMap.clear();
        mutiChoicePointMap.put("choicesId", saveId);
        mutiChoicePointMap.put("pointValue",requestBody.get("pointB"));
        mutiChoicePointMap.put("point", "B");
        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMCHOICESPOINT");
        baseService.save(mutiChoicePointMap);
        mutiChoicePointMap.clear();
        mutiChoicePointMap.put("choicesId", saveId);
        mutiChoicePointMap.put("pointValue", requestBody.get("pointC"));
        mutiChoicePointMap.put("point", "C");
        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMCHOICESPOINT");
        baseService.save(mutiChoicePointMap);
        mutiChoicePointMap.clear();
        mutiChoicePointMap.put("choicesId", saveId);
        mutiChoicePointMap.put("pointValue",requestBody.get("pointD"));
        mutiChoicePointMap.put("point","D");
        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMCHOICESPOINT");
        baseService.save(mutiChoicePointMap);
        mutiChoicePointMap.clear();
        mutiChoicePointMap.put("choicesId", saveId);
        mutiChoicePointMap.put("pointValue",requestBody.get("pointE"));
        mutiChoicePointMap.put("point","E");
        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMCHOICESPOINT");
        baseService.save(mutiChoicePointMap);
        mutiChoicePointMap.clear();
        mutiChoicePointMap.put("choicesId", saveId);
        mutiChoicePointMap.put("pointValue",requestBody.get("pointF"));
        mutiChoicePointMap.put("point","F");
        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMCHOICESPOINT");
        baseService.save(mutiChoicePointMap);
        mutiChoicePointMap.clear();

        //维护题库试题关系表
        Map<String,Object> mapping = new HashMap<>();
        //题库科目Id
        mapping.put("subjectCategoryId",requestBody.get("subjectCategoryId"));
        //试题Id
        mapping.put("questionsId",saveId);
        mapping.put("type",2);
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
