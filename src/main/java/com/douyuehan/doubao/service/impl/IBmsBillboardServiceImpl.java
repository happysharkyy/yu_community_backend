package com.douyuehan.doubao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.douyuehan.doubao.common.api.ColumnFilter;
import com.douyuehan.doubao.common.api.PageRequest;
import com.douyuehan.doubao.common.api.PageResult;
import com.douyuehan.doubao.mapper.BmsBillboardMapper;
import com.douyuehan.doubao.model.entity.BmsBillboard;
import com.douyuehan.doubao.model.entity.BmsTag;
import com.douyuehan.doubao.model.entity.SysRole;
import com.douyuehan.doubao.model.entity.SysUser;
import com.douyuehan.doubao.service.IBmsBillboardService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class IBmsBillboardServiceImpl extends ServiceImpl<BmsBillboardMapper
        , BmsBillboard> implements IBmsBillboardService {


    public String getColumnFilterValue(PageRequest pageRequest, String filterName) {
        String value = null;
        ColumnFilter columnFilter = pageRequest.getColumnFilters().get(filterName);
        if(columnFilter != null) {
            value = columnFilter.getValue();
        }
        return value;
    }
    @Override
    public PageResult findPage(PageRequest pageRequest) {
        PageResult pageResult = null;
        String name = getColumnFilterValue(pageRequest, "name");
        int pageNum = pageRequest.getPageNum();
        int pageSize = pageRequest.getPageSize();
        Page<BmsBillboard> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<BmsBillboard> queryWrapper = new LambdaQueryWrapper<>();
        if(!StringUtils.isEmpty(name)) {
            queryWrapper.eq(BmsBillboard::getContent, name);
        }
        IPage<BmsBillboard> result = this.baseMapper.selectPage(page, queryWrapper);;
        setShowDetail(result);
        pageResult = new PageResult(result);
        return pageResult;
    }
    private void setShowDetail(IPage page) {
        List<?> content = page.getRecords();
        for(Object object:content) {
            BmsBillboard bmsBillboard = (BmsBillboard) object;
            if(bmsBillboard.isShow()==true){
                bmsBillboard.setStatusDetail("使用中");
            }else {
                bmsBillboard.setStatusDetail("未使用");
            }
        }
    }
}
