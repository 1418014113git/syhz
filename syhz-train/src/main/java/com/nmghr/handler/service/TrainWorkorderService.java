/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.nmghr.handler.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.common.exception.GlobalErrorException;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.util.ValidationUtils;
import com.nmghr.handler.vo.DeptEntityBo;
import com.nmghr.handler.vo.WorkflowEntityBo;
import com.nmghr.util.SyhzUtil;

/**
 * 知识库、网上培训 审批相关节点数据业务处理.
 *
 * @author kaven
 * @date 2019年9月23日 上午11:15:33
 * @version 1.0
 */
@Service("trainWorkorderService")
public class TrainWorkorderService {

  private static final String ALIAS_TRAIN_WORK = "trainWorkorder"; // 审核主体表
  private static final String ALIAS_TRAIN_WORK_FLOW = "TRAINWORKORDERFLOW"; // 审核节点表

	private static final int AUDIT_STATUS_0 = 1; // 0待审核

  /**
   * 创建工作流审批数据
   * 
   * @param headers
   * @param requestBody
   * @throws Exception
   */
  @Transactional
  public Object createWorkflowData(IBaseService baseService, Map<String, String> headers,
      Map<String, Object> requestBody) throws Exception {
    WorkflowEntityBo workflowEntityBo = verifyData(headers, requestBody);
    if (workflowEntityBo != null) {
      return saveWorkorder(baseService, workflowEntityBo);
    } else {
      return "";
    }
  }

  /**
   * 验证必要参数是否为空 且转换为对应Bo
   * 
   * @param headers
   * @param requestBody
   * @return
   */
  private WorkflowEntityBo verifyData(Map<String, String> headers, Map<String, Object> requestBody)
      throws GlobalErrorException {
    String belongSys = SyhzUtil.setDate(requestBody.get("belongSys"));
    String belongMode = SyhzUtil.setDate(requestBody.get("belongMode"));
    String belongType = SyhzUtil.setDate(requestBody.get("belongType"));
    String tableId = SyhzUtil.setDate(requestBody.get("tableId"));
    String creationId = SyhzUtil.setDate(requestBody.get("creationId"));
    String creationName = SyhzUtil.setDate(requestBody.get("creationName"));
    Object myDept = requestBody.get("myDept"); // 本部门
    Object provinceDept = requestBody.get("provinceDept"); // 省级部门


    ValidationUtils.notNull(belongSys, "审批所属系统不能为空");
    ValidationUtils.notNull(belongMode, "审批所属模块不能为空");
    ValidationUtils.notNull(belongType, "审批所属类型不能为空");
    ValidationUtils.notNull(tableId, "审批对应id不能为空");
    ValidationUtils.notNull(creationId, "创建人id不能为空");
    ValidationUtils.notNull(creationName, "创建人姓名不能为空");

    ValidationUtils.notNull(myDept, "本单位部门不能为空");
    ValidationUtils.notNull(provinceDept, "省级部门不能为空");

    WorkflowEntityBo workflowEntityBo =
        WorkflowEntityBo.workflowMapToEntityBo(headers, requestBody);
    return workflowEntityBo;
  }

  private Object saveWorkorder(IBaseService baseService, WorkflowEntityBo workflowEntityBo)
      throws Exception {
    Map<String, Object> workorderMap = new HashMap<String, Object>();
    workorderMap.put("belongSys", workflowEntityBo.getBelongSys());
    workorderMap.put("belongMode", workflowEntityBo.getBelongMode());
    workorderMap.put("belongType", workflowEntityBo.getBelongType());
    workorderMap.put("tableId", workflowEntityBo.getTableId());
    workorderMap.put("auditStatus", AUDIT_STATUS_0);
    workorderMap.put("creationId", workflowEntityBo.getCreationId());
    workorderMap.put("creationName", workflowEntityBo.getCreationName());
    workorderMap.put("creationTime", workflowEntityBo.getCreationTime());

    DeptEntityBo deptEntityBo = workflowEntityBo.getMyDept(); // 本单位
    workorderMap.put("auditCurrentNode", 0);
    workorderMap.put("currentAuditType", 1);
    workorderMap.put("currentAuditDepCode", deptEntityBo.getDeptCode());
    workorderMap.put("currentAuditAreaCode", deptEntityBo.getDeptAreaCode());

    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS_TRAIN_WORK);
    Object workId = baseService.save(workorderMap);
    saveWorkorderFlow(baseService, SyhzUtil.setDate(workId), workflowEntityBo);
    return workId;
  }

  /**
   * 初始化审批节点数据
   * 
   * @param workId
   * @param workflowEntityBo
   * @return
   * @throws Exception
   */
  private void saveWorkorderFlow(IBaseService baseService, String workId,
      WorkflowEntityBo workflowEntityBo) throws Exception {
    // 本单位审批节点数据
    DeptEntityBo myDeptBo = workflowEntityBo.getMyDept();
    String myDepCode = SyhzUtil.setDate(myDeptBo.getDeptCode());

    String parentWorkId = "";
    String parentDepCode = "";
    // 优先保存省级审批节点数据
    DeptEntityBo provinceDeptBo = workflowEntityBo.getProvinceDept();
    if (provinceDeptBo != null) {
      String provinceDepCode = SyhzUtil.setDate(provinceDeptBo.getDeptCode());
      if (!"".equals(provinceDepCode)) {
        if (myDepCode.equals(provinceDepCode)) { // 如果本部门code 与 省级code码相同 则直接调用本部门
          parentWorkId = "0";
          parentDepCode = "0";
          // 本单位 在省级别
          saveWorkorderFlowDataMe(baseService, workId, parentWorkId, parentDepCode, workflowEntityBo,
              myDeptBo);
        } else {
          Object provinceWorkId =
              saveWorkorderFlowDataProvince(baseService, workId, workflowEntityBo, provinceDeptBo);
          parentWorkId = SyhzUtil.setDate(provinceWorkId);
          parentDepCode = provinceDepCode;
        }
      }
      
    }

    // 处理市级审批节点数据
    DeptEntityBo cityDeptBo = workflowEntityBo.getCityDept();
    if (cityDeptBo != null) {
      String cityDepCode = SyhzUtil.setDate(cityDeptBo.getDeptCode());
      if (!"".equals(cityDepCode)) {
        if (myDepCode.equals(cityDepCode)) { // 如果本部门code 与 市级code码相同 则直接调用本部门
          // 本单位 在市级别
          saveWorkorderFlowDataMe(baseService, workId, parentWorkId, parentDepCode,
              workflowEntityBo, myDeptBo);
        } else {
          Object cityWorkId = saveWorkorderFlowDataCity(baseService, workId, parentWorkId,
              parentDepCode, workflowEntityBo, cityDeptBo);
          parentWorkId = SyhzUtil.setDate(cityWorkId);
          parentDepCode = SyhzUtil.setDate(cityDeptBo.getDeptCode());

          // 本单位 在区级别
          saveWorkorderFlowDataMe(baseService, workId, parentWorkId, parentDepCode,
              workflowEntityBo, myDeptBo);
        }
      }
    }
    
  }

  /**
   * 省级审批 流程节点数据保存
   * 
   * @param workId 审批主体表id
   * @param workflowEntityBo
   * @return
   * @throws Exception
   */
  private Object saveWorkorderFlowDataProvince(IBaseService baseService, Object workId,
      WorkflowEntityBo workflowEntityBo, DeptEntityBo provinceDeptBo) throws Exception {
    Map<String, Object> workorderFlowMap = new HashMap<String, Object>();
    workorderFlowMap.put("workId", workId);
    workorderFlowMap.put("auditDeptNode", 2); // 当前审核节点（0部门 1市级 2省级）
    workorderFlowMap.put("auditAreaCode", provinceDeptBo.getDeptAreaCode());
    workorderFlowMap.put("auditDeptCode", provinceDeptBo.getDeptCode());
    workorderFlowMap.put("auditDeptName", provinceDeptBo.getDeptName());
    workorderFlowMap.put("audit_status", AUDIT_STATUS_0);
    workorderFlowMap.put("creationId", workflowEntityBo.getCreationId());
    workorderFlowMap.put("creationName", workflowEntityBo.getCreationName());
    workorderFlowMap.put("creationTime", workflowEntityBo.getCreationTime());
    workorderFlowMap.put("nextAuditDeptCode", "0"); // 如果是省级审批 则上级部门为0
    workorderFlowMap.put("nextFlowId", "0"); // 如果是省级审批 则下一次流转为0 审核通过后下次流程节点id

    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS_TRAIN_WORK_FLOW);
    Object provinceWorkId = baseService.save(workorderFlowMap);
    return provinceWorkId;
  }

  /**
   * 市级审批 流程节点数据保存
   * 
   * @param workId 审核主体表id
   * @param provinceWorkId 省级节点审核id
   * @param provinceDepCode 省级部门code
   * @param workflowEntityBo
   * @param cityDeptBo 市级部门code
   * @return
   * @throws Exception
   */
  private Object saveWorkorderFlowDataCity(IBaseService baseService, Object workId,
      Object provinceWorkId, String provinceDepCode, WorkflowEntityBo workflowEntityBo,
      DeptEntityBo cityDeptBo) throws Exception {
    // 处理 保存市级审批节点数据

    Map<String, Object> workorderFlowMap = new HashMap<String, Object>();
    workorderFlowMap.put("workId", workId);
    workorderFlowMap.put("auditDeptNode", 1); // 当前审核节点（0部门 1市级 2省级）
    workorderFlowMap.put("auditAreaCode", cityDeptBo.getDeptAreaCode());
    workorderFlowMap.put("auditDeptCode", cityDeptBo.getDeptCode());
    workorderFlowMap.put("auditDeptName", cityDeptBo.getDeptName());
    workorderFlowMap.put("audit_status", AUDIT_STATUS_0);
    workorderFlowMap.put("creationId", workflowEntityBo.getCreationId());
    workorderFlowMap.put("creationName", workflowEntityBo.getCreationName());
    workorderFlowMap.put("creationTime", workflowEntityBo.getCreationTime());
    workorderFlowMap.put("nextAuditDeptCode", provinceDepCode); // 当前部门上级的部门code
    workorderFlowMap.put("nextFlowId", provinceWorkId); // 审核通过后下次流程节点id

    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS_TRAIN_WORK_FLOW);
    Object cityWorkId = baseService.save(workorderFlowMap);
    return cityWorkId;
  }

  /**
   * 本部门审批 流程节点数据保存
   * 
   * @param workId 审核主体表id
   * @param parentWorkId 上级节点审核id
   * @param parentDepCode 本单位部门code
   * @param workflowEntityBo
   * @param myDeptBo 本单位部门信息
   * @return
   * @throws Exception
   */
  private Object saveWorkorderFlowDataMe(IBaseService baseService, Object workId,
      Object parentWorkId, Object parentDepCode, WorkflowEntityBo workflowEntityBo,
      DeptEntityBo myDeptBo) throws Exception {
    // 处理 保存市级审批节点数据
    Map<String, Object> workorderFlowMap = new HashMap<String, Object>();
    workorderFlowMap.put("workId", workId);
    workorderFlowMap.put("auditDeptNode", 0); // 当前审核节点（0部门 1市级 2省级）
    workorderFlowMap.put("auditAreaCode", myDeptBo.getDeptAreaCode());
    workorderFlowMap.put("auditDeptCode", myDeptBo.getDeptCode());
    workorderFlowMap.put("auditDeptName", myDeptBo.getDeptName());
    workorderFlowMap.put("audit_status", AUDIT_STATUS_0);
    workorderFlowMap.put("creationId", workflowEntityBo.getCreationId());
    workorderFlowMap.put("creationName", workflowEntityBo.getCreationName());
    workorderFlowMap.put("creationTime", workflowEntityBo.getCreationTime());
    workorderFlowMap.put("nextAuditDeptCode", parentDepCode); // 当前部门上级的部门code
    workorderFlowMap.put("nextFlowId", parentWorkId); // 审核通过后下次流程节点id

    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS_TRAIN_WORK_FLOW);
    Object cityWorkId = baseService.save(workorderFlowMap);
    return cityWorkId;
  }


}
