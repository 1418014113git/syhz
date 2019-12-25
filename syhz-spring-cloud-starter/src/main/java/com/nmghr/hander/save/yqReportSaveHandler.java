/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.nmghr.hander.save;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.nmghr.basic.common.Constant;
import com.nmghr.basic.common.exception.GlobalErrorEnum;
import com.nmghr.basic.common.exception.GlobalErrorException;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractSaveHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * <功能描述/>
 *
 * @author weber
 * @date 2018年7月26日 下午3:46:31
 * @version 1.0
 */
@Service("yqreportSaveHandler")
public class yqReportSaveHandler extends AbstractSaveHandler {

  public yqReportSaveHandler(IBaseService baseService) {
    super(baseService);
  }

  /**
   * 增加舆情报告
   */
  @Override
  @Transactional
  public Object save(Map<String, Object> requestBody) throws Exception {
    // 添加主表

    String attachment = String.valueOf(requestBody.get("attachment"));
    if(attachment!=null && !"".equals(attachment)){
      JSONArray attachments = JSONArray.parseArray(attachment);
      try {
      for (int i = 0; i < attachments.size(); i++) {

          JSONObject jsonObject = attachments.getJSONObject(i);
          String path = String.valueOf(jsonObject.get("path"));
          String name = String.valueOf(jsonObject.get("name"));
          String category = String.valueOf(requestBody.get("category"));
          if ("1".equals(category)) {
            //日报
            String shortName = name.substring(0,name.lastIndexOf("."));

            if("".equals(shortName) || shortName.length() != 14 ){
              throw new GlobalErrorException(GlobalErrorEnum.PARAM_NOT_VALID.getCode(), "报告保存失败，请确认文件名是否正确");
            }
            String title = name.substring(0, 8);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            Date time = sdf.parse(title);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(time);
            calendar.add(calendar.DATE, -1);
            Date newTime = calendar.getTime();
            title = sdf.format(newTime);
            String titleOK = title.substring(0, 4) + "." + title.substring(4, 6) + "." + title.substring(6, 8) + "日报";
            Date createTime = sdf.parse(name.substring(0, 8));
            requestBody.put("title", titleOK);
            requestBody.put("createTime", createTime);
            requestBody.put("fileName", name);
            requestBody.put("attachment", jsonObject.toJSONString());
            LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "YQREPORT");
            baseService.save(requestBody);
          }
          if ("2".equals(category)) {
            String shortName = name.substring(0,name.lastIndexOf("."));
            if("".equals(shortName) || shortName.length() != 29 || !shortName.contains("-") ){
              throw new GlobalErrorException(GlobalErrorEnum.PARAM_NOT_VALID.getCode(), "报告保存失败，请确认文件名是否正确");
            }
            //周报
            String[] time = name.split("-");
            String start = time[0].substring(0, 4) + "." + time[0].substring(4, 6) + "." + time[0].substring(6, 8);
            String end = time[1].substring(0, 8);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            Date endTime = sdf.parse(end);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(endTime);
            calendar.add(calendar.DATE, -1);
            Date newTime = calendar.getTime();
            end = sdf.format(newTime);
            end = end.substring(0, 4) + "." + end.substring(4, 6) + "." + end.substring(6, 8);
            String weekTitleOK = start + "-" + end + "周报";
            Date createTime = sdf.parse(time[1].substring(0, 8));
            requestBody.put("title", weekTitleOK);
            requestBody.put("createTime", createTime);
            requestBody.put("fileName", name);
            requestBody.put("attachment", jsonObject.toJSONString());
            LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "YQREPORT");
            baseService.save(requestBody);

          }
          if ("3".equals(category)) {
            //月报
            String shortName = name.substring(0,name.lastIndexOf("."));
            if("".equals(shortName) || shortName.length() != 17 || !shortName.contains("-") ){
              throw new GlobalErrorException(GlobalErrorEnum.PARAM_NOT_VALID.getCode(), "报告保存失败，请确认文件名是否正确");
            }
            String[] time = name.split("-");
            String start = time[0];
            String title = start.substring(0, 4) + "." + start.substring(4, 6) + "月报";
            String end = time[1].substring(0, 8);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            Date createTime = sdf.parse(time[1].substring(0, 8));
            requestBody.put("title", title);
            requestBody.put("createTime", createTime);
            requestBody.put("fileName", name);
            requestBody.put("attachment", jsonObject.toJSONString());
            LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "YQREPORT");
            baseService.save(requestBody);
          }
        }


      }
      catch (Exception e){
        throw new GlobalErrorException(GlobalErrorEnum.PARAM_NOT_VALID.getCode(), "报告保存失败，请确认文件名是否正确");
      }
    }

    return null;


  }
}
