package com.douyuehan.doubao.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.douyuehan.doubao.mapper.ChatMessageMapper;
import com.douyuehan.doubao.mapper.SysUserMapper;
import com.douyuehan.doubao.model.dto.ChatMessageDTO;
import com.douyuehan.doubao.model.entity.ChatMessage;
import com.douyuehan.doubao.model.entity.SysUser;
import com.douyuehan.doubao.model.vo.MsgVO;
import com.douyuehan.doubao.service.ChatMessageService;
import com.douyuehan.doubao.utils.SysSensitiveFilterUtil;
import com.vdurmont.emoji.EmojiParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class ChatMessageServiceImpl extends ServiceImpl<ChatMessageMapper, ChatMessage> implements ChatMessageService {

    @Autowired
    private ChatMessageMapper chatMessageMapper;
    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private SysSensitiveFilterUtil sensitiveFilter;

    public Page<ChatMessage> findConversations(String userId, int offset, int limit) {
        Page<ChatMessage> iPage = chatMessageMapper.selectConversations(userId,new Page<>(offset, limit));
        List<ChatMessage> list = iPage.getRecords();
        List<ChatMessage> result = new ArrayList<>();
        for (ChatMessage s:
             list) {
            s.setContent(EmojiParser.parseToUnicode(s.getContent()));
            SysUser sysUser = sysUserMapper.selectById(s.getFromId());
            s.setFromUser(sysUser);
            SysUser sysUser1 = sysUserMapper.selectById(s.getToId());
            s.setToUser(sysUser1);
            s.setCal(this.findLetterUnreadCount(userId,s.getConversationId()));
            if(s.getCal()!=0){//未读数量为0  不add进去
                result.add(s);
            }

        }
        iPage.setRecords(result);
        return iPage;
    }

    public int findConversationCount(String userId) {
        return chatMessageMapper.selectConversationCount(userId);
    }

    public List<MsgVO> findLetters(String conversationId,String userId) {
        List<ChatMessage> list =  chatMessageMapper.selectLetters(conversationId);
        System.out.println(list.toString());
        List<MsgVO> result = new ArrayList<>();
        List<Integer> read = new ArrayList<>();
        for (ChatMessage s:
                list) {
            MsgVO s1 = new MsgVO();
            s1.setMsg(EmojiParser.parseToUnicode(s.getContent()));
            SysUser sysUser = sysUserMapper.selectById(s.getFromId());
            s1.setFromUserDetail(sysUser);
            s1.setFromUser(sysUser.getUsername());
            SysUser sysUser1 = sysUserMapper.selectById(s.getToId());
            s1.setToUserDetail(sysUser1);
            s1.setCreateTime(s.getCreateTime());
            s1.setToUser(sysUser1.getUsername());
            result.add(s1);
            read.add(s.getId());
        }
        if(!read.isEmpty()){
            readSysMessage(read,userId);
        }

        return result;
    }

    public int findLetterCount(String conversationId) {
        return chatMessageMapper.selectLetterCount(conversationId);
    }

    public int findLetterUnreadCount(String userId, String conversationId) {
        return chatMessageMapper.selectLetterUnreadCount(userId, conversationId);
    }

    public int addSysMessage(ChatMessage ChatMessage) {
        ChatMessage.setContent(HtmlUtils.htmlEscape(ChatMessage.getContent()));
        ChatMessage.setContent(sensitiveFilter.filter(ChatMessage.getContent()));
        return chatMessageMapper.insertMessage(ChatMessage);
    }

    public int readSysMessage(List<Integer> ids,String userId) {
        return chatMessageMapper.updateStatus(ids,userId, 1);
    }

    public ChatMessage findLatestNotice(int userId, String topic) {
        return chatMessageMapper.selectLatestNotice(userId, topic);
    }

    public int findNoticeCount(int userId, String topic) {
        return chatMessageMapper.selectNoticeCount(userId, topic);
    }

    public int findNoticeUnreadCount(int userId, String topic) {
        return chatMessageMapper.selectNoticeUnreadCount(userId, topic);
    }

    public List<ChatMessage> findNotices(int userId, String topic, int offset, int limit) {
        return chatMessageMapper.selectNotices(userId, topic, offset, limit);
    }

    @Override
    public int findUnreadCountAll(String userId) {
        LambdaQueryWrapper<ChatMessage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ChatMessage::getToId,userId);
        queryWrapper.eq(ChatMessage::getStatus,0);
        chatMessageMapper.selectList(queryWrapper);
        return  chatMessageMapper.selectList(queryWrapper).size();
    }

    @Override
    public List<ChatMessageDTO> getMessageAll() {
        List<ChatMessage> list = chatMessageMapper.selectList(null);
        List<ChatMessageDTO> result = new ArrayList<>();
        int i=0;
        for (ChatMessage s:
                list) {
            ChatMessageDTO s1 = new ChatMessageDTO();
            s1.setContent(EmojiParser.parseToUnicode(s.getContent()));
            SysUser sysUser = sysUserMapper.selectById(s.getFromId());
            s1.setFromUserName(sysUser.getUsername());
            SysUser sysUser1 = sysUserMapper.selectById(s.getToId());
            s1.setToUserName(sysUser1.getUsername());
            s1.setCreateTime(s.getCreateTime());
            s1.setId(i++);
            result.add(s1);
        }

        return result;
    }

}
