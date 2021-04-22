package com.douyuehan.doubao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.douyuehan.doubao.mapper.BmsCommentsReplyMapper;
import com.douyuehan.doubao.model.dto.CommentsReplyDTO;
import com.douyuehan.doubao.model.entity.BmsCommentsReply;
import com.douyuehan.doubao.model.entity.SysUser;
import com.douyuehan.doubao.model.vo.CommentReplyVO;
import com.douyuehan.doubao.service.IBmsCommentsReplyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
@Slf4j
@Service
public class IBmsCommentsReplyServiceImpl extends ServiceImpl<BmsCommentsReplyMapper, BmsCommentsReply> implements IBmsCommentsReplyService {
    @Autowired
    BmsCommentsReplyMapper bmsCommentsReplyMapper;
    @Override
    public int delete(List<BmsCommentsReply> bmsCommentsReplyList) {
        for (BmsCommentsReply bmsCommentsReply : bmsCommentsReplyList) {
            bmsCommentsReplyMapper.deleteById(bmsCommentsReply.getId());
        }
        return 1;
    }

    @Override
    public void saveCommentsReply(BmsCommentsReply bmsCommentsReply) {
        if(bmsCommentsReply.getId()==null){
            bmsCommentsReplyMapper.insert(bmsCommentsReply);
        }else {
            bmsCommentsReplyMapper.updateById(bmsCommentsReply);
        }
    }

    @Override
    public List<BmsCommentsReply> list() {
        LambdaQueryWrapper<BmsCommentsReply> wrapper = new LambdaQueryWrapper<>();
        List<BmsCommentsReply> bmsCommentsReplyList = bmsCommentsReplyMapper.selectList(wrapper);
        return bmsCommentsReplyList;
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
    public BmsCommentsReply create(CommentsReplyDTO dto, SysUser principal, SysUser Toprincipal) {
        BmsCommentsReply comment = BmsCommentsReply.builder()
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
