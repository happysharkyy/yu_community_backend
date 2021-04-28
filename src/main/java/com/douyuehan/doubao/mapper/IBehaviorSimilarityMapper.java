package com.douyuehan.doubao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.douyuehan.doubao.model.entity.BehaviorSimilarity;

import java.util.List;

/**
 * 类描述：更新数据库中用户与用户之间的相似度
 * 类名称：com.lyu.shopping.recommendate.mapper.UserSimilarityMapper
 * @author 曲健磊
 * 2018年3月28日.下午8:53:06
 * @version V1.0
 */
public interface IBehaviorSimilarityMapper extends BaseMapper<BehaviorSimilarity> {

	/**
	 * 新增用户相似度数据
	 * @param behaviorSimilarity 用户相似度数据
	 * @return 受影响到的行数，0表示影响0行，1表示影响1行
	 */
	int saveUserSimilarity(BehaviorSimilarity behaviorSimilarity);
	
	/**
	 * 更新用户相似度数据
	 * @param behaviorSimilarity 用户相似度数据
	 * @return 受影响到的行数，0表示影响0行，1表示影响1行
	 */
	int updateUserSimilarity(BehaviorSimilarity behaviorSimilarity);
	
	/**
	 * 判断某两个用户之间的相似度是否已经存在
	 * @param behaviorSimilarity 存储两个用户的id
	 * @return 返回1表示已经存在，返回0表示不存在
	 */
	int countUserSimilarity(BehaviorSimilarity behaviorSimilarity);
	
	/**
	 * 查询某个用户与其他用户之间的相似度列表
	 * @param userId 带查询的用户id
	 * @return 该用户与其他用户的相似度列表
	 */
	List<BehaviorSimilarity> listUserSimilarityByUId(Long userId);
	
}
