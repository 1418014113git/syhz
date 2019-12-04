/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.nmghr.controller.app;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.nmghr.entity.operation.OperationResult;
import com.nmghr.entity.operation.OperationResultData;
import com.nmghr.entity.query.QueryResult;
import com.nmghr.service.app.UserService;
import com.nmghr.util.app.AppVerifyUtils;
import com.nmghr.util.app.Result;
import com.nmghr.vo.OperationRequestVo;
import com.nmghr.vo.QueryRequestVo;

/**
 * app-用户信息
 *
 * @author kaven
 * @date 2019年11月23日 下午2:30:18
 * @version 1.0
 */
@RestController
public class AppMessageController {

	@Autowired
	private UserService UserService;

	public Object getUserMessage(QueryRequestVo queryRequestVo, Map<String, Object> requestBody,
			Map<String, Object> conditionMap) throws Exception {
		// 将请求参照转化为Vo
		String sign = "";
		String sourceId = AppVerifyUtils.getQuerySourceId(queryRequestVo);
		// 查询
		Map<String, Object> responseMap = (Map<String, Object>) UserService
				.get(String.valueOf(conditionMap.get("userName")));
		List<Map<String, Object>> messageList = new ArrayList<Map<String, Object>>();
		messageList.add(responseMap);
		QueryResult result = AppVerifyUtils.setQueryResult(sign, sourceId, messageList);
		return Result.ok(queryRequestVo.getJsonrpc(), queryRequestVo.getId(), result);

	}

	public Object updateUserMessage(OperationRequestVo operationRequestVo, Map<String, Object> requestBody,
			Map<String, Object> operationMap) throws Exception {

		UserService.update("1", operationMap);
		OperationResult result = new OperationResult();
		List<OperationResultData> operations = new ArrayList<OperationResultData>();
		OperationResultData operationResultData = new OperationResultData();
		operationResultData
				.setOperationId(operationRequestVo.getParams().getData().getOperations().get(0).getOperationId());
		operationResultData.setOperationCode("1");
		operations.add(operationResultData);
		result.setCode("1");
		result.setMsg("OK");
		result.setOperations(operations);
		return Result.ok(operationRequestVo.getJsonrpc(), operationRequestVo.getId(), result);

	}
}
