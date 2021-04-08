package com.douyuehan.doubao.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.douyuehan.doubao.common.api.ApiResult;
import com.douyuehan.doubao.common.api.PageRequest;
import com.douyuehan.doubao.mapper.SysRoleMapper;
import com.douyuehan.doubao.model.entity.SysRole;
import com.douyuehan.doubao.model.entity.SysRoleMenu;
import com.douyuehan.doubao.model.entity.SysUser;
import com.douyuehan.doubao.service.SysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("role")
public class SysRoleController {
    @Autowired
    private SysRoleService roleService;
    @Autowired
    private SysRoleMapper roleMapper;

    @PostMapping(value="/save")
    public ApiResult save(@RequestBody SysRole record, Principal principal) {
        String username = principal.getName();
        record.setUpdateTime(new Date());
        record.setUpdateBy(username);
        SysRole role = roleService.getById(record.getId());
        if(role != null) {
            if("admin".equalsIgnoreCase(role.getName())) {
                return ApiResult.failed("超级管理员不允许修改!");
            }
        }
        // 新增角色
        if((record.getId() == null || record.getId() ==0)) {
            if (!roleMapper.selectList(new LambdaQueryWrapper<SysRole>().eq(SysRole::getName, record.getName())).isEmpty()) {
                return ApiResult.failed("角色名已存在!");
            }
            record.setCreateTime(new Date());
            record.setCreateBy(username);
            roleService.save(record);
        } else {
            record.setUpdateTime(new Date());
            roleService.updateById(record);
        }
        return ApiResult.success();
    }
    
    @PostMapping(value="/delete")
    public ApiResult delete(@RequestBody List<SysRole> records) {
        roleService.delete(records);
        return ApiResult.success();
    }


    @PostMapping(value="/findPage")
    public ApiResult findPage(@RequestBody PageRequest pageRequest) {
        return ApiResult.success(roleService.findPage(pageRequest));
    }


    @GetMapping(value="/findAll")
    public ApiResult findAll() {
        System.out.println(roleService.list());
        return ApiResult.success(roleService.list());
    }


    @GetMapping(value="/findRoleMenus")
    public ApiResult findRoleMenus(@RequestParam int roleId) {
        return ApiResult.success(roleService.findRoleMenus(roleId));
    }


    @PostMapping(value="/saveRoleMenus")
    public ApiResult saveRoleMenus(@RequestBody List<SysRoleMenu> records) {
        for(SysRoleMenu record:records) {
            SysRole sysRole = roleMapper.selectById(record.getRoleId());
            if("admin".equalsIgnoreCase(sysRole.getName())) {
                // 如果是超级管理员，不允许修改
                return ApiResult.failed("超级管理员拥有所有菜单权限，不允许修改！");
            }
        }
        roleService.saveRoleMenus(records);
        return ApiResult.success();
    }
}
