package com.douyuehan.doubao.model.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@TableName("sys_file")
@AllArgsConstructor
@NoArgsConstructor
public class SysFile {
    private Integer id;
    private String local;
    private String name;
    private String fileType;
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

}
