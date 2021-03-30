package com.douyuehan.doubao.service;

import com.douyuehan.doubao.model.dto.SysStarDTO;
import com.douyuehan.doubao.model.entity.BmsComment;
import com.douyuehan.doubao.model.entity.SysStar;

import java.util.List;

public interface SysStarService {
    List<SysStar> select(String objId, String Type);
    int save_star(SysStarDTO dto);
}
