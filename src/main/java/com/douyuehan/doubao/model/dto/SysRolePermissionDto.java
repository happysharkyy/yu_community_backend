package com.douyuehan.doubao.model.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
@Data
public class SysRolePermissionDto {

    private int id;

    private String remark;

    private String value;

    private List<SysRolePermissionDto> children = new ArrayList<>();

    private Integer isLeaf;

    private boolean fake;


}
