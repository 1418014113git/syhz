package com.nmghr.handler.save;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractSaveHandler;
import com.nmghr.basic.core.util.ValidationUtils;
import com.nmghr.util.SyhzUtil;

@Service("konwledgeLogSaveHandler")
public class KonwledgeLogSaveHandler extends AbstractSaveHandler {

	// 添加浏览记录和更改浏览下载次数
	private static final String ALIAS_TRAINKNOWLEDGELOG = "TRAINKNOWLEDGELOG";
	private static final String ALIAS_KNOWLEDGELOGVIEW = "KNOWLEDGELOGVIEW";
	private static final String ALIAS_KNOWLEDGEENCLOSURE = "KNOWLEDGEENCLOSURE";

	public KonwledgeLogSaveHandler(IBaseService baseService) {
		super(baseService);
		// TODO Auto-generated constructor stub
	}

	@Transactional
	public Object save(Map<String, Object> requestBody) throws Exception {
		validation(requestBody);
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS_TRAINKNOWLEDGELOG);
		requestBody.put("stopTime", 0);
		Object ID = baseService.save(requestBody);
		String viewType = SyhzUtil.setDate(requestBody.get("viewType"));
		String tableId = SyhzUtil.setDate(requestBody.get("tableId"));
		String ensId = SyhzUtil.setDate(requestBody.get("ensId"));

		if ("0".equals(viewType)) {// 修改文章预览数
			LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS_KNOWLEDGELOGVIEW);
			baseService.update(tableId, requestBody);
		}
		if ("1".equals(viewType)) {// 修改附件预览数或下载数
			LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS_KNOWLEDGEENCLOSURE);
			baseService.update(ensId, requestBody);
		}
		return ID;

	}

	private void validation(Map<String, Object> requestBody) {
		Object areaCode = requestBody.get("areaCode");
		ValidationUtils.notNull(areaCode, "所属区域不能为空");
		Object belongDepCode = requestBody.get("belongDepCode");
		ValidationUtils.notNull(belongDepCode, "部门code不能为空");
		Object belongDepName = requestBody.get("belongDepName");
		ValidationUtils.notNull(belongDepName, "部门名称不能为空");

	}

}
