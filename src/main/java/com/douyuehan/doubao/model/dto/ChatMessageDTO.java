package com.douyuehan.doubao.model.dto;

import lombok.Data;

import java.util.Date;

@Data
public class ChatMessageDTO {
    private int id;
    private String fromUserName;
    private String toUserName;
    private String content;
    private Date createTime;
}
