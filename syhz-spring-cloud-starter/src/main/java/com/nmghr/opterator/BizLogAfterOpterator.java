package com.nmghr.opterator;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpServletRequest;
import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.opt.IAfterOpterator;
import com.nmghr.basic.core.opt.OperatorContext;
import com.nmghr.basic.core.service.impl.BaseServiceImpl;
import com.nmghr.util.GetIpUtil;
import com.nmghr.util.LogQueueThread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service("bizLog")
public class BizLogAfterOpterator implements IAfterOpterator {
  private static final Logger log = LoggerFactory.getLogger(BizLogAfterOpterator.class);
  
  @Autowired
  private BaseServiceImpl baseService;

  @Override
  public Object after(OperatorContext context, Object result) {

    RequestAttributes ra = RequestContextHolder.getRequestAttributes();
    ServletRequestAttributes sra = (ServletRequestAttributes) ra;
    HttpServletRequest request = sra.getRequest();
    Map<String, Object> map = context.getBody();
    // 初始化操作日志表所需的值
    try {
      String param = context.getRequestParams().isEmpty() == true ? null : context.getRequestParams().toString();
      Map<String, Object> initmap = new HashMap<String, Object>();
      if(request.getHeader("userId")!=null) {
        initmap.put("userId", request.getHeader("userId"));
      } else if(map!=null && map.get("userId")!=null){
        initmap.put("userId", map.get("userId"));
      }
      if(request.getHeader("userName")!=null) {
        initmap.put("userName", URLDecoder.decode(request.getHeader("userName"), "UTF-8"));
      } else if(map!=null && map.get("userName")!=null){
        initmap.put("userName", URLDecoder.decode(String.valueOf(map.get("userName")), "UTF-8"));
      }
      if(request.getHeader("realName")!=null) {
        initmap.put("nickName", URLDecoder.decode(request.getHeader("realName"), "UTF-8"));
      } else if(map!=null && map.get("realName")!=null){
        initmap.put("nickName", URLDecoder.decode(String.valueOf(map.get("realName")), "UTF-8"));
      }
      initmap.put("ipAddress", GetIpUtil.getIpAddr(request));
      initmap.put("url", request.getRequestURL().toString());
      initmap.put("httpMethod", context.getCurOptConfig().getStatement());
      initmap.put("action", context.getCurOptConfig().getOptDesc());
      initmap.put("args", param);
      initmap.put("queryStatement", context.getCurOptConfig().getOptValue());
      String res = String.valueOf(result);
      if (res.length()>5000) {
        res = res.substring(0, 5000);
      }
      initmap.put("returnData", res);
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "SYSOPERATRLOG");
      baseService.save(initmap);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      log.error("bizLog error{}", e.getMessage());
      e.printStackTrace();
    }
    
    if (map != null && map.get("bizType") != null && map.get("action") != null
        && map.get("userId") != null && map.get("userName") != null) {
      Map<String, Object> params = new HashMap<String, Object>();
      params.put("bizType", map.get("bizType"));
      params.put("action", map.get("action"));
      if (map.get("bizId") != null) {
        params.put("bizId", map.get("bizId"));
      } else {
        params.put("bizId", map.get("id"));
      }
      params.put("userId", map.get("userId"));
      params.put("userName", map.get("userName"));
      LogQueueThread.add(params);
    }
    return result;
  }
}
