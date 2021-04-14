package com.douyuehan.doubao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.douyuehan.doubao.common.api.ColumnFilter;
import com.douyuehan.doubao.common.api.PageRequest;
import com.douyuehan.doubao.common.api.PageResult;
import com.douyuehan.doubao.mapper.BmsTagMapper;
import com.douyuehan.doubao.model.entity.BmsPost;
import com.douyuehan.doubao.model.entity.BmsTag;
import com.douyuehan.doubao.service.IBmsTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Tag 实现类
 *
 * @author Knox 2020/11/7
 */
@Service
public class IBmsTagServiceImpl extends ServiceImpl<BmsTagMapper, BmsTag> implements IBmsTagService {

    @Autowired
    private com.douyuehan.doubao.service.IBmsTopicTagService IBmsTopicTagService;

    @Autowired
    private com.douyuehan.doubao.service.IBmsPostService IBmsPostService;

    @Override
    public List<BmsTag> insertTags(List<String> tagNames) {
        List<BmsTag> tagList = new ArrayList<>();
        for (String tagName : tagNames) {
            BmsTag tag = this.baseMapper.selectOne(new LambdaQueryWrapper<BmsTag>().eq(BmsTag::getName, tagName));
            if (tag == null) {
                tag = BmsTag.builder().name(tagName).build();
                this.baseMapper.insert(tag);
            } else {
                tag.setTopicCount(tag.getTopicCount() + 1);
                this.baseMapper.updateById(tag);
            }
            tagList.add(tag);
        }
        return tagList;
    }

    @Override
    public Page<BmsPost> selectTopicsByTagId(Page<BmsPost> topicPage, String id) {

        // 获取关联的话题ID
        Set<String> ids = IBmsTopicTagService.selectTopicIdsByTagId(id);
        LambdaQueryWrapper<BmsPost> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(BmsPost::getId, ids);

        return IBmsPostService.page(topicPage, wrapper);
    }
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
        String tagName = getColumnFilterValue(pageRequest, "tagName");
        int pageNum = pageRequest.getPageNum();
        int pageSize = pageRequest.getPageSize();
        Page<BmsTag> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<BmsTag> queryWrapper = new LambdaQueryWrapper<>();
        if(!StringUtils.isEmpty(tagName)) {
            queryWrapper.eq(BmsTag::getName, tagName);
        }
        IPage<BmsTag> result = this.baseMapper.selectPage(page, queryWrapper);;
        pageResult = new PageResult(result);
        return pageResult;
    }

}
