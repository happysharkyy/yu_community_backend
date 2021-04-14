//package com.douyuehan.doubao.model.vo;
//
//import com.baomidou.mybatisplus.annotation.FieldFill;
//import com.baomidou.mybatisplus.annotation.IdType;
//import com.baomidou.mybatisplus.annotation.TableField;
//import com.baomidou.mybatisplus.annotation.TableId;
//import com.douyuehan.doubao.model.entity.SysRole;
//import com.fasterxml.jackson.annotation.JsonIgnore;
//import lombok.Builder;
//import lombok.Data;
//
//import java.util.Date;
//@Data
//public class SysUserVO {
//    private static final long serialVersionUID = -5051120337175047163L;
//
//    @TableId(value = "id", type = IdType.ASSIGN_ID)
//    private String id;
//
//    @TableField("username")
//    private String username;
//
//    @TableField("alias")
//    private String alias;
//
//    @JsonIgnore()
//    @TableField("password")
//    private String password;
//
//    @Builder.Default
//    @TableField("avatar")
//    private String avatar = "https://s3.ax1x.com/2020/12/01/DfHNo4.jpg";
//
//    @TableField("email")
//    private String email;
//
//    @TableField("mobile")
//    private String mobile;
//
//    @Builder.Default
//    @TableField("bio")
//    private String bio = "自由职业者";
//
//    @Builder.Default
//    @TableField("score")
//    private Integer score = 0;
//
//    @JsonIgnore
//    @TableField("token")
//    private String token;
//
//    @Builder.Default
//    @TableField("active")
//    private Boolean active = true;
//
//    /**
//     * 状态。1:使用，0:已停用
//     */
//    @Builder.Default
//    @TableField("`status`")
//    private Boolean status = true;
//
//    /**
//     * 用户角色
//     */
//    @TableField("role_id")
//    private Integer roleId;
//
//    @TableField(value = "create_time", fill = FieldFill.INSERT)
//    private Date createTime;
//
//    @TableField(value = "modify_time", fill = FieldFill.INSERT_UPDATE)
//    private Date modifyTime;
//    @TableField(exist = false)
//    private String statusDetail;
//
//    @TableField(exist = false)
//    private String roleNames;
//
//    @TableField(exist = false)
//    private String userRoleid;
//}
