/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.nmghr.handler.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.common.exception.GlobalErrorEnum;
import com.nmghr.basic.common.exception.GlobalErrorException;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.util.ValidationUtils;
import com.nmghr.handler.vo.FractionEntityBo;
import com.nmghr.util.SyhzUtil;

/**
 * 积分计算服务
 *
 * @author kaven
 * @date 2019年9月25日 上午10:51:57
 * @version 1.0
 */
@Service("trainFractionService")
public class TrainFractionService {
	private static final String ALIAS_TRAIN_FRACTION_LOG = "trainFractionLog"; // 积分记录明细表
	private static final String ALIAS_TRAIN_FRACTION_LOG_COUNT = "TRAINFRACTIONLOGCOUNT"; // 积分记录明细表

  private final static int FRACTION_RECKON_0 = 0; // 增加积分
  private final static int FRACTION_RECKON_1 = 1; // 减少积分
  
  private final static int FRACTION_TYPE_0 = 0; // 登陆
  private final static int FRACTION_TYPE_1 = 1; // 学习资料
  private final static int FRACTION_TYPE_2 = 2; // 资料上传
  private final static int FRACTION_TYPE_3 = 3; // 资料下载
  private final static int FRACTION_TYPE_4 = 4; // 学习时长

  @Transactional
  public Object fractionReckon(IBaseService baseService,
      Map<String, Object> requestBody) throws Exception {
    FractionEntityBo fractionEntityBo = verifyData(requestBody);
    if (fractionEntityBo != null) {
      return fractionHandler(baseService, fractionEntityBo);
    } else {
      throw new GlobalErrorException(GlobalErrorEnum.PARAM_NOT_VALID.getCode(), "无效的参数类型");
    }
  }

  /**
   * 验证参数是否为空
   * 
   * @param headers
   * @param requestBody
   * @return
   * @throws GlobalErrorException
   */
  private FractionEntityBo verifyData(Map<String, Object> requestBody)
      throws GlobalErrorException {


		int fractionType = SyhzUtil.setDateInt(requestBody.get("fractionType"));
    ValidationUtils.notNull(fractionType, "积分获得类型不能为空");
    if(FRACTION_TYPE_0 == fractionType || FRACTION_TYPE_1 == fractionType || FRACTION_TYPE_2== fractionType 
        || FRACTION_TYPE_3== fractionType|| FRACTION_TYPE_4== fractionType) {
			if (FRACTION_TYPE_0 != fractionType) {
				String belongSys = SyhzUtil.setDate(requestBody.get("belongSys"));
				ValidationUtils.notNull(belongSys, "所属系统不能为空");

				String belongMode = SyhzUtil.setDate(requestBody.get("belongMode"));
				ValidationUtils.notNull(belongMode, "所属模块不能为空");

				String belongType = SyhzUtil.setDate(requestBody.get("belongType"));
				ValidationUtils.notNull(belongType, "所属类型不能为空");

				String tableId = SyhzUtil.setDate(requestBody.get("tableId")); // 对应表id
				ValidationUtils.notNull(tableId, "所属数据id不能为空");
			}
    } else {
      throw new GlobalErrorException(GlobalErrorEnum.PARAM_NOT_VALID.getCode(), "无效的的积分分类");
    }

		String branch = SyhzUtil.setDate(requestBody.get("branch"));// 单次增加分数
		ValidationUtils.notNull(branch, "单次增加积分数不能为空");

		String maxBranch = SyhzUtil.setDate(requestBody.get("maxBranch"));// 一天最多增加分数
		ValidationUtils.notNull(maxBranch, "最多增加积分数不能为空");

    int fractionReckon = SyhzUtil.setDateInt(requestBody.get("fractionReckon"));
    ValidationUtils.notNull(fractionReckon, "增减积分类型不能为空");
    if(FRACTION_RECKON_0 == fractionReckon || FRACTION_RECKON_0 == fractionReckon ) {
      
    } else {
      throw new GlobalErrorException(GlobalErrorEnum.PARAM_NOT_VALID.getCode(), "无效的增减积分类型");
    }

    // String fractionNumber = SyhzUtil.setDate(requestBody.get("fractionNumber"));
    // ValidationUtils.notNull(fractionNumber, "积分获得数量不能为空");

    String fractionTime = SyhzUtil.setDate(requestBody.get("fractionTime"));
    ValidationUtils.notNull(fractionTime, "积分获得时间不能为空");

    String fractionUserId = SyhzUtil.setDate(requestBody.get("fractionUserId"));
    ValidationUtils.notNull(fractionUserId, "积分获得人不能为空");

    String fractionUserName = SyhzUtil.setDate(requestBody.get("fractionUserName"));
    ValidationUtils.notNull(fractionUserName, "积分获得人姓名不能为空");

    String fractionAreaCode = SyhzUtil.setDate(requestBody.get("fractionAreaCode"));
    ValidationUtils.notNull(fractionAreaCode, "积分获得人部门所属区域不能为空");

    String fractionDeptCode = SyhzUtil.setDate(requestBody.get("fractionDeptCode"));
    ValidationUtils.notNull(fractionDeptCode, "积分获得人部门code不能为空");

    String fractionDeptName = SyhzUtil.setDate(requestBody.get("fractionDeptName"));
    ValidationUtils.notNull(fractionDeptName, "积分获得人部门名称不能为空");

    String creationId = SyhzUtil.setDate(requestBody.get("creationId"));
    ValidationUtils.notNull(creationId, "创建人id不能为空");

    String creationName = SyhzUtil.setDate(requestBody.get("creationName"));
    ValidationUtils.notNull(creationName, "创建人姓名不能为空");

    String remark = SyhzUtil.setDate(requestBody.get("remark"));
    ValidationUtils.notNull(remark, "积分获得备注不能为空");

    FractionEntityBo fractionEntityBo = FractionEntityBo.mapDataToBoData(requestBody);
    return fractionEntityBo;
  }

  /**
   * 根据不同方式来计算相关积分业务
   * 
   * @param baseService
   * @param fractionEntityBo
   * @return
   * @throws Exception
   */
  private Object fractionHandler(IBaseService baseService, FractionEntityBo fractionEntityBo)
      throws Exception {
    Object retId = "";
    int branchCount = 0;
    int fractionReckon = fractionEntityBo.getFractionReckon(); // 操作积分 0增加 1减少
    int fractionType = fractionEntityBo.getFractionType(); // 积分类型
		String userId = fractionEntityBo.getFractionUserId();
    Map<String, Object> queryMap = new HashMap<String, Object>();
    if (FRACTION_RECKON_0 == fractionReckon) { // 增加积分
      SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
      String dataStr = simpleDateFormat.format(new Date());
      queryMap.put("fractionReckon", FRACTION_RECKON_0); // 0增加
      queryMap.put("fractionTimeStart", dataStr + " 00:00:00"); // 今天开始时间
      queryMap.put("fractionTimeEnd", dataStr + " 23:59:59"); // 今天结束时间
      queryMap.put("fractionType", fractionType);
			queryMap.put("userId", userId);
//      optFractionType(queryMap, fractionType);
      // 查询今日相关类型获得的积分数量
      branchCount = getReckon(baseService, queryMap);
      // 业务处理是否需要增加积分 记录
      retId = fractionHandler(baseService, fractionEntityBo, branchCount);
    } else if (FRACTION_RECKON_1 == fractionReckon) { // 减少积分
      fractionEntityBo.setFractionReckon(FRACTION_RECKON_1);
      retId = reduceFractionHandler(baseService, fractionEntityBo);
    } else {
      throw new GlobalErrorException(GlobalErrorEnum.PARAM_NOT_VALID.getCode(), "无效的增减类型值");
    }
    return retId;
  }
  
  /**
   * 动态组装查询条件
   * @param queryMap
   * @param fractionType
   */
  private void optFractionType(Map<String, Object> queryMap, int fractionType) {
    if (FRACTION_TYPE_0 == fractionType) { // 登陆
      queryMap.put("fractionType", FRACTION_TYPE_0);
    } else if (FRACTION_TYPE_1 == fractionType) { // 学习资料
      queryMap.put("fractionType", FRACTION_TYPE_1);
    } else if (FRACTION_TYPE_2 == fractionType) { // 资料上传
      queryMap.put("fractionType", FRACTION_TYPE_2);
    } else if (FRACTION_TYPE_3 == fractionType) { // 资料下载
      queryMap.put("fractionType", FRACTION_TYPE_3);
    } else if (FRACTION_TYPE_4 == fractionType) { // 学习时长
      queryMap.put("fractionType", FRACTION_TYPE_4);
    } else {
      throw new GlobalErrorException(GlobalErrorEnum.PARAM_NOT_VALID.getCode(), "无效的的积分分类");
    }
  }

  /**
   * 增加积分 业务处理
   * 
   * @param baseService
   * @param fractionEntityBo
   * @param fractionType
   * @return
   * @throws Exception
   */
  private Object fractionHandler(IBaseService baseService, FractionEntityBo fractionEntityBo,
      int branchCount) throws Exception {
    int branch = fractionEntityBo.getBranch(); // 单次获得积分数
    int maxBranch = fractionEntityBo.getMaxBranch(); // 每天最多获得积分数
    if (branchCount < maxBranch) { // 需要增加积分记录
      fractionEntityBo.setFractionNumber(branch);
      return saveFractionLog(baseService, fractionEntityBo);
    }
		return "999";
  }

  /**
   * 减少积分 业务处理
   * 
   * @param baseService
   * @param fractionEntityBo
   * @param fractionType
   * @return
   * @throws Exception
   */
  private Object reduceFractionHandler(IBaseService baseService, FractionEntityBo fractionEntityBo)
      throws Exception {
    int branch = fractionEntityBo.getBranch(); // 单次积分数
    fractionEntityBo.setFractionNumber(branch);
    return saveFractionLog(baseService, fractionEntityBo);

  }

  /**
   * 查询当前类型 今天获得的 积分总和
   * 
   * @param baseService
   * @param queryMap
   * @return
   * @throws Exception
   */
  @SuppressWarnings("unchecked")
  private int getReckon(IBaseService baseService, Map<String, Object> queryMap) throws Exception {
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS_TRAIN_FRACTION_LOG_COUNT);
    Object trainFractionLogObj = baseService.get(queryMap);
    if (trainFractionLogObj != null) {
      Map<String, Object> trainFractionLogMap = (Map<String, Object>) trainFractionLogObj;
      return SyhzUtil.setDateInt(trainFractionLogMap.get("branchCount"));
    }
    return 0;
  }

  /**
   * 保存积分增加、减少 记录
   * 
   * @param baseService
   * @param fractionLogMap
   * @return
   * @throws Exception
   */
  private Object saveFractionLog(IBaseService baseService, FractionEntityBo fractionEntityBo)
      throws Exception {
    Map<String, Object> fractionLogMap = new HashMap<String, Object>();
    fractionLogMap.put("fractionType", fractionEntityBo.getFractionType());
    fractionLogMap.put("fractionReckon", fractionEntityBo.getFractionReckon());
    fractionLogMap.put("fractionNumber", fractionEntityBo.getFractionNumber());
    fractionLogMap.put("fractionTime", fractionEntityBo.getFractionTime());
    fractionLogMap.put("fractionUserId", fractionEntityBo.getFractionUserId());
    fractionLogMap.put("fractionUserName", fractionEntityBo.getFractionUserName());
    fractionLogMap.put("fractionAreaCode", fractionEntityBo.getFractionAreaCode());
    fractionLogMap.put("fractionDepCode", fractionEntityBo.getFractionDeptCode());
    fractionLogMap.put("fractionDepName", fractionEntityBo.getFractionDeptName());
    fractionLogMap.put("belongSys", fractionEntityBo.getBelongSys());
    fractionLogMap.put("belongMode", fractionEntityBo.getBelongMode());
    fractionLogMap.put("belongType", fractionEntityBo.getBelongType());
    fractionLogMap.put("tableId", fractionEntityBo.getTableId());
    fractionLogMap.put("creationId", fractionEntityBo.getCreationId());
    fractionLogMap.put("creationName", fractionEntityBo.getCreationName());
    fractionLogMap.put("remark", fractionEntityBo.getRemark());
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS_TRAIN_FRACTION_LOG);
    return baseService.save(fractionLogMap);
  }

}
