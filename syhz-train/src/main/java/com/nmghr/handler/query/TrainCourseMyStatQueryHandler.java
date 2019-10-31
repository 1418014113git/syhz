package com.nmghr.handler.query;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractQueryHandler;
import com.nmghr.util.SyhzUtil;

@Service("traincoursemystatlogQueryHandler")
public class TrainCourseMyStatQueryHandler extends AbstractQueryHandler{

	//	我的学习
	private static final String ALIAS_TRAINCOURSELOG = "TRAINCOURSELOG"; // 查询浏览记录
	private static final String ALIAS_TRAINCOURSEPERSONTOTOL = "TRAINCOURSEPERSONTOTOL"; // 查询次数
	
	public TrainCourseMyStatQueryHandler(IBaseService baseService) {
		super(baseService);
	}
	
	@Override
	public Object list(Map<String, Object> requestMap) throws Exception {
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS_TRAINCOURSELOG);
		List<Map<String,Object>> list = (List<Map<String, Object>>) baseService.list(requestMap);
		
		String courseId = SyhzUtil.setDate(requestMap.get("id"));
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS_TRAINCOURSEPERSONTOTOL);
		Map<String, Object> totol = (Map<String, Object>) baseService.get(courseId);
		Map<String, Object> mystart = new HashMap<String, Object>();
		mystart.put("list", list);
		mystart.put("totol", totol);
		return mystart;
	}
	

}
