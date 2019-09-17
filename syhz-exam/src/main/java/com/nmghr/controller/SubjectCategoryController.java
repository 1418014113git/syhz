/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.nmghr.controller;
import java.util.Map;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.nmghr.basic.core.util.ValidationUtils;



/** <功能描述/>
 *  题库类型控层
 * @author wangpengwei  
 * @date 2019年9月17日 下午7:24:25 
 * @version 1.0   
 */
@RestController
@RequestMapping("/subjectCategory")
public class SubjectCategoryController {

	@PutMapping("save")
	public void save(@RequestBody Map<String, Object> requestBody) throws Exception {
	  //校验表单数据
      validParams(requestBody);
	}

	private void validParams(Map<String, Object> requestBody) {
	  ValidationUtils.notNull(requestBody.get("categoryName"), "题库类型不能为空!");
	  ValidationUtils.notNull(requestBody.get("sort"), "题库类型排序次序不能为空!");
	}
}
