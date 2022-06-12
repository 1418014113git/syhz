/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.nmghr.hander.query;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractQueryHandler;

/**
 * <功能描述/>
 *
 * @author wujin
 * @date 2018年10月10日 下午2:21:09
 * @version 1.0
 */
@Service("configQueryHandler")
public class ConfigQueryHandler extends AbstractQueryHandler {

  public ConfigQueryHandler(IBaseService baseService) {
    super(baseService);
  }

  @Override
  public Object list(Map<String, Object> requestBody) throws Exception {
    Object condition = requestBody.get("condition");
    if (condition.equals("aj")) {
      return ajconfig(requestBody);
    }else if(condition.equals("xyr")) {
      return xyrconfig(requestBody);
    }else if (condition.equals("qbxs")) {
      return qbxsconfig(requestBody);
    }else if (condition.equals("ajgldw")) {
      return ajgldwconfig(requestBody);
    }
    return null;
  }

  @SuppressWarnings("unchecked")
  public List<Map<String, Object>> ajconfig(Map<String, Object> requestBody) throws Exception {
    requestBody.put("buinessType", "1");
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "CONFIGCX");// 案件配置查询
    Object ajobj = baseService.list(requestBody);
    List<Map<String, Object>> ajlist = (List<Map<String, Object>>) ajobj;
    if (ajlist.size() == 0) {
      Object type=requestBody.get("type");
      if(type.equals("1")) {
      Map<String, Object> map = new HashMap<>();
      map.put("column_name", "BARXM,BARBM,BARCSRQ,BARXB_NAME,BARZJHM,BARDW,BARLXFS,BARHJSZDSSXQ_NAME,BARSJJZDSSXQ_NAME,"
          + "AJXZ_NAME,AJSX_NAME,BASJ,SLDW_NAME,SLSJ,ZBDW_NAME,YSDW_NAME,YSSJ,LADW_NAME,LARQ,PARQ,PADW_NAME,JARQ,"
          + "GDDW_NAME,GDRQ,GDSPDW_NAME,SSZRQ_NAME,CZDW_NAME,XZ_DCQKTBDW_NAME,SAJZ");
      map.put("user_id", requestBody.get("userid"));
      map.put("config", 0);
      ajlist.add(map);
      }else if(type.equals("2")) {
        Map<String, Object> map = new HashMap<>();
        map.put("column_name", "AJBH,AJZT,AJMC,AJLB_NAME,LARQ,SYH_FLLB,RLDW");
        map.put("user_id", requestBody.get("userid"));
        ajlist.add(map);
        map.put("config", 0);
      }
    }
    return ajlist;
  }
  
  @SuppressWarnings("unchecked")
  public List<Map<String, Object>> xyrconfig(Map<String, Object> requestBody) throws Exception {
    requestBody.put("buinessType", "2");
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "CONFIGCX");//嫌疑人配置查询
    Object xyrobj = baseService.list(requestBody);
    List<Map<String, Object>> xyrlist = (List<Map<String, Object>>) xyrobj;
    if (xyrlist.size() == 0) {
      Object type=requestBody.get("type");
      if(type.equals("1")) {
      Map<String, Object> map = new HashMap<>();
      map.put("column_name", "WHCD,HYZK");
      map.put("user_id", requestBody.get("userid"));
      map.put("config", 0);
      xyrlist.add(map);
      }else if(type.equals("2")) {
        Map<String, Object> map = new HashMap<>();
        map.put("column_name", "XYRBH,AJBH,GMSFHM,XM,CSRQ,MZ,XZQH");
        map.put("user_id", requestBody.get("userid"));
        map.put("config", 0);
        xyrlist.add(map);
      }
      
    }
    return xyrlist;
  }
  
  @SuppressWarnings("unchecked")
  public List<Map<String, Object>> qbxsconfig(Map<String, Object> requestBody) throws Exception {
    requestBody.put("buinessType", "4");
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "CONFIGCX");//情报线索配置查询
    Object obj = baseService.list(requestBody);
    List<Map<String, Object>> list = (List<Map<String, Object>>) obj;
    if (list.size() == 0) {
      Object type=requestBody.get("type");
      if(type.equals("1")) {
      Map<String, Object> map = new HashMap<>();
      map.put("column_name", "GJC,XSPJ,CJR_NAME,ASJFSDSSSS_NAME");
      map.put("user_id", requestBody.get("userid"));
      map.put("config", 0);
      list.add(map);
      }else if(type.equals("2")) {
        Map<String, Object> map = new HashMap<>();
        map.put("column_name", "BT,XXZW,SSLB_NAME,ASSJ,ASDDLB_NAME,XSZT_NAME,XSLX");
        map.put("user_id", requestBody.get("userid"));
        map.put("config", 0);
        list.add(map);
      }
    }
    return list;
  }
  
  @SuppressWarnings("unchecked")
  public List<Map<String, Object>> ajgldwconfig(Map<String, Object> requestBody) throws Exception {
    requestBody.put("buinessType", "3");
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "CONFIGCX");//案件关联单位配置查询
    Object obj = baseService.list(requestBody);
    List<Map<String, Object>> list = (List<Map<String, Object>>) obj;
    if (list.size() == 0) {
      Object type=requestBody.get("type");
      if(type.equals("1")) {
      Map<String, Object> map = new HashMap<>();
      map.put("column_name", "DWDH,SWDJH,FRDBGMSFHM,FRDBXM,WFFZSS,CFRQ,CFLX_NAME,CFQK,CFDW_NAME,BAR_NAME,WFZJ,"
          + "ZJZRR,ZGRY,JYSM,DJDW_NAME,DJR_NAME,DJRQ,CZDW_NAME,CZR_NAME,CZSJ,SJSSJE,SAJE,WHSSJE,ZXFS_NAME");
      map.put("user_id", requestBody.get("userid"));
      map.put("config", 0);
      list.add(map);
      }else if(type.equals("2")) {
        Map<String, Object> map = new HashMap<>();
        map.put("column_name", "AJBH,DWMC,DWXZ_NAME,DWLX_NAME,XYLB_NAME,FRDBGMSFHM,FRDBXM,AJMC");
        map.put("user_id", requestBody.get("userid"));
        map.put("config", 0);
        list.add(map);
      }
    }
    return list;
  }

}
