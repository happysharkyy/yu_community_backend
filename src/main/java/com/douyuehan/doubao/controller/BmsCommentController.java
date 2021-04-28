package com.douyuehan.doubao.controller;

import com.douyuehan.doubao.common.api.ApiResult;
import com.douyuehan.doubao.common.api.PageRequest;
import com.douyuehan.doubao.model.dto.CommentDTO;
import com.douyuehan.doubao.model.entity.BmsComment;
import com.douyuehan.doubao.model.entity.SysUser;
import com.douyuehan.doubao.model.vo.CommentVO;
import com.douyuehan.doubao.service.IBmsCommentService;
import com.douyuehan.doubao.service.IUmsUserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.security.Principal;
import java.util.List;



@RestController
@RequestMapping("/comment")
public class BmsCommentController extends BaseController {

    @Resource
    private IBmsCommentService bmsCommentService;
    @Resource
    private IUmsUserService umsUserService;

//    @PreAuthorize("hasAuthority('comment:list')")
    @GetMapping("/get_comments")
    public ApiResult<List<CommentVO>> getCommentsByTopicID(@RequestParam(value = "topicid", defaultValue = "1") String topicid) {
        List<CommentVO> lstBmsComment = bmsCommentService.getCommentsByTopicID(topicid);
        return ApiResult.success(lstBmsComment);
    }

    @PostMapping("/add_comment")
    public ApiResult<BmsComment> add_comment(Principal principal,
                                             @RequestBody CommentDTO dto) {
        SysUser user = umsUserService.getUserByUsername(principal.getName());
        BmsComment comment = bmsCommentService.create(dto, user);
        return ApiResult.success(comment);
    }

    @PostMapping(value="/findPage")
    public ApiResult findPage(@RequestBody PageRequest pageRequest) {
        System.out.println(bmsCommentService.findPage(pageRequest));
        return ApiResult.success(bmsCommentService.findPage(pageRequest));
    }
}
