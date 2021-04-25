package com.douyuehan.doubao.redis;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.script.ScriptUtil;
import com.douyuehan.doubao.model.entity.Activity;
import com.douyuehan.doubao.model.entity.ActivityUser;
import com.douyuehan.doubao.model.entity.SysUser;
import com.douyuehan.doubao.service.ActivityService;
import com.douyuehan.doubao.service.ActivityUserService;
import com.douyuehan.doubao.utils.MD5Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Objects;
import java.util.Random;
import java.util.stream.IntStream;

/**
 * @author imyzt
 * @date 2019/3/13 18:25
 * @description MiaoshaService
 */
@Service
@Slf4j
public class MiaoshaService {

    private static final char[] OPS = new char[]{'+','-','*'};
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ActivityService goodsService;
    @Autowired
    private ActivityUserService orderService;
    @Autowired
    private RedisService redisService;

    @Transactional(rollbackFor = Exception.class)
    public ActivityUser miaosha(SysUser user, Activity goodsVo) {
        // 减库存
        boolean reduceStock = goodsService.reduceStock(goodsVo);
        if (reduceStock) {
            // 下订单
            return orderService.createOrder(user, goodsVo);
        } else {
            // 设置该商品状态为已售完
            setGoodsOver(goodsVo.getId());
            return null;
        }
    }

    public long getMiaoshaResult(String userId, Long goodsId) {

        ActivityUser miaoshaOrder = orderService.getMiaoshaOrderByUserIdGoodsId(userId, goodsId);
        // 秒杀成功
        if (null != miaoshaOrder) {
            return miaoshaOrder.getId();
        } else {
            // 判断该商品是否已售完
            boolean isOver = getGoodsOver(goodsId);
            if (isOver) {
                return -1;
            } else {
                return 0;
            }
        }
    }

    private void setGoodsOver(Long goodsId) {
        redisService.set(MiaoshaKey.IS_GOODS_OVER, ""+goodsId, true);
    }

    private boolean getGoodsOver(Long goodsId) {
        return redisService.exists(MiaoshaKey.IS_GOODS_OVER, ""+goodsId);
    }

    public String createMiaoshaPath(SysUser user, Long goodsId) {
        String path = passwordEncoder.encode(RandomUtil.randomString(32) + "&*!@:LJ:");
        // 存入redis
        redisService.set(MiaoshaKey.GET_MIAOSHA_PATH, ""+user.getId()+"_"+goodsId, path);
        return path;
    }

    public boolean checkMiaoshaPath(SysUser user, Long goodsId) {
        if (null == user) {
            return false;
        }
        String pathOld = redisService.get(MiaoshaKey.GET_MIAOSHA_PATH, "" + user.getId() + "_" + goodsId, String.class);
        return true;
    }

    public BufferedImage createMiaoshaVerifyCode(SysUser user, Long goodsId) {

        int width = 80, height = 32;

        // 创建图片
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics graphics = image.getGraphics();

        // 设置画笔颜色
        graphics.setColor(new Color(0xDCDCDC));
        // 设置背景颜色
        graphics.fillRect(0, 0, width, height);
        // 重设画笔颜色
        graphics.setColor(new Color(0, 0, 0));
        graphics.drawRect(0, 0, width -1, height -1);

        // 创建50个干扰点
        Random random = new Random();
        IntStream.range(0, 50).forEach(i->{
            int x = random.nextInt();
            int y = random.nextInt();
            graphics.drawOval(x, y, 0, 0);
        });

        // 创建验证码
        String verifyCode = verifyCode(random);
        graphics.setColor(new Color(0, 100, 0));
        graphics.setFont(new Font("Candara", Font.BOLD, 24));
        graphics.drawString(verifyCode, 8, 24);
        graphics.dispose();

        // 将验证码存放到redis中
        int rnd = calc(verifyCode);
        redisService.set(MiaoshaKey.GET_MIAOSHA_VERIFY_CODE, ""+user.getId()+"_"+goodsId, rnd);

        return image;
    }

    private int calc(String exp) {
        return (Integer) ScriptUtil.eval(exp);
    }

    private String verifyCode(Random random) {
        int num1 = random.nextInt(10);
        int num2 = random.nextInt(10);
        int num3 = random.nextInt(10);

        char op1 = OPS[random.nextInt(3)];
        char op2 = OPS[random.nextInt(3)];
        return String.format("%d%s%d%s%d", num1, op1, num2, op2, num3);
    }

    public boolean checkMiaoshaVerifyCode(SysUser user, Long goodsId, Integer verifyCode) {
        Integer oldCode = redisService.get(MiaoshaKey.GET_MIAOSHA_VERIFY_CODE, "" + user.getId() + "_" + goodsId, Integer.class);
        if (null == oldCode || oldCode - verifyCode != 0) {
            return false;
        }
        redisService.delete(MiaoshaKey.GET_MIAOSHA_VERIFY_CODE, "" + user.getId() + "_" + goodsId);
        return true;
    }


}
