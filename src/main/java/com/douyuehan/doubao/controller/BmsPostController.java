package com.douyuehan.doubao.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.douyuehan.doubao.common.api.ApiResult;
import com.douyuehan.doubao.common.api.PageRequest;
import com.douyuehan.doubao.model.dto.CreateTopicDTO;
import com.douyuehan.doubao.model.entity.BmsPost;
import com.douyuehan.doubao.model.entity.SysSensitive;
import com.douyuehan.doubao.model.entity.SysUser;
import com.douyuehan.doubao.model.vo.PostVO;
import com.douyuehan.doubao.service.IBmsPostService;
import com.douyuehan.doubao.service.IUmsUserService;
import com.douyuehan.doubao.utils.SysSensitiveFilterUtil;
import com.vdurmont.emoji.EmojiParser;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.validation.Valid;

import java.io.IOException;
import java.security.Principal;
import java.util.Date;
import java.util.List;
import java.util.Map;




@RestController
@RequestMapping("/post")
public class BmsPostController extends BaseController {

    @Resource
    private IBmsPostService iBmsPostService;
    @Resource
    private IUmsUserService umsUserService;

    @GetMapping("/list")
    public ApiResult<Page<PostVO>> list(@RequestParam(value = "tab", defaultValue = "latest") String tab,
                                        @RequestParam(value = "pageNo", defaultValue = "1")  Integer pageNo,
                                        @RequestParam(value = "size", defaultValue = "+") Integer pageSize) {
        Page<PostVO> list = iBmsPostService.getList(new Page<>(pageNo, pageSize), tab);
        return ApiResult.success(list);
    }
    @GetMapping("/list/follow")
    public ApiResult<List<PostVO>> follow(Principal principal) {
        List<PostVO> list= iBmsPostService.getListFllow(principal);;
        return ApiResult.success(list);
    }
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ApiResult<BmsPost> create(Principal principal
            , @RequestBody CreateTopicDTO dto) throws IOException {
        SysUser user = umsUserService.getUserByUsername(principal.getName());
        BmsPost topic = iBmsPostService.create(dto, user);
        return ApiResult.success(topic);
    }
    @GetMapping()
    public ApiResult<Map<String, Object>> view(Principal principal,@RequestParam("id") String id) {
        Map<String, Object> map = iBmsPostService.viewTopic(principal,id);
        return ApiResult.success(map);
    }

    @GetMapping("/recommend")
    public ApiResult<List<BmsPost>> getRecommend(@RequestParam("topicId") String id) {
        List<BmsPost> topics = iBmsPostService.getRecommend(id);
        return ApiResult.success(topics);
    }

    @GetMapping("/findById/{id}")
    public ApiResult<Map<String, Object>> findById(Principal principal,@PathVariable("id") String id) {
        Map<String, Object> map = iBmsPostService.viewTopic(principal,id);
        return ApiResult.success(map);
    }

    @PostMapping("/update")
    public ApiResult<BmsPost> update(Principal principal, @Valid @RequestBody BmsPost post) {
        SysUser sysUser = umsUserService.getUserByUsername(principal.getName());
        Assert.isTrue(sysUser.getId().equals(post.getUserId()), "非本人无权修改");
        post.setModifyTime(new Date());
        post.setContent(EmojiParser.parseToAliases(post.getContent()));
        iBmsPostService.updateById(post);
        return ApiResult.success(post);
    }
    @PostMapping("/save")
    public ApiResult<BmsPost> update(@RequestBody BmsPost post) {
        iBmsPostService.updateById(post);
        return ApiResult.success(post);
    }
    @DeleteMapping("/delete/{id}")
    public ApiResult<String> delete(Principal principal, @PathVariable("id") String id) {
        SysUser sysUser = umsUserService.getUserByUsername(principal.getName());
        BmsPost byId = iBmsPostService.getById(id);
        Assert.notNull(byId, "来晚一步，话题已不存在");
        Assert.isTrue(byId.getUserId().equals(sysUser.getId()), "你为什么可以删除别人的话题？？？");
        iBmsPostService.removeById(id);
        return ApiResult.success(null,"删除成功");
    }
    @PostMapping(value="/findPage")
    public ApiResult findPage(@RequestBody PageRequest pageRequest) {
        System.out.println(iBmsPostService.findPage(pageRequest));
        return ApiResult.success(iBmsPostService.findPage(pageRequest));
    }
    @GetMapping(value="/getTodayAddPost")
    public ApiResult getTodayAddPost() {
        return ApiResult.success(iBmsPostService.getTodayAddPost());
    }
    @GetMapping(value="/getMonthAddPost")
    public ApiResult getMonthAddPost() {
        return ApiResult.success(iBmsPostService.getMonthAddPost());
    }
    @GetMapping(value="/getRank")
    public ApiResult getRank() {
        return ApiResult.success(iBmsPostService.getRank());
    }
    @GetMapping(value="/getViewRank")
    public ApiResult getViewRank() {
        return ApiResult.success(iBmsPostService.getViewRank());
    }
}
