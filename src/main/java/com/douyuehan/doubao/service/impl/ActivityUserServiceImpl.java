package com.douyuehan.doubao.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.douyuehan.doubao.mapper.ActivityUserMapper;
import com.douyuehan.doubao.model.entity.Activity;
import com.douyuehan.doubao.model.entity.ActivityUser;
import com.douyuehan.doubao.model.entity.SysUser;
import com.douyuehan.doubao.redis.OrderKey;
import com.douyuehan.doubao.redis.RedisService;
import com.douyuehan.doubao.service.ActivityUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;

@Service
public class ActivityUserServiceImpl extends ServiceImpl<ActivityUserMapper, ActivityUser> implements ActivityUserService {
    @Autowired
    ActivityUserMapper activityUserMapper;
    @Autowired
    private RedisService redisService;
    @Override
    public ActivityUser createOrder(SysUser user, Activity goods) {

        // 下订单
        ActivityUser orderInfo = new ActivityUser();
        orderInfo.setCreateTime(Date.from(Instant.now()));
        orderInfo.setActivityChannel("0");
        orderInfo.setActivityId(goods.getId());
        orderInfo.setUserId(user.getId());
        orderInfo.setIsDel(0);
        orderInfo.setUserId(user.getId());

        activityUserMapper.insert(orderInfo);

        redisService.set(OrderKey.getMiaoshaOrderByUidGid, ""+user.getId()+"_"+goods.getId(), orderInfo);

        System.out.println("报名成功. userId, goodsId"+user.getId()+goods.getId()+"_"+goods.getStock());
        return orderInfo;
    }

    @Override
    public ActivityUser getMiaoshaOrderByUserIdGoodsId(String userId, Long goodsId) {
        return redisService.get(OrderKey.getMiaoshaOrderByUidGid, ""+userId+"_"+goodsId, ActivityUser.class);
    }
}
