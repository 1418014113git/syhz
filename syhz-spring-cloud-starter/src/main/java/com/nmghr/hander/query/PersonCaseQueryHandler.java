/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.nmghr.hander.query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.IteratorUtils;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.page.Paging;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractQueryHandler;
import com.nmghr.controller.vo.XyrBean;

/**
 * <功能描述/>
 *
 * @author weber
 * @date 2019年1月22日 下午6:14:41
 * @version 1.0
 */
@Service("personcaseQueryHandler")
public class PersonCaseQueryHandler extends AbstractQueryHandler {

  public PersonCaseQueryHandler(IBaseService baseService) {
    super(baseService);
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  @Override
  public Object list(Map<String, Object> params) throws Exception {
    int pageNum = 1, pageSize = 3;
    if(params.get("pageNum")!=null&& !"".equals(String.valueOf(params.get("pageNum")))) {
      pageNum = Integer.parseInt(String.valueOf(params.get("pageNum")));
    }
    if(params.get("pageSize")!=null&& !"".equals(String.valueOf(params.get("pageSize")))) {
      pageSize = Integer.parseInt(String.valueOf(params.get("pageSize")));
    }
    // 本案的嫌疑人身份证号码
    List<String> sfzhs = new ArrayList<String>();

    // 同案人查询
    JSONObject xyrCount = new JSONObject();
    getXryTongAnCount(params, xyrCount, sfzhs);
    if (xyrCount.size() == 0) {
      return new ArrayList(); // 没有其他嫌疑人
    }
    List<String> xyrSfzhs = IteratorUtils.toList(xyrCount.keySet().iterator());
    // 通话记录查询
    JSONObject xyrTHCount = new JSONObject();
    getXyrThCount(xyrSfzhs, sfzhs, xyrTHCount);

    // 同出行同入住数据查询
    JSONObject xyrTCX = new JSONObject(); // 同出行
    JSONObject xyrTRZ = new JSONObject(); // 同入住
    getTcxTrz(xyrSfzhs, sfzhs, xyrTCX, xyrTRZ);

    //计算总分数
    List<XyrBean> result = new ArrayList<XyrBean>();
    handlerPersonInfo(xyrCount, params, xyrTHCount, xyrTCX, xyrTRZ, result);
    pageNum = pageNum - 1;
    int fromIndex = pageNum * pageSize;
    int toIndex = (pageNum + 1) * pageSize;
    if (fromIndex>= result.size()) {
      new Paging(pageSize, pageNum, result.size(), new ArrayList<>());
    }
    if(toIndex>result.size()) {
      toIndex = result.size();
    }
    
    return new Paging(pageSize, pageNum, result.size(), result.subList(fromIndex, toIndex));
  }

  /**
   * 处理同出行同入住数据
   * @param tAsfzhs
   * @param sfzhs
   * @param xyrTCX
   * @param xyrTRZ
   * @throws Exception
   */
  @SuppressWarnings("unchecked")
  private void getTcxTrz(List<String> tAsfzhs, List<String> sfzhs, JSONObject xyrTCX,
      JSONObject xyrTRZ) throws Exception {
    // 处理同入住同出行 XYRTONGCXRZ
    List<String> zjhms = new ArrayList<String>();
    zjhms.addAll(sfzhs);
    zjhms.addAll(tAsfzhs);
    // 根据所有的身份证号码查询手机号码
    Map<String, Object> cxrzCS = new HashMap<String, Object>();
    cxrzCS.put("zjhms", zjhms);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "XYRTONGCXRZ");
    List<Map<String, Object>> zjhmCs = (List<Map<String, Object>>) baseService.list(cxrzCS);
    for (String sfzh : tAsfzhs) {
      int totalCX = 0; // 同出行
      int totalRZ = 0; // 同入住
      for (Map<String, Object> map : zjhmCs) {
        if (map.get("zjhm") != null && map.get("gxr_zjhm") != null
            && (map.get("zjhm").equals(sfzh) || map.get("gxr_zjhm").equals(sfzh))) {
          if (map.get("gxcs") != null && map.get("gxlxdm") != null) {
            int cs = 0;
            if (!"".equals(map.get("gxcs"))) {
              cs = Integer.valueOf(String.valueOf(map.get("gxcs")));
            }
            // 同入住类型
            if ("10001".equals(map.get("gxlxdm"))) {
              totalRZ += cs;
            }
            // 同出行类型
            if ("10002".equals(map.get("gxlxdm"))) {
              totalCX += cs;
            }
          }
        }
      }
      xyrTCX.put(sfzh, totalCX);
      xyrTRZ.put(sfzh, totalRZ);
    }
  }

  /**
   * 处理人员信息及其分数
   * 
   * @param xyrCount
   * @param params
   * @param xyrTHCount
   * @param xyrTCX
   * @param xyrTRZ
   * @param result
   * @throws Exception
   */

  @SuppressWarnings("unchecked")
  private void handlerPersonInfo(JSONObject xyrCount, Map<String, Object> params,
      JSONObject xyrTHCount, JSONObject xyrTCX, JSONObject xyrTRZ, List<XyrBean> result)
      throws Exception {

    int tcx = 1;// 同出行
    int trz = 1;// 同入住
    int thjl = 1;// 通话记录
    int tar = 1;// 同案人
    if (params.get("tcx") != null) {
      tcx = Integer.parseInt(String.valueOf(params.get("tcx")));// 同出行
    }
    if (params.get("trz") != null) {
      trz = Integer.parseInt(String.valueOf(params.get("trz")));// 同入住
    }
    if (params.get("thjl") != null) {
      thjl = Integer.parseInt(String.valueOf(params.get("thjl")));// 通话记录
    }
    if (params.get("tar") != null) {
      tar = Integer.parseInt(String.valueOf(params.get("tar")));// 同案人
    }

    Map<String, Object> perParams = new HashMap<String, Object>();
    perParams.put("gmsfhm", IteratorUtils.toList(xyrCount.keySet().iterator()));
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "PERSONNMBYSFZHM");
    List<Map<String, Object>> persons = (List<Map<String, Object>>) baseService.list(perParams);
    for (Map<String, Object> map : persons) {
      XyrBean bean = new XyrBean();
      bean.setBean(map);
      int tcxScore = 0; // 同出行计算
      int trzScore = 0;// 同入住计算
      int tarScore = 0;// 同案人计算
      int thjlScore = 0;// 童虎记录计算
      String sfhm = String.valueOf(map.get("gmsfhm"));
      if(xyrTCX.getInteger(sfhm)!=null) {
        tcxScore = xyrTCX.getInteger(sfhm) * tcx; // 同出行计算
      }
      if(xyrTRZ.getInteger(sfhm)!=null) {
        trzScore = xyrTRZ.getInteger(sfhm) * trz;// 同入住计算
      }
      if(xyrCount.getInteger(sfhm)!=null) {
        tarScore = xyrCount.getInteger(sfhm) * tar;// 同案人计算
      }
      if(xyrTHCount.getInteger(sfhm)!=null) {
        thjlScore = xyrTHCount.getInteger(sfhm) * thjl;// 童虎记录计算
      }
      bean.setTcx(tcxScore);
      bean.setTrz(trzScore);
      bean.setTar(tarScore);
      bean.setThjl(thjlScore);
      bean.setTotalScore(tcxScore + trzScore + tarScore + thjlScore);// 计算总数
      result.add(bean);
    }
    // 按总分倒叙
    Collections.sort(result, new Comparator<XyrBean>() {
      public int compare(XyrBean p1, XyrBean p2) {
        Integer score1 = p1.getTotalScore();
        Integer score2 = p2.getTotalScore();
        return score1.compareTo(score2) * -1;
      }
    });
  }

  /**
   * 获取同案人次数
   * 
   * @param params
   * @param xyrCount
   * @param sfzhs
   * @throws Exception
   */
  @SuppressWarnings("unchecked")
  private void getXryTongAnCount(Map<String, Object> params, JSONObject xyrCount,
      List<String> sfzhs) throws Exception {
    String ajbh = String.valueOf(params.get("ajbh"));
    List<String> ajbhs = new ArrayList<String>();
    ajbhs.add(ajbh);
    params = new HashMap<String, Object>();
    params.put("ajbh", ajbhs);
    // 根据案件编号查询嫌疑人身份证号码
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "XYRTONGAN");
    List<Map<String, Object>> pers = (List<Map<String, Object>>) baseService.list(params);
    if (pers != null && pers.size() > 0) {
      // 整理嫌疑人身份证号码
      for (Map<String, Object> per : pers) {
        if (per.get("GMSFHM") != null) {
          sfzhs.add(String.valueOf(per.get("GMSFHM")));
        }
      }
      if (sfzhs.size() > 0) {
        // 根据嫌疑人身份证号码查询 相关的所有案件
        params = new HashMap<String, Object>();
        params.put("gmsfhm", sfzhs);
        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "XYRTONGAN");
        List<Map<String, Object>> perAjs = (List<Map<String, Object>>) baseService.list(params);
        // 整理所有案件
        List<String> perAjbhs = new ArrayList<String>();
        for (Map<String, Object> perAj : perAjs) {
          if (perAj.get("AJBH") != null) {
            if (!ajbh.equals(String.valueOf(perAj.get("AJBH")))) {
              perAjbhs.add(String.valueOf(perAj.get("AJBH")));
            }
          }
        }
        // 查询相关案件的嫌疑人
        if (perAjbhs.size() > 0) {
          params = new HashMap<String, Object>();
          params.put("ajbh", perAjbhs);
          LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "XYRTONGAN");
          List<Map<String, Object>> otherAjs = (List<Map<String, Object>>) baseService.list(params);
          for (Map<String, Object> otAj : otherAjs) {
            if (otAj.get("GMSFHM") != null) {
              String wxyrhm = String.valueOf(otAj.get("GMSFHM"));
              if (!sfzhs.contains(wxyrhm)) {
                // 不是本案嫌疑人 遍历查询他所在案件是否有本案嫌疑人
                int count = 0;
                for (Map<String, Object> map : otherAjs) {
                  if (map.get("AJBH") != null && map.get("AJBH").equals(otAj.get("AJBH"))
                      && sfzhs.contains(String.valueOf(map.get("GMSFHM")))) {
                    count++;
                  }
                }
                xyrCount.put(wxyrhm, count);
              }
            }
          }
        }
      }
    }
  }

  /**
   * 获取通话记录次数
   * 
   * @param xyrCount
   * @param sfzhs
   * @param xyrTHCount
   * @return
   * @throws Exception
   */
  @SuppressWarnings("unchecked")
  private void getXyrThCount(List<String> xyrSfzhs, List<String> sfzhs, JSONObject xyrTHCount)
      throws Exception {
    List<String> zjhms = new ArrayList<String>();
    zjhms.addAll(sfzhs);
    zjhms.addAll(xyrSfzhs);
    // 根据所有的身份证号码查询手机号码
    Map<String, Object> tonghua = new HashMap<String, Object>();
    tonghua.put("zjhms", zjhms);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "XYRPHONES");
    List<Map<String, Object>> zjhmPhone = (List<Map<String, Object>>) baseService.list(tonghua);

    JSONObject benAnRen = new JSONObject();// 本案人的身份证号和手机号码
    JSONObject taAnRen = new JSONObject(); // 它案人的身份证号和手机号码
    List<String> phones = new ArrayList<String>();
    for (Map<String, Object> phone : zjhmPhone) {
      if (phone.get("zh") != null) {
        // 保留本案人的手机号
        if (phone.get("sfzhm") != null) {
          if (sfzhs.contains(phone.get("sfzhm"))) {
            benAnRen.put(String.valueOf(phone.get("sfzhm")), phone.get("zh"));
          } else {
            taAnRen.put(String.valueOf(phone.get("sfzhm")), phone.get("zh"));
            phones.add(String.valueOf(phone.get("zh")));
          }
        }
      }
    }
    // 拿所有的手机号码查询所有的通讯记录
    tonghua = new HashMap<String, Object>();
    tonghua.put("phones", phones);
    if (phones.size() == 0) {
      return;
    }
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "XYRTONGHUA");
    List<Map<String, Object>> phoneRecords = (List<Map<String, Object>>) baseService.list(tonghua);
    // 遍历统计手机通话记录数
    Iterator<String> it = taAnRen.keySet().iterator();
    Iterator<Object> benAnphone = benAnRen.values().iterator();
    List<Object> bAPhones = IteratorUtils.toList(benAnphone);
    // 查询其他案人与本案人通话记录次数
    while (it.hasNext()) {
      String key = it.next(); // 它案人的身份证号
      String phone = taAnRen.getString(key);
      int count = 0;
      for (Map<String, Object> map : phoneRecords) {
        String brhm = String.valueOf(map.get("brhm"));
        String dfhm = String.valueOf(map.get("dfhm"));
        if (phone.equals(brhm) || phone.equals(dfhm)) { // 手机号码存在 它案人手机号
          if (bAPhones.contains(brhm) || bAPhones.contains(dfhm)) { // 手机号码存在于本案中 次数增加
            count++;
          }
        }
      }
      xyrTHCount.put(key, count);
    }
  }

}
