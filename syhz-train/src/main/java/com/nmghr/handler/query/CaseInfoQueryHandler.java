package com.nmghr.handler.query;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractQueryHandler;
import com.nmghr.util.SyhzUtil;

@Service("caseinfoQueryHandler")
public class CaseInfoQueryHandler extends AbstractQueryHandler {

	public CaseInfoQueryHandler(IBaseService baseService) {
		super(baseService);
	}

	private static String ALIAS_TRAINCASEINFO = "TRAINCASEINFO";
	private static String ALIAS_ENCLOSURE = "KNOWLEDGEENCLOSURE";
	private static int belong_mode = 4;// 1 法律法规、2行业标准、3规则制度、4案例指引



	public Object list(Map<String, Object> requestBody) throws Exception {
		List<Map> mapList = (List<Map>) requestBody.get("data");
		int pageTotal = SyhzUtil.setDateInt(requestBody.get("pageTotal"));
		if (mapList != null && mapList.size() > 0) {
			LinkedList<String> strArray = new LinkedList<String>();
			for (Map map : mapList) {
				strArray.add(String.valueOf(map.get("documentId")));
			}
			LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS_TRAINCASEINFO);
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("ids", strArray);
			List<Map<String, Object>> list = (List<Map<String, Object>>) baseService.list(paramMap);
			requestBody.put("list", list);
			if (list == null || list.size() <= 0) {
				requestBody.put("totalCount", 0);
			}
			requestBody.remove("data");
			return requestBody;
		}

		return requestBody;

	}

	public Object get(String id) throws Exception {
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS_TRAINCASEINFO);
		Map<String, Object> lawinfo = (Map<String, Object>) baseService.get(id);
		lawinfo.put("tableId", id);
		lawinfo.put("belongMode", belong_mode);// 案例指引
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS_ENCLOSURE);
		List<Map<String, Object>> list = (List<Map<String, Object>>) baseService.list(lawinfo);
		lawinfo.put("enclosure", list);
		return lawinfo;

	}


}
