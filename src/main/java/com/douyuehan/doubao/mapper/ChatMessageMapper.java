package com.douyuehan.doubao.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.douyuehan.doubao.model.dto.ChatMessageDTO;
import com.douyuehan.doubao.model.entity.ChatMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface ChatMessageMapper extends BaseMapper<ChatMessage> {

    // 查询当前用户的会话列表,针对每个会话只返回一条最新的私信.
    Page<ChatMessage> selectConversations(@Param("userId") String userId, @Param("page") Page<ChatMessage> page);

    // 查询当前用户的会话数量.
    int selectConversationCount(@Param("userId") String userId);

    // 查询某个会话所包含的私信列表.
    List<ChatMessage> selectLetters(@Param("conversationId") String conversationId);

    // 查询某个会话所包含的私信数量.
    int selectLetterCount(@Param("conversationId") String conversationId);

    // 查询未读私信的数量
    int selectLetterUnreadCount(@Param("userId") String userId, @Param("conversationId") String conversationId);

    // 新增消息
    int insertMessage(ChatMessage message);

    // 修改消息的状态
    int updateStatus(@Param("ids") List<Integer> ids, @Param("userId") String userId ,@Param("status") int status);

    // 查询某个主题下最新的通知
    ChatMessage selectLatestNotice(@Param("userId") int userId, @Param("topic") String topic);

    // 查询某个主题所包含的通知数量
    int selectNoticeCount(@Param("userId") int userId, @Param("topic") String topic);

    // 查询未读的通知的数量
    int selectNoticeUnreadCount(@Param("userId") int userId, @Param("topic") String topic);

    // 查询某个主题所包含的通知列表
    List<ChatMessage> selectNotices(@Param("userId") int userId, @Param("topic") String topic, @Param("offset") int offset, @Param("limit") int limit);

}
