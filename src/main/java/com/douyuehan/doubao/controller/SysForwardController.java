package com.douyuehan.doubao.controller;

import com.douyuehan.doubao.common.api.ApiResult;
import com.douyuehan.doubao.model.dto.SysForwardDTO;
import com.douyuehan.doubao.model.entity.SysForward;
import com.douyuehan.doubao.service.SysForwardService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/forward")
public class SysForwardController extends BaseController {

    @Resource
    private SysForwardService sysForwardService;
    @GetMapping("/get_forwards")
    public ApiResult<List<SysForward>> getCommentsByTopicID(@RequestParam(value = "topicid", defaultValue = "1") String topicid, @RequestParam(value = "type", defaultValue = "1") String type) {
        List<SysForward> lstBmsComment = sysForwardService.select(topicid,type);
        return ApiResult.success(lstBmsComment);
    }
    @PostMapping("/save_forward")
    public ApiResult save_Forward(@RequestBody SysForwardDTO dto) {
        System.out.println(dto.toString()+"------------");
        return ApiResult.success(sysForwardService.save_Forward(dto));
    }
}