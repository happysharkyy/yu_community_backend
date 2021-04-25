package com.douyuehan.doubao.rabbitmq.dto;

import com.douyuehan.doubao.model.entity.SysUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author imyzt
 * @date 2019/3/20 18:02
 * @description MiaoshaMessage
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MiaoshaMessage {
    private SysUser user;
    private Long activitysId;
}
