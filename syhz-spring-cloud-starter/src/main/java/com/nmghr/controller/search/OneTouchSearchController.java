/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.nmghr.controller.search;

import com.nmghr.basic.common.Result;
import com.nmghr.basic.core.service.handler.IQueryHandler;
import com.nmghr.basic.core.util.SpringUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 一键搜索
 *
 * @author mxd.
 * @date 2020-01-14 - 14:58.
 */
@RestController
@RequestMapping("/oneTouchSearch")
public class OneTouchSearchController {

    @GetMapping(value = "/query")
    public Object query(@RequestParam Map<String, Object> params) throws Exception {
        if (ObjectUtils.isEmpty(params.get("search"))) {
            return Result.fail("999989", "关键字不能为空");
        }
        if (ObjectUtils.isEmpty(params.get("resourceType"))) {
            return Result.fail("999989", "资源类型不能为空");
        }
        if (ObjectUtils.isEmpty(params.get("pageSize"))) {
            return Result.fail("999989", "每页行数不能为空");
        }
        if (ObjectUtils.isEmpty(params.get("pageNum"))) {
            return Result.fail("999989", "页码不能为空");
        }
        if (Integer.valueOf(params.get("pageNum").toString()) == 0) {
            params.put("pageNum", 1);
        }
        IQueryHandler queryHandler = SpringUtils.getBean("oneTouchSearchQueryHandler", IQueryHandler.class);
        return queryHandler.page(params, Integer.valueOf(params.get("pageNum").toString()), Integer.valueOf(params.get("pageSize").toString()));
    }
}
