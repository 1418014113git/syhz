package com.nmghr.hander.update;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.common.exception.GlobalErrorEnum;
import com.nmghr.basic.common.exception.GlobalErrorException;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractUpdateHandler;
import com.nmghr.basic.core.util.ValidationUtils;

@Service("basicEquipAllocateUpdateHandler")
public class BasicEquipAllocateUpdateHandler extends AbstractUpdateHandler{

	private static final String ALIAS_BASICEQUIPALLOCATE = "BASICEQUIPALLOCATE";
	
	public BasicEquipAllocateUpdateHandler(IBaseService baseService) {
		super(baseService);
	}
	
	public Object update(String id, Map<String, Object> requestBody) throws Exception {
		validation(requestBody);
		try{
			LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS_BASICEQUIPALLOCATE);
		    baseService.update(id, requestBody);
		    return "装备信息保存成功！";

		}catch(Exception e1){
		throw new GlobalErrorException(GlobalErrorEnum.PARAM_NOT_VALID.getCode(), "装备信息保存失败，请联系管理员！");

		}
		
	}

	private void validation(Map<String, Object> requestBody) {
		Object allocateName = requestBody.get("allocateName");
		ValidationUtils.notNull(allocateName, "配备项名称不能为空");
		Object provinceCondition = requestBody.get("provinceCondition");
		ValidationUtils.notNull(provinceCondition, "总队不能为空");
		Object cityCondition = requestBody.get("cityCondition");
		ValidationUtils.notNull(cityCondition, "支队不能为空");
		Object areaCondition = requestBody.get("areaCondition");
		ValidationUtils.notNull(areaCondition, "大队不能为空");
		Object allocateType = requestBody.get("allocateType");
		ValidationUtils.notNull(allocateType, "配备项类型不能为空");
		Object unitType = requestBody.get("unitType");
		ValidationUtils.notNull(unitType, "计量单位类型不能为空");
		Object lastId = requestBody.get("lastId");
		ValidationUtils.notNull(lastId, "修改人id不能为空");
		Object lastName = requestBody.get("lastName");
		ValidationUtils.notNull(lastName, "修改人用户名不能为空");
		
	}

}
