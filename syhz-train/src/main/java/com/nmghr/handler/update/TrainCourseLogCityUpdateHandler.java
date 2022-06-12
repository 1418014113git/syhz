package com.nmghr.handler.update;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractUpdateHandler;


/**
 * 查询区县统计
 * @author wangzhe
 *
 */
@Service("trainCourseLogCityUpdateHandler")
public class TrainCourseLogCityUpdateHandler extends AbstractUpdateHandler {

	private static String ALIAS_TRAINCOURSELOGSTATISTICS = "TRAINCOURSELOGSTATISTICS";
	private static String ALIAS_TRAINCOURSESTATISTICS = "TRAINCOURSESTATISTICS";

	public TrainCourseLogCityUpdateHandler(IBaseService baseService) {
		super(baseService);
	}
	
	public Object  update(String id, Map<String, Object> requestBody) throws Exception {
//		validation(requestMap);
		Map<String, Object> mystart = new HashMap<String, Object>();
		List<Map<String, Object>> mapList= (List<Map<String, Object>>) requestBody.get("dept");
		String coursType = (String) requestBody.get("coursType");
		String startDate = (String) requestBody.get("startDate");
		String endDate = (String) requestBody.get("endDate");
		String creationTime = (String) requestBody.get("creationTime");
		System.out.println(mapList);
		for (int i = 0; i < mapList.size(); i++) {
			String areaCode = (String) mapList.get(i).get("areaCode");
			String departName = (String) mapList.get(i).get("departName");
			Map<String, Object> mapRequest = new HashMap<String, Object>();
			mapRequest.put("areaCode", areaCode);
			mapRequest.put("coursType", coursType);
			mapRequest.put("startDate", startDate);
			mapRequest.put("endDate", endDate);
			mapRequest.put("creationTime", creationTime);
			
			LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS_TRAINCOURSESTATISTICS);
			Map<String, Object> train = (Map<String, Object>) baseService.get(mapRequest);
			LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS_TRAINCOURSELOGSTATISTICS);
			Map<String, Object> knowledge = (Map<String, Object>) baseService.get(mapRequest);
			train.putAll(knowledge);
			mystart.put(departName, train);
		}
		
		return mystart;
	}

//	private void validation(Map<String, Object> requestMap) {
//		Object creationTime = requestMap.get("creationTime");
//		int tp = SyhzUtil.setDateInt(creationTime);
//		if(tp == 1 || tp == 2 || tp == 3) {
//			
//		} else {
//			throw new GlobalErrorException("999886", "筛选条件creationTime取值超出范围");
//		}
//		
//	}
	

}
