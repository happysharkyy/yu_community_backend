package com.douyuehan.doubao.service;

import com.douyuehan.doubao.common.api.PageRequest;
import com.douyuehan.doubao.common.api.PageResult;
import com.douyuehan.doubao.model.entity.SysSensitive;

import java.util.List;

public interface SysSensitiveService {
    List<SysSensitive> getList();
    void add(String text);

    PageResult findPage(PageRequest pageRequest);
}
