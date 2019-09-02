/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.nmghr.job;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.impl.BaseServiceImpl;
import com.nmghr.basic.task.Task;
import com.nmghr.entity.BusinessSign;
import com.nmghr.mapper.CaseManagerMapper;
import com.nmghr.util.DateUtil;

/**
 * <功能描述/>
 *
 * @author brook
 * @date 2018年8月22日 下午6:37:16
 * @version 1.0
 */
@Service("caseManagerJob")
public class CaseManagerJob implements Task {

  private static Logger logger = LoggerFactory.getLogger(CaseManagerJob.class);

  @Autowired
  private BaseServiceImpl baseService;

  @Autowired
  private CaseManagerMapper caseMapper;

  /*
   * @Value("${signDept}") private String signDept;
   */

  @Override
  @Transactional
  public void run() {
    // TODO Auto-generated method stub
    logger.info("caseManagerJob start!");
    try {
      // 获取案件ETL认领表数据
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "JBXXETL");
      List<Map<String, Object>> list = (List<Map<String, Object>>) baseService.list(null);
      logger.info("caseManagerJob  list.size = {}!", list.size());
      if (CollectionUtils.isNotEmpty(list)) {
        for (Map<String, Object> jbxx : list) {
          List<BusinessSign> sign = caseMapper.getBusinessSignByAJBH(String.valueOf(jbxx.get("AJBH")));
          // 签收表如果没有新增加的案件签收任务，此时增加此案件签收信息(统一由总队处理，总队的)
          if (null == sign || sign.size() == 0) {
            LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "BUSINESSSIGN");
            Map<String, Object> map = initSignTable(String.valueOf(jbxx.get("DEPT_ID")),
                String.valueOf(jbxx.get("deptName")),
                String.valueOf(jbxx.get("AJBH")));
            baseService.save(map);
            checkDya(jbxx);
          }
        }
      } else {
        logger.info("caseManagerJob query list zero!");
      }

    } catch (Exception e) {
      e.printStackTrace();
      logger.error(e.getMessage() + "!!!!!!!!!!!!!!!!!!!!!!!!!");
    }
    logger.info("caseManagerJob end!");
  }

  /**
   * 初始化签收表insert所需的参数值
   * 
   **/
  public Map<String, Object> initSignTable(String deptId, String deptName, String ajbh) {
    Map<String, Object> map = new HashMap<String, Object>();
    Timestamp timestamp = DateUtil.Date2Timestamp(new Date());
    map.put("signUserId", "-1");
    map.put("signUserName", "系统推送");
    map.put("signTime", timestamp);
    map.put("businessTable", "aj_jbxx_etl");
    map.put("businessProperty", "AJBH");
    map.put("businessValue", ajbh);
    map.put("noticeOrgId", deptId);
    map.put("noticeOrgName", deptName);
    map.put("noticeRoleId", "-1");
    map.put("noticeTime", timestamp);
    map.put("noticeUserId", "-1");
    map.put("qsStatus", "3");
    map.put("parentId", "-1");
    map.put("noticeLx", "");
    map.put("updateUserId", "-1");
    map.put("businessType", "2");
    map.put("deadlineTime", timestamp);
    map.put("status", 3);
    return map;
  }

  /**
   * 判断当前案件是不是大要案.
   * 
   * @param jbxx 案件信息
   * @throws Exception 异常信息
   */
  private void checkDya(Map<String, Object> jbxx) throws Exception {
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "FZINFO");
    List<Map<String, Object>> fzList = (List<Map<String, Object>>) baseService.list(null);
    for (Map<String, Object> fz : fzList) {
      Object fzId = fz.get("id");
      Object ajbh = jbxx.get("AJBH");
      String zhrys = jbxx.get("ZHRYS").toString();
      BigDecimal sszhrmb = new BigDecimal(jbxx.get("SAJZ").toString());
      String sfsw = jbxx.get("SFSW").toString();
      String ajmc = jbxx.get("AJMC").toString();
      String jyaq = jbxx.get("JYAQ").toString();
      if (fz.get("property").equals("zhrys")) {
        String fzzhrys = fz.get("threshold").toString();

        if (Integer.parseInt(zhrys) > Integer.parseInt(fzzhrys)) {
          insertDya(fzId, ajbh);
        }
      }
      if (fz.get("property").equals("sszhrmb")) {
        BigDecimal fzsszhrmb = new BigDecimal(fz.get("threshold").toString());
        if (sszhrmb.compareTo(fzsszhrmb) == 1) {
          insertDya(fzId, ajbh);
        }
      }
      if (fz.get("property").equals("sfsw")) {
        String fzsfsw = fz.get("threshold").toString();
        if (Integer.parseInt(sfsw) == Integer.parseInt(fzsfsw)) {
          insertDya(fzId, ajbh);
        }
      }
      if (fz.get("property").equals("sjwp")) {
        String fzajmcs = fz.get("threshold").toString();
        String[] fzajmc = fzajmcs.split(",");
        for (String fzajms : fzajmc) {
          if (ajmc.contains(fzajms)) {
            insertDya(fzId, ajbh);
          } else if (jyaq.contains("fzajms")) {
            insertDya(fzId, ajbh);
          }
        }
      }
    }
  }

  /**
   * 添加大要案.
   * 
   * @param fzId fz表id
   * @param ajbh 案件编号
   * @throws Exception 异常信息
   */
  private void insertDya(Object fzId, Object ajbh) throws Exception {
    Map<String, Object> requestBody = new HashMap<String, Object>();
    requestBody.put("fzId", fzId);
    requestBody.put("ajbh", ajbh);
    Object dyaObj = getDya(requestBody);
    List<Map<String, Object>> dyaList = (List<Map<String, Object>>) dyaObj;
    if (dyaList.size() < 1) {
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "DYAFZ");
      baseService.save(requestBody);
    }
  }

  /**
   * 查询大要案.
   * 
   * @param requestBody 要案信息
   * @return
   * @throws Exception 异常信息
   */
  private Object getDya(Map<String, Object> requestBody) throws Exception {
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "DYACHECK");
    return baseService.list(requestBody);
  }

}
