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
import com.nmghr.controller.vo.*;
import com.nmghr.hander.save.ExamQuestionsSaveHandler;
import com.sargeraswang.util.ExcelUtil.ExcelSheet;
import com.sargeraswang.util.ExcelUtil.ExcelUtil;
import com.sargeraswang.util.ExcelUtil.ExcelsUtil;
import org.apache.xmlbeans.impl.regex.RegularExpression;
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
        List<String> simpleChoiceMsgList = (List<String>) checkSimpleChoiceData(simpleChoiceVos);
        List<String> mutiChoiceMsgList = (List<String>) checkMutiChoiceData(mutiChoiceVos);
        List<String> fillGapMsgList = (List<String>) checkFillGapData(fillGapVos);
        List<String> judgeMsgList = (List<String>) checkJudgeData(judgeVos);
        List<String> easyQuestionMsgList = (List<String>) checkEasyQuestionData(easyQuestionVos);
        List<String> discussMsgList = (List<String>) checkDiscussData(discussVos);
        List<String> caseAnalysisMsgList = (List<String>) checkAnalysisData(caseAnalysisVos);
        if(mutiChoiceMsgList!=null && simpleChoiceMsgList.size() > 0){
            return simpleChoiceMsgList;
        }
        if(mutiChoiceMsgList!=null && mutiChoiceMsgList.size() > 0){
           return mutiChoiceMsgList;
        }
        if(fillGapMsgList!=null && fillGapMsgList.size() > 0){
            return fillGapMsgList;
        }
        if(judgeMsgList!=null && judgeMsgList.size() > 0){
            return judgeMsgList;
        }
        if(easyQuestionMsgList!=null && easyQuestionMsgList.size() > 0){
            return easyQuestionMsgList;
        }
        if(discussMsgList!=null && discussMsgList.size() > 0) {
            return discussMsgList;
        }
        if(caseAnalysisMsgList!=null && caseAnalysisMsgList.size() > 0){
            return caseAnalysisMsgList;
        }

        //入库单选
        saveSimpleChoice(simpleChoiceVos);
        //入库多选
        saveMutiChoice(mutiChoiceVos);
        //入库填空
        saveFillGap(fillGapVos);
        //入库判断
        saveJudge(judgeVos);
        //入库论述
        saveDiscuss(discussVos);
        //入库简答
        saveEasyQuestion(easyQuestionVos);
        //入库案例分析
        saveAnalysis(caseAnalysisVos);




    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (Exception e) {
      // TODO Auto-generated catch block
      log.error("excel uploadFile error", e.getMessage());
      e.printStackTrace();
    }
    return Result.ok(null);
  }

    private void saveAnalysis(List<ExamExcelCaseAnalysisVo> caseAnalysisVos) throws Exception {
        //案例分析表Map
        Map<String, Object> caseAnalysisMap = new HashMap<>();

        for (ExamExcelCaseAnalysisVo caseAnalysisVo : caseAnalysisVos) {
            //题目名称
            caseAnalysisMap.put("subjectName",caseAnalysisVo.getContent());
            caseAnalysisMap.put("type","7");
            //题目解析
            caseAnalysisMap.put("analysis",caseAnalysisVo.getAnalysis());
            //来源
            caseAnalysisMap.put("source",caseAnalysisVo.getSource());//出处
            caseAnalysisMap.put("sort",caseAnalysisVo.getSort());
            questionsSaveHandler.save(caseAnalysisMap);
        }
    }
    private void saveEasyQuestion(List<ExamExcelEasyQuestionVo> easyQuestionVos) throws Exception {
        //简答表Map
        Map<String, Object> easyQuestionMap = new HashMap<>();
        for (int i = 0; i < easyQuestionVos.size(); i++) {
            ExamExcelEasyQuestionVo easyQuestionVo = easyQuestionVos.get(i);
            //题目名称
            easyQuestionMap.put("subjectName",easyQuestionVo.getContent());
            easyQuestionMap.put("type","5");
            //题目解析
            easyQuestionMap.put("analysis",easyQuestionVo.getAnalysis());
            //来源
            easyQuestionMap.put("source",easyQuestionVo.getSource());//出处
            easyQuestionMap.put("sort",easyQuestionVo.getSort());
            questionsSaveHandler.save(easyQuestionMap);
        }

    }

    private void saveDiscuss(List<ExamExcelDiscussVo> discussVos) throws Exception {
        //论述表Map
        Map<String, Object> discussMap = new HashMap<>();
        for (int i = 0; i < discussVos.size(); i++) {
            ExamExcelDiscussVo examExcelDiscussVo = discussVos.get(i);
            //题目名称
            discussMap.put("subjectName",examExcelDiscussVo.getContent());
            discussMap.put("type","6");
            //题目解析
            discussMap.put("analysis",examExcelDiscussVo.getAnalysis());
            //来源
            discussMap.put("source",examExcelDiscussVo.getSource());//出处
            discussMap.put("sort",examExcelDiscussVo.getSort());
            questionsSaveHandler.save(discussMap);

        }
    }

    //入库判断并建立关系
    private void saveJudge(List<ExamExcelJudgeVo> judgeVos) throws Exception{
        //填空题表Map
        Map<String, Object> judgeMap = new HashMap<>();
        for (ExamExcelJudgeVo judgeVo : judgeVos) {
            //题目名称
            judgeMap.put("subjectName",judgeVo.getContent());
            judgeMap.put("type","4");
            //答案
            judgeMap.put("answer",judgeVo.getAnswer());
            //题目解析
            judgeMap.put("analysis",judgeVo.getAnalysis());
            //来源
            judgeMap.put("source",judgeVo.getSource());
            //排序
            judgeMap.put("sort",judgeVo.getSort());
            questionsSaveHandler.save(judgeMap);
        }
    }

    //入库填空并建立关系
    private void saveFillGap(List<ExamExcelFillGapVo> fillGapVos ) throws Exception {
        //填空题表Map
        Map<String, Object> fillGapMap = new HashMap<>();
        for (ExamExcelFillGapVo fillGapVo : fillGapVos) {
            //题目名称
            fillGapMap.put("subjectName",fillGapVo.getContent());
            fillGapMap.put("type","3");
            //答案
            fillGapMap.put("answer",fillGapVo.getAnswer());
            //题目解析
            fillGapMap.put("analysis",fillGapVo.getAnalysis());
            //来源
            fillGapMap.put("source",fillGapVo.getSource());
            //排序
            fillGapMap.put("sort",fillGapVo.getSort());
            questionsSaveHandler.save(fillGapMap);
        }
    }
    //入库多选并建立关系
    private void saveMutiChoice(List<ExamExcelMutiChoiceVo> mutiChoiceVos) throws Exception {
        //多选题表Map
        Map<String, Object> mutiChoiceMap = new HashMap<>();
        for (ExamExcelMutiChoiceVo mutiChoiceVo : mutiChoiceVos) {
            //题目名称
            mutiChoiceMap.put("subjectName", mutiChoiceVo.getContent());
            //题目类型 2多选
            mutiChoiceMap.put("type","2");
            //答案
            mutiChoiceMap.put("answer", mutiChoiceVo.getAnswer());
            //题目解析
            mutiChoiceMap.put("analysis", mutiChoiceVo.getAnalysis());
            //来源
            mutiChoiceMap.put("source", mutiChoiceVo.getSource());
            //排序
            mutiChoiceMap.put("sort", mutiChoiceVo.getSort());
            //选项表Map
            Map<String, Object> mutiChoicePointMap = new HashMap<>();
            mutiChoicePointMap.put("pointA",mutiChoiceVo.getChoiceA());
            mutiChoicePointMap.put("pointB",mutiChoiceVo.getChoiceB());
            mutiChoicePointMap.put("pointC",mutiChoiceVo.getChoiceC());
            mutiChoicePointMap.put("pointD",mutiChoiceVo.getChoiceD());
            mutiChoicePointMap.put("pointE",mutiChoiceVo.getChoiceE());
            mutiChoicePointMap.put("pointF",mutiChoiceVo.getChoiceF());
            mutiChoiceMap.put("points",mutiChoicePointMap);
            questionsSaveHandler.save(mutiChoiceMap);

        }
    }
    //入库单选并建立关系
    private void saveSimpleChoice(List<ExamExcelSimpleChoiceVo> simpleChoiceVos) throws Exception {
        //单选题表Map
        Map<String,Object> simpleChoiceMap = new HashMap<>();
        for (ExamExcelSimpleChoiceVo simpleChoiceVo : simpleChoiceVos) {
            //题目名称
            simpleChoiceMap.put("subjectName",simpleChoiceVo.getContent());
            //题目类型 1单选
            simpleChoiceMap.put("type","1");
            //答案
            simpleChoiceMap.put("answer",simpleChoiceVo.getAnswer());
            //题目解析
            simpleChoiceMap.put("analysis",simpleChoiceVo.getAnalysis());
            //来源
            simpleChoiceMap.put("source",simpleChoiceVo.getSource());
            //排序
            simpleChoiceMap.put("sort",simpleChoiceVo.getSort());
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
    private Object checkJudgeData(List<ExamExcelJudgeVo> judgeList) {
        List<String> judgeMsgList = new ArrayList<>();
        for (int i = 0; i < judgeList.size(); i++) {
            ExamExcelJudgeVo vo = judgeList.get(i);
            if(vo.getContent() == null){
                judgeMsgList.add("判断题"+ (i+1)+"行"+"题目内容为空");
            }
            if(vo.getContent() != null && vo.getContent().length() > 200){
                judgeMsgList.add("判断题"+ (i+1)+"行"+"题目内容长度大于200");
            }
            if(vo.getAnswer() == null){
                judgeMsgList.add("判断题"+(i+1)+"行"+"正确答案为空");
            }
            if(vo.getSource() == null){
                judgeMsgList.add("判断题"+(i+1)+"行"+"出处为空");
            }
            if(vo.getSource() != null && vo.getSource().length() > 50){
                judgeMsgList.add("判断题"+(i+1)+"行"+"出处长度大于50");
            }
        }

     return judgeMsgList;
    }
    //校验填空题必填项的方法
    private Object checkFillGapData(List<ExamExcelFillGapVo> fillGapList) {
        List<String> fillGapMsgList = new ArrayList<>();
        for (int i = 0; i < fillGapList.size(); i++) {
            ExamExcelFillGapVo vo = fillGapList.get(i);
            if(vo.getContent() == null){
                fillGapMsgList.add("填空题"+ (i+1)+"行"+"题目内容为空");
            }
            if(vo.getContent() != null && vo.getContent().length() > 200){
                fillGapMsgList.add("判断题"+ (i+1)+"行"+"题目内容长度大于200");
            }
            if(vo.getAnswer() == null){
                fillGapMsgList.add("填空题"+(i+1)+"行"+"正确答案为空");
            }
            if(vo.getAnswer() != null &&vo.getAnswer().length() > 255){
                fillGapMsgList.add("填空题"+(i+1)+"行"+"正确答案长度大于255");
            }
            if(vo.getSource() == null){
                fillGapMsgList.add("填空题"+(i+1)+"行"+"出处为空");
            }
            if(vo.getSource() != null && vo.getSource().length() > 50){
                fillGapMsgList.add("填空题"+(i+1)+"行"+"出处长度大于50");
            }
        }
      return fillGapMsgList;
    }
    //校验多选题必填项的方法
    private Object checkMutiChoiceData(List<ExamExcelMutiChoiceVo> mutiChoiceList) {
        List<String> mutiChoiceMsgList = new ArrayList<>();
        for (int i = 0; i < mutiChoiceList.size(); i++) {
            ExamExcelMutiChoiceVo vo = mutiChoiceList.get(i);
            if(vo.getContent() == null){
                mutiChoiceMsgList.add("多选题"+ (i+1)+"行"+"题目内容为空");
            }
            if(vo.getContent().length() > 200){
                mutiChoiceMsgList.add("多选题"+ (i+1)+"行"+"题目内容长度大于200");
            }
            if(vo.getChoiceA() == null){
                mutiChoiceMsgList.add("多选题"+ (i+1)+"行"+"选项A内容为空");
            }
            if(vo.getChoiceA() != null && vo.getChoiceA().length() > 200){
                mutiChoiceMsgList.add("多选题"+ (i+1)+"行"+"选项A长度大于200");
            }
            if(vo.getChoiceB() == null){
                mutiChoiceMsgList.add("多选题"+ (i+1)+"行"+"选项B内容为空");
            }
            if(vo.getChoiceB() != null && vo.getChoiceB().length() > 200){
                mutiChoiceMsgList.add("多选题"+ (i+1)+"行"+"选项B长度大于200");
            }
            if(vo.getChoiceC() == null){
                mutiChoiceMsgList.add("多选题"+ (i+1)+"行"+"选项C内容为空");
            }
            if(vo.getChoiceC() != null && vo.getChoiceC().length() > 200){
                mutiChoiceMsgList.add("多选题"+ (i+1)+"行"+"选项C长度大于200");
            }
            if(vo.getChoiceD() == null){
                mutiChoiceMsgList.add("多选题"+ (i+1)+"行"+"选项D内容为空");
            }
            if(vo.getChoiceD() != null && vo.getChoiceD().length() > 200){
                mutiChoiceMsgList.add("多选题"+ (i+1)+"行"+"选项D长度大于200");
            }
            if(vo.getAnswer() == null){
                mutiChoiceMsgList.add("多选题"+ (i+1)+"行"+"答案为空");
            }
            if(vo.getAnswer() != null && vo.getAnswer().length() >255){
                mutiChoiceMsgList.add("多选题"+ (i+1)+"行"+"答案长度大于255");
            }
            if(vo.getSource() == null){
                mutiChoiceMsgList.add("多选题"+ (i+1)+"行"+"出处为空");
            }
            if(vo.getSource()!=null && vo.getSource().length() > 50){
                mutiChoiceMsgList.add("多选题"+ (i+1)+"行"+"出处大于50");
            }
        }
        return mutiChoiceMsgList;
    }
    //校验单选题必填项的方法
    private Object checkSimpleChoiceData(List<ExamExcelSimpleChoiceVo> simpleChoiceList) {
        List<String> simpleChoiceMsgList = new ArrayList<>();
        for (int i = 0; i < simpleChoiceList.size(); i++) {
            ExamExcelSimpleChoiceVo vo = simpleChoiceList.get(i);
            if(vo.getContent() == null){
                    simpleChoiceMsgList.add("单选题"+ (i+1)+"行"+"题目内容为空");
            }
            if(vo.getContent().length() > 200){
                    simpleChoiceMsgList.add("单选题"+ (i+1)+"行"+"题目内容长度大于200");
            }
            if(vo.getChoiceA() == null){
                simpleChoiceMsgList.add("单选题"+ (i+1)+"行"+"选项A内容为空");
            }
            if(vo.getChoiceA() != null && vo.getChoiceA().length() > 200){
                simpleChoiceMsgList.add("单选题"+ (i+1)+"行"+"选项A长度大于200");
            }
            if(vo.getChoiceB() == null){
                simpleChoiceMsgList.add("单选题"+ (i+1)+"行"+"选项B内容为空");
            }
            if(vo.getChoiceB() != null && vo.getChoiceB().length() > 200){
                simpleChoiceMsgList.add("单选题"+ (i+1)+"行"+"选项B长度大于200");
            }
            if(vo.getChoiceC() == null){
                simpleChoiceMsgList.add("单选题"+ (i+1)+"行"+"选项C内容为空");
            }
            if(vo.getChoiceC() != null && vo.getChoiceC().length() > 200){
                simpleChoiceMsgList.add("单选题"+ (i+1)+"行"+"选项C长度大于200");
            }
            if(vo.getChoiceD() == null){
                simpleChoiceMsgList.add("单选题"+ (i+1)+"行"+"选项D内容为空");
            }
            if(vo.getChoiceD() != null && vo.getChoiceD().length() > 200){
                simpleChoiceMsgList.add("单选题"+ (i+1)+"行"+"选项D长度大于200");
            }
            if(vo.getAnswer() == null){
                simpleChoiceMsgList.add("单选题"+ (i+1)+"行"+"答案为空");
            }else {
                String anser = vo.getAnswer();
                if (!(anser.equals("A") || anser.equals("B") || anser.equals("C") || anser.equals("D"))) {
                    simpleChoiceMsgList.add("单选题" + (i + 1) + "行" + "答案非法");
                }
            }
            if(vo.getSource() == null){
                simpleChoiceMsgList.add("单选题"+ (i+1)+"行"+"出处为空");
            }
            if(vo.getSource()!=null && vo.getSource().length() > 50){
                simpleChoiceMsgList.add("单选题"+ (i+1)+"行"+"出处大于50");
            }
        }
        return simpleChoiceMsgList;
    }

    private Object checkAnalysisData(List<ExamExcelCaseAnalysisVo> caseAnalysisList) {
        List<String> checkAnalysisMsgList = new ArrayList<>();
        for (int i = 0; i < caseAnalysisList.size(); i++) {
            ExamExcelCaseAnalysisVo vo = caseAnalysisList.get(i);
            if(vo.getContent() == null){
                checkAnalysisMsgList.add("案例分析题"+ (i+1)+"行"+"题目内容为空");
            }
            if(vo.getContent() != null && vo.getContent().length() > 1000){
                checkAnalysisMsgList.add("案例分析题"+ (i+1)+"行"+"题目内容长度大于1000");
            }
            if(vo.getSource() == null){
                checkAnalysisMsgList.add("案例分析题"+(i+1)+"行"+"出处为空");
            }
            if(vo.getSource() != null && vo.getSource().length() > 50){
                checkAnalysisMsgList.add("案例分析题"+(i+1)+"行"+"出处长度大于50");
            }
        }
        return checkAnalysisMsgList;
    }

    private Object checkDiscussData(List<ExamExcelDiscussVo> discussList) {
        List<String> discussMsgList = new ArrayList<>();
        for (int i = 0; i < discussList.size(); i++) {
            ExamExcelDiscussVo vo = discussList.get(i);
            if(vo.getContent() == null){
                discussMsgList.add("论述题"+ (i+1)+"行"+"题目内容为空");
            }
            if(vo.getContent() != null && vo.getContent().length() > 1000){
                discussMsgList.add("论述题"+ (i+1)+"行"+"题目内容长度大于1000");
            }
            if(vo.getSource() == null){
                discussMsgList.add("论述题"+(i+1)+"行"+"出处为空");
            }
            if(vo.getSource() != null && vo.getSource().length() > 50){
                discussMsgList.add("论述题"+(i+1)+"行"+"出处长度大于50");
            }
        }
        return discussMsgList;
    }

    private Object checkEasyQuestionData(List<ExamExcelEasyQuestionVo> easyQuestionList) {
        List<String> easyQuestionMsgList = new ArrayList<>();
        for (int i = 0; i < easyQuestionList.size(); i++) {
            ExamExcelEasyQuestionVo vo = easyQuestionList.get(i);
            if(vo.getContent() == null){
                easyQuestionMsgList.add("简答题"+ (i+1)+"行"+"题目内容为空");
            }
            if(vo.getContent() != null && vo.getContent().length() > 1000){
                easyQuestionMsgList.add("简答题"+ (i+1)+"行"+"题目内容长度大于1000");
            }
            if(vo.getSource() == null){
                easyQuestionMsgList.add("简答题"+(i+1)+"行"+"出处为空");
            }
            if(vo.getSource() != null && vo.getSource().length() > 50){
                easyQuestionMsgList.add("简答题"+(i+1)+"行"+"出处长度大于50");
            }

        }
        return easyQuestionMsgList;
    }



}
