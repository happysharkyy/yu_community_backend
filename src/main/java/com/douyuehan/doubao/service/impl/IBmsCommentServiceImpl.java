package com.douyuehan.doubao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.douyuehan.doubao.common.api.ColumnFilter;
import com.douyuehan.doubao.common.api.PageRequest;
import com.douyuehan.doubao.common.api.PageResult;
import com.douyuehan.doubao.mapper.BmsCommentMapper;
import com.douyuehan.doubao.model.dto.CommentDTO;
import com.douyuehan.doubao.model.entity.BmsComment;
import com.douyuehan.doubao.model.entity.BmsPost;
import com.douyuehan.doubao.model.entity.SysUser;
import com.douyuehan.doubao.model.vo.CommentVO;
import com.douyuehan.doubao.service.IBmsCommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Slf4j
@Service
public class IBmsCommentServiceImpl extends ServiceImpl<BmsCommentMapper, BmsComment> implements IBmsCommentService {
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
}
