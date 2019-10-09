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
import com.nmghr.basic.core.page.Paging;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.util.ValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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

      int pageNum = 1, pageSize = 15;
      if (params.get("pageNum") != null && !"".equals(String.valueOf(params.get("pageNum")).trim())) {
          pageNum = Integer.parseInt(String.valueOf(params.get("pageNum")));
      }
      if (params.get("pageSize") != null && !"".equals(String.valueOf(params.get("pageSize")).trim())) {
          pageSize = Integer.parseInt(String.valueOf(params.get("pageSize")));
      }

      //Integer size = pageSize/7;
      //单选
      if("1".equals(type)){
        //查当前题库的所有单选
        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMSIMPLECHOICEBYSUB");
        //List<Map<String, Object>> list = (List<Map<String, Object>>) baseService.list(params);
          Paging paging = (Paging) baseService.page(params, pageNum, pageSize);
         return paging;
        }
      //当前题库所有多选
      if("2".equals(type)){
        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMMUTICHOICEBYSUB");
          Paging paging = (Paging) baseService.page(params, pageNum, pageSize);
          return paging;
      }
        //当前题库所有填空
      if("3".equals(type)){
          LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMFILLGAPSBYSUB");
          Paging paging = (Paging) baseService.page(params, pageNum, pageSize);
          return paging;
      }
      //当前题库所有判断
      if("4".equals(type)){
          LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMJUDGEBYSUB");
          Paging paging = (Paging) baseService.page(params, pageNum, pageSize);
          return paging;
      }
      //5简答6论述7案例分析
      if("5".equals(type) || "6".equals(type) || "7".equals(type)){
          LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMDISCUSSBYSUBANDTYPE");
          params.put("type",type);
          Paging paging = (Paging) baseService.page(params, pageNum, pageSize);
          return paging;
      }
      //所有题目 5简答 6 论述 7 案例分析
      if("0".equals(type)){
          List<Map<String, Object>> questions = new ArrayList<>();
          LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMSIMPLECHOICEBYSUB");
          Paging simpleChoicePaging = (Paging) baseService.page(params, pageNum, pageSize);
          LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMMUTICHOICEBYSUB");
          Paging mutiChoicePaging = (Paging) baseService.page(params, pageNum, pageSize);
          LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMFILLGAPSBYSUB");
          Paging fillgapPaging = (Paging) baseService.page(params, pageNum, pageSize);
          LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMJUDGEBYSUB");
          Paging judgePaging = (Paging) baseService.page(params, pageNum, pageSize);
          LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMDISCUSSBYSUBANDTYPE");
          params.put("type","5");
          Paging jdPageing = (Paging) baseService.page(params, pageNum, pageSize);
          params.put("type","6");
          Paging lsPageing = (Paging) baseService.page(params, pageNum, pageSize);
          params.put("type","7");
          Paging alfxPageing = (Paging) baseService.page(params, pageNum, pageSize);

          questions.addAll(simpleChoicePaging.getList());
          questions.addAll(mutiChoicePaging.getList());
          questions.addAll(fillgapPaging.getList());
          questions.addAll(judgePaging.getList());
          questions.addAll(jdPageing.getList());
          questions.addAll(lsPageing.getList());
          questions.addAll(alfxPageing.getList());

          long count = simpleChoicePaging.getTotalCount() + mutiChoicePaging.getTotalCount() + fillgapPaging.getTotalCount()
                  +judgePaging.getTotalCount() + jdPageing.getTotalCount() + lsPageing.getTotalCount() + alfxPageing.getTotalCount();
          return new Paging<>(pageSize,pageNum,count,questions);
      }
      return new Paging<>(pageSize, pageNum,0,new ArrayList<>());
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
            return new HashMap<>();
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
            return new HashMap<>();
        }
        if("5".equals(String.valueOf(params.get("type"))) || "6".equals(String.valueOf(params.get("type")))
        || "7".equals(String.valueOf(params.get("type")))) {

            LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMDISCUSS");
            Map<String, Object> discusses = (Map<String, Object>) baseService.get(params);
            if(discusses!=null){
                return discusses;
            }
            return  new HashMap<>();
        }
        return new HashMap<>();
    }
    /*
    查询试题的引用状态
     */
    @GetMapping("/checkinpaper")
    @ResponseBody
    public Object checkInpaper(@RequestParam Map<String, Object> params) throws Exception {
        if(params.get("id") == null){
            throw new GlobalErrorException("998001", "试题Id不能为空");
        }
        Map<String,Object> resultMap  = new HashMap<String,Object>();
        //判断是否有引用
        /*
        编辑和删除时，检查该试题当前是否在进行中考试试卷中有使用，如果使用，
        提示：该试题已经被抽取到XXXX（试卷名称）
        试卷中，暂时不能编辑或删除！（知道了）
        如果未在进行中的考试试卷中使用，
        在已结束的考试试卷中有使用，
        提示：该试题在已结束的考试试卷中有使用，
        如果修改可能会影响到警员查看以往考试信息！是否继续修改？（确认/取消）
         */
        Map<String,Object> isInPaperParam = new HashMap();
        isInPaperParam.put("questionsId", params.get("id"));
        //引用当前试题的所有正在考试的试卷,取距当前时间最近的试卷
        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMQUESTIONINPAPER");
        Map<String,Object> map = (Map<String, Object>) baseService.get(isInPaperParam);
        if(map!=null){
            //存在正在考试的试卷
            String paperName = String.valueOf(map.get("paperName"));
            resultMap.put("type","1");
            resultMap.put("paperName",paperName);
            return resultMap;
            //return Result.fail("998001","该试题已经被抽取到"+paperName+"试卷中，暂时不能编辑或删除！");
        }
        //引用当前试题的考过的试卷
        Map<String,Object> isInBeforePaperParam = new HashMap();
        isInBeforePaperParam.put("questionsId", params.get("id"));
        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMQUESTIONINPAPERBEFORE");
        List<Map<String,Object>> list = (List<Map<String,Object>>) baseService.list(isInBeforePaperParam);
        if(list!= null && list.size() > 0){
            String msg = "该试题在已结束的考试试卷中有使用,如果修改可能会影响到警员查看以往考试信息！";
            resultMap.put("type","2");
            resultMap.put("data",msg);
            return resultMap;
            //已经考过的卷子存在试题的引用
            //return Result.fail("998001","该试题在已结束的考试试卷中有使用,如果修改可能会影响到警员查看以往考试信息！");
        }
        return Result.ok(null);
    }

    //删除试题
    /*
    @param id:id
    @param type:题目类型 1单选2多选3填空4判断 5简答 6 论述 7 案例分析
     */
    @GetMapping("/deletebyid")
    @ResponseBody
    public Object deleteQuestion(@RequestParam Map<String, Object> params) throws Exception {
        validParams(params);
        Map<String,Object> resultMap = new HashMap<>();
        //判断是否有引用
        /*
        编辑和删除时，检查该试题当前是否在进行中考试试卷中有使用，如果使用，
        提示：该试题已经被抽取到XXXX（试卷名称）
        试卷中，暂时不能编辑或删除！（知道了）
        如果未在进行中的考试试卷中使用，
        在已结束的考试试卷中有使用，
        提示：该试题在已结束的考试试卷中有使用，
        如果修改可能会影响到警员查看以往考试信息！是否继续修改？（确认/取消）
         */
        Map<String,Object> isInPaperParam = new HashMap();
        isInPaperParam.put("questionsId", params.get("id"));
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

        //物理删除映射表
        deleteMappings(String.valueOf(params.get("id")));
        //逻辑删除试题表
        if("1".equals(String.valueOf(params.get("type"))) || "2".equals(String.valueOf(params.get("type")))){
            LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMCHOICES");
            Map<String,Object> delMap = new HashMap<>();
            delMap.put("delFlag",1);
            return Result.ok(baseService.update(String.valueOf(params.get("id")),delMap));
        }
        if("3".equals(String.valueOf(params.get("type")))) {
            //填空
            LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMFILLGAPS");
            Map<String,Object> delMap = new HashMap<>();
            delMap.put("delFlag",1);
            return Result.ok(baseService.update(String.valueOf(params.get("id")),delMap));
        }
        if("4".equals(String.valueOf(params.get("type")))) {
            //判断
            LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMJUDGE");
            Map<String,Object> delMap = new HashMap<>();
            delMap.put("delFlag",1);
            return Result.ok(baseService.update(String.valueOf(params.get("id")),delMap));
        }
        if("5".equals(String.valueOf(params.get("type"))) || "6".equals(String.valueOf(params.get("type")))
                || "7".equals(String.valueOf(params.get("type")))) {
            LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMDISCUSS");
            Map<String,Object> delMap = new HashMap<>();
            delMap.put("delFlag",1);
            return Result.ok(baseService.update(String.valueOf(params.get("id")),delMap));
        }
        return Result.ok(null);
    }
    private void validParams(Map<String, Object> requestBody) {
        ValidationUtils.notNull(requestBody.get("id"), "题目Id不能为空!");
        ValidationUtils.notNull(requestBody.get("type"), "题目类型不能为空!");
    }

    private void deleteMappings(String questionId) throws Exception {
        Map<String,Object> mapping = new HashMap<>();
        //试题Id
        mapping.put("questionsId",questionId);
        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMSUBJECTCATEGORYMAPPING");
        baseService.remove(mapping);
    }
}
