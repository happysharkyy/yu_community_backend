package com.douyuehan.doubao.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.google.gson.internal.$Gson$Types;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Builder
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@TableName("behavior_user_log")
public class BehaviorUserLog {
    private String id;
    private Date createTime;
    private double weight;
}
