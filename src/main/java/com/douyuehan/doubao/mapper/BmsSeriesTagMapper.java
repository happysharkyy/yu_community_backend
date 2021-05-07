package com.douyuehan.doubao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.douyuehan.doubao.model.entity.BmsSeriesTag;

import java.util.Set;

public interface BmsSeriesTagMapper extends BaseMapper<BmsSeriesTag> {
    Set<String> getTopicIdsByTagId(String id);
}
