package com.douyuehan.doubao.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.douyuehan.doubao.model.entity.Activity;
import com.douyuehan.doubao.model.entity.ActivityUser;
import com.douyuehan.doubao.model.entity.SysUser;

import java.util.List;

public interface ActivityUserService extends IService<ActivityUser> {
    ActivityUser createOrder(SysUser user, Activity goods);
    ActivityUser getMiaoshaOrderByUserIdGoodsId(String userId, Long goodsId);
    List<ActivityUser> getOrderByActivity(Long id);

    int unsign(String id, String activityId);
}
