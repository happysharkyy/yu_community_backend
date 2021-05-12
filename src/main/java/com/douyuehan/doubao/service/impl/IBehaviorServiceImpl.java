package com.douyuehan.doubao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.douyuehan.doubao.mapper.IBehaviorMapper;
import com.douyuehan.doubao.model.entity.Behavior;
import com.douyuehan.doubao.model.entity.SysUser;
import com.douyuehan.doubao.service.IBehaviorService;
import com.douyuehan.doubao.service.IUmsUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author yihuili
 * @since 2020-01-18
 */
@Service
public class IBehaviorServiceImpl extends ServiceImpl<IBehaviorMapper, Behavior> implements IBehaviorService {

    @Autowired
    private IBehaviorMapper iBehaviorMapper;
    @Autowired
    private IUmsUserService iUmsUserService;

    @Override
    public List<Behavior> listAllUserActive() {
        return iBehaviorMapper.selectList(null);
    }

    @Override
    @Transactional(isolation = Isolation.DEFAULT, propagation = Propagation.REQUIRED)
    public boolean saveUserActive(Behavior behavior) {
        boolean flag = false;
        // 1.先判断数据库中是否存在当前用户的浏览记录
        LambdaQueryWrapper<Behavior> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Behavior::getPostId,behavior.getPostId());
        queryWrapper.eq(Behavior::getUserId,behavior.getUserId());
        int rows = this.iBehaviorMapper.selectCount(queryWrapper);
        int saveRows = 0;
        int updateRows = 0;
        // 2.不存在就添加
        if (rows < 1) { // 不存在
            behavior.setCount(1L); // 不存在记录的话那肯定是第一次访问，那点击量肯定是1
            saveRows = this.iBehaviorMapper.saveUserActive(behavior);
        } else { // 已经存在
            // 3.存在则先把当前用户对当前二级类目的点击量取出来+1
            long hits = this.iBehaviorMapper.getHitsByUserActiveInfo(behavior);
            // 4.然后在更新用户的浏览记录
            hits++;
            behavior.setCount(hits);
            updateRows = this.iBehaviorMapper.updateUserActive(behavior);
        }
        if (saveRows > 0 || updateRows > 0) {
            flag = true;
        }
        return flag;
    }

    @Override
    public void insert(Behavior behavior) {
        List<SysUser> list = iUmsUserService.list();
        for(SysUser sysUser:list){
            if(sysUser.getId().equals(behavior.getUserId())){
                iBehaviorMapper.insert(behavior);
            }else{
                Behavior behavior1 = new Behavior(sysUser.getId(),behavior.getPostId(),new Date(),1, (long) 1);
                iBehaviorMapper.insert(behavior1);
            }
        }
    }

    @Override
    public Behavior getByBehaviorType(String userId, String postId) {
        LambdaQueryWrapper<Behavior> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Behavior::getUserId,userId);
        queryWrapper.eq(Behavior::getPostId,postId);
        return iBehaviorMapper.selectOne(queryWrapper);
    }

    @Override
    public void insertNewUser(SysUser addUser) {
        List<String> list = iBehaviorMapper.selectPost();
        for (String s:
             list) {
            iBehaviorMapper.insert(new Behavior(addUser.getId(),s,new Date(),0,0L));
        }
    }

}
