package com.douyuehan.doubao.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@TableName("sys_sensitive")
@Accessors(chain = true)
public class SysSensitive {
    private Integer id;
    private String text;
}
