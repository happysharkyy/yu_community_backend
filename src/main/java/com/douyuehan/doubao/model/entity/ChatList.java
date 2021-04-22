package com.douyuehan.doubao.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@TableName("chat_list")
@Accessors(chain = true)
public class ChatList {
    private int id;
    private String userId;
    private String toId;
    private String conversationId;
    private int listStatus;

    @TableField(exist = false)
    private SysUser sysUser;

    @TableField(exist = false)
    private SysUser toUser;
}
