package com.douyuehan.doubao.controller;

import com.douyuehan.doubao.common.api.ApiResult;
import com.douyuehan.doubao.model.dto.CommentDTO;
import com.douyuehan.doubao.model.dto.SysStarDTO;
import com.douyuehan.doubao.model.entity.BmsComment;
import com.douyuehan.doubao.model.entity.SysStar;
import com.douyuehan.doubao.model.entity.SysUser;
import com.douyuehan.doubao.model.vo.CommentVO;
import com.douyuehan.doubao.service.IBmsCommentService;
import com.douyuehan.doubao.service.SysStarService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;


@RestController
@RequestMapping("/star")
public class SysStarController extends BaseController {

    @Resource
    private SysStarService sysStarService;
    @GetMapping("/get_stars")
    public ApiResult<List<SysStar>> getCommentsByTopicID(@RequestParam(value = "topicid", defaultValue = "1") String topicid, @RequestParam(value = "type", defaultValue = "1") String type) {
        List<SysStar> lstBmsComment =
                sysStarService.select(topicid,type);
        return ApiResult.success(lstBmsComment);
    }
    @PostMapping("/save_star")
    public ApiResult save_star(@RequestBody SysStarDTO dto) {
        System.out.println(dto.toString()+"------------");
        return ApiResult.success(sysStarService.save_star(dto));
    }
}