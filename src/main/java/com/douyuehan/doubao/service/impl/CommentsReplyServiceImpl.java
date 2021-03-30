package com.douyuehan.doubao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.douyuehan.doubao.mapper.CommentsReplyMapper;
import com.douyuehan.doubao.model.dto.CommentsReplyDTO;
import com.douyuehan.doubao.model.entity.BmsComment;
import com.douyuehan.doubao.model.entity.CommentsReply;
import com.douyuehan.doubao.model.entity.SysUser;
import com.douyuehan.doubao.model.vo.CommentReplyVO;
import com.douyuehan.doubao.model.vo.CommentVO;
import com.douyuehan.doubao.service.CommentsReplyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
@Slf4j
@Service
public class CommentsReplyServiceImpl  extends ServiceImpl<CommentsReplyMapper, CommentsReply> implements CommentsReplyService {
    @Autowired
    CommentsReplyMapper commentsReplyMapper;
    @Override
    public int delete(List<CommentsReply> commentsReplyList) {
        for (CommentsReply commentsReply : commentsReplyList) {
            commentsReplyMapper.deleteById(commentsReply.getId());
        }
        return 1;
    }

    @Override
    public void saveCommentsReply(CommentsReply commentsReply) {
        if(commentsReply.getId()==null){
            commentsReplyMapper.insert(commentsReply);
        }else {
            commentsReplyMapper.updateById(commentsReply);
        }
    }

    @Override
    public List<CommentsReply> list() {
        LambdaQueryWrapper<CommentsReply> wrapper = new LambdaQueryWrapper<>();
        List<CommentsReply> commentsReplyList = commentsReplyMapper.selectList(wrapper);
        return commentsReplyList;
    }

    @Override
    public List<CommentReplyVO> getCommentsByCommentId(String cmmentId) {
        List<CommentReplyVO> lstBmsComment = new ArrayList<CommentReplyVO>();
        try {
            lstBmsComment = this.baseMapper.getCommentsByCommentId(cmmentId);
        } catch (Exception e) {
            log.info("getCommentsByCommentId失败");
        }
        return lstBmsComment;
    }

    @Override
    public CommentsReply create(CommentsReplyDTO dto, SysUser principal,SysUser Toprincipal) {
        CommentsReply comment = CommentsReply.builder()
                .commentId(dto.getComment_id())
                .fromId(principal.getId())
                .fromName(principal.getUsername())
                .fromAvatar(principal.getAvatar())
                .toId(Toprincipal.getId())
                .toName(Toprincipal.getUsername())
                .toAvatar(Toprincipal.getAvatar())
                .content(dto.getContent())
                .createTime(new Date())
                .build();
        this.baseMapper.insert(comment);
        return comment;
    }
}
