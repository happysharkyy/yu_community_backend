package com.douyuehan.doubao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.douyuehan.doubao.mapper.ChatListMapper;
import com.douyuehan.doubao.mapper.ChatMessageMapper;
import com.douyuehan.doubao.mapper.SysUserMapper;
import com.douyuehan.doubao.model.entity.ChatList;
import com.douyuehan.doubao.model.entity.ChatMessage;
import com.douyuehan.doubao.model.entity.SysUser;
import com.douyuehan.doubao.service.ChatListService;
import com.douyuehan.doubao.service.ChatMessageService;
import com.douyuehan.doubao.service.IUmsUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ChatListServiceImpl extends ServiceImpl<ChatListMapper, ChatList> implements ChatListService {
    @Autowired
    ChatListMapper chatListMapper;
    @Autowired
    SysUserMapper sysUserMapper;
    @Override
    public List<ChatList> getList(String userId) {
        LambdaQueryWrapper<ChatList> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ChatList::getUserId,userId);
        queryWrapper.eq(ChatList::getListStatus,0);
        List<ChatList> list = chatListMapper.selectList(queryWrapper);
        for (ChatList chatList:
        list) {
            chatList.setSysUser(sysUserMapper.selectById(chatList.getUserId()));
            chatList.setToUser(sysUserMapper.selectById(chatList.getToId()));
        }
        return list;
    }

    @Override
    public int Remove(int id) {
        ChatList chatList = chatListMapper.selectById(id);
        chatList.setListStatus(1);
        chatListMapper.updateById(chatList);
        return 1;
    }

    @Override
    public int insertChat(String userId, String signId) {
        ChatList chatList = new ChatList();
        chatList.setToId(signId);
        chatList.setUserId(userId);
        chatList.setListStatus(0);
        if(userId.compareTo(signId)>0){
            chatList.setConversationId(userId+"_"+signId);
        }else{
            chatList.setConversationId(signId+"_"+userId);
        }
        chatListMapper.insert(chatList);
        return 0;
    }
}
