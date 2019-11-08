package com.nmghr.controller.caseAssist;

import com.nmghr.basic.common.Result;
import com.nmghr.basic.common.exception.GlobalErrorException;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.ISaveHandler;
import com.nmghr.basic.core.util.SpringUtils;
import com.sargeraswang.util.ExcelUtil.ExcelUtil;
import org.apache.commons.collections.IteratorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.util.*;

/**
 * 案件协查线索
 */
@SuppressWarnings("unchecked")
@RestController
@RequestMapping("/caseassistclue")
public class CaseAssistClueController {

  private static final Logger log = LoggerFactory.getLogger(CaseAssistClueController.class);
  @Autowired
  private IBaseService baseService;
  /**
   * 案件协查列表
   * @return
   */
  @PostMapping(value = "/upload")
  @ResponseBody
  public Object clueImport(@RequestParam("file") MultipartFile mulFile,
                           @RequestParam("type") Object type,
                           @RequestParam("userId") Object userId,
                           @RequestParam("userName") Object userName,
                           @RequestParam("category") Object category,
                           @RequestParam("curDeptCode") Object curDeptCode,
                           @RequestParam("curDeptName") Object curDeptName,
                           @RequestParam("assistId") Object assistId){
    try {
      if (null != mulFile) {
        Collection<Map> list = ExcelUtil.importExcel(Map.class, mulFile.getInputStream(), 0);
        if (!CollectionUtils.isEmpty(list)) {
          log.info("excel uploadFile file query size {}", list.size());
          if (list.size() > 1000) {
            log.error("excel uploadFile error, Maximum length exceeds 1000 ");
            throw new GlobalErrorException("99952", "最多不能超过1000条");
          }

          List<Map<String, Object>> params = IteratorUtils.toList(list.iterator());
//          List<LinkedHashMap<String, Object>> params = IteratorUtils.toList(list.iterator());
          if(params.size()>0){
            Map<String, Object> map = params.get(0);
//            LinkedHashMap<String, Object> map = params.get(0);
            List<String> keys = IteratorUtils.toList(map.keySet().iterator());
            StringBuilder err = new StringBuilder();
           if(!keys.contains("序号")){
             err.append("标题必须包含《序号》;");
           }
            if(!keys.contains("地址")){
              err.append("标题必须包含《地址》;");
            }
            if(err.length()>0){
             return Result.fail("999668", err.toString());
            }
          }
          Map<String, Object> data = new HashMap<>();
          data.put("type", type);
          data.put("category", category);
          data.put("userId", userId);
          data.put("userName", userName);
          data.put("curDeptCode", curDeptCode);
          data.put("curDeptName", curDeptName);
          data.put("assistId", assistId);
          data.put("list", params);
          ISaveHandler saveHandler = SpringUtils.getBean("qbxsSaveHandler", ISaveHandler.class);
          Object obj = saveHandler.save(data);
          return Result.ok(obj);
        } else {
          return Result.fail("99954", "上传文件为空");
        }
      } else {
        return Result.fail("99954", "上传文件为空");
      }
    } catch (IllegalArgumentException e) {
      log.error("excel uploadFile error", e.getMessage());
      return Result.fail("99951", "上传文件错误");
    } catch (FileNotFoundException e) {
      log.error("excel uploadFile error", e.getMessage());
      return Result.fail("99954", "上传文件为空");
    } catch (GlobalErrorException e) {
      return Result.fail("999668", e.getMessage());
    } catch (Exception e) {
      log.error("excel uploadFile error", e.getMessage());
    }
    return Result.fail("999669", "保存失败");
  }
  /**
   * 协查线索列表
   * @return
   */
  public Object clueList(){
    return null;
  }
  /**
   * 协查线索列表简版
   * @return
   */
  public Object simpleList(){
    return null;
  }
  /**
   * 线索分发时的线索列表
   * @return
   */
  public Object clues(){
    return null;
  }
  /**
   * 协查线索分发
   * @return
   */
  public Object arrange(){
    return null;
  }
  /**
   * 协查线索取消分发
   * @return
   */
  public Object cancelArrange(){
    return null;
  }

  /**
   * 协查线索取消分发
   * @return
   */
  public Object delClue(){
    return null;
  }

  /**
   * 案件协查线索详情
   * @return
   */
  public Object clueDetail(){
    return null;
  }

  /**
   * 案件协查情况统计
   * @return
   */
  public Object statistics(){
    return null;
  }

  /**
   * 线索协查战国反馈表
   * @return
   */
  public Object detailCount(){
    return null;
  }

  /**
   * 线索反馈
   * @return
   */
  public Object feedBack(){
    return null;
  }

}

