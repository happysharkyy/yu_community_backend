package com.douyuehan.doubao.model.vo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;
@Data
public class BmsPostVO01  implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String title;
    private String content;

    private String userId;

    private Integer comments = 0;

    private Integer collects = 0;

    private Integer view = 0;

    private Integer sectionId = 0;

    private Boolean top = false;

    private Boolean essence = false;

    private Date createTime;

    private Date modifyTime;

    private String tagList;

    private String username;
}