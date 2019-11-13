package com.nmghr.hander.query;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractQueryHandler;

@Service("basicEquipAllocateListQueryHandler")
public class BasicEquipAllocateListQueryHandler extends AbstractQueryHandler {

	private static final String ALIAS_BASICEQUIPALLOCATE = "BASICEQUIPALLOCATE";
	
	public BasicEquipAllocateListQueryHandler(IBaseService baseService) {
		super(baseService);
	}

	public Object list(Map<String, Object> requestBody) throws Exception {

		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS_BASICEQUIPALLOCATE);
		List<Map<String, Object>> list = (List<Map<String, Object>>) baseService.list(requestBody);
//		for (int i = 0; i < list.size(); i++) {
//			System.out.println(list.get(i).get("groupId"));
//		}
		return list;
		
	}

}
