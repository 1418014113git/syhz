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

@Service("judgeSaveHandler")
public class JudgeSaveHandler extends AbstractSaveHandler {
    public JudgeSaveHandler(IBaseService baseService) {
        super(baseService);
    }

    @Transactional
    @Override
    public Object save(Map<String, Object> requestBody) throws Exception {
        validParams(requestBody);
        Map<String,Object> fillGapMap = new HashMap();
        //题目Map
        fillGapMap.put("subjectName",requestBody.get("subjectName"));
        //题目类型 1单选 2多选
        fillGapMap.put("choicesType",1);
        //答案
        fillGapMap.put("answer",requestBody.get("answer"));
        //题目解析
        fillGapMap.put("analysis",requestBody.get("analysis"));
        //来源
        fillGapMap.put("source",requestBody.get("source"));
        //排序
        fillGapMap.put("sort",requestBody.get("sort"));
        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMJUDGE");
        Object saveId = baseService.save(fillGapMap);
        Integer Id = (Integer) saveId;
        //维护题库试题关系表
        Map<String,Object> mapping = new HashMap<>();
        //题库科目Id
        mapping.put("subjectCategoryId",requestBody.get("subjectCategoryId"));
        //试题Id
        mapping.put("questionsId",Id);
        mapping.put("type",4);
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
        if(requestBody.get("answer") == null){
            throw new GlobalErrorException("998001", "请输入正确答案!");
        }
        if(requestBody.get("source") == null){
            throw new GlobalErrorException("998001", "请输入出处!");
        }
    }
}
