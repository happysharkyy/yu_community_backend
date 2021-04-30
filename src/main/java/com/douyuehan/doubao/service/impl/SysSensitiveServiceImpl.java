package com.douyuehan.doubao.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.douyuehan.doubao.mapper.SysSensitiveMapper;
import com.douyuehan.doubao.mapper.SysStarMapper;
import com.douyuehan.doubao.model.entity.SysSensitive;
import com.douyuehan.doubao.model.entity.SysStar;
import com.douyuehan.doubao.service.SysSensitiveService;
import com.douyuehan.doubao.service.SysStarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class SysSensitiveServiceImpl  extends ServiceImpl<SysSensitiveMapper, SysSensitive> implements SysSensitiveService {
    @Autowired
    SysSensitiveMapper sysSensitiveMapper;
    @Override
    public List<SysSensitive> getList() {
        return sysSensitiveMapper.selectList(null);
    }

    @Override
    public void add(String text) {
        SysSensitive sysSensitive = new SysSensitive();
        sysSensitive.setId(0);
        sysSensitive.setText(text);
        sysSensitiveMapper.insert(sysSensitive);
    }
}
