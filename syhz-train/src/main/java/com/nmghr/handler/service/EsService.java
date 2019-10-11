package com.nmghr.handler.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.frameworkset.elasticsearch.ElasticSearchException;
import org.frameworkset.elasticsearch.boot.BBossESStarter;
import org.frameworkset.elasticsearch.client.ClientInterface;
import org.frameworkset.elasticsearch.entity.ESDatas;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nmghr.util.SyhzUtil;

/**
 * Es公共类
 * 
 * @author heijiantao
 * @date 2019年9月26日
 * @version 1.0
 */
@Service
public class EsService {
	@Autowired
	private BBossESStarter bbossESStarter;

	public Map<String, Object> query(String index, Map<String, Object> map, String DSL) {
		int pageSize = SyhzUtil.setDateInt(map.get("pageSize"));
		int pageNum = SyhzUtil.setDateInt(map.get("pageNum"));
		map.put("num", (pageNum - 1) * pageSize);
		Map<String, Object> reposneMap = new HashMap<String, Object>();

		try {
			String category = SyhzUtil.setDate(map.get("category"));
			if (!"".equals(category)) {
				map.put("category", category.replaceAll(",", " "));
			}
			ClientInterface clientUtil = bbossESStarter.getConfigRestClient("esmapper/" + index + ".xml");
			ESDatas<Map> esDatas = // ESDatas包含当前检索的记录集合，最多1000条记录，由dsl中的size属性指定
					clientUtil.searchList(index + "/_search", // demo为索引表，_search为检索操作action
							DSL, // esmapper/demo.xml中定义的dsl语句
							map, // 变量参数
							Map.class);// 返回的文档封装对象类型
			List<Map> mapList = esDatas.getDatas();
			long totalSize = esDatas.getTotalSize();
			reposneMap.put("data", mapList);
			reposneMap.put("pageSize", pageSize);
			reposneMap.put("pageNum", pageNum);
			reposneMap.put("totalCount", totalSize);
		} catch (Exception e) {
			Map documentMap = new HashMap();
			documentMap.put("documnetId", -1);
			List<Map> dList = new ArrayList<Map>();
			dList.add(documentMap);
			reposneMap.put("data", dList);
			reposneMap.put("pageSize", pageSize);
			reposneMap.put("pageNum", pageNum);
			reposneMap.put("totalCount", 0);
		}

		return reposneMap;
	}

	public String insert(Map<String, Object> map, String index, Object object) {
		ClientInterface clientUtil = bbossESStarter.getRestClient();
		String id = String.valueOf(map.get("documentId"));
		String documentId = "";
		try {
			String response = clientUtil.addDocument(index, // 索引表
					"_doc/" + id, // 索引类型
					object);
			documentId = id.toString();
		} catch (ElasticSearchException e) {
			documentId = "false";
		}
		return documentId;
	}

	public void delete(String index, String documentId) {
		ClientInterface clientUtil = bbossESStarter.getRestClient();
		clientUtil.deleteDocument(index, "_doc", documentId);
	}

	public void update(Map<String, Object> map, String documnetId, String index, Object object) {
		ClientInterface clientUtil = bbossESStarter.getRestClient();
		clientUtil.updateDocument(index + "/_doc/" + documnetId, // 索引表
				"_doc", // 索引类型
				"", // 文档id
				toMap(map));
	}

	public Map<String, Object> toMap(Map<String, Object> map) {
		Map<String, Object> esMap = new HashMap<String, Object>();
		esMap.put("title", SyhzUtil.setDate(map.get("title")));
		esMap.put("articleType", SyhzUtil.setDate(map.get("articleType")));
		esMap.put("category", SyhzUtil.setDate(map.get("category")));
		esMap.put("content", SyhzUtil.setDate(map.get("content")));
		esMap.put("publishTime", SyhzUtil.setDate(map.get("publishTime")));
		return esMap;

	}

	public void auidt(String index, String documentId) {
		ClientInterface clientUtil = bbossESStarter.getConfigRestClient("esmapper/" + index + ".xml");
		clientUtil.updateByQuery(index + "/_doc/" + documentId + "/_update", "audit");
	}

	public void remove(String index, String documentId) {
		ClientInterface clientUtil = bbossESStarter.getConfigRestClient("esmapper/" + index + ".xml");
		clientUtil.updateByQuery(index + "/_doc/" + documentId + "/_update", "delete");
	}
}
