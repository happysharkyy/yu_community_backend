package com.douyuehan.doubao.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.douyuehan.doubao.common.api.ApiResult;
import com.douyuehan.doubao.common.api.PageRequest;
import com.douyuehan.doubao.common.api.PageResult;
import com.douyuehan.doubao.model.dto.LoginDTO;
import com.douyuehan.doubao.model.dto.RegisterDTO;
import com.douyuehan.doubao.model.entity.SysUser;
import com.douyuehan.doubao.model.vo.ProfileVO;

import java.util.List;
import java.util.Set;


public interface IUmsUserService extends IService<SysUser> {

    int delete(List<SysUser> users);
    /**
     * 注册功能
     *
     * @param dto
     * @return 注册对象
     */
    SysUser executeRegister(RegisterDTO dto);
    /**
     * 获取用户信息
     *
     * @param username
     * @return dbUser
     */
    SysUser getUserByUsername(String username);
    /**
     * 用户登录
     *
     * @param dto
     * @return 生成的JWT的token
     */
    String executeLogin(LoginDTO dto);
    /**
     * 获取用户信息
     *
     * @param id 用户ID
     * @return
     */
    ProfileVO getUserProfile(String id);


    PageResult findPage(PageRequest pageRequest);
    /**
     * 查找用户的菜单权限标识集合
     * @param userName
     * @return
     */
    Set<String> findPermissions(String userName);

    ApiResult saveUser(SysUser record);
//    List<SysUserRole> findUserRoles(Long userId);
}
