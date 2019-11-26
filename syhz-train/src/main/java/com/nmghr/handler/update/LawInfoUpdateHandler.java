package com.nmghr.handler.update;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.common.Result;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractUpdateHandler;
import com.nmghr.basic.core.util.ValidationUtils;
import com.nmghr.handler.service.EnclosureAuditService;
import com.nmghr.util.SyhzUtil;

@Service("lawInfoUpdateHandler")
public class LawInfoUpdateHandler extends AbstractUpdateHandler {
	@Autowired
	private EnclosureAuditService EnclosureAuditService;
	private static String ALIAS_LAWINFO = "TRAINLAWINFO";// 法律法规
	private static String ALIAS_KNOWLEDGEENCLOSURE = "KNOWLEDGEENCLOSURE";// 附件
	private static String ALIAS_TARINKNOWAUDITSTATUS = "TARINKNOWAUDITSTATUS";// 状态查询
	private static String ALIAS_TRAINWORKORDER = "TRAINWORKORDER";// 修改工单状态
	private static int belong_sys = 1;// 所属系统(1 知识库 2网上培训)
	private static int belong_mode = 1;// 1 法律法规、2行业标准、3规则制度、4案例指引

	public LawInfoUpdateHandler(IBaseService baseService) {
		super(baseService);
	}

	@Override
	@Transactional
	public Object update(String id, Map<String, Object> requestBody) throws Exception {
		String subType = SyhzUtil.setDate(requestBody.get("subType"));
		String workId = SyhzUtil.setDate(requestBody.get("workId"));
		String creationId = SyhzUtil.setDate(requestBody.get("creationId"));
		String authorId = SyhzUtil.setDate(requestBody.get("authorId"));
		int adminFlag = SyhzUtil.setDateInt(requestBody.get("adminFlag"));// 是否为管理员
		int depType = SyhzUtil.setDateInt(requestBody.get("depType"));// 是否派出所
		validation(requestBody);
		requestBody.put("id", id);
		requestBody.put("belongMode", belong_mode);// 行业标准
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS_KNOWLEDGEENCLOSURE);
		baseService.remove(requestBody);// 删除之前的附件
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS_LAWINFO);
		baseService.update(id, requestBody);
		EnclosureAuditService.enclouseSave(requestBody, id, baseService, belong_mode);// 添加附件

		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS_TARINKNOWAUDITSTATUS);
		Map<String, Object> map = (Map<String, Object>) baseService.get(requestBody);
		int status = SyhzUtil.setDateInt(map.get("auditStatus"));
		int statu2 = SyhzUtil.setDateInt(map.get("auditStatus2"));
		if ("1".equals(subType) && (statu2 == 0 || status == 3)) {
			if (status == 0 && adminFlag == 0 && depType != 4) {
				EnclosureAuditService.subimtaduit(workId, id, belong_sys, belong_mode, requestBody, baseService);
			}
			if (status == 3) {
				EnclosureAuditService.subimtaduitFail(workId, id, belong_sys, belong_mode, requestBody, baseService);
			}
			if (status == 4) {
				Map<String, Object> mapStatus = new HashMap<String, Object>();
				mapStatus.put("auditStatus", "0");
				LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS_TRAINWORKORDER);
				baseService.update(workId, mapStatus);
			}
			if (status != 0 && adminFlag == 0 && depType != 4 && creationId.equals(authorId)) {
				EnclosureAuditService.subimtaduit(workId, id, belong_sys, belong_mode, requestBody, baseService);
			}
		}
		return Result.ok("");
	}

	private void validation(Map<String, Object> requestBody) {
		Object articleType = requestBody.get("articleType");
		ValidationUtils.notNull(articleType, "类型不能为空");
		requestBody.put("type", articleType);
		Object belongAreaCode = requestBody.get("belongAreaCode");
		ValidationUtils.notNull(belongAreaCode, "所属区域不能为空");
		requestBody.put("areaCode", belongAreaCode);
	}

}
