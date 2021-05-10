package com.douyuehan.doubao.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.douyuehan.doubao.common.api.PageRequest;
import com.douyuehan.doubao.common.api.PageResult;
import com.douyuehan.doubao.model.entity.BmsFollow;

import java.security.Principal;


public interface IBmsFollowService extends IService<BmsFollow> {
    PageResult findPage(PageRequest pageRequest);

    PageResult findPageOwn(Principal principal , PageRequest pageRequest);
}
