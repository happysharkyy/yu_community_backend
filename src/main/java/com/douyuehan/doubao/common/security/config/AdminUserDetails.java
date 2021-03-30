package com.douyuehan.doubao.common.security.config;

import com.douyuehan.doubao.model.entity.SysPermission;
import com.douyuehan.doubao.model.entity.SysUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户登录数据
 *
 * @author knox
 * @since 2021-01-09
 */
public class AdminUserDetails implements UserDetails {

    private final SysUser umsUser;

    // private final List<UmsResource> resourceList;
    private List<SysPermission> permissionList;
    public AdminUserDetails(SysUser umsUser, List<SysPermission> permissionList) {
        this.umsUser = umsUser;
        this.permissionList = permissionList;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        //返回当前用户的权限
        return permissionList.stream()
                .filter(permission -> permission.getValue()!=null)
                .map(permission ->new SimpleGrantedAuthority(permission.getValue()))
                .collect(Collectors.toList());
    }


    @Override
    public String getPassword() {
        return umsUser.getPassword();
    }

    @Override
    public String getUsername() {
        return umsUser.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return umsUser.getStatus();
    }
}
