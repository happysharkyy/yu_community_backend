package com.douyuehan.doubao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.douyuehan.doubao.model.entity.BmsCommentsReply;
import com.douyuehan.doubao.model.vo.CommentReplyVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@org.apache.ibatis.annotations.Mapper
@Repository
public interface BmsCommentsReplyMapper extends BaseMapper<BmsCommentsReply> {
    List<CommentReplyVO> getCommentsByCommentId(@Param("cmmentId")String cmmentId);
}