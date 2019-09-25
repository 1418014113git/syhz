package com.nmghr.handler.save;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractSaveHandler;
import com.nmghr.basic.core.util.ValidationUtils;
import com.nmghr.handler.service.EsService;

@Service("lawinfoSaveHandler")
public class LawInfoSaveHandler extends AbstractSaveHandler {

	private EsService EsService;
	private static String ALIAS_LAWINFO = "TRAINLAWINFO";// 法律法规

	public LawInfoSaveHandler(IBaseService baseService) {
		super(baseService);
	}

	@Override
	@Transactional
	public Object save(Map<String, Object> requestBody) throws Exception {
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS_LAWINFO);
		baseService.save(requestBody);
		return requestBody;
	}

	private void validation(Map<String, Object> requestBody) {
		Object title = requestBody.get("title");
		ValidationUtils.notNull(title, "法律法规不能为空");
		Object articleType = requestBody.get("articleType");
		ValidationUtils.notNull(articleType, "法律法规类型");
		Object category = requestBody.get("category");
		ValidationUtils.notNull(category, "法律法规分类");
		Object publishTime = requestBody.get("publishTime");
		ValidationUtils.notNull(publishTime, "颁发时间");
		Object belongAreaCode = requestBody.get("belongAreaCode");
		ValidationUtils.notNull(belongAreaCode, "所属区域不能为空");
		Object belongDepCode = requestBody.get("belongDepCode");
		ValidationUtils.notNull(belongDepCode, "部门CODE不能为空");
		Object belongDepName = requestBody.get("belongDepName");
		ValidationUtils.notNull(belongDepName, "部门名称不能为空");
		Object creationId = requestBody.get("creationId");
		ValidationUtils.notNull(creationId, "创建人不能为空");
		Object creationName = requestBody.get("creationName");
		ValidationUtils.notNull(creationName, "创建人用户名不能为空");
		Object creationTime = requestBody.get("creationTime");
		ValidationUtils.notNull(creationTime, "创建时间不能为空");
	}

}
