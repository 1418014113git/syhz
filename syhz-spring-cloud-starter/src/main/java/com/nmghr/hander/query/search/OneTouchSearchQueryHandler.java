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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

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

    @Value("${elasticsearch.hostNames}")
    private String ES_HOSTNAME;

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

    private static final String CODE_ENABLED_YES = "1";

    @Override
    public Object page(Map<String, Object> requestMap, int currentPage, int pageSize) throws Exception {
        Map<String, Object> retMap = null;
        // 从码表取出资源类型code和排序
        Map<Integer, Integer> resource = getResourceType("yjss");
        int resourceType = Integer.valueOf(requestMap.get("resourceType").toString());

        switch (resourceType) {
            case RESOURCE_TYPE_ALL:
                break;
            case RESOURCE_TYPE_KNOWLEDGE:
                // 设置别名
                if (esService.addAlias("caseinfo", "onetouchsearchknowledge")
                        && esService.addAlias("lawinfo", "onetouchsearchknowledge")
                        && esService.addAlias("industryinfo", "onetouchsearchknowledge")
                        && esService.addAlias("standardinfo", "onetouchsearchknowledge")) {
                    retMap = oneTouchSearchEs("onetouchsearch", "onetouchsearchknowledge", requestMap, "oneTouchSearch");
                } else {
                    throw new GlobalErrorException("999989", "设置别名失败");
                }
                break;
            case RESOURCE_TYPE_TRAIN:
                retMap = oneTouchSearchEs("onetouchsearch", "traincourse", requestMap, "oneTouchSearch");
                break;
            case RESOURCE_TYPE_CASE:
                retMap = oneTouchSearchEs("onetouchsearch", "case", requestMap, "oneTouchSearch");
                break;
            case RESOURCE_TYPE_CLUE:
                retMap = oneTouchSearchEs("onetouchsearch", "clue", requestMap, "oneTouchSearch");
                break;
            case RESOURCE_TYPE_PUBLIC_OPINION:
                retMap = oneTouchSearchEs("onetouchsearch", "hsyz", requestMap, "oneTouchSearchHsyz");
                break;
            case RESOURCE_TYPE_INTERNAL_RES:
                retMap = oneTouchSearchEs("onetouchsearch", "reptile", requestMap, "oneTouchSearch");
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
     * 根据currentPage，pageSize信息获取分页数据
     * @param list
     * @param currentPage
     * @param pageSize
     * @return
     * @throws Exception
     */
    private List<Map<String, Object>> getPageList(List<Map<String, Object>> list, int currentPage, int pageSize) throws Exception {
        int startIndex = currentPage > 1 ? ((currentPage - 1) * pageSize) : 0;
        int endIndex = currentPage == 0 ? pageSize : currentPage * pageSize;
        List<Map<String, Object>> retNewList = null;
        if (list.size() < endIndex) {
            retNewList = list.subList(startIndex, list.size());
        } else {
            retNewList = list.subList(startIndex, endIndex);
        }
        return retNewList;
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
    private Map<String, Object> oneTouchSearchEs(String xmlName, String index, Map<String, Object> map, String DSL) throws Exception {
        try {
            Map<String, Object> esCaseInfoMap = esService.query(xmlName, index, map, DSL);
            List<Map<String, Object>> dataList = (List<Map<String, Object>>) esCaseInfoMap.get("data");
            for (int i = 0; i < dataList.size(); i++) {
                Map<String, Object> item = dataList.get(i);
                // 时间字符串转时间戳
                if (!ObjectUtils.isEmpty(item.get("publishTime"))) {
                    if (!isInteger(item.get("publishTime").toString())) {
                        long timeStamp = timeStr2Timestamp(item.get("publishTime").toString());
                        item.put("publishTime", timeStamp);
                    }
                }
                if (!ObjectUtils.isEmpty(item.get("ctime"))) {
                    if (!isInteger(item.get("ctime").toString())) {
                        long timeStamp = timeStr2Timestamp(item.get("ctime").toString());
                        item.put("publishTime", timeStamp);
                    } else {
                        item.put("publishTime", item.get("ctime"));
                    }
                    item.remove("ctime");
                }
            }
            Map<String, Object> resultMap = new HashMap<String, Object>(2);
            resultMap.put("list", esCaseInfoMap.get("data"));
            resultMap.put("totalCount", esCaseInfoMap.get("totalCount"));
            return resultMap;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 查询资源分类
     * @param codeLx
     * @return
     * @throws Exception
     */
    private Map<Integer, Integer> getResourceType(String codeLx) throws Exception {
        Map<String, Object> searchMap = new HashMap<>(4);
        searchMap.put("codeLx", codeLx);
        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "TCODE");
        List<Map<String, Object>> retList = (List<Map<String, Object>>) baseService.list(searchMap);
        if (ObjectUtils.isEmpty(retList)) {
            throw new GlobalErrorException("999989", "查询资源类型失败");
        }
        Map<Integer, Integer> retMap = new HashMap<>(16);
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
                retMap.put(code, sort);
            }
        }
        return retMap;
    }


    /**
     * 构造ES请求路径
     * @param resourceType
     * @return
     * @throws Exception
     */
    private String getRequestUrlES(int resourceType) throws Exception {
        String esPostUrl = null;
        String esIndexs = null;
        switch (resourceType) {
            case RESOURCE_TYPE_ALL:
                esIndexs = ES_INDEX_KNOWLEDGE_CASEINFO + ","
                        + ES_INDEX_KNOWLEDGE_LAWINFO + ","
                        + ES_INDEX_KNOWLEDGE_INDUSTRYINFO + ","
                        + ES_INDEX_KNOWLEDGE_STANDARDINFO + ","
                        + ES_INDEX_TRAIN + ","
                        + ES_INDEX_CASE + ","
                        + ES_INDEX_CLUE + ","
                        + ES_INDEX_PUBLIC_OPINION + ","
                        + ES_INDEX_INTERNAL_RES + ","
                        + ES_INDEX_EXTERNAL_RES;
                break;
            case RESOURCE_TYPE_KNOWLEDGE:
                esIndexs = ES_INDEX_KNOWLEDGE_CASEINFO + "," + ES_INDEX_KNOWLEDGE_LAWINFO + "," + ES_INDEX_KNOWLEDGE_INDUSTRYINFO + "," + ES_INDEX_KNOWLEDGE_STANDARDINFO;
                break;
            case RESOURCE_TYPE_TRAIN:
                esIndexs = ES_INDEX_TRAIN;
                break;
            case RESOURCE_TYPE_CASE:
                esIndexs = ES_INDEX_CASE;
                break;
            case RESOURCE_TYPE_CLUE:
                esIndexs = ES_INDEX_CLUE;
                break;
            case RESOURCE_TYPE_PUBLIC_OPINION:
                esIndexs = ES_INDEX_PUBLIC_OPINION;
                break;
            case RESOURCE_TYPE_INTERNAL_RES:
                esIndexs = ES_INDEX_INTERNAL_RES;
                break;
            case RESOURCE_TYPE_EXTERNAL_RES:
                esIndexs = ES_INDEX_EXTERNAL_RES;
                break;
            default:
                return null;
        }
        esPostUrl ="http://" + ES_HOSTNAME + "/" + esIndexs + "/_search";
        return esPostUrl;
    }


    private JSONObject getRequestBodyES(Object search, int from, int size) throws Exception {
        Map<String, Object> requestBodyMap = new HashMap<>(8);
        Map<String, Object> queryMap = new HashMap<>(8);
        Map<String, Object> multiMatchMap = new HashMap<>(8);
        List<String> fieldsList = new ArrayList<String>();
        fieldsList.add("content");
        fieldsList.add("title");
        fieldsList.add("attachment");
        multiMatchMap.put("query", search);
        multiMatchMap.put("fields", fieldsList);
        queryMap.put("multi_match", multiMatchMap);
        Map<String, Object> sortMap = new HashMap<>(8);
        List<Map<String, Object>> sortList = new ArrayList<Map<String, Object>>();
        Map<String, Object> publishTimeMap = new HashMap<>(8);
        Map<String, Object> orderMap = new HashMap<>(8);
        orderMap.put("order", "desc");
        publishTimeMap.put("publishTime", orderMap);
        sortList.add(publishTimeMap);
        requestBodyMap.put("query", queryMap);
        requestBodyMap.put("sort", sortList);
        requestBodyMap.put("from", from);
        requestBodyMap.put("size", size);
        return new JSONObject(requestBodyMap);
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
