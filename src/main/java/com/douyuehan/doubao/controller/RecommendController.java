package com.douyuehan.doubao.controller;

import com.douyuehan.doubao.common.api.ApiResult;
import com.douyuehan.doubao.model.entity.*;
import com.douyuehan.doubao.model.vo.BmsPostVO;
import com.douyuehan.doubao.service.*;
import com.douyuehan.doubao.utils.test.RecommendUtils;
import javafx.geometry.Pos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("Recommend")
public class RecommendController {
    @Autowired
    IBehaviorService iBehaviorService;

    @Autowired
    IBehaviorSimilarityService userSimilarityService;

    @Autowired
    IUmsUserService iUmsUserService;

    @Autowired
    IBmsPostService iBmsPostServicel;

    @Autowired
    IBmsTopicTagService iBmsTopicTagService;


    @GetMapping("/getList")
    public ApiResult<List<BmsPostVO>> findById(Principal principal) {
        // 1.查询所有的用户浏览记录
        List<Behavior> behaviorList = iBehaviorService.listAllUserActive();

        // 2.调用推荐模块工具类的方法组装成一个ConcurrentHashMap来存储每个用户以及其对应的二级类目的点击量
        ConcurrentHashMap<String, ConcurrentHashMap<String, Double>> activeMap = RecommendUtils.assembleUserBehavior(behaviorList);


        // 3.调用推荐模块工具类的方法计算用户与用户之间的相似度
        List<BehaviorSimilarity> similarityList = RecommendUtils.calcSimilarityBetweenUsers(activeMap);

        // 4.输出计算好的用户之间的相似度
        for (BehaviorSimilarity usim : similarityList) {
            System.out.println(usim.getUserId() + "\t" + usim.getUserRefId() + "\t" + usim.getSimilarity());
            // 5.如果用户之间的相似度已经存在与数据库中就修改，不存在就添加
            if (userSimilarityService.isExistsUserSimilarity(usim)) { // 修改
                boolean flag = userSimilarityService.updateUserSimilarity(usim);
                if (flag) {
                    System.out.println("修改数据成功");
                }
            } else { // 新增
                boolean flag = userSimilarityService.saveUserSimilarity(usim);
                if (flag) {
                    System.out.println("插入数据成功");
                }
            }
        }

        SysUser sysUser = iUmsUserService.getUserByUsername(principal.getName());
        List<BehaviorSimilarity> userSimilarityList = userSimilarityService.listUserSimilarityByUId(sysUser.getId());

        // 2.获取所有的用户的浏览记录
        List<Behavior> userActiveList = iBehaviorService.listAllUserActive();
        for (BehaviorSimilarity BehaviorSimilarity : userSimilarityList) {
            System.out.println(BehaviorSimilarity.getUserId() + "\t" + BehaviorSimilarity.getUserRefId() + "\t" + BehaviorSimilarity.getSimilarity());
        }

        // 3.找出与id为1L的用户浏览行为最相似的前5个用户
        List<String> userIds = RecommendUtils.getSimilarityBetweenUsers(sysUser.getId(), userSimilarityList, 5);
        for (String userRefId : userIds) {
            System.out.println(userRefId);
        }

        // 4.获取应该推荐给1L用户的帖子
        List<String> recommendate = RecommendUtils.getRecommendateCategory2(sysUser.getId(), userIds, userActiveList);


        for (String postId : recommendate) {
            System.out.println("被推荐的文章id：" + postId);
        }
        Set<String> listNew = new HashSet<>(recommendate);
        Set<String> result = listNew;

        // 5.根据标签搜索相关帖子
        for (String s:
                listNew) {
            List<BmsTopicTag> list1 = iBmsTopicTagService.selectByTopicId(s);
            if(list1.size()>0){
                for (BmsTopicTag b:
                     list1) {
                    result.addAll(iBmsTopicTagService.selectTopicIdsByTagId(b.getTagId()));
                }
            }
        }
        List<BmsPostVO> list = iBmsPostServicel.getByListId(result);
        return ApiResult.success(list);
    }
}
