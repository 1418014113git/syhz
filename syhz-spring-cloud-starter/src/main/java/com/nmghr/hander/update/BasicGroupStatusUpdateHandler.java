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

	public BasicGroupStatusUpdateHandler(IBaseService baseService) {
		super(baseService);
	}
	
	public Object update(String id, Map<String, Object> requestBody) throws Exception {
		String groupStatus = (String) requestBody.get("groupStatus");
		String stopTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		
		if ("0".equals(groupStatus)) {
			requestBody.put("stopTime", stopTime);
		} else if ("1".equals(groupStatus)) {
			requestBody.put("stopTime", null);
		}

		
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS_BASICGROUPSTATUS);
		baseService.update(id, requestBody);
		return id;
		
	}

}
