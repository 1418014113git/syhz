/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.nmghr.controller;

import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;
import com.nmghr.basic.common.Constant;
import com.nmghr.basic.common.Result;
import com.nmghr.basic.common.exception.GlobalErrorEnum;
import com.nmghr.basic.common.exception.GlobalErrorException;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.ISaveHandler;
import com.nmghr.basic.core.util.SpringUtils;
import com.nmghr.controller.vo.PitchManVO;
import com.sargeraswang.util.ExcelUtil.ExcelUtil;

/**
 * 摊贩信息导入
 *
 * @author weber
 * @date 2019年5月6日 下午2:10:40
 * @version 1.0
 */
@RestController
@SuppressWarnings("all")
@RequestMapping("/pitchman")
public class PitchManController {
  private static final Logger log = LoggerFactory.getLogger(PitchManController.class);
  @Autowired
  private IBaseService baseService;


  @PostMapping(value = "/upload")
  @ResponseBody
  public Object upload(@RequestParam("file") MultipartFile mulFile,
      @RequestParam("createDept") Object createDept, @RequestParam("createName") Object createName,
      HttpServletRequest request) throws Exception {
    log.info("excel uploadFile file start {}{}", mulFile);
    try {
      if (null != mulFile) {
        List<PitchManVO> list =
            (List<PitchManVO>) ExcelUtil.importExcel(PitchManVO.class, mulFile.getInputStream(), 0);
        if (!CollectionUtils.isEmpty(list)) {
          log.info("excel uploadFile file query size {}", list.size());
          if (list.size() > 1000) {
            log.error("excel uploadFile error, Maximum length exceeds 1000 ");
            throw new GlobalErrorException("99952", "最多不能超过1000条");
          }
          Map<String, Object> result = checkVOList(list, createDept, createName);
          return Result.ok(result);
        } else {
          throw new GlobalErrorException("99954", "上传文件为空");
        }
      } else {
        throw new GlobalErrorException("99954", "上传文件为空");
      }
    } catch (IllegalArgumentException e) {
      log.error("excel uploadFile error", e.getMessage());
      throw new GlobalErrorException("99951", "上传文件错误");
    } catch (FileNotFoundException e) {
      log.error("excel uploadFile error", e.getMessage());
      throw new GlobalErrorException("99954", "上传文件为空");
    } catch (Exception e) {
      log.error("excel uploadFile error", e.getMessage());
      throw new GlobalErrorException("99950", "上传异常");
    }
  }


  public Map<String, Object> checkVOList(List<PitchManVO> list, Object createDept,
      Object createName) {
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    Map<String, Object> map = null;
    int i = 1;
    List<String> errs = new ArrayList<String>();
    for (PitchManVO vo : list) {
      List<String> e = new ArrayList<String>();
      // 姓名判断
      if (vo.getName() == null || "".equals(vo.getName())) {
        e.add("名称不能为空");
      } else {
        if (vo.getName().length() > 50) {
          e.add("名称应小于20个字");
        } else {
          Map<String, Object> params = new HashMap<String, Object>();
          params.put("name", vo.getName());
          try {
            LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "PITCHMANVALID");
            List<Map<String, Object>> cks = (List<Map<String, Object>>) baseService.list(params);
            if (cks != null && cks.size() > 0) {
              e.add("商户名称已使用");
            } else {
              for (PitchManVO pitch : list) {
                if (!vo.equals(pitch) && vo.getName().equals(pitch.getName())) {
                  e.add("商户名称重复");
                  break;
                }
              }
            }
          } catch (Exception e1) {
            e1.printStackTrace();
          }
        }
      } 
      // 身份证号码
      if (vo.getCardNumber() == null
          || !Pattern.matches("(^\\d{15}$)|(^\\d{17}([0-9]|X|x)$)", vo.getCardNumber())) {
        e.add("身份证号码格式不正确");
      }
      if (vo.getPersonName() == null || "".equals(vo.getPersonName())) {
        e.add("负责人姓名不能为空");
      } else {
        if (vo.getName().length() > 50) {
          e.add("负责人姓名应小于20个字");
        }
      }
      if (vo.getPhone() == null || !Pattern.matches("^1\\d{10}$", vo.getPhone())) {
        e.add("请输入11位手机号码");
      }
      if (vo.getType() == null || !"ABC".contains(vo.getType())) {
        e.add("类型应为A，B，C");
      }
      if (vo.getManageProject() == null || "".equals(vo.getManageProject())) {
        e.add("经营项目不能为空");
      } else {
        if (vo.getName().length() > 50) {
          e.add("经营项目应小于50个字");
        }
      }
      if (vo.getStatus() == null || !"正常".contains(vo.getStatus())) {
        e.add("经营状态应为正常");
      }
      if (vo.getAddress() == null || "".equals(vo.getAddress())) {
        e.add("摊位地址不能为空");
      } else {
        if (vo.getName().length() > 50) {
          e.add("摊位地址应小于50个字");
        }
      }
      if (vo.getArea() == null || "".equals(vo.getArea())) {
        e.add("行政区划不能为空");
      } else {
        if (vo.getName().length() > 50) {
          e.add("行政区划应小于50个字");
        }
      }
      if (vo.getGrade() == null || !"高中低".contains(vo.getGrade())) {
        e.add("等级应为高，中，低");
      }
      if (vo.getPersonNumber() == null) {
        e.add("从业人数不能为空");
      }

      if (e.size() > 0) {
        e.add(0, "第" + i + "行：");
        errs.add(JSONObject.toJSONString(e));
      }
      i++;
    }
    // 调用service 判断唯一性 错误时返回空map 有错误时 结果放入error
    if (errs.size() > 0) {
      Map<String, Object> result = new HashMap<String, Object>();
      result.put("list", errs);
      result.put("type", "error");
      return result;
    }
    // 检验通过 调用插入handler
    try {
      Map<String, Object> params = new HashMap<String, Object>();
      ISaveHandler handler = SpringUtils.getBean("pitchmanexcelHandler", ISaveHandler.class);
      params.put("list", list);
      params.put("createDept", createDept);
      params.put("createName", createName);
      Object obj = handler.save(params);
      Map<String, Object> result = new HashMap<String, Object>();
      result.put("type", "success");
      return result;
    } catch (Exception e) {
      e.printStackTrace();
      Map<String, Object> result = new HashMap<String, Object>();
      result.put("type", "error");
      return result;
    }
  }


  /**
   * 时间格式正则判断
   * 
   * @param date
   * @return
   */
  private boolean isDate(String date) {
    Pattern p = Pattern.compile(
        "^((\\d{2}(([02468][048])|([13579][26]))[\\-\\s]?((((0?[13578])|(1[02]))[\\-\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\s]?((((0?[13578])|(1[02]))[\\-\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))?$");
    return p.matcher(date).matches();
  }


}
