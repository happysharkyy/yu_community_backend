package com.douyuehan.doubao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.douyuehan.doubao.mapper.SysRolePermissionMapper;
import com.douyuehan.doubao.model.entity.SysRolePermission;
import com.douyuehan.doubao.service.SysRolePermissionService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 角色权限 实现类
 *
 * @author Knox 2020/11/7
 */
@Service
public class SysRolePermissionServiceImpl extends ServiceImpl<SysRolePermissionMapper, SysRolePermission> implements SysRolePermissionService {

    @Override
    public List<SysRolePermission> selectByRoleId(Integer roleId) {
        QueryWrapper<SysRolePermission> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(SysRolePermission::getRoleId, roleId);
        return this.baseMapper.selectList(wrapper);
    }
}
