package com.douyuehan.doubao.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.douyuehan.doubao.mapper.SysRoleMapper;
import com.douyuehan.doubao.model.entity.SysRole;
import com.douyuehan.doubao.service.SysRoleService;
;
import org.springframework.stereotype.Service;

/**
 * 角色 实现类
 *
 * @author Knox 2020/11/7
 */
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    @Override
    public SysRole getRoleById(Integer roleId) {
        return this.baseMapper.selectById(roleId);
    }

}
