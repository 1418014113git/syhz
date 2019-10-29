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


	private static final String ALIAS_BASICEQUIPALLOCATE = "BASICEQUIPALLOCATE";
	private static final String ALIAS_BASICALLOCATEENABED = "BASICALLOCATEENABED";
	
	public BasicAllocateEnabledUpdateHandler(IBaseService baseService) {
		super(baseService);
	}
	
	public Object update(String id, Map<String, Object> requestBody) throws Exception {
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS_BASICEQUIPALLOCATE);
		Map<String, Object> basic = (Map<String, Object>) baseService.get(id);
		String remark = (String) basic.get("remark");
		String enabled = (String) requestBody.get("enabled");
		String remarkTime = "停用时间："+ new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss").format(new Date())+"。";
		if (remark == null) {
			if ("0".equals(enabled)) {
				requestBody.put("remark", remarkTime);
			} else if ("1".equals(enabled)) {
				requestBody.put("remark", remark);
			}
			
		} else {
			int remaLength = remark.length();
			int sum = remarkTime.length();
			remaLength=remaLength+sum;
			if ("0".equals(enabled)) {
				if (remaLength <= 500) {
					requestBody.put("remark", remark+remarkTime);
				} else {
					requestBody.put("remark", remark);
				}
			} else if ("1".equals(enabled)) {
				requestBody.put("remark", remark);
			}

		}
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS_BASICALLOCATEENABED);
		baseService.update(id, requestBody);
		return id;
	}

}
