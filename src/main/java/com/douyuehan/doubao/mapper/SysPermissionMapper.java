package com.douyuehan.doubao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.douyuehan.doubao.model.entity.SysPermission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 权限
 *
 * @author Knox 2020/11/7
 */
@Mapper
@Repository
public interface SysPermissionMapper extends BaseMapper<SysPermission> {

    /**
     * 获取用户权限
     *
     * @param userId 用户ID
     * @return 权限集合
     */
    List<SysPermission> selectList(@Param("userId") String userId);

}
