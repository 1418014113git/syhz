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
import com.nmghr.basic.common.exception.GlobalErrorException;
import org.frameworkset.elasticsearch.boot.BBossESStarter;
import org.frameworkset.elasticsearch.client.ClientInterface;
import org.frameworkset.elasticsearch.entity.MapRestResponse;
import org.frameworkset.elasticsearch.entity.MapSearchHit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;


/**
 * 一键搜索
 *
 * @author mxd.
 * @date 2020-01-15 - 21:27.
 */
@Service
public class EsOneTouchSearchService {

    // 最大返回数量
    private static final int ES_RETURN_TOTALCOUNT = 1000;

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
    public Map<String, Object> query(String xmlName, String index, Map<String, Object> map, String DSL, Map<String, Object> indexCode) {
        if (ObjectUtils.isEmpty(map.get("pageSize")) || ObjectUtils.isEmpty(map.get("pageNum"))) {
            return null;
        }
        int pageSize = Integer.valueOf(map.get("pageSize").toString());
        int pageNum = Integer.valueOf(map.get("pageNum").toString()) == 0 ? 1 : Integer.valueOf(map.get("pageNum").toString());
        map.put("num", (pageNum - 1) * pageSize);
        Map<String, Object> reposneMap = new HashMap<String, Object>(8);
        try {
            ClientInterface clientUtil = bbossESStarter.getConfigRestClient("esmapper/" + xmlName + ".xml");
            long totalSize = 0;
            List<Object> dataList = new ArrayList<Object>();
            MapRestResponse esMap = clientUtil.search(index + "/_search", DSL, map);
            List<MapSearchHit> hits = esMap.getSearchHits().getHits();
            for (int i = 0; i < hits.size(); i++) {
                Map<String, Object> sourceMap = hits.get(i).getSource();
                String indexName = hits.get(i).getIndex();
                // 返回索引代码
                if (!ObjectUtils.isEmpty(indexCode)) {
                    Map<String, Object> indexInfo = (Map<String, Object>) indexCode.get(indexName);
                    if (!ObjectUtils.isEmpty(indexInfo)) {
                        if (ObjectUtils.isEmpty(sourceMap.get("type"))) {
                            sourceMap.put("type", indexInfo.get("code"));
                        }
                        if (ObjectUtils.isEmpty(sourceMap.get("typeSort"))) {
                            sourceMap.put("typeSort", indexInfo.get("sort"));
                        }
                        if (!ObjectUtils.isEmpty(indexInfo.get("active")) && ObjectUtils.isEmpty(sourceMap.get("active"))) {
                            sourceMap.put("active", indexInfo.get("active"));
                        }
                    }
                }
                // 统一数据
                processingData(sourceMap);
                dataList.add(sourceMap);
            }
            if (!ObjectUtils.isEmpty(((LinkedHashMap)esMap.getSearchHits().getTotal()).get("value"))) {
                totalSize = Long.valueOf(((LinkedHashMap)esMap.getSearchHits().getTotal()).get("value").toString());
                if (totalSize > ES_RETURN_TOTALCOUNT) {
                    totalSize = ES_RETURN_TOTALCOUNT;
                }
            }
            reposneMap.put("data", dataList);
            reposneMap.put("pageSize", pageSize);
            reposneMap.put("pageNum", pageNum);
            reposneMap.put("totalCount", totalSize);
        } catch (Exception e) {
            throw new GlobalErrorException("999989", "查询失败");
        }
        return reposneMap;
    }

    private void processingData(Map<String, Object> sourceMap) throws Exception {
        // 时间字符串转时间戳
        if (!ObjectUtils.isEmpty(sourceMap.get("publishTime"))) {
            if (!isInteger(sourceMap.get("publishTime").toString())) {
                long timeStamp = timeStr2Timestamp(sourceMap.get("publishTime").toString());
                sourceMap.put("publishTime", timeStamp);
            }
        }
        if (!ObjectUtils.isEmpty(sourceMap.get("ctime"))) {
            if (!isInteger(sourceMap.get("ctime").toString())) {
                long timeStamp = timeStr2Timestamp(sourceMap.get("ctime").toString());
                sourceMap.put("publishTime", timeStamp);
            } else {
                sourceMap.put("publishTime", sourceMap.get("ctime"));
            }
            sourceMap.remove("ctime");
        }
        if (!ObjectUtils.isEmpty(sourceMap.get("uuid"))) {
            sourceMap.put("id", sourceMap.get("uuid"));
        }
        if (!ObjectUtils.isEmpty(sourceMap.get("documentId"))) {
            sourceMap.put("id", sourceMap.get("documentId"));
        }
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
        throw new GlobalErrorException("999989", "索引无效");
    }

    /**
     * 别名是否存在
     * @param indexName
     * @param alias
     * @return
     */
    public boolean existAlias(String indexName, String alias, String DSL) {
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


    public boolean addFieldAlias(String xmlName, String indexName, String DSL) {
        ClientInterface clientUtil = bbossESStarter.getConfigRestClient("esmapper/" + xmlName + ".xml");
        Boolean exist = clientUtil.existIndice(indexName);
        if (exist) {
            String aliasStr = clientUtil.executeHttp("/"+indexName+"/_mapping", DSL, "post");
            JSONObject codeNameJson = JSONObject.parseObject(aliasStr);
            return Boolean.valueOf(codeNameJson.get("acknowledged").toString());
        }
        return false;
    }



    /**
     * 字段别名是否存在
     * @param index
     * @param fieldAlias
     * @return
     */
    public boolean existFieldAlias(String index, String fieldAlias) {
        ClientInterface clientUtil = bbossESStarter.getRestClient();
        Boolean exist = clientUtil.existIndice(index);
        if (exist) {
            JSONObject retJson = JSONObject.parseObject(clientUtil.getIndexMapping(index));
            if (!ObjectUtils.isEmpty(retJson.get(index))) {
                Map<String, Object> indexMap = (Map<String, Object>) retJson.get(index);
                if ( !ObjectUtils.isEmpty(indexMap.get("mappings"))) {
                    Map<String, Object> indexMapping = (Map<String, Object>) indexMap.get("mappings");
                    if (!ObjectUtils.isEmpty(indexMapping.get("properties"))) {
                        Map<String, Object> propertiesMap = (Map<String, Object>) indexMapping.get("properties");
                        if (!ObjectUtils.isEmpty(propertiesMap.get(fieldAlias))) {
//                            Map<String, Object> typeMap = (Map<String, Object>) propertiesMap.get(fieldAlias);
//                            if ("alias".equals(typeMap.get("type"))){
//                                return true;
//                            }
                            return true;
                        } else {
                            return false;
                        }

                    }
                }
            }
            throw new GlobalErrorException("999989", "数据有误");
        } else {
            throw new GlobalErrorException("999989", "索引无效");
        }
    }

    /**
     * 判断是否为整数
     * @param str
     * @return 整数返回true
     */
    public static boolean isInteger(String str) {
        String reg = "^[-\\+]?[\\d]*$";
        Pattern pattern = Pattern.compile(reg);
        return pattern.matcher(str).matches();
    }

    /**
     * 字符串时间转时间戳
     * @param timeStr
     * @return
     * @throws Exception
     */
    private long timeStr2Timestamp(String timeStr) throws Exception {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = simpleDateFormat.parse(timeStr);
        return date.getTime();
    }


}
