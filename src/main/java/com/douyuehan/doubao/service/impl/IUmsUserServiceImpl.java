package com.douyuehan.doubao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.douyuehan.doubao.common.exception.ApiAsserts;
import com.douyuehan.doubao.common.security.util.JwtTokenUtil;
import com.douyuehan.doubao.mapper.BmsFollowMapper;
import com.douyuehan.doubao.mapper.BmsTopicMapper;
import com.douyuehan.doubao.mapper.SysUserMapper;
import com.douyuehan.doubao.model.dto.LoginDTO;
import com.douyuehan.doubao.model.dto.RegisterDTO;
import com.douyuehan.doubao.model.entity.BmsFollow;
import com.douyuehan.doubao.model.entity.BmsPost;
import com.douyuehan.doubao.model.entity.SysUser;
import com.douyuehan.doubao.model.vo.ProfileVO;
import com.douyuehan.doubao.service.IUmsUserService;
import com.douyuehan.doubao.utils.MD5Utils;
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
import java.util.Date;



@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class IUmsUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements IUmsUserService {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private BmsTopicMapper bmsTopicMapper;
    @Autowired
    private BmsFollowMapper bmsFollowMapper;

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
}
