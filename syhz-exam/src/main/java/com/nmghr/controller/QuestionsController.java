/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nmghr.controller;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.common.Result;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.util.ValidationUtils;
import com.nmghr.controller.vo.ExamExcelFillGapVo;
import com.nmghr.controller.vo.ExamExcelJudgeVo;
import com.nmghr.controller.vo.ExamExcelMutiChoiceVo;
import com.nmghr.controller.vo.ExamExcelSimpleChoiceVo;
import com.sargeraswang.util.ExcelUtil.ExcelSheet;
import com.sargeraswang.util.ExcelUtil.ExcelUtil;
import com.sargeraswang.util.ExcelUtil.ExcelsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;
import java.util.jar.JarEntry;

/**
 * <功能描述/>
 *
 * @author zhanghang
 * @date 2019年4月29日 下午2:46:54 
 * @version 1.0   
 */
@RestController
@RequestMapping("/questions")
public class QuestionsController {
//  private static final Logger log = LoggerFactory.getLogger(LogExcelController.class);
  @Autowired
  @Qualifier("baseService")
  private IBaseService baseService;

  private static final Logger log = LoggerFactory.getLogger(QuestionsController.class);

  /*
  @param subjectCategoryId 模块Id
  @param type 0全部 1单选 2多选 3 填空 4 判断
   */
  @GetMapping("/list/{type}")
  @ResponseBody
  public Object questionsList(@PathVariable String type,@RequestParam Map<String, Object> params) throws Exception {
      //单选
      if("1".equals(type)){
        //查当前题库的所有单选
        params.put("subjectCategoryId",1);
        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMSIMPLECHOICEBYSUB");
        List<Map<String, Object>> list = (List<Map<String, Object>>) baseService.list(params);
        return list;
        }
      //当前题库所有多选
      if("2".equals(type)){
        params.put("subjectCategoryId",1);
        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMMUTICHOICEBYSUB");
        List<Map<String, Object>> list = (List<Map<String, Object>>) baseService.list(params);
        return list;
      }
        //当前题库所有填空
      if("3".equals(type)){
          params.put("subjectCategoryId",1);
          LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMFILLGAPSBYSUB");
          List<Map<String, Object>> list = (List<Map<String, Object>>) baseService.list(params);
          return list;
      }
      //当前题库所有判断
      if("4".equals(type)){
          params.put("subjectCategoryId",1);
          LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMJUDGEBYSUB");
          List<Map<String, Object>> list = (List<Map<String, Object>>) baseService.list(params);
          return list;
      }
      //所有题目
      if("0".equals(type)){
          params.put("subjectCategoryId",1);
          List<Map<String, Object>> questions = new ArrayList<>();
          LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMSIMPLECHOICEBYSUB");
          List<Map<String, Object>> simpleChoiceList = (List<Map<String, Object>>) baseService.list(params);
          LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMMUTICHOICEBYSUB");
          List<Map<String, Object>> mutiChoiceList = (List<Map<String, Object>>) baseService.list(params);
          LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMFILLGAPSBYSUB");
          List<Map<String, Object>> fillGapsList = (List<Map<String, Object>>) baseService.list(params);
          LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMJUDGEBYSUB");
          List<Map<String, Object>> judgeList = (List<Map<String, Object>>) baseService.list(params);
          questions.addAll(simpleChoiceList);
          questions.addAll(mutiChoiceList);
          questions.addAll(fillGapsList);
          questions.addAll(judgeList);
          return  questions;
      }
    return new ArrayList<>();
  }

//试题详情
    /*
    @param id:id
    @param type:题目类型 1单选2多选3填空4判断
     */
    @GetMapping("/questionbyid")
    @ResponseBody
    public Object detail(@RequestParam Map<String, Object> params) throws Exception {
        validParams(params);
        if("1".equals(String.valueOf(params.get("type"))) || "2".equals(String.valueOf(params.get("type")))){
            //查单选与多选
            LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMCHOICE");
            Map<String, Object> choices = (Map<String, Object>) baseService.get(params);
            if(choices!=null && choices.get("id")!=null){
                //查选项
                LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMPOINTBYCHOICEID");
                List<Map<String, Object>> points = (List<Map<String, Object>>) baseService.list(params);

                //组合题和选项
                choices.put("points",points);
               return  choices;
            }
            return  new HashMap<>();
        }
        if("3".equals(String.valueOf(params.get("type")))) {
            //填空
            LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMFILLGAPS");
            Map<String, Object> fillGaps = (Map<String, Object>) baseService.get(params);
            if(fillGaps!=null){
                return fillGaps;
            }
            return new HashMap<>();
        }
        if("4".equals(String.valueOf(params.get("type")))) {
            //判断
            LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMJUDGE");
            Map<String, Object> judges = (Map<String, Object>) baseService.get(params);
            if(judges!=null){
                return judges;
            }
            return  new HashMap<>();
        }
        return new HashMap<>();
    }

    private void validParams(Map<String, Object> requestBody) {
        ValidationUtils.notNull(requestBody.get("id"), "题目Id不能为空!");
        ValidationUtils.notNull(requestBody.get("type"), "题目类型不能为空!");
    }



    /**
   * @mathod 试题Excel导入
   * @param mulFile 导入文件 前端导入文件名称为 file
   * @Param type 分类
   *
   *
   *
   **/
  @PostMapping(value = "/examUploadFile")
  @ResponseBody
  public Object uploadFile(@RequestParam("file") MultipartFile mulFile,
                           @RequestParam("type") String type, HttpServletRequest request) {
    log.info("excel uploadFile file start {}{}", mulFile, type);
    Map<String, Object> result = new HashMap<String, Object>();
    try {
        List<Class> classes = new ArrayList<>();
        //加入各个实体类对应class
        classes.add(ExamExcelSimpleChoiceVo.class);
        classes.add(ExamExcelMutiChoiceVo.class);
        classes.add(ExamExcelFillGapVo.class);
        classes.add(ExamExcelJudgeVo.class);
        List<ExamExcelSimpleChoiceVo> simpleChoiceVos = new ArrayList<>();
        List<ExamExcelMutiChoiceVo> mutiChoiceVos = new ArrayList<>();
        List<ExamExcelFillGapVo> fillGapVos = new ArrayList<>();
        List<ExamExcelJudgeVo> judgeVos = new ArrayList<>();
        Collection<Object> lists = ExcelsUtil.importExcel(classes, mulFile.getInputStream(), 0);
        //装入四个集合
        for (Object vo : lists) {
           if(vo.getClass() == ExamExcelSimpleChoiceVo.class){
              simpleChoiceVos.add((ExamExcelSimpleChoiceVo) vo);
           }
            if(vo.getClass() == ExamExcelMutiChoiceVo.class){
                mutiChoiceVos.add((ExamExcelMutiChoiceVo) vo);
            }
            if(vo.getClass() == ExamExcelFillGapVo.class){
                fillGapVos.add((ExamExcelFillGapVo) vo);
            }
            if(vo.getClass() == ExamExcelJudgeVo.class){
                judgeVos.add((ExamExcelJudgeVo) vo);
            }
        }
        //去除第一行即为标题行
        simpleChoiceVos.remove(0);
        mutiChoiceVos.remove(0);
        fillGapVos.remove(0);
        judgeVos.remove(0);
        //获取数据校验消息集合 若返回值为NULL，则校验通过
        List<String> simpleChoiceMsgList = (List<String>) checkSimpleChoiceData(simpleChoiceVos);
        List<String> mutiChoiceMsgList = (List<String>) checkMutiChoiceData(mutiChoiceVos);
        List<String> fillGapMsgList = (List<String>) checkFillGapData(fillGapVos);
        List<String> judgeMsgList = (List<String>) checkJudgeData(judgeVos);
        if(simpleChoiceMsgList!=null && simpleChoiceMsgList.size() > 0){
            return Result.fail("99998",simpleChoiceMsgList.get(0));
        }
        if(mutiChoiceMsgList!=null && mutiChoiceMsgList.size() > 0){
            return Result.fail("99998",mutiChoiceMsgList.get(0));
        }
        if(fillGapMsgList!=null && fillGapMsgList.size() > 0){
            return Result.fail("99998",fillGapMsgList.get(0));
        }
        if(judgeMsgList!=null && judgeMsgList.size() > 0){
            return Result.fail("99998",judgeMsgList.get(0));
        }
        //入库单选
        saveSimpleChoice(simpleChoiceVos);
        //入库多选
        saveMutiChoice(mutiChoiceVos);
        //入库填空
        saveFillGap(fillGapVos);
        //入库判断
        saveJudge(judgeVos);

    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (Exception e) {
      // TODO Auto-generated catch block
      log.error("excel uploadFile error", e.getMessage());
      e.printStackTrace();
    }
    return Result.ok(null);
  }


  //入库判断并建立关系
    private void saveJudge(List<ExamExcelJudgeVo> judgeVos) throws Exception{
        //填空题表Map
        Map<String, Object> judgeMap = new HashMap<>();
        for (ExamExcelJudgeVo judgeVo : judgeVos) {
            //题目名称
            judgeMap.put("subjectName",judgeVo.getContent());
            //答案
            judgeMap.put("answer",judgeVo.getAnswer());
            //题目解析
            judgeMap.put("analysis",judgeVo.getAnswerReason());
            //来源
            judgeMap.put("source",judgeVo.getFrom());
            //排序
            judgeMap.put("sort",judgeVo.getOrder());
            //入库
            LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMJUDGE");
            Object saveId = baseService.save(judgeMap);
            Integer Id = (Integer) saveId;

            //建立题库和题目的关系
            //题库试题映射表Map
            //INSERT INTO `exam_subject_category_mapping` (`subject_category_mapping_id`, `subject_category_id`, `questions_id`, `type`, `creator`, `create_date`, `modifier`, `modify_date`, `del_flag`)VALUES
            //(#{id}, #{subjectCategoryId}, #{questionsId}, #{type}, #{creator}, NOW(), NULL, NULL, 0);
            Map<String,Object> mapping = new HashMap<>();
            //题库科目Id
            mapping.put("subjectCategoryId",1);
            //试题Id
            mapping.put("questionsId",Id);
            mapping.put("type",4);
            mapping.put("creator","创建人账号");
            LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMSUBJECTCATEGORYMAPPING");
            baseService.save(mapping);
        }
    }

    //入库填空并建立关系
    private void saveFillGap(List<ExamExcelFillGapVo> fillGapVos ) throws Exception {
        //填空题表Map
        Map<String, Object> fillGapMap = new HashMap<>();
        for (ExamExcelFillGapVo fillGapVo : fillGapVos) {
            //题目名称
            fillGapMap.put("subjectName",fillGapVo.getContent());
            //答案
            fillGapMap.put("answer",fillGapVo.getAnswer());
            //题目解析
            fillGapMap.put("analysis",fillGapVo.getAnswerReason());
            //来源
            fillGapMap.put("source",fillGapVo.getFrom());
            //排序
            fillGapMap.put("sort",fillGapVo.getOrder());
            //入库
            LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMFILLGAPS");
            Object saveId = baseService.save(fillGapMap);
            Integer Id = (Integer) saveId;

            //建立题库和题目的关系
            //题库试题映射表Map
            //INSERT INTO `exam_subject_category_mapping` (`subject_category_mapping_id`, `subject_category_id`, `questions_id`, `type`, `creator`, `create_date`, `modifier`, `modify_date`, `del_flag`)VALUES
            //(#{id}, #{subjectCategoryId}, #{questionsId}, #{type}, #{creator}, NOW(), NULL, NULL, 0);
            Map<String,Object> mapping = new HashMap<>();
            //题库科目Id
            mapping.put("subjectCategoryId",1);
            //试题Id
            mapping.put("questionsId",Id);
            mapping.put("type",3);
            mapping.put("creator","创建人账号");
            LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMSUBJECTCATEGORYMAPPING");
            baseService.save(mapping);
        }
    }
    //入库多选并建立关系
    private void saveMutiChoice(List<ExamExcelMutiChoiceVo> mutiChoiceVos) throws Exception {
        //多选题表Map
        Map<String, Object> mutiChoiceMap = new HashMap<>();
        //选项表Map
        Map<String, Object> mutiChoicePointMap = new HashMap<>();
        for (ExamExcelMutiChoiceVo mutiChoiceVo : mutiChoiceVos) {
            //题目名称
            mutiChoiceMap.put("subjectName", mutiChoiceVo.getContent());
            //题目类型 2多选
            mutiChoiceMap.put("choicesType", 2);
            //答案
            mutiChoiceMap.put("answer", mutiChoiceVo.getAnswer());
            //题目解析
            mutiChoiceMap.put("analysis", mutiChoiceVo.getAnswerReason());
            //来源
            mutiChoiceMap.put("source", mutiChoiceVo.getFrom());
            //排序
            mutiChoiceMap.put("sort", mutiChoiceVo.getOrder());
            //入库
            LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMCHOICES");
            Object saveId = baseService.save(mutiChoiceMap);

            Integer choicesId = (Integer) saveId;
            mutiChoiceMap.clear();
            //建立与选项表的关系
            mutiChoicePointMap.put("choicesId", choicesId);
            mutiChoicePointMap.put("pointValue", mutiChoiceVo.getChoiceA());
            mutiChoicePointMap.put("point", "A");
            LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMCHOICESPOINT");
            baseService.save(mutiChoicePointMap);
            mutiChoicePointMap.clear();
            mutiChoicePointMap.put("choicesId", choicesId);
            mutiChoicePointMap.put("pointValue", mutiChoiceVo.getChoiceB());
            mutiChoicePointMap.put("point", "B");
            LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMCHOICESPOINT");
            baseService.save(mutiChoicePointMap);
            mutiChoicePointMap.clear();
            mutiChoicePointMap.put("choicesId", choicesId);
            mutiChoicePointMap.put("pointValue", mutiChoiceVo.getChoiceC());
            mutiChoicePointMap.put("point", "C");
            LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMCHOICESPOINT");
            baseService.save(mutiChoicePointMap);
            mutiChoicePointMap.clear();
            mutiChoicePointMap.put("choicesId", choicesId);
            mutiChoicePointMap.put("pointValue",mutiChoiceVo.getChoiceD());
            mutiChoicePointMap.put("point","D");
            LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMCHOICESPOINT");
            baseService.save(mutiChoicePointMap);
            mutiChoicePointMap.clear();

            //建立题库和题目的关系
            //题库试题映射表Map
            //INSERT INTO `exam_subject_category_mapping` (`subject_category_mapping_id`, `subject_category_id`, `questions_id`, `type`, `creator`, `create_date`, `modifier`, `modify_date`, `del_flag`)VALUES
            //(#{id}, #{subjectCategoryId}, #{questionsId}, #{type}, #{creator}, NOW(), NULL, NULL, 0);
            Map<String,Object> mapping = new HashMap<>();
            //题库科目Id
            mapping.put("subjectCategoryId",1);
            //试题Id
            mapping.put("questionsId",choicesId);
            mapping.put("type",2);
            mapping.put("creator","创建人账号");
            LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMSUBJECTCATEGORYMAPPING");
            baseService.save(mapping);
        }
    }
    //入库单选并建立关系
    private void saveSimpleChoice(List<ExamExcelSimpleChoiceVo> simpleChoiceVos) throws Exception {
        //单选题表Map
        Map<String,Object> simpleChoiceMap = new HashMap<>();
        //选项表Map
        Map<String,Object> simpleChoicePointMap = new HashMap<>();

        for (ExamExcelSimpleChoiceVo simpleChoiceVo : simpleChoiceVos) {
            //题目名称
            simpleChoiceMap.put("subjectName",simpleChoiceVo.getContent());
            //题目类型 1单选
            simpleChoiceMap.put("choicesType",1);
            //答案
            simpleChoiceMap.put("answer",simpleChoiceVo.getAnswer());
            //题目解析
            simpleChoiceMap.put("analysis",simpleChoiceVo.getAnswerReason());
            //来源
            simpleChoiceMap.put("source",simpleChoiceVo.getFrom());
            //排序
            simpleChoiceMap.put("sort",simpleChoiceVo.getOrder());
            //入库
            LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMCHOICES");
            Object saveId = baseService.save(simpleChoiceMap);
            Integer choicesId = (Integer)saveId;
            simpleChoiceMap.clear();
            //建立与选项表的关系
            simpleChoicePointMap.put("choicesId", choicesId);
            simpleChoicePointMap.put("pointValue", simpleChoiceVo.getChoiceA());
            simpleChoicePointMap.put("point", "A");
            LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMCHOICESPOINT");
            baseService.save(simpleChoicePointMap);
            simpleChoicePointMap.clear();
            simpleChoicePointMap.put("choicesId", choicesId);
            simpleChoicePointMap.put("pointValue", simpleChoiceVo.getChoiceB());
            simpleChoicePointMap.put("point", "B");
            LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMCHOICESPOINT");
            baseService.save(simpleChoicePointMap);
            simpleChoicePointMap.clear();
            simpleChoicePointMap.put("choicesId", choicesId);
            simpleChoicePointMap.put("pointValue", simpleChoiceVo.getChoiceC());
            simpleChoicePointMap.put("point", "C");
            LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMCHOICESPOINT");
            baseService.save(simpleChoicePointMap);
            simpleChoicePointMap.clear();
            simpleChoicePointMap.put("choicesId", choicesId);
            simpleChoicePointMap.put("pointValue",simpleChoiceVo.getChoiceD());
            simpleChoicePointMap.put("point","D");
            LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMCHOICESPOINT");
            baseService.save(simpleChoicePointMap);
            simpleChoicePointMap.clear();
            //建立题库和题目的关系
            //题库试题映射表Map
            //INSERT INTO `exam_subject_category_mapping` (`subject_category_mapping_id`, `subject_category_id`, `questions_id`, `type`, `creator`, `create_date`, `modifier`, `modify_date`, `del_flag`)VALUES
            //(#{id}, #{subjectCategoryId}, #{questionsId}, #{type}, #{creator}, NOW(), NULL, NULL, 0);
            Map<String,Object> mapping = new HashMap<>();
            //题库科目Id
            mapping.put("subjectCategoryId",1);
            //试题Id
            mapping.put("questionsId",choicesId);
            mapping.put("type",1);
            mapping.put("creator","创建人账号");
            LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMSUBJECTCATEGORYMAPPING");
            baseService.save(mapping);
        }
    }
    //校验判断题必填项的方法
    private Object checkJudgeData(List<ExamExcelJudgeVo> judgeList) {
        List<String> judgeMsgList = new ArrayList<>();
        for (int i = 0; i < judgeList.size(); i++) {
            ExamExcelJudgeVo vo = judgeList.get(i);
            if(vo.getContent() == null){
                judgeMsgList.add("判断题"+ (i+1)+"行"+"题目内容为空");
                return judgeMsgList;
            }
            if(vo.getAnswer() == null){
                judgeMsgList.add("判断题"+(i+1)+"行"+"正确答案为空");
                return judgeMsgList;
            }
            if(vo.getFrom() == null){
                judgeMsgList.add("判断题"+(i+1)+"行"+"出处为空");
                return judgeMsgList;
            }
        }

     return null;
    }
    //校验填空题必填项的方法
    private Object checkFillGapData(List<ExamExcelFillGapVo> fillGapList) {
        List<String> fillGapMsgList = new ArrayList<>();
        for (int i = 0; i < fillGapList.size(); i++) {
            ExamExcelFillGapVo vo = fillGapList.get(i);
            if(vo.getContent() == null){
                fillGapMsgList.add("填空题"+ (i+1)+"行"+"题目内容为空");
                return fillGapMsgList;
            }
            if(vo.getAnswer() == null){
                fillGapMsgList.add("填空题"+(i+1)+"行"+"正确答案为空");
                return fillGapMsgList;
            }
            if(vo.getFrom() == null){
                fillGapMsgList.add("填空题"+(i+1)+"行"+"出处为空");
                return fillGapList;
            }
        }


      return null;
    }
    //校验多选题必填项的方法
    private Object checkMutiChoiceData(List<ExamExcelMutiChoiceVo> mutiChoiceList) {
        List<String> mutiChoiceMsgList = new ArrayList<>();
        for (int i = 0; i < mutiChoiceList.size(); i++) {
            ExamExcelMutiChoiceVo vo = mutiChoiceList.get(i);
            if(vo.getContent() == null){
                mutiChoiceMsgList.add("多选题"+ (i+1)+"行"+"题目内容为空");
                return mutiChoiceMsgList;
            }
            if(vo.getChoiceA() == null){
                mutiChoiceMsgList.add("多选题"+(i+1)+"行"+"选项A内容为空");
                return mutiChoiceMsgList;
            }
            if(vo.getChoiceB() == null){
                mutiChoiceMsgList.add("多选题"+(i+1)+"行"+"选项B内容为空");
                return mutiChoiceMsgList;
            }
            if(vo.getChoiceC() == null){
                mutiChoiceMsgList.add((i+1)+"行"+"选项C内容为空");
                return mutiChoiceMsgList;
            }
            if(vo.getChoiceD() == null){
                mutiChoiceMsgList.add("多选题"+(i+1)+"行"+"选项D内容为空");
                return mutiChoiceMsgList;
            }
            if(vo.getAnswer() == null){
                mutiChoiceMsgList.add("多选题"+(i+1)+"行"+"答案为空");
                return mutiChoiceMsgList;
            }
            //校验答案格式
            //if(vo.getAnswer().)
            if(vo.getFrom() == null){
                mutiChoiceMsgList.add("多选题"+(i+1)+"行"+"出处为空");
                return mutiChoiceMsgList;
            }
        }
        return null;
    }
    //校验单选题必填项的方法
    private Object checkSimpleChoiceData(List<ExamExcelSimpleChoiceVo> simpleChoiceList) {
        List<String> simpleChoiceMsgList = new ArrayList<>();
        for (int i = 0; i < simpleChoiceList.size(); i++) {
            ExamExcelSimpleChoiceVo vo = simpleChoiceList.get(i);
            if(vo.getContent() == null){
                simpleChoiceMsgList.add("单选题"+ (i+1)+"行"+"题目内容为空");
                return simpleChoiceMsgList;
            }
            if(vo.getChoiceA() == null){
                simpleChoiceMsgList.add("单选题"+ (i+1)+"行"+"选项A内容为空");
                return simpleChoiceMsgList;
            }
            if(vo.getChoiceB() == null){
                simpleChoiceMsgList.add("单选题"+ (i+1)+"行"+"选项B内容为空");
                return simpleChoiceMsgList;
            }
            if(vo.getChoiceC() == null){
                simpleChoiceMsgList.add("单选题"+ (i+1)+"行"+"选项C内容为空");
                return simpleChoiceMsgList;
            }
            if(vo.getChoiceD() == null){
                simpleChoiceMsgList.add("单选题"+ (i+1)+"行"+"选项D内容为空");
                return simpleChoiceMsgList;
            }
            if(vo.getAnswer() == null){
                simpleChoiceMsgList.add("单选题"+ (i+1)+"行"+"答案为空");
                return simpleChoiceMsgList;
            }
            if(vo.getFrom() == null){
                simpleChoiceMsgList.add("单选题"+ (i+1)+"行"+"出处为空");
                return simpleChoiceMsgList;
            }
        }
        return null;
    }
}
