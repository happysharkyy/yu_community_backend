package com.douyuehan.doubao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.douyuehan.doubao.common.api.ApiResult;
import com.douyuehan.doubao.common.api.ColumnFilter;
import com.douyuehan.doubao.common.api.PageRequest;
import com.douyuehan.doubao.common.api.PageResult;
import com.douyuehan.doubao.common.exception.ApiAsserts;
import com.douyuehan.doubao.common.security.util.JwtTokenUtil;
import com.douyuehan.doubao.mapper.BmsFollowMapper;
import com.douyuehan.doubao.mapper.BmsTopicMapper;
import com.douyuehan.doubao.mapper.SysRoleMapper;
import com.douyuehan.doubao.mapper.SysUserMapper;
import com.douyuehan.doubao.model.dto.LoginDTO;
import com.douyuehan.doubao.model.dto.RegisterDTO;
import com.douyuehan.doubao.model.entity.*;
import com.douyuehan.doubao.model.vo.ProfileVO;
import com.douyuehan.doubao.service.IBehaviorService;
import com.douyuehan.doubao.service.IUmsUserService;
import com.douyuehan.doubao.service.SysMenuService;
import com.douyuehan.doubao.service.SysPermissionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.*;


@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class IUmsUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements IUmsUserService{
    @Autowired
    private SysPermissionService sysPermissionService;
    @Autowired
    private SysMenuService menuService;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private BmsTopicMapper bmsTopicMapper;
    @Autowired
    private BmsFollowMapper bmsFollowMapper;
    @Autowired
    private SysRoleMapper sysRoleMapper;
    @Autowired
    IBehaviorService iBehaviorService;

    @Autowired
    private UserDetailsService userDetailsService;
    @Override
    public SysUser executeRegister(RegisterDTO dto) {

        //查询是否有相同用户名的用户
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, dto.getName()).or().eq(SysUser::getEmail, dto.getEmail());
        SysUser sysUser = baseMapper.selectOne(wrapper);
        if (!ObjectUtils.isEmpty(sysUser)) {
            ApiAsserts.fail("账号或邮箱已存在！");
        }
        SysUser addUser = SysUser.builder()
                .username(dto.getName())
                .alias(dto.getName())
                .password(passwordEncoder.encode(dto.getPass()))
                .email(dto.getEmail())
                .createTime(new Date())
                .status(true)
                .build();
        baseMapper.insert(addUser);
        iBehaviorService.insertNewUser(addUser);

        return addUser;
    }
    @Override
    public SysUser getUserByUsername(String username) {
        return baseMapper.selectOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username));
    }
    @Override
    public String executeLogin(LoginDTO dto) {
        String token = null;
        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(dto.getUsername());
            if (!userDetails.isEnabled()) {
                ApiAsserts.fail("账号已被锁定，请联系管理员处理");
            }
            boolean matches = passwordEncoder.matches(dto.getPassword(), userDetails.getPassword());
            if (!matches) {
                throw new BadCredentialsException("密码错误");
            }
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            token = jwtTokenUtil.generateToken(userDetails);
        } catch (UsernameNotFoundException e) {
            log.warn("用户不存在=======>{}", dto.getUsername());
        } catch (BadCredentialsException e) {
            log.warn("密码验证失败=======>{}", dto.getPassword());
        }
        return token;
    }
    @Override
    public ProfileVO getUserProfile(String id) {
        ProfileVO profile = new ProfileVO();
        SysUser user = baseMapper.selectById(id);
        BeanUtils.copyProperties(user, profile);
        // 用户文章数
        int count = bmsTopicMapper.selectCount(new LambdaQueryWrapper<BmsPost>().eq(BmsPost::getUserId, id));
        profile.setTopicCount(count);

        // 粉丝数
        int followers = bmsFollowMapper.selectCount((new LambdaQueryWrapper<BmsFollow>().eq(BmsFollow::getParentId, id)));
        profile.setFollowerCount(followers);

        return profile;
    }
    /**
     * 获取过滤字段的值
     * @param filterName
     * @return
     */
    public String getColumnFilterValue(PageRequest pageRequest, String filterName) {
        String value = null;
        ColumnFilter columnFilter = pageRequest.getColumnFilters().get(filterName);
        if(columnFilter != null) {
            value = columnFilter.getValue();
        }
        return value;
    }
    @Override
    public PageResult findPage(PageRequest pageRequest) {
        PageResult pageResult = null;
        String name = getColumnFilterValue(pageRequest, "name");
        String email = getColumnFilterValue(pageRequest, "email");
        int pageNum = pageRequest.getPageNum();
        int pageSize = pageRequest.getPageSize();
        Page<SysUser> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        if(!StringUtils.isEmpty(name)) {
            queryWrapper.eq(SysUser::getUsername, name);
            if(!StringUtils.isEmpty(email)) {
                queryWrapper.eq(SysUser::getEmail, email);
            }
        }
        IPage<SysUser> result = baseMapper.selectPage(page, queryWrapper);;
        findUserRoles(result);
        pageResult = new PageResult(result);
        return pageResult;
    }
    private void findUserRoles(IPage page) {
        List<?> content = page.getRecords();
        for(Object object:content) {
            SysUser sysUser = (SysUser) object;
            if(sysUser.getStatus()==true){
                sysUser.setStatusDetail("使用中");
            }else {
                sysUser.setStatusDetail("未使用");
            }
            SysRole userRoles = sysRoleMapper.selectById(sysUser.getRoleId());
            sysUser.setRoleNames(userRoles.getRemark());
            sysUser.setUserRoles(userRoles);
        }
    }

    public SysUser findByName(String userName) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, userName);
        return baseMapper.selectOne(wrapper);
    }
    @Override
    public Set<String> findPermissions(String userName) {
        SysUser user = findByName(userName);
        if (user != null) {
            List<SysPermission> list = sysPermissionService.getByRoleId(user.getId());
            List<String> tempList = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                tempList.add(list.get(i).getValue());
            }
            Set<String> stringSet;
            stringSet = new HashSet<>(tempList);
            System.out.println("--------------"+stringSet.toString());
            return stringSet;
        }
        return new HashSet<>();
    }

    @Override
    public ApiResult saveUser(SysUser record) {

        if(record.getId().equals("0")){
            LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysUser::getUsername, record.getUsername());
            SysUser sysUser = baseMapper.selectOne(wrapper);
            if (!ObjectUtils.isEmpty(sysUser)) {
                ApiAsserts.fail("账号或邮箱已存在！");
            }
            SysUser addUser = SysUser.builder()
                    .username(record.getUsername())
                    .alias(record.getUsername())
                    .password(passwordEncoder.encode(record.getPassword()))
                    .email(record.getEmail())
                    .mobile(record.getMobile())
                    .roleId(record.getRoleId())
                    .bio(record.getBio())
                    .createTime(new Date())
                    .status(true)
                    .build();
            return ApiResult.success(baseMapper.insert(addUser));
        }else{
            return ApiResult.success(updateById(record));
        }
    }

    @Override
    public int delete(List<SysUser> users) {
        for (SysUser user : users) {
            removeById(user.getId());
        }
        return 1;
    }

}
