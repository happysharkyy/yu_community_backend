package com.douyuehan.doubao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.douyuehan.doubao.mapper.SysMenuMapper;
import com.douyuehan.doubao.mapper.SysNoticeMapper;
import com.douyuehan.doubao.model.entity.SysMenu;
import com.douyuehan.doubao.model.entity.SysNotice;
import com.douyuehan.doubao.model.entity.SysUser;
import com.douyuehan.doubao.service.IBmsPostService;
import com.douyuehan.doubao.service.IUmsUserService;
import com.douyuehan.doubao.service.SysMenuService;
import com.douyuehan.doubao.service.SysNoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SysNoticeServiceImpl extends ServiceImpl<SysNoticeMapper, SysNotice> implements SysNoticeService {
    @Autowired
    SysNoticeMapper sysNoticeMapper;
    @Autowired
    IUmsUserService iUmsUserService;
    @Autowired
    IBmsPostService iBmsPostService;
    @Override
    public List<SysNotice> findNotice(SysUser user) {
        LambdaQueryWrapper<SysNotice> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysNotice::getToId,user.getId());
        List<SysNotice> list = sysNoticeMapper.selectList(queryWrapper);
        for (SysNotice s:
             list) {
            SysNotice sysNotice = s;
            sysNotice.setIsRead(1);
            sysNoticeMapper.updateById(sysNotice);
            s.setFromUser(iUmsUserService.getById(s.getFromId()));
            s.setToUser(iUmsUserService.getById(s.getToId()));
            if(!s.getObjId().isEmpty()){
                s.setBmsPost(iBmsPostService.getById(s.getObjId()));
            }
        }
        return list;
    }

    @Override
    public void insert(SysNotice sysNotice) {
        sysNoticeMapper.insert(sysNotice);
    }

    @Override
    public int count(SysUser sysUser) {
        LambdaQueryWrapper<SysNotice> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysNotice::getToId,sysUser.getId());
        queryWrapper.eq(SysNotice::getIsRead,0);
        return sysNoticeMapper.selectCount(queryWrapper);
    }
}
