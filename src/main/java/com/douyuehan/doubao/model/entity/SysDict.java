package com.douyuehan.doubao.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

@Data
public class SysDict {
    // 数据库自增id
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;

    private String createBy;

    private Date createTime;

    private String lastUpdateBy;

    private Date lastUpdateTime;

    private String value;

    private String label;

    private String type;

    private String description;

    private Long sort;

    private String remarks;

    private Byte delFlag;
}
