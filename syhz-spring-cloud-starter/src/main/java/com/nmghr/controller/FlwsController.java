/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.nmghr.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.common.Result;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;

/**
 * 法律文书
 *
 * @author weber
 * @date 2019年8月5日 下午4:25:48
 * @version 1.0
 */
@RestController
@RequestMapping("/flws")
public class FlwsController {

  @Autowired
  @Qualifier("baseService")
  private IBaseService baseService;

  /**
   * 无文书申请
   * 
   * @param body
   * @param request
   * @return
   * @throws Exception
   */
  @PutMapping(value = "/examine")
  @ResponseBody
  public Object examine(@RequestBody Map<String, Object> body, HttpServletRequest request)
      throws Exception {

    if (body.get("content") == null || "".equals(body.get("content"))) {
      return Result.fail("999995", "请输入申请内容");
    }
    if (body.get("acptDept") == null || "".equals(body.get("acptDept"))) {
      return Result.fail("999995", "请输入审核部门");
    }

    // 保存工单主表信息
    Map<String, Object> workOrder = new HashMap<String, Object>();
    workOrder.put("type", "0009");
    workOrder.put("status", 1);
    workOrder.put("user", body.get("userId"));
    workOrder.put("userName", body.get("userName"));
    workOrder.put("dept", body.get("deptId"));
    workOrder.put("deptName", body.get("deptName"));
    workOrder.put("acceptDept", body.get("acptDept"));
    workOrder.put("acceptDeptName", body.get("acptDeptName"));
    workOrder.put("table", "AJFLWS");
    workOrder.put("value", body.get("ajbh"));
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "workorder".toUpperCase());
    Object orderId = baseService.save(workOrder);

    // 保存工单明细表信息
    Map<String, Object> orderFlowMap = new HashMap<String, Object>();
    orderFlowMap.put("wdId", orderId); // wd_id 工单id
    orderFlowMap.put("acceptedDept", body.get("acptDept")); // accepted_dept 工单流接收部门ID
    orderFlowMap.put("acceptedDeptName", body.get("acptDeptName"));
    orderFlowMap.put("acceptedUser", body.get("acptUser")); // accepted_user 工单流接收人
    orderFlowMap.put("wdFlowStatus", 1); // wd_flow_status 工单流转状态: 1 待审批; 2 审批中; 3 已完成; 4驳回; 5已过期
    orderFlowMap.put("ext1", body.get("content")); // wd_flow_status 工单流转状态: 1 待审批; 2 审批中; 3 已完成; 4驳回; 5已过期
    orderFlowMap.put("ext2", body.get("files")); // 无文书审核 附件列表
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "workorderFlow".toUpperCase());
    return baseService.save(orderFlowMap);
  }

  /**
   * 文件详情查询
   * 
   * @param id
   * @param requestParam
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  @SuppressWarnings("unchecked")
  @GetMapping(value = "/detail/{id}")
  public Object detail(@PathVariable String id, @RequestParam Map<String, Object> requestParam,
      HttpServletRequest request, HttpServletResponse response) throws Exception {
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJFLWS");
    Map<String, Object> map = (Map<String, Object>) baseService.get(id);
    Map<String, Object> result = new HashMap<>();
    if ("0".equals(String.valueOf(map.get("sjly")))) { // 本平台 目前保存的是图片地址
      // result.put("imgPaths", map.get("wsnr"));
      result.put("imgPaths", new String((byte[]) map.get("wsnr"), "UTF-8"));
    }
    if ("1".equals(String.valueOf(map.get("sjly")))) { // 警综 返回 有数据返回true ,无数据false
      result.put("pdf", map.get("wsnrPdf") != null);
      result.put("html", map.get("wsnr") != null);
    }
    result.put("sjly", map.get("sjly"));
    return result;
  }

  /**
   * 删除
   * 
   * @param param
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  @SuppressWarnings("unchecked")
  @PostMapping(value = "/delete/{id}")
  public Object delete(@PathVariable String id, @RequestBody Map<String, Object> param,
      HttpServletRequest request, HttpServletResponse response) throws Exception {
    if (param.get("ajbh") == null || "".equals(param.get("ajbh"))) {
      return Result.fail("999995", "参数异常ajbh.");
    }
    if (param.get("deptId") == null || "".equals(param.get("deptId"))) {
      return Result.fail("999995", "参数异常deptId.");
    }
    if (param.get("deptCode") == null || "".equals(param.get("deptCode"))) {
      return Result.fail("999995", "参数异常deptCode.");
    }
    Map<String, Object> params = new HashMap<String, Object>();
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "CHECKAJSIGN");
    params.put("ajbh", param.get("ajbh"));
    params.put("status", 5);
    List<Map<String, Object>> sign = (List<Map<String, Object>>) baseService.list(params);
    if (sign != null && sign.size() > 0) {
      // 已认领 保留一条不能删除 判断是否剩最后一条
      params = new HashMap<String, Object>();
      params.put("ajbh", param.get("ajbh"));
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJFLWS");
      List<Map<String, Object>> list = (List<Map<String, Object>>) baseService.list(params);
      if (list != null && list.size() == 1) {
        return Result.fail("999996", "至少保留一条法律文书");
      }
    }
    params = new HashMap<String, Object>();
    params.put("deptCode", param.get("deptCode"));
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJFLWSDEL");
    return baseService.update(id, params);
  }

  /**
   * 文书状态
   * 
   * @param param
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  @SuppressWarnings("unchecked")
  @GetMapping(value = "/status")
  public Object status(@RequestParam Map<String, Object> param, HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    if (param.get("ajbh") == null || "".equals(param.get("ajbh"))) {
      return Result.fail("999995", "请输入案件编号");
    }
    Map<String, Object> result = new HashMap<>();
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJFLWS");
    List<Map<String, Object>> list = (List<Map<String, Object>>) baseService.list(param);
    if (list != null && list.size() > 0) {
      result.put("list", list.size());
      return result;
    }
    result.put("list", 0);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJFLWSEXAMINES");
    List<Map<String, Object>> examine = (List<Map<String, Object>>) baseService.list(param);

    if (examine != null && examine.size() > 0) {
      Map<String, Object> map = examine.get(0);
      result.put("examine", map.get("status"));
    }
    return result;
  }

  /**
   * wsnrpdf pdf 信息
   * 
   * @param id
   * @param requestParam
   * @param request
   * @param response
   */
  @SuppressWarnings("unchecked")
  @GetMapping(value = "/pdf/{id}")
  public void detailPdf(@PathVariable String id, @RequestParam Map<String, Object> requestParam,
      HttpServletRequest request, HttpServletResponse response) {
    try {
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJFLWS");
      Map<String, Object> map = (Map<String, Object>) baseService.get(id);
      byte[] content = (byte[]) map.get("wsnrPdf");
      InputStream is = new ByteArrayInputStream(content);
      response.reset();
      response.setCharacterEncoding("utf-8");
      ServletOutputStream out = response.getOutputStream();
      BufferedInputStream bis = null;
      BufferedOutputStream bos = null;
      try {
        bis = new BufferedInputStream(is);
        bos = new BufferedOutputStream(out);
        byte[] buff = new byte[2048];
        int bytesRead;
        while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
          bos.write(buff, 0, bytesRead);
        }
      } catch (final IOException e) {
        throw e;
      } finally {
        if (bis != null)
          bis.close();
        if (bos != null)
          bos.close();
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * wsnr html信息
   * 
   * @param id
   * @param requestParam
   * @param request
   * @param response
   */
  @SuppressWarnings("unchecked")
  @GetMapping(value = "/html/{id}")
  public void detailHtml(@PathVariable String id, @RequestParam Map<String, Object> requestParam,
      HttpServletRequest request, HttpServletResponse response) {
    try {
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJFLWS");
      Map<String, Object> map = (Map<String, Object>) baseService.get(id);
      response.setContentType("text/html;charset=GBK");
      PrintWriter out = response.getWriter();
      try {
        if (map.get("wsnr") != null) {
          out.println(new String((byte[]) map.get("wsnr"), "GBK"));
        }
      } finally {
        out.close();
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
