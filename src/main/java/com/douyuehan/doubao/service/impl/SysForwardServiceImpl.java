package com.douyuehan.doubao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.douyuehan.doubao.mapper.SysForwardMapper;
import com.douyuehan.doubao.model.dto.SysForwardDTO;
import com.douyuehan.doubao.model.entity.Behavior;
import com.douyuehan.doubao.model.entity.SysForward;
import com.douyuehan.doubao.service.IBehaviorService;
import com.douyuehan.doubao.service.IBehaviorUserLogService;
import com.douyuehan.doubao.service.SysForwardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class SysForwardServiceImpl extends ServiceImpl<SysForwardMapper, SysForward> implements SysForwardService {
    @Autowired
    IBehaviorService iBehaviorService;
    @Autowired
    IBehaviorUserLogService iBehaviorUserLogService;
    @Autowired
    SysForwardMapper sysForwardMapper;
    @Override
    public List<SysForward> select(String objId, String Type) {
        LambdaQueryWrapper<SysForward> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysForward::getObjId, objId);
        wrapper.eq(SysForward::getType, Type);
        wrapper.eq(SysForward::getForwardStatus, 0);
        List<SysForward> SysForwards = sysForwardMapper.selectList(wrapper);
        return SysForwards;
    }

    @Override
    public int save_Forward(SysForwardDTO dto) {
        SysForward sysForward = new SysForward();
        BeanUtils.copyProperties(dto,sysForward);
        sysForward.setModifyTime(new Date());
        sysForward.setCreateTime(new Date());
        sysForward.setId(0);
        //用户转发帖子 更新权重
        Behavior behavior = new Behavior(dto.getUserId(),dto.getObjId(),new Date(),
                iBehaviorUserLogService.getWeightByType("forward")+iBehaviorService.getByBehaviorType(dto.getUserId(),dto.getObjId())
                        .getBehaviorType (),iBehaviorService.getByBehaviorType(dto.getUserId(),dto.getObjId())
                .getCount()+1);
        LambdaQueryWrapper<Behavior> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Behavior::getUserId,dto.getUserId());
        queryWrapper.eq(Behavior::getPostId,dto.getObjId());
        iBehaviorService.update(behavior,queryWrapper);
        return sysForwardMapper.insert(sysForward);
    }
}
