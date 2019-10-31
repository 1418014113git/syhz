package com.nmghr.handler.query;

import java.util.List;
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
	private static String ALIAS_ENCLOSURE = "KNOWLEDGEENCLOSURE";

	public TrainStandardInfoDetailQueryHandler(IBaseService baseService) {
		super(baseService);
	}

	public Object get(String id) throws Exception {
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS_TRAINSTANDARDINFO);
		Map<String, Object> standardDetail = (Map<String, Object>) baseService.get(id);
		standardDetail.put("tableId", id);
		standardDetail.put("belongMode", 3);// 法律法规
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS_ENCLOSURE);
		List<Map<String, Object>> list = (List<Map<String, Object>>) baseService.list(standardDetail);
		standardDetail.put("enclosure", list);
		return standardDetail;

	}

}
