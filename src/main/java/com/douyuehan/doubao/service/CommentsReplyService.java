package com.douyuehan.doubao.service;

import com.douyuehan.doubao.model.dto.CommentDTO;
import com.douyuehan.doubao.model.dto.CommentsReplyDTO;
import com.douyuehan.doubao.model.entity.BmsComment;
import com.douyuehan.doubao.model.entity.CommentsReply;
import com.douyuehan.doubao.model.entity.SysUser;
import com.douyuehan.doubao.model.vo.CommentReplyVO;
import com.douyuehan.doubao.model.vo.CommentVO;

import java.util.List;

public interface CommentsReplyService {
    int delete(List<CommentsReply> commentsReplyList);

    void saveCommentsReply(CommentsReply commentsReply);

    List<CommentsReply> list();

    List<CommentReplyVO> getCommentsByCommentId(String cmmentId);

    CommentsReply create(CommentsReplyDTO dto, SysUser principal, SysUser Toprincipal);
}
