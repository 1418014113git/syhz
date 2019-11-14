package com.nmghr.handler.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractQueryHandler;

@Service("sysMessagesQueryHandler")
public class SysMessagesQueryHandler extends AbstractQueryHandler {

	private static String ALIAS_SYSMESSAGES = "SYSMESSAGES";// 消息中心

	public SysMessagesQueryHandler(IBaseService baseService) {
		super(baseService);
	}

	public Object list(Map<String, Object> requestBody) throws Exception {
		// int pageNum = 1;
		// if (requestBody.get("pageNum") != null) {
		// pageNum = Integer.parseInt(requestBody.get("pageNum").toString());
		// }
		// int pageSize = 10;
		// if (requestBody.get("pageSize") != null) {
		// pageSize = Integer.parseInt(requestBody.get("pageSize").toString());
		// }
		// Page page = PageHelper.startPage(pageNum, pageSize);

		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS_SYSMESSAGES);
		List<Map<String, Object>> list = (List<Map<String, Object>>) baseService.list(requestBody);
		List<Map<String, Object>> list0 = new ArrayList<Map<String, Object>>();

		List<Map<String, Object>> list1 = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> bussionType1 = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> bussionType2 = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> bussionType3 = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> bussionType4 = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < list.size(); i++) {
			if ("0".equals(String.valueOf(list.get(i).get("status")))) {
				list0.add(list.get(i));
			} else if ("1".equals(String.valueOf(list.get(i).get("status")))) {
				list1.add(list.get(i));
			}
		}

		for (int i = 0; i < list.size(); i++) {
			if ("1".equals(String.valueOf(list.get(i).get("bussionType")))) {
				bussionType1.add(list.get(i));
			} else if ("2".equals(String.valueOf(list.get(i).get("bussionType")))) {
				bussionType2.add(list.get(i));
			} else if ("3".equals(String.valueOf(list.get(i).get("bussionType")))) {
				bussionType3.add(list.get(i));
			} else if ("4".equals(String.valueOf(list.get(i).get("bussionType")))) {
				bussionType4.add(list.get(i));
			}
		}

		Map<String, Object> map = new HashMap<String, Object>();
		//
		// // param.put("systemRank", new Paging(pageSize, pageNum, list0.size(),
		// list0));
		//
		// LocalThreadStorage.put(Constant.CONTROLLER_PAGE_TOTALCOUNT, list.size());
		map.put("list", list.size());
		map.put("list0", list0.size());
		map.put("list1", list1.size());
		map.put("bussionType1", bussionType1.size());
		map.put("bussionType2", bussionType2.size());
		map.put("bussionType3", bussionType3.size());
		map.put("bussionType4", bussionType4.size());
		return map;

	}

}
