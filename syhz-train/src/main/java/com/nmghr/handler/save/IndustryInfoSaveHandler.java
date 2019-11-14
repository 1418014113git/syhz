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

@Service("industryinfoSaveHandler")
public class IndustryInfoSaveHandler extends AbstractSaveHandler {

	@Autowired
	private TrainWorkorderService TrainWorkorderService;
	@Autowired
	private EnclosureAuditService EnclosureAuditService;

	@Autowired
	private EsService EsService;
	private static String ALIAS_TRAININDUSTRYINFO = "TRAININDUSTRYINFO";// 法律法规
	private static int belong_sys = 1;// 所属系统(1 知识库 2网上培训)
	private static int belong_mode = 2;// 1 法律法规、2行业标准、3规则制度、4案例指引

	public IndustryInfoSaveHandler(IBaseService baseService) {
		super(baseService);
	}

	@Override
	@Transactional
	public Object save(Map<String, Object> requestBody) throws Exception {
		Object lawInfoId = SyhzUtil.setDate(requestBody.get("id"));// 是否已存为草稿
		int draft = SyhzUtil.setDateInt(requestBody.get("draft"));// 是否为草稿
		int adminFlag = SyhzUtil.setDateInt(requestBody.get("adminFlag"));// 是否为管理员
		int depType = SyhzUtil.setDateInt(requestBody.get("depType"));// 是否派出所
		validation(requestBody);
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS_TRAININDUSTRYINFO);
		lawInfoId = baseService.save(requestBody);
		EnclosureAuditService.enclouseSave(requestBody, lawInfoId, baseService, belong_mode);// 保存附件
		requestBody.put("crouseId", lawInfoId);
		Map<String, String> header = new HashMap<String, String>();
		Map<String, Object> auditMap = EnclosureAuditService.audit(requestBody, belong_sys, belong_mode);
		Object workId = TrainWorkorderService.createWorkflowData(baseService, header, auditMap);// 添加审批记录
		if (draft == 1 && adminFlag == 0 && depType != 4) {// 不是草稿管理员默认审核通过
			EnclosureAuditService.subimtaduit(workId, lawInfoId, belong_sys, belong_mode, requestBody, baseService);
		}
		return requestBody;
	}

	private void validation(Map<String, Object> requestBody) {
		Object articleType = requestBody.get("articleType");
		ValidationUtils.notNull(articleType, "类型不能为空");
		requestBody.put("type", articleType);
		Object enclosure = requestBody.get("enclosure");
		ValidationUtils.notNull(enclosure, "附件不能为空");
		Object belongOrgId = requestBody.get("belongDepCode");
		ValidationUtils.notNull(belongOrgId, "所属机构Code不能为空");
		Object belongDepName = requestBody.get("belongDepName");
		ValidationUtils.notNull(belongDepName, "所属机构名称不能为空");
		Object belongAreaCode = requestBody.get("belongAreaCode");
		ValidationUtils.notNull(belongAreaCode, "所属区域不能为空");
		requestBody.put("areaCode", belongAreaCode);
		Object creationId = requestBody.get("creationId");
		ValidationUtils.notNull(creationId, "上传人不能为空");
		Object creationName = requestBody.get("creationName");
		ValidationUtils.notNull(creationName, "上传人姓名不能为空");
	}
}
