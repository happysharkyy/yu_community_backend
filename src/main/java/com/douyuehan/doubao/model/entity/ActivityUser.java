package com.douyuehan.doubao.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

@Data
@Builder
@Accessors(chain = true)
@TableName("activity_user")
@NoArgsConstructor
@AllArgsConstructor
public class ActivityUser {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    @TableField("user_id")
    private String userId;

    @TableField("activity_id")
    private Long activityId;

    @TableField("activity_channel")
    private String activityChannel;

    @TableField(value="create_time", fill = FieldFill.INSERT)
    private Date createTime;

    @TableField("is_del")
    private int isDel;

    @TableField(exist = false)
    private SysUser sysUser;
}
