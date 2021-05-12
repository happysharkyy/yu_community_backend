package com.douyuehan.doubao.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.douyuehan.doubao.common.api.PageRequest;
import com.douyuehan.doubao.common.api.PageResult;
import com.douyuehan.doubao.model.entity.Activity;
import com.douyuehan.doubao.model.entity.Behavior;

import java.util.List;

public interface ActivityService extends IService<Activity> {
    boolean reduceStock(Activity goods);
    List<Activity> listGoodsVo(String userId);
    boolean checkStockCount(Long goodsId);
    Activity getGoodsVoByGoodsId(Long goodsId);
    PageResult findPage(PageRequest pageRequest);
    List<Activity> listGoods();

    List<Activity> getListByUser(String id);
}
