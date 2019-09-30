package com.nmghr.handler.query;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractQueryHandler;

// 查询规范制度详情
@Service("standardinfodetailQueryHandler")
public class TrainStandardInfoDetailQueryHandler extends AbstractQueryHandler {
	private static final String ALIAS_TRAINSTANDARDINFO = "TRAINSTANDARDINFO";

	public TrainStandardInfoDetailQueryHandler(IBaseService baseService) {
		super(baseService);
	}
	
	public Object get(String id) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", id);
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS_TRAINSTANDARDINFO);
		Map<String, Object> standardDetail = (Map<String, Object>) baseService.get(id);

		return standardDetail;
		
	}

}
