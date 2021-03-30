package com.douyuehan.doubao.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.douyuehan.doubao.model.entity.SysRolePermission;

import java.util.List;

/**
 * 角色-权限接口
 *
 * @author Knox 2020/11/7
 */
public interface SysRolePermissionService extends IService<SysRolePermission> {

    /**
     * 查询角色的权限关联记录
     *
     * @param roleId
     * @return
     */
    List<SysRolePermission> selectByRoleId(Integer roleId);
}
