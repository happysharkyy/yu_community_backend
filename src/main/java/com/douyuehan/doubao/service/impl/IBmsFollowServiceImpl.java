package com.douyuehan.doubao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.douyuehan.doubao.common.api.ColumnFilter;
import com.douyuehan.doubao.common.api.PageRequest;
import com.douyuehan.doubao.common.api.PageResult;
import com.douyuehan.doubao.mapper.BmsFollowMapper;
import com.douyuehan.doubao.model.entity.Activity;
import com.douyuehan.doubao.model.entity.BmsFollow;
import com.douyuehan.doubao.model.entity.SysUser;
import com.douyuehan.doubao.service.IBmsFollowService;
import com.douyuehan.doubao.service.IUmsUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;


@Service
public class IBmsFollowServiceImpl extends ServiceImpl<BmsFollowMapper, BmsFollow> implements IBmsFollowService {
    @Autowired
    IUmsUserService iUmsUserService;
    @Override
    public PageResult findPage(PageRequest pageRequest) {
        ColumnFilter columnFilter = pageRequest.getColumnFilters().get("toId");
        LambdaQueryWrapper<BmsFollow> wrapper = new LambdaQueryWrapper<>();
        if (columnFilter != null && !StringUtils.isEmpty(columnFilter.getValue())) {
            wrapper.eq(BmsFollow::getFollowerId, columnFilter.getValue());
        }
        int pageNum = pageRequest.getPageNum();
        int pageSize = pageRequest.getPageSize();
        Page<BmsFollow> page = new Page<>(pageNum, pageSize);
        IPage<BmsFollow> result = this.baseMapper.selectPage(page, wrapper);
        for (BmsFollow b:
             result.getRecords()) {
            SysUser sysUser = iUmsUserService.getById(b.getParentId());
            b.setFromUserName(sysUser.getUsername());
            SysUser sysUser1 = iUmsUserService.getById(b.getFollowerId());
            b.setToUserName(sysUser1.getUsername());
        }
        PageResult pageResult = new PageResult(result);
        return pageResult;
    }
}
