/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.nmghr.controller;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.page.Paging;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.rmdb.datasource.TargetDataSource;
import com.nmghr.controller.vo.UserScoreInfo;
import com.nmghr.service.UserDeptService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * <功能描述/> 考试管理
 *
 * @author wangpengwei
 * @date 2019年9月24日 下午4:44:51
 * @version 1.0
 */
@RestController
@RequestMapping("/examination")
public class ExaminationStatisticsController {

    @Autowired
    @Qualifier("baseService")
    private IBaseService baseService;

    @Autowired
    @Qualifier("userdeptService")
    private UserDeptService userdeptService;

    @GetMapping("statistics")
    public Object statistics(@RequestParam Map<String, Object> requestParam) throws Exception {

        int pageNum = 1, pageSize = 15;
        if (requestParam.get("pageNum") != null && !"".equals(String.valueOf(requestParam.get("pageNum")).trim())) {
            pageNum = Integer.parseInt(String.valueOf(requestParam.get("pageNum")));
        }
        if (requestParam.get("pageSize") != null && !"".equals(String.valueOf(requestParam.get("pageSize")).trim())) {
            pageSize = Integer.parseInt(String.valueOf(requestParam.get("pageSize")));
        }

        //根据条件查考试
        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMSTATISTICS");
        Paging paging = (Paging) baseService.page(requestParam, pageNum, pageSize);

        List<Object> allReset = new ArrayList<Object>();
        List<Map<String, Object>> examinationList = paging.getList();
        List<UserScoreInfo> allPersons = new ArrayList<>();
        //查应考人数
        for (Map<String, Object> examination : examinationList) {
            String openDepts = (String) examination.get("openDepts");
            Map<String, Object> totalNumParMap = new HashMap<>();
            totalNumParMap.put("depts", openDepts);
            Object totalNum = userdeptService.get(totalNumParMap);
            if (totalNum != null) {
                examination.put("totalNum", totalNum);
            }
            if (examination.get("examinationId") != null) {
                LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMUSERSCORE");
                Map<String, Object> para = new HashMap<>();
                para.put("examId", examination.get("examinationId"));
                List<Map<String, Object>> userScoreList = (List<Map<String, Object>>) baseService.list(para);
                if (userScoreList != null && userScoreList.size() > 0) {
                    //查到实考人员
                    examination.put("realNum", userScoreList.size());
                    //根据考试统计，确定考试对应人的成绩状态：优、良、中、差
                    List<UserScoreInfo> list = statisticsUser(userScoreList, examination);
                    allPersons.addAll(list);
                } else {
                    examination.put("realNum", 0);
                }
            }

        }

        //根据地市统计
        List<List<Map<String, Object>>>  cityList = statisticsUserByCity(allPersons);
        allReset.add(examinationList);
        allReset.add(cityList);
        return allReset;
    }

    private List<List<Map<String, Object>>> statisticsUserByCity(List<UserScoreInfo> info) throws Exception {

        Integer yscore = 0;
        Integer lscore = 0;
        Integer zscore = 0;
        Integer cscore = 0;
        Integer realNum = 0;

        Map<String,Object> map = new HashMap<String,Object>();
        List<List<Map<String, Object>>> list = new ArrayList<List<Map<String, Object>>>();
        //考试的人
        List<Map<String,Object>> cityList = (List<Map<String, Object>>) getCitys();
        if(CollectionUtils.isNotEmpty(cityList)){
            for (Map<String, Object> city : cityList) {
                String deptId = String.valueOf(city.get("deptId"));
                Map<String, Object> cityChildMap = new HashMap<>();
                cityChildMap.put("cityId", deptId);
                List<Map<String, Object>> cityChildList = (List<Map<String, Object>>) userdeptService.page(cityChildMap, 0, 0);
                list.add(cityChildList);
                //所有子部门
                for (Map<String, Object> childCity : cityChildList) {
                        yscore = 0;
                        lscore = 0;
                        zscore = 0;
                        cscore = 0;
                        realNum = 0;
                    //CityAnalysisVo vo = new CityAnalysisVo();
                    for (UserScoreInfo scoreInfo : info) {
                        if(childCity.get("deptCode").equals(scoreInfo.getDeptCode())){
                            realNum++;
                        }

                        if(childCity.get("deptCode").equals(scoreInfo.getDeptCode()) && "1".equals(scoreInfo.getFlag())) {
                            yscore++;
                        }
                        if(childCity.get("deptCode").equals(scoreInfo.getDeptCode()) && "2".equals(scoreInfo.getFlag())) {
                            lscore++;
                        }
                        if(childCity.get("deptCode").equals(scoreInfo.getDeptCode()) && "3".equals(scoreInfo.getFlag())) {
                            zscore++;
                        }
                        if(childCity.get("deptCode").equals(scoreInfo.getDeptCode()) && "4".equals(scoreInfo.getFlag())) {
                            cscore++;
                        }

                    }
                    childCity.put("y", yscore);
                    childCity.put("l", lscore);
                    childCity.put("z", zscore);
                    childCity.put("c", cscore);
                    childCity.put("realNum",realNum);
                }
            }
            list.add(cityList);
        }
        return list;
}


private int judgeRange(Long score) throws Exception {
    Integer yscore = 0;
    Integer lscore = 0;
    Integer zscore = 0;
    Integer cscore = 0;

    //获取分数等级
    List<Map<String, Object>> scoreCodeList = getScoreRange();
    for (Map<String, Object> scoreRange : scoreCodeList) {
        if ("1".equals(String.valueOf(scoreRange.get("scoreRange")))) {
            yscore = Integer.valueOf(String.valueOf(scoreRange.get("code")));
        }
        if ("2".equals(String.valueOf(scoreRange.get("scoreRange")))) {
            lscore = Integer.valueOf(String.valueOf(scoreRange.get("code")));
        }
        if ("3".equals(String.valueOf(scoreRange.get("scoreRange")))) {
            zscore = Integer.valueOf(String.valueOf(scoreRange.get("code")));
        }
        if ("4".equals(String.valueOf(scoreRange.get("scoreRange")))) {
            cscore = Integer.valueOf(String.valueOf(scoreRange.get("code")));
        }
    }

    if (score >= yscore) {
       return 1;
    }
    if (score > lscore && score < yscore) {
       return 2;
    }
    if (score > zscore && score < lscore) {
        return 3;
    }
    if (score <= cscore) {
        return 4;
    }
    return  -1;
}

    //统计id确定的一场考试的人
    private List<UserScoreInfo> statisticsUser(List<Map<String, Object>> userScoreList, Map<String, Object> examination) throws Exception {
        //查西安的所有地区

        Integer ycount = 0;
        Integer lcount = 0;
        Integer zcount = 0;
        Integer ccount = 0;

//        //获取分数等级
//        List<Map<String, Object>> scoreCodeList = getScoreRange();
//        for (Map<String, Object> scoreRange : scoreCodeList) {
//            if ("1".equals(String.valueOf(scoreRange.get("scoreRange")))) {
//                yscore = Integer.valueOf(String.valueOf(scoreRange.get("code")));
//            }
//            if ("2".equals(String.valueOf(scoreRange.get("scoreRange")))) {
//                lscore = Integer.valueOf(String.valueOf(scoreRange.get("code")));
//            }
//            if ("3".equals(String.valueOf(scoreRange.get("scoreRange")))) {
//                zscore = Integer.valueOf(String.valueOf(scoreRange.get("code")));
//            }
//            if ("4".equals(String.valueOf(scoreRange.get("scoreRange")))) {
//                cscore = Integer.valueOf(String.valueOf(scoreRange.get("code")));
//            }
//        }
            /*
            2、	支持对应考人员、实考人员及考试结果（优、良、中、差）；
            优：85及以上、良：75-84、中：65-74
            差：64及以下；（暂时只提供在数据库配置）
             */
        //记录这场考试的人的分数等级
        List<UserScoreInfo> userInfoList = new ArrayList<>();
        for (Map<String, Object> userScore : userScoreList) {
            UserScoreInfo info = new UserScoreInfo();
            Long totalScore = (Long) userScore.get("totalScore");
            info.setDeptCode(userScore.get("deptCode") +"");
            if (Long.valueOf(judgeRange(totalScore)) == 1) {

                info.setFlag("1");
                ycount++;
//                UserScoreInfo userScoreInfo = new UserScoreInfo();
//                userScoreInfo.setRange(1);
//                userScoreInfo.setUserId(String.valueOf(userScore.get("userId")));
//                userScoreInfo.setDeptCode(String.valueOf(userScore.get("deptCode")));
//                userInfoList.add(userScoreInfo);
            }
            if (Long.valueOf(judgeRange(totalScore)) == 2) {
                lcount++;
                info.setFlag("2");
//                UserScoreInfo userScoreInfo = new UserScoreInfo();
//                userScoreInfo.setRange(2);
//                userScoreInfo.setUserId(String.valueOf(userScore.get("userId")));
//                userScoreInfo.setDeptCode(String.valueOf(userScore.get("deptCode")));
//                userInfoList.add(userScoreInfo);
            }
            if (Long.valueOf(judgeRange(totalScore)) == 3) {
                zcount++;
                info.setFlag("3");
//                UserScoreInfo userScoreInfo = new UserScoreInfo();
//                userScoreInfo.setRange(3);
//                userScoreInfo.setUserId(String.valueOf(userScore.get("userId")));
//                userScoreInfo.setDeptCode(String.valueOf(userScore.get("deptCode")));
//                userInfoList.add(userScoreInfo);
            }
            if (Long.valueOf(judgeRange(totalScore)) == 4) {
                info.setFlag("4");
//                UserScoreInfo userScoreInfo = new UserScoreInfo();
//                userScoreInfo.setRange(4);
//                userScoreInfo.setUserId(String.valueOf(userScore.get("userId")));
//                userScoreInfo.setDeptCode(String.valueOf(userScore.get("deptCode")));
//                userInfoList.add(userScoreInfo);
                ccount++;
            }
            examination.put("y", ycount);
            examination.put("l", lcount);
            examination.put("z", zcount);
            examination.put("c", ccount);
            userInfoList.add(info);
        }
        return userInfoList;
    }
    //查西安所有地区
    @TargetDataSource(value="hrupms")
    private Object getCitys() throws Exception {
        List<Map<String,Object>> cityList = (List<Map<String, Object>>) userdeptService.list(null);
        return cityList;
    }

    private List<Map<String, Object>> getScoreRange() throws Exception {
        //查编码表
        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "SCORERANGE");
        Map<String,Object> para = new HashMap<>();
        List<Map<String,Object>> scoreCodeList = (List<Map<String, Object>>) baseService.list(para);
        return scoreCodeList;
    }


}
