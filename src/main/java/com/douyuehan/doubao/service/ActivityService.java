package com.douyuehan.doubao.service;

import com.douyuehan.doubao.model.entity.Activity;

import java.util.List;

public interface ActivityService {
    boolean reduceStock(Activity goods);
    List<Activity> listGoodsVo();
    boolean checkStockCount(Long goodsId);
    Activity getGoodsVoByGoodsId(Long goodsId);
}
