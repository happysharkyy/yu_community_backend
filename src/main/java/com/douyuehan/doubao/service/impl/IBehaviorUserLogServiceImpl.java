package com.douyuehan.doubao.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.douyuehan.doubao.mapper.IBehaviorSimilarityMapper;
import com.douyuehan.doubao.mapper.IBehaviorUserLogMapper;
import com.douyuehan.doubao.model.entity.BehaviorSimilarity;
import com.douyuehan.doubao.model.entity.BehaviorUserLog;
import com.douyuehan.doubao.service.IBehaviorSimilarityService;
import com.douyuehan.doubao.service.IBehaviorUserLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IBehaviorUserLogServiceImpl extends ServiceImpl<IBehaviorUserLogMapper, BehaviorUserLog> implements IBehaviorUserLogService {
    @Autowired
    IBehaviorUserLogMapper iBehaviorUserLogMapper;
    @Override
    public Double getWeightByType(String type) {
        return iBehaviorUserLogMapper.selectById(type).getWeight();
    }
}
