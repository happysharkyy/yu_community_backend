package com.douyuehan.doubao.common.api;

import lombok.Data;

import java.util.List;

/**
 * @author Liyihui
 * @Date: 2020/3/22
 * @Time: 3:08
 */

@Data
/** 数据类 **/
public class IndexPage {

    private Integer start;

    private Integer pageSize;
    /**推荐的subjectList*/
    private List<Long> subjectList;

    /**推荐的problemList*/
    private List<Long> problemList;

    /**除了推荐之后剩下的List*/
    private List<Long> postList;

    private Long userId;

    /**用于协同过滤算法*/
    private Long subjectId;

    /**用于协同过滤算法*/
    private Long problemId;

    /**用于协同过滤算法*/
    private List<Long> userList;

    private Integer behaviorType;

    /**用于协同过滤算法*/
    private Long postId;

    /**用于协同过滤算法*/
    private Integer num;

    /**用于协同过滤算法*/
    private List<Long> CFpostList;


    public IndexPage(List<Long> postList, List<Long> CFpostList, Long userId) {
        this.postList = postList;
        this.CFpostList = CFpostList;
        this.userId = userId;
    }

    public IndexPage(Integer start, Integer pageSize, List<Long> problemList, List<Long> subjectList, List<Long> postList, Long userId) {
        this.start = start;
        this.pageSize = pageSize;
        this.problemList = problemList;
        this.subjectList = subjectList;
        this.postList = postList;
        this.userId = userId;
    }

    public IndexPage(List<Long> postList, List<Long> userList, Integer behaviorType, Integer num) {
        this.postList = postList;
        this.userList = userList;
        this.behaviorType = behaviorType;
        this.num = num;
    }

    public IndexPage(List<Long> postList, Long userId, Long subjectId, Long problemId, Integer behaviorType) {
        this.postList = postList;
        this.userId = userId;
        this.subjectId = subjectId;
        this.problemId = problemId;
        this.behaviorType = behaviorType;
    }

    @Override
    public String toString() {
        return "IndexPage{" +
                "start=" + start +
                ", pageSize=" + pageSize +
                ", subjectList=" + subjectList +
                ", problemList=" + problemList +
                ", postList=" + postList +
                ", userId=" + userId +
                ", subjectId=" + subjectId +
                ", problemId=" + problemId +
                ", userList=" + userList +
                ", behaviorType=" + behaviorType +
                ", postId=" + postId +
                ", num=" + num +
                ", CFpostList=" + CFpostList +
                '}';
    }
}
