package com.douyuehan.doubao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.douyuehan.doubao.model.entity.BmsSeries;
import com.douyuehan.doubao.model.vo.BmsSeriesVO;

public interface BmsSeriesMapper extends BaseMapper<BmsSeries> {
    Page<BmsSeriesVO> selectListAndPage(Page<BmsSeriesVO> page,String id);
    Page<BmsSeriesVO> selectListAll(Page<BmsSeriesVO> page);
}
