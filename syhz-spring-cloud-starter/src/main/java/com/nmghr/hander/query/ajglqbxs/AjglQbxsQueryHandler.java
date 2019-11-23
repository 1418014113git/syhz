package com.nmghr.hander.query.ajglqbxs;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractQueryHandler;
import org.springframework.stereotype.Service;

import java.util.*;

@SuppressWarnings("unchecked")
@Service("ajglQbxsQueryHandler")
public class AjglQbxsQueryHandler extends AbstractQueryHandler {
  public AjglQbxsQueryHandler(IBaseService baseService) {
    super(baseService);
  }

  @Override
  public Object list(Map<String, Object> requestMap) throws Exception {
    Map<String, Object> result = new HashMap<>();
    Map<String, Object> params = new HashMap<String, Object>();
    params.put("assistId", requestMap.get("assistId"));
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJGLQBXSBYASSIST");
    List<Map<String, Object>> titles = (List<Map<String, Object>>) baseService.list(params);
    if (titles == null || titles.size() == 0) {
      return new ArrayList();
    }
    List<Object> heads = new ArrayList<>();
    for (Map<String, Object> map : titles) {
      heads.add(Integer.parseInt(String.valueOf(map.get("qbxsIndex"))), map.get("title"));
    }
    result.put("titles", heads);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJGLQBXSINFOBYASSIST");
    List<Map<String, Object>> values = (List<Map<String, Object>>) baseService.list(params);
    if (values == null || values.size() == 0) {
      return result;
    }
    Map<String, Object> valMap = new LinkedHashMap<>();
    for (Map<String, Object> map : values) {
      if (valMap.containsKey(String.valueOf(map.get("qbxsIdx")))) {
        Map<String, Object> valData = (Map<String, Object>) valMap.get(String.valueOf(map.get("qbxsIdx")));
        List<Object> vals = (List<Object>) valData.get("data");
        vals.add(Integer.parseInt(String.valueOf(map.get("rowIndex"))), map.get("value"));
        valMap.put(String.valueOf(map.get("qbxsIdx")), valData);
      } else {
        List<Object> vals = new ArrayList<>();
        vals.add(Integer.parseInt(String.valueOf(map.get("rowIndex"))), map.get("value"));
        Map<String, Object> valData = new HashMap<String, Object>();
        valData.put("qbxsIdx", map.get("qbxsIdx"));
        valData.put("qbxsCategory", map.get("qbxsCategory"));
        valData.put("data", vals);
        valMap.put(String.valueOf(map.get("qbxsIdx")), valData);
      }
    }
    result.put("list", valMap.values());
    return result;
  }
}
