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

/**
 * 法律法规
 * 
 * @author heijiantao
 * @date 2019年9月25日
 * @version 1.0
 */
@Service("lawinfoQueryHandler")
public class LawInfoQueryHandler extends AbstractQueryHandler {
	private static String ALIAS_TRAINLAWINFO = "TRAINLAWINFO";

	public LawInfoQueryHandler(IBaseService baseService) {
		super(baseService);
		// TODO Auto-generated constructor stub
	}

	public Object list(Map<String, Object> requestBody) throws Exception {
		List<Map> mapList = (List<Map>) requestBody.get("data");
		int pageTotal = SyhzUtil.setDateInt(requestBody.get("pageTotal"));
		if (mapList != null && mapList.size() > 0) {
			LinkedList<String> strArray = new LinkedList<String>();
			for (Map map : mapList) {
				strArray.add(String.valueOf(map.get("documentId")));
			}
			LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS_TRAINLAWINFO);
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("ids", strArray);
			List<Map<String, Object>> list = (List<Map<String, Object>>) baseService.list(paramMap);
			requestBody.put("list", list);
			requestBody.remove("data");
			return requestBody;
		}

		return requestBody;

	}

}
