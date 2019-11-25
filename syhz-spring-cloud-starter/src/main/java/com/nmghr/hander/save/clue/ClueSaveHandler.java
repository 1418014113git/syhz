package com.nmghr.hander.save.clue;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractSaveHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * 情报信息中线索管理
 */
@Service("clueSaveHandler")
public class ClueSaveHandler extends AbstractSaveHandler {

    public ClueSaveHandler(IBaseService baseService) {
        super(baseService);
    }

    @Override
    @Transactional
    public Object save(Map<String, Object> body) throws Exception {
        // 提交时间
        Date submitTime = new Date();
        body.put("submitTime", submitTime);
        // 生成线索编号，单位所在行政区划代码+提交时间年月日时分毫秒
        body.put("clueNumber", body.get("deptAreaCode") + new SimpleDateFormat("yyyyMMddHHmmssSSS").format(submitTime));
        body.put("dataStatus", 1);  // 数据状态
        body.put("shareStatus", 0); // 0原报，1分享
        body.put("submitPersonNumber", body.get("submitPersonNumber").toString()); // 警号
        body.put("collectionLocation", body.get("collectionLocation").toString()); // 采集地点行政区划
        body.put("collectionLocationLable", body.get("collectionLocationLable").toString()); // 采集地点行政区划名称
        body.put("clueSortId", body.get("clueSortId").toString()); // 线索分类字典表主键
        body.put("collectionTypeId", body.get("collectionTypeId").toString()); // 采集类型字典表主键
        body.put("clueTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(String.valueOf(body.get("clueTime")))); // 发生时间
        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "QBXXCLUE");
        return baseService.save(body);
    }
}
