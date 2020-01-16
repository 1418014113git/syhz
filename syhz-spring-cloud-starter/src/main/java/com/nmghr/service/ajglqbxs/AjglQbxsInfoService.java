package com.nmghr.service.ajglqbxs;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.hander.save.common.BatchSaveHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@SuppressWarnings("unchecked")
@Service("ajglQbxsInfoService")
public class AjglQbxsInfoService {
  @Autowired
  private IBaseService baseService;
  @Autowired
  private BatchSaveHandler batchSaveHandler;
  /**
   * 更新线索信息
   *
   * @param body
   * @return
   * @throws Exception
   */
  @Transactional
  public Object modifyQbxsInfo(Map<String, Object> body) throws Exception {
    Object category = body.get("category");
    Object creator = body.get("creator");
    Object assistId = body.get("id");
    Map<String, Object> updAddrMap = (Map<String, Object>) body.get("updAddrMap");
    Map<String, Object> idxMap = (Map<String, Object>) body.get("idxMap");
    List<LinkedHashMap<String, Object>> updDatas = (List<LinkedHashMap<String, Object>>) body.get("updDatas");

    //批量删除xsinfo
    List<Object> qbxsIds = (List<Object>) body.get("updQxIds");
    if(qbxsIds!=null && qbxsIds.size()>0){
      Map<String, Object> bhP = new HashMap<>();
      bhP.put("qbxsIds", qbxsIds);
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJGLQBXSINFODEL");
      baseService.remove(bhP);
      System.out.println("删除线索："+ qbxsIds.toString());
    }
    if(updDatas!=null && updDatas.size()>0){
      //增加info数据
      List<Map<String, Object>> paramValues = new ArrayList<>(updDatas.size());
      int size = updDatas.size();
      for (int i = 0; i < size; i++) {
        Map<String, Object> values = updDatas.get(i);
        Object qbxsId = values.get("qbxsId");
        values.remove("qbxsId");
        for (String key: values.keySet()) {
          String str = String.valueOf(values.get(key));
          Map<String, Object> p = new HashMap<>();
          p.put("assistId", assistId);
          p.put("qbxsId", qbxsId);
          p.put("rowIndex", idxMap.get(key));
          p.put("columnIndex", i);
          p.put("value", str);
          p.put("qbxsCategory", category);
          p.put("creator", creator);
          paramValues.add(p);
        }
      }
      System.out.println("新增线索："+ paramValues.size());
      Map<String, Object> params2 = new HashMap<>();
      params2.put("list", paramValues);
      params2.put("alias", "AJGLQBXSINFOBATCH");
      params2.put("seqName", "AJGLQBXSINFO");
      params2.put("subSize", 50);
      batchSaveHandler.save(params2);

      Map<String, Object> bean = new HashMap<>();
      bean.put("category", category);
      bean.put("ids", qbxsIds);
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJGLQBXSBASEBATCHUPDATE");
      baseService.update("", bean);
    }

    if(updAddrMap!=null && updAddrMap.size()>0){
      //循环修改base 表地址。
      for (String key : updAddrMap.keySet()) {
        Map<String, Object> p = new HashMap<>();
        p.put("address", updAddrMap.get(key));
        p.put("category", category);
        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJGLQBXSBASEUPD");
        baseService.update(key, p);
        System.out.println("修改线索address："+ updAddrMap.get(key));
      }
    }
    return "ok";
  }
}
