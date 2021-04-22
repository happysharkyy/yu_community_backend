package com.douyuehan.doubao.model.vo;

import com.douyuehan.doubao.model.entity.SysUser;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class MsgVO {


    private int type; //聊天类型0：群聊，1：单聊.
    private String fromUser;//发送者.
    private String toUser;//接受者.
    private String msg;//消息

    private SysUser fromUserDetail;
    private SysUser toUserDetail;
    private Date createTime;






}