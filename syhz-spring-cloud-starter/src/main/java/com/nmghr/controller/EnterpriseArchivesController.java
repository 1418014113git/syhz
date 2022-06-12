/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.nmghr.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.common.exception.GlobalErrorException;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;

/**
 * 新企业摊贩信息
 *
 * @author weber
 * @date 2019年5月10日 下午5:22:22
 * @version 1.0
 */
@RestController
public class EnterpriseArchivesController {
  private static final Logger log = LoggerFactory.getLogger(EnterpriseArchivesController.class);

  @Autowired
  private IBaseService baseService;


  @GetMapping(value = "/companyArchives/{type}")
  @ResponseBody
  public Object upload(@PathVariable("type") String type, @RequestParam Map<String, Object> params,
      HttpServletRequest request) throws Exception {

    String ALIAS = "";
    if ("01".equals(type)) {
      ALIAS = "CCENVIRONMENT";
    } else if ("02".equals(type)) {
      ALIAS = "CCCATERINGINDUSTRY";
    } else if ("03".equals(type)) {
      ALIAS = "CCFOODMACHINING";
    } else if ("04".equals(type)) {
      ALIAS = "CCALCOHOLICPRODUCE";
    } else if ("05".equals(type)) {
      ALIAS = "CCFOODCURRENCY";
    } else if ("06".equals(type)) {
      ALIAS = "CCFOODWHOLESALE";
    } else if ("07".equals(type)) {
      ALIAS = "CCDRUGPRODUCE";
    } else if ("08".equals(type)) {
      ALIAS = "CCDRUGWHOLESALE";
    } else if ("09".equals(type)) {
      ALIAS = "CCDRUGSTORE";
    } else if ("10".equals(type)) {
      ALIAS = "CCDRUGHEALTH";
    } else if ("11".equals(type)) {
      ALIAS = "CCDRUGAPPARATUS";
    } else if ("12".equals(type)) {
      ALIAS = "CCHUSBANDRYMEAT";
    } else if ("13".equals(type)) {
      ALIAS = "CCHUSBANDRYCULTURE";
    } else if ("14".equals(type)) {
      ALIAS = "CCMEDICALCLINIC";
    } else if ("15".equals(type)) {
      ALIAS = "CCMEDICALORG";
    } else if ("16".equals(type)) {
      ALIAS = "CCOTHERPERSONAL";
    }
    if ("".equals(ALIAS)) {
      throw new GlobalErrorException("99950", "类型不正确");
    }
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS);
    try {
      if (params.get("pageNum") != null && params.get("pageSize") != null) {
        int num = Integer.parseInt(String.valueOf(params.get("pageNum")));
        int size = Integer.parseInt(String.valueOf(params.get("pageSize")));
        return baseService.page(params, num, size);
      }
    } catch (NumberFormatException e) {
      throw new GlobalErrorException("99950", "分页数据不正确");
    }
    return null;
  }

  @SuppressWarnings("unchecked")
  @GetMapping(value = "/companyDetail/{type}/{id}")
  @ResponseBody
  public Object companyDetail(@PathVariable("id") String id, @PathVariable("type") String type,
      @RequestParam Map<String, Object> params, HttpServletRequest request) throws Exception {
    if ("company".equals(type)) {
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "CCOMPANYDETAIL");//企业
    } else if ("pitch".equals(type)) {
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "CPITCHMANDETAIL");//摊贩
    } else {
      throw new GlobalErrorException("99950", "类型不正确");
    }
    if (id == null || "".equals(id)) {
      return new HashMap<String, Object>();
    }
    Map<String, Object> obj = (Map<String, Object>) baseService.get(id);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "CCDETAILCOUNT");
    Map<String, Object> count = (Map<String, Object>) baseService.get(id);
    obj.put("count", count);
    return obj;
  }

}
