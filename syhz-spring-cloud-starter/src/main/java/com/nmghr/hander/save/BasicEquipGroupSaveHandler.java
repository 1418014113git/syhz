package com.nmghr.hander.save;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractSaveHandler;

@Service("basicEquipGroupSaveHandler")
public class BasicEquipGroupSaveHandler extends AbstractSaveHandler{

	private static final String ALIAS_BASICEQUIPGROUP = "BASICEQUIPGROUP";

	public BasicEquipGroupSaveHandler(IBaseService baseService) {
		super(baseService);
	}

	public Object save(Map<String, Object> requestBody) throws Exception {
		validation(requestBody);
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS_BASICEQUIPGROUP);
		return requestBody;
		
	}

	private void validation(Map<String, Object> requestBody) {
		
	}

}
