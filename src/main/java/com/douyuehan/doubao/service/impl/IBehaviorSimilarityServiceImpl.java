package com.douyuehan.doubao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.douyuehan.doubao.mapper.IBehaviorSimilarityMapper;
import com.douyuehan.doubao.model.entity.BehaviorSimilarity;
import com.douyuehan.doubao.service.IBehaviorSimilarityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 类描述：对用户之间的相似度进行增删改查的服务实现类
 * 类名称：com.lyu.shopping.recommendate.service.impl.UserSimilarityServiceImpl
 * @author 曲健磊
 * 2018年3月28日.下午9:02:22
 * @version V1.0
 */
@Service
public class IBehaviorSimilarityServiceImpl extends ServiceImpl<IBehaviorSimilarityMapper, BehaviorSimilarity> implements IBehaviorSimilarityService {

	@Autowired
	private IBehaviorSimilarityMapper userSimilarityMapper;

	@Override
	public boolean saveUserSimilarity(BehaviorSimilarity behaviorSimilarity) {
		boolean flag = false;
		
		int rows = this.userSimilarityMapper.insert(behaviorSimilarity);
		if (rows > 0) {
			flag = true;
		}
		return flag;
	}

	@Override
	public boolean updateUserSimilarity(BehaviorSimilarity behaviorSimilarity) {
		boolean flag = false;
		LambdaQueryWrapper<BehaviorSimilarity> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(BehaviorSimilarity::getUserId,behaviorSimilarity.getUserId());
		wrapper.eq(BehaviorSimilarity::getUserRefId,behaviorSimilarity.getUserRefId());
		int rows = this.userSimilarityMapper.update(behaviorSimilarity,wrapper);
		if (rows > 0) {
			flag = true;
		}
		return flag;
	}

	@Override
	public boolean isExistsUserSimilarity(BehaviorSimilarity behaviorSimilarity) {
		LambdaQueryWrapper<BehaviorSimilarity> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(BehaviorSimilarity::getUserId,behaviorSimilarity.getUserId());
		queryWrapper.eq(BehaviorSimilarity::getUserRefId,behaviorSimilarity.getUserRefId());
		queryWrapper.eq(BehaviorSimilarity::getSimilarity,behaviorSimilarity.getSimilarity());
		int count = this.userSimilarityMapper.selectCount(queryWrapper);
		if (count > 0) {
			return true;
		}
		return false;
	}
	
	@Override
	public List<BehaviorSimilarity> listUserSimilarityByUId(String userId) {
		if (userId == null) {
			return null;
		}
		LambdaQueryWrapper<BehaviorSimilarity> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(BehaviorSimilarity::getUserId,userId);
		List<BehaviorSimilarity> userSimilarityList = this.userSimilarityMapper.selectList(queryWrapper);
		
		return userSimilarityList;
	}

}
