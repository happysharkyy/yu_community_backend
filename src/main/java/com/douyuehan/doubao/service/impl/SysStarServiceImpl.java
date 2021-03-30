package com.douyuehan.doubao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.douyuehan.doubao.mapper.CommentsReplyMapper;
import com.douyuehan.doubao.mapper.SysStarMapper;
import com.douyuehan.doubao.model.dto.SysStarDTO;
import com.douyuehan.doubao.model.entity.CommentsReply;
import com.douyuehan.doubao.model.entity.SysStar;
import com.douyuehan.doubao.service.CommentsReplyService;
import com.douyuehan.doubao.service.SysStarService;
import com.douyuehan.doubao.utils.BeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
@Slf4j
@Service
public class SysStarServiceImpl  extends ServiceImpl<SysStarMapper, SysStar> implements SysStarService {
    @Autowired
    SysStarMapper sysStarMapper;
    @Override
    public List<SysStar> select(String objId, String Type) {
        LambdaQueryWrapper<SysStar> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysStar::getObjId, objId);
        wrapper.eq(SysStar::getType, Type);
        wrapper.eq(SysStar::getStarStatus, 0);
        List<SysStar> sysStars = sysStarMapper.selectList(wrapper);
        return sysStars;
    }

    @Override
    public int save_star(SysStarDTO dto) {
        LambdaQueryWrapper<SysStar> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysStar::getObjId, dto.getObjId());
        wrapper.eq(SysStar::getType, dto.getType());
        wrapper.eq(SysStar::getUserId, dto.getUserId());
//        wrapper.eq(SysStar::getStarStatus, dto.getStarStatus());
        SysStar sysStar = new SysStar();
        BeanUtils.copyProperties(dto,sysStar);
        sysStar.setModifyTime(new Date());
        if(sysStarMapper.selectList(wrapper).size()==0){
            sysStar.setCreateTime(new Date());
            sysStar.setId(0);
            sysStarMapper.insert(sysStar);
        }else {
            sysStarMapper.update(sysStar,wrapper);
        }
        return 1;
    }
}
