package com.nmghr.handler.update;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractUpdateHandler;
import com.nmghr.util.SyhzUtil;

@Service("KnowledgeRemoveUpdateHandler")
public class KnowledgeRemoveUpdateHandler extends AbstractUpdateHandler {
	private static String ALIAS_TRAINKNOWLEDGEBASE = "KNOWLEDGELOGREMOVE";
	// private static String ALIAS_WORKORDERENABLE = "WORKORDERENABLE";
	private static String ALIAS_TRAINKNOWLEDGEENCLOUSURE = "TRAINKNOWLEDGEENCLOUSURE";
	private static String ALIAS_TRAINKNOWCLOSURE = "TRAINKNOWCLOSURE";
	private static String ALIAS_TRAINWOKORDER = "TRAINWOKORDER";
	private static String ALIAS_TRAINWORKORDERFLOW = "TRAINWORKORDERFLOW";

	public KnowledgeRemoveUpdateHandler(IBaseService baseService) {
		super(baseService);
		// TODO Auto-generated constructor stub
	}

	@Override
	@Transactional
	public Object update(String id, Map<String, Object> requestBody) throws Exception {
		requestBody.put("id", id);
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS_TRAINKNOWCLOSURE);// 查附件
		List<Map<String, Object>> list = (List<Map<String, Object>>) baseService.list(requestBody);
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS_TRAINWORKORDERFLOW);// 查附件
		baseService.remove(requestBody);// 工单流表
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS_TRAINWOKORDER);// 查附件
		baseService.remove(requestBody);// 工单流表
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS_TRAINKNOWLEDGEENCLOUSURE);// 查附件
		baseService.remove(requestBody);// 工单流表
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS_TRAINKNOWLEDGEBASE);// 查附件
		baseService.remove(requestBody);// 工单流表
		return list;

	}

	private Object toList(List<Map<String, Object>> list) {
		if (list != null && list.size() > 0) {
			String[] s = new String[] {}, b = new String[] {};
			int i = 0;
			for (Map<String, Object> map : list) {
				String oldPath = SyhzUtil.setDate(map.get("oldPath"));
				String newOld = SyhzUtil.setDate(map.get("newOld"));
				s[i] = oldPath;
				b[i] = newOld;
			}
			return Arrays.asList(s).addAll(Arrays.asList(b));
		} else {
			return new ArrayList();
		}
	}
}
