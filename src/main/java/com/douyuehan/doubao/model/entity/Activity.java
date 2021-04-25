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
@Accessors(chain = true)
@TableName("activity")
@NoArgsConstructor
@AllArgsConstructor
public class Activity implements Serializable{

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("title")
    private String title;

    @TableField("name")
    private String name;

    @TableField("pic")
    private String pic;

    @TableField(value = "start_time", fill = FieldFill.INSERT)
    private Date startTime;

    @TableField(value="create_time", fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(value = "modify_time", fill = FieldFill.INSERT)
    private Date modifyTime;

    @TableField(value = "end_time", fill = FieldFill.INSERT)
    private Date endTime;

    @TableField("create_user")
    private String createUser;

    @TableField("stock")
    private int stock;


    @TableField("is_del")
    private int isDel;

}
