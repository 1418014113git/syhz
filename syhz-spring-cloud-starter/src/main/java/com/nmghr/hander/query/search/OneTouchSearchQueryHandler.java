/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.nmghr.hander.query.search;

import com.alibaba.fastjson.JSONObject;
import com.nmghr.basic.common.Constant;
import com.nmghr.basic.common.Result;
import com.nmghr.basic.common.exception.GlobalErrorException;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.page.Paging;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractQueryHandler;
import com.nmghr.service.EsOneTouchSearchService;
import com.nmghr.util.ListSortUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.*;

/**
 * 一键搜索
 *
 * @author mxd.
 * @date 2020-01-14 - 14:57.
 */
@Service(value = "oneTouchSearchQueryHandler")
public class OneTouchSearchQueryHandler extends AbstractQueryHandler{
    public OneTouchSearchQueryHandler(IBaseService baseService) {
        super(baseService);
    }

    @Autowired
    private EsOneTouchSearchService esService;

    /** ES使用的XML文件名 */
    private static final String ES_ONE_TOUCH_SEARCH_XML_NAME = "onetouchsearch";

    /** 全部 */
    private static final int RESOURCE_TYPE_ALL = 0;
    /** 知识库 */
    private static final int RESOURCE_TYPE_KNOWLEDGE = 1;
    private static final String ES_INDEX_KNOWLEDGE_CASEINFO = "caseinfo";
    private static final String ES_INDEX_KNOWLEDGE_LAWINFO = "lawinfo";
    private static final String ES_INDEX_KNOWLEDGE_INDUSTRYINFO = "industryinfo";
    private static final String ES_INDEX_KNOWLEDGE_STANDARDINFO = "standardinfo";
    /** 培训资料 */
    private static final int RESOURCE_TYPE_TRAIN = 2;
    private static final String ES_INDEX_TRAIN = "traincourse";
    /** 案件 */
    private static final int RESOURCE_TYPE_CASE = 3;
    private static final String ES_INDEX_CASE = "case";
    /** 线索 */
    private static final int RESOURCE_TYPE_CLUE = 4;
    private static final String ES_INDEX_CLUE = "clue";
    /** 舆情 */
    private static final int RESOURCE_TYPE_PUBLIC_OPINION = 5;
    private static final String ES_INDEX_PUBLIC_OPINION = "hsyz";
    /** 内部资源 */
    private static final int RESOURCE_TYPE_INTERNAL_RES = 6;
    private static final String ES_INDEX_INTERNAL_RES = "reptile";
    /** 外部资源 */
    private static final int RESOURCE_TYPE_EXTERNAL_RES = 7;
    private static final String ES_INDEX_EXTERNAL_RES = "";

    /** 知识库索引（别名） */
    private static final String ES_INDEX_KNOWLEDGE_ALIAS = "onetouchsearchknowledge";
    /** 全部（别名） */
    private static final String ES_INDEX_ALL = "onetouchsearchall";

    private static final String CODE_ENABLED_YES = "1";

    @Override
    public Object page(Map<String, Object> requestMap, int currentPage, int pageSize) throws Exception {
        Map<String, Object> retMap = null;
        // 从码表取出资源类型code和排序
        Map<String, Object> resource = getResourceType("yjss");
        int resourceType = Integer.valueOf(requestMap.get("resourceType").toString());
        switch (resourceType) {
            case RESOURCE_TYPE_ALL:
                // 给hsyz ctime字段添加别名
                if (!esService.existFieldAlias("hsyz", "publishTime")) {
                    if (!esService.addFieldAlias(ES_ONE_TOUCH_SEARCH_XML_NAME, "hsyz", "setHsyzFieldAlias")) {
                        return Result.fail("999989", "设置字段别名失败");
                    }
                }
                if (esService.addAlias(ES_INDEX_KNOWLEDGE_CASEINFO, ES_INDEX_ALL)
                        && esService.addAlias(ES_INDEX_KNOWLEDGE_LAWINFO, ES_INDEX_ALL)
                        && esService.addAlias(ES_INDEX_KNOWLEDGE_INDUSTRYINFO, ES_INDEX_ALL)
                        && esService.addAlias(ES_INDEX_KNOWLEDGE_STANDARDINFO, ES_INDEX_ALL)
                        && esService.addAlias(ES_INDEX_TRAIN, ES_INDEX_ALL)
                        && esService.addAlias(ES_INDEX_CASE, ES_INDEX_ALL)
                        && esService.addAlias(ES_INDEX_CLUE, ES_INDEX_ALL)
                        && esService.addAlias(ES_INDEX_PUBLIC_OPINION, ES_INDEX_ALL)
                        && esService.addAlias(ES_INDEX_INTERNAL_RES, ES_INDEX_ALL)
                        ) {
                    retMap = oneTouchSearchEs(ES_ONE_TOUCH_SEARCH_XML_NAME, ES_INDEX_ALL, requestMap, "oneTouchSearch", resource);
                    List<Map<String, Object>> data = (List<Map<String, Object>>) retMap.get("list");
                    // 相同时间情况下根据资源类型排序
                    List<Map<String, Object>> newData = resultSortByType(data);
                    retMap.put("list", newData);
                }
                break;
            case RESOURCE_TYPE_KNOWLEDGE:
                // 设置别名
                if (esService.addAlias(ES_INDEX_KNOWLEDGE_CASEINFO, ES_INDEX_KNOWLEDGE_ALIAS)
                        && esService.addAlias(ES_INDEX_KNOWLEDGE_LAWINFO, ES_INDEX_KNOWLEDGE_ALIAS)
                        && esService.addAlias(ES_INDEX_KNOWLEDGE_INDUSTRYINFO, ES_INDEX_KNOWLEDGE_ALIAS)
                        && esService.addAlias(ES_INDEX_KNOWLEDGE_STANDARDINFO, ES_INDEX_KNOWLEDGE_ALIAS)) {
                    retMap = oneTouchSearchEs(ES_ONE_TOUCH_SEARCH_XML_NAME, ES_INDEX_KNOWLEDGE_ALIAS, requestMap, "oneTouchSearch", resource);
                } else {
                    return Result.fail("999989", "设置别名失败");
                }
                break;
            case RESOURCE_TYPE_TRAIN:
                retMap = oneTouchSearchEs(ES_ONE_TOUCH_SEARCH_XML_NAME, ES_INDEX_TRAIN, requestMap, "oneTouchSearch", resource);
                break;
            case RESOURCE_TYPE_CASE:
                retMap = oneTouchSearchEs(ES_ONE_TOUCH_SEARCH_XML_NAME, ES_INDEX_CASE, requestMap, "oneTouchSearch", resource);
                break;
            case RESOURCE_TYPE_CLUE:
                retMap = oneTouchSearchEs(ES_ONE_TOUCH_SEARCH_XML_NAME, ES_INDEX_CLUE, requestMap, "oneTouchSearch", resource);
                break;
            case RESOURCE_TYPE_PUBLIC_OPINION:
                retMap = oneTouchSearchEs(ES_ONE_TOUCH_SEARCH_XML_NAME, ES_INDEX_PUBLIC_OPINION, requestMap, "oneTouchSearchHsyz", resource);
                break;
            case RESOURCE_TYPE_INTERNAL_RES:
                retMap = oneTouchSearchEs(ES_ONE_TOUCH_SEARCH_XML_NAME, ES_INDEX_INTERNAL_RES, requestMap, "oneTouchSearch", resource);
                break;
            case RESOURCE_TYPE_EXTERNAL_RES:
                break;
            default:
                return null;
        }

        Paging newPaging = null;
        if (!ObjectUtils.isEmpty(retMap)) {
            newPaging = new Paging(pageSize, currentPage, Long.valueOf(retMap.get("totalCount").toString()), (List) retMap.get("list"));
        } else {
            newPaging = new Paging(pageSize, currentPage, 0, null);
        }
        return Result.ok(newPaging);
    }

    /**
     * 一键搜索ES
     * @param xmlName
     * @param index
     * @param map
     * @param DSL
     * @return
     * @throws Exception
     */
    private Map<String, Object> oneTouchSearchEs(String xmlName, String index, Map<String, Object> map, String DSL, Map<String, Object> indexCode) throws Exception {
        try {
            Map<String, Object> esCaseInfoMap = esService.query(xmlName, index, map, DSL, indexCode);
            Map<String, Object> resultMap = new HashMap<String, Object>(2);
            resultMap.put("list", esCaseInfoMap.get("data"));
            resultMap.put("totalCount", esCaseInfoMap.get("totalCount"));
            return resultMap;
        } catch (Exception e) {
            throw new GlobalErrorException("999989", "查询失败");
        }
    }

    /**
     * 查询资源分类
     * @param codeLx
     * @return
     * @throws Exception
     */
    private Map<String, Object> getResourceType(String codeLx) throws Exception {
        Map<String, Object> searchMap = new HashMap<>(4);
        searchMap.put("codeLx", codeLx);
        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "TCODE");
        List<Map<String, Object>> retList = (List<Map<String, Object>>) baseService.list(searchMap);
        if (ObjectUtils.isEmpty(retList)) {
            throw new GlobalErrorException("999989", "查询资源类型失败");
        }
        Map<String, Object> retMap = new HashMap<>(16);
        for(Map<String, Object> item : retList) {
            if (ObjectUtils.isEmpty(item.get("code"))) {
                throw new GlobalErrorException("999989", "Id为" + item.get("id").toString() + "的code字段不能为空");
            }
            if (ObjectUtils.isEmpty(item.get("code_name"))) {
                throw new GlobalErrorException("999989", "码表编码名称不能为空");
            }
            if (ObjectUtils.isEmpty(item.get("code_sorted"))) {
                throw new GlobalErrorException("999989", "码表排序不能为空");
            }
            JSONObject codeNameJson = JSONObject.parseObject(item.get("code_name").toString());
            if (CODE_ENABLED_YES.equals(codeNameJson.get("enabled"))) {
                int code = Integer.valueOf(item.get("code").toString());
                int sort = Integer.valueOf(item.get("code_sorted").toString());
                switch (code) {
                    case RESOURCE_TYPE_KNOWLEDGE:
                        retMap.put(ES_INDEX_KNOWLEDGE_CASEINFO, setIndexCodeInfo(code, sort, 4));
                        retMap.put(ES_INDEX_KNOWLEDGE_LAWINFO, setIndexCodeInfo(code, sort, 1));
                        retMap.put(ES_INDEX_KNOWLEDGE_INDUSTRYINFO, setIndexCodeInfo(code, sort, 2));
                        retMap.put(ES_INDEX_KNOWLEDGE_STANDARDINFO, setIndexCodeInfo(code, sort, 3));
                        break;
                    case RESOURCE_TYPE_TRAIN:
                        retMap.put(ES_INDEX_TRAIN, setIndexCodeInfo(code, sort, -1));
                        break;
                    case RESOURCE_TYPE_CASE:
                        retMap.put(ES_INDEX_CASE, setIndexCodeInfo(code, sort, -1));
                        break;
                    case RESOURCE_TYPE_CLUE:
                        retMap.put(ES_INDEX_CLUE, setIndexCodeInfo(code, sort, -1));
                        break;
                    case RESOURCE_TYPE_PUBLIC_OPINION:
                        retMap.put(ES_INDEX_PUBLIC_OPINION, setIndexCodeInfo(code, sort, -1));
                        break;
                    case RESOURCE_TYPE_INTERNAL_RES:
                        retMap.put(ES_INDEX_INTERNAL_RES, setIndexCodeInfo(code, sort, -1));
                        break;
                    case RESOURCE_TYPE_EXTERNAL_RES:
//                        retMap.put(ES_INDEX_EXTERNAL_RES, map);
                        break;
                    default:
                        break;
                }
            }
        }
        return retMap;
    }

    private Map<String, Object> setIndexCodeInfo(int code, int sort, int active ) {
        Map<String, Object> map = new HashMap<>(4);
        map.put("code", code);
        map.put("sort", sort);
        if (active > 0) {
            map.put("active", active);
        }
        return map;
    }

    private List<Map<String, Object>> resultSortByType(List<Map<String, Object>> list) {
        ListSortUtil.resultOrder(list, "publishTime", -1);
        Map<String, List<Map<String, Object>>> map = new TreeMap<>(Comparator.reverseOrder());
        for (Map<String, Object> map1 : list) {
            String key = map1.get("publishTime").toString();
            if (map.containsKey(key)) {
                map.get(key).add(map1);
            } else {
                List<Map<String, Object>> listTemp = new ArrayList<>();
                listTemp.add(map1);
                map.put(key, listTemp);
            }
        }
        List<Map<String, Object>> newMapList = new ArrayList<>();
        for (List<Map<String, Object>> testBeans : map.values()) {
            newMapList.addAll(ListSortUtil.resultOrder(testBeans, "type", 1));
        }
        return newMapList;
    }

}
