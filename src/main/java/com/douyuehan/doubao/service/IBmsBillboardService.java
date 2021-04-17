package com.douyuehan.doubao.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.douyuehan.doubao.common.api.PageRequest;
import com.douyuehan.doubao.common.api.PageResult;
import com.douyuehan.doubao.model.entity.BmsBillboard;

public interface IBmsBillboardService extends IService<BmsBillboard> {
    PageResult findPage(PageRequest pageRequest);
}
