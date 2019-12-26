package com.nmghr.service.ajglqbxs;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.common.exception.GlobalErrorException;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.hander.save.cluster.DeptMapperSaveHandler;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;

@SuppressWarnings("unchecked")
@Service("caseAssistService")
public class CaseAssistService {

  @Autowired
  private IBaseService baseService;
  private static Map<String, String> DeptJP = new HashedMap();
  private static Map<String, String> DeptAssist = new HashedMap();

  static {
    DeptJP.put("610000", "SX");
    DeptJP.put("610100", "XA");
    DeptJP.put("610200", "TC");
    DeptJP.put("610300", "BJ");
    DeptJP.put("610400", "XY");
    DeptJP.put("610500", "WN");
    DeptJP.put("610600", "YA");
    DeptJP.put("610700", "HZ");
    DeptJP.put("610800", "YL");
    DeptJP.put("610900", "AK");
    DeptJP.put("611000", "SL");
    DeptJP.put("611400", "YLX");
    DeptJP.put("616200", "XX");

    DeptAssist.put("610000", "SX");
    DeptAssist.put("610100", "XA");
    DeptAssist.put("610200", "TC");
    DeptAssist.put("610300", "BJ");
    DeptAssist.put("610400", "XY");
    DeptAssist.put("610500", "WN");
    DeptAssist.put("610600", "YA");
    DeptAssist.put("610700", "HZ");
    DeptAssist.put("610800", "YL");
    DeptAssist.put("610900", "AK");
    DeptAssist.put("611000", "SL");
    DeptAssist.put("611400", "YLX");
    DeptAssist.put("616200", "XX");
  }


  public Boolean checkNumber(String dept, String number, Object id, String category) throws Exception {
    Map<String, Object> params = new HashMap<>();
    params.put("deptCode", dept);
    params.put("number", number);
    if (!StringUtils.isEmpty(id)) {
      params.put("id", id);
    }
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJCLUSTERNUMBERCHECK");
    List<Map<String, Object>> list = (List<Map<String, Object>>) baseService.list(params);
    if (list != null && list.size() > 0) {
      throw new GlobalErrorException("999887", "集群战役编号重复");
    }
    if ("1".equals(category)) {
      return true;
    }
    dept = dept.substring(0, 6);
    if (!DeptJP.containsKey(dept)) {
      throw new GlobalErrorException("999887", "部门编号不正确");
    }
    if ("611400".equals(dept)) {
      if (!String.valueOf(DeptJP.get(dept)).equals(number.substring(0, 3))) {
        throw new GlobalErrorException("999887", "编号前缀应为YLX");
      }
      if (number.length() < 10) {
        throw new GlobalErrorException("999887", "编号格式不正确");
      }
    } else {
      if (!String.valueOf(DeptJP.get(dept)).equals(number.substring(0, 2))) {
        throw new GlobalErrorException("999887", "编号前缀应为" + String.valueOf(DeptJP.get(dept)));
      }
      if (number.length() < 9) {
        throw new GlobalErrorException("999887", "编号格式不正确");
      }
    }
    return true;
  }

  /**
   * 检查案件协查编号是否重复
   *
   * @param dept
   * @param number
   * @return
   * @throws Exception
   */
  public Boolean checkAssistNumber(String dept, String number, String assistId) throws Exception {
    Map<String, Object> params = new HashMap<>();
    params.put("deptCode", dept);
    params.put("number", number);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "AJASSISTNUMBERCHECK");
    List<Map<String, Object>> list = (List<Map<String, Object>>) baseService.list(params);
    if ("".equals(assistId)) {
      if (list != null && list.size() > 0) {
        throw new GlobalErrorException("999887", "案件协查编号重复");
      }
    } else {
      Map<String, Object> assistMap = list.get(0);
      if (!String.valueOf(assistMap.get("id")).equals(assistId)) {
        throw new GlobalErrorException("999887", "案件协查编号重复");
      }
    }

    dept = dept.substring(0, 4) + "00";
    if (!DeptAssist.containsKey(dept)) {
      throw new GlobalErrorException("999887", "部门编号不正确");
    }
    if ("611400".equals(dept)) {
      if (!String.valueOf(DeptAssist.get(dept)).equals(number.substring(0, 3))) {
        throw new GlobalErrorException("999887", "编号前缀应为YLX");
      }
      if (number.length() < 10) {
        throw new GlobalErrorException("999887", "编号格式不正确");
      }
    } else {
      if (!String.valueOf(DeptAssist.get(dept)).equals(number.substring(0, 2))) {
        throw new GlobalErrorException("999887", "编号前缀应为" + String.valueOf(DeptAssist.get(dept)));
      }
      if (number.length() < 9) {
        throw new GlobalErrorException("999887", "编号格式不正确");
      }
    }
    return true;
  }

  /**
   * 根据业务id 获取线索的总数和已分配的数量
   *
   * @param dept
   * @return
   * @throws Exception
   */
  public Object number(String dept, int type) throws Exception {
    Map<String, Object> param = new HashedMap();
    param.put("deptCode", dept);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, type == 1 ? "AJCLUSTERNUMBER" : "AJASSISTNUMBER");
    Map<String, Object> rs = (Map<String, Object>) baseService.get(param);
    if (rs == null) {
      return initNumber(dept.substring(0, 4) + "00");
    }
    String number = type == 1 ? String.valueOf(rs.get("clusterNumber")) : String.valueOf(rs.get("assistNumber"));
    return getNumber(dept.substring(0, 4) + "00", number);
  }


  private String getNumber(String dept, String nStr) {
    if ("611400".equals(dept)) {
      int year = Integer.parseInt(String.valueOf(nStr.substring(3, 7)));
      int ser = Integer.parseInt(String.valueOf(nStr.substring(7)));
      int thisYear = Calendar.getInstance().get(Calendar.YEAR);
      if (!String.valueOf(thisYear).equals(nStr.substring(3, 7))) {
        ser = 1;
        year = thisYear;
      } else {
        ser++;
      }
      String num = String.valueOf(ser);
      if (ser < 1000) {
        num = String.format("%03d", ser);
      }
      String jp = "NN";
      if (DeptJP.get(dept) != null) {
        jp = DeptJP.get(dept);
      }
      return jp + year + num;
    }
    int year = Integer.parseInt(String.valueOf(nStr.substring(2, 6)));
    int ser = Integer.parseInt(String.valueOf(nStr.substring(6)));
    int thisYear = Calendar.getInstance().get(Calendar.YEAR);
    if (!String.valueOf(thisYear).equals(nStr.substring(2, 6))) {
      ser = 1;
      year = thisYear;
    } else {
      ser++;
    }
    String num = String.valueOf(ser);
    if (ser < 1000) {
      num = String.format("%03d", ser);
    }
    String jp = "NN";
    if (DeptJP.get(dept) != null) {
      jp = DeptJP.get(dept);
    }
    return jp + year + num;
  }

  private String initNumber(String dept) {
    String jp = "NN";
    if (DeptJP.get(dept) != null) {
      jp = DeptJP.get(dept);
    }
    return jp + Calendar.getInstance().get(Calendar.YEAR) + String.format("%03d", 1);
  }


}
