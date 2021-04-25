package com.douyuehan.doubao.service;

import com.douyuehan.doubao.model.entity.Activity;
import com.douyuehan.doubao.model.entity.ActivityUser;
import com.douyuehan.doubao.model.entity.SysUser;

public interface ActivityUserService {
    ActivityUser createOrder(SysUser user, Activity goods);
    ActivityUser getMiaoshaOrderByUserIdGoodsId(String userId, Long goodsId);
}
