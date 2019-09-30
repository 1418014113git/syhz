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

@Service("industryinfoQueryHandler")
public class IndustryInfoQueryHandler extends AbstractQueryHandler {

	private static String ALIAS_TRAININDUSTRYINFO = "TRAININDUSTRYINFO";
	private static String ALIAS_ENCLOSURE = "KNOWLEDGEENCLOSURE";
	private static int belong_mode = 2;// 1 法律法规、2行业标准、3规则制度、4案例指引

	public IndustryInfoQueryHandler(IBaseService baseService) {
		super(baseService);
	}

	public Object list(Map<String, Object> requestBody) throws Exception {
		List<Map> mapList = (List<Map>) requestBody.get("data");
		int pageTotal = SyhzUtil.setDateInt(requestBody.get("pageTotal"));
		if (mapList != null && mapList.size() > 0) {
			LinkedList<String> strArray = new LinkedList<String>();
			for (Map map : mapList) {
				strArray.add(String.valueOf(map.get("documentId")));
			}
			LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS_TRAININDUSTRYINFO);
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("ids", strArray);
			List<Map<String, Object>> list = (List<Map<String, Object>>) baseService.list(paramMap);
			requestBody.put("list", list);
			requestBody.remove("data");
			if (list == null || list.size() <= 0) {
				requestBody.put("totalCount", 0);
			}
			return requestBody;
		}
		return requestBody;

	}

	public Object get(String id) throws Exception {
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS_TRAININDUSTRYINFO);
		Map<String, Object> lawinfo = (Map<String, Object>) baseService.get(id);
		lawinfo.put("tableId", id);
		lawinfo.put("belongMode", belong_mode);// 法律法规
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS_ENCLOSURE);
		List<Map<String, Object>> list = (List<Map<String, Object>>) baseService.list(lawinfo);
		lawinfo.put("enclosure", list);
		return lawinfo;

	}

}
