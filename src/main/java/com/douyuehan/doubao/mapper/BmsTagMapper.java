package com.douyuehan.doubao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.douyuehan.doubao.model.dto.TagRankDTO;
import com.douyuehan.doubao.model.entity.BmsTag;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface BmsTagMapper extends BaseMapper<BmsTag> {
    List<TagRankDTO> getTagRank();
}
