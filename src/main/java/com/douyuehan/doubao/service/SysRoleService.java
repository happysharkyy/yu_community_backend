package com.douyuehan.doubao.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.douyuehan.doubao.model.entity.SysRole;

/**
 * 角色 接口
 *
 * @author Knox 2020/11/7
 */
public interface SysRoleService extends IService<SysRole> {

    /**
     * 获取角色
     *
     * @param roleId
     * @return
     */
    SysRole getRoleById(Integer roleId);
}
