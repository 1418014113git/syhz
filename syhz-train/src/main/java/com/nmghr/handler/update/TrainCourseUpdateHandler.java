package com.nmghr.handler.update;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractUpdateHandler;
import com.nmghr.handler.service.EnclosureAuditService;
import com.nmghr.util.SyhzUtil;

@Service("traincourseupdateUpdateHandler")
public class TrainCourseUpdateHandler extends AbstractUpdateHandler {

	@Autowired
	private EnclosureAuditService EnclosureAuditService;
	private static String ALIAS_TRAINCOURSE = "TRAINCOURSEUPDATE";// 课程
	private static String ALIAS_TRAINCOURSESTATUS = "TRAINCOURSESTATUS";// 状态查询
	private static String ALIAS_TRAINWORKORDER = "TRAINWORKORDER";// 状态查询
	private static int belong_sys = 2;// 所属系统(1 知识库 2网上培训)
	private static int belong_mode = 1;// 1 法律法规、2行业标准、3规则制度、4案例指引

	public TrainCourseUpdateHandler(IBaseService baseService) {
		super(baseService);
	}

	@Override
	@Transactional
	public Object update(String id, Map<String, Object> requestBody) throws Exception {

		titleSubstring(requestBody);
		int subType = SyhzUtil.setDateInt(requestBody.get("subType"));
		String creationId = SyhzUtil.setDate(requestBody.get("creationId"));
		int adminFlag = SyhzUtil.setDateInt(requestBody.get("adminFlag"));// 是否为管理员
		String authorId = SyhzUtil.setDate(requestBody.get("authorId"));
		int depType = SyhzUtil.setDateInt(requestBody.get("depType"));// 是否派出所

		requestBody.put("id", id);
		requestBody.put("belongMode", belong_mode);// 行业标准
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS_TRAINCOURSESTATUS);
		Map<String, Object> map = (Map<String, Object>) baseService.get(requestBody);
		String workId = SyhzUtil.setDate(map.get("workId"));
		int status = SyhzUtil.setDateInt(map.get("auditStatus"));
		int statu2 = SyhzUtil.setDateInt(map.get("auditStatus2"));
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS_TRAINCOURSE);
		baseService.update(id, requestBody);
		if (subType == 1 && (statu2 == 0 || status == 3)) {
			if (status == 0 && adminFlag == 0 && depType != 4) {
				EnclosureAuditService.subimtaduit(workId, id, belong_sys, belong_mode, requestBody, baseService);
			}
			if (status == 3 && creationId.equals(authorId)) {// 管理员提交自己默认审核通过
				EnclosureAuditService.subimtaduitFail(workId, id, belong_sys, belong_mode, requestBody, baseService);
				if (adminFlag == 0 && depType != 4) {
					EnclosureAuditService.subimtaduit(workId, id, belong_sys, belong_mode, requestBody, baseService);
				}
			}
			if (status == 4) {
				Map<String, Object> mapStatus = new HashMap<String, Object>();
				mapStatus.put("auditStatus", "0");
				LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS_TRAINWORKORDER);
				baseService.update(workId, mapStatus);
			}

		}
		return status;
	}

	private void titleSubstring(Map<String, Object> requestBody) {
		String enName = String.valueOf(requestBody.get("enName"));
		String enType = String.valueOf(requestBody.get("enType"));
		Pattern pattern = Pattern.compile("_\\d+$");
		Matcher matcher = pattern.matcher(enName);
		if (matcher.find() && !"0".equals(enType)) {
			int j = enName.indexOf(matcher.group());
			String title = enName.substring(0, j);
			requestBody.put("title", title);
			requestBody.put("order", matcher.group().substring(1, matcher.group().length()));// 排序
		} else {
			requestBody.put("title", enName);
		}
	}

}
