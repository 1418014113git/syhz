package com.nmghr.es.crud;

import java.util.Map;

import org.frameworkset.elasticsearch.ElasticSearchException;
import org.frameworkset.elasticsearch.boot.BBossESStarter;
import org.frameworkset.elasticsearch.client.ClientInterface;
import org.frameworkset.elasticsearch.client.ClientUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nmghr.es.demo.Article;

@Service
public class trainCrud {
	@Autowired
	private BBossESStarter bbossESStarter;

	public void CreateIndice() {
		// 创建加载配置文件的客户端工具，单实例多线程安全
		ClientInterface clientUtil = bbossESStarter.getConfigRestClient("esmapper/article.xml");
		try {
			// 判读索引表article是否存在，存在返回true，不存在返回false
			boolean exist = clientUtil.existIndice("article");

			// 如果索引表article已经存在先删除mapping
			if (exist) {
				String r = clientUtil.dropIndice("article");
				exist = clientUtil.existIndice("article");
				// r = clientUtil.dropIndice("article");
				String articleIndice = clientUtil.getIndice("article");// 获取最新建立的索引表结构
			}
			// 创建索引表article
			clientUtil.createIndiceMapping("article", // 索引表名称
					"createTrainIndice");// 索引表mapping dsl脚本名称，在esmapper/article.xml中定义createarticleIndice
			String articleIndice = clientUtil.getIndice("article");// 获取最新建立的索引表结构
		} catch (ElasticSearchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void query() {
		ClientInterface clientUtil = bbossESStarter.getConfigRestClient("esmapper/article.xml");
		String log = clientUtil.executeHttp("/article", ClientUtil.HTTP_GET);
	}

	public void insert(Map<String, Object> map) {
		// 创建创建/修改/获取/删除文档的客户端对象，单实例多线程安全
		ClientInterface clientUtil = bbossESStarter.getRestClient();
		// 构建一个对象，日期类型，字符串类型属性演示
		Article atricle = new Article();
		atricle.setAuditOpinion("同意");
		atricle.setAuditStatus(0);
		// atricle.setAuditTime(new Date());
		atricle.setCategory(1);
		atricle.setContent("航海技术等哈就后端的喝红酒");
		atricle.setAuditUserId(111);
		atricle.setBelongOrgId(114);
		atricle.setCreationId(1111);
		// atricle.setCreationTime(new Date());
		// atricle.setEffectiveTime(new Date());
		atricle.setEnable(1);
		atricle.setLastId(111);
		// atricle.setLatsTime(new Date());
		atricle.setModelCategory(1);
		atricle.setModelType(1);
		atricle.setPublishCode("A01");
		atricle.setRemark("备注");
		String response = clientUtil.addDocument("atricle", // 索引表
				"_doc", // 索引类型
				atricle);
	}

}
