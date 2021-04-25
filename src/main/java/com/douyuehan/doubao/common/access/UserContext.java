package com.douyuehan.doubao.common.access;

import com.douyuehan.doubao.model.entity.SysUser;

/**
 * @author imyzt
 * @date 2019/3/21 15:38
 * @description 用户信息上下文
 */
public class UserContext {

    private static final ThreadLocal<SysUser> USER_HOLDER = new ThreadLocal<>();

    public static SysUser getUser() {
        return USER_HOLDER.get();
    }

    public static void setUser(SysUser user) {
        USER_HOLDER.set(user);
    }

    public static void remove() {
        USER_HOLDER.remove();
    }
}
