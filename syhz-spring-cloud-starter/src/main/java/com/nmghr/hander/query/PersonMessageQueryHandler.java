package com.nmghr.hander.query;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractQueryHandler;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
		List<Map<String, Object>> fxxs = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> qszt = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> shzt = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> zbjl = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> zblx = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> zbzt = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> dbjb = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> dbajzt = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> dbajpczt = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> cbzt = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> qy = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> qxx = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> mtlx = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> xsfl = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> cjlx = new ArrayList<Map<String, Object>>();

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
			} else if ("fxxs".equals(String.valueOf(list.get(i).get("codeLx")))) {
				fxxs.add(list.get(i));
			} else if ("qszt".equals(String.valueOf(list.get(i).get("codeLx")))) {
				qszt.add(list.get(i));
			} else if ("shzt".equals(String.valueOf(list.get(i).get("codeLx")))) {
				shzt.add(list.get(i));
			} else if ("zbjl".equals(String.valueOf(list.get(i).get("codeLx")))) {
				zbjl.add(list.get(i));
			} else if ("zblx".equals(String.valueOf(list.get(i).get("codeLx")))) {
				zblx.add(list.get(i));
			} else if ("zbzt".equals(String.valueOf(list.get(i).get("codeLx")))) {
				zbzt.add(list.get(i));
			} else if ("dbjb".equals(String.valueOf(list.get(i).get("codeLx")))) {
				dbjb.add(list.get(i));
			} else if ("dbajzt".equals(String.valueOf(list.get(i).get("codeLx")))) {
				dbajzt.add(list.get(i));
			} else if ("dbajpczt".equals(String.valueOf(list.get(i).get("codeLx")))) {
				dbajpczt.add(list.get(i));
			} else if ("cbzt".equals(String.valueOf(list.get(i).get("codeLx")))) {
				cbzt.add(list.get(i));
			} else if ("qy".equals(String.valueOf(list.get(i).get("codeLx")))) {
				qy.add(list.get(i));
			} else if ("qxx".equals(String.valueOf(list.get(i).get("codeLx")))) {
				qxx.add(list.get(i));
			} else if ("mtlx".equals(String.valueOf(list.get(i).get("codeLx")))) {
				mtlx.add(list.get(i));
			} else if ("xsfl".equals(String.valueOf(list.get(i).get("codeLx")))) {
				xsfl.add(list.get(i));
			} else if ("cjlx".equals(String.valueOf(list.get(i).get("codeLx")))) {
				cjlx.add(list.get(i));
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
		map.put("fxxs", fxxs);
		map.put("qszt", qszt);
		map.put("shzt", shzt);
		map.put("zbjl", zbjl);
		map.put("zblx", zblx);
		map.put("zbzt", zbzt);
		map.put("dbjb", dbjb);
		map.put("dbajzt", dbajzt);
		map.put("dbajpczt", dbajpczt);
		map.put("cbzt", cbzt);
		map.put("qy", qy);
		map.put("qxx", qxx);
		map.put("mtlx", mtlx);
		map.put("xsfl", xsfl);
		map.put("cjlx", cjlx);
		return map;

	}

}
