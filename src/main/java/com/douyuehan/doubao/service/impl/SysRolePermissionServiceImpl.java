package com.douyuehan.doubao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.douyuehan.doubao.mapper.SysPermissionMapper;
import com.douyuehan.doubao.mapper.SysRolePermissionMapper;
import com.douyuehan.doubao.model.dto.SysRolePermissionDto;
import com.douyuehan.doubao.model.entity.SysMenu;
import com.douyuehan.doubao.model.entity.SysPermission;
import com.douyuehan.doubao.model.entity.SysRolePermission;
import com.douyuehan.doubao.service.SysPermissionService;
import com.douyuehan.doubao.service.SysRolePermissionService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 角色权限 实现类
 *
 * @author Knox 2020/11/7
 */
@Service
public class SysRolePermissionServiceImpl extends ServiceImpl<SysRolePermissionMapper, SysRolePermission> implements SysRolePermissionService {

    @Autowired
    SysRolePermissionMapper sysRolePermissionMapper;
    @Autowired
    SysPermissionMapper sysPermissionMapper;

    @Autowired
    SysPermissionService sysPermissionService;

    @Override
    public List<SysRolePermission> selectByRoleId(Integer roleId) {
        QueryWrapper<SysRolePermission> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(SysRolePermission::getRoleId, roleId);
        return this.baseMapper.selectList(wrapper);
    }
    @Override
    public List<SysPermission> findPermissionByRoleId(Integer roleId) {
        List<SysRolePermission> list = sysRolePermissionMapper.selectByRoleId(roleId);//这个角色有哪些权限
        List<SysPermission> result = new ArrayList<>();
//        LambdaQueryWrapper<SysPermission> wrapper = new LambdaQueryWrapper<>();
        List<SysPermission> list1 = sysPermissionMapper.selectListByRoleId(roleId);
        List<SysPermission> getPermission = sysPermissionMapper.selectListByRoleId(roleId);
        Set<SysPermission> menuSet = new HashSet<>();
        for (SysPermission sysPermission : list1) {
            SysPermission tempMenu = sysPermission;
            while (tempMenu != null && tempMenu.getPid() != null && tempMenu.getPid() != 0) {
                SysPermission parent = sysPermissionService.getById(tempMenu.getPid());
                // 如果没有加入父节点，加入
                if (parent != null && !list1.contains(parent)) {
                    menuSet.add(parent);
                }
                tempMenu = parent;
            }
        }
        list1.addAll(menuSet);
// 为顶级父节点设置level=0,并加入到结果集中
        for (SysPermission sysPermission: list1) {
            if (sysPermission.getPid() == null || sysPermission.getPid() == 0) {
//                if(getPermission.contains(sysPermission)){
//                    System.out.println("--------------yes");
//                    sysPermission.setIsCal(1);
//                }
                sysPermission.setLevel(0);
                if (!exists(result, sysPermission)) {
                    result.add(sysPermission);
                }
            }
        }
        findChildren(result, list1);
        return result;
    }
    @Override
    public List<SysPermission> queryAccessByRole() {

        List<SysPermission> result = new ArrayList<>();
//        LambdaQueryWrapper<SysPermission> wrapper = new LambdaQueryWrapper<>();
        List<SysPermission> list1 = sysPermissionMapper.select();



        Set<SysPermission> menuSet = new HashSet<>();
        for (SysPermission sysPermission : list1) {
            SysPermission tempMenu = sysPermission;
            while (tempMenu != null && tempMenu.getPid() != null && tempMenu.getPid() != 0) {
                SysPermission parent = sysPermissionService.getById(tempMenu.getPid());
                // 如果没有加入父节点，加入
                if (parent != null && !list1.contains(parent)) {
                    menuSet.add(parent);
                }
                tempMenu = parent;
            }
        }
        list1.addAll(menuSet);
// 为顶级父节点设置level=0,并加入到结果集中
        for (SysPermission sysPermission: list1) {
            if (sysPermission.getPid() == null || sysPermission.getPid() == 0) {
//                if(getPermission.contains(sysPermission)){
//                    System.out.println("--------------yes");
//                    sysPermission.setIsCal(1);
//                }
//                sysPermission.setLevel(0);
                if (!exists(result, sysPermission)) {
                    result.add(sysPermission);
                }
            }
        }
        findChildren(result, list1);
        return result;
    }
    private void findChildren(List<SysPermission> result, List<SysPermission> chooseMenus) {
        for (SysPermission parentMenu : result) {
            List<SysPermission> children = new ArrayList<>();
            for (SysPermission menu : chooseMenus) {
                if (parentMenu.getId() != null && parentMenu.getId().equals(menu.getPid())) {

//                    if(getPermission.contains(menu)){
//                        menu.setIsCal(1);
//                    }
//                    menu.setParentName(parentMenu.getRemark());
 //                   menu.setLevel(parentMenu.getLevel() + 1);
                    if(!exists(children, menu)) {
                        children.add(menu);
                    }
                }
            }
            parentMenu.setChildren(children);
            findChildren(children, chooseMenus);
        }
    }

    private boolean exists(List<SysPermission> sysMenus, SysPermission sysMenu) {
        boolean exist = false;
        for(SysPermission menu : sysMenus) {
            if(menu.getId().equals(sysMenu.getId())) {
                exist = true;
            }
        }
        return exist;
    }

}
