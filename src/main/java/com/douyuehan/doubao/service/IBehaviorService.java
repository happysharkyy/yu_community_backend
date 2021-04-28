package com.douyuehan.doubao.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.douyuehan.doubao.model.entity.Behavior;
import com.douyuehan.doubao.model.entity.SysUser;

import java.util.Date;
import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author yihuili
 * @since 2020-01-18
 */
public interface IBehaviorService extends IService<Behavior> {

    /**
     * 查询出所有的用户行为
     * @return 返回用户的行为数据
     */
    List<Behavior> listAllUserActive();

    /**
     * 保存用户的浏览记录，如果用户的浏览记录存在则更新，不存在则添加
     * @param userActiveDTO 用户的行为数据
     * @return true表示更新成功，false表示更新失败
     */
    boolean saveUserActive(Behavior userActiveDTO);

    void insert(Behavior behavior);

    Behavior getByBehaviorType(String userId, String postId);
    void insertNewUser(SysUser addUser);
}
