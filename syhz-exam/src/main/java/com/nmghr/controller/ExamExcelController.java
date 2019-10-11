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
import com.nmghr.basic.common.exception.GlobalErrorException;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.controller.vo.*;
import com.nmghr.hander.save.ExamQuestionsSaveHandler;
import com.sargeraswang.util.ExcelUtil.ExcelsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * <功能描述/>
 *
 * @author zhanghang
 * @date 2019年4月29日 下午2:46:54 
 * @version 1.0   
 */
@RestController
@RequestMapping("/exam")
public class ExamExcelController {
//  private static final Logger log = LoggerFactory.getLogger(LogExcelController.class);
  @Autowired
  @Qualifier("baseService")
  private IBaseService baseService;
  @Autowired
  ExamQuestionsSaveHandler questionsSaveHandler;

  private static final Logger log = LoggerFactory.getLogger(ExamExcelController.class);
  @PostMapping(value = "/examUploadFile")
  @ResponseBody
  public Object uploadFile(@RequestParam("file") MultipartFile mulFile,
                           @RequestParam("subjectCategoryId") String subjectCategoryId,
                           @RequestParam("creator") String creator,
                           @RequestParam("deptCode") String deptCode,
                           @RequestParam("deptName") String deptName,
                           HttpServletRequest request

  ) {
    log.info("excel uploadFile file start {}{}", mulFile);
    if(subjectCategoryId == null || "".equals(subjectCategoryId)){
        throw new GlobalErrorException("998001", "模块Id不能为空!");
    }
      if(creator == null || "".equals(creator)){
          throw new GlobalErrorException("998001", "创建人不能为空!");
      }
      if(deptCode == null || "".equals(deptCode)){
          throw new GlobalErrorException("998001", "区域编码不能为空!");
      }


    try {
        List<Class> classes = new ArrayList<>();
        //加入各个实体类对应class
        classes.add(ExamExcelSimpleChoiceVo.class);
        classes.add(ExamExcelMutiChoiceVo.class);
        classes.add(ExamExcelFillGapVo.class);
        classes.add(ExamExcelJudgeVo.class);
        //简答题
        classes.add(ExamExcelEasyQuestionVo.class);
        //论述
        classes.add(ExamExcelDiscussVo.class);
        //案例分析
        classes.add(ExamExcelCaseAnalysisVo.class);

        List<ExamExcelSimpleChoiceVo> simpleChoiceVos = new ArrayList<>();
        List<ExamExcelMutiChoiceVo> mutiChoiceVos = new ArrayList<>();
        List<ExamExcelFillGapVo> fillGapVos = new ArrayList<>();
        List<ExamExcelJudgeVo> judgeVos = new ArrayList<>();
        //简答题
        List<ExamExcelEasyQuestionVo> easyQuestionVos = new ArrayList<>();
        //论述题
        List<ExamExcelDiscussVo> discussVos = new ArrayList<>();
        //案例分析
        List<ExamExcelCaseAnalysisVo> caseAnalysisVos = new ArrayList<>();

        Collection<Object> lists = ExcelsUtil.importExcel(classes, mulFile.getInputStream(), 0);

        if (lists.size() > 1000) {
            log.error("excel uploadFile error, Maximum length exceeds 1000 ");
            throw new GlobalErrorException("99952", "最多不能超过1000条");
        }
        //装入集合
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
            if(vo.getClass() == ExamExcelEasyQuestionVo.class){
                easyQuestionVos.add((ExamExcelEasyQuestionVo) vo);
            }
            if(vo.getClass() == ExamExcelDiscussVo.class){
                discussVos.add((ExamExcelDiscussVo) vo);
            }
            if(vo.getClass() == ExamExcelCaseAnalysisVo.class){
                caseAnalysisVos.add((ExamExcelCaseAnalysisVo) vo);
            }
        }
        //去除第一行即为标题行
        simpleChoiceVos.remove(0);
        mutiChoiceVos.remove(0);
        fillGapVos.remove(0);
        judgeVos.remove(0);
        easyQuestionVos.remove(0);
        discussVos.remove(0);
        caseAnalysisVos.remove(0);
        //获取数据校验消息集合 若返回值为NULL，则校验通过
        Map<String,Object> simpleChoiceMsgMap = (Map<String, Object>) checkSimpleChoiceData(simpleChoiceVos,subjectCategoryId);
        Map<String,Object> mutiChoiceMsgMap = (Map<String, Object>)checkMutiChoiceData(mutiChoiceVos,subjectCategoryId);
        Map<String,Object> fillGapMsgMap = (Map<String, Object>)checkFillGapData(fillGapVos,subjectCategoryId);
        Map<String,Object> judgeMsgMap = (Map<String, Object>)checkJudgeData(judgeVos,subjectCategoryId);
        Map<String,Object> easyQuestionMsgMap = (Map<String, Object>)checkEasyQuestionData(easyQuestionVos,subjectCategoryId);
        Map<String,Object> discussMsgMap = (Map<String, Object>)checkDiscussData(discussVos,subjectCategoryId);
        Map<String,Object> caseAnalysisMsgMap = (Map<String, Object>)checkAnalysisData(caseAnalysisVos,subjectCategoryId);


        List<Map<String,Object>> errors = new ArrayList<>();
        Map<String,Object> simpleChoiceError = (Map<String, Object>) simpleChoiceMsgMap.get("errors");
        if(simpleChoiceError !=null && simpleChoiceError.size() > 0) {
            errors.add(simpleChoiceMsgMap);
        }
        Map<String,Object> mutiChoiceError = (Map<String, Object>) mutiChoiceMsgMap.get("errors");
        if(mutiChoiceError !=null && mutiChoiceError.size() > 0) {
            errors.add(mutiChoiceMsgMap);
        }
        Map<String,Object> fillGapError = (Map<String, Object>) fillGapMsgMap.get("errors");
        if(fillGapError !=null && fillGapError.size() > 0) {
            errors.add(fillGapMsgMap);
        }
        Map<String,Object> judgeError = (Map<String, Object>) judgeMsgMap.get("errors");
        if(judgeError !=null && judgeError.size() > 0) {
            errors.add(judgeMsgMap);
        }
        Map<String,Object> easyQuestionError = (Map<String, Object>) easyQuestionMsgMap.get("errors");
        if(easyQuestionError !=null && easyQuestionError.size() > 0) {
            errors.add(easyQuestionMsgMap);
        }
        Map<String,Object> discussError = (Map<String, Object>) discussMsgMap.get("errors");
        if(discussError !=null && discussError.size() > 0) {
            errors.add(discussMsgMap);
        }
        Map<String,Object> caseAnalysisError = (Map<String, Object>) caseAnalysisMsgMap.get("errors");
        if(caseAnalysisError !=null && caseAnalysisError.size() > 0) {
            errors.add(caseAnalysisMsgMap);
        }
        //将异常返回前端
        if(errors!=null && errors.size() > 0){
            return errors;
        }
        else {
            Map<String,Object> baseParam = new HashMap<>();
            baseParam.put("subjectCategoryId",subjectCategoryId);
            baseParam.put("creator",creator);
            baseParam.put("deptCode",deptCode);
            baseParam.put("deptName",deptName);
            //入库单选
            saveSimpleChoice(simpleChoiceVos,baseParam);
            //入库多选
            saveMutiChoice(mutiChoiceVos,baseParam);
            //入库填空
            saveFillGap(fillGapVos,baseParam);
            //入库判断
            saveJudge(judgeVos,baseParam);
            //入库论述
            saveDiscuss(discussVos,baseParam);
            //入库简答
            saveEasyQuestion(easyQuestionVos,baseParam);
            //入库案例分析
            saveAnalysis(caseAnalysisVos,baseParam);
        }
    } catch (FileNotFoundException e) {
        log.error("excel uploadFile error", e.getMessage());
        throw new GlobalErrorException("99954", "上传文件为空");
    } catch (IllegalArgumentException e) {
        log.error("excel uploadFile error", e.getMessage());
        throw new GlobalErrorException("99951", "上传文件错误:"+e.getMessage());
    }
    catch (Exception e) {
        log.error("excel uploadFile error", e.getMessage());
        throw new GlobalErrorException("99950", "上传异常:"+e.getMessage());
    }
    return Result.ok(null);
  }

    private void saveAnalysis(List<ExamExcelCaseAnalysisVo> caseAnalysisVos,Map<String,Object> baseParamMap) throws Exception {
        //案例分析表Map
        Map<String, Object> caseAnalysisMap = new HashMap<>();

        for (ExamExcelCaseAnalysisVo caseAnalysisVo : caseAnalysisVos) {
            caseAnalysisMap.put("subjectCategoryId",baseParamMap.get("subjectCategoryId"));
            //创建人
            caseAnalysisMap.put("creator",baseParamMap.get("creator"));
            caseAnalysisMap.put("deptCode",baseParamMap.get("deptCode"));
            caseAnalysisMap.put("deptName",baseParamMap.get("deptName"));
            //题目名称
            caseAnalysisMap.put("subjectName",caseAnalysisVo.getContent());
            caseAnalysisMap.put("type","7");
            //题目解析
            if(caseAnalysisVo.getAnalysis() != null) {
                caseAnalysisMap.put("analysis", caseAnalysisVo.getAnalysis());
            }
            //来源
            caseAnalysisMap.put("source",caseAnalysisVo.getSource());//出处
            if(caseAnalysisVo.getSort() != null) {
                caseAnalysisMap.put("sort", caseAnalysisVo.getSort());
            }
            questionsSaveHandler.save(caseAnalysisMap);
        }
    }
    private void saveEasyQuestion(List<ExamExcelEasyQuestionVo> easyQuestionVos, Map<String,Object> baseParamMap) throws Exception {
        //简答表Map
        Map<String, Object> easyQuestionMap = new HashMap<>();
        for (int i = 0; i < easyQuestionVos.size(); i++) {
            ExamExcelEasyQuestionVo easyQuestionVo = easyQuestionVos.get(i);
            easyQuestionMap.put("subjectCategoryId",baseParamMap.get("subjectCategoryId"));
            //创建人
            easyQuestionMap.put("creator",baseParamMap.get("creator"));
            easyQuestionMap.put("deptCode",baseParamMap.get("deptCode"));
            easyQuestionMap.put("deptName",baseParamMap.get("deptName"));
            easyQuestionMap.put("subjectName",easyQuestionVo.getContent());
            easyQuestionMap.put("type","5");
            //题目解析
            if(easyQuestionVo.getAnalysis()!=null) {
                easyQuestionMap.put("analysis", easyQuestionVo.getAnalysis());
            }
            //来源
            easyQuestionMap.put("source",easyQuestionVo.getSource());//出处
            if(easyQuestionVo.getSort()!=null) {
                easyQuestionMap.put("sort", easyQuestionVo.getSort());
            }
            questionsSaveHandler.save(easyQuestionMap);
        }
    }

    private void saveDiscuss(List<ExamExcelDiscussVo> discussVos, Map<String,Object> baseParamMap) throws Exception {
        //论述表Map
        Map<String, Object> discussMap = new HashMap<>();
        for (int i = 0; i < discussVos.size(); i++) {
            ExamExcelDiscussVo examExcelDiscussVo = discussVos.get(i);
            discussMap.put("subjectCategoryId",baseParamMap.get("subjectCategoryId"));
            //创建人
            discussMap.put("creator",baseParamMap.get("creator"));
            discussMap.put("deptCode",baseParamMap.get("deptCode"));
            discussMap.put("deptName",baseParamMap.get("deptName"));
            discussMap.put("subjectName",examExcelDiscussVo.getContent());
            discussMap.put("type","6");
            //题目解析
            if(examExcelDiscussVo.getAnalysis()!=null) {
                discussMap.put("analysis", examExcelDiscussVo.getAnalysis());
            }
            //来源
            discussMap.put("source",examExcelDiscussVo.getSource());//出处
            if(examExcelDiscussVo.getSort()!=null) {
                discussMap.put("sort", examExcelDiscussVo.getSort());
            }
            questionsSaveHandler.save(discussMap);

        }
    }

    //入库判断并建立关系
    private void saveJudge(List<ExamExcelJudgeVo> judgeVos, Map<String,Object> baseParamMap) throws Exception{
        //填空题表Map
        Map<String, Object> judgeMap = new HashMap<>();
        for (ExamExcelJudgeVo judgeVo : judgeVos) {
            judgeMap.put("subjectCategoryId",baseParamMap.get("subjectCategoryId"));
            //创建人
            judgeMap.put("creator",baseParamMap.get("creator"));
            judgeMap.put("deptCode",baseParamMap.get("deptCode"));
            judgeMap.put("deptName",baseParamMap.get("deptName"));

            //题目名称
            judgeMap.put("subjectName",judgeVo.getContent());
            //创建人
            judgeMap.put("type","4");
            //答案
            if("正确".equals(judgeVo.getAnswer())){
                judgeMap.put("answer",1);
            }
            if("错误".equals(judgeVo.getAnswer())){
                judgeMap.put("answer",2);
            }
            if(judgeVo.getAnalysis()!=null) {
                //题目解析
                judgeMap.put("analysis", judgeVo.getAnalysis());
            }
            //来源
            judgeMap.put("source",judgeVo.getSource());
            if(judgeVo.getSort()!=null) {
                //排序
                judgeMap.put("sort", judgeVo.getSort());
            }
            questionsSaveHandler.save(judgeMap);
        }
    }

    //入库填空并建立关系
    private void saveFillGap(List<ExamExcelFillGapVo> fillGapVos, Map<String,Object> baseParamMap) throws Exception {
        //填空题表Map
        Map<String, Object> fillGapMap = new HashMap<>();
        for (ExamExcelFillGapVo fillGapVo : fillGapVos) {
            fillGapMap.put("subjectCategoryId",baseParamMap.get("subjectCategoryId"));
            //创建人
            fillGapMap.put("creator",baseParamMap.get("creator"));
            fillGapMap.put("deptCode",baseParamMap.get("deptCode"));
            fillGapMap.put("deptName",baseParamMap.get("deptName"));
            //题目名称
            fillGapMap.put("subjectName",fillGapVo.getContent());
            fillGapMap.put("type","3");
            //答案
            fillGapMap.put("answer",fillGapVo.getAnswer());
            if(fillGapVo.getAnalysis()!=null) {
                //题目解析
                fillGapMap.put("analysis", fillGapVo.getAnalysis());
            }
            //来源
            fillGapMap.put("source",fillGapVo.getSource());
            if(fillGapVo.getSort()!=null) {
                //排序
                fillGapMap.put("sort", fillGapVo.getSort());
            }
            questionsSaveHandler.save(fillGapMap);
        }
    }
    //入库多选并建立关系
    private void saveMutiChoice(List<ExamExcelMutiChoiceVo> mutiChoiceVos, Map<String,Object> baseParamMap) throws Exception {
        //多选题表Map
        Map<String, Object> mutiChoiceMap = new HashMap<>();
        for (ExamExcelMutiChoiceVo mutiChoiceVo : mutiChoiceVos) {
            //题库Id
            mutiChoiceMap.put("subjectCategoryId",baseParamMap.get("subjectCategoryId"));
            //创建人
            mutiChoiceMap.put("creator",baseParamMap.get("creator"));
            mutiChoiceMap.put("deptCode",baseParamMap.get("deptCode"));
            mutiChoiceMap.put("deptName",baseParamMap.get("deptName"));
            //题目名称
            mutiChoiceMap.put("subjectName", mutiChoiceVo.getContent());
            //题目类型 2多选
            mutiChoiceMap.put("type","2");
            //答案
            mutiChoiceMap.put("answer", mutiChoiceVo.getAnswer());
            if(mutiChoiceVo.getAnalysis()!=null) {
                //题目解析
                mutiChoiceMap.put("analysis", mutiChoiceVo.getAnalysis());
            }
            //来源
            mutiChoiceMap.put("source", mutiChoiceVo.getSource());
            if(mutiChoiceVo.getSort()!=null) {
                //排序
                mutiChoiceMap.put("sort", mutiChoiceVo.getSort());
            }
            //选项表Map
            Map<String, Object> mutiChoicePointMap = new HashMap<>();
            mutiChoicePointMap.put("pointA",mutiChoiceVo.getChoiceA());
            mutiChoicePointMap.put("pointB",mutiChoiceVo.getChoiceB());
            mutiChoicePointMap.put("pointC",mutiChoiceVo.getChoiceC());
            mutiChoicePointMap.put("pointD",mutiChoiceVo.getChoiceD());
            if(mutiChoiceVo.getChoiceE()!=null) {
                mutiChoicePointMap.put("pointE", mutiChoiceVo.getChoiceE());
            }
            if(mutiChoiceVo.getChoiceF() !=null) {
                mutiChoicePointMap.put("pointF", mutiChoiceVo.getChoiceF());
            }
            mutiChoiceMap.put("points", mutiChoicePointMap);
            questionsSaveHandler.save(mutiChoiceMap);
        }
    }
    //入库单选并建立关系
    private void saveSimpleChoice(List<ExamExcelSimpleChoiceVo> simpleChoiceVos,Map<String,Object> baseParamMap) throws Exception {

      String subjectCategoryId =  String.valueOf(baseParamMap.get("subjectCategoryId"));
      String creator = String.valueOf(baseParamMap.get("creator"));
      String deptCode = String.valueOf(baseParamMap.get("deptCode"));
      String deptName = String.valueOf(baseParamMap.get("deptName"));


        //单选题表Map
        Map<String,Object> simpleChoiceMap = new HashMap<>();
        for (ExamExcelSimpleChoiceVo simpleChoiceVo : simpleChoiceVos) {
            //题库Id
            simpleChoiceMap.put("subjectCategoryId",subjectCategoryId);
            //创建人
            simpleChoiceMap.put("creator",creator);
            //deptCode
            simpleChoiceMap.put("deptCode",deptCode);
            //deptName
            simpleChoiceMap.put("deptName",deptName);
            //题目名称
            simpleChoiceMap.put("subjectName",simpleChoiceVo.getContent());
            //题目类型 1单选
            simpleChoiceMap.put("type","1");
            //答案
            simpleChoiceMap.put("answer",simpleChoiceVo.getAnswer());
            if(simpleChoiceVo.getAnalysis()!=null) {
                //题目解析
                simpleChoiceMap.put("analysis", simpleChoiceVo.getAnalysis());
            }
            //来源
            simpleChoiceMap.put("source",simpleChoiceVo.getSource());
            //排序
            if(simpleChoiceVo.getSort()!=null) {
                simpleChoiceMap.put("sort", simpleChoiceVo.getSort());
            }
            //选项表Map
            Map<String,Object> simpleChoicePointMap = new HashMap<>();
            simpleChoicePointMap.put("pointA",simpleChoiceVo.getChoiceA());
            simpleChoicePointMap.put("pointB",simpleChoiceVo.getChoiceB());
            simpleChoicePointMap.put("pointC",simpleChoiceVo.getChoiceC());
            simpleChoicePointMap.put("pointD",simpleChoiceVo.getChoiceD());
            simpleChoiceMap.put("points",simpleChoicePointMap);
            questionsSaveHandler.save(simpleChoiceMap);
        }
    }
    //校验判断题必填项的方法
    private Object checkJudgeData(List<ExamExcelJudgeVo> judgeList, String subjectCategoryId) throws Exception {
        Map<String,Object> judgeMsgMap = new HashMap<>();
        List<String> msgArr = new ArrayList<>();
        Map<String,Object> lineMsgMap = new HashMap<>();
        for (int i = 0; i < judgeList.size(); i++) {
             msgArr.clear();
            ExamExcelJudgeVo vo = judgeList.get(i);
            if(vo.getContent() == null){
                msgArr.add("题目内容为空");
            }
            LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMJUDGEBYSUB");
            Map<String, Object> checkRepeatMap = new HashMap<>();
            checkRepeatMap.put("subjectName", String.valueOf(vo.getContent()));
            checkRepeatMap.put("subjectCategoryId", subjectCategoryId);
            List<Map<String, Object>> list = (List<Map<String, Object>>) baseService.list(checkRepeatMap);
            if (list != null && list.size() > 0) {
                msgArr.add("题目名称重复");
            }
            if(vo.getContent() != null && vo.getContent().length() > 200){
                msgArr.add("题目内容长度大于200");
            }
            if(vo.getAnswer() == null){
                msgArr.add("正确答案为空");
            }
            if(vo.getAnswer() != null){
                if(!"错误".equals(vo.getAnswer())){
                    if(!"正确".equals(vo.getAnswer())){
                        msgArr.add("正确答案必须为正确或错误");
                    }
                }
            }
            if(vo.getSource() == null){
                msgArr.add("出处为空");
            }
            if(vo.getSource() != null && vo.getSource().length() > 50){
                msgArr.add("出处长度大于50");
            }
            if(msgArr!=null && msgArr.size() > 0) {
                lineMsgMap.put(String.valueOf(i + 1), ((ArrayList<String>) msgArr).clone());
            }
        }
        judgeMsgMap.put("type","4");
        if(lineMsgMap.size() > 0) {
            judgeMsgMap.put("errors", lineMsgMap);
        }
        return judgeMsgMap;
    }
    //校验填空题必填项的方法
    private Object checkFillGapData(List<ExamExcelFillGapVo> fillGapList, String subjectCategoryId) throws Exception {
        Map<String,Object> fillGapMsgMap = new HashMap<>();
        List<String> msgArr = new ArrayList<>();
        Map<String,Object> lineMsgMap = new HashMap<>();
        for (int i = 0; i < fillGapList.size(); i++) {
            msgArr.clear();
            ExamExcelFillGapVo vo = fillGapList.get(i);
            if(vo.getContent() == null){
                msgArr.add("题目内容为空");
            }
            LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMFILLGAPSBYSUB");
            Map<String, Object> checkRepeatMap = new HashMap<>();
            checkRepeatMap.put("subjectName", String.valueOf(vo.getContent()));
            checkRepeatMap.put("subjectCategoryId", subjectCategoryId);
            List<Map<String, Object>> list = (List<Map<String, Object>>) baseService.list(checkRepeatMap);
            if (list != null && list.size() > 0) {
                msgArr.add("题目名称重复");
            }
            if(vo.getContent() != null && vo.getContent().length() > 200){
                msgArr.add("题目内容长度大于200");
            }
            if(vo.getAnswer() == null){
                msgArr.add("正确答案为空");
            }
            if(vo.getAnswer() != null &&vo.getAnswer().length() > 255){
                msgArr.add("正确答案长度大于255");
            }
            if(vo.getSource() == null){
                msgArr.add("出处为空");
            }
            if(vo.getSource() != null && vo.getSource().length() > 50){
                msgArr.add("出处长度大于50");
            }
           if(msgArr!=null && msgArr.size() > 0) {
               lineMsgMap.put(String.valueOf(i + 1), ((ArrayList<String>) msgArr).clone());
           }
        }
        fillGapMsgMap.put("type","3");
        if(lineMsgMap.size() > 0) {
            fillGapMsgMap.put("errors", lineMsgMap);
        }
      return fillGapMsgMap;
    }
    //校验多选题必填项的方法
    private Object checkMutiChoiceData(List<ExamExcelMutiChoiceVo> mutiChoiceList, String subjectCategoryId) throws Exception {
        Map<String,Object> mutiChoiceMsgMap = new HashMap<>();
        Map<String,Object> lineMsgMap = new HashMap<>();
        List<String> msgArr = new ArrayList<>();
        for (int i = 0; i < mutiChoiceList.size(); i++) {
            msgArr.clear();
            ExamExcelMutiChoiceVo vo = mutiChoiceList.get(i);
            if(vo.getContent() == null){
                msgArr.add("题目内容为空");
            }
            LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMMUTICHOICEBYSUB");
            Map<String, Object> checkRepeatMap = new HashMap<>();
            checkRepeatMap.put("subjectName", String.valueOf(vo.getContent()));
            checkRepeatMap.put("subjectCategoryId", subjectCategoryId);
            List<Map<String, Object>> list = (List<Map<String, Object>>) baseService.list(checkRepeatMap);
            if (list != null && list.size() > 0) {
                msgArr.add("题目名称重复");
            }
            if(vo.getContent() != null && vo.getContent().length() > 200){
                msgArr.add("题目内容长度大于200");
            }
            if(vo.getChoiceA() == null){
                msgArr.add("选项A内容为空");
            }
            if(vo.getChoiceA() != null && vo.getChoiceA().length() > 200){
                msgArr.add("选项A长度大于200");
            }
            if(vo.getChoiceB() == null){
                msgArr.add("选项B内容为空");
            }
            if(vo.getChoiceB() != null && vo.getChoiceB().length() > 200){
                msgArr.add("选项B长度大于200");
            }
            if(vo.getChoiceC() == null){
                msgArr.add("选项C内容为空");
            }
            if(vo.getChoiceC() != null && vo.getChoiceC().length() > 200){
                msgArr.add("选项C长度大于200");
            }
            if(vo.getChoiceD() == null){
                msgArr.add("选项D内容为空");
            }
            if(vo.getChoiceD() != null && vo.getChoiceD().length() > 200){
                msgArr.add("选项D长度大于200");
            }
            if(vo.getAnswer() == null){
                msgArr.add("答案为空");
            }
            if(vo.getAnswer() != null && vo.getAnswer().length() >255){
                msgArr.add("答案长度大于255");
            }
            if(vo.getSource() == null){
                msgArr.add("出处为空");
            }
            if(vo.getSource()!=null && vo.getSource().length() > 50){
                msgArr.add("出处长度大于50");
            }
            if(msgArr!=null && msgArr.size() > 0) {
                lineMsgMap.put(String.valueOf(i + 1), ((ArrayList<String>) msgArr).clone());
            }
        }
        mutiChoiceMsgMap.put("type","2");
        if(lineMsgMap.size() > 0) {
            mutiChoiceMsgMap.put("errors", lineMsgMap);
        }
        return mutiChoiceMsgMap;
    }
    //校验单选题必填项的方法
    private Object checkSimpleChoiceData(List<ExamExcelSimpleChoiceVo> simpleChoiceList, String subjectCategoryId) throws Exception {
        Map<String,Object> simpleChoiceMsgMap = new HashMap<>();
        Map<String,Object> lineMsgMap = new HashMap<>();
        List<String> msgArr = new ArrayList<>();
        for (int i = 0; i < simpleChoiceList.size(); i++) {
            ExamExcelSimpleChoiceVo vo = simpleChoiceList.get(i);
            msgArr.clear();
            if(vo.getContent() == null){
                msgArr.add("题目内容为空");
            }
            LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMSIMPLECHOICEBYSUB");
            Map<String, Object> checkRepeatMap = new HashMap<>();
            checkRepeatMap.put("subjectName", String.valueOf(vo.getContent()));
            checkRepeatMap.put("subjectCategoryId", subjectCategoryId);
            List<Map<String, Object>> list = (List<Map<String, Object>>) baseService.list(checkRepeatMap);
            if (list != null && list.size() > 0) {
                msgArr.add("题目名称重复");
            }

            if(vo.getContent()!=null && vo.getContent().length() > 200){
                msgArr.add("题目内容长度大于200");
            }
            if(vo.getChoiceA() == null){
                msgArr.add("选项A内容为空");
            }
            if(vo.getChoiceA() != null && vo.getChoiceA().length() > 200){
                msgArr.add("选项A长度大于200");
            }
            if(vo.getChoiceB() == null){
                msgArr.add("选项B内容为空");
            }
            if(vo.getChoiceB() != null && vo.getChoiceB().length() > 200){
                msgArr.add("选项B长度大于200");
            }
            if(vo.getChoiceC() == null){
                msgArr.add("选项C内容为空");
            }
            if(vo.getChoiceC() != null && vo.getChoiceC().length() > 200){
                msgArr.add("选项C长度大于200");
            }
            if(vo.getChoiceD() == null){
                msgArr.add("选项D内容为空");
            }
            if(vo.getChoiceD() != null && vo.getChoiceD().length() > 200){
                msgArr.add("选项D长度大于200");
            }
            if(vo.getAnswer() == null){
                msgArr.add("答案为空");
            }else {
                String anser = vo.getAnswer();
                if (!(anser.equals("A") || anser.equals("B") || anser.equals("C") || anser.equals("D"))) {
                    msgArr.add("答案应为A,B,C,D");
                }
            }
            if(vo.getSource() == null){
                msgArr.add("出处为空");
            }
            if(vo.getSource()!=null && vo.getSource().length() > 50){
                msgArr.add("出处长度大于50");
            }
            if(msgArr!=null && msgArr.size() > 0) {
                lineMsgMap.put(String.valueOf(i + 1), ((ArrayList<String>) msgArr).clone());
            }
        }
        simpleChoiceMsgMap.put("type","1");
        if(lineMsgMap.size() > 0) {
            simpleChoiceMsgMap.put("errors", lineMsgMap);
        }
        return simpleChoiceMsgMap;
    }

    private Object checkAnalysisData(List<ExamExcelCaseAnalysisVo> caseAnalysisList, String subjectCategoryId) throws Exception {
        Map<String,Object> caseAnalysisMsgMap = new HashMap<>();
        Map<String,Object> lineMsgMap = new HashMap<>();
        List<String> msgArr = new ArrayList<>();
        for (int i = 0; i < caseAnalysisList.size(); i++) {
            msgArr.clear();
            ExamExcelCaseAnalysisVo vo = caseAnalysisList.get(i);
            if(vo.getContent() == null){
                msgArr.add("题目内容为空");
            }
            //检查试题名称是否重复,案例分析
            LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMDISCUSSBYSUBANDTYPE");
            Map<String, Object> checkRepeatMap = new HashMap<>();
            checkRepeatMap.put("subjectName", String.valueOf(vo.getContent()));
            checkRepeatMap.put("subjectCategoryId",subjectCategoryId);
            checkRepeatMap.put("type","7");
            List<Map<String, Object>> list = (List<Map<String, Object>>) baseService.list(checkRepeatMap);
            if (list != null && list.size() > 0) {
                msgArr.add("试题名称重复!");
            }
            if(vo.getContent() != null && vo.getContent().length() > 1000){
                msgArr.add("题目内容长度大于1000");
            }
            if(vo.getSource() == null){
                msgArr.add("出处为空");
            }
            if(vo.getSource() != null && vo.getSource().length() > 50){
                msgArr.add("出处长度大于50");
            }
            if(msgArr!=null && msgArr.size() > 0) {
                lineMsgMap.put(String.valueOf(i + 1), ((ArrayList<String>) msgArr).clone());
            }
        }
        caseAnalysisMsgMap.put("type","7");
        if(lineMsgMap.size() > 0) {
            caseAnalysisMsgMap.put("errors", lineMsgMap);
        }
        return caseAnalysisMsgMap;
    }

    private Object checkDiscussData(List<ExamExcelDiscussVo> discussList, String subjectCategoryId) throws Exception {
        Map<String,Object> discussMsgMap = new HashMap<>();
        Map<String,Object> lineMsgMap = new HashMap<>();
        List<String> msgArr = new ArrayList<>();
        for (int i = 0; i < discussList.size(); i++) {
            msgArr.clear();
            ExamExcelDiscussVo vo = discussList.get(i);
            if(vo.getContent() == null){
                msgArr.add((i+1)+"题目内容为空");
            }
            //检查试题名称是否重复,论述
            LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMDISCUSSBYSUBANDTYPE");
            Map<String, Object> checkRepeatMap = new HashMap<>();
            checkRepeatMap.put("subjectName", String.valueOf(vo.getContent()));
            checkRepeatMap.put("subjectCategoryId",subjectCategoryId);
            checkRepeatMap.put("type","6");
            List<Map<String, Object>> list = (List<Map<String, Object>>) baseService.list(checkRepeatMap);
            if (list != null && list.size() > 0) {
                msgArr.add("试题名称重复!");
            }
            if(vo.getContent() != null && vo.getContent().length() > 1000){
                msgArr.add((i+1)+"题目内容长度大于1000");
            }
            if(vo.getSource() == null){
                msgArr.add((i+1)+"出处为空");
            }
            if(vo.getSource() != null && vo.getSource().length() > 50){
                msgArr.add((i+1)+"出处长度大于50");
            }
            if(msgArr!=null && msgArr.size() > 0) {
                lineMsgMap.put(String.valueOf(i + 1), ((ArrayList<String>) msgArr).clone());
            }
        }
        discussMsgMap.put("type","6");
        if(lineMsgMap.size() > 0) {
            discussMsgMap.put("errors", lineMsgMap);
        }
        return discussMsgMap;
    }

    private Object checkEasyQuestionData(List<ExamExcelEasyQuestionVo> easyQuestionList, String subjectCategoryId) throws Exception {
        Map<String,Object> easyQuestionMsgMap = new HashMap<>();
        Map<String,Object> lineMsgMap = new HashMap<>();
        List<String> msgArr = new ArrayList<>();
        for (int i = 0; i < easyQuestionList.size(); i++) {
            msgArr.clear();
            ExamExcelEasyQuestionVo vo = easyQuestionList.get(i);
            if(vo.getContent() == null){
                msgArr.add("题目内容为空");
            }
            //检查试题名称是否重复,简答
            LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMDISCUSSBYSUBANDTYPE");
            Map<String, Object> checkRepeatMap = new HashMap<>();
            checkRepeatMap.put("subjectName", String.valueOf(vo.getContent()));
            checkRepeatMap.put("subjectCategoryId",subjectCategoryId);
            checkRepeatMap.put("type","5");
            List<Map<String, Object>> list = (List<Map<String, Object>>) baseService.list(checkRepeatMap);
            if (list != null && list.size() > 0) {
                msgArr.add("试题名称重复!");
            }
            if(vo.getContent() != null && vo.getContent().length() > 1000){
                msgArr.add("题目内容长度大于1000");
            }
            if(vo.getSource() == null){
                msgArr.add("出处为空");
            }
            if(vo.getSource() != null && vo.getSource().length() > 50){
                msgArr.add("出处长度大于50");
            }
            if(msgArr!=null && msgArr.size() > 0) {
                lineMsgMap.put(String.valueOf(i + 1), ((ArrayList<String>) msgArr).clone());
            }
        }
        easyQuestionMsgMap.put("type","5");
        if(lineMsgMap.size() > 0) {
            easyQuestionMsgMap.put("errors", lineMsgMap);
        }
        return easyQuestionMsgMap;
    }
}
