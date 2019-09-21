package com.nmghr.hander.save;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.common.exception.GlobalErrorException;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractSaveHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

@Service("questionSaveHandler")
public class ExamQuestionsSaveHandler extends AbstractSaveHandler {
    public ExamQuestionsSaveHandler(IBaseService baseService) {
        super(baseService);
    }

    @Transactional
    @Override
    public Object save(Map<String, Object> requestBody) throws Exception {
        validParams(requestBody);
        if("1".equals(String.valueOf(requestBody.get("type"))) || "2".equals(String.valueOf(requestBody.get("type")))){
            LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMCHOICES");
            Object saveId = baseService.save(requestBody);
            //选项Map
            Map<String,Object> choicePointMap = new HashMap<>();
            //遍历参数中的选项数组
            Map<String,Object> points = (Map<String, Object>) requestBody.get("points");
            for (String s : points.keySet()) {
                Object pointValue = points.get(s);
                if(pointValue!=null &&!"".equals(pointValue)) {
                    choicePointMap.put("choicesId", saveId);
                    choicePointMap.put("pointValue", pointValue);
                    choicePointMap.put("point", s.substring(s.length() - 1));
                    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMCHOICESPOINT");
                    baseService.save(choicePointMap);
                }
            }
            //维护题库试题关系表
            saveMappings(requestBody, saveId);
        }
        else {
            if("3".equals(String.valueOf(requestBody.get("type")))) {
                LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMFILLGAPS");
                Object saveId = baseService.save(requestBody);
                saveMappings(requestBody, saveId);
            }
            if("4".equals(String.valueOf(requestBody.get("type")))) {
                LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMJUDGE");
                Object saveId = baseService.save(requestBody);
                saveMappings(requestBody, saveId);
            }
            if("5".equals(String.valueOf(requestBody.get("type")))
            || "6".equals(String.valueOf(requestBody.get("type")))
            || "7".equals(String.valueOf(requestBody.get("type")))) {
                LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMDISCUSS");
                Object saveId = baseService.save(requestBody);
                saveMappings(requestBody, saveId);
            }
        }
    return null;
    }

    private void saveMappings(Map<String, Object> requestBody, Object saveId) throws Exception {
        Map<String,Object> mapping = new HashMap<>();
        //题库科目Id
        mapping.put("subjectCategoryId",requestBody.get("subjectCategoryId"));
        //试题Id
        mapping.put("questionsId",saveId);
        mapping.put("type",requestBody.get("type"));
        mapping.put("creator",requestBody.get("creator"));
        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMSUBJECTCATEGORYMAPPING");
        baseService.save(mapping);
    }

    private void validParams(Map<String, Object> requestBody) {

        if(requestBody.get("type") == null){
            throw new GlobalErrorException("998001", "试题类型不能为空!");
        }
        if(requestBody.get("subjectCategoryId") == null || "".equals(requestBody.get("subjectCategoryId"))){
            throw new GlobalErrorException("998001", "模块Id不能为空!");
        }
        if(requestBody.get("subjectName") == null || "".equals(requestBody.get("subjectName"))){
            throw new GlobalErrorException("998001", "请输入题目内容!");
        }
        String type = String.valueOf(requestBody.get("type"));
        {
            if("1".equals(type) || ("2".equals(type)) || ("3".equals(type)) || ("4".equals(type))){
                if(requestBody.get("answer") == null ||"".equals(requestBody.get("answer"))){
                    throw new GlobalErrorException("998001", "请输入正确答案!");
                }
            }
        }
        if(requestBody.get("source") == null || "".equals(requestBody.get("source"))){
            throw new GlobalErrorException("998001", "请输入出处!");
        }
        if("1".equals(requestBody.get("type")) || "2".equals(requestBody.get("type"))){
            Map<String,Object> points = (Map<String, Object>) requestBody.get("points");
            if(points!=null) {
                if (points.get("pointA") == null || "".equals(requestBody.get("pointA"))) {
                    throw new GlobalErrorException("998001", "请输入选项A!");
                }
                if (points.get("pointB") == null || "".equals(requestBody.get("pointB"))) {
                    throw new GlobalErrorException("998001", "请输入选项B!");
                }
                if (points.get("pointC") == null || "".equals(requestBody.get("pointC"))) {
                    throw new GlobalErrorException("998001", "请输入选项C!");
                }
                if (points.get("pointD") == null || "".equals(requestBody.get("pointD"))) {
                    throw new GlobalErrorException("998001", "请输入选项D!");
                }
            }
        }
    }
}
