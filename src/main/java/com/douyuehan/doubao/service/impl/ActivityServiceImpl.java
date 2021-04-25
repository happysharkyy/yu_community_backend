package com.douyuehan.doubao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.douyuehan.doubao.mapper.ActivityMapper;
import com.douyuehan.doubao.model.entity.Activity;
import com.douyuehan.doubao.model.entity.ActivityUser;
import com.douyuehan.doubao.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActivityServiceImpl  extends ServiceImpl<ActivityMapper, Activity> implements ActivityService {
    @Autowired
    ActivityMapper activityMapper;

    public List<Activity> listGoodsVo() {
        return activityMapper.selectList(new LambdaQueryWrapper<>());
    }

    public Activity getGoodsVoByGoodsId(Long goodsId) {
        return activityMapper.selectById(goodsId);
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
