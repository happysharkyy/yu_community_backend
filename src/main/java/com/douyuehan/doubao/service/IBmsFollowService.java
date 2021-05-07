package com.douyuehan.doubao.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.douyuehan.doubao.common.api.PageRequest;
import com.douyuehan.doubao.common.api.PageResult;
import com.douyuehan.doubao.model.entity.BmsFollow;


public interface IBmsFollowService extends IService<BmsFollow> {
    PageResult findPage(PageRequest pageRequest);
}
