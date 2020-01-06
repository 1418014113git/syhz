/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.nmghr.controller;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.common.Result;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.rmdb.datasource.TargetDataSource;
import com.nmghr.controller.vo.UserScoreInfo;
import com.nmghr.service.UserDeptService;
import com.nmghr.util.ListPageUtil;
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
 * @version 1.0
 * @date 2019年9月24日 下午4:44:51
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
        List<Map<String, Object>> requestList = (List<Map<String, Object>>) baseService.list(requestParam);

        Map<String, Object> responseMap = new HashMap<String, Object>();
        responseMap.put("pageSize", pageSize);
        responseMap.put("pageNum", pageNum);
        responseMap.put("totalCount", requestList.size());
        if (requestList == null || requestList.size() == 0) {
        } else {
            ListPageUtil<Map<String, Object>> listPageUtil = new ListPageUtil<Map<String, Object>>(requestList, pageNum,
                    pageSize);
            requestList = listPageUtil.getPagedList();
        }

        //查分数等级
        List<Map<String, Object>> scoreCodeList = getScoreRange();
        List<Map<String, Object>> examinationList = requestList;
        List<UserScoreInfo> allPersons = new ArrayList<>();
        //查应考人数
        if (examinationList != null && examinationList.size() > 0) {
            for (Map<String, Object> examination : examinationList) {
                String openDepts = (String) examination.get("openDepts");
                Map<String, Object> totalNumParMap = new HashMap<>();
                totalNumParMap.put("depts", openDepts);
                Object totalNum = userdeptService.get(totalNumParMap);
                if (totalNum != null) {
                    examination.put("totalNum", totalNum);
                }
                if (examination.get("examinationId") != null) {
                    Map<String, Object> para = new HashMap<>();
                    para.put("examId", Integer.valueOf(String.valueOf(examination.get("examinationId"))));
                    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMUSERSCORE");
                    List<Map<String, Object>> userScoreList = (List<Map<String, Object>>) baseService.list(para);
                    if (userScoreList != null && userScoreList.size() > 0) {
                        //查到实考人员
                        examination.put("realNum", userScoreList.size());
                        //根据考试统计，确定考试对应人的成绩状态：优、良、中、差
                        List<UserScoreInfo> list = statisticsUser(userScoreList, examination, scoreCodeList);
                        allPersons.addAll(list);
                    } else {
                        examination.put("realNum", 0);
                        examination.put("y", 0);
                        examination.put("l", 0);
                        examination.put("z", 0);
                        examination.put("c", 0);
                    }
                }
            }
        }
        responseMap.put("list", examinationList);
        return responseMap;
    }

    @GetMapping("statisticsOne")
    public Object statisticsOne(@RequestParam Map<String, Object> requestParam) throws Exception {
        if (requestParam.get("examinationIds") == null) {
            return Result.fail("808888", "考试Id不能为空");
        }
        List<UserScoreInfo> allPersons = new ArrayList<>();
        //查分数等级
        List<Map<String, Object>> scoreCodeList = getScoreRange();
        //根据条件查考试
        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMSTATISTICS");
        List<Map<String, Object>> examinationList = (List<Map<String, Object>>) baseService.list(requestParam);
        //取当前考试
        if (examinationList != null && examinationList.size() > 0) {
            for (Map<String, Object> examination : examinationList) {
                //查应考人数
                if (examination != null) {
                    String openDepts = (String) examination.get("openDepts");
                    Map<String, Object> totalNumParMap = new HashMap<>();
                    totalNumParMap.put("depts", openDepts);
                    Object totalNum = userdeptService.get(totalNumParMap);
                    if (totalNum != null) {
                        examination.put("totalNum", totalNum);
                    }
                    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMUSERSCORE");
                    Map<String, Object> para = new HashMap<>();
                    para.put("examId", examination.get("examinationId"));
                    List<Map<String, Object>> userScoreList = (List<Map<String, Object>>) baseService.list(para);
                    if (userScoreList != null && userScoreList.size() > 0) {
                        //查到实考人员
                        examination.put("realNum", userScoreList.size());
                        //根据考试统计，确定单场考试对应人的成绩状态：优、良、中、差（Id确定的某次考试）
                        List<UserScoreInfo> list = statisticsUser(userScoreList, examination, scoreCodeList);
                        allPersons.addAll(list);
                    }
                }
            }
            List<Map<String, Object>> cityList = statisticsUserByCity(allPersons, String.valueOf(requestParam.get("examinationIds")));
            return cityList;
        } else {
            return new ArrayList<>();
        }
    }


    @GetMapping("statisticsIndex")
    public Object statisticsIndex(@RequestParam Map<String, Object> requestParam) throws Exception {

        List<Map<String, Object>> allscores = new ArrayList<>();
        //查所有考试
        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMSTATISTICS");
        List<Map<String, Object>> examinationList = (List<Map<String, Object>>) baseService.list(requestParam);
        //取当前考试
        if (examinationList != null && examinationList.size() > 0) {
            for (Map<String, Object> examination : examinationList) {
                //查应考人数
                if (examination != null) {
                    String openDepts = (String) examination.get("openDepts");
                    Map<String, Object> totalNumParMap = new HashMap<>();
                    totalNumParMap.put("depts", openDepts);
                    Object totalNum = userdeptService.get(totalNumParMap);
                    if (totalNum != null) {
                        examination.put("totalNum", totalNum);
                    }
                    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMUSERSCORE");
                    Map<String, Object> para = new HashMap<>();
                    para.put("examId", examination.get("examinationId"));
                    List<Map<String, Object>> userScoreList = (List<Map<String, Object>>) baseService.list(para);
                    if (userScoreList != null && userScoreList.size() > 0) {
                        allscores.addAll(userScoreList);
                    }
                }
            }
            List<Map<String, Object>> cityList = statisticsIndexByCity(allscores, examinationList);
            return cityList;
        } else {
            return new ArrayList<>();
        }
    }

    private List<Map<String, Object>> statisticsUserByCity(List<UserScoreInfo> info, String examinationIds) throws Exception {
        int yscore = 0; //优人数,大队或派出所
        int lscore = 0;
        int zscore = 0;
        int cscore = 0;
        int cityyscore = 0;//支队 优人数
        int citylscore = 0;
        int cityzscore = 0;
        int citycscore = 0;
        int realNum = 0; //大队,派出所实考人数
        int cityRealNum = 0;//支队实考人数
        int cityTotalNum = 0;//支队应考人数

        //所有考试的开放部门
        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "EXAMCHILDCITYSBYEXAMID");
        Map<String, Object> param = new HashMap<>();
        param.put("examinationIds", examinationIds);
        Map<String, Object> depts = (Map<String, Object>) baseService.get(param);
        String deptsStr = (String) depts.get("depts");
        //获取这些部门的deptCode和人数
        List<Map<String, Object>> totalCountList = (List<Map<String, Object>>) userdeptService.get(deptsStr);
        //查总队 和各个支队
        List<Map<String, Object>> cityList = (List<Map<String, Object>>) getAllCitys();

        if (CollectionUtils.isNotEmpty(cityList)) {
            for (Map<String, Object> city : cityList) {
                //市级别统计
                cityTotalNum = 0;
                cityRealNum = 0;
                cityyscore = 0;
                citylscore = 0;
                cityzscore = 0;
                citycscore = 0;
                for (UserScoreInfo userScoreInfo : info) {
                    if (city.get("deptCode").equals(userScoreInfo.getDeptCode())) {
                        cityRealNum++;
                    }
                    if (city.get("deptCode").equals(userScoreInfo.getDeptCode()) && "1".equals(userScoreInfo.getFlag())) {
                        cityyscore++;
                    }
                    if (city.get("deptCode").equals(userScoreInfo.getDeptCode()) && "2".equals(userScoreInfo.getFlag())) {
                        citylscore++;
                    }
                    if (city.get("deptCode").equals(userScoreInfo.getDeptCode()) && "3".equals(userScoreInfo.getFlag())) {
                        cityzscore++;
                    }
                    if (city.get("deptCode").equals(userScoreInfo.getDeptCode()) && "4".equals(userScoreInfo.getFlag())) {
                        citycscore++;
                    }
                }
                for (Map<String, Object> totalCountMap : totalCountList) {
                    // 遍历所有开放部门 获取 支队应考人数，没有去重，应该累加
                    if (totalCountMap.get("departCode") != null && totalCountMap.get("departCode").equals(city.get("deptCode"))) {
                        cityTotalNum += Integer.valueOf(String.valueOf(totalCountMap.get("totalNum")));
                    }
                }
                //查开放部门的人数，即应考人数 支队
                String deptId = String.valueOf(city.get("deptId"));
                String areaName = String.valueOf(city.get("areaName"));
                if (!"null".equals(deptId) && !"null".equals(areaName)) {

                    //查出市的所有子部门 包括 大队和派出所
                    Map<String, Object> cityChildMap = new HashMap<>();
                    cityChildMap.put("cityId", deptId);
                    cityChildMap.put("areaName", areaName);
                    List<Map<String, Object>> cityChildList = (List<Map<String, Object>>) userdeptService.page(cityChildMap, 0, 0);
                    for (Map<String, Object> childCity : cityChildList) {
                        //大队或派出所
                        int totalNum = 0;
                        for (Map<String, Object> totalCountMap : totalCountList) {
                            /// 遍历所有开放部门 获取 大队或派出所应考人数，没有去重，应该累加
                            if (totalCountMap.get("departCode") != null && totalCountMap.get("departCode").equals(childCity.get("deptCode"))) {
                                totalNum += Integer.valueOf(String.valueOf(totalCountMap.get("totalNum")));
                            }
                        }
                        yscore = 0;
                        lscore = 0;
                        zscore = 0;
                        cscore = 0;
                        realNum = 0;
                        for (UserScoreInfo scoreInfo : info) {
                            if (childCity.get("deptCode").equals(scoreInfo.getDeptCode())) {
                                realNum++;
                            }
                            if (childCity.get("deptCode").equals(scoreInfo.getDeptCode()) && "1".equals(scoreInfo.getFlag())) {
                                yscore++;
                            }
                            if (childCity.get("deptCode").equals(scoreInfo.getDeptCode()) && "2".equals(scoreInfo.getFlag())) {
                                lscore++;
                            }
                            if (childCity.get("deptCode").equals(scoreInfo.getDeptCode()) && "3".equals(scoreInfo.getFlag())) {
                                zscore++;
                            }
                            if (childCity.get("deptCode").equals(scoreInfo.getDeptCode()) && "4".equals(scoreInfo.getFlag())) {
                                cscore++;
                            }
                        }
                        childCity.put("y", yscore);
                        childCity.put("l", lscore);
                        childCity.put("z", zscore);
                        childCity.put("c", cscore);
                        childCity.put("realNum", realNum);
                        childCity.put("totalNum", totalNum);
                    }

                    Map<String, Object> self = new HashMap<>();
                    //self代表支队,放到city的 child里面,后面有用
                    self.put("deptName", city.get("deptName"));
                    self.put("realNum", cityRealNum);
                    self.put("totalNum", cityTotalNum);
                    self.put("y", cityyscore);
                    self.put("l", citylscore);
                    self.put("z", cityzscore);
                    self.put("c", citycscore);
                    self.put("deptCode", city.get("deptCode"));
                    cityChildList.add(self);
                    city.put("child", cityChildList);
                    city.put("y", cityyscore);
                    city.put("l", citylscore);
                    city.put("z", cityzscore);
                    city.put("c", citycscore);
                    city.put("realNum", cityRealNum);
                    city.put("totalNum", cityTotalNum);
                    city.put("self", self);
                }
            }
        }


        //整理子部门数据(统计后)
        for (Map<String, Object> city : cityList) {
            int pcsRealNum = 0;
            int pcsTotalNum = 0;
            int pcsy = 0;
            int pcsl = 0;
            int pcsz = 0;
            int pcsc = 0;

            //支队的大队集合
            List<Map<String, Object>> ddList = new ArrayList<>();
            //支队的派出所集合
            List<Map<String, Object>> pcsList = new ArrayList<>();
            //过滤总队
            if (String.valueOf(city.get("deptCode")).equals("610000530000")) {
                continue;
            }
            //得到大队及派出所，最后一个元素是支队信息
            List<Map<String, Object>> cityChildList = (List<Map<String, Object>>) city.get("child");
            for (Map<String, Object> child : cityChildList) {
                if ("3".equals(String.valueOf(child.get("departType")))) {
                    //所有大队
                    ddList.add(child);
                }
                if ("4".equals(String.valueOf(child.get("departType")))) {
                    //所有派出所
                    pcsList.add(child);
                }
            }
            //查支队下的派出所
            List<Map<String, Object>> cityPcsList = getPcsByParentCode(String.valueOf(city.get("deptCode")), pcsList);
            if (cityPcsList != null && cityPcsList.size() > 0) {
                for (Map<String, Object> pcs : cityPcsList) {
                    //累加派出所的应考，实考，优良中查到支队
                    pcsRealNum += Integer.valueOf(String.valueOf(pcs.get("realNum")));
                    pcsTotalNum += Integer.valueOf(String.valueOf(pcs.get("totalNum")));
                    pcsy += Integer.valueOf(String.valueOf(pcs.get("y")));
                    pcsl += Integer.valueOf(String.valueOf(pcs.get("l")));
                    pcsz += Integer.valueOf(String.valueOf(pcs.get("z")));
                    pcsc += Integer.valueOf(String.valueOf(pcs.get("c")));

                }
            }
            city.put("realNum", Integer.valueOf(String.valueOf(city.get("realNum"))) + pcsRealNum);
            city.put("totalNum", Integer.valueOf(String.valueOf(city.get("totalNum"))) + pcsTotalNum);
            city.put("y", Integer.valueOf(String.valueOf(city.get("y"))) + pcsy);
            city.put("l", Integer.valueOf(String.valueOf(city.get("l"))) + pcsl);
            city.put("z", Integer.valueOf(String.valueOf(city.get("z"))) + pcsz);
            city.put("c", Integer.valueOf(String.valueOf(city.get("c"))) + pcsc);


            //遍历大队，得到大队下的派出所 累加到大队
            if (ddList != null && ddList.size() > 0) {
                for (Map<String, Object> ddMap : ddList) {
                    //派出所实考
                    int ddpcsRealNum = 0;
                    //派出所应考
                    int ddpcsTotalNum = 0;
                    int ddpcsy = 0;
                    int ddpcsl = 0;
                    int ddpcsz = 0;
                    int ddpcsc = 0;
                    //查大队下的派出所
                    List<Map<String, Object>> child = getPcsByParentCode(String.valueOf(ddMap.get("deptCode")), pcsList);
                    if (child != null && child.size() > 0) {
                        for (Map<String, Object> pcs : child) {
                            ddpcsRealNum += Integer.valueOf(String.valueOf(pcs.get("realNum")));
                            ddpcsTotalNum += Integer.valueOf(String.valueOf(pcs.get("totalNum")));
                            ddpcsy += Integer.valueOf(String.valueOf(pcs.get("y")));
                            ddpcsl += Integer.valueOf(String.valueOf(pcs.get("l")));
                            ddpcsz += Integer.valueOf(String.valueOf(pcs.get("z")));
                            ddpcsc += Integer.valueOf(String.valueOf(pcs.get("c")));
                        }
                    }
                    ddMap.put("realNum", Integer.valueOf(String.valueOf(ddMap.get("realNum"))) + ddpcsRealNum);
                    ddMap.put("totalNum", Integer.valueOf(String.valueOf(ddMap.get("totalNum"))) + ddpcsTotalNum);
                    ddMap.put("y", Integer.valueOf(String.valueOf(ddMap.get("y"))) + ddpcsy);
                    ddMap.put("l", Integer.valueOf(String.valueOf(ddMap.get("l"))) + ddpcsl);
                    ddMap.put("z", Integer.valueOf(String.valueOf(ddMap.get("z"))) + ddpcsz);
                    ddMap.put("c", Integer.valueOf(String.valueOf(ddMap.get("c"))) + ddpcsc);
                }
            }
            Map<String, Object> self = (Map<String, Object>) city.get("self");
            ddList.add(self);
            city.put("child", ddList);
            city.remove("self");
        }
        return cityList;
    }

    //首页统计
    private List<Map<String, Object>> statisticsIndexByCity(List<Map<String, Object>> info, List<Map<String, Object>> examinationList) throws Exception {

        List<Map<String, Object>> childCityList = new ArrayList<>();
        int realNum = 0;
        int examCount = 0;
        int cityRealNum = 0;
        int cityExamCount = 0;
        //查所有地区,包括总队
        List<Map<String, Object>> cityList = (List<Map<String, Object>>) getAllCitys();
        if (CollectionUtils.isNotEmpty(cityList)) {
            for (Map<String, Object> city : cityList) {
                cityRealNum = 0;
                cityExamCount = 0;
                for (Map<String, Object> scoreInfo : info) {
                    if (String.valueOf(city.get("deptCode")).equals(String.valueOf(scoreInfo.get("deptCode")))) {
                        cityRealNum++;
                    }
                }
                for (Map<String, Object> examination : examinationList) {
                    String examDeptCode = (String) examination.get("deptCode");
                    if (String.valueOf(city.get("deptCode")).equals(examDeptCode)) {
                        cityExamCount++;
                    }
                }
                String deptId = String.valueOf(city.get("deptId"));
                String areaName = String.valueOf(city.get("areaName"));
                if (!"null".equals(deptId) && !"null".equals(areaName)) {
                    Map<String, Object> cityChildMap = new HashMap<>();
                    cityChildMap.put("cityId", deptId);
                    cityChildMap.put("areaName", areaName);
                    List<Map<String, Object>> cityChildList = (List<Map<String, Object>>) userdeptService.page(cityChildMap, 0, 0);

                    for (Map<String, Object> childCity : cityChildList) {
                        realNum = 0;
                        examCount = 0;
                        for (Map<String, Object> scoreInfo : info) {
                            if (String.valueOf(childCity.get("deptCode")).equals(String.valueOf(scoreInfo.get("deptCode")))) {
                                realNum++;
                            }
                        }
                        for (Map<String, Object> examination : examinationList) {
                            String examDeptCode = (String) examination.get("deptCode");
                            if (String.valueOf(childCity.get("deptCode")).equals(examDeptCode)) {
                                examCount++;
                            }
                        }
                        childCity.put("realNum", realNum);
                        childCity.put("examCount", examCount);
                        cityRealNum += realNum;
                        cityExamCount += examCount;
                    }
                }
                city.put("examCount", cityExamCount);
                city.put("realNum", cityRealNum);
            }
        }
        return cityList;
    }

    private int judgeRange(Long score, List<Map<String, Object>> scoreCodeList) throws Exception {

        Integer yscore = 0;
        Integer lscore = 0;
        Integer zscore = 0;
        Integer cscore = 0;

        //获取分数等级
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
        if (score >= lscore && score < yscore) {
            return 2;
        }
        if (score >= zscore && score < lscore) {
            return 3;
        }
        if (score <= cscore) {
            return 4;
        }
        return -1;
    }

    //统计id确定的一场考试的人
    private List<UserScoreInfo> statisticsUser(List<Map<String, Object>> userScoreList, Map<String, Object> examination, List<Map<String, Object>> scoreCodeList) throws Exception {
        Integer ycount = 0;
        Integer lcount = 0;
        Integer zcount = 0;
        Integer ccount = 0;
        //记录这场考试的人的分数等级
        List<UserScoreInfo> userInfoList = new ArrayList<>();
        for (Map<String, Object> userScore : userScoreList) {
            UserScoreInfo info = new UserScoreInfo();
            Long totalScore = (Long) userScore.get("totalScore");
            info.setDeptCode(userScore.get("deptCode") + "");
            if (Long.valueOf(judgeRange(totalScore, scoreCodeList)) == 1) {
                info.setFlag("1");
                ycount++;
            }
            if (Long.valueOf(judgeRange(totalScore, scoreCodeList)) == 2) {
                lcount++;
                info.setFlag("2");
            }
            if (Long.valueOf(judgeRange(totalScore, scoreCodeList)) == 3) {
                zcount++;
                info.setFlag("3");
            }
            if (Long.valueOf(judgeRange(totalScore, scoreCodeList)) == 4) {
                info.setFlag("4");
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

    //查所有地市
    @TargetDataSource(value = "hrupms")
    private Object getCitys() throws Exception {
        List<Map<String, Object>> cityList = (List<Map<String, Object>>) userdeptService.list(null);
        return cityList;
    }

    //查所有地市包括总队
    @TargetDataSource(value = "hrupms")
    private Object getAllCitys() throws Exception {
        List<Map<String, Object>> cityList = (List<Map<String, Object>>) userdeptService.findAll();
        return cityList;
    }

    private List<Map<String, Object>> getScoreRange() throws Exception {
        //查编码表
        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "SCORERANGE");
        Map<String, Object> para = new HashMap<>();
        List<Map<String, Object>> scoreCodeList = (List<Map<String, Object>>) baseService.list(para);
        return scoreCodeList;
    }

    //查大队或支队的派出所,参数父级部门Code,所有派出所
    private List<Map<String, Object>> getPcsByParentCode(String parentCode, List<Map<String, Object>> arr) {
        List<Map<String, Object>> pcss = new ArrayList<>();
        if (arr != null && arr.size() > 0) {
            for (Map<String, Object> pcs : arr) {
                if (parentCode.equals(String.valueOf(pcs.get("parentCode")))) {
                    pcss.add(pcs);
                }
            }
            return pcss;
        }
        return pcss;
    }
}
