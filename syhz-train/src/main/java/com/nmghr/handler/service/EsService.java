package com.nmghr.handler.service;

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

@Service
public class EsService {
	@Autowired
	private BBossESStarter bbossESStarter;

	public void CreateIndice() {
		// 创建加载配置文件的客户端工具，单实例多线程安全
		ClientInterface clientUtil = bbossESStarter.getConfigRestClient("esmapper/lawInfo.xml");
		try {
			// 判读索引表article是否存在，存在返回true，不存在返回false
			boolean exist = clientUtil.existIndice("lawinfo");// 索引名不支持小写
			// 如果索引表article已经存在先删除mapping
			if (exist) {
				String r = clientUtil.dropIndice("lawinfo");
				exist = clientUtil.existIndice("lawinfo");
				// r = clientUtil.dropIndice("article");
				String articleIndice = clientUtil.getIndice("lawinfo");// 获取最新建立的索引表结构
			}
			// 创建索引表article
			clientUtil.createIndiceMapping("lawinfo", // 索引表名称
					"createlawInfoIndice");// 索引表mapping dsl脚本名称，在esmapper/lawInfo.xml中定义createlawInfoIndice
			String articleIndice = clientUtil.getIndice("lawinfo");// 获取最新建立的索引表结构
		} catch (ElasticSearchException e) {
			e.printStackTrace();
		}

	}

	public Map<String, Object> query(String index, Map<String, Object> map, String DSL) {
		int pageSize = SyhzUtil.setDateInt(map.get("pageSize"));
		int pageNum = SyhzUtil.setDateInt(map.get("pageNum"));
		map.put("num", pageNum * (pageSize - 1));
		ClientInterface clientUtil = bbossESStarter.getConfigRestClient("esmapper/" + index + ".xml");
		ESDatas<Map> esDatas = // ESDatas包含当前检索的记录集合，最多1000条记录，由dsl中的size属性指定
				clientUtil.searchList(index + "/_search", // demo为索引表，_search为检索操作action
						DSL, // esmapper/demo.xml中定义的dsl语句
						map, // 变量参数
						Map.class);// 返回的文档封装对象类型
		List<Map> mapList = esDatas.getDatas();
		long totalSize = esDatas.getTotalSize();
		Map<String, Object> reposneMap = new HashMap<String, Object>();
		reposneMap.put("data", mapList);
		reposneMap.put("pageSize", pageSize);
		reposneMap.put("pageNum", pageNum);
		reposneMap.put("totalCount", totalSize);
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
		clientUtil.updateDocument("lawinfo", // 索引表
				"_doc", // 索引类型
				documnetId, // 文档id
				object);
	}
}
