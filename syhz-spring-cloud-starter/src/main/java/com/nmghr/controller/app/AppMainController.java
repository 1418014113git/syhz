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
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.common.AppeErrorException;
import com.nmghr.entity.operation.OperationResult;
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
	private static final String METHOD_CONNECT = "connect";

	private static AppObjectIdUtil AppObjectIdUtil;

	@PostMapping(value = "/hsyzapp/main")
	@ResponseBody
	public String appMain(@RequestBody Map<String, Object> requestBody) {
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
				return (String) getQueryMethod(getDataObjId, queryRequestVo, requestBody, conditionMap);
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

				return (String) getOperateMethod(getDataObjId, operationRequestVo, requestBody, conditionMap);

			} catch (AppeErrorException e) {
				e.printStackTrace();
				return Result.fail(operationRequestVo.getJsonrpc(), operationRequestVo.getId(), e.getCode(),
						e.getMessage());
			} catch (Exception e) {
				e.printStackTrace();
				return Result.fail(operationRequestVo.getJsonrpc(), operationRequestVo.getId(),
						SyhzAppErrorEnmu.ERROR_500.getCode(), SyhzAppErrorEnmu.ERROR_500.getMessage());
			}
		} else if (METHOD_CONNECT.equals(method)) {
			Map<String, Object> resultMap = (Map<String, Object>) requestBody.get("params");
			Map<String, Object> responseMap = new HashMap<String, Object>();

			resultMap.put("code", 1);
			resultMap.put("msg", "OK");
			Map<String, Object> data = (Map<String, Object>) resultMap.get("data");
			data.remove("version");
			data.put("sessionId", String.valueOf(UUID.randomUUID()).replace("-", ""));
			resultMap.put("data", data);
			return Result.ok(String.valueOf(requestBody.get("jsonrpc")), String.valueOf(requestBody.get("id")),
					resultMap);
		}
		return Result.fail(SyhzUtil.setDate(requestBody.get("jsonrpc")), SyhzUtil.setDate(requestBody.get("id")),
				SyhzAppErrorEnmu.ERROR_500.getCode(), SyhzAppErrorEnmu.ERROR_500.getMessage());
	}

	@GetMapping(value = "/hsyzapp/main")
	@ResponseBody
	public Object ping() {
		return null;
	}

	private Object getQueryMethod(String getDataObjId, QueryRequestVo queryRequestVo, Map<String, Object> requestBody,
			Map<String, Object> conditionMap) throws Exception {
		// 将请求参照转化为Vo
		String sign = "";
		String sourceId = AppVerifyUtils.getQuerySourceId(queryRequestVo);

		// 查询
		List<Map<String, Object>> messageList = new ArrayList<Map<String, Object>>();
		if (AppObjectIdUtil.PERSON_MESSAGE.equals(getDataObjId)) {// 获取字典
			return AppMessageService.getpersonMessage(queryRequestVo, requestBody, conditionMap, baseService);
		} else if (AppObjectIdUtil.USER_QUERY.equals(getDataObjId)) {// 获取用户信息
			return getUserMessage(queryRequestVo, requestBody, conditionMap);
		} else if (AppObjectIdUtil.MESSAGE_PAGE.equals(getDataObjId)) {// 获取站内消息
			return AppMessageService.getMessageList(queryRequestVo, requestBody, conditionMap, baseService);
		} else if (AppObjectIdUtil.MESSAGE_DETAIL.equals(getDataObjId)) {// 获取站内消息详情
			return AppMessageService.getMessageDetail(queryRequestVo, requestBody, conditionMap, baseService);
		} else if (AppObjectIdUtil.MESSAGES.equals(getDataObjId)) {// 获取站内消息详情
			return AppMessageService.getMessagesList(queryRequestVo, requestBody, conditionMap, baseService);
		} else if (AppObjectIdUtil.NOTICE_HOME.equals(getDataObjId)) {// 获取站内通知
			return NoticeServise.home(queryRequestVo, requestBody, conditionMap, baseService);
		} else if (AppObjectIdUtil.NOTICE_LIST.equals(getDataObjId)) {// 获取站内列表
			return NoticeServise.list(queryRequestVo, requestBody, conditionMap, baseService);
		} else if (AppObjectIdUtil.NOTICE_DETAIL.equals(getDataObjId)) {// 站内通知详情
			return NoticeServise.detail(queryRequestVo, requestBody, conditionMap, baseService);
		} else if (AppObjectIdUtil.NOTICE_FLOW_LIST.equals(getDataObjId)) {// 站内通知审核记录
			return NoticeServise.basemessageflowlsit(queryRequestVo, requestBody, conditionMap, baseService);
		} else if (AppObjectIdUtil.NOTICE_SIGN_LIST.equals(getDataObjId)) {// 站内通知签收记录
			return NoticeServise.basemessagesign(queryRequestVo, requestBody, conditionMap, baseService);
		} else if (AppObjectIdUtil.DEPT_CHILDREN.equals(getDataObjId)) {// 获取子部门
			return getUserDeptMessage(queryRequestVo, requestBody, conditionMap);
		} else if (AppObjectIdUtil.MESSAGE_QUERY.equals(getDataObjId)) {// 获取站内消息详情
			return AppMessageService.query(queryRequestVo, requestBody, conditionMap, baseService);
		} else if (AppObjectIdUtil.NOTICE_VAILD_NAME.equals(getDataObjId)) {// 名字校验重复
			return NoticeServise.validName(queryRequestVo, requestBody, conditionMap, baseService);
		} else if (AppObjectIdUtil.GROUP_INFO.equals(getDataObjId)) {// 查询常用组
			return AppGroupService.getGroup(queryRequestVo, requestBody, conditionMap, baseService);
		} else if (AppObjectIdUtil.GROUP_VAILD_NAME.equals(getDataObjId)) {// 名字校验重复
			return AppGroupService.checkRepeat(queryRequestVo, requestBody, conditionMap, baseService);
		} else if (AppObjectIdUtil.DEPT_USER.equals(getDataObjId)) {// 获取子部门
			return getDeptUser(queryRequestVo, requestBody, conditionMap);
		} else if (AppObjectIdUtil.CASEMANAGE_CASELIST.equals(getDataObjId)) {// 案件列表
			return AppCaseServise.caseList(queryRequestVo, requestBody, conditionMap, baseService);
		} else if (AppObjectIdUtil.CASEMANAGE_CASEDETAIL.equals(getDataObjId)) {// 案件详情
			return AppCaseServise.caseDetail(queryRequestVo, requestBody, conditionMap, baseService);
		} else if (AppObjectIdUtil.CASEMANAGE_CASEAJZM.equals(getDataObjId)) {// 案件罪名
			return AppCaseServise.caseAjzm(queryRequestVo, requestBody, conditionMap, baseService);
		} else if (AppObjectIdUtil.CASEMANAGE_CASEAJZM_CODE.equals(getDataObjId)) {// 案件罪名code
			return AppCaseServise.caseAjzmCode(queryRequestVo, requestBody, conditionMap, baseService);
		} else if (AppObjectIdUtil.CASEMANAGE_CASEAJLB.equals(getDataObjId)) {// 案件类别
			return AppCaseServise.caseAjlb(queryRequestVo, requestBody, conditionMap, baseService);
		} else if (AppObjectIdUtil.CASEMANAGE_CASETCPCODE.equals(getDataObjId)) {// 案例code
			return AppCaseServise.caseTcpcode(queryRequestVo, requestBody, conditionMap, baseService);
		} else if (AppObjectIdUtil.DEPARTTREE.equals(getDataObjId)) {// 查询地市下部门
			return getDeptTree(queryRequestVo, requestBody, conditionMap);
		} else if (AppObjectIdUtil.GROUP_Detail.equals(getDataObjId)) {// 常用组详情
			return AppGroupService.getGroupDetail(queryRequestVo, requestBody, conditionMap, baseService);
		} else if (AppObjectIdUtil.CASEMANAGE_CASEGROUP.equals(getDataObjId)) { // 首页待审核
			return AppCaseServise.workGroup(queryRequestVo, requestBody, conditionMap, baseService);
		} else if (AppObjectIdUtil.CLUE_LIST.equals(getDataObjId)) {// 线索列表
			return AppClueServise.getClusList(queryRequestVo, requestBody, conditionMap, baseService);
		} else if (AppObjectIdUtil.CLUE_DETAIL.equals(getDataObjId)) {// 线索详情
			return AppClueServise.getClueDetail(queryRequestVo, requestBody, conditionMap, baseService);
		}
		return null;
	}

	private Object getOperateMethod(String getDataObjId, OperationRequestVo operationRequestVo,
			Map<String, Object> requestBody, Map<String, Object> operationMap) throws Exception {
		if (AppObjectIdUtil.MESSAGE_DEL.equals(getDataObjId)) {// 消息删除
			return AppMessageService.updateMessageInfo(operationRequestVo, requestBody, operationMap, baseService);
		} else if (AppObjectIdUtil.USER_UPDATE.equals(getDataObjId)) {// 编辑用户信息
			return updateUserMessage(operationRequestVo, requestBody, operationMap);
		} else if (AppObjectIdUtil.NOTICE_SIGN_SAVE.equals(getDataObjId)) {// 站内通知签收
			return NoticeServise.sign(operationRequestVo, requestBody, operationMap, baseService);
		} else if (AppObjectIdUtil.MESSAGE_DELETE.equals(getDataObjId)) {// 站内通知删除
			return AppMessageService.delete(operationRequestVo, requestBody, operationMap, baseService);
		} else if (AppObjectIdUtil.MESSAGE_SEND.equals(getDataObjId)) {// 站内通知发送
			return AppMessageService.send(operationRequestVo, requestBody, operationMap, baseService);
		} else if (AppObjectIdUtil.NOTICE_SAVE.equals(getDataObjId)) {// 站内通知添加
			return NoticeServise.save(operationRequestVo, requestBody, operationMap);
		} else if (AppObjectIdUtil.GROUP_DEL.equals(getDataObjId)) {// 删除常用组
			return AppGroupService.delGroup(operationRequestVo, requestBody, operationMap, baseService);
		} else if (AppObjectIdUtil.MESSAGES_STATUS.equals(getDataObjId)) {// 已读
			return AppMessageService.updateMessageStaus(operationRequestVo, requestBody, operationMap, baseService);
		} else if (AppObjectIdUtil.GROUP_UPDATE.equals(getDataObjId)) {// 修改常用组
			return AppGroupService.updateGroup(operationRequestVo, requestBody, operationMap, baseService);
		} else if (AppObjectIdUtil.GROUP_SAVE.equals(getDataObjId)) {// 添加常用组
			return AppGroupService.saveGroup(operationRequestVo, requestBody, operationMap, baseService);
		} else if (AppObjectIdUtil.CLUE_SAVE.equals(getDataObjId)) {// 添加线索
			return AppClueServise.ClueSave(operationRequestVo, requestBody, operationMap, baseService);
		}
		return "";
	}

	// 获取用户信息
	public Object getUserMessage(QueryRequestVo queryRequestVo, Map<String, Object> requestBody,
			Map<String, Object> conditionMap) throws Exception {

		// 查询
		Map<String, Object> responseMap = (Map<String, Object>) UserService
				.get(String.valueOf(conditionMap.get("userName")));
		List<Map<String, Object>> messageList = new ArrayList<Map<String, Object>>();
		if (responseMap != null) {
			messageList.add(responseMap);
		}
		QueryResult result = AppVerifyUtils.setQueryResult(queryRequestVo, messageList);
		return Result.ok(queryRequestVo.getJsonrpc(), queryRequestVo.getId(), result);

	}

	// 修改用户信息
	public Object updateUserMessage(OperationRequestVo operationRequestVo, Map<String, Object> requestBody,
			Map<String, Object> operationMap) throws Exception {

		UserService.update("1", operationMap);
		OperationResult result = AppVerifyUtils.setOperatorReult(operationRequestVo);
		return Result.ok(operationRequestVo.getJsonrpc(), operationRequestVo.getId(), result);

	}

	// 获取用户部门
	public Object getUserDeptMessage(QueryRequestVo queryRequestVo, Map<String, Object> requestBody,
			Map<String, Object> conditionMap) throws Exception {
		// 查询
		List<Map<String, Object>> messageList = (List<Map<String, Object>>) UserService.list(conditionMap);
		QueryResult result = AppVerifyUtils.setQueryResult(queryRequestVo, messageList);
		return Result.ok(queryRequestVo.getJsonrpc(), queryRequestVo.getId(), result);
	}

	// 获取部门下用户
	public Object getDeptUser(QueryRequestVo queryRequestVo, Map<String, Object> requestBody,
			Map<String, Object> conditionMap) throws Exception {
		// 查询
		List<Map<String, Object>> messageList = (List<Map<String, Object>>) UserService.page(conditionMap, 1, 1);
		QueryResult result = AppVerifyUtils.setQueryResult(queryRequestVo, messageList);
		return Result.ok(queryRequestVo.getJsonrpc(), queryRequestVo.getId(), result);
	}

	// 获取省市区下部门
	public Object getDeptTree(QueryRequestVo queryRequestVo, Map<String, Object> requestBody,
			Map<String, Object> conditionMap) throws Exception {
		// 查询
		List<Map<String, Object>> messageList = (List<Map<String, Object>>) DeptServise.list(conditionMap);
		QueryResult result = AppVerifyUtils.setQueryResult(queryRequestVo, messageList);
		return Result.ok(queryRequestVo.getJsonrpc(), queryRequestVo.getId(), result);
	}

}
