package com.nmghr.handler.save;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractSaveHandler;
import com.nmghr.basic.core.util.ValidationUtils;
import com.nmghr.handler.service.EnclosureAuditService;
import com.nmghr.handler.service.EsService;
import com.nmghr.handler.service.TrainWorkorderService;
import com.nmghr.util.SyhzUtil;

@Service("standardinfoSaveHandler")
public class StandardInfoSaveHandler extends AbstractSaveHandler {
	@Autowired
	private TrainWorkorderService TrainWorkorderService;
	@Autowired
	private EnclosureAuditService EnclosureAuditService;
	@Autowired
	private EsService EsService;

	private static String ALIAS_TRAINSTANDARDINFO = "TRAINSTANDARDINFO";// 课程查询
	private static int belong_sys = 1;// 所属系统(1 知识库 2网上培训)
	private static int belong_mode = 3;// 1 法律法规、2行业标准、3规则制度、4案例指引

	public StandardInfoSaveHandler(IBaseService baseService) {
		super(baseService);
	}

	@Override
	@Transactional
	public Object save(Map<String, Object> requestBody) throws Exception {
		Object lawInfoId = SyhzUtil.setDate(requestBody.get("lawInfoId"));// 是否已存为草稿
		int draft = SyhzUtil.setDateInt(requestBody.get("draft"));// 是否为草稿
		int adminFlag = SyhzUtil.setDateInt(requestBody.get("adminFlag"));// 是否为管理员
		validation(requestBody);
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS_TRAINSTANDARDINFO);
		lawInfoId = baseService.save(requestBody);
		EnclosureAuditService.enclouseSave(requestBody, lawInfoId, baseService, belong_mode);// 保存附件
		requestBody.put("crouseId", lawInfoId);
		Map<String, String> header = new HashMap<String, String>();
		Map<String, Object> auditMap = EnclosureAuditService.audit(requestBody, belong_sys, belong_mode);
		Object workId = TrainWorkorderService.createWorkflowData(baseService, header, auditMap);// 添加审批记录
		if (draft == 1 && adminFlag == 0) {// 管理员默认审核通过
			EnclosureAuditService.subimtaduit(workId, lawInfoId, belong_sys, belong_mode, requestBody, baseService);
		}
		return requestBody;

	}

	private void validation(Map<String, Object> requestBody) {
		Object title = requestBody.get("title");
		ValidationUtils.notNull(title, "法律规则制度标题不能为空");
		Object articleType = requestBody.get("articleType");
		requestBody.put("type", articleType);
		ValidationUtils.notNull(articleType, "规则制度类型不能为空");
		Object publishTime = requestBody.get("publishTime");
		ValidationUtils.notNull(publishTime, "颁发时间不能为空");
		Object belongAreaCode = requestBody.get("belongAreaCode");
		ValidationUtils.notNull(belongAreaCode, "所属区域Code不能为空");
		Object belongDepCode = requestBody.get("belongDepCode");
		requestBody.put("areaCode", belongAreaCode);
		ValidationUtils.notNull(belongDepCode, "部门code不能为空");
		Object belongDepName = requestBody.get("belongDepName");
		ValidationUtils.notNull(belongDepName, "部门名称不能为空");
		Object creationId = requestBody.get("creationId");
		ValidationUtils.notNull(creationId, "创建人ID不能为空");
		Object creationName = requestBody.get("creationName");
		ValidationUtils.notNull(creationName, "创建人用户名不能为空");
	}

}
