/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.nmghr.controller;

import com.nmghr.basic.common.Result;
import com.nmghr.basic.common.exception.GlobalErrorEnum;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.service.UserDeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * <功能描述/> 考试管理
 *
 * @author wangpengwei
 * @version 1.0
 * @date 2019年9月24日 下午4:44:51
 */
@RestController
public class deptController {

    @Autowired
    @Qualifier("baseService")
    private IBaseService baseService;

    @Autowired
    @Qualifier("userdeptService")
    private UserDeptService userdeptService;

    @GetMapping("childDept")
    public Object getChildDept(@RequestParam Map<String, Object> requestParam) throws Exception {
        Map<String,Object> map = new HashMap<>();
        if(requestParam.get("deptCode") == null || "".equals(requestParam.get("deptCode"))){
            return Result.fail(GlobalErrorEnum.PARAM_NOT_VALID.getCode(), "部门编号不能为空");
        }
        Map<String,Object> deptMap = (Map<String, Object>) userdeptService.list(requestParam);
        return deptMap;
    }
}
