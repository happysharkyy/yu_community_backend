package com.douyuehan.doubao.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.douyuehan.doubao.common.api.ApiResult;
import com.douyuehan.doubao.common.api.PageRequest;
import com.douyuehan.doubao.model.dto.LoginDTO;
import com.douyuehan.doubao.model.dto.RegisterDTO;
import com.douyuehan.doubao.model.entity.BmsPost;
import com.douyuehan.doubao.model.entity.SysUser;
//import com.douyuehan.doubao.model.vo.SysUserVO;
import com.douyuehan.doubao.service.IBmsPostService;
import com.douyuehan.doubao.service.IUmsUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.util.Assert;
import javax.annotation.Resource;
import javax.validation.Valid;
import java.security.Principal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/ums/user")
public class UmsUserController extends BaseController {
    @Resource
    private IUmsUserService iUmsUserService;
    @Resource
    private IBmsPostService iBmsPostService;
    @Resource
    private PasswordEncoder passwordEncoder;
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ApiResult<Map<String, Object>> register(@Valid @RequestBody RegisterDTO dto) {
        SysUser user = iUmsUserService.executeRegister(dto);
        if (ObjectUtils.isEmpty(user)) {
            return ApiResult.failed("账号注册失败");
        }
        Map<String, Object> map = new HashMap<>(16);
        map.put("user", user);
        return ApiResult.success(map);
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ApiResult<Map<String, String>> login(@Valid @RequestBody LoginDTO dto) {
        String token = iUmsUserService.executeLogin(dto);
        if (ObjectUtils.isEmpty(token)) {
            return ApiResult.failed("账号密码错误");
        }
        Map<String, String> map = new HashMap<>(16);
        map.put("token", token);
        return ApiResult.success(map, "登录成功");
    }

    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public ApiResult<SysUser> getUser(Principal principal) {
        SysUser user = iUmsUserService.getUserByUsername(principal.getName());
        return ApiResult.success(user);
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public ApiResult<Object> logOut() {
        return ApiResult.success(null, "注销成功");
    }

    @GetMapping("/getByUserName/{username}")
    public ApiResult getUserByName01(@PathVariable("username") String username) {
        SysUser user = iUmsUserService.getUserByUsername(username);
        Assert.notNull(user, "用户不存在");
        return ApiResult.success(user);
    }
    @GetMapping("/{username}")
    public ApiResult<Map<String, Object>> getUserByName(@PathVariable("username") String username,
                                                        @RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
                                                        @RequestParam(value = "size", defaultValue = "10") Integer size) {
        Map<String, Object> map = new HashMap<>(16);
        SysUser user = iUmsUserService.getUserByUsername(username);
        Assert.notNull(user, "用户不存在");
        Page<BmsPost> page = iBmsPostService.page(new Page<>(pageNo, size),
                new LambdaQueryWrapper<BmsPost>().eq(BmsPost::getUserId, user.getId()));
        map.put("user", user);
        map.put("topics", page);
        return ApiResult.success(map);
    }
    @PostMapping(value="/update")
    public ApiResult<SysUser> updateUser(@RequestBody SysUser sysUser) {

//        SysUser sysUser = new SysUser();
//        BeanUtils.copyProperties(sysUserVO,sysUser);
//        sysUser.setModifyTime(new Date());
//        sysUser.setUserRoles(sysUserVO.getUserRoleid());
//
        System.out.println(sysUser+"-----------------------------------------------------------");
        iUmsUserService.updateById(sysUser);
        return ApiResult.success(sysUser);
    }
    //    @PreAuthorize("hasAuthority('user:view')")
//    @RequiresPermissions("sys:user:view")
//    @GetMapping(value="/findUserRoles")
//    public ApiResult findUserRoles(@RequestParam Long userId) {
//        return ApiResult.success(iUmsUserService.findUserRoles(userId));
//    }

//    @Log("查看用户")
//    @RequiresPermissions("sys:user:view")
    @PostMapping(value="/findPage")
    public ApiResult findPage(@RequestBody PageRequest pageRequest) {
        System.out.println(iUmsUserService.findPage(pageRequest));
        return ApiResult.success(iUmsUserService.findPage(pageRequest));
    }
    @GetMapping(value="/findPermissions")
    public ApiResult findPermissions(@RequestParam String name) {
        return ApiResult.success(iUmsUserService.findPermissions(name));
    }

    @PostMapping(value="/save")
    public ApiResult save(@RequestBody SysUser record) {
        System.out.println(record+"-----------------------01");
        SysUser user = iUmsUserService.getUserByUsername(record.getUsername());
        if(user != null) {
            if("admin".equalsIgnoreCase(user.getUsername())) {
                return ApiResult.failed("超级管理员不允许修改!");
            }
        }
        if(record.getPassword() != null) {
            if(user == null) {
                // 新增用户
                if(iUmsUserService.getUserByUsername(record.getUsername()) != null) {
                    return ApiResult.failed("用户名已存在!");
                }
                record.setPassword(passwordEncoder.encode(record.getPassword()));
            } else {
                // 修改用户, 且修改了密码
                if(!record.getPassword().equals(user.getPassword())) {
                    System.out.println("-------------"+passwordEncoder.encode(record.getPassword()));
                    record.setPassword(passwordEncoder.encode(record.getPassword()));
                }
            }
        }
        record.setModifyTime(new Date());

        return iUmsUserService.saveUser(record);
    }


    @PostMapping(value="/delete")
    public ApiResult delete(@RequestBody List<SysUser> records) {
        for(SysUser record : records) {
            SysUser sysUser = iUmsUserService.getUserByUsername(record.getUsername());
            if(sysUser != null && "admin".equalsIgnoreCase(sysUser.getUsername())) {
                return ApiResult.failed("超级管理员不允许删除!");
            }
        }
        return ApiResult.success("成功");
    }
}
