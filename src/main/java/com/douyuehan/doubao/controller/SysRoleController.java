package com.douyuehan.doubao.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.douyuehan.doubao.common.api.ApiResult;
import com.douyuehan.doubao.common.api.PageRequest;
import com.douyuehan.doubao.mapper.SysPermissionMapper;
import com.douyuehan.doubao.mapper.SysRoleMapper;
import com.douyuehan.doubao.mapper.SysRolePermissionMapper;
import com.douyuehan.doubao.model.entity.*;
import com.douyuehan.doubao.service.SysPermissionService;
import com.douyuehan.doubao.service.SysRolePermissionService;
import com.douyuehan.doubao.service.SysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("role")
public class SysRoleController {
    @Autowired
    private SysRolePermissionService sysRolePermissionService;
    @Autowired
    private SysRolePermissionMapper sysRolePermissionMapper;
    @Autowired
    private SysRoleService roleService;
    @Autowired
    private SysRoleMapper roleMapper;

    @Autowired
    private SysPermissionMapper sysPermissionMapper;

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
    @GetMapping(value="/findPermissionByRoleId/{roleId}")
    public ApiResult findPermissionByRoleId(@PathVariable("roleId") int roleId) {

        List<SysPermission> list = sysPermissionMapper.selectListByRoleId(roleId);
        List<Integer> ids = list.stream().map(SysPermission::getId).collect(Collectors.toList());
        if(ids.isEmpty()){
            return ApiResult.success(new ArrayList<>());
        }else{
            return ApiResult.success(sysPermissionMapper.selectBatchIds(ids));
        }

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

    @PostMapping(value="/saveRolePermission")
    public ApiResult saveRolePermission(@RequestBody List<SysRolePermission> records) {
        for(SysRolePermission record:records) {
            SysRole sysRole = roleMapper.selectById(record.getRoleId());
            if("admin".equalsIgnoreCase(sysRole.getName())) {
                // 如果是超级管理员，不允许修改
                return ApiResult.failed("超级管理员拥有所有菜单权限，不允许修改！");
            }
        }
        sysRolePermissionMapper.delete(new LambdaQueryWrapper<SysRolePermission>().eq(SysRolePermission::getRoleId, records.get(0).getRoleId()));
        sysRolePermissionService.saveBatch(records);
        return ApiResult.success();
    }

    @PostMapping("/queryAccessByRole")
    public ApiResult queryMenuByRole() {
        return ApiResult.success(sysRolePermissionService.queryAccessByRole());
    }
}
