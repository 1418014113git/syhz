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
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.util.ValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import java.util.*;


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
        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMSIMPLECHOICEBYSUB");
        List<Map<String, Object>> list = (List<Map<String, Object>>) baseService.list(params);
        return list;
        }
      //当前题库所有多选
      if("2".equals(type)){
        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMMUTICHOICEBYSUB");
        List<Map<String, Object>> list = (List<Map<String, Object>>) baseService.list(params);
        return list;
      }
        //当前题库所有填空
      if("3".equals(type)){
          LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMFILLGAPSBYSUB");
          List<Map<String, Object>> list = (List<Map<String, Object>>) baseService.list(params);
          return list;
      }
      //当前题库所有判断
      if("4".equals(type)){
          LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMJUDGEBYSUB");
          List<Map<String, Object>> list = (List<Map<String, Object>>) baseService.list(params);
          return list;
      }
      //5简答6论述7案例分析
      if("5".equals(type) || "6".equals(type) || "7".equals(type)){
          LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMDISCUSSBYSUBANDTYPE");
          List<Map<String, Object>> list = (List<Map<String, Object>>) baseService.list(params);
          return list;
      }

      //所有题目
      if("0".equals(type)){
          List<Map<String, Object>> questions = new ArrayList<>();
          LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMSIMPLECHOICEBYSUB");
          List<Map<String, Object>> simpleChoiceList = (List<Map<String, Object>>) baseService.list(params);
          LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMMUTICHOICEBYSUB");
          List<Map<String, Object>> mutiChoiceList = (List<Map<String, Object>>) baseService.list(params);
          LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMFILLGAPSBYSUB");
          List<Map<String, Object>> fillGapsList = (List<Map<String, Object>>) baseService.list(params);
          LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMJUDGEBYSUB");
          List<Map<String, Object>> judgeList = (List<Map<String, Object>>) baseService.list(params);
          LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMDISCUSSBYSUBANDTYPE");
          params.put("type","5");
          List<Map<String, Object>> jdList = (List<Map<String, Object>>) baseService.list(params);
          params.put("type","6");
          List<Map<String, Object>> lsList = (List<Map<String, Object>>) baseService.list(params);
          params.put("type","7");
          List<Map<String, Object>> alfxList = (List<Map<String, Object>>) baseService.list(params);

          questions.addAll(simpleChoiceList);
          questions.addAll(mutiChoiceList);
          questions.addAll(fillGapsList);
          questions.addAll(judgeList);
          questions.addAll(jdList);
          questions.addAll(lsList);
          questions.addAll(alfxList);
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
        if("5".equals(String.valueOf(params.get("type"))) || "6".equals(String.valueOf(params.get("type")))
        || "7".equals(String.valueOf(params.get("type")))) {
            //判断
            LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMDISCUSS");
            Map<String, Object> discusses = (Map<String, Object>) baseService.get(params);
            if(discusses!=null){
                return discusses;
            }
            return  new HashMap<>();
        }
        return new HashMap<>();
    }

    private void validParams(Map<String, Object> requestBody) {
        ValidationUtils.notNull(requestBody.get("id"), "题目Id不能为空!");
        ValidationUtils.notNull(requestBody.get("type"), "题目类型不能为空!");
    }

}
