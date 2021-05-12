package com.douyuehan.doubao.rabbitmq;

import com.douyuehan.doubao.common.api.Constant;
import com.douyuehan.doubao.model.entity.Activity;
import com.douyuehan.doubao.model.entity.ActivityUser;
import com.douyuehan.doubao.model.entity.SysUser;
import com.douyuehan.doubao.rabbitmq.dto.MiaoshaMessage;
import com.douyuehan.doubao.redis.MiaoshaService;
import com.douyuehan.doubao.service.ActivityService;
import com.douyuehan.doubao.service.ActivityUserService;
import com.douyuehan.doubao.utils.ConvertUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * @author imyzt
 * @date 2019/3/20 11:26
 * @description mq 接收端
 */
@Service
@Slf4j
public class MqReceiver {

    @Autowired
    private ActivityService activityService;

    @Autowired
    private ActivityUserService orderService;

    @Autowired
    private MiaoshaService miaoshaService;

    @RabbitListener(queues = Constant.MIAOSHA_QUEUE)
    public void miaoshaQueueReceiver(String message) {

        log.debug("miaoshaQueueReceiver msg={}", message);

        MiaoshaMessage miaoshaMessage = ConvertUtil.strToBean(message, MiaoshaMessage.class);
        SysUser user = miaoshaMessage.getUser();
        Long goodsId = miaoshaMessage.getActivitysId();

        // 判断库存
        Activity goodsVo = activityService.getGoodsVoByGoodsId(goodsId);
        if (null == goodsVo || goodsVo.getStock() <= 0) {
            return;
        }

        // 判断是否重复下单
        ActivityUser miaoshaOrder = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsId);
        if (null != miaoshaOrder) {
//            log.error("miaoshaQueueReceiver, 重复秒杀, u={}, g={}", user.getId(), goodsId);
            return;
        }

        // 减库存,下订单 写入订单
        miaoshaService.miaosha(user, goodsVo);
        log.info("下单QueueReceiver. 报名成功. userId={}, goodsId={}", user.getId(), goodsVo.getId());
    }

    @RabbitListener(queues = Constant.DEFAULT_QUEUE_NAME)
    public void listenerQueue(String message) {
        log.info("receiver DEFAULT_QUEUE_NAME msg={}", message);
    }

    @RabbitListener(queues = Constant.TOPIC_QUEUE_1)
    public void listenerTopicQueue1(String message) {
        log.info("receiver TOPIC_QUEUE_1 msg={}", message);
    }
    @RabbitListener(queues = Constant.TOPIC_QUEUE_2)
    public void listenerTopicQueue2(String message) {
        log.info("receiver TOPIC_QUEUE_2 msg={}", message);
    }

    @RabbitListener(queues = Constant.FANOUT_QUEUE_1)
    public void listenerFanoutQueue1(String message) {
        log.info("receiver FANOUT_QUEUE_1 msg={}", message);
    }
    @RabbitListener(queues = Constant.FANOUT_QUEUE_2)
    public void listenerFanoutQueue2(String message) {
        log.info("receiver FANOUT_QUEUE_2 msg={}", message);
    }

    @RabbitListener(queues = Constant.HEADERS_QUEUE)
    public void listenerHeadersQueue(byte[] message) {
        log.info("receiver HEADERS_QUEUE msg={}", message);
    }
}
