package com.douyuehan.doubao.controller;

import com.douyuehan.doubao.common.api.ApiResult;
import com.douyuehan.doubao.common.api.PageRequest;
import com.douyuehan.doubao.model.dto.ChatMessageDTO;
import com.douyuehan.doubao.model.entity.ChatMessage;
import com.douyuehan.doubao.service.IUmsUserService;
import com.douyuehan.doubao.service.ChatMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("sysMessage")
public class ChatMessageController {
    @Autowired
    private IUmsUserService iUmsUserService;
    @Autowired
    private ChatMessageService chatMessageService;
    @PostMapping(value="/findMessagePageByUserId")
    public ApiResult findMessagePageByUserId( @RequestParam(value = "pageNo", defaultValue = "1")  Integer pageNo,
                                              @RequestParam(value = "size", defaultValue = "10") Integer pageSize,@RequestParam String name) {
        return ApiResult.success(chatMessageService.findConversations(iUmsUserService.getUserByUsername(name).getId(),pageNo, pageSize));
    }
    @PostMapping(value="/findMessageByConversationId")
    public ApiResult findMessageByUserId(@RequestParam String conversationId ,@RequestParam String userId) {
        return ApiResult.success(chatMessageService.findLetters(conversationId,userId));
    }
    @GetMapping(value="/getCountUnreadAll")
    public ApiResult findMessageByUserId(Principal principal) {
        return ApiResult.success(chatMessageService.findUnreadCountAll(iUmsUserService.getUserByUsername(principal.getName()).getId()));
    }
    @GetMapping(value="/getMessageAll")
    public ApiResult<List<ChatMessageDTO>> getMessageAll() {
        return ApiResult.success(chatMessageService.getMessageAll());
    }

    @PostMapping("/findPage")
    public ApiResult findPage(@RequestBody PageRequest pageRequest) {
        return ApiResult.success(chatMessageService.findPage(pageRequest));
    }
}
