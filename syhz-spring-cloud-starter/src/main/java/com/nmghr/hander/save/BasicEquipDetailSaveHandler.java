package com.nmghr.hander.save;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractSaveHandler;
import com.nmghr.basic.core.util.ValidationUtils;

/**
 * 装备明细添加
 * 
 * @author heijiantao
 * @date 2019年10月28日
 * @version 1.0
 */
@Service("basicequipdetailSaveHandler")
public class BasicEquipDetailSaveHandler extends AbstractSaveHandler {

	public BasicEquipDetailSaveHandler(IBaseService baseService) {
		super(baseService);
	}

	@Transactional
	public Object save(Map<String, Object> requestBody) throws Exception {
		validation(requestBody);
		Object groupId = requestBody.get("groupId");
		Object allocateId = requestBody.get("allocateId");
		Object equipNumber = requestBody.get("equipNumber");
		Object purchaseTime = requestBody.get("purchaseTime");
		Object belongDepCode = requestBody.get("belongDepCode");
		Object userId = requestBody.get("userId");
		Object userName = requestBody.get("userName");
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "BASICEQUIPONE");
		Map<String, Object> map = (Map<String, Object>) baseService.get(requestBody);// 主表是否已有记录
		if (map != null && map.containsKey("id")) {
			Map<String, Object> equipinfoMap = new HashMap<String, Object>();
			String equipId = String.valueOf(map.get("id"));
			String dateFlag = String.valueOf(map.get("dateFlag"));
			Map<String, Object> dateMap = new HashMap<String, Object>();
			equipinfoMap.put("equipNumber", equipNumber);
			equipinfoMap.put("userId", userId);
			equipinfoMap.put("userName", userName);
			if ("1".equals(dateFlag)) {
				equipinfoMap.put("purchaseTime", purchaseTime);
			}
			LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "BASICEQUIPINFO");
			baseService.update(equipId, equipinfoMap);// 更新主表装备数量及购买时间
			requestBody.put("equipId", equipId);
			requestBody.put("delFlage", 0);
			LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "BASICEQUIPDETAIL");
			return baseService.save(requestBody);// 添加详情表记录
		} else {
			requestBody.put("equipStatus", 0);
			LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "BASICEQUIPINFO");// 添加主表记录
			Object equipId = baseService.save(requestBody);
			requestBody.put("equipId", equipId);
			requestBody.put("delFlage", 0);
			LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "BASICEQUIPDETAIL");// 添加详情表记录
			return baseService.save(requestBody);
		}
	}

	private static void validation(Map<String, Object> requestBody) {
		Object groupId = requestBody.get("groupId");
		ValidationUtils.notNull(groupId, "请选择装备分类");
		Object allocateId = requestBody.get("allocateId");
		ValidationUtils.notNull(allocateId, "请选择配备项目");
		Object equipNumber = requestBody.get("equipNumber");
		ValidationUtils.notNull(equipNumber, "配备数量不能为空");
		Object purchaseTime = requestBody.get("purchaseTime");
		ValidationUtils.notNull(purchaseTime, "请选择采购日期");
		Object belongDepCode = requestBody.get("belongDepCode");
		ValidationUtils.notNull(belongDepCode, "无部门信息");
	}

}
