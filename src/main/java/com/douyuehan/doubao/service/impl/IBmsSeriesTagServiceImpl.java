package com.douyuehan.doubao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.douyuehan.doubao.mapper.BmsSeriesTagMapper;
import com.douyuehan.doubao.model.entity.BmsSeriesTag;
import com.douyuehan.doubao.model.entity.BmsTag;
import com.douyuehan.doubao.model.entity.BmsTopicTag;
import com.douyuehan.doubao.service.IBmsSeriesTagService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class IBmsSeriesTagServiceImpl extends ServiceImpl<BmsSeriesTagMapper, BmsSeriesTag> implements IBmsSeriesTagService {
    @Override
    public List<BmsSeriesTag> selectByTopicId(String topicId) {
        QueryWrapper<BmsSeriesTag> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(BmsSeriesTag::getSeriesId, topicId);
        return this.baseMapper.selectList(wrapper);
    }

    @Override
    public void createTopicTag(String id, List<BmsTag> tags) {
        // 先删除topicId对应的所有记录
        this.baseMapper.delete(new LambdaQueryWrapper<BmsSeriesTag>().eq(BmsSeriesTag::getSeriesId, id));

        // 循环保存对应关联
        tags.forEach(tag -> {
            BmsSeriesTag topicTag = new BmsSeriesTag();
            topicTag.setSeriesId(id);
            topicTag.setTagId(tag.getId());
            this.baseMapper.insert(topicTag);
        });
    }

    @Override
    public Set<String> selectTopicIdsByTagId(String id) {
        return this.baseMapper.getTopicIdsByTagId(id);
    }
}
