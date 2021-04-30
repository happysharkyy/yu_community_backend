package com.douyuehan.doubao.controller;

import com.douyuehan.doubao.common.api.ApiResult;
import com.douyuehan.doubao.model.entity.Activity;
import com.douyuehan.doubao.model.vo.BmsPostVO;
import com.douyuehan.doubao.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/activity")
public class ActivityController {
    @Autowired
    private ActivityService activityService;
    @GetMapping("/getList")
    public ApiResult<List<Activity>> getList() {
        return ApiResult.success(activityService.listGoodsVo());
    }
    @GetMapping("/getActivityById/{id}")
    public ApiResult<Activity> getActivityById(@PathVariable int id) {
        return ApiResult.success(activityService.getGoodsVoByGoodsId((long) id));
    }

}
