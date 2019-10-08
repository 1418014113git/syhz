package com.nmghr.hander.update;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.common.exception.GlobalErrorException;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractUpdateHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("examquestionUpdateHandler")
public class ExamQuestionsUpdateHandler extends AbstractUpdateHandler {
    public ExamQuestionsUpdateHandler(IBaseService baseService) {
        super(baseService);
    }
    @Transactional
    @Override
    public Object update(String id, Map<String, Object> requestBody) throws Exception {
        validParams(requestBody);
        Map<String,Object> resultMap = new HashMap<>();
        Map<String,Object> isInPaperParam = new HashMap();
        isInPaperParam.put("questionsId", id);
        //引用当前试题的所有正在考试的试卷,取距当前时间最近的试卷
        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMQUESTIONINPAPER");
        Map<String,Object> map = (Map<String, Object>) baseService.get(isInPaperParam);
        if(map!=null){
            //存在正在考试的试卷
            String paperName = String.valueOf(map.get("paperName"));
            resultMap.put("type","1");
            resultMap.put("paperName",paperName);
            return resultMap;
        }

        if("1".equals(String.valueOf(requestBody.get("type"))) || "2".equals(String.valueOf(requestBody.get("type")))) {
            if("1".equals(String.valueOf(requestBody.get("type")))) {
                //检查试题名称是否重复,单选
                LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMSIMPLECHOICEBYSUB");
                Map<String, Object> checkRepeatMap = new HashMap<>();
                checkRepeatMap.put("subjectName", String.valueOf(requestBody.get("subjectName")));
                checkRepeatMap.put("subjectCategoryId", requestBody.get("subjectCategoryId"));
                List<Map<String, Object>> list = (List<Map<String, Object>>) baseService.list(checkRepeatMap);
                if (list != null && list.size() > 0) {
                    if(!(list.get(0).get("id").equals(id)))
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


            //更新选择题
            LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMCHOICES");
            Object updateInfo = baseService.update(id,requestBody);
            //删除当前题对应的选项
            LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMPOINTBYCHOICEID");
            Map<String,Object> param = new HashMap<>();
            param.put("choicesId",id);
            baseService.remove(param);
            //重新添加选项
            Map<String,Object> points = (Map<String, Object>) requestBody.get("points");
            Map<String,Object> choicePointMap = new HashMap<>();
            for (String s : points.keySet()) {
                Object pointValue = points.get(s);
                if(pointValue!=null &&!"".equals(pointValue)) {
                    choicePointMap.put("choicesId", id);
                    choicePointMap.put("pointValue", pointValue);
                    choicePointMap.put("point", s.substring(s.length() - 1));
                    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMCHOICESPOINT");
                    baseService.save(choicePointMap);
                }
            }
            return updateInfo;
        }
        else{
                if("3".equals(String.valueOf(requestBody.get("type")))) {
                    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMFILLGAPS");
                    Object updateInfo = baseService.update(id,requestBody);
                    return updateInfo;
                }
                if("4".equals(String.valueOf(requestBody.get("type")))) {
                    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMJUDGE");
                    Object updateInfo = baseService.update(id,requestBody);
                    return updateInfo;
                }
                if("5".equals(String.valueOf(requestBody.get("type")))
                        ||"6".equals(String.valueOf(requestBody.get("type")))
                        ||"7".equals(String.valueOf(requestBody.get("type")))) {
                LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMDISCUSS");
                Object updateInfo = baseService.update(id,requestBody);
                return updateInfo;
                }
        }
        return true;
    }

//    private Object checkInPaper(String id) throws Exception {
//        //判断是否有引用
//        Map<String,Object> isInPaperParam = new HashMap();
//        isInPaperParam.put("questionsId", id);
//        //引用当前试题的所有正在考试的试卷,取距当前时间最近的试卷
//        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMQUESTIONINPAPER");
//        Map<String,Object> map = (Map<String, Object>) baseService.get(isInPaperParam);
//        if(map!=null){
//            //存在正在考试的试卷
//            String paperName = String.valueOf(map.get("paperName"));
//            return Result.fail("998001","该试题已经被抽取到"+paperName+"试卷中，暂时不能编辑或删除！");
//        }
//        //引用当前试题的考过的试卷
//        Map<String,Object> isInBeforePaperParam = new HashMap();
//        isInBeforePaperParam.put("questionsId", id);
//        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMQUESTIONINPAPERBEFORE");
//        List<Map<String,Object>> list = (List<Map<String,Object>>) baseService.list(isInBeforePaperParam);
//        if(list!= null && list.size() > 0){
//            //已经考过的卷子存在试题的引用
//            return Result.fail("998001","该试题在已结束的考试试卷中有使用,如果修改或删除可能会影响到警员查看以往考试信息！");
//        }
//        return null;
//    }

    private void validParams(Map<String, Object> requestBody) {

        if(requestBody.get("type") == null || "".equals(requestBody.get("type"))){
            throw new GlobalErrorException("998001", "试题类型不能为空!");
        }
        if(requestBody.get("subjectName") == null || "".equals(requestBody.get("subjectName"))){
            throw new GlobalErrorException("998001", "请输入题目内容!");
        }
        if(requestBody.get("creator") == null || "".equals(requestBody.get("creator"))){
            throw new GlobalErrorException("998001", "创建人不能为空!");
        }
        if(requestBody.get("deptCode") == null || "".equals(requestBody.get("deptCode"))){
            throw new GlobalErrorException("998001", "地区编号不能为空!");
        }
        if(requestBody.get("deptName") == null || "".equals(requestBody.get("deptName"))){
            throw new GlobalErrorException("998001", "地区名称不能为空!");
        }
        String type = String.valueOf(requestBody.get("type"));
        {
            if("1".equals(type) || ("2".equals(type)) || ("3".equals(type)) || ("4".equals(type))){
                if(requestBody.get("answer") == null || "".equals(requestBody.get("answer"))){
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
