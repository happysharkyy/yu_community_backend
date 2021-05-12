package com.douyuehan.doubao.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.douyuehan.doubao.common.api.ColumnFilter;
import com.douyuehan.doubao.common.api.PageRequest;
import com.douyuehan.doubao.common.api.PageResult;
import com.douyuehan.doubao.mapper.BmsCommentMapper;
import com.douyuehan.doubao.model.dto.CommentDTO;
import com.douyuehan.doubao.model.entity.*;
import com.douyuehan.doubao.model.vo.CommentVO;
import com.douyuehan.doubao.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Slf4j
@Service
public class IBmsCommentServiceImpl extends ServiceImpl<BmsCommentMapper, BmsComment> implements IBmsCommentService {
    @Autowired
    IBehaviorService iBehaviorService;
    @Autowired
    IBehaviorUserLogService iBehaviorUserLogService;
    @Autowired
    SysNoticeService sysNoticeService;
    @Autowired
    IBmsPostService iBmsPostService;
    @Override
    public List<CommentVO> getCommentsByTopicID(String topicid) {
        List<CommentVO> lstBmsComment = new ArrayList<CommentVO>();
        try {
            lstBmsComment = this.baseMapper.getCommentsByTopicID(topicid);
        } catch (Exception e) {
            log.info("lstBmsComment失败");
        }
        return lstBmsComment;
    }

    @Override
    public BmsComment create(CommentDTO dto, SysUser user) {
        BmsComment comment = BmsComment.builder()
                .userId(user.getId())
                .type(dto.getType())
                .content(dto.getContent())
                .topicId(dto.getTopic_id())
                .createTime(new Date())
                .build();
        SysNotice sysNotice = new SysNotice();
        sysNotice.setId(0);
        sysNotice.setOperation("评论");
        sysNotice.setCreateTime(new Date());
        sysNotice.setObjId(dto.getTopic_id());
        sysNotice.setObjType("帖子");
        sysNotice.setContent(dto.getContent());
        sysNotice.setFromId(user.getId());
        sysNotice.setToId(iBmsPostService.getById(dto.getTopic_id()).getUserId());
        sysNoticeService.insert(sysNotice);
        //用户评论帖子 更新权重
        if(!ObjectUtil.isEmpty(iBehaviorService.getByBehaviorType(user.getId(), dto.getTopic_id()))) {
            Behavior behavior = new Behavior(user.getId(), dto.getTopic_id(), new Date(),
                    iBehaviorUserLogService.getWeightByType("comment") + iBehaviorService.getByBehaviorType(user.getId(), dto.getTopic_id())
                            .getBehaviorType(),iBehaviorService.getByBehaviorType(user.getId(), dto.getTopic_id()).getCount()+1);
            LambdaQueryWrapper<Behavior> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Behavior::getUserId, user.getId());
            queryWrapper.eq(Behavior::getPostId, dto.getTopic_id());
            iBehaviorService.update(behavior, queryWrapper);
        }
        this.baseMapper.insert(comment);
        return comment;
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
        String content = getColumnFilterValue(pageRequest, "content");
        int pageNum = pageRequest.getPageNum();
        int pageSize = pageRequest.getPageSize();
        Page<BmsComment> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<BmsComment> queryWrapper = new LambdaQueryWrapper<>();
        if(!StringUtils.isEmpty(content)) {
            queryWrapper.eq(BmsComment::getContent, content);
        }
        IPage<BmsComment> result = this.baseMapper.selectPage(page, queryWrapper);;
        pageResult = new PageResult(result);
        return pageResult;
    }
    @Override
    public int getTodayAddComment() {
        LambdaQueryWrapper<BmsComment> queryWrapper = new LambdaQueryWrapper<>();
        SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");
        java.util.Calendar calendar=java.util.Calendar.getInstance();
        calendar.roll(java.util.Calendar.DAY_OF_YEAR,-1);
        String last = df.format(calendar.getTime());
        calendar.roll(java.util.Calendar.DAY_OF_YEAR,1);
        String next = df.format(calendar.getTime());
        queryWrapper.ge(BmsComment::getCreateTime, last).apply("DATE_FORMAT(create_time,'%Y-%m-%d') <= DATE_FORMAT({0},'%Y-%m-%d')", next);
        return this.baseMapper.selectCount(queryWrapper);
    }
}
