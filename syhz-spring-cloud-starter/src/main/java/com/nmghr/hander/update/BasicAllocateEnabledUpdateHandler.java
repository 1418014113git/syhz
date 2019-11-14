package com.nmghr.hander.update;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractUpdateHandler;

@Service("basicAllocateEnabledUpdateHandler")
public class BasicAllocateEnabledUpdateHandler extends AbstractUpdateHandler {

	private static final String ALIAS_BASICALLOCATEENABED = "BASICALLOCATEENABED";
	
	public BasicAllocateEnabledUpdateHandler(IBaseService baseService) {
		super(baseService);
	}
	
	public Object update(String id, Map<String, Object> requestBody) throws Exception {
		String enabled = (String) requestBody.get("enabled");
		String stopTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		
		if ("0".equals(enabled)) {
			requestBody.put("stopTime", stopTime);
		} else if ("1".equals(enabled)) {
			requestBody.put("stopTime", null);
		}

		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS_BASICALLOCATEENABED);
		baseService.update(id, requestBody);
		return id;
	}

}
