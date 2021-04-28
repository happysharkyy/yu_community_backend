package com.douyuehan.doubao.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;


/**
 * <p>
 *
 * </p>
 *
 * @author yihuili
 * @since 2020-01-18
 */
@Data
@Builder
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@TableName("behavior")
public class Behavior extends Model<Behavior> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "user_id")
    private String userId;
    @TableField("post_id")
    private String postId;
    @TableField("create_time")
    private Date creationTime;
    /**
     * 0:发帖，1搜索，2收藏，3回帖，4看帖
     */
    @TableField("behavior_type")
    private double behaviorType;

    @TableField("count")
    private Long count;

}
