package com.douyuehan.doubao.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.douyuehan.doubao.model.entity.SysPermission;

import java.util.List;

/**
 * 权限接口
 *
 * @author Knox 2020/11/7
 */
public interface SysPermissionService extends IService<SysPermission> {

    /**
     * 根据角色ID查询用户权限
     *
     * @param userId
     * @return
     */
    List<SysPermission> getByRoleId(String userId);
}
