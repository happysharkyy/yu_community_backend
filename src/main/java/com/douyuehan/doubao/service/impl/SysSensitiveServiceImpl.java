package com.douyuehan.doubao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.douyuehan.doubao.common.api.ColumnFilter;
import com.douyuehan.doubao.common.api.PageRequest;
import com.douyuehan.doubao.common.api.PageResult;
import com.douyuehan.doubao.mapper.SysSensitiveMapper;
import com.douyuehan.doubao.mapper.SysStarMapper;
import com.douyuehan.doubao.model.entity.Activity;
import com.douyuehan.doubao.model.entity.SysSensitive;
import com.douyuehan.doubao.model.entity.SysStar;
import com.douyuehan.doubao.service.SysSensitiveService;
import com.douyuehan.doubao.service.SysStarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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

    @Override
    public PageResult findPage(PageRequest pageRequest) {
        ColumnFilter columnFilter = pageRequest.getColumnFilters().get("title");
        LambdaQueryWrapper<SysSensitive> wrapper = new LambdaQueryWrapper<>();
        if (columnFilter != null && !StringUtils.isEmpty(columnFilter.getValue())) {
            wrapper.eq(SysSensitive::getText, columnFilter.getValue());
        }
        int pageNum = pageRequest.getPageNum();
        int pageSize = pageRequest.getPageSize();
        Page<SysSensitive> page = new Page<>(pageNum, pageSize);
        IPage<SysSensitive> result = this.baseMapper.selectPage(page, wrapper);
        PageResult pageResult = new PageResult(result);
        return pageResult;
    }
}
