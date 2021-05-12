package com.douyuehan.doubao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.douyuehan.doubao.common.api.ColumnFilter;
import com.douyuehan.doubao.common.api.PageRequest;
import com.douyuehan.doubao.common.api.PageResult;
import com.douyuehan.doubao.mapper.ActivityMapper;
import com.douyuehan.doubao.mapper.ActivityUserMapper;
import com.douyuehan.doubao.model.entity.Activity;
import com.douyuehan.doubao.model.entity.ActivityUser;
import com.douyuehan.doubao.model.entity.BmsPromotion;
import com.douyuehan.doubao.service.ActivityService;
import com.douyuehan.doubao.service.ActivityUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ActivityServiceImpl  extends ServiceImpl<ActivityMapper, Activity> implements ActivityService {
    @Autowired
    ActivityMapper activityMapper;
    @Autowired
    ActivityUserService activityUserService;
    @Autowired
    ActivityUserMapper activityUserMapper;


    public List<Activity> listGoodsVo(String userId) {
        List<Activity> list = activityMapper.selectList(new LambdaQueryWrapper<>());
        for (Activity activity:
             list) {
            List<ActivityUser> list1 = activityUserService.getOrderByActivity(activity.getId());
            for (ActivityUser a:
                 list1) {
                if(a.getUserId().equals(userId)){
                    activity.setStatusDetail("已报名");
                    break;
                }
            }
        }
        return list;
    }

    public Activity getGoodsVoByGoodsId(Long goodsId) {
        Activity activity = activityMapper.selectById(goodsId);
        List<ActivityUser> list = activityUserService.getOrderByActivity(activity.getId());
        activity.setList(list);
        return activity;
    }

    @Override
    public PageResult findPage(PageRequest pageRequest) {
        ColumnFilter columnFilter = pageRequest.getColumnFilters().get("title");
        LambdaQueryWrapper<Activity> wrapper = new LambdaQueryWrapper<>();
        if (columnFilter != null && !StringUtils.isEmpty(columnFilter.getValue())) {
            wrapper.eq(Activity::getTitle, columnFilter.getValue());
        }
        int pageNum = pageRequest.getPageNum();
        int pageSize = pageRequest.getPageSize();
        Page<Activity> page = new Page<>(pageNum, pageSize);
        IPage<Activity> result = this.baseMapper.selectPage(page, wrapper);
        PageResult pageResult = new PageResult(result);
        return pageResult;
    }

    @Override
    public List<Activity> listGoods() {
        return activityMapper.selectList(new LambdaQueryWrapper<>());
    }

    @Override
    public List<Activity> getListByUser(String id) {
        LambdaQueryWrapper<ActivityUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ActivityUser::getUserId,id);
        List<ActivityUser> list2 = activityUserMapper.selectList(queryWrapper);
        List<Long> params = list2.stream().map(ActivityUser::getActivityId).collect(Collectors.toList());
        List<Activity> list = activityMapper.selectBatchIds(params);
        for (Activity activity:
                list) {
            List<ActivityUser> list1 = activityUserService.getOrderByActivity(activity.getId());
            for (ActivityUser a:
                    list1) {
                if(a.getUserId().equals(id)){
                    activity.setStatusDetail("已报名");
                    break;
                }
            }
        }
        return list;
    }

    /**
     * 判断库存
     * @param goodsId 商品id
     * @return 是否能够下单
     */
    public boolean checkStockCount(Long goodsId) {
        Activity goodsVo = this.getGoodsVoByGoodsId(goodsId);
        if (null != goodsVo) {
            Integer stockCount = goodsVo.getStock();
            return stockCount > 0;
        }
        return false;
    }

    /**
     * 库存 -1
     * @param goods 秒杀商品
     */
    public boolean reduceStock(Activity goods) {
        goods.setStock(goods.getStock()-1);
        int i = activityMapper.updateById(goods);
        return i > 0;
    }
}
