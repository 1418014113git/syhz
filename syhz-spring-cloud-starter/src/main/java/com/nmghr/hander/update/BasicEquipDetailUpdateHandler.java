package com.nmghr.hander.update;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.common.Result;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractUpdateHandler;
import com.nmghr.basic.core.util.ValidationUtils;
import com.nmghr.util.SyhzUtil;

/**
 * 装备明细编辑
 * 
 * @author heijiantao
 * @date 2019年10月28日
 * @version 1.0
 */
@Service("basicequipdetailUpdateHandler")
public class BasicEquipDetailUpdateHandler extends AbstractUpdateHandler {

	public BasicEquipDetailUpdateHandler(IBaseService baseService) {
		super(baseService);
		// TODO Auto-generated constructor stub
	}

	@Override
	@Transactional
	public Object update(String id, Map<String, Object> requestBody) throws Exception {
		validation(requestBody);
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "BASICEQUIPDETAIL");
		baseService.update(id, requestBody);
		return updateInfo(id, requestBody);// 更新主表
	}

	private Object updateInfo(String id, Map<String, Object> requestBody) throws Exception {
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "BASICEQUIPDETAILONE");// 查询总配备数量
		requestBody.put("id", id);
		Map<String, Object> detailMap = (Map<String, Object>) baseService.get(requestBody);
		int detailNumber = SyhzUtil.setDateInt(detailMap.get("detailNumber"));
		int delFlage = SyhzUtil.setDateInt(requestBody.get("delFlage"));// 0正常1删除
		String infoId = SyhzUtil.setDate(detailMap.get("id"));
		int addNum = SyhzUtil.setDateInt(requestBody.get("equipNumber"));
		Map<String, Object> infoMap = new HashMap();
		infoMap.put("equipNumber", detailNumber);
		infoMap.put("userId", SyhzUtil.setDate(requestBody.get("userId")));
		infoMap.put("userName", SyhzUtil.setDate(requestBody.get("userName")));
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "BASICEQUIPMAXDATE");// 查询最小购买日期
		Map<String, Object> DateMap = (Map<String, Object>) baseService.get(infoId);
		if (DateMap != null) {
			infoMap.put("purchaseTime", SyhzUtil.setDate(DateMap.get("purchaseTime")));
			LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "BASICEQUIPINFOONE");
			return baseService.update(infoId, infoMap);// 更新主表装备数量及购买时间
		} else {
			LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "BASICEQUIPINFO");
			baseService.remove(infoId);// 删除主表记录
			return Result.ok("");
		}
	}

	private static void validation(Map<String, Object> requestBody) {
		Object equipNumber = requestBody.get("equipNumber");
		ValidationUtils.notNull(equipNumber, "配备数量不能为空");
		Object purchaseTime = requestBody.get("purchaseTime");
		ValidationUtils.notNull(purchaseTime, "采购日期不能为空");
		Object userId = requestBody.get("userId");
		ValidationUtils.notNull(userId, "未获取到用户信息");
		Object userName = requestBody.get("userName");
		ValidationUtils.notNull(userName, "未获取到用户信息");
	}

}
