package com.douyuehan.doubao.service;

import com.douyuehan.doubao.model.dto.SysForwardDTO;
import com.douyuehan.doubao.model.entity.SysForward;;

import java.util.List;

public interface SysForwardService {
    List<SysForward> select(String objId, String Type);
    int save_Forward(SysForwardDTO dto);
    int getTodayAddForward();
}
