package com.douyuehan.doubao.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;
import java.util.List;


@Data
public class SysMenu{
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;

    private String createBy;

    private Date createTime;

    private String lastUpdateBy;

    private Date lastUpdateTime;

    private Long parentId;

    private String name;

    private String url;

    private String perms;

    private Integer type;

    private String icon;

    private Integer orderNum;

    private Byte delFlag;

    @TableField(exist = false)
    private String parentName;
    @TableField(exist = false)
    private Integer level;
    @TableField(exist = false)
    private List<SysMenu> children;
}
