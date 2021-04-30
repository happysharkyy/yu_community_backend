package com.douyuehan.doubao.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.douyuehan.doubao.mapper.SysFileMapper;
import com.douyuehan.doubao.mapper.SysForwardMapper;
import com.douyuehan.doubao.model.entity.SysFile;
import com.douyuehan.doubao.model.entity.SysForward;
import com.douyuehan.doubao.service.SysFileService;
import com.douyuehan.doubao.service.SysForwardService;
import org.springframework.stereotype.Service;

@Service
public class SysFileServiceImpl extends ServiceImpl<SysFileMapper, SysFile> implements SysFileService {
    @Override
    public int insert(SysFile sysFile) {
        return this.baseMapper.insert(sysFile);
    }
}
