package com.douyuehan.doubao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.douyuehan.doubao.mapper.BmsSeriesPostMapper;
import com.douyuehan.doubao.mapper.BmsTagMapper;
import com.douyuehan.doubao.model.entity.*;
import com.douyuehan.doubao.model.vo.BmsSeriesVO;
import com.douyuehan.doubao.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class IBmsSeriesPostServiceImpl extends ServiceImpl<BmsSeriesPostMapper, BmsSeriesPost> implements IBmsSeriesPostService {
    @Autowired
    IBmsTagService iBmsTagService;
    @Autowired
    BmsTagMapper bmsTagMapper;
    @Autowired
    IBmsSeriesService iBmsSeriesService;
    @Autowired
    IBmsPostService iBmsPostService;
    @Autowired
    IBmsTopicTagService iBmsTopicTagService;
    @Autowired
    IUmsUserService iUmsUserService;
    @Override
    public List<BmsSeriesPost> selectBySeriesId(String seriesId) {
        QueryWrapper<BmsSeriesPost> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(BmsSeriesPost::getSeriesId, seriesId);
        return this.baseMapper.selectList(wrapper);
    }

    @Override
    public void createSeriesPost(String id, List<BmsPost> posts) {
        // 先删除topicId对应的所有记录
        this.baseMapper.delete(new LambdaQueryWrapper<BmsSeriesPost>().eq(BmsSeriesPost::getSeriesId, id));
        // 循环保存对应关联
        posts.forEach(post -> {
            BmsSeriesPost topicTag = new BmsSeriesPost();
            topicTag.setSeriesId(id);
            topicTag.setPostId(post.getId());
            this.baseMapper.insert(topicTag);
        });
    }

    @Override
    public Set<String> selectSeriesIdsByPostId(String id) {
        return this.baseMapper.selectSeriesIdsByPostId(id);
    }

    @Override
    public int saveSp(String topicId, String seriesId) {
        LambdaQueryWrapper<BmsSeriesPost> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BmsSeriesPost::getSeriesId,seriesId);
        queryWrapper.eq(BmsSeriesPost::getPostId,topicId);
        if(this.baseMapper.selectList(queryWrapper).size()==0){
            BmsSeriesPost bmsSeriesPost = new BmsSeriesPost();
            bmsSeriesPost.setPostId(topicId);
            bmsSeriesPost.setSeriesId(seriesId);
            bmsSeriesPost.setId(0);
            this.baseMapper.insert(bmsSeriesPost);
        }else {
            BmsSeriesPost bmsSeriesPost= this.baseMapper.selectList(queryWrapper).get(0);
            bmsSeriesPost.setSeriesId(seriesId);
            this.baseMapper.updateById(bmsSeriesPost);
        }
        return 0;
    }

    @Override
    public String findSeriesByPost(String topicId) {
        LambdaQueryWrapper<BmsSeriesPost> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BmsSeriesPost::getPostId,topicId);
        try {
            if(!StringUtils.isEmpty(this.baseMapper.selectOne(queryWrapper).getSeriesId())){
                return this.baseMapper.selectOne(queryWrapper).getSeriesId();
            }
        }catch (Exception e){


        }
        return "";
    }

    @Override
    public Page<BmsSeriesPost> findPostBySeries(Page<BmsSeriesPost> tPage, String seriesId) {
        Page<BmsSeriesPost> iPage = this.baseMapper.findPostBySeries(tPage,seriesId);
        setPostTags(iPage);
        return iPage;
    }
    private void setPostTags(Page<BmsSeriesPost> iPage) {
        iPage.getRecords().forEach(topic -> {
            topic.setPost(iBmsPostService.getById(topic.getPostId()));
            topic.setSeries(iBmsSeriesService.getById(topic.getSeriesId()));
            topic.setUser(iUmsUserService.getById(topic.getSeries().getUserId()));
            List<BmsTopicTag> topicTags = iBmsTopicTagService.selectByTopicId(topic.getPostId());
            if (!topicTags.isEmpty()) {
                List<String> tagIds = topicTags.stream().map(BmsTopicTag::getTagId).collect(Collectors.toList());
                List<BmsTag> tags = bmsTagMapper.selectBatchIds(tagIds);
                topic.setList(tags);
            }
        });
    }
}
