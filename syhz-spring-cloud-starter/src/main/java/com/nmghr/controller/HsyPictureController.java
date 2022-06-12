package com.nmghr.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;

/**
 * 食药环一张图
 * 
 * @author heijiantao
 * @date 2020年1月15日
 * @version 1.0
 */
@RestController
@RequestMapping("/HsyPicture")
public class HsyPictureController {
  @Autowired
  @Qualifier("baseService")
  private IBaseService baseService;

  @GetMapping(value = "/list")
  @ResponseBody
  public Object upload(@RequestParam Map<String, Object> params) throws Exception {
    Map<String, Object> map = new HashMap<String, Object>();
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "HSYPICTUREAJ");
    List<Map<String, Object>> ajlist = (List<Map<String, Object>>) baseService.list(params);
    map.put("aj", ajlist);// 案件
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "HSYPICTUREDW");
    List<Map<String, Object>> dwlist = (List<Map<String, Object>>) baseService.list(params);
    map.put("dw", dwlist);// 单位
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "HSYPICTURERY");
    List<Map<String, Object>> rylist = (List<Map<String, Object>>) baseService.list(params);
    map.put("ry", rylist);// 人员
    return map;
  }
}
