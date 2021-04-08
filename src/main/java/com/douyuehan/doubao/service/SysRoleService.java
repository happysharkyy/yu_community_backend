package com.douyuehan.doubao.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.douyuehan.doubao.common.api.PageRequest;
import com.douyuehan.doubao.common.api.PageResult;
import com.douyuehan.doubao.model.entity.SysMenu;
import com.douyuehan.doubao.model.entity.SysRole;
import com.douyuehan.doubao.model.entity.SysRoleMenu;

import java.util.List;

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


    List<SysMenu> findRoleMenus(int roleId);

    void saveRoleMenus(List<SysRoleMenu> sysRoleMenus);

    void delete(List<SysRole> roles);

    PageResult findPage(PageRequest pageRequest);
}
