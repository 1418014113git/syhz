package com.nmghr.es.crud;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.frameworkset.elasticsearch.ElasticSearchHelper;
import org.frameworkset.elasticsearch.boot.BBossESStarter;
import org.frameworkset.elasticsearch.client.ClientInterface;
import org.frameworkset.elasticsearch.entity.ESDatas;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nmghr.es.demo.Course;

@Service
public class courseCrud {
	@Autowired
	private BBossESStarter bbossESStarter;

	public void save(Map<String, Object> map) {
		// 创建创建/修改/获取/删除文档的客户端对象，单实例多线程安全
		ClientInterface clientUtil = bbossESStarter.getRestClient();
		// 构建一个对象，日期类型，字符串类型属性演示
		Course course = new Course();
		course.setAuditOpinion("as");
		course.setDescribe("能想到所属地");
		course.setRemark("撒娇的和教案集");
		course.setTitle("上课拉");
		course.setType("1");
		String response = clientUtil.addDocument("course", // 索引表
				"_doc", // 索引类型
				course);

	}

	public Map<String, Object> query(Map<String, Object> map) {
		// 创建创建/修改/获取/删除文档的客户端对象，单实例多线程安全
		ClientInterface clientUtil = ElasticSearchHelper.getConfigRestClientUtil("esmapper/course.xml");
		// 构建一个对象，日期类型，字符串类型属性演示
		Course course = new Course();
		Map<String, Object> params = new HashMap<String, Object>();
		if (map.get("search") != null) {
			params.put("search", String.valueOf(map.get("search")));
		}
		if (map.get("type") != null) {
			params.put("type", String.valueOf(map.get("type")));
		}
		String pageSize = String.valueOf(map.get("pageSize"));
		String pageNum = String.valueOf(map.get("pageNum"));
		params.put("pageNum", pageNum);// 设置每页返回的记录条数
		params.put("pageSize", pageSize);// 设置每页返回的记录条数
		ESDatas<Course> esDatas = // ESDatas包含当前检索的记录集合，最多1000条记录，由dsl中的size属性指定
				clientUtil.searchList("course/_search", // demo为索引表，_search为检索操作action
						"searchPage", // esmapper/demo.xml中定义的dsl语句
						params, // 变量参数
						Course.class);// 返回的文档封装对象类型
		List<Course> courseList = esDatas.getDatas();
		String idList = "";
		if (courseList != null) {
			for (Course c : courseList) {
				idList += c.getCourseId() + ",";
			}
		}
		idList = idList + "0";
		Map<String, Object> responseMap = new HashMap<String, Object>();
		responseMap.put("idList", idList);
		responseMap.put("pageTotol", courseList.size());
		return responseMap;
	}
}
