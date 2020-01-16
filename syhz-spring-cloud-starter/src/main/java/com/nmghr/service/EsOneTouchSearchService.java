/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.nmghr.service;

import com.alibaba.fastjson.JSONObject;
import org.frameworkset.elasticsearch.boot.BBossESStarter;
import org.frameworkset.elasticsearch.client.ClientInterface;
import org.frameworkset.elasticsearch.entity.ESDatas;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 一键搜索
 *
 * @author mxd.
 * @date 2020-01-15 - 21:27.
 */
@Service
public class EsOneTouchSearchService {

    @Autowired
    private BBossESStarter bbossESStarter;

    /**
     * 查询
     * @param xmlName xml文件名
     * @param index 索引名
     * @param map 查询参数
     * @param DSL
     * @return
     */
    public Map<String, Object> query(String xmlName, String index, Map<String, Object> map, String DSL) {
        if (ObjectUtils.isEmpty(map.get("pageSize")) || ObjectUtils.isEmpty(map.get("pageNum"))) {
            return null;
        }
        int pageSize = Integer.valueOf(map.get("pageSize").toString());
        int pageNum = Integer.valueOf(map.get("pageNum").toString()) == 0 ? 1 : Integer.valueOf(map.get("pageNum").toString());
        map.put("num", (pageNum - 1) * pageSize);
        Map<String, Object> reposneMap = new HashMap<String, Object>(8);
        try {
            ClientInterface clientUtil = bbossESStarter.getConfigRestClient("esmapper/" + xmlName + ".xml");
            // ESDatas包含当前检索的记录集合，最多1000条记录，由dsl中的size属性指定
            // demo为索引表，_search为检索操作action
            // esmapper/demo.xml中定义的dsl语句
            // map 变量参数
            ESDatas<Map> esDatas = clientUtil.searchList(index + "/_search", DSL, map, Map.class);
            List<Map> mapList = esDatas.getDatas();

            long totalSize = esDatas.getTotalSize();
            reposneMap.put("data", mapList);
            reposneMap.put("pageSize", pageSize);
            reposneMap.put("pageNum", pageNum);
            reposneMap.put("totalCount", totalSize);
        } catch (Exception e) {
            Map documentMap = new HashMap(8);
            List<Map> dList = new ArrayList<Map>();
            dList.add(documentMap);
            reposneMap.put("data", dList);
            reposneMap.put("pageSize", pageSize);
            reposneMap.put("pageNum", pageNum);
            reposneMap.put("totalCount", 0);
        }
        return reposneMap;
    }

    /**
     * 为index建立别名
     * @param indexName
     * @param alias
     * @return
     */
    public boolean addAlias(String indexName, String alias) {
        ClientInterface clientUtil = bbossESStarter.getRestClient();
        Boolean exist = clientUtil.existIndice(indexName);
        if (exist) {
            String aliasStr = clientUtil.executeHttp("/"+indexName+"/_alias", "get");
            if (!aliasStr.contains(alias)) {
                String result = clientUtil.addAlias(indexName, alias);
                JSONObject codeNameJson = JSONObject.parseObject(result);
                return Boolean.valueOf(codeNameJson.get("acknowledged").toString());
            } else {
                return true;
            }
        }
        return false;
    }

    /**
     * 别名是否存在
     * @param indexName
     * @param alias
     * @return
     */
    public boolean existAlias(String indexName, String alias) {
        ClientInterface clientUtil = bbossESStarter.getRestClient();
        Boolean exist = clientUtil.existIndice(indexName);
        if (exist) {
            String aliasStr = clientUtil.executeHttp("/"+indexName+"/_alias", "get");
            return aliasStr.contains(alias);
        }
        return false;
    }

    /**
     * 移除别名
     * @param indexName
     * @param alias
     * @return
     */
    public String removeAlias(String indexName, String alias) {
        ClientInterface clientUtil = bbossESStarter.getRestClient();
        Boolean exist = clientUtil.existIndice(indexName);
        if (exist) {
            String aliasStr = clientUtil.removeAlias(indexName, alias);
            return aliasStr;
        }
        return null;
    }

    /**
     *
     * @param index
     * @param commandCode _aliases - post
     * @param DSL
     * @param method
     * @return
     */
    public String executeHttp(String index, String commandCode, String DSL, String method) {
        ClientInterface clientUtil = bbossESStarter.getConfigRestClient("esmapper/" + index + ".xml");
        String ret = clientUtil.executeHttp(commandCode, DSL, method);
        return ret;
    }


}
