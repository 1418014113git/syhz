/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.nmghr.handler.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.common.exception.GlobalErrorEnum;
import com.nmghr.basic.common.exception.GlobalErrorException;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.util.ValidationUtils;
import com.nmghr.handler.vo.WorkflowExamineEntityBo;
import com.nmghr.util.SyhzUtil;

/**
 * 知识库、网上培训 部门审批相关业务处理.
 *
 * @author kaven
 * @date 2019年9月23日 下午8:46:44
 * @version 1.0
 */
@Service("trainWorkorderExamineService")
public class TrainWorkorderExamineService {
	private static final String ALIAS_TRAIN_WORK = "trainWorkorder"; // 审核主体表
	private static final String ALIAS_TRAIN_WORK_FLOW = "trainWorkorderFlow"; // 审核节点表
	private static final String ALIAS_TRAIN_WORK_FLOW_LOG = "trainWorkorderFlowLog"; // 审核节点记录表
	private static final String ALIAS_ARTICLE_STATUS = "articleStatus"; // 审核主体表对应文章id

	private static final String SYSTEM_1 = "1"; // 知识库
	private static final String SYSTEM_2 = "2"; // 网上培训

	private static final String TABLE_NAME_0 = "train_course"; // 默认为 网上培训课程表
	private static final String TABLE_NAME_1 = "train_law_info"; // 1 法律法规
	private static final String TABLE_NAME_2 = "train_industry_info"; // 2行业标准
	private static final String TABLE_NAME_3 = "train_standard_info"; // 3规则制度
	private static final String TABLE_NAME_4 = "train_case_info"; // 4案例指引

	private static final int AUDIT_STATUS_0 = 0; // 0待审核
	private static final int AUDIT_STATUS_1 = 1; // 1审核中
	private static final int AUDIT_STATUS_2 = 2; // 2审核通过
	private static final int AUDIT_STATUS_3 = 3; // 3审核不通过

	/**
	 * 节点审批流转
	 * 
	 * @param headers
	 * @param requestBody
	 * @throws Exception
	 */
	@Transactional
	public Object examineWorkFlowData(IBaseService baseService, Map<String, String> headers,
			Map<String, Object> requestBody) throws Exception {
		String tableIds = SyhzUtil.setDate(requestBody.get("tableId"));
		String[] tableId = tableIds.split(",");
		String workIds = SyhzUtil.setDate(requestBody.get("workId"));
		String[] workId = workIds.split(",");
		String belongSys = SyhzUtil.setDate(requestBody.get("belongSys"));
		for (int i = 0; i < tableId.length; i++) {
			requestBody.put("tableId", tableId[i]);
			requestBody.put("workId", workId[i]);
			if ("1".equals(belongSys)) {
				String belongType = SyhzUtil.setDate(requestBody.get("belongType"));
				String[] belongTypes = belongType.split(",");
				requestBody.put("belongType", belongTypes[i]);
			}
			WorkflowExamineEntityBo workflowExamineEntityBo = verifyData(headers, requestBody);
			if (workflowExamineEntityBo != null) {
				saveWorkorder(baseService, workflowExamineEntityBo);
			}
		}
		return "";
	}

	/**
	 * 验证必要参数是否为空 且转换为对应Bo
	 * 
	 * @param headers
	 * @param requestBody
	 * @return
	 */
	private WorkflowExamineEntityBo verifyData(Map<String, String> headers, Map<String, Object> requestBody)
			throws GlobalErrorException {
		String belongSys = SyhzUtil.setDate(requestBody.get("belongSys"));
		String belongMode = SyhzUtil.setDate(requestBody.get("belongMode"));
		String belongType = SyhzUtil.setDate(requestBody.get("belongType"));
		String tableId = SyhzUtil.setDate(requestBody.get("tableId")); // 对应表id
		String workId = SyhzUtil.setDate(requestBody.get("workId")); // 流程主表id
		String deptAreaCode = SyhzUtil.setDate(requestBody.get("deptAreaCode"));
		String deptCode = SyhzUtil.setDate(requestBody.get("deptCode"));
		String deptName = SyhzUtil.setDate(requestBody.get("deptName"));
		String creationId = SyhzUtil.setDate(requestBody.get("creationId"));
		String creationName = SyhzUtil.setDate(requestBody.get("creationName"));
		String currentAuditType = SyhzUtil.setDate(requestBody.get("currentAuditType")); // 审核状态（1审核中、2审核通过、3审核不通过）
		String remark = SyhzUtil.setDate(requestBody.get("remark")); // 审核意见

		ValidationUtils.notNull(belongSys, "审批所属系统不能为空");
		ValidationUtils.notNull(belongMode, "审批所属模块不能为空");
		ValidationUtils.notNull(belongType, "审批所属类型不能为空");
		ValidationUtils.notNull(deptCode, "审批所属部门不能为空");
		ValidationUtils.notNull(deptAreaCode, "审批部门所属地区不能为空");
		ValidationUtils.notNull(deptName, "审批所属部门名称不能为空");
		ValidationUtils.notNull(tableId, "审批所属数据id不能为空");
		ValidationUtils.notNull(workId, "审批流程id不能为空");
		ValidationUtils.notNull(creationId, "创建人id不能为空");
		ValidationUtils.notNull(creationName, "创建人姓名不能为空");
		ValidationUtils.notNull(currentAuditType, "创建人姓名不能为空");
		ValidationUtils.notNull(remark, "审批意见不能为空");

		WorkflowExamineEntityBo workflowExamineEntityBo = WorkflowExamineEntityBo.workflowExamineMapToEntityBo(headers,
				requestBody);
		return workflowExamineEntityBo;
	}

	private Object saveWorkorder(IBaseService baseService, WorkflowExamineEntityBo workflowExamineEntityBo)
			throws Exception {

		// 获取 审批表主体信息
		Map<String, Object> workorderExamineMap = new HashMap<String, Object>();
		workorderExamineMap.put("workId", workflowExamineEntityBo.getWorkId());
		workorderExamineMap.put("deptCode", workflowExamineEntityBo.getDeptCode());
		workorderExamineMap.put("auditStatus", AUDIT_STATUS_1);

		Map<String, Object> workHeaderMap = getWorkHeader(baseService, workorderExamineMap);
		if (workHeaderMap == null) {
			throw new GlobalErrorException(GlobalErrorEnum.PARAM_NOT_VALID.getCode(), "未知的审批主体信息");
		}

		Map<String, Object> workNodeMap = getWorkNode(baseService, workorderExamineMap);
		if (workNodeMap == null) {
			throw new GlobalErrorException(GlobalErrorEnum.PARAM_NOT_VALID.getCode(), "未知的审批节点信息");
		}

		int currentAuditType = workflowExamineEntityBo.getCurrentAuditType();
		if (AUDIT_STATUS_2 == currentAuditType) { // 审核通过
			AuditSuccess(baseService, workflowExamineEntityBo, workHeaderMap, workNodeMap);
		}

		if (AUDIT_STATUS_3 == currentAuditType) { // 审核不通过
			// 将当前节点 状态设置为 审核不通过 然后在将当前数据复制一份
			AuditFail(baseService, workflowExamineEntityBo, workHeaderMap, workNodeMap);
		}

		return "";
	}

	private void AuditSuccess(IBaseService baseService, WorkflowExamineEntityBo workflowExamineEntityBo,
			Map<String, Object> workHeaderMap, Map<String, Object> workNodeMap) throws Exception {
		Map<String, Object> workorderExamineMap = new HashMap<String, Object>();
		// 获取下有审核工作流转
		String nextFlowId = SyhzUtil.setDate(workNodeMap.get("nextFlowId"));
		if ("0".equals(nextFlowId)) { // 如果 下一级流转是0 则说明审核为最终审批 需修改主体表审核状态为 2审核通过 审核节点表状态为2 对应文章表审核状态为2
			// 修改审批 主体表 审批状态
			String workId = SyhzUtil.setDate(workHeaderMap.get("id"));
			Map<String, Object> workHeaderDataMap = new HashMap<String, Object>();
			workHeaderDataMap.put("auditStatus", AUDIT_STATUS_2);
			workHeaderDataMap.put("currentAuditType", AUDIT_STATUS_2);
			workHeaderDataMap.put("currentAuditNode", AUDIT_STATUS_2);
			workHeaderDataMap.put("currentAuditAreaCode", workflowExamineEntityBo.getDeptAreaCode());
			workHeaderDataMap.put("currentAuditDepCode", workflowExamineEntityBo.getDeptCode());
			workHeaderDataMap.put("lastId", workflowExamineEntityBo.getCreationId());
			workHeaderDataMap.put("lastName", workflowExamineEntityBo.getCreationName());
			workHeaderDataMap.put("remark", workflowExamineEntityBo.getRemark());
			upWorkHeaderData(baseService, workId, workHeaderDataMap);

			// 修改文章信息状态
			String tableId = SyhzUtil.setDate(workHeaderMap.get("tableId"));
			String belongSys = SyhzUtil.setDate(workHeaderMap.get("belongSys"));
			Map<String, Object> articleDataMap = new HashMap<String, Object>();
			articleDataMap.put("auditStatus", AUDIT_STATUS_2);
			articleDataMap.put("lastId", workflowExamineEntityBo.getCreationId());
			articleDataMap.put("lastName", workflowExamineEntityBo.getCreationName());
			articleDataMap.put("remark", workflowExamineEntityBo.getRemark());

			String belongMode = SyhzUtil.setDate(workHeaderMap.get("belongMode")); // 所属模块(知识库包含：1
																					// 法律法规、2行业标准、3规则制度、4案例指引 网上培训：1在线课程
			String tableName = optTableName(belongSys, belongMode);
			if (!"".equals(tableName)) {
				articleDataMap.put("tableName", tableName);
				upArticleData(baseService, tableId, articleDataMap);
				// 审核通过的文章 需要发起消息提醒
				Map<String, Object> msgDataMap = new HashMap<String, Object>();
				sendMsgSaveData(baseService, msgDataMap);
			}

		} else { // 如果下一级流转不是0 则需要将本级设置为 审核通过 将下一级的信息写入至 主体审核表中
			// 查询下一级 审批节点信息
			workorderExamineMap.put("id", nextFlowId);
			Map<String, Object> workNodeNextMap = getWorkNode(baseService, workorderExamineMap);

			// 修改审批 主体表 审批状态为1审核中 将当前主体表的节点设置为下一级的节点的信息
			String workId = SyhzUtil.setDate(workHeaderMap.get("id"));
			Map<String, Object> workHeaderDataMap = new HashMap<String, Object>();
			workHeaderDataMap.put("auditStatus", AUDIT_STATUS_1);
			workHeaderDataMap.put("currentAuditType", AUDIT_STATUS_1);
			workHeaderDataMap.put("currentAuditNode", workNodeNextMap.get("auditDeptNode"));
			workHeaderDataMap.put("currentAuditAreaCode", workNodeNextMap.get("auditAreaCode"));
			workHeaderDataMap.put("currentAuditDepCode", workNodeNextMap.get("auditDeptCode"));
			workHeaderDataMap.put("lastId", workflowExamineEntityBo.getCreationId());
			workHeaderDataMap.put("lastName", workflowExamineEntityBo.getCreationName());
			workHeaderDataMap.put("remark", workflowExamineEntityBo.getRemark());
			upWorkHeaderData(baseService, workId, workHeaderDataMap);
		}

		// 修改本级节点审批状态 为2审核通过
		String workNodeId = SyhzUtil.setDate(workNodeMap.get("id"));
		Map<String, Object> workNodeDataMap = new HashMap<String, Object>();
		workNodeDataMap.put("workId", "");
		workNodeDataMap.put("auditUserId", workflowExamineEntityBo.getCreationId());
		workNodeDataMap.put("auditUserName", workflowExamineEntityBo.getCreationName());
		workNodeDataMap.put("auditStatus", AUDIT_STATUS_2);
		workNodeDataMap.put("lastId", workflowExamineEntityBo.getCreationId());
		workNodeDataMap.put("lastName", workflowExamineEntityBo.getCreationName());
		workNodeDataMap.put("remark", workflowExamineEntityBo.getRemark());
		upWorkNodeData(baseService, workNodeId, workNodeDataMap);

		// 审核通过记录 添加
		folwLogSaveData(baseService, workflowExamineEntityBo, workNodeMap.get("auditDeptNode"), AUDIT_STATUS_2);
	}

	/**
	 * 修改审核主体表数据
	 * 
	 * @param baseService
	 * @param workId
	 * @param dataMap
	 * @throws Exception
	 */
	private void upWorkHeaderData(IBaseService baseService, String workId, Map<String, Object> dataMap)
			throws Exception {
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS_TRAIN_WORK);
		baseService.update(workId, dataMap);
	}

	/**
	 * 修改节点表数据
	 * 
	 * @param baseService
	 * @param workNodeId
	 * @param dataMap
	 * @throws Exception
	 */
	private void upWorkNodeData(IBaseService baseService, String workNodeId, Map<String, Object> dataMap)
			throws Exception {
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS_TRAIN_WORK_FLOW);
		baseService.update(workNodeId, dataMap);
	}

	/**
	 * 修改审核主体对应文章表状态
	 * 
	 * @param baseService
	 * @param articleId
	 * @param dataMap
	 * @throws Exception
	 */
	private void upArticleData(IBaseService baseService, String articleId, Map<String, Object> dataMap)
			throws Exception {
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS_ARTICLE_STATUS);
		baseService.update(articleId, dataMap);
	}

	/**
	 * 发送消息提醒
	 * 
	 * @param baseService
	 * @param msgDataMap
	 */
	private void sendMsgSaveData(IBaseService baseService, Map<String, Object> msgDataMap) {

	}

	/**
	 * 存储审核记录明显
	 * 
	 * @param baseService
	 * @param workflowExamineEntityBo
	 * @param auditDeptNode
	 * @param auditStatus
	 * @throws Exception
	 */
	private void folwLogSaveData(IBaseService baseService, WorkflowExamineEntityBo workflowExamineEntityBo,
			Object auditDeptNode, Object auditStatus) throws Exception {
		Map<String, Object> logDataMap = new HashMap<String, Object>();
		logDataMap.put("belongSys", workflowExamineEntityBo.getBelongSys());
		logDataMap.put("belongMode", workflowExamineEntityBo.getBelongMode());
		logDataMap.put("belongType", workflowExamineEntityBo.getBelongType());
		logDataMap.put("tableId", workflowExamineEntityBo.getTableId());
		logDataMap.put("auditAreaCode", workflowExamineEntityBo.getDeptAreaCode());
		logDataMap.put("auditDeptNode", auditDeptNode);
		logDataMap.put("auditDeptCode", workflowExamineEntityBo.getDeptCode());
		logDataMap.put("auditDeptName", workflowExamineEntityBo.getDeptName());
		logDataMap.put("auditUserId", workflowExamineEntityBo.getCreationId());
		logDataMap.put("auditUserName", workflowExamineEntityBo.getCreationName());
		logDataMap.put("auditStatus", auditStatus);
		logDataMap.put("creationId", workflowExamineEntityBo.getCreationId());
		logDataMap.put("creationName", workflowExamineEntityBo.getCreationName());
		logDataMap.put("remark", workflowExamineEntityBo.getRemark());
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS_TRAIN_WORK_FLOW_LOG);
		baseService.save(logDataMap);
	}

	/**
	 * 查询审批主体表信息
	 * 
	 * @param baseService
	 * @param workorderExamineMap
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Object> getWorkHeader(IBaseService baseService, Map<String, Object> workorderExamineMap)
			throws Exception {
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS_TRAIN_WORK);
		Object workObj = baseService.list(workorderExamineMap);
		if (workObj != null) {
			List<Object> workInfoList = (ArrayList<Object>) workObj;
			if (workInfoList.size() >= 1) {
				return (HashMap<String, Object>) workInfoList.get(0);
			} else {
				throw new GlobalErrorException(GlobalErrorEnum.PARAM_NOT_VALID.getCode(), "未获取到审批主体表信息");
			}
		}
		return null;
	}

	/**
	 * 查询审批节点表信息
	 * 
	 * @param baseService
	 * @param workorderExamineMap
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Object> getWorkNode(IBaseService baseService, Map<String, Object> workorderExamineMap)
			throws Exception {
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS_TRAIN_WORK_FLOW);
		Object workNodeObj = baseService.list(workorderExamineMap);
		if (workNodeObj != null) {
			List<Object> workNodeInfoList = (ArrayList<Object>) workNodeObj;
			if (workNodeInfoList.size() >= 1) {
				return (HashMap<String, Object>) workNodeInfoList.get(0);
			} else {
				throw new GlobalErrorException(GlobalErrorEnum.PARAM_NOT_VALID.getCode(), "未获取到审批节点表信息");
			}
		}
		return null;
	}

	/**
	 * 审核不通过业务处理
	 * 
	 * @param baseService
	 * @param workflowExamineEntityBo
	 * @param workHeaderMap
	 * @param workNodeMap
	 * @throws Exception
	 */
	private void AuditFail(IBaseService baseService, WorkflowExamineEntityBo workflowExamineEntityBo,
			Map<String, Object> workHeaderMap, Map<String, Object> workNodeMap) throws Exception {
		// 获取 审批节点表 本部门是 审批节点信息
		Map<String, Object> workorderExamineMap = new HashMap<String, Object>();
		workorderExamineMap.put("workId", workflowExamineEntityBo.getWorkId());
		// workorderExamineMap.put("auditDeptNode", "0"); // 本部门
		Map<String, Object> workNodedataMap = getWorkNode(baseService, workorderExamineMap);

		// 审核不通 初始化 审核主题表的 总状态为 1待审核 当前节点审核为1
		String workId = SyhzUtil.setDate(workHeaderMap.get("id"));
		Map<String, Object> workHeaderDataMap = new HashMap<String, Object>();
		workHeaderDataMap.put("auditStatus", AUDIT_STATUS_1);
		workHeaderDataMap.put("currentAuditType", AUDIT_STATUS_1);
		workHeaderDataMap.put("currentAuditNode", "0");
		workHeaderDataMap.put("currentAuditAreaCode", workNodedataMap.get("auditAreaCode"));
		workHeaderDataMap.put("currentAuditDepCode", workNodedataMap.get("auditDeptCode"));
		workHeaderDataMap.put("lastId", workflowExamineEntityBo.getCreationId());
		workHeaderDataMap.put("lastName", workflowExamineEntityBo.getCreationName());
		workHeaderDataMap.put("remark", workflowExamineEntityBo.getRemark());
		upWorkHeaderData(baseService, workId, workHeaderDataMap);

		// 初始化审核所有流程节点为 1待审核
		// String workNodeId = SyhzUtil.setDate(workNodeMap.get("id"));
		Map<String, Object> workNodeDataMap = new HashMap<String, Object>();
		workNodeDataMap.put("workId", workId);
		workNodeDataMap.put("auditUserId", workflowExamineEntityBo.getCreationId());
		workNodeDataMap.put("auditUserName", workflowExamineEntityBo.getCreationName());
		workNodeDataMap.put("auditStatus", AUDIT_STATUS_1);
		workNodeDataMap.put("lastId", workflowExamineEntityBo.getCreationId());
		workNodeDataMap.put("lastName", workflowExamineEntityBo.getCreationName());
		workNodeDataMap.put("remark", workflowExamineEntityBo.getRemark());
		upWorkNodeData(baseService, workId, workNodeDataMap);

		// 修改文章信息状态为 1待审核
		String tableId = SyhzUtil.setDate(workHeaderMap.get("tableId"));
		String belongSys = SyhzUtil.setDate(workHeaderMap.get("belongSys"));
		Map<String, Object> articleDataMap = new HashMap<String, Object>();
		articleDataMap.put("auditStatus", AUDIT_STATUS_1);
		articleDataMap.put("lastId", workflowExamineEntityBo.getCreationId());
		articleDataMap.put("lastName", workflowExamineEntityBo.getCreationName());
		articleDataMap.put("remark", workflowExamineEntityBo.getRemark());
		String belongMode = SyhzUtil.setDate(workHeaderMap.get("belongMode")); // 所属模块(知识库包含：1 法律法规、2行业标准、3规则制度、4案例指引
																				// 网上培训：1在线课程
		String tableName = optTableName(belongSys, belongMode);
		if (!"".equals(tableName)) {
			articleDataMap.put("tableName", tableName);
			upArticleData(baseService, tableId, articleDataMap);
			// 审核通过的文章 需要发起消息提醒
			Map<String, Object> msgDataMap = new HashMap<String, Object>();
			sendMsgSaveData(baseService, msgDataMap);
			// 审核不通过记录 添加
			folwLogSaveData(baseService, workflowExamineEntityBo, workNodeMap.get("auditDeptNode"), AUDIT_STATUS_3);
		}
	}

	private String optTableName(String belongSys, String belongMode) {
		String tableName = "";
		if (SYSTEM_1.equals(belongSys)) { // 知识库
			if ("1".equals(belongMode)) {
				tableName = TABLE_NAME_1;
			} else if ("2".equals(belongMode)) {
				tableName = TABLE_NAME_2;
			} else if ("3".equals(belongMode)) {
				tableName = TABLE_NAME_3;
			} else if ("4".equals(belongMode)) {
				tableName = TABLE_NAME_4;
			} else {
				throw new GlobalErrorException(GlobalErrorEnum.PARAM_NOT_VALID.getCode(), "未获的所属模块值");
			}
		} else if (SYSTEM_2.equals(belongSys)) { // 网上培训
			tableName = TABLE_NAME_0;
		} else {
			throw new GlobalErrorException(GlobalErrorEnum.PARAM_NOT_VALID.getCode(), "未获的所属系统值");
		}
		return tableName;
	}
}
