package com.douyuehan.doubao.service;

import com.douyuehan.doubao.model.entity.SysSensitive;

import java.util.List;

public interface SysSensitiveService {
    List<SysSensitive> getList();
    void add(String text);
}
