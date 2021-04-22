package com.douyuehan.doubao.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@TableName("chat_message")
@Accessors(chain = true)
public class ChatMessage {
    private static final long serialVersionUID = -2694960432845360318L;
    private int id;
    private String fromId;
    private String toId;
    private String conversationId;
    private String content;
    private int status;
    private Date createTime;
    @TableField(exist = false)
    private SysUser fromUser;
    @TableField(exist = false)
    private SysUser toUser;
    @TableField(exist = false)
    private int cal;

}
