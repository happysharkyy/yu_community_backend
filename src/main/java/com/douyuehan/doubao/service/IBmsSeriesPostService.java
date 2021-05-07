package com.douyuehan.doubao.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.douyuehan.doubao.model.entity.BmsPost;
import com.douyuehan.doubao.model.entity.BmsSeriesPost;
import com.douyuehan.doubao.model.entity.BmsSeriesTag;
import com.douyuehan.doubao.model.entity.BmsTag;
import com.douyuehan.doubao.model.vo.BmsSeriesVO;

import java.util.List;
import java.util.Set;

public interface IBmsSeriesPostService extends IService<BmsSeriesPost> {
    /**
     * 获取Topic Tag 关联记录
     *
     * @param seriesId TopicId
     * @return
     */
    List<BmsSeriesPost> selectBySeriesId(String seriesId);
    /**
     * 创建中间关系
     *
     * @param id
     * @param posts
     * @return
     */
    void createSeriesPost(String id, List<BmsPost> posts);
    /**
     * 获取标签换脸话题ID集合
     *
     * @param id
     * @return
     */
    Set<String> selectSeriesIdsByPostId(String id);

    int saveSp(String topicId, String seriesId);

    String findSeriesByPost(String topicId);


    Page<BmsSeriesPost> findPostBySeries(Page<BmsSeriesPost> tPage, String seriesId);
}
