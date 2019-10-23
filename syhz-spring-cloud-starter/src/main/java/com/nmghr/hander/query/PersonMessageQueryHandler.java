package com.nmghr.hander.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractQueryHandler;

@Service("personMessageQueryHandler")
public class PersonMessageQueryHandler extends AbstractQueryHandler {
	
	private static String ALIAS_PERSONMESSAGE = "PERSONMESSAGE";// 字典

	public PersonMessageQueryHandler(IBaseService baseService) {
		super(baseService);
	}

	public Object list(Map<String, Object> requestBody) throws Exception {

		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS_PERSONMESSAGE);
		List<Map<String, Object>> list = (List<Map<String, Object>>) baseService.list(requestBody);
		List<Map<String, Object>> mz = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> zzmm = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> whcd = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> xw = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> xrzj = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> xrzwpcs = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> xrzwdd = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> xrzwzhd = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> xrzwzod = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> gzgw = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> ryztmj = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> ryztfj = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> rylx = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> jgszms = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> ydzt = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> zyzz = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < list.size(); i++) {
			if ("mz".equals(String.valueOf(list.get(i).get("codeLx")))) {
				mz.add(list.get(i));
			} else if ("zzmm".equals(String.valueOf(list.get(i).get("codeLx")))) {
				zzmm.add(list.get(i));
			} else if ("whcd".equals(String.valueOf(list.get(i).get("codeLx")))) {
				whcd.add(list.get(i));
			} else if ("xw".equals(String.valueOf(list.get(i).get("codeLx")))) {
				xw.add(list.get(i));
			} else if ("xrzj".equals(String.valueOf(list.get(i).get("codeLx")))) {
				xrzj.add(list.get(i));
			} else if ("xrzwpcs".equals(String.valueOf(list.get(i).get("codeLx")))) {
				xrzwpcs.add(list.get(i));
			} else if ("xrzwdd".equals(String.valueOf(list.get(i).get("codeLx")))) {
				xrzwdd.add(list.get(i));
			} else if ("xrzwzhd".equals(String.valueOf(list.get(i).get("codeLx")))) {
				xrzwzhd.add(list.get(i));
			} else if ("xrzwzod".equals(String.valueOf(list.get(i).get("codeLx")))) {
				xrzwzod.add(list.get(i));
			} else if ("gzgw".equals(String.valueOf(list.get(i).get("codeLx")))) {
				gzgw.add(list.get(i));
			} else if ("ryztmj".equals(String.valueOf(list.get(i).get("codeLx")))) {
				ryztmj.add(list.get(i));
			} else if ("ryztfj".equals(String.valueOf(list.get(i).get("codeLx")))) {
				ryztfj.add(list.get(i));
			} else if ("rylx".equals(String.valueOf(list.get(i).get("codeLx")))) {
				rylx.add(list.get(i));
			} else if ("jgszms".equals(String.valueOf(list.get(i).get("codeLx")))) {
				jgszms.add(list.get(i));
			} else if ("ydzt".equals(String.valueOf(list.get(i).get("codeLx")))) {
				ydzt.add(list.get(i));
			} else if ("zyzz".equals(String.valueOf(list.get(i).get("codeLx")))) {
				zyzz.add(list.get(i));
			}
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("mz", mz);
		map.put("zzmm", zzmm);
		map.put("whcd", whcd);
		map.put("xw", xw);
		map.put("xrzj", xrzj);
		map.put("xrzwpcs", xrzwpcs);
		map.put("xrzwdd", xrzwdd);
		map.put("xrzwzhd", xrzwzhd);
		map.put("xrzwzod", xrzwzod);
		map.put("gzgw", gzgw);
		map.put("ryztmj", ryztmj);
		map.put("ryztfj", ryztfj);
		map.put("rylx", rylx);
		map.put("jgszms", jgszms);
		map.put("ydzt", ydzt);
		map.put("zyzz", zyzz);
		return map;
		
	}
	

}
