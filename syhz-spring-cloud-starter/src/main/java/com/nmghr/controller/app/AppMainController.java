/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.nmghr.controller.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.common.AppeErrorException;
import com.nmghr.entity.operation.OperationResult;
import com.nmghr.entity.operation.OperationResultData;
import com.nmghr.entity.query.QueryResult;
import com.nmghr.service.app.AppCaseServise;
import com.nmghr.service.app.AppClueServise;
import com.nmghr.service.app.AppGroupService;
import com.nmghr.service.app.AppMessageService;
import com.nmghr.service.app.DeptServise;
import com.nmghr.service.app.NoticeServise;
import com.nmghr.service.app.UserService;
import com.nmghr.util.SyhzUtil;
import com.nmghr.util.app.AppObjectIdUtil;
import com.nmghr.util.app.AppVerifyUtils;
import com.nmghr.util.app.Result;
import com.nmghr.util.app.SyhzAppErrorEnmu;
import com.nmghr.vo.OperationRequestVo;
import com.nmghr.vo.QueryRequestVo;

/**
 * 陕西环食药APP入门
 *
 * @author kaven
 * @date 2019年11月26日 上午10:15:07
 * @version 1.0
 */
@RestController
@RequestMapping("/hsyzapp")
public class AppMainController {

	@Autowired
	private IBaseService baseService;
	@Autowired
	private UserService UserService;// 人员
	@Autowired
	private DeptServise DeptServise;// 部门
	@Autowired
	private AppMessageService AppMessageService;// 消息
	@Autowired
	private NoticeServise NoticeServise;// 通知
	@Autowired
	private AppGroupService AppGroupService;// 常用组
	@Autowired
	private AppCaseServise AppCaseServise;// 案件
	@Autowired
	private AppClueServise AppClueServise;// 线索

	private static final String METHOD_QUERY = "query";
	private static final String METHOD_OPERATE = "operate";

	private static AppObjectIdUtil AppObjectIdUtil;

	@PostMapping(value = "/main")
	@ResponseBody
	public Object appMain(@RequestBody Map<String, Object> requestBody) {
		String method = SyhzUtil.setDate(requestBody.get("method"));
		if (METHOD_QUERY.equals(method)) { // 查询
			// 将请求参照转化为Vo
			QueryRequestVo queryRequestVo = QueryRequestVo.dataToVo(requestBody);
			try {
				// 验证查询请求参数的必填项
				AppVerifyUtils.verifyQueryParams(queryRequestVo);

				String getDataObjId = AppVerifyUtils.getQueryDataObjId(queryRequestVo);

				Map<String, Object> conditionMap = new HashMap<String, Object>();
				// userId = '1' and deptCode='610000530000'
				// 获取查询条件组织为map
				AppVerifyUtils.getQueryCondition(conditionMap, queryRequestVo);
				return getQueryMethod(getDataObjId, queryRequestVo, requestBody, conditionMap);
			} catch (AppeErrorException e) {
				e.printStackTrace();
				return Result.fail(queryRequestVo.getJsonrpc(), queryRequestVo.getId(), e.getCode(), e.getMessage());
			} catch (Exception e) {
				e.printStackTrace();
				return Result.fail(queryRequestVo.getJsonrpc(), queryRequestVo.getId(),
						SyhzAppErrorEnmu.ERROR_500.getCode(), SyhzAppErrorEnmu.ERROR_500.getMessage());
			}
		} else if (METHOD_OPERATE.equals(method)) { // 操作
			// 将参数转化为operationVo
			OperationRequestVo operationRequestVo = OperationRequestVo.dataToVo(requestBody);
			try {
				// 验证参数
				AppVerifyUtils.verifyOperationParams(operationRequestVo);
				Map<String, Object> conditionMap = new HashMap<String, Object>();
				// 获取参数
				AppVerifyUtils.getOperationCondition(conditionMap, operationRequestVo);
				String getDataObjId = AppVerifyUtils.getOperateDataObjId(operationRequestVo);

				return getOperateMethod(getDataObjId, operationRequestVo, requestBody, conditionMap);

			} catch (AppeErrorException e) {
				e.printStackTrace();
				return Result.fail(operationRequestVo.getJsonrpc(), operationRequestVo.getId(), e.getCode(),
						e.getMessage());
			} catch (Exception e) {
				e.printStackTrace();
				return Result.fail(operationRequestVo.getJsonrpc(), operationRequestVo.getId(),
						SyhzAppErrorEnmu.ERROR_500.getCode(), SyhzAppErrorEnmu.ERROR_500.getMessage());
			}
		}
		return Result.fail(SyhzUtil.setDate(requestBody.get("jsonrpc")), SyhzUtil.setDate(requestBody.get("id")),
				SyhzAppErrorEnmu.ERROR_500.getCode(), SyhzAppErrorEnmu.ERROR_500.getMessage());
	}

	private Object getQueryMethod(String getDataObjId, QueryRequestVo queryRequestVo, Map<String, Object> requestBody,
			Map<String, Object> conditionMap) throws Exception {
		// 将请求参照转化为Vo
		String sign = "";
		String sourceId = AppVerifyUtils.getQuerySourceId(queryRequestVo);

		// 查询
		List<Map<String, Object>> messageList = new ArrayList<Map<String, Object>>();
		if (AppObjectIdUtil.getPersonMessage().equals(getDataObjId)) {// 获取字典
			return AppMessageService.getpersonMessage(queryRequestVo, requestBody, conditionMap, baseService);
		} else if (AppObjectIdUtil.getUserQuery().equals(getDataObjId)) {// 获取用户信息
			return getUserMessage(queryRequestVo, requestBody, conditionMap);
		} else if (AppObjectIdUtil.getMessagePage().equals(getDataObjId)) {// 获取站内消息
			return AppMessageService.getMessageList(queryRequestVo, requestBody, conditionMap, baseService);
		} else if (AppObjectIdUtil.getMessageDetail().equals(getDataObjId)) {// 获取站内消息详情
			return AppMessageService.getMessageDetail(queryRequestVo, requestBody, conditionMap, baseService);
		} else if (AppObjectIdUtil.getMessages().equals(getDataObjId)) {// 获取站内消息详情
			return AppMessageService.getMessagesList(queryRequestVo, requestBody, conditionMap, baseService);
		} else if (AppObjectIdUtil.getNoticeHome().equals(getDataObjId)) {// 获取站内通知
			return NoticeServise.home(queryRequestVo, requestBody, conditionMap, baseService);
		} else if (AppObjectIdUtil.getNoticeList().equals(getDataObjId)) {// 获取站内列表
			return NoticeServise.list(queryRequestVo, requestBody, conditionMap, baseService);
		} else if (AppObjectIdUtil.getNoticeDetail().equals(getDataObjId)) {// 站内通知详情
			return NoticeServise.detail(queryRequestVo, requestBody, conditionMap, baseService);
		} else if (AppObjectIdUtil.getNoticeFlowList().equals(getDataObjId)) {// 站内通知审核记录
			return NoticeServise.basemessageflowlsit(queryRequestVo, requestBody, conditionMap, baseService);
		} else if (AppObjectIdUtil.getNoticeSignList().equals(getDataObjId)) {// 站内通知签收记录
			return NoticeServise.basemessagesign(queryRequestVo, requestBody, conditionMap, baseService);
		} else if (AppObjectIdUtil.getDeptChildren().equals(getDataObjId)) {// 获取子部门
			return getUserDeptMessage(queryRequestVo, requestBody, conditionMap);
		} else if (AppObjectIdUtil.getMessageQuery().equals(getDataObjId)) {// 获取站内消息详情
			return AppMessageService.query(queryRequestVo, requestBody, conditionMap, baseService);
		} else if (AppObjectIdUtil.getNoticeVaildName().equals(getDataObjId)) {// 名字校验重复
			return NoticeServise.validName(queryRequestVo, requestBody, conditionMap, baseService);
		} else if (AppObjectIdUtil.getGroupInfo().equals(getDataObjId)) {// 查询常用组
			return AppGroupService.getGroup(queryRequestVo, requestBody, conditionMap, baseService);
		} else if (AppObjectIdUtil.getGroupVaildName().equals(getDataObjId)) {// 名字校验重复
			return AppGroupService.checkRepeat(queryRequestVo, requestBody, conditionMap, baseService);
		} else if (AppObjectIdUtil.getDeptUser().equals(getDataObjId)) {// 获取子部门
			return getDeptUser(queryRequestVo, requestBody, conditionMap);
		} else if (AppObjectIdUtil.getCasemanageCaselist().equals(getDataObjId)) {// 案件列表
			return AppCaseServise.caseList(queryRequestVo, requestBody, conditionMap, baseService);
		} else if (AppObjectIdUtil.getCasemanageCasedetail().equals(getDataObjId)) {// 案件详情
			return AppCaseServise.caseDetail(queryRequestVo, requestBody, conditionMap, baseService);
		} else if (AppObjectIdUtil.getCasemanageCaseajzm().equals(getDataObjId)) {// 案件罪名
			return AppCaseServise.caseAjzm(queryRequestVo, requestBody, conditionMap, baseService);
		} else if (AppObjectIdUtil.getCasemanageCaseajzmCode().equals(getDataObjId)) {// 案件罪名code
			return AppCaseServise.caseAjzmCode(queryRequestVo, requestBody, conditionMap, baseService);
		} else if (AppObjectIdUtil.getCasemanageCaseajlb().equals(getDataObjId)) {// 案件类别
			return AppCaseServise.caseAjlb(queryRequestVo, requestBody, conditionMap, baseService);
		} else if (AppObjectIdUtil.getCasemanageCasetcpcode().equals(getDataObjId)) {// 案例code
			return AppCaseServise.caseTcpcode(queryRequestVo, requestBody, conditionMap, baseService);
		} else if (AppObjectIdUtil.getDeparttree().equals(getDataObjId)) {// 修改常用组
			return getDeptTree(queryRequestVo, requestBody, conditionMap);
		} else if (AppObjectIdUtil.getGroupDetail().equals(getDataObjId)) {// 修改常用组
			return AppGroupService.getGroupDetail(queryRequestVo, requestBody, conditionMap, baseService);
		} else if (AppObjectIdUtil.getCasemanageCasegroup().equals(getDataObjId)) {// 案例code
			return AppCaseServise.workGroup(queryRequestVo, requestBody, conditionMap, baseService);
		} else if (AppObjectIdUtil.getClueList().equals(getDataObjId)) {// 案例code
			return AppClueServise.getClusList(queryRequestVo, requestBody, conditionMap, baseService);
		} else if (AppObjectIdUtil.getClueDetail().equals(getDataObjId)) {// 案例code
			return AppClueServise.getClueDetail(queryRequestVo, requestBody, conditionMap, baseService);
		}
		return null;
	}

	private Object getOperateMethod(String getDataObjId, OperationRequestVo operationRequestVo,
			Map<String, Object> requestBody, Map<String, Object> operationMap) throws Exception {
		if (AppObjectIdUtil.getMessageDel().equals(getDataObjId)) {// 消息删除
			return AppMessageService.updateMessageInfo(operationRequestVo, requestBody, operationMap, baseService);
		} else if (AppObjectIdUtil.getUserUpdate().equals(getDataObjId)) {// 编辑用户信息
			return updateUserMessage(operationRequestVo, requestBody, operationMap);
		} else if (AppObjectIdUtil.getNoticeSignSave().equals(getDataObjId)) {// 站内通知签收
			return NoticeServise.sign(operationRequestVo, requestBody, operationMap, baseService);
		} else if (AppObjectIdUtil.getMessageDelete().equals(getDataObjId)) {// 站内通知删除
			return AppMessageService.delete(operationRequestVo, requestBody, operationMap, baseService);
		} else if (AppObjectIdUtil.getMessageSend().equals(getDataObjId)) {// 站内通知发送
			return AppMessageService.send(operationRequestVo, requestBody, operationMap, baseService);
		} else if (AppObjectIdUtil.getNoticeSave().equals(getDataObjId)) {// 站内通知添加
			return NoticeServise.save(operationRequestVo, requestBody, operationMap);
		} else if (AppObjectIdUtil.getGroupDel().equals(getDataObjId)) {// 删除常用组
			return AppGroupService.delGroup(operationRequestVo, requestBody, operationMap, baseService);
		} else if (AppObjectIdUtil.getMessagesStatus().equals(getDataObjId)) {// 删除常用组
			return AppMessageService.updateMessageStaus(operationRequestVo, requestBody, operationMap, baseService);
		} else if (AppObjectIdUtil.getGroupUpdate().equals(getDataObjId)) {// 删除常用组
			return AppGroupService.updateGroup(operationRequestVo, requestBody, operationMap, baseService);
		} else if (AppObjectIdUtil.getGroupSave().equals(getDataObjId)) {// 删除常用组
			return AppGroupService.saveGroup(operationRequestVo, requestBody, operationMap, baseService);
		} else if (AppObjectIdUtil.getClueSave().equals(getDataObjId)) {// 删除常用组
			return AppClueServise.ClueSave(operationRequestVo, requestBody, operationMap, baseService);
		}
		return "";
	}

	public Object getUserMessage(QueryRequestVo queryRequestVo, Map<String, Object> requestBody,
			Map<String, Object> conditionMap) throws Exception {
		// 将请求参照转化为Vo
		String sign = "";
		String sourceId = AppVerifyUtils.getQuerySourceId(queryRequestVo);
		// 查询
		Map<String, Object> responseMap = (Map<String, Object>) UserService
				.get(String.valueOf(conditionMap.get("userName")));
		List<Map<String, Object>> messageList = new ArrayList<Map<String, Object>>();
		if (responseMap != null) {
			messageList.add(responseMap);
		}
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

	public Object getUserDeptMessage(QueryRequestVo queryRequestVo, Map<String, Object> requestBody,
			Map<String, Object> conditionMap) throws Exception {
		// 将请求参照转化为Vo
		String sign = "";
		String sourceId = AppVerifyUtils.getQuerySourceId(queryRequestVo);
		// 查询
		List<Map<String, Object>> messageList = (List<Map<String, Object>>) UserService.list(conditionMap);
		QueryResult result = AppVerifyUtils.setQueryResult(sign, sourceId, messageList);
		return Result.ok(queryRequestVo.getJsonrpc(), queryRequestVo.getId(), result);
	}

	public Object getDeptUser(QueryRequestVo queryRequestVo, Map<String, Object> requestBody,
			Map<String, Object> conditionMap) throws Exception {
		// 将请求参照转化为Vo
		String sign = "";
		String sourceId = AppVerifyUtils.getQuerySourceId(queryRequestVo);
		// 查询
		List<Map<String, Object>> messageList = (List<Map<String, Object>>) UserService.page(conditionMap, 1, 1);
		QueryResult result = AppVerifyUtils.setQueryResult(sign, sourceId, messageList);
		return Result.ok(queryRequestVo.getJsonrpc(), queryRequestVo.getId(), result);
	}

	public Object getDeptTree(QueryRequestVo queryRequestVo, Map<String, Object> requestBody,
			Map<String, Object> conditionMap) throws Exception {
		// 将请求参照转化为Vo
		String sign = "";
		String sourceId = AppVerifyUtils.getQuerySourceId(queryRequestVo);
		// 查询
		List<Map<String, Object>> messageList = (List<Map<String, Object>>) DeptServise.list(conditionMap);
		QueryResult result = AppVerifyUtils.setQueryResult(sign, sourceId, messageList);
		return Result.ok(queryRequestVo.getJsonrpc(), queryRequestVo.getId(), result);
	}

}
