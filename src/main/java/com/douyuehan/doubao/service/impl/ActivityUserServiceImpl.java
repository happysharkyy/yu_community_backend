package com.douyuehan.doubao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.douyuehan.doubao.mapper.ActivityUserMapper;
import com.douyuehan.doubao.model.entity.Activity;
import com.douyuehan.doubao.model.entity.ActivityUser;
import com.douyuehan.doubao.model.entity.SysUser;
import com.douyuehan.doubao.redis.GoodsKey;
import com.douyuehan.doubao.redis.OrderKey;
import com.douyuehan.doubao.redis.RedisService;
import com.douyuehan.doubao.service.ActivityService;
import com.douyuehan.doubao.service.ActivityUserService;
import com.douyuehan.doubao.service.IUmsUserService;
import com.douyuehan.doubao.utils.BeanUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ActivityUserServiceImpl extends ServiceImpl<ActivityUserMapper, ActivityUser> implements ActivityUserService {
    @Autowired
    ActivityUserMapper activityUserMapper;
    @Autowired
    private RedisService redisService;
    @Autowired
    IUmsUserService iUmsUserService;
    @Autowired
    ActivityService activityService;
    @Override
    public ActivityUser createOrder(SysUser user, Activity goods) {

        // 下订单
        ActivityUser orderInfo = new ActivityUser();
        orderInfo.setCreateTime(new Date());
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

    @Override
    public List<ActivityUser> getOrderByActivity(Long id) {
        LambdaQueryWrapper<ActivityUser> queryWrapper =new LambdaQueryWrapper<>();
        queryWrapper.eq(ActivityUser::getActivityId,id);
        List<ActivityUser> list = activityUserMapper.selectList(queryWrapper);
        List<ActivityUser> result = new ArrayList<>();

        for (ActivityUser a:list) {
            ActivityUser activityUser = new ActivityUser();
            BeanUtils.copyProperties(a,activityUser);
            activityUser.setSysUser(iUmsUserService.getById(a.getUserId()));
            result.add(activityUser);
        }
        return result;
    }

    @Override
    public int unsign(String id, String activityId) {


        redisService.delete(OrderKey.getMiaoshaOrderByUidGid, ""+id+"_"+activityId);
        LambdaQueryWrapper<ActivityUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ActivityUser::getUserId,id);
        queryWrapper.eq(ActivityUser::getActivityId,activityId);
        //更新库存
        Activity activity = activityService.getById(activityId);
        activity.setStock(activity.getStock()+1);
        redisService.set(GoodsKey.getMiaoshaGoodsStock, ""+activityId, activity.getStock()+1);
        activityService.updateById(activity);
        return activityUserMapper.delete(queryWrapper);
    }
}
