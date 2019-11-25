package com.nmghr.hander.save.clue;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractSaveHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.*;

/**
 * 线索分享
 */
@Service("clueShareHandler")
public class ClueShareHandler extends AbstractSaveHandler {

    public ClueShareHandler(IBaseService baseService) {
        super(baseService);
    }

    @Override
    @Transactional
    public Object save(Map<String, Object> body) throws Exception {
        List<Map<String, Object>> listDept = (List<Map<String, Object>>)body.get("receiveDeptId");
        List<Object> listPson = (List<Object>) body.get("receivePersonId");

        List<String> deptIds = new ArrayList<>();   // 要分享到人员的单位
        List<String> psonIds = new ArrayList<>();   // 接收人员ids
        if(!ObjectUtils.isEmpty(listPson)) {
            for (Object obj : listPson) {
                psonIds.add(obj.toString().split(",")[0]);
                if(!deptIds.contains(obj.toString().split(",")[2])) {
                    deptIds.add(obj.toString().split(",")[2]);
                }
            }
        }
        // 分享校验
        if(!ObjectUtils.isEmpty(listDept)){
            Map<String, Object> p = new HashMap<>();
            for(Map<String, Object> map : listDept){
                p.put("receiveDeptId", map.get("id"));
                p.put("clueId", body.get("clueId"));
                LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "QBXXCLUESHAREDEPT");
                List<Map<String, Object>> objList = (List<Map<String, Object>>)baseService.list(p);
                if(!ObjectUtils.isEmpty(objList) && objList.size() > 0){    // 分享单位表有数据
                    for(Map<String, Object> _map : objList){
                        p.put("shareDepartmentId", _map.get("id"));
                        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "QBXXCLUESHAREPSON");
                        List psonList = (List)baseService.list(p);
                        if(!ObjectUtils.isEmpty(psonList) && psonList.size() > 0){    // 分享人员表有数据
                            if(deptIds.contains(String.valueOf(map.get("id")))){    // 目前要分享到人，不允许重复分享
                                for(Object obj : psonList){
                                    Map<String, Object> _mapPson = (Map<String, Object>)obj;
                                    if(psonIds.contains(String.valueOf(_mapPson.get("receivePersonId")))){ // 重复分享到人
                                        Map<String, Object> result = new HashMap<>();
                                        result.put("msgId",1);
                                        result.put("msg", "线索已分享给\"" +_mapPson.get("receivePersonName") + "\"不能重复分享");
                                        return result;
                                    }
                                }
                            }else{  // 目前要分享到单位，是否撤回人员记录
                                Map<String, Object> result = new HashMap<>();
                                result.put("msgId",2);
                                result.put("msg","线索已分享给单位\"" + _map.get("receiveDeptName") + "\"中的某些人，是否撤回对此单位中人员的分享？");
                                result.put("deptId", _map.get("receiveDeptId"));
                                result.put("clueId", body.get("clueId"));
                                return result;
                            }
                        }else{    // 分享人员表无数据，线索只分享给单位的
                            if(deptIds.contains(String.valueOf(map.get("id")))){    // 目前要分享到人，是否撤回单位记录
                                Map<String, Object> result = new HashMap<>();
                                result.put("msgId",3);
                                result.put("msg","线索已分享给单位\"" + _map.get("receiveDeptName") + "\"，是否撤回对此单位的分享？");
                                result.put("deptId", _map.get("receiveDeptId"));
                                result.put("clueId", body.get("clueId"));
                                return result;
                            }else{  // 目前要分享到单位，但是单位分享已有，不允许重复分享
                                Map<String, Object> result = new HashMap<>();
                                result.put("msgId",4);
                                result.put("msg","线索已分享给单位\"" + _map.get("receiveDeptName") + "\"，不能重复分享");
                                return result;
                            }
                        }
                    }
                }
            }
        }
        // 校验通过添加记录
        body.put("shareTime", new Date());  // 提交时间
        if(!ObjectUtils.isEmpty(listDept)){
            for(Map<String, Object> map : listDept){
                body.put("receiveDeptId", map.get("id"));
                body.put("receiveDeptName", map.get("name"));
                LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "QBXXCLUESHAREDEPT");
                Object objId = baseService.save(body);
                if(!ObjectUtils.isEmpty(listPson)){
                    for(Object obj : (List)listPson){
                        if(obj.toString().split(",")[2].equals(map.get("id").toString())){
                            body.put("receivePersonId", obj.toString().split(",")[0]);
                            body.put("receivePersonName", obj.toString().split(",")[1]);
                            body.put("shareDepartmentId", objId);
                            LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "QBXXCLUESHAREPSON");
                            baseService.save(body);
                        }
                    }
                }
            }
        }
        // 分享完修改分享状态
        Map<String, Object> p = new HashMap<>();
        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "QBXXCLUESHARESTATUS");
        p.put("clueId", body.get("clueId"));
        p.put("shareStatus", 0);
        baseService.update(String.valueOf(body.get("clueId")), p);
        return true;
    }
    // 分享校验
        /*if(!ObjectUtils.isEmpty(listDept)){
        Map<String, Object> p = new HashMap<>();
        for(Map<String, Object> map : listDept){
            p.put("receiveDeptId", map.get("id"));
            p.put("clueId", body.get("clueId"));
            LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "QBXXCLUESHAREDEPT");
            List objList = (List)baseService.list(p);
            if(!ObjectUtils.isEmpty(objList) && objList.size() > 0){    // 分享单位表有数据
                Map<String, Object> _map = (Map<String, Object>) objList.get(0);
                p.put("shareDepartmentId", _map.get("id"));
                LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "QBXXCLUESHAREPSON");
                List psonList = (List)baseService.list(p);
                if(!ObjectUtils.isEmpty(psonList) && psonList.size() > 0){    // 分享人员表有数据
                    if(deptIds.contains(String.valueOf(map.get("id")))){    // 目前要分享到人，不允许重复分享
                        for(Object obj : psonList){
                            Map<String, Object> _mapPson = (Map<String, Object>)obj;
                            if(psonIds.contains(String.valueOf(_mapPson.get("receivePersonId")))){ // 重复分享到人
                                Map<String, Object> result = new HashMap<>();
                                result.put("msgId",1);
                                result.put("msg", "线索已分享给\"" +_mapPson.get("receivePersonName") + "\"不能重复分享");
                                return result;
                            }
                        }
                    }else{  // 目前要分享到单位，是否撤回人员记录
                        Map<String, Object> result = new HashMap<>();
                        result.put("msgId",2);
                        result.put("msg","线索已分享给单位\"" + _map.get("receiveDeptName") + "\"中的某些人，是否撤回对此单位中人员的分享？");
                        result.put("deptId", _map.get("receiveDeptId"));
                        result.put("clueId", body.get("clueId"));
                        return result;
                    }
                }else{    // 分享人员表无数据，线索只分享给单位的
                    if(deptIds.contains(String.valueOf(map.get("id")))){    // 目前要分享到人，是否撤回单位记录
                        Map<String, Object> result = new HashMap<>();
                        result.put("msgId",3);
                        result.put("msg","线索已分享给单位\"" + _map.get("receiveDeptName") + "\"，是否撤回对此单位的分享？");
                        result.put("deptId", _map.get("receiveDeptId"));
                        result.put("clueId", body.get("clueId"));
                        return result;
                    }else{  // 目前要分享到单位，但是单位分享已有，不允许重复分享
                        Map<String, Object> result = new HashMap<>();
                        result.put("msgId",4);
                        result.put("msg","线索已分享给单位\"" + _map.get("receiveDeptName") + "\"，不能重复分享");
                        return result;
                    }
                }
            }
        }
    }*/
}
