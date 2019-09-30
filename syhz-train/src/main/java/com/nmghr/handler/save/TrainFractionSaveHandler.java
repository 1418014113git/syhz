/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nmghr.handler.save;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.common.exception.GlobalErrorEnum;
import com.nmghr.basic.common.exception.GlobalErrorException;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractSaveHandler;
import com.nmghr.basic.core.util.ValidationUtils;
import com.nmghr.handler.service.TrainFractionService;
import com.nmghr.util.SyhzUtil;

/**
 * 积分请求API业务处理
 *
 * @author kaven
 * @date 2019年9月26日 下午7:28:13
 * @version 1.0
 */
@Service("trainFractionSaveHandler")
public class TrainFractionSaveHandler extends AbstractSaveHandler {
	@Autowired
	private TrainFractionService trainFractionService;

	private static final String ALIAS_TRAIN_FRACTION_LOG = "trainFractionLogCount"; // 积分记录明细表

	private final static int FRACTION_RECKON_0 = 0; // 增加积分
	private final static int FRACTION_RECKON_1 = 1; // 减少积分

	private final static int FRACTION_TYPE_0 = 0; // 登陆
	private final static int FRACTION_TYPE_1 = 1; // 学习资料
	private final static int FRACTION_TYPE_2 = 2; // 资料上传
	private final static int FRACTION_TYPE_3 = 3; // 资料下载
	private final static int FRACTION_TYPE_4 = 4; // 学习时长

	public TrainFractionSaveHandler(IBaseService baseService) {
		super(baseService);
	}

	@Transactional
	public Object save(Map<String, Object> requestBody) throws Exception {
		verifyData(requestBody);

		int fractionType = SyhzUtil.setDateInt(requestBody.get("fractionType"));
		Map<String, Object> queryMap = new HashMap<String, Object>();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String dataStr = simpleDateFormat.format(new Date());
		int maxBranch = SyhzUtil.setDateInt(requestBody.get("maxBranch"));
		queryMap.put("fractionReckon", FRACTION_RECKON_0); // 0增加
		queryMap.put("fractionTimeStart", dataStr + " 00:00:00"); // 今天开始时间
		queryMap.put("fractionTimeEnd", dataStr + " 23:59:59"); // 今天结束时间
		queryMap.put("fractionType", fractionType);
		queryMap.put("belongSys", requestBody.get("belongSys"));
		queryMap.put("belongMode", requestBody.get("belongMode"));
		queryMap.put("tableId", requestBody.get("tableId"));
		int fractionCount = getReckon(queryMap);
		if (fractionCount == 0) { // 如果当前文章 今天没有增加积分 则发起增加积分服务器
			return trainFractionService.fractionReckon(baseService, requestBody);
		}
		if (fractionCount >= maxBranch) {
			return "999";
		}

		return "";
	}

	private void verifyData(Map<String, Object> requestBody) throws GlobalErrorException {
		int fractionType = SyhzUtil.setDateInt(requestBody.get("fractionType"));
		ValidationUtils.notNull(fractionType, "积分获得类型不能为空");
		if (FRACTION_TYPE_0 == fractionType || FRACTION_TYPE_1 == fractionType || FRACTION_TYPE_2 == fractionType
				|| FRACTION_TYPE_3 == fractionType || FRACTION_TYPE_4 == fractionType) {
			if (FRACTION_TYPE_0 != fractionType) {
				String belongSys = SyhzUtil.setDate(requestBody.get("belongMode"));
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
		if (FRACTION_RECKON_0 == fractionReckon || FRACTION_RECKON_0 == fractionReckon) {

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
	}

	/**
	 * 查询当前类型 今天文章是否获得了积分
	 * 
	 * @param baseService
	 * @param queryMap
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private int getReckon(Map<String, Object> queryMap) throws Exception {
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS_TRAIN_FRACTION_LOG);
		Object trainFractionLogObj = baseService.get(queryMap);
		if (trainFractionLogObj != null) {
			Map<String, Object> trainFractionLogMap = (Map<String, Object>) trainFractionLogObj;
			return SyhzUtil.setDateInt(trainFractionLogMap.get("branchCount"));
		}
		return 0;
	}

}
