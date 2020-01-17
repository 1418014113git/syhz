package com.nmghr.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;

@RestController
public class KnowledgeNameQueryController {
  @Autowired
  IBaseService baseService;
  private static String ALIAS_TRAININDUSTRYINFOBYTITLE = "TRAININDUSTRYINFOBYTITLE";
  private static String ALIAS_TRAINLAWINFOBYTITLE = "TRAINLAWINFOBYTITLE";
  private static String ALIAS_TRAINSTANDARDINFOBYTITLE = "TRAINSTANDARDINFOBYTITLE";
  private static String ALIAS_TRAINCASEINFOBYTITLE = "TRAINCASEINFOBYTITLE";

  @PostMapping("/trainindustryinfobytitle")
  @ResponseBody
  public Object queryNameindustryinfo(@RequestBody Map<String, Object> map) throws Exception {
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS_TRAININDUSTRYINFOBYTITLE);
    return baseService.list(map);
  }

  @PostMapping("/trainlawinfobytitle")
  @ResponseBody
  public Object queryNametrainlawinfobytitle(@RequestBody Map<String, Object> map) throws Exception {
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS_TRAINLAWINFOBYTITLE);
    return baseService.list(map);
  }

  @PostMapping("/trainstandardinfobytitle")
  @ResponseBody
  public Object queryNametrainstandardinfobytitle(@RequestBody Map<String, Object> map) throws Exception {
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS_TRAINSTANDARDINFOBYTITLE);
    return baseService.list(map);
  }

  @PostMapping("/traincaseinfobytitle")
  @ResponseBody
  public Object queryNametraincaseinfobytitle(@RequestBody Map<String, Object> map) throws Exception {
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS_TRAINCASEINFOBYTITLE);
    return baseService.list(map);
  }

  @PostMapping("/traincourseonly")
  @ResponseBody
  public Object queryNametraincoursebytitle(@RequestBody Map<String, Object> map) throws Exception {
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "TRAINCOURSEONLY");
    return baseService.list(map);
  }

  @PostMapping("/knowledgeenclosurebyname")
  @ResponseBody
  public Object knowledgeenclosurebyname(@RequestBody Map<String, Object> map) throws Exception {
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "KNOWLEDGEENCLOSUREBYNAME");
    return baseService.list(map);
  }

}
