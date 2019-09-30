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

@Service("standardinfoSaveHandler")
public class StandardInfoSaveHandler extends AbstractSaveHandler {

	private EsService EsService;
	private static String ALIAS_TRAINSTANDARDINFO = "TRAINSTANDARDINFO";// 课程查询

	public StandardInfoSaveHandler(IBaseService baseService) {
		super(baseService);
	}

	@Override
	@Transactional
	public Object save(Map<String, Object> requestBody) throws Exception{
		validation(requestBody);
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS_TRAINSTANDARDINFO);
		Object id = baseService.save(requestBody);
		return id;
		
	}

	private void validation(Map<String, Object> requestBody) {
		Object title = requestBody.get("title");
		ValidationUtils.notNull(title, "法律规则制度标题不能为空");
		Object articleType = requestBody.get("articleType");
		ValidationUtils.notNull(articleType, "规则制度类型不能为空");
		Object publishTime = requestBody.get("publishTime");
		ValidationUtils.notNull(publishTime, "颁发时间不能为空");
		Object belongAreaCode = requestBody.get("belongAreaCode");
		ValidationUtils.notNull(belongAreaCode, "所属区域Code不能为空");
		Object belongDepCode = requestBody.get("belongDepCode");
		ValidationUtils.notNull(belongDepCode, "部门code不能为空");
		Object belongDepName = requestBody.get("belongDepName");
		ValidationUtils.notNull(belongDepName, "部门名称不能为空");
		Object creationId = requestBody.get("creationId");
		ValidationUtils.notNull(creationId, "创建人ID不能为空");
		Object creationName = requestBody.get("creationName");
		ValidationUtils.notNull(creationName, "创建人用户名不能为空");
		
	}

}
