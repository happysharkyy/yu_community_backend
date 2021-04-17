package com.douyuehan.doubao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.douyuehan.doubao.model.entity.SysRolePermission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 角色权限
 *
 * @author Knox 2020/11/7
 */
@Mapper
@Repository
public interface SysRolePermissionMapper extends BaseMapper<SysRolePermission> {
    List<SysRolePermission> selectByRoleId(@Param("roleId") Integer roleId);
}
