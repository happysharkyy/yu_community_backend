package com.douyuehan.doubao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.douyuehan.doubao.common.api.IndexPage;
import com.douyuehan.doubao.model.entity.Behavior;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author yihuili
 * @since 2020-01-18
 */
@Mapper
public interface IBehaviorMapper extends BaseMapper<Behavior> {

    /**
     * 查询出所有的用户行为
     * @return 返回用户的行为数据
     */
    List<Behavior> listAllUserActive();

    /**
     * 根据用户已有的行为信息获取它对某个二级类目的点击量
     * @param behavior 用户的行为数据
     * @return 某个用户对某个二级类目的点击量
     */
    int getHitsByUserActiveInfo(Behavior behavior);

    /**
     * 统计某个用户的行为记录的条数
     * @param behavior 要查询的用户的行为记录的条件
     * @return 1就说明存在这个用户的行为，0说明不存在
     */
    int countUserActive(Behavior behavior);

    /**
     * 向用户行为表中添加一条用户的行为记录
     * @param behavior 用户的行为数据
     * @return 受影响的行数，1表示插入成功，0表示插入失败
     */
    int saveUserActive(Behavior behavior);

    /**
     * 更新用户对某个二级类目的点击量
     * @param behavior 用户的浏览行为数据
     * @return 1表示更新成功，0表示更新失败
     */
    int updateUserActive(Behavior behavior);

    List<String> selectPost();
}
