package com.nmghr.handler.update;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractUpdateHandler;
import com.nmghr.basic.core.util.ValidationUtils;

@Service("traincourselogUpdateHandler")
public class TrainCourseLogUpdateHandler extends AbstractUpdateHandler{
	private static final String ALIAS_TRAINCOURSELOG = "TRAINCOURSELOG";
	
	// 修改停留时间
	public TrainCourseLogUpdateHandler(IBaseService baseService) {
		super(baseService);
	}
	
	
	@Transactional
	public Object update(String id, Map<String, Object> requestBody) throws Exception{
		validation(requestBody);
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS_TRAINCOURSELOG);
		  baseService.update(id, requestBody);
		  return id;
	}


	private void validation(Map<String, Object> requestBody) {
		Object stopTime = requestBody.get("stopTime");
		ValidationUtils.notNull(stopTime, "停留时间不能为空");
		
	}

}
