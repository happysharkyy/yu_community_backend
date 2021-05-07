package com.douyuehan.doubao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.douyuehan.doubao.model.entity.BmsSeriesPost;

import java.util.Set;

public interface BmsSeriesPostMapper extends BaseMapper<BmsSeriesPost> {
    Set<String> selectSeriesIdsByPostId(String id);
    Page<BmsSeriesPost> findPostBySeries(Page<BmsSeriesPost> tPage, String seriesId);
}
