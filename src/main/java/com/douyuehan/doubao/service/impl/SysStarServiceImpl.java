package com.douyuehan.doubao.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.douyuehan.doubao.mapper.SysStarMapper;
import com.douyuehan.doubao.model.dto.SysStarDTO;
import com.douyuehan.doubao.model.entity.Behavior;
import com.douyuehan.doubao.model.entity.BmsComment;
import com.douyuehan.doubao.model.entity.SysNotice;
import com.douyuehan.doubao.model.entity.SysStar;
import com.douyuehan.doubao.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
@Slf4j
@Service
public class SysStarServiceImpl  extends ServiceImpl<SysStarMapper, SysStar> implements SysStarService {
    @Autowired
    IBehaviorService iBehaviorService;
    @Autowired
    IBehaviorUserLogService iBehaviorUserLogService;
    @Autowired
    SysStarMapper sysStarMapper;
    @Autowired
    SysNoticeService sysNoticeService;
    @Autowired
    IBmsPostService iBmsPostService;
    @Override
    public List<SysStar> select(String objId, String Type) {
        LambdaQueryWrapper<SysStar> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysStar::getObjId, objId);
        wrapper.eq(SysStar::getType, Type);
        wrapper.eq(SysStar::getStarStatus, 0);
        List<SysStar> sysStars = sysStarMapper.selectList(wrapper);
        return sysStars;
    }

    @Override
    public int save_star(SysStarDTO dto) {
        LambdaQueryWrapper<SysStar> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysStar::getObjId, dto.getObjId());
        wrapper.eq(SysStar::getType, dto.getType());
        wrapper.eq(SysStar::getUserId, dto.getUserId());
//        wrapper.eq(SysStar::getStarStatus, dto.getStarStatus());
        SysStar sysStar = new SysStar();
        BeanUtils.copyProperties(dto,sysStar);
        sysStar.setModifyTime(new Date());
        if(sysStarMapper.selectList(wrapper).size()==0){
            sysStar.setCreateTime(new Date());
            sysStar.setId(0);
            sysStarMapper.insert(sysStar);
        }else {
            sysStarMapper.update(sysStar,wrapper);
        }
        SysNotice sysNotice = new SysNotice();
        sysNotice.setId(0);
        sysNotice.setOperation("点赞");
        sysNotice.setCreateTime(new Date());
        sysNotice.setObjId(dto.getObjId());
        sysNotice.setObjType("帖子");
        sysNotice.setFromId(dto.getUserId());
        sysNotice.setToId(iBmsPostService.getById(dto.getObjId()).getUserId());
        sysNoticeService.insert(sysNotice);

        if(!ObjectUtil.isEmpty(iBehaviorService.getByBehaviorType(dto.getUserId(),dto.getObjId()))) {
            //用户点赞帖子 更新权重
            if (dto.getStarStatus() == 0) {
                Behavior behavior = new Behavior(dto.getUserId(), dto.getObjId(), new Date(),
                        iBehaviorUserLogService.getWeightByType("star") + iBehaviorService.getByBehaviorType(dto.getUserId(), dto.getObjId())
                                .getBehaviorType(), iBehaviorService.getByBehaviorType(dto.getUserId(),dto.getObjId())
                        .getCount()+1);
                LambdaQueryWrapper<Behavior> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(Behavior::getUserId, dto.getUserId());
                queryWrapper.eq(Behavior::getPostId, dto.getObjId());
                iBehaviorService.update(behavior, queryWrapper);
            } else {
                Behavior behavior = new Behavior(dto.getUserId(), dto.getObjId(), new Date(),
                        iBehaviorUserLogService.getWeightByType("star") - iBehaviorService.getByBehaviorType(dto.getUserId(), dto.getObjId())
                                .getBehaviorType(), iBehaviorService.getByBehaviorType(dto.getUserId(),dto.getObjId())
                        .getCount()-1);
                LambdaQueryWrapper<Behavior> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(Behavior::getUserId, dto.getUserId());
                queryWrapper.eq(Behavior::getPostId, dto.getObjId());
                iBehaviorService.update(behavior, queryWrapper);
            }
        }

        return 1;
    }
    @Override
    public int getTodayAddStar() {
        LambdaQueryWrapper<SysStar> queryWrapper = new LambdaQueryWrapper<>();
        SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");
        java.util.Calendar calendar=java.util.Calendar.getInstance();
        calendar.roll(java.util.Calendar.DAY_OF_YEAR,-1);
        String last = df.format(calendar.getTime());
        calendar.roll(java.util.Calendar.DAY_OF_YEAR,1);
        String next = df.format(calendar.getTime());
        queryWrapper.ge(SysStar::getCreateTime, last).apply("DATE_FORMAT(create_time,'%Y-%m-%d') <= DATE_FORMAT({0},'%Y-%m-%d')", next);
        return this.baseMapper.selectCount(queryWrapper);
    }
}
