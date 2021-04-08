package com.douyuehan.doubao.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.douyuehan.doubao.mapper.SysMenuMapper;
import com.douyuehan.doubao.mapper.SysUserMapper;
import com.douyuehan.doubao.model.entity.SysMenu;
import com.douyuehan.doubao.model.entity.SysUser;
import com.douyuehan.doubao.service.IUmsUserService;
import com.douyuehan.doubao.service.SysMenuService;
import com.douyuehan.doubao.service.SysRoleService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

    @Autowired
    private IUmsUserService userService;

    @Autowired
    private SysUserMapper userMapper;
    @Autowired
    private SysRoleService roleService;



    @Override
    public List<SysMenu> findByUser(String userName, Wrapper<SysMenu> queryWrapper) {
        if (userName == null || "".equals(userName) || "admin".equals(userName)) {
            return this.list(queryWrapper);
        }
        SysUser sysUser = userMapper.selectOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, userName));
        if (sysUser == null) {
            return new ArrayList<>();
        }

        int roldIds = sysUser.getRoleId();
        List<SysMenu> result = new ArrayList<>();
        result.addAll(roleService.findRoleMenus(roldIds));
        return result;
    }

    @Override
    public List<SysMenu> findTree(String userName, int menuType, String name) {
        List<SysMenu> resultMenus = new ArrayList<>();
        // 是否是匹配name查询
        boolean isSearch = false;
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        if (!StringUtils.isEmpty(name)) {
            wrapper.like(SysMenu::getName, name);
            isSearch = true;
        }
        List<SysMenu> findMenus = findByUser(userName, wrapper);
        if (isSearch) {
            // 子节点匹配但父节点没匹配 把父节点加入
            Set<SysMenu> menuSet = new HashSet<>();
            for (SysMenu menu : findMenus) {
                SysMenu tempMenu = menu;
                while (tempMenu != null && tempMenu.getParentId() != null && tempMenu.getParentId() != 0) {
                    SysMenu parent = getById(tempMenu.getParentId());
                    // 如果没有加入父节点，加入
                    if (parent != null && !findMenus.contains(parent)) {
                        menuSet.add(parent);
                    }
                    tempMenu = parent;
                }
            }
            findMenus.addAll(menuSet);
        }
        // 为顶级父节点设置level=0,并加入到结果集中
        for (SysMenu menu : findMenus) {
            if (menu.getParentId() == null || menu.getParentId() == 0) {
                menu.setLevel(0);
                if (!exists(resultMenus, menu)) {
                    resultMenus.add(menu);
                }
            }
        }
        // 升序排序
        resultMenus.sort(Comparator.comparing(SysMenu::getOrderNum));
        findChildren(resultMenus, findMenus, menuType);
        return resultMenus;
    }

    /**
     * 从chooseMenus挑选parentMenus中菜单的子菜单
     * @param parentMenus
     * @param chooseMenus
     * @param menuType
     */
    private void findChildren(List<SysMenu> parentMenus, List<SysMenu> chooseMenus, int menuType) {
        for (SysMenu parentMenu : parentMenus) {
            List<SysMenu> children = new ArrayList<>();
            for (SysMenu menu : chooseMenus) {
                if(menu.getType() != menuType && menu.getType() == 2) {
                    // 如果是获取类型不需要按钮，且菜单类型是按钮的，直接过滤掉
                    continue ;
                }
                if (parentMenu.getId() != null && parentMenu.getId().equals(menu.getParentId())) {
                    menu.setParentName(parentMenu.getName());
                    menu.setLevel(parentMenu.getLevel() + 1);
                    if(!exists(children, menu)) {
                        children.add(menu);
                    }
                }
            }
            parentMenu.setChildren(children);
            children.sort(Comparator.comparing(SysMenu::getOrderNum));
            findChildren(children, chooseMenus, menuType);
        }
    }

    private boolean exists(List<SysMenu> sysMenus, SysMenu sysMenu) {
        boolean exist = false;
        for(SysMenu menu : sysMenus) {
            if(menu.getId().equals(sysMenu.getId())) {
                exist = true;
            }
        }
        return exist;
    }
}
