package com.douyuehan.doubao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.douyuehan.doubao.common.api.ColumnFilter;
import com.douyuehan.doubao.common.api.PageRequest;
import com.douyuehan.doubao.common.api.PageResult;
import com.douyuehan.doubao.mapper.SysDictMapper;
import com.douyuehan.doubao.model.entity.SysDict;
import com.douyuehan.doubao.service.SysDictService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class SysDictServiceImpl extends ServiceImpl<SysDictMapper, SysDict> implements SysDictService {
    @Override
    public void delete(List<SysDict> records) {
        for (SysDict record : records) {
            removeById(record.getId());
        }
    }

    @Override
    public PageResult findPage(PageRequest pageRequest) {
        ColumnFilter columnFilter = pageRequest.getColumnFilters().get("label");
        LambdaQueryWrapper<SysDict> wrapper = new LambdaQueryWrapper<>();
        if (columnFilter != null && !StringUtils.isEmpty(columnFilter.getValue())) {
            wrapper.eq(SysDict::getLabel, columnFilter.getValue());
        }
        int pageNum = pageRequest.getPageNum();
        int pageSize = pageRequest.getPageSize();
        Page<SysDict> page = new Page<>(pageNum, pageSize);
        IPage<SysDict> result = baseMapper.selectPage(page, wrapper);
        PageResult pageResult = new PageResult(result);
        return pageResult;
    }

    @Override
    public List<SysDict> findByLable(String lable) {
        LambdaQueryWrapper<SysDict> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDict::getLabel, lable);
        return baseMapper.selectList(wrapper);
    }
}
