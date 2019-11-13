/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nmghr.service;

import com.nmghr.basic.common.Result;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.rmdb.datasource.TargetDataSource;
import com.nmghr.hander.mapper.UserDeptMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <功能描述/>
 *
 * @author weber
 * @version 1.0
 * @date 2019年4月29日 下午3:19:43
 */
@Service("userdeptService")
public class UserDeptService implements IBaseService {

    @Autowired
    @Resource
    UserDeptMapper userDeptMapper;

    /*
    获取某部门的所有子部门
     */
    @Override
    @TargetDataSource(value = "hrupms")
    public Object save(Map<String, Object> requestMap) throws Exception {
        if (requestMap.get("deptCode") == null) {
            return Result.fail("880088", "父级部门编号不能为空");
        }
        return userDeptMapper.getChildByDeptCode(String.valueOf(requestMap.get("deptCode")));
    }

    @Override
    public Object update(String id, Map<String, Object> requestBody) throws Exception {


        return null;
    }

    @Override
    public Object update(Map<String, Object> requestParam, Map<String, Object> requestBody)
            throws Exception {
        return null;
    }
    @Override
    public void remove(Map<String, Object> requestMap) throws Exception {

    }
    @Override
    public void remove(String id) throws Exception {
    }
    //查某些地区的部门人数
    @Override
    @TargetDataSource(value = "hrupms")
    public Object get(String deptIds) throws Exception {
        List<Map<String, Object>> userCountList = new ArrayList<>();
        String[] deptArr = deptIds.split(",");
        for (String s : deptArr) {
            Map<String, Object> userCountMap = (Map<String, Object>) userDeptMapper.getCityChildTotalNumByDeptId(s);
            userCountList.add(userCountMap);
        }
        return userCountList;
    }

    @SuppressWarnings("unchecked")
/*
用deptCode查所有子部门（多级）
 */
    @Override
    @TargetDataSource(value = "hrupms")
    public Object list(Map<String, Object> map) throws Exception {
        if (map.get("deptCode") == null || "".equals(map.get("deptCode"))) {
            return null;
        }
        String deptCode = String.valueOf(map.get("deptCode"));
        Map<String, Object> depts = getChildDept(deptCode);
        return depts;
    }

    private Map<String, Object> getChildDept(String deptCode) {
        Map<String, Object> dept = userDeptMapper.getDeptInfo(deptCode);
        //总队
        if(dept!=null) {
            if ("1".equals(String.valueOf(dept.get("departType")))) {
                //查支队
                List<Map<String, Object>> zdDepts = userDeptMapper.getChildByDeptCode(deptCode);
                if (zdDepts != null && zdDepts.size() > 0) {
                    for (Map<String, Object> zd : zdDepts) {
                        //查大队
                        List<Map<String, Object>> ddDepts = userDeptMapper.getChildByDeptCode(String.valueOf(zd.get("deptCode")));
                        if (ddDepts != null && ddDepts.size() > 0) {
                            for (Map<String, Object> dd : ddDepts) {
                                //查派出所
                                List<Map<String, Object>> pcss = userDeptMapper.getChildByDeptCode(String.valueOf(dd.get("deptCode")));
                                dd.put("child", pcss);
                            }
                        }
                        zd.put("child", ddDepts);
                    }
                }
                dept.put("child", zdDepts);
                return dept;
            }
            //支队
            if ("2".equals(String.valueOf(dept.get("departType")))) {
                //查到大队
                List<Map<String, Object>> ddDepts = userDeptMapper.getChildByDeptCode(deptCode);
                if (ddDepts != null && ddDepts.size() > 0) {
                    for (Map<String, Object> dd : ddDepts) {
                        //查派出所
                        List<Map<String, Object>> pcss = userDeptMapper.getChildByDeptCode(String.valueOf(dd.get("deptCode")));
                        dd.put("child", pcss);
                    }
                }
                dept.put("child", ddDepts);
                return dept;
            }
            //大队
            if ("3".equals(String.valueOf(dept.get("departType")))) {
                //查派出所
                List<Map<String, Object>> pcss = userDeptMapper.getChildByDeptCode(deptCode);
                dept.put("child", pcss);
                return dept;
            }
            //派出所
            if ("4".equals(String.valueOf(dept.get("departType")))) {
                return dept;
            }
        }
        return new HashMap<>();
}
    /*
    用areaName查子部门
     */
    @Override
    @TargetDataSource(value = "hrupms")
    public Object page(Map<String, Object> requestMap, int currentPage, int pageSize)
            throws Exception {
        if (requestMap.get("cityId") == null) {
            return Result.fail("880088", "父级部门Id不能为空");
        }
        return userDeptMapper.getCityChild(String.valueOf(requestMap.get("areaName")), String.valueOf(requestMap.get("cityId")));
    }

    @Override
    @TargetDataSource(value = "hrupms")
    public Object get(Map<String, Object> requestMap) throws Exception {
        if (requestMap.get("depts") == null) {
            return Result.fail("880088", "开放部门不能为空");
        }
        Map<String, Object> totalNumMap = userDeptMapper.getTotalNum(String.valueOf(requestMap.get("depts")));
        if (totalNumMap != null && totalNumMap.get("totalNum") != null) {
            String totalNum = String.valueOf(totalNumMap.get("totalNum"));
            return totalNum;
        } else
            return null;
    }

    //查总队和各个支队
    @Override
    @TargetDataSource(value = "hrupms")
    public Object findAll() throws Exception {
        return userDeptMapper.getAllCitys();
    }

    @Override
    public Object getSequence(String seqName) throws Exception {
        return null;
    }

}
