package com.douyuehan.doubao.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 类描述：存储用户与用户之间的相似度
 * 类名称：com.lyu.shopping.recommendate.dto.UsersSimilarityDTO
 * @author 曲健磊
 * 2018年3月25日.下午8:35:02
 * @version V1.0
 */
@Data
@Builder
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@TableName("behavior_similarity")
public class BehaviorSimilarity implements Serializable, Comparable<BehaviorSimilarity> {

	private static final long serialVersionUID = 3940726816248544380L;
	// 用户id
	private String userId;
	
	// 进行比较的用户id
	private String userRefId;
	
	// userId与userRefId之间的相似度
	private Double similarity;


	@Override
	public int compareTo(BehaviorSimilarity o) {
		return o.getSimilarity().compareTo(this.getSimilarity());
	}
}
