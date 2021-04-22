package com.douyuehan.doubao.controller;

import com.douyuehan.doubao.common.api.ApiResult;
import com.douyuehan.doubao.model.entity.ChatList;
import com.douyuehan.doubao.service.ChatListService;
import com.douyuehan.doubao.service.ChatMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("chatList")
public class ChatListController {
    @Autowired
    private ChatListService chatListService;

    @PostMapping(value="/getList")
    public ApiResult getList(@RequestParam String userId) {
        return ApiResult.success(chatListService.getList(userId));
    }
    @PostMapping(value="/remove")
    public ApiResult remove(@RequestParam Integer id) {
        return ApiResult.success(chatListService.Remove(id));
    }
}
