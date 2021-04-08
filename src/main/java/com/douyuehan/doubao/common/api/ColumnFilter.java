package com.douyuehan.doubao.common.api;

import lombok.Data;

@Data
public class ColumnFilter {
    /**
     * 过滤列名
     */
    private String name;
    /**
     * 查询的值
     */
    private String value;
}
