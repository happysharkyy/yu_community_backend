package com.douyuehan.doubao.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.douyuehan.doubao.common.api.PageRequest;
import com.douyuehan.doubao.common.api.PageResult;
import com.douyuehan.doubao.model.dto.ChatMessageDTO;
import com.douyuehan.doubao.model.entity.ChatMessage;
import com.douyuehan.doubao.model.vo.MsgVO;

import java.util.List;

public interface ChatMessageService {
    Page<ChatMessage> findConversations(String userId, int offset, int limit);

    int findConversationCount(String userId);

    public List<MsgVO> findLetters(String conversationId,String userId);

    int findLetterCount(String conversationId);

    int findLetterUnreadCount(String userId, String conversationId);

    int addSysMessage(ChatMessage ChatMessage);

    int readSysMessage(List<Integer> ids,String userId);

    ChatMessage findLatestNotice(int userId, String topic);

    int findNoticeCount(int userId, String topic);

    int findNoticeUnreadCount(int userId, String topic);

    List<ChatMessage> findNotices(int userId, String topic, int offset, int limit);

    int findUnreadCountAll(String userId);

    List<ChatMessageDTO> getMessageAll();

    PageResult findPage(PageRequest pageRequest);
}
