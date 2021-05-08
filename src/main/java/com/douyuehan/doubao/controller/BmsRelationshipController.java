package com.douyuehan.doubao.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.douyuehan.doubao.common.api.ApiResult;
import com.douyuehan.doubao.common.exception.ApiAsserts;
import com.douyuehan.doubao.model.entity.BmsFollow;
import com.douyuehan.doubao.model.entity.SysNotice;
import com.douyuehan.doubao.model.entity.SysUser;
import com.douyuehan.doubao.service.IBmsFollowService;
import com.douyuehan.doubao.service.IBmsPostService;
import com.douyuehan.doubao.service.IUmsUserService;
import com.douyuehan.doubao.service.SysNoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.security.Principal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/relationship")
public class BmsRelationshipController extends BaseController {

    @Resource
    private IBmsFollowService bmsFollowService;

    @Resource
    private IUmsUserService umsUserService;
    @Autowired
    SysNoticeService sysNoticeService;
    @Autowired
    IBmsPostService iBmsPostService;


    @GetMapping("/subscribe/{userId}")
    public ApiResult<Object> handleFollow(Principal principal
            , @PathVariable("userId") String parentId) {
        SysUser sysUser = umsUserService.getUserByUsername(principal.getName());
        if (parentId.equals(sysUser.getId())) {
            ApiAsserts.fail("您脸皮太厚了，怎么可以关注自己呢 😮");
        }
        BmsFollow one = bmsFollowService.getOne(
                new LambdaQueryWrapper<BmsFollow>()
                        .eq(BmsFollow::getParentId, parentId)
                        .eq(BmsFollow::getFollowerId, sysUser.getId()));
        if (!ObjectUtils.isEmpty(one)) {
            ApiAsserts.fail("已关注");
        }
        SysNotice sysNotice = new SysNotice();
        sysNotice.setId(0);
        sysNotice.setOperation("关注");
        sysNotice.setCreateTime(new Date());
        sysNotice.setObjType("关注");
        sysNotice.setFromId(sysUser.getId());
        sysNotice.setToId(parentId);
        sysNoticeService.insert(sysNotice);

        BmsFollow follow = new BmsFollow();
        follow.setParentId(parentId);
        follow.setFollowerId(sysUser.getId());
        bmsFollowService.save(follow);
        return ApiResult.success(null, "关注成功");
    }

    @GetMapping("/unsubscribe/{userId}")
    public ApiResult<Object> handleUnFollow(Principal principal
            , @PathVariable("userId") String parentId) {
        SysUser sysUser = umsUserService.getUserByUsername(principal.getName());
        BmsFollow one = bmsFollowService.getOne(
                new LambdaQueryWrapper<BmsFollow>()
                        .eq(BmsFollow::getParentId, parentId)
                        .eq(BmsFollow::getFollowerId, sysUser.getId()));
        if (ObjectUtils.isEmpty(one)) {
            ApiAsserts.fail("未关注！");
        }
        SysNotice sysNotice = new SysNotice();
        sysNotice.setId(0);
        sysNotice.setOperation("取消关注");
        sysNotice.setCreateTime(new Date());
        sysNotice.setObjType("取消关注");
        sysNotice.setFromId(sysUser.getId());
        sysNotice.setToId(parentId);
        sysNoticeService.insert(sysNotice);
        bmsFollowService.remove(new LambdaQueryWrapper<BmsFollow>().eq(BmsFollow::getParentId, parentId)
                .eq(BmsFollow::getFollowerId, sysUser.getId()));
        return ApiResult.success(null, "取关成功");
    }

    @GetMapping("/validate/{topicUserId}")
    public ApiResult<Map<String, Object>> isFollow(Principal principal
            , @PathVariable("topicUserId") String topicUserId) {
        SysUser sysUser = umsUserService.getUserByUsername(principal.getName());
        Map<String, Object> map = new HashMap<>(16);
        map.put("hasFollow", false);
        if (!ObjectUtils.isEmpty(sysUser)) {
            BmsFollow one = bmsFollowService.getOne(new LambdaQueryWrapper<BmsFollow>()
                    .eq(BmsFollow::getParentId, topicUserId)
                    .eq(BmsFollow::getFollowerId, sysUser.getId()));
            if (!ObjectUtils.isEmpty(one)) {
                map.put("hasFollow", true);
            }
        }


        return ApiResult.success(map);
    }
}
