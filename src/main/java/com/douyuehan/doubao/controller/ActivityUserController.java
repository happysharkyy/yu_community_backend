package com.douyuehan.doubao.controller;

import cn.hutool.core.util.ImageUtil;
import com.douyuehan.doubao.common.access.AccessLimit;
import com.douyuehan.doubao.common.api.ApiResult;
import com.douyuehan.doubao.common.api.CodeMsg;
import com.douyuehan.doubao.model.entity.Activity;
import com.douyuehan.doubao.model.entity.ActivityUser;
import com.douyuehan.doubao.model.entity.SysUser;
import com.douyuehan.doubao.rabbitmq.MqSender;
import com.douyuehan.doubao.rabbitmq.dto.MiaoshaMessage;
import com.douyuehan.doubao.redis.MiaoshaService;
import com.douyuehan.doubao.redis.RedisService;
import com.douyuehan.doubao.service.ActivityService;
import com.douyuehan.doubao.service.ActivityUserService;
import com.douyuehan.doubao.service.IUmsUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.douyuehan.doubao.redis.GoodsKey;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author imyzt
 * @date 2019/3/8 17:27
 * @description GoodsController
 */
@RestController
@Slf4j
@RequestMapping("/activity")
public class ActivityUserController  implements InitializingBean {
    @Autowired
    private  ActivityService goodsService;
    @Autowired
    private  RedisService redisService;
    @Autowired
    private IUmsUserService iUmsUserService;
    @Autowired
    private  MiaoshaService miaoshaService;
    @Autowired
    private  MqSender sender;
    @Autowired
    private  ActivityUserService orderService;

    private static final Map<Long, Boolean> LOCAL_GOODS_MAP = new ConcurrentHashMap<>();


    /**
     * 初始化系统时, 将所有商品的库存加入到redis缓存中
     */
    @Override
    public void afterPropertiesSet() {
        List <Activity> goodsVos = goodsService.listGoodsVo();
        if (null != goodsVos) {
            goodsVos.parallelStream().forEach(goodsVo -> {
                redisService.set(GoodsKey.getMiaoshaGoodsStock, ""+goodsVo.getId(), goodsVo.getStock());
                LOCAL_GOODS_MAP.put(goodsVo.getId(), false);
                System.out.println(LOCAL_GOODS_MAP);
            });
        }
    }

    @AccessLimit(seconds = 5, maxCount = 5)
    @GetMapping("/do_miaosha/{goodsId}")
    public  ApiResult miaosha(Principal principal,@PathVariable int goodsId) {
        SysUser user = iUmsUserService.getUserByUsername(principal.getName());

        // 未登录
        if (null == user) {
            return ApiResult.failed(CodeMsg.SESSION_ERROR);
        }

        if (goodsId <= 0) {
            return ApiResult.failed(CodeMsg.GOODS_NOT_EXIST);
        }

        // 判断秒杀接口,60s会自动失效
        boolean checkMiaoshaPath = miaoshaService.checkMiaoshaPath(user, (long) goodsId);
        if (!checkMiaoshaPath) {
            return ApiResult.failed(CodeMsg.REQUEST_ILLEGAL);
        }

        //判断库存, 先减一再判断减一后的结果是否大于0
        Long stock = redisService.decr(GoodsKey.getMiaoshaGoodsStock, "" + goodsId);
        if (null != stock && stock < 0) {
            LOCAL_GOODS_MAP.put((long) goodsId, true);
            return ApiResult.failed(CodeMsg.MIAO_SHA_OVER);
        }

//        // 内存标记, 减少redis访问
//        if (LOCAL_GOODS_MAP.get(goodsId)) {
//            return ApiResult.failed(CodeMsg.MIAO_SHA_OVER);
//        }

        //判断是否已经秒杀到了
        ActivityUser order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), (long) goodsId);
        if(order != null) {
            return ApiResult.failed(CodeMsg.REPEATE_MIAOSHA);
        }

        // 请求入队
        MiaoshaMessage miaoshaMessage = new MiaoshaMessage(user, (long) goodsId);
        sender.miaoshaSender(miaoshaMessage);//排队

        return ApiResult.success(0);
    }

    @GetMapping("result")
    public @ResponseBody ApiResult<Long> miaoshaResult(SysUser user, Long goodsId) {

        if (null == user) {
            return ApiResult.failed(CodeMsg.SESSION_ERROR);
        }

        if (goodsId <= 0) {
            return ApiResult.failed(CodeMsg.GOODS_NOT_EXIST);
        }

        long orderId = miaoshaService.getMiaoshaResult(user.getId(), goodsId);
        return ApiResult.success(orderId);
    }

    @AccessLimit(seconds = 5, maxCount = 5)
    @GetMapping("path")
    public @ResponseBody ApiResult getMiaoshaPath(SysUser user, Long goodsId, Integer verifyCode) {
        if (null == user) {
            return ApiResult.failed(CodeMsg.SESSION_ERROR);
        }
        if (goodsId <= 0) {
            return ApiResult.failed(CodeMsg.GOODS_NOT_EXIST);
        }
        // 校验验证码是否正确
        boolean code = miaoshaService.checkMiaoshaVerifyCode(user, goodsId, verifyCode);
        if (!code) {
            return ApiResult.failed(CodeMsg.VERIFY_CODE_FAIL);
        }

        // 生成请求地址
        String path = miaoshaService.createMiaoshaPath(user, goodsId);
        return ApiResult.success(path);
//        return ApiResult.success();
    }

    /**
     * 生成验证码, 防恶意刷请求. 5s max request < 50
     */
    @AccessLimit(seconds = 5, maxCount = 50)
    @GetMapping("verifyCode")
    public @ResponseBody ApiResult getMiaoshaVerifyCode(HttpServletResponse response, SysUser user, Long goodsId) {

        if (null == user) {
            return ApiResult.failed(CodeMsg.SESSION_ERROR);
        }

        if (goodsId <= 0) {
            return ApiResult.failed(CodeMsg.GOODS_NOT_EXIST);
        }

        BufferedImage bufferedImage = miaoshaService.createMiaoshaVerifyCode(user, goodsId);

        try (ServletOutputStream outputStream = response.getOutputStream()) {
            ImageIO.write(bufferedImage, ImageUtil.IMAGE_TYPE_JPEG, outputStream);
            outputStream.flush();
            return null;
        } catch (IOException e) {
            log.error(e.getMessage());
            return ApiResult.failed(CodeMsg.MIAOSHA_FAIL);
        }
    }

}
