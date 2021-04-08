package com.douyuehan.doubao.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 角色
 *
 * @author Knox
 */
@Data
@TableName("sys_role")
@Accessors(chain = true)
public class SysRole implements Serializable {
    private static final long serialVersionUID = 7824693669858106664L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("name")
    private String name;

    @TableField("remark")
    private String remark;


    private int isDel;

    private String createBy;

    private Date createTime;

    private String updateBy;

    private Date updateTime;


}
