package com.douyuehan.doubao.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.douyuehan.doubao.common.api.ApiResult;
import com.douyuehan.doubao.common.api.PageRequest;
import com.douyuehan.doubao.model.entity.BmsPost;
import com.douyuehan.doubao.model.entity.BmsTag;
import com.douyuehan.doubao.model.entity.BmsTopicTag;
import com.douyuehan.doubao.model.entity.SysUser;
import com.douyuehan.doubao.service.IBmsTagService;
import com.douyuehan.doubao.service.IBmsTopicTagService;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.swing.text.html.HTML;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/tag")
public class BmsTagController extends BaseController {

    @Resource
    private IBmsTagService bmsTagService;
    @Resource
    private IBmsTopicTagService iBmsTopicTagService;

    @GetMapping("/{name}")
    public ApiResult<Map<String, Object>> getTopicsByTag(
            @PathVariable("name") String tagName,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size) {

        Map<String, Object> map = new HashMap<>(16);

        LambdaQueryWrapper<BmsTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BmsTag::getName, tagName);
        BmsTag one = bmsTagService.getOne(wrapper);
        Assert.notNull(one, "话题不存在，或已被管理员删除");
        Page<BmsPost> topics = bmsTagService.selectTopicsByTagId(new Page<>(page, size), one.getId());
        // 其他热门标签
        Page<BmsTag> hotTags = bmsTagService.page(new Page<>(1, 10),
                new LambdaQueryWrapper<BmsTag>()
                        .notIn(BmsTag::getName, tagName)
                        .orderByDesc(BmsTag::getTopicCount));

        map.put("topics", topics);
        map.put("hotTags", hotTags);

        return ApiResult.success(map);
    }
    @PostMapping(value="/findPage")
    public ApiResult findPage(@RequestBody PageRequest pageRequest) {
        return ApiResult.success(bmsTagService.findPage(pageRequest));
    }
    @PostMapping(value="/save/{topicId}")
    public ApiResult save(@RequestBody List<String> list,@PathVariable("topicId") String topicId) {

        List<BmsTopicTag> topicTagList = iBmsTopicTagService.selectByTopicId(topicId);
        List<BmsTag> tagList = new ArrayList<>();//这个文章所有标签

        for (String s:list) {
            //查标签表是否包含该标签
            LambdaQueryWrapper<BmsTag> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(BmsTag::getName, s);
            List<BmsTag> bmsTags = bmsTagService.list(wrapper);

            if(bmsTags.isEmpty()){//不包含该标签
                List<String> strings = new ArrayList<>();
                strings.add(s);
                List<BmsTag> list1 = bmsTagService.insertTags(strings);
                tagList.add(list1.get(0));
                System.out.println(tagList.toString()+"-----a"+topicId);
            }else{//包含该标签判断标签有没有关联这个文章
                for (BmsTag bmsTag : bmsTags) {
                    List<BmsTopicTag> topicTagList1 = topicTagList.stream().filter(p -> p.getTagId().equals(bmsTag.getId())).collect(Collectors.toList());
                    if(topicTagList1.isEmpty()){//没有关联
                        List<String> strings = new ArrayList<>();
                        strings.add(s);
                        List<BmsTag> list1 = bmsTagService.insertTags(strings);
                        tagList.add(bmsTag);
                        System.out.println(tagList.toString()+"-----a"+topicId);
                    }else{
                        tagList.add(bmsTag);
                    }
                }
            }
        }
        iBmsTopicTagService.createTopicTag(topicId, tagList);
        return ApiResult.success();
    }


}
