package com.nmghr.handler.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nmghr.basic.core.service.IBaseService;

/***审核流程**

@author
heijiantao*@date
2019 年9月25日 上午10:51:57*@version
1.0*/@Service("trainauditService")
public class TrainauditService {
	@Autowired
	private EnclosureAuditService EnclosureAuditService;

	@Autowired
	TrainWorkorderExamineService trainWorkorderExamineService;
	@Autowired
	@Qualifier("baseService")
	private IBaseService baseService;

	@Transactional(rollbackFor = Exception.class)
 public void audit(Map<String, Object> requestBody, Map<String, String>
 headers) throws Exception {
 trainWorkorderExamineService.examineWorkFlowData(baseService, headers,
 requestBody);
 EnclosureAuditService.rule1(requestBody, baseService);
 EnclosureAuditService.audit(requestBody);
 }
}
