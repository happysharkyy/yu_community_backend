package com.douyuehan.doubao.controller;

import com.douyuehan.doubao.common.api.ApiResult;
import com.douyuehan.doubao.model.dto.CreateTopicDTO;
import com.douyuehan.doubao.model.entity.BmsPost;
import com.douyuehan.doubao.model.entity.SysNotice;
import com.douyuehan.doubao.model.entity.SysUser;
import com.douyuehan.doubao.service.IUmsUserService;
import com.douyuehan.doubao.service.SysNoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("notice")
public class SysNoticeController {
    @Autowired
    SysNoticeService sysNoticeService;
    @Autowired
    IUmsUserService iUmsUserService;
    @RequestMapping(value = "/findNotice", method = RequestMethod.GET)
    public ApiResult<List<SysNotice>> findNotice(Principal principal) {
        SysUser user = iUmsUserService.getUserByUsername(principal.getName());
        List<SysNotice> topic = sysNoticeService.findNotice(user);
        return ApiResult.success(topic);
    }
    @RequestMapping(value = "/Count", method = RequestMethod.GET)
    public ApiResult<Integer> Count(Principal principal) {
        SysUser user = iUmsUserService.getUserByUsername(principal.getName());
        return ApiResult.success(sysNoticeService.count(user));
    }
}
