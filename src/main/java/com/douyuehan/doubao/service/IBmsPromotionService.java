package com.douyuehan.doubao.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.douyuehan.doubao.common.api.PageRequest;
import com.douyuehan.doubao.common.api.PageResult;
import com.douyuehan.doubao.model.entity.BmsPromotion;


public interface IBmsPromotionService extends IService<BmsPromotion> {
    PageResult findPage(PageRequest pageRequest);
}
