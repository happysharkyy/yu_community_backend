package com.douyuehan.doubao.service;

import com.douyuehan.doubao.model.dto.CommentsReplyDTO;
import com.douyuehan.doubao.model.entity.BmsCommentsReply;
import com.douyuehan.doubao.model.entity.SysUser;
import com.douyuehan.doubao.model.vo.CommentReplyVO;

import java.util.List;

public interface IBmsCommentsReplyService {
    int delete(List<BmsCommentsReply> bmsCommentsReplyList);

    void saveCommentsReply(BmsCommentsReply bmsCommentsReply);

    List<BmsCommentsReply> list();

    List<CommentReplyVO> getCommentsByCommentId(String cmmentId);

    BmsCommentsReply create(CommentsReplyDTO dto, SysUser principal, SysUser Toprincipal);
}
