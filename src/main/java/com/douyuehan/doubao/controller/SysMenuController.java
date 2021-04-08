package com.douyuehan.doubao.controller;

import com.douyuehan.doubao.common.api.ApiResult;
import com.douyuehan.doubao.model.entity.SysMenu;
import com.douyuehan.doubao.service.SysMenuService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/menu")
public class SysMenuController {
    @Autowired
    private SysMenuService menuService;


    @PostMapping(value="/save")
    public ApiResult save(@RequestBody SysMenu record) {
        if (record.getParentId() == null) {
            record.setParentId(0L);
        }
        menuService.saveOrUpdate(record);
        return ApiResult.success();
    }

    @PostMapping(value="/delete")
    public ApiResult delete(@RequestBody List<SysMenu> records) {
        for (SysMenu record : records) {
            menuService.removeById(record.getId());
        }
        return ApiResult.success(records.size());
    }

    @GetMapping("/findNavTree")
    public ApiResult findNavTree(@RequestParam String username) {
        List<SysMenu> menuList = menuService.findTree(username, 1, null);
        return ApiResult.success(menuList);
    }

    @GetMapping("/findMenuTree")
    public ApiResult findMenuTree(@RequestParam String name) {
        List<SysMenu> menuList = menuService.findTree(null, 0, name);
        return ApiResult.success(menuList);
    }

}
