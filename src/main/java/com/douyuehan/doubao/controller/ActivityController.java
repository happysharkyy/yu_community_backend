package com.douyuehan.doubao.controller;

import com.douyuehan.doubao.common.api.ApiResult;
import com.douyuehan.doubao.common.api.PageRequest;
import com.douyuehan.doubao.model.entity.Activity;
import com.douyuehan.doubao.model.vo.BmsPostVO;
import com.douyuehan.doubao.service.ActivityService;
import com.douyuehan.doubao.service.IUmsUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/activity")
public class ActivityController {
    @Autowired
    private ActivityService activityService;
    @Autowired
    private IUmsUserService iUmsUserService;
    @GetMapping("/getList")
    public ApiResult<List<Activity>> getList(Principal principal) {
        return ApiResult.success(activityService.listGoodsVo(iUmsUserService.getUserByUsername(principal.getName()).getId()));
    }
    @GetMapping("/getActivityById/{id}")
    public ApiResult<Activity> getActivityById(@PathVariable int id) {
        return ApiResult.success(activityService.getGoodsVoByGoodsId((long) id));
    }
    @PostMapping("/findPage")
    public ApiResult findPage(@RequestBody PageRequest pageRequest) {
        return ApiResult.success(activityService.findPage(pageRequest));
    }
    @GetMapping("/getListByUser")
    public ApiResult<List<Activity>> getListByUser(Principal principal) {
        return ApiResult.success(activityService.getListByUser(iUmsUserService.getUserByUsername(principal.getName()).getId()));
    }
}
