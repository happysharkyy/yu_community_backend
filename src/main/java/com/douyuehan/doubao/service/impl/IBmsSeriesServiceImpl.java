package com.douyuehan.doubao.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.douyuehan.doubao.common.api.ColumnFilter;
import com.douyuehan.doubao.common.api.PageRequest;
import com.douyuehan.doubao.common.api.PageResult;
import com.douyuehan.doubao.mapper.BmsSeriesMapper;
import com.douyuehan.doubao.mapper.BmsTagMapper;
import com.douyuehan.doubao.model.dto.CreateSeriesDTO;
import com.douyuehan.doubao.model.entity.*;
import com.douyuehan.doubao.model.vo.BmsSeriesVO;
import com.douyuehan.doubao.model.vo.PostVO;
import com.douyuehan.doubao.model.vo.ProfileVO;
import com.douyuehan.doubao.service.IBmsSeriesService;
import com.douyuehan.doubao.service.IBmsSeriesTagService;
import com.douyuehan.doubao.service.IBmsTagService;
import com.douyuehan.doubao.service.IUmsUserService;
import com.douyuehan.doubao.utils.SysSensitiveFilterUtil;
import com.vdurmont.emoji.EmojiParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;
@Service
public class IBmsSeriesServiceImpl extends ServiceImpl<BmsSeriesMapper, BmsSeries> implements IBmsSeriesService {
    @Autowired
    IBmsSeriesTagService iBmsSeriesTagService;
    @Autowired
    BmsTagMapper bmsTagMapper;
    @Autowired
    SysSensitiveFilterUtil sysSensitiveFilterUtil;
    @Autowired
    IBmsTagService iBmsTagService;
    @Autowired
    IUmsUserService iUmsUserService;
    @Override
    public Page<BmsSeriesVO> getList(Page<BmsSeriesVO> page,Principal principal){
        // 查询话题
        Page<BmsSeriesVO> iPage = this.baseMapper.selectListAndPage(page,iUmsUserService.getUserByUsername(principal.getName()).getId());
        // 查询话题的标签
        setSeriesTags(iPage);
        return iPage;
    }

    private void setSeriesTags(Page<BmsSeriesVO> iPage) {
        iPage.getRecords().forEach(topic -> {
            List<BmsSeriesTag> topicTags = iBmsSeriesTagService.selectByTopicId(topic.getId());

            if (!topicTags.isEmpty()) {
                List<String> tagIds = topicTags.stream().map(BmsSeriesTag::getTagId).collect(Collectors.toList());
                List<BmsTag> tags = bmsTagMapper.selectBatchIds(tagIds);
                topic.setTags(tags);
            }
        });
    }

    @Override
    public BmsSeries create(CreateSeriesDTO dto, SysUser principal) {
        BmsSeries topic1 = this.baseMapper.selectOne(new LambdaQueryWrapper<BmsSeries>().eq(BmsSeries::getTitle, dto.getTitle()));
        Assert.isNull(topic1, "合辑已存在，请修改!");
        // 封装
        BmsSeries topic = BmsSeries.builder()
                .userId(principal.getId())
                .title(dto.getTitle())
                .img(dto.getImg())
                .content(sysSensitiveFilterUtil.filter(EmojiParser.parseToAliases(dto.getContent())))
                .createTime(new Date())
                .build();
        this.baseMapper.insert(topic);

        // 标签
        if (!ObjectUtils.isEmpty(dto.getTags())) {
            // 保存标签
            List<BmsTag> tags = iBmsTagService.insertTags(dto.getTags());
            // 处理标签与话题的关联
            iBmsSeriesTagService.createTopicTag(topic.getId(), tags);
        }

        return topic;
    }

    @Override
    public Map<String, Object> viewTopic(Principal principal, String id) {
        Map<String, Object> map = new HashMap<>(16);
        BmsSeries topic = this.baseMapper.selectById(id);
        Assert.notNull(topic, "当前话题不存在,或已被作者删除");
        // emoji转码
        topic.setContent(EmojiParser.parseToUnicode(topic.getContent()));
        map.put("topic", topic);
        // 标签
        QueryWrapper<BmsSeriesTag> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(BmsSeriesTag::getSeriesId, topic.getId());
        Set<String> set = new HashSet<>();
        for (BmsSeriesTag articleTag : iBmsSeriesTagService.list(wrapper)) {
            set.add(articleTag.getTagId());
        }
        if(!set.isEmpty()){
            List<BmsTag> tags = iBmsTagService.listByIds(set);
            map.put("tags", tags);
        }else{
            List<BmsTag> tags = new ArrayList<>();
            map.put("tags", tags);
        }


        // 作者

        ProfileVO user = iUmsUserService.getUserProfile(topic.getUserId());
        map.put("user", user);

        return map;
    }

    @Override
    public List<BmsSeries> findSeries(Principal principal) {
        SysUser sysUser = iUmsUserService.getUserByUsername(principal.getName());
        LambdaQueryWrapper<BmsSeries> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BmsSeries::getUserId,sysUser.getId());
        return this.baseMapper.selectList(queryWrapper);
    }

    @Override
    public Page<BmsSeriesVO> getListAll(Page<BmsSeriesVO> page) {
        // 查询话题
        Page<BmsSeriesVO> iPage = this.baseMapper.selectListAll(page);
        // 查询话题的标签
        setSeriesTags(iPage);
        return iPage;
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
        String title = getColumnFilterValue(pageRequest, "title");
        int pageNum = pageRequest.getPageNum();
        int pageSize = pageRequest.getPageSize();
        Page<BmsSeries> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<BmsSeries> queryWrapper = new LambdaQueryWrapper<>();
        if(!StringUtils.isEmpty(title)) {
            queryWrapper.eq(BmsSeries::getTitle, title);
        }
        IPage<BmsSeries> result = this.baseMapper.selectPage(page, queryWrapper);;
        pageResult = new PageResult(result);
        return pageResult;
    }

}

