package com.nmghr.handler.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.handler.message.QueueConfig;
import com.nmghr.util.SyhzUtil;

/****
 * 审核流程****
 * 
 * @author heijiantao**@date 2019 年9月25日 上午10:51:57*@version 1.0
 */
@Service("trainauditService")
public class TrainauditService {
	@Autowired
	private EnclosureAuditService EnclosureAuditService;

	@Autowired
	TrainWorkorderExamineService trainWorkorderExamineService;
	@Autowired
	@Qualifier("baseService")
	private IBaseService baseService;
	@Autowired
	private SendMessageService snedMessgeService;

	@Transactional(rollbackFor = Exception.class)
	public void audit(Map<String, Object> requestBody, Map<String, String> headers) throws Exception {
		trainWorkorderExamineService.examineWorkFlowData(baseService, headers, requestBody);// 审核
		send(requestBody);// 发送消息
		EnclosureAuditService.rule1(requestBody, baseService);// 积分
		EnclosureAuditService.audit(requestBody);// es
	}

	private void send(Map<String, Object> requestBody) throws Exception {
		String workIds = SyhzUtil.setDate(requestBody.get("workIds"));
		String[] workId = workIds.split(",");
		String tableIds = SyhzUtil.setDate(requestBody.get("tableIds"));
		String[] tableId = tableIds.split(",");
		for (int i = 0; i < workId.length; i++) {
			requestBody.put("workId", workId[i]);
			requestBody.put("tableId", tableId[i]);
			Map<String, Object> sendMap = EnclosureAuditService.activeMq(requestBody, baseService, 1);
			int sendFlag = SyhzUtil.setDateInt(sendMap.get("sendFlag"));
			if (sendFlag == 0) {
				snedMessgeService.sendMessage(sendMap, QueueConfig.KNOWLEDGE);
			}
		}
	}

}
