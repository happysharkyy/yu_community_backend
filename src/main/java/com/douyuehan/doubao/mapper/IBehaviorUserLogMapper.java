package com.douyuehan.doubao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.douyuehan.doubao.model.entity.Behavior;
import com.douyuehan.doubao.model.entity.BehaviorUserLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface IBehaviorUserLogMapper extends BaseMapper<BehaviorUserLog> {
}
