package com.douyuehan.doubao.controller;

import com.douyuehan.doubao.common.api.ApiResult;
import com.douyuehan.doubao.model.entity.Behavior;
import com.douyuehan.doubao.model.entity.BehaviorSimilarity;
import com.douyuehan.doubao.model.entity.BmsPost;
import com.douyuehan.doubao.model.entity.SysUser;
import com.douyuehan.doubao.model.vo.BmsPostVO;
import com.douyuehan.doubao.service.IBehaviorService;
import com.douyuehan.doubao.service.IBehaviorSimilarityService;
import com.douyuehan.doubao.service.IBmsPostService;
import com.douyuehan.doubao.service.IUmsUserService;
import com.douyuehan.doubao.utils.test.RecommendUtils;
import javafx.geometry.Pos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.*;

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


    @GetMapping("/getList")
    public ApiResult<List<BmsPostVO>> findById(Principal principal) {
        SysUser sysUser = iUmsUserService.getUserByUsername(principal.getName());
        List<BehaviorSimilarity> userSimilarityList = userSimilarityService.listUserSimilarityByUId(sysUser.getId());

        // 2.获取所有的用户的浏览记录
        List<Behavior> userActiveList = iBehaviorService.listAllUserActive();
        for (BehaviorSimilarity BehaviorSimilarity : userSimilarityList) {
            System.out.println(BehaviorSimilarity.getUserId() + "\t" + BehaviorSimilarity.getUserRefId() + "\t" + BehaviorSimilarity.getSimilarity());
        }

        // 3.找出与id为1L的用户浏览行为最相似的前5个用户
        List<String> userIds = RecommendUtils.getSimilarityBetweenUsers(sysUser.getId(), userSimilarityList, 5);
        System.out.println("与1349618748226658305"  + "号用户最相似的前5个用户为：");
        for (String userRefId : userIds) {
            System.out.println(userRefId);
        }

        // 4.获取应该推荐给1L用户的二级类目
        List<String> recommendate = RecommendUtils.getRecommendateCategory2(sysUser.getId(), userIds, userActiveList);
        for (String postId : recommendate) {
            System.out.println("被推荐的文章id：" + postId);
        }
        Set<String> listNew = new HashSet<>(recommendate);
        List<BmsPostVO> list = iBmsPostServicel.getByListId(listNew);


        return ApiResult.success(list);
    }
}
