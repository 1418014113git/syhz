package com.nmghr.handler.query;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.common.exception.GlobalErrorException;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractQueryHandler;
import com.nmghr.basic.core.util.ValidationUtils;
import com.nmghr.util.SyhzUtil;

@Service("trainCourseLogQueryHandler")
public class TrainCourseLogQueryHandler extends AbstractQueryHandler {

	private static String ALIAS_TRAINCOURSELOGSTATISTICS = "TRAINCOURSELOGSTATISTICS"; // 查询学习人次、时长、下载次数
	private static String ALIAS_TRAINCOURSESTATISTICS = "TRAINCOURSESTATISTICS"; // 查询资料发布与审核数量

	public TrainCourseLogQueryHandler(IBaseService baseService) {
		super(baseService);
	}
	
	@Override
	public Object list(Map<String, Object> requestMap) throws Exception {
		validation(requestMap);
		Map<String, Object> mystart = new HashMap<String, Object>();
		int areaCode3 = 6100;
		for (int i = 0; i < 12; i++) {
			int areaCode1 = areaCode3+i;
			String areaCode2 = String.valueOf(areaCode1);
			String areaCode4 = areaCode2 + "00";
			requestMap.put("areaCode", areaCode4);
			LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS_TRAINCOURSESTATISTICS);
			Map<String, Object> train = (Map<String, Object>) baseService.get(requestMap);
			LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS_TRAINCOURSELOGSTATISTICS);
			Map<String, Object> knowledge =  (Map<String, Object>) baseService.get(requestMap);
			train.putAll(knowledge);
			mystart.put("train"+i, train);
		}
		
		return mystart;
	}

	private void validation(Map<String, Object> requestMap) {
		Object creationTime = requestMap.get("creationTime");
		int tp = SyhzUtil.setDateInt(creationTime);
		if(tp == 1 || tp == 2 || tp == 3 || tp == 0) {
			
		} else {
			throw new GlobalErrorException("999886", "筛选条件creationTime取值超出范围");
		}
		
	}
	

}
