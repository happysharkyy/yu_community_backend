package com.douyuehan.doubao.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.douyuehan.doubao.common.api.PageRequest;
import com.douyuehan.doubao.common.api.PageResult;
import com.douyuehan.doubao.model.dto.CreateTopicDTO;
import com.douyuehan.doubao.model.dto.RankDTO;
import com.douyuehan.doubao.model.dto.RankViewDTO;
import com.douyuehan.doubao.model.dto.ResultDTO;
import com.douyuehan.doubao.model.entity.BmsPost;
import com.douyuehan.doubao.model.entity.SysUser;
import com.douyuehan.doubao.model.vo.BmsPostVO;
import com.douyuehan.doubao.model.vo.PostVO;

import java.io.IOException;
import java.security.Principal;
import java.util.*;


public interface IBmsPostService extends IService<BmsPost> {

    /**
     * 获取首页话题列表
     *
     * @param page
     * @param tab
     * @return
     */
    Page<PostVO> getList(Page<PostVO> page, String tab);
    /**
     * 发布
     *
     * @param dto
     * @param principal
     * @return
     */
    BmsPost create(CreateTopicDTO dto, SysUser principal) throws IOException;

    /**
     * 查看话题详情
     *
     * @param id
     * @return
     */
    Map<String, Object> viewTopic(Principal principal,String id);
    /**
     * 获取随机推荐10篇
     *
     * @param id
     * @return
     */
    List<BmsPost> getRecommend(String id);
    /**
     * 关键字检索
     *
     * @param keyword
     * @param page
     * @return
     */
    Page<PostVO> searchByKey(Principal principal,String keyword, Page<PostVO> page) throws Exception;

    PageResult findPage(PageRequest pageRequest);

    List<BmsPostVO> getByListId(Set<String> recommendate);

    int getTodayAddPost();

    public ResultDTO  getMonthAddPost();

    RankDTO getRank();

    List<RankViewDTO> getViewRank();

    List<PostVO> getListFllow(Principal principal);
}
