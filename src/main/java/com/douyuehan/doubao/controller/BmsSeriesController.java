package com.douyuehan.doubao.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.douyuehan.doubao.common.api.ApiResult;
import com.douyuehan.doubao.common.api.PageRequest;
import com.douyuehan.doubao.model.dto.CreateSeriesDTO;
import com.douyuehan.doubao.model.dto.CreateTopicDTO;
import com.douyuehan.doubao.model.entity.BmsPost;
import com.douyuehan.doubao.model.entity.BmsSeries;
import com.douyuehan.doubao.model.entity.BmsSeriesPost;
import com.douyuehan.doubao.model.entity.SysUser;
import com.douyuehan.doubao.model.vo.BmsSeriesVO;
import com.douyuehan.doubao.model.vo.PostVO;
import com.douyuehan.doubao.service.IBmsPostService;
import com.douyuehan.doubao.service.IBmsSeriesPostService;
import com.douyuehan.doubao.service.IBmsSeriesService;
import com.douyuehan.doubao.service.IUmsUserService;
import com.vdurmont.emoji.EmojiParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.security.Principal;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/series")
public class BmsSeriesController {
    @Resource
    private IBmsSeriesService iBmsSeriesService;
    @Resource
    private IUmsUserService umsUserService;
    @Autowired
    private IBmsSeriesPostService iBmsSeriesPostService;

    @GetMapping("/list")
    public ApiResult<Page<BmsSeriesVO>> list(@RequestParam(value = "pageNo", defaultValue = "1")  Integer pageNo,
                                             @RequestParam(value = "size", defaultValue = "+") Integer pageSize,
                                             Principal principal) {
        Page<BmsSeriesVO> list = iBmsSeriesService.getList(new Page<>(pageNo, pageSize),principal);
        return ApiResult.success(list);
    }
    @GetMapping("/listAll")
    public ApiResult<Page<BmsSeriesVO>> listAll(@RequestParam(value = "pageNo", defaultValue = "1")  Integer pageNo,
                                             @RequestParam(value = "size", defaultValue = "+") Integer pageSize) {
        Page<BmsSeriesVO> list = iBmsSeriesService.getListAll(new Page<>(pageNo, pageSize));
        return ApiResult.success(list);
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ApiResult<BmsSeries> create(Principal principal
            , @RequestBody CreateSeriesDTO dto) {
        SysUser user = umsUserService.getUserByUsername(principal.getName());
        BmsSeries topic = iBmsSeriesService.create(dto, user);
        return ApiResult.success(topic);
    }
    @GetMapping()
    public ApiResult<Map<String, Object>> view(Principal principal, @RequestParam("id") String id) {
        Map<String, Object> map = iBmsSeriesService.viewTopic(principal,id);
        return ApiResult.success(map);
    }

    @GetMapping("/findById/{id}")
    public ApiResult<Map<String, Object>> findById(Principal principal,@PathVariable("id") String id) {
        Map<String, Object> map = iBmsSeriesService.viewTopic(principal,id);
        return ApiResult.success(map);
    }
    @GetMapping("/findSeries")
    public ApiResult<List<BmsSeries>> findSeries(Principal principal) {
       List<BmsSeries> map = iBmsSeriesService.findSeries(principal);
        return ApiResult.success(map);
    }
    @PostMapping("/update")
    public ApiResult<BmsSeries> update(Principal principal, @Valid @RequestBody BmsSeries post) {
        SysUser sysUser = umsUserService.getUserByUsername(principal.getName());
        Assert.isTrue(sysUser.getId().equals(post.getUserId()), "非本人无权修改");
        post.setModifyTime(new Date());
        post.setContent(EmojiParser.parseToAliases(post.getContent()));
        iBmsSeriesService.updateById(post);
        return ApiResult.success(post);
    }
    @PostMapping("/save")
    public ApiResult<BmsSeries> update(@RequestBody BmsSeries post) {
        iBmsSeriesService.updateById(post);
        return ApiResult.success(post);
    }
    @DeleteMapping("/delete/{id}")
    public ApiResult<String> delete(Principal principal, @PathVariable("id") String id) {
        SysUser sysUser = umsUserService.getUserByUsername(principal.getName());
        BmsSeries byId = iBmsSeriesService.getById(id);
        Assert.notNull(byId, "来晚一步，话题已不存在");
        Assert.isTrue(byId.getUserId().equals(sysUser.getId()), "你为什么可以删除别人的话题？？？");
        iBmsSeriesService.removeById(id);
        return ApiResult.success(null,"删除成功");
    }
    @PostMapping("/saveSeriesPost")
    public ApiResult saveSeriesPost(Principal principal, @RequestParam("topicId") String topicId,
                                                         @RequestParam("seriesId") String seriesId) {
        return ApiResult.success(iBmsSeriesPostService.saveSp(topicId,seriesId));
    }
    @PostMapping("/findSeriesByPost")
    public ApiResult<String> findSeriesByPost(Principal principal, @RequestParam("topicId") String topicId) {
        return ApiResult.success(iBmsSeriesPostService.findSeriesByPost(topicId));
    }
    @PostMapping("/findPostBySeries")
    public ApiResult<Page<BmsSeriesPost>> findPostBySeries(@RequestParam("seriesId") String seriesId,
                                                           @RequestParam(value = "pageNo", defaultValue = "1")  Integer pageNo,
                                                           @RequestParam(value = "size", defaultValue = "+") Integer pageSize) {
        return ApiResult.success(iBmsSeriesPostService.findPostBySeries(new Page<>(pageNo, pageSize),seriesId));
    }
    @PostMapping(value="/findPage")
    public ApiResult findPage(@RequestBody PageRequest pageRequest) {
        return ApiResult.success(iBmsSeriesService.findPage(pageRequest));
    }
}
