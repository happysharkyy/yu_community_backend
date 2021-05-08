package com.douyuehan.doubao.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
@TableName("sys_notice")
@AllArgsConstructor
@NoArgsConstructor
public class SysNotice implements Serializable {
    private static final long serialVersionUID = -2694960432845360318L;

    @TableId(type = IdType.AUTO)
    private int id;

    @TableField("from_id")
    private String fromId;

    @TableField("operation")
    private String operation;

    @TableField("to_id")
    private String toId;

    @TableField("content")
    private String content;

    @TableField("obj_id")
    private String objId;

    @TableField("obj_type")
    private String objType;

    @TableField("is_read")
    private int isRead;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(exist = false)
    private SysUser fromUser;

    @TableField(exist = false)
    private SysUser toUser;

    @TableField(exist = false)
    private BmsPost bmsPost;
}
