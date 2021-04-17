package com.douyuehan.doubao.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * 权限
 *
 * @author Knox
 */
@Data
@TableName("sys_permission")
@Accessors(chain = true)
public class SysPermission implements Serializable {
    private static final long serialVersionUID = -2694960432845360318L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("`value`")
    private String value;

    @TableField("remark")
    private String remark;

    /**
     * 权限的父节点的id
     */
    @TableField("pid")
    private Integer pid;

    @TableField(exist = false)
    private String parentName;
    @TableField(exist = false)
    private Integer level;
    @TableField(exist = false)
    private Integer isCal;
    @TableField(exist = false)
    private List<SysPermission> children;
}
