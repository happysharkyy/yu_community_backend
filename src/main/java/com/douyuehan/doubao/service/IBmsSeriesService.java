package com.douyuehan.doubao.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.douyuehan.doubao.common.api.PageRequest;
import com.douyuehan.doubao.common.api.PageResult;
import com.douyuehan.doubao.model.dto.*;
import com.douyuehan.doubao.model.entity.BmsPost;
import com.douyuehan.doubao.model.entity.BmsSeries;
import com.douyuehan.doubao.model.entity.BmsSeriesTag;
import com.douyuehan.doubao.model.entity.SysUser;
import com.douyuehan.doubao.model.vo.BmsPostVO;
import com.douyuehan.doubao.model.vo.BmsSeriesVO;
import com.douyuehan.doubao.model.vo.PostVO;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IBmsSeriesService extends IService<BmsSeries> {
    /**
     * 获取首页话题列表
     *
     * @param page
     * @return
     */
    Page<BmsSeriesVO> getList(Page<BmsSeriesVO> page,Principal principal);
     /**
     * 发布
     *
     * @param dto
     * @param principal
     * @return
     */
    BmsSeries create(CreateSeriesDTO dto, SysUser principal);

    /**
     * 查看话题详情
     *
     * @param id
     * @return
     */
    Map<String, Object> viewTopic(Principal principal, String id);

    List<BmsSeries> findSeries(Principal principal);

    Page<BmsSeriesVO> getListAll(Page<BmsSeriesVO> page);

    PageResult findPage(PageRequest pageRequest);
}
