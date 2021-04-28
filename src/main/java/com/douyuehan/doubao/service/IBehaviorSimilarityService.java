package com.douyuehan.doubao.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.douyuehan.doubao.model.entity.Behavior;
import com.douyuehan.doubao.model.entity.BehaviorSimilarity;

import java.util.List;

/**
 * 类描述：对用户之间的相似度进行增删改查的服务接口
 * 类名称：com.lyu.shopping.recommendate.service.UserSimilarityService
 * @author 曲健磊
 * 2018年3月28日.下午9:01:06
 * @version V1.0
 */
public interface IBehaviorSimilarityService extends IService<BehaviorSimilarity> {

	/**
	 * 新增用户相似度数据
	 * @param behaviorSimilarity 用户相似度数据
	 * @return true则新增用户相似度成功，false则失败
	 */
	boolean saveUserSimilarity(BehaviorSimilarity behaviorSimilarity);
	
	/**
	 * 更新用户相似度数据
	 * @param behaviorSimilarity 用户相似度数据
	 * @return 受影响到的行数，0表示影响0行，1表示影响1行
	 */
	boolean updateUserSimilarity(BehaviorSimilarity behaviorSimilarity);
	
	/**
	 * 判断某两个用户之间的相似度是否已经存在
	 * @param behaviorSimilarity 存储两个用户的id
	 * @return true表示已经存在，false表示不存在
	 */
	boolean isExistsUserSimilarity(BehaviorSimilarity behaviorSimilarity);
	
	/**
	 * 查询某个用户与其他用户之间的相似度列表
	 * @param userId 带查询的用户id
	 * @return 该用户与其他用户的相似度列表
	 */
	List<BehaviorSimilarity> listUserSimilarityByUId(String userId);
	
}
