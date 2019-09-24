package com.nmghr.handler.save;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.common.exception.GlobalErrorEnum;
import com.nmghr.basic.common.exception.GlobalErrorException;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractSaveHandler;
import com.nmghr.basic.core.util.ValidationUtils;
import com.nmghr.util.SyhzUtil;

@Service("traincourselogSaveHandler")
public class TrainCourseLogSaveHandler extends AbstractSaveHandler {
	
	// 添加浏览记录和更改浏览下载次数	
	private static final String ALIAS_TRAINCOURSELOG = "TRAINCOURSELOG";
	private static final String ALIAS_TRAINCOURSE = "TRAINCOURSE";
	
	public TrainCourseLogSaveHandler(IBaseService baseService) {
		super(baseService);
		
	}
	
	@Transactional
	public Object save(Map<String, Object> requestBody) throws Exception {
		validation(requestBody);
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS_TRAINCOURSELOG);
		Object ID= baseService.save(requestBody);
		

		String courseId = SyhzUtil.setDate(requestBody.get("courseId"));
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS_TRAINCOURSE);
		baseService.update(courseId, requestBody);
		return ID;
		
	}
	
	private void validation(Map<String, Object> requestBody) {
		Object type = requestBody.get("type");
		ValidationUtils.notNull(type, "操作方式不能为空");
		int tp = SyhzUtil.setDateInt(type);
		if(tp == 1 || tp == 0) {
			
		} else {
			throw new GlobalErrorException("999886", "操作方式取值超出范围");
		}
		
		Object areaCode = requestBody.get("areaCode");
		ValidationUtils.notNull(areaCode, "所属区域不能为空");
		Object belongDepCode = requestBody.get("belongDepCode");
		ValidationUtils.notNull(belongDepCode, "部门code不能为空");
		Object belongDepName = requestBody.get("belongDepName");
		ValidationUtils.notNull(belongDepName, "部门名称不能为空");
		
	}

}
