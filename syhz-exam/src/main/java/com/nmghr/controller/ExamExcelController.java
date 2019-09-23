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

  private static final Logger log = LoggerFactory.getLogger(ExamExcelController.class);

  @GetMapping("/excel/modal")
  @ResponseBody
  public void excel(@RequestParam Map<String, Object> params, HttpServletRequest request, HttpServletResponse response) throws Exception {
    List<Map<String, Object>> list= new ArrayList<>();


    OutputStream os = new ByteArrayOutputStream();
  //单选sheet页
    ExcelSheet excelSheetSimpleChoice = new ExcelSheet();
    excelSheetSimpleChoice.setSheetName("单选题");
    Map<String, String> simpleChoiceHeadersMap = new LinkedHashMap<String, String>();
    simpleChoiceHeadersMap.put("content","题目内容 *");
    simpleChoiceHeadersMap.put("order","次序");
    simpleChoiceHeadersMap.put("answerReason","题目解析");
    simpleChoiceHeadersMap.put("ChoiceA","选项A *");
    simpleChoiceHeadersMap.put("ChoiceB","选项B *");
    simpleChoiceHeadersMap.put("ChoiceC","选项C *");
    simpleChoiceHeadersMap.put("ChoiceD","选项D *");
    simpleChoiceHeadersMap.put("answer","正确答案 *");
    simpleChoiceHeadersMap.put("from","出处 *");
    excelSheetSimpleChoice.setHeaders(simpleChoiceHeadersMap);
    excelSheetSimpleChoice.setDataset(new ArrayList());



  //多选sheet页
    ExcelSheet excelSheetMutipleChoice = new ExcelSheet();
    excelSheetMutipleChoice.setSheetName("多选题");
    Map<String, String> mutipleChoiceHeadersMap = new LinkedHashMap<String, String>();
   //单选多选模板一致
    mutipleChoiceHeadersMap = simpleChoiceHeadersMap;
    excelSheetMutipleChoice.setHeaders(mutipleChoiceHeadersMap);
    excelSheetMutipleChoice.setDataset(new ArrayList());

    //填空sheet页
    ExcelSheet excelSheetFillGap = new ExcelSheet();
    excelSheetFillGap.setSheetName("填空题");
    Map<String, String> fillGapHeadersMap = new LinkedHashMap<String, String>();
    fillGapHeadersMap.put("content","题目内容 *");
    fillGapHeadersMap.put("order","次序");
    fillGapHeadersMap.put("answerReason","题目解析");
    fillGapHeadersMap.put("answer","正确答案 *");
    fillGapHeadersMap.put("from","出处 *");
    excelSheetFillGap.setHeaders(fillGapHeadersMap);
    excelSheetFillGap.setDataset(new ArrayList());
    //判断sheet页
    ExcelSheet excelSheetJudge = new ExcelSheet();
    excelSheetJudge.setSheetName("判断题");
    Map<String, String> JudgeHeadersMap = new LinkedHashMap<String, String>();
    JudgeHeadersMap.put("content","题目内容 *");
    JudgeHeadersMap.put("order","次序");
    JudgeHeadersMap.put("answerReason","题目解析");
    JudgeHeadersMap.put("answer","正确答案 *");
    JudgeHeadersMap.put("from","出处 *");
    excelSheetJudge.setHeaders(JudgeHeadersMap);
    excelSheetJudge.setDataset(new ArrayList());

   List<ExcelSheet<ExcelSheet>> excelSheets = new ArrayList<>();
   excelSheets.add(excelSheetSimpleChoice);
   excelSheets.add(excelSheetMutipleChoice);
   excelSheets.add(excelSheetFillGap);
   excelSheets.add(excelSheetJudge);

    String fileName = "考试题导入模板";
    ExcelUtil.exportExcel(excelSheets,os,null,null);
    // 配置浏览器下载
    byte[] content = ((ByteArrayOutputStream) os).toByteArray();
    InputStream is = new ByteArrayInputStream(content);
    response.reset();
    response.setContentType("application/vnd.ms-excel;charset=utf-8");
    response.setHeader("Content-Disposition",
        "attachment;filename=" + new String((fileName + ".xlsx").getBytes(), "iso-8859-1"));
    ServletOutputStream out = response.getOutputStream();
    BufferedInputStream bis = null;
    BufferedOutputStream bos = null;
    try {
      bis = new BufferedInputStream(is);
      bos = new BufferedOutputStream(out);
      byte[] buff = new byte[2048];
      int bytesRead;
      while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
        bos.write(buff, 0, bytesRead);
      }
    } catch (final IOException e) {
      throw e;
    } finally {
      if (bis != null)
        bis.close();
      if (bos != null)
        bos.close();
    }
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
