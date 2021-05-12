package com.douyuehan.doubao.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.douyuehan.doubao.model.entity.SysMenu;
import com.douyuehan.doubao.model.entity.SysNotice;
import com.douyuehan.doubao.model.entity.SysUser;

import java.util.List;

public interface SysNoticeService  extends IService<SysNotice> {
    List<SysNotice> findNotice(SysUser user);
    void insert(SysNotice sysNotice);
    int count(SysUser sysUser);
    List<SysNotice> findDomain(SysUser user);
}
