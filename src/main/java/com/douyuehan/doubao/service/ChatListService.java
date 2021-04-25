package com.douyuehan.doubao.service;

import com.douyuehan.doubao.model.entity.ChatList;

import java.util.List;

public interface ChatListService {
    List<ChatList> getList(String userId);
    int Remove(int id);
    int insertChat(String userId, String signId);
}
