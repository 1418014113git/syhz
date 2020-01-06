package com.nmghr.hander.save;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.common.exception.GlobalErrorException;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractSaveHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("questionSaveHandler")
public class ExamQuestionsSaveHandler extends AbstractSaveHandler {

    private Logger log = LoggerFactory.getLogger(ExamQuestionsSaveHandler.class);
    public ExamQuestionsSaveHandler(IBaseService baseService) {
        super(baseService);
    }

    @Transactional
    @Override
    public Object save(Map<String, Object> requestBody) throws Exception {
        validParams(requestBody);
        try {
            if ("1".equals(String.valueOf(requestBody.get("type"))) || "2".equals(String.valueOf(requestBody.get("type")))) {
               if("1".equals(String.valueOf(requestBody.get("type")))) {
                   //检查试题名称是否重复,单选
                   LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMSIMPLECHOICEBYSUB");
                   Map<String, Object> checkRepeatMap = new HashMap<>();
                   checkRepeatMap.put("subjectName", String.valueOf(requestBody.get("subjectName")));
                   checkRepeatMap.put("subjectCategoryId", requestBody.get("subjectCategoryId"));
                   List<Map<String, Object>> list = (List<Map<String, Object>>) baseService.list(checkRepeatMap);
                   if (list != null && list.size() > 0) {
                       throw new GlobalErrorException("998001", "试题名称重复!");
                   }
               }
               if("2".equals(String.valueOf(requestBody.get("type")))){
                   //检查试题名称是否重复,多选
                   LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMMUTICHOICEBYSUB");
                   Map<String, Object> checkRepeatMap = new HashMap<>();
                   checkRepeatMap.put("subjectName", String.valueOf(requestBody.get("subjectName")));
                   checkRepeatMap.put("subjectCategoryId", requestBody.get("subjectCategoryId"));
                   List<Map<String, Object>> list = (List<Map<String, Object>>) baseService.list(checkRepeatMap);
                   if (list != null && list.size() > 0) {
                       throw new GlobalErrorException("998001", "试题名称重复!");
                   }

               }
                LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMCHOICES");
                Object saveId = baseService.save(requestBody);
                //选项Map
                Map<String, Object> choicePointMap = new HashMap<>();
                //遍历参数中的选项数组
                Map<String, Object> points = (Map<String, Object>) requestBody.get("points");
                for (String s : points.keySet()) {
                    Object pointValue = points.get(s);
                    if (pointValue != null && !"".equals(pointValue)) {
                        choicePointMap.put("choicesId", saveId);
                        choicePointMap.put("pointValue", pointValue);
                        choicePointMap.put("point", s.substring(s.length() - 1));
                        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMCHOICESPOINT");
                        baseService.save(choicePointMap);
                    }
                }
                //维护题库试题关系表
                saveMappings(requestBody, saveId);
                return true;
            } else {
                if ("3".equals(String.valueOf(requestBody.get("type")))) {

                    //检查试题名称是否重复,填空
                        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMFILLGAPSBYSUB");
                    Map<String, Object> checkRepeatMap = new HashMap<>();
                    checkRepeatMap.put("subjectName", String.valueOf(requestBody.get("subjectName")));
                    checkRepeatMap.put("subjectCategoryId", requestBody.get("subjectCategoryId"));
                    List<Map<String, Object>> list = (List<Map<String, Object>>) baseService.list(checkRepeatMap);
                    if (list != null && list.size() > 0) {
                        throw new GlobalErrorException("998001", "试题名称重复!");
                    }
                    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMFILLGAPS");
                    Object saveId = baseService.save(requestBody);
                    saveMappings(requestBody, saveId);
                    return true;
                }
                if ("4".equals(String.valueOf(requestBody.get("type")))) {
                    //检查试题名称是否重复,判断
                    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMJUDGEBYSUB");
                    Map<String, Object> checkRepeatMap = new HashMap<>();
                    checkRepeatMap.put("subjectName", String.valueOf(requestBody.get("subjectName")));
                    checkRepeatMap.put("subjectCategoryId", requestBody.get("subjectCategoryId"));
                    List<Map<String, Object>> list = (List<Map<String, Object>>) baseService.list(checkRepeatMap);
                    if (list != null && list.size() > 0) {
                        throw new GlobalErrorException("998001", "试题名称重复!");
                    }

                    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMJUDGE");
                    Object saveId = baseService.save(requestBody);
                    saveMappings(requestBody, saveId);
                    return true;
                }
                if ("5".equals(String.valueOf(requestBody.get("type")))
                        || "6".equals(String.valueOf(requestBody.get("type")))
                        || "7".equals(String.valueOf(requestBody.get("type")))) {
                    //检查试题名称是否重复,简答论述案例分析
                    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMDISCUSSBYSUBANDTYPE");
                    Map<String, Object> checkRepeatMap = new HashMap<>();
                    checkRepeatMap.put("subjectName", String.valueOf(requestBody.get("subjectName")));
                    checkRepeatMap.put("subjectCategoryId", requestBody.get("subjectCategoryId"));
                    checkRepeatMap.put("type",requestBody.get("type"));
                    List<Map<String, Object>> list = (List<Map<String, Object>>) baseService.list(checkRepeatMap);
                    if (list != null && list.size() > 0) {
                        throw new GlobalErrorException("998001", "试题名称重复!");
                    }
                    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMDISCUSS");
                    Object saveId = baseService.save(requestBody);
                    saveMappings(requestBody, saveId);
                    return true;
                }
            }
        }catch (Exception e){
            log.error("questionSaveHandler ERROR : " + e.getMessage());
            throw new GlobalErrorException("999996", e.getMessage());
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
        if(requestBody.get("creator") == null || "".equals(requestBody.get("creator"))){
            throw new GlobalErrorException("998001", "创建人不能为空!");
        }
        if(requestBody.get("deptCode") == null || "".equals(requestBody.get("deptCode"))){
            throw new GlobalErrorException("998001", "区域编号不能为空!");
        }
        if(requestBody.get("deptName") == null || "".equals(requestBody.get("deptName"))){
            throw new GlobalErrorException("998001", "区域名称不能为空!");
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
        if(requestBody.get("source") != null && String.valueOf(requestBody.get("source")).length() > 1000){
            throw new GlobalErrorException("998001", "出处长度大于1000!");
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
