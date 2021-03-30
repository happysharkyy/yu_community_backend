package com.douyuehan.doubao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.douyuehan.doubao.mapper.SysPermissionMapper;
import com.douyuehan.doubao.model.entity.SysPermission;
import com.douyuehan.doubao.model.entity.SysRolePermission;
import com.douyuehan.doubao.service.SysPermissionService;
import com.douyuehan.doubao.service.SysRolePermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 权限 实现类
 *
 * @author Knox 2020/11/7
 */
@Service
public class SysPermissionServiceImpl extends ServiceImpl<SysPermissionMapper, SysPermission> implements SysPermissionService {

    @Autowired
    private SysRolePermissionService sysRolePermissionService;

    @Override
    public List<SysPermission> getByRoleId(Integer roleId) {
        List<SysRolePermission> rolePermissions = sysRolePermissionService.selectByRoleId(roleId);
        List<Integer> permissionIds = rolePermissions.stream()
                .map(SysRolePermission::getPermissionId)
                .collect(Collectors
                        .toList());
        QueryWrapper<SysPermission> wrapper = new QueryWrapper<>();
        wrapper.lambda().in(SysPermission::getId, permissionIds);
        return this.baseMapper.selectList(wrapper);
    }
}
