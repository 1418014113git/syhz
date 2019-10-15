/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.nmghr.aspect;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.impl.BaseServiceImpl;
import com.nmghr.controller.AuthenticateController;
import com.nmghr.util.GetIpUtil;

/**
 * <功能描述/>
 *
 * @author brook
 * @date 2018年9月4日 下午1:58:46
 * @version 1.0
 */
@Aspect
@Component
public class WebLogAspect {

  private static final Logger log = LoggerFactory.getLogger(AuthenticateController.class);
  @Autowired
  private BaseServiceImpl baseService;

  @Pointcut("execution(public * com.nmghr..controller.*.*(..))")
  public void webLog() {}

  @Before("webLog()")
  public void doBefore(JoinPoint joinPoint) throws Throwable {
    // 接收到请求，记录请求内容
    ServletRequestAttributes attributes =
        (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    HttpServletRequest request = attributes.getRequest();

    // 记录下请求内容
//    log.info("URL : " + request.getRequestURL().toString());
//    log.info("HTTP_METHOD : " + request.getMethod());
//    log.info("IP : " + GetIpUtil.getIpAddr(request));
//    log.info("CLASS_METHOD : " + joinPoint.getSignature().getDeclaringTypeName() + "."
//        + joinPoint.getSignature().getName());
//    log.info("ARGS : " + Arrays.toString(joinPoint.getArgs()));

//    try {
//      Map<String, Object> map = new HashMap<String, Object>();
//
//      map.put("url", request.getRequestURL().toString());
//      map.put("httpMethod", request.getMethod());
//      map.put("ipAddress", GetIpUtil.getIpAddr(request));
//      map.put("classMethod", joinPoint.getSignature().getDeclaringTypeName() + "."
//          + joinPoint.getSignature().getName());
//      map.put("args", Arrays.toString(joinPoint.getArgs()));
//      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "SYSOPERATRLOG");
//      baseService.save(map);
//    } catch (Exception e) {
//      log.error("save operatr log error", e.getMessage());
//    }

  }

  @AfterReturning(returning = "ret", pointcut = "webLog()")
  public void doAfterReturning(Object ret) throws Throwable {
    // 处理完请求，返回内容
    log.info("RESPONSE : " + ret);
  }

  @Around("webLog()")
  public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
    long startTime = System.currentTimeMillis();
    Object ob = pjp.proceed();// ob 为方法的返回值
    log.info("耗时 : " + (System.currentTimeMillis() - startTime));
    return ob;
  }
}
