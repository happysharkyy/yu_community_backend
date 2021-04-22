package com.douyuehan.doubao.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
@TableName("bms_comments_reply")
@AllArgsConstructor
@NoArgsConstructor
public class BmsCommentsReply implements Serializable {

    private String id;

    @TableField("唯一id")
    private String commentId;

    @TableField("评论者的id")
    private String fromId;

    @TableField("评论者的名字")
    private String fromName;

    @TableField("评论者的头像链接")
    private String fromAvatar;

    @TableField("被评论者的id")
    private String toId;

    @TableField("被评论者的名字")
    private String toName;

    @TableField("被评论者的头像链接")
    private String toAvatar;

    @TableField("评论的点赞数")
    private String starNum;

    @TableField("评论的点赞数")
    private String content;

    @TableField("评论的点赞数")
    private Date createTime;

    private static final long serialVersionUID = 1L;
}