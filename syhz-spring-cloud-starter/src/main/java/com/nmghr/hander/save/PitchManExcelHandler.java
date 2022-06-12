/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nmghr.hander.save;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.common.exception.GlobalErrorException;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractSaveHandler;
import com.nmghr.controller.vo.PitchManVO;

/**
 * <功能描述/>
 *
 * @author weber  
 * @date 2019年5月6日 下午3:00:39 
 * @version 1.0   
 */
@Service("pitchmanexcelHandler")
public class PitchManExcelHandler extends AbstractSaveHandler {

  private final Integer subSize = 100;
  private Logger log = LoggerFactory.getLogger(PitchManExcelHandler.class);
  
  public PitchManExcelHandler(IBaseService baseService) {
    super(baseService);
  }

  /**
   * 批量导入数据增加方法
   */
  @SuppressWarnings("unchecked")
  @Transactional
  @Override
  public Object save(Map<String, Object> requestBody) {
    ArrayList<PitchManVO> list = (ArrayList<PitchManVO>) requestBody.get("list");
    if(list == null) {
      return null;
    }
    if (list.size() > subSize) {
      do {
        if(list.size()>subSize) {
          List<PitchManVO> sub = list.subList(0, subSize);
          if (!saveData(requestBody, sub)) {
            throw new GlobalErrorException("9996", "提交数据有误");
          }
          list.removeAll(sub);
        } else {
          if (!saveData(requestBody, list)) {
            throw new GlobalErrorException("9996", "提交数据有误");
          }
          list.removeAll(list);
        }
      } while (list.size()>0);
      return "success";
    } else {
      return saveData(requestBody, list);
    }
  }
  
  /**
   * 批量处理增加数据
   * @param requestBody
   * @param list
   * @return
   */
  @SuppressWarnings("unchecked")
  private boolean saveData(Map<String, Object> requestBody, List<PitchManVO> list) {
    log.info("batch list size: "+ list.size());
    Long initId = null;
    Map<String, Object> params = new HashMap<String, Object>();
    params.put("num", list.size());
    params.put("seqName", "PITCHMANBATCH");
    try {
      //修改sequence 表自增id
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "SEQUENCEUPDATE");
      Map<String, Object> map = (Map<String, Object>) baseService.get(params);
      if (map==null) {
        return false;
      }
      //计算初始id
      initId = Long.parseLong(String.valueOf(map.get("id"))) - list.size();
    } catch (Exception e) {
      log.error("batch save get SEQUENCEUPDATE list Error: "+ e.getMessage());
      throw new GlobalErrorException("9996", "提交数据有误");
    }
    
    //拼装需要的参数
    String createDept = (String)requestBody.get("createDept");
    String createName = (String)requestBody.get("createName");
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String date = df.format(new Date());
    for (PitchManVO bean : list) {
      initId++;//增加ID
      bean.setId(String.valueOf(initId));
      bean.setCreateDate(date);
      bean.setCreateDept(createDept);
      bean.setCreateName(createName);
      bean.setSourceType(1);
    }

    params = new HashMap<String, Object>();
    params.put("list", list);
    try {
      //提交数据
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "PITCHMANBATCH");
      baseService.save(params);
      return true;
    } catch (Exception e) {
      log.error("batch save list Error: "+ e.getMessage());
      throw new GlobalErrorException("9996", "提交数据有误");
    }
  }
  
}
