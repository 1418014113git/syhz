package com.nmghr.handler.query;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.page.Paging;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractQueryHandler;
import com.nmghr.util.SyhzUtil;

/**
 * 学习积分
 * 
 * @author heijiantao
 * @date 2019年10月9日
 * @version 1.0
 */
@Service("fractionlogQueryHandler")
public class FractionLogQueryHandler extends AbstractQueryHandler {

	private static String ALIAS_FRACTIONLOG = "FRACTIONLOG";
	private static String ALIAS_DAYFRACTIONLOG = "DAYFRACTIONLOG";
	private static int month = 1;

	public FractionLogQueryHandler(IBaseService baseService) {
		super(baseService);
		// TODO Auto-generated constructor stub
	}

	public Object list(Map<String, Object> requestBody) throws Exception {
		int pageNum = 1;
		if (requestBody.get("pageNum") != null) {
			pageNum = Integer.parseInt(requestBody.get("pageNum").toString());
		}
		int pageSize = 10;
		if (requestBody.get("pageSize") != null) {
			pageSize = Integer.parseInt(requestBody.get("pageSize").toString());
		}
		String id = SyhzUtil.setDate(requestBody.get("id"));
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS_FRACTIONLOG);
		Map<String, Object> fraction = (Map<String, Object>) baseService.get(id);// 我的积分
		Map<String, Object> param = new HashMap<String, Object>();
		List<Map<String, Object>> systemRank = (List<Map<String, Object>>) baseService.list(param);// 总排行
		param.put("months", month);
		List<Map<String, Object>> monthRank = (List<Map<String, Object>>) baseService.list(param);// 月排行

		param.put("userId", id);
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS_DAYFRACTIONLOG);
		List<Map<String, Object>> dayFraction = (List<Map<String, Object>>) baseService.list(param);// 赚取积分
		param.clear();
		param.put("fraction", fraction);
		Page page = PageHelper.startPage(pageNum, pageSize);
		LocalThreadStorage.put(Constant.CONTROLLER_PAGE_TOTALCOUNT, systemRank.size());
		param.put("systemRank", new Paging(pageSize, pageNum, systemRank.size(), systemRank));
		LocalThreadStorage.put(Constant.CONTROLLER_PAGE_TOTALCOUNT, monthRank.size());
		param.put("monthRank", new Paging(pageSize, pageNum, monthRank.size(), monthRank));
		param.put("dayFraction", dayFraction);
		return param;
	}
}
