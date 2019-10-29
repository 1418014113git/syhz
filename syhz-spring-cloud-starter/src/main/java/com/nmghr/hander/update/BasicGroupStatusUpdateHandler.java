package com.nmghr.hander.update;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractUpdateHandler;

@Service("basicGroupStatusUpdateHandler")
public class BasicGroupStatusUpdateHandler extends AbstractUpdateHandler {

	private static final String ALIAS_BASICGROUPSTATUS = "BASICGROUPSTATUS";
	private static final String ALIAS_BASICEQUIPGROUP = "BASICEQUIPGROUP";

	public BasicGroupStatusUpdateHandler(IBaseService baseService) {
		super(baseService);
	}
	
	public Object update(String id, Map<String, Object> requestBody) throws Exception {
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS_BASICEQUIPGROUP);
		Map<String, Object> basic = (Map<String, Object>) baseService.get(id);
		String remark = (String) basic.get("remark");
		String groupStatus = (String) requestBody.get("groupStatus");
		String remarkTime = "停用时间："+ new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss").format(new Date())+"。";
		String stopTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		if (remark == null) {
			if ("0".equals(groupStatus)) {
				requestBody.put("stopTime", stopTime);
				requestBody.put("remark", remarkTime);
			} else if ("1".equals(groupStatus)) {
				requestBody.put("stopTime", null);
				requestBody.put("remark", remark);
			}
			
		} else {
			int remaLength = remark.length();
			int sum = remarkTime.length();
			remaLength=remaLength+sum;
			
			if ("0".equals(groupStatus)) {
				requestBody.put("stopTime", stopTime);
				if (remaLength <= 500) {
					requestBody.put("remark", remark+remarkTime);
				} else {
					requestBody.put("remark", remark);
				}
			} else if ("1".equals(groupStatus)) {
				requestBody.put("stopTime", null);
				requestBody.put("remark", remark);
			}

		}
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS_BASICGROUPSTATUS);
		baseService.update(id, requestBody);
		return id;
		
	}

}
