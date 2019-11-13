package com.nmghr.hander.save.cluster;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractSaveHandler;
import com.nmghr.hander.save.common.BatchSaveHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
@Service("deptMapperSaveHandler")
public class DeptMapperSaveHandler extends AbstractSaveHandler {

  @Autowired
  private BatchSaveHandler batchSaveHandler;

  public DeptMapperSaveHandler(IBaseService baseService) {
    super(baseService);
  }

  @Override
  @Transactional
  public Object save(Map<String, Object> body) throws Exception {
    List<Map<String, Object>> list = (List<Map<String, Object>>) body.get("list");

    List<Map<String, Object>> adds = new ArrayList<>();
    for (Map<String, Object> map : list) {
      Map<String, Object> p = new HashMap<>();
      p.put("clusterId",map.get("clusterId"));
      p.put("deptCode",map.get("deptCode"));
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJCLUSTERASSISTDEPT");
      List<Map<String, Object>> rs = (List<Map<String, Object>>) baseService.list(p);
      if(rs!=null && rs.size()>0){
        Map<String, Object> bean = rs.get(0);
        p = new HashMap<>();
        p.put("clusterId",map.get("clusterId"));
        p.put("deptCode",map.get("deptCode"));
        p.put("clueCount",Integer.parseInt(String.valueOf(map.get("clueCount"))) + Integer.parseInt(String.valueOf(bean.get("clueCount"))));
        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJCLUSTERASSISTDEPT");
        baseService.update(String.valueOf(bean.get("id")), p);
      } else {
        adds.add(map);
      }
    }
    if(adds.size()>0){
      Map<String, Object> params = new HashMap<>();
      params.put("list", adds);
      params.put("alias", "AJCLUSTERASSISTDEPTBATCH");
      params.put("seqName", "AJCLUSTERASSISTDEPT");
      params.put("subSize", 50);
      batchSaveHandler.save(params);
    }

    return adds.size();
  }
}
