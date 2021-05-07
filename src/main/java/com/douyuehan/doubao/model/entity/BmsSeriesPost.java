package com.douyuehan.doubao.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@TableName("bms_series_post")
@AllArgsConstructor
@NoArgsConstructor
public class BmsSeriesPost implements Serializable {
    private static final long serialVersionUID = -5028599844989220715L;

    @TableId(type = IdType.AUTO)
    private Integer id;

    @TableField("series_id")
    private String seriesId;

    @TableField("post_id")
    private String postId;

    @TableField(exist = false)
    private BmsSeries series;

    @TableField(exist = false)
    private BmsPost post;

    @TableField(exist = false)
    private SysUser user;

    @TableField(exist = false)
    private List<BmsTag> list;

}